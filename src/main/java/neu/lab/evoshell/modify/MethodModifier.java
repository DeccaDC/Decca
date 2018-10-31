package neu.lab.evoshell.modify;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TryCatchBlockNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.util.TraceClassVisitor;

import neu.lab.evoshell.FileUtil;
import neu.lab.evoshell.MthdFormatUtil;
import neu.lab.evoshell.ShellConfig;

/**
 * 1.IF_CMP in for will goto front label,IF_CMP in if will goto behind label. 
 * 2.Else-body inbyteCode will always be posterior. 
 * 3.Else-body is body between condition label and goto target label.
 * 
 * @author asus
 *
 */
public class MethodModifier {
	// insert a null Object filed to class
	// all condition in modifiedMethod will be changed to "$insertFiledName==null".
	private static String insertFiledName = "nullObject_";

	private String targetClassDir;// D:\cWS\eclipse1\plug.testcase.asm\target\classes\
	private String erM;// soot-format
	private String erJarPath;
	private String eeM;// soot-format
	// private String eeJarPath;

	public MethodModifier(String targetClassDir, String erM, String erJarPath, String eeM) {
		super();
		this.targetClassDir = targetClassDir;
		this.erM = erM;
		this.erJarPath = erJarPath;
		this.eeM = eeM;
		System.out.println("erM:" + this.erM);
		System.out.println("eeM:" + this.eeM);
		System.out.println("jarPath:" + this.erJarPath);
	}

	public void modifyMthd() throws Exception {
		// TODO writeHalfByte before modify.
		//		writeModifiedByteCode(getErClassStream(), "d:\\cWs\\notepad++\\ClassBefore.txt");

		ClassReader cr = new ClassReader(getErClassStream());
		ClassNode classNode = new ClassNode();
		cr.accept(classNode, ClassReader.SKIP_FRAMES);

		int acc = Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC;
		boolean hasInsert = false;
		for (FieldNode fieldNode : classNode.fields) {
			if (fieldNode.name.equals(insertFiledName)) {
				hasInsert = true;
			}
		}
		if (!hasInsert)
			classNode.fields.add(new FieldNode(acc, insertFiledName, "Ljava/lang/Object;", null, null));
		// System.out.println();
		String evoErM = MthdFormatUtil.soot2evo(erM);
		boolean findMthd2modify = false;

		for (MethodNode mn : classNode.methods) {
			String evoMthd = classNode.name.replace("/", ".") + "." + mn.name + mn.desc;
			if (evoErM.equals(evoMthd)) {
				findMthd2modify = true;
				// filter node
				pickBranch(classNode, mn);
			}
		}

		if (!findMthd2modify) {
			throw new Exception("can't find " + erM);
		}

		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		classNode.accept(cw);
		byte[] b = cw.toByteArray();
		BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(getOutFilePath()));
		out.write(b);
		out.close();
		// TODO writeHalfByte after modify.
		//		writeModifiedByteCode(new FileInputStream(getOutFilePath()), "d:\\cWs\\notepad++\\ClassAfter.txt");
		System.out.println("end modify===");
	}

	private List<AbstractInsnNode> getOriCmpInses(MethodNode mn, LabelNode startLabel) {// may have multiCondition
																						// because of ||,&&
		List<AbstractInsnNode> oriCmpInses = new ArrayList<AbstractInsnNode>();
		InsnList insns = mn.instructions;
		ListIterator<AbstractInsnNode> ite = insns.iterator();
		boolean hasFindLabel = false;
		while (ite.hasNext()) {
			AbstractInsnNode insNode = ite.next();
			if (startLabel == insNode) {
				hasFindLabel = true;
			}
			if (hasFindLabel && startLabel != insNode) {
				if (insNode instanceof LabelNode) {
					break;
				} else {
					oriCmpInses.add(insNode);
				}
			}
		}
		return oriCmpInses;
	}

	private void pickBranch(ClassNode cn, MethodNode mn) throws Exception {

		// ExeLabelPaths allPath = getAllExePath(mn);
		// System.out.println(allPath.getPathsStr());
		List<LabelNode> callLabels = getCallLabels(mn, MthdFormatUtil.soot2evo(eeM));
		if (callLabels.size() > 0) {// eeMethod is not exist in catch-body.
			// ExeLabelPath targetPath = allPath.getRemainPath(callLabels);// path that
			// contains condition.
			List<LabelNode> labelSeq = getLabelSeq(mn);
			Set<AbstractInsnNode> allIfOfFor = getAllIfOfFors(mn, labelSeq, true);
			Set<AbstractInsnNode> allGotoOfFor = getAllGotoOfFor(mn, getAllIfOfFors(mn, labelSeq, false));
			List<LabelNode> catchEndLabels = new ArrayList<LabelNode>();
			for (TryCatchBlockNode tryCatch : mn.tryCatchBlocks) {
				catchEndLabels.add(tryCatch.end);
			}
			InsnList insns = mn.instructions;
			ListIterator<AbstractInsnNode> ite = insns.iterator();
			LabelNode currentLabel = null;
			Map<LabelNode, List<AbstractInsnNode>> label2oriCmpIns = new HashMap<LabelNode, List<AbstractInsnNode>>();
			Map<LabelNode, LabelNode> label2else = new HashMap<LabelNode, LabelNode>();
			while (ite.hasNext()) {
				AbstractInsnNode insNode = ite.next();
				// System.out.println(insNode);

				if (insNode instanceof LineNumberNode) {
					ite.remove();
				}
				if (insNode instanceof LabelNode) {
					currentLabel = (LabelNode) insNode;
				}
				if (insNode instanceof JumpInsnNode) {
					if (!catchEndLabels.contains(currentLabel) && !allIfOfFor.contains(insNode)
							&& !allGotoOfFor.contains(insNode)) {// not jump of try,not jump of for
						JumpInsnNode jumpNode = ((JumpInsnNode) insNode);
						if (jumpNode.getOpcode() == Opcodes.GOTO) {

						} else {// if statement
							LabelNode mayElse = label2else.get(currentLabel);
							if (mayElse == null || aBeforeB(labelSeq, mayElse, jumpNode.label)) {
								// we consider posterior code is else-body.
								label2else.put(currentLabel, jumpNode.label);
							}
							if (label2oriCmpIns.get(currentLabel) == null) {
								label2oriCmpIns.put(currentLabel, getOriCmpInses(mn, currentLabel));
							}
						}
					}
				}
			}
			for (LabelNode condLabel : label2else.keySet()) {
				if (!callLabels.contains(condLabel)) {
					boolean shouldJump = shouldJump(callLabels, labelSeq, condLabel, label2else.get(condLabel));
					changeCondition(cn, mn, condLabel, label2else.get(condLabel), shouldJump);
					handleOriCmpIns(mn, condLabel, label2oriCmpIns.get(condLabel));
				}
			}
		}

	}

	private boolean shouldJump(List<LabelNode> callLabels, List<LabelNode> labelSeq, LabelNode condLabel,
			LabelNode elseLabel) throws Exception {
		int indexOfcond = labelSeq.indexOf(condLabel);
		int indexOfelse = labelSeq.indexOf(elseLabel);
		if (indexOfcond == -1 || indexOfelse == -1) {
			throw new Exception("there's no cond or else in shouldJump");
		}
		if (indexOfelse < indexOfcond) {
			throw new Exception("Else is front of condition in shouldJump");
		}
		for (int i = indexOfcond; i < indexOfelse; i++) {
			if (callLabels.contains(labelSeq.get(i))) {
				//in if-body ,shouldn't jump.
				return false;
			}
		}
		return true;
	}

	private void changeCondition(ClassNode cn, MethodNode mn, LabelNode condLabel, LabelNode elseLabel,
			boolean shouldJump) {
		LabelNode label2jump = elseLabel;

		FieldInsnNode getObjIns = new FieldInsnNode(Opcodes.GETSTATIC, cn.name, insertFiledName, "Ljava/lang/Object;");
		mn.instructions.insert(condLabel, getObjIns);

		JumpInsnNode jumpIns = null;
		// System.out.println(label2jump);
		if (shouldJump) {// jump
			//			System.out.println("jump");
			jumpIns = new JumpInsnNode(Opcodes.IFNULL, label2jump);
		} else {// not jump
			jumpIns = new JumpInsnNode(Opcodes.IFNONNULL, label2jump);
		}
		mn.instructions.insert(getObjIns, jumpIns);
	}

	private void changeCondition(MethodNode mn, Map<LabelNode, LabelNode> label2else) {

	}

	private void handleOriCmpIns(MethodNode mn, Map<LabelNode, List<AbstractInsnNode>> label2oriCmpIns) {

		for (LabelNode labelNode : label2oriCmpIns.keySet()) {

			handleOriCmpIns(mn, labelNode, label2oriCmpIns.get(labelNode));
		}

	}

	// private FrameNode findPreviousFrame(MethodNode mn, LabelNode labelNode) {
	// ListIterator<AbstractInsnNode> ite = mn.instructions.iterator();
	// FrameNode currentFrame = null;
	// while (ite.hasNext()) {
	// AbstractInsnNode insNode = ite.next();
	// if (insNode instanceof FrameNode) {
	// currentFrame = (FrameNode) insNode;
	// }
	// if (insNode == labelNode) {
	// if (currentFrame == null) {
	// System.out.println("********last frame is null.");
	// }
	// return currentFrame;
	// }
	// }
	// System.out.println("********can't find labelNode in findPreviousFrame.");
	// return currentFrame;
	// }

	private void handleOriCmpIns(MethodNode mn, LabelNode condLabel, List<AbstractInsnNode> oriCmpIns) {
		// FrameNode previousFrame = findPreviousFrame(mn, labelNode);
		// delete strategy
		ListIterator<AbstractInsnNode> ite = mn.instructions.iterator();
		while (ite.hasNext()) {
			AbstractInsnNode insNode = ite.next();
			if (oriCmpIns.contains(insNode)) {
				ite.remove();
			}
		}

		// move origin condition front 
		//		InsnList fakeIf = new InsnList();
		//		fakeIf.add(new LabelNode());
		//		for (AbstractInsnNode insNode : oriCmpIns) {
		//			if (insNode instanceof JumpInsnNode) {
		//				JumpInsnNode jumpNode = ((JumpInsnNode) insNode);
		//				jumpNode.label = condLabel;
		//			}
		//			fakeIf.add(insNode);
		//		}
		//		fakeIf.add(new LabelNode());
		//		fakeIf.add(new TypeInsnNode(Opcodes.NEW, "java/lang/Object"));
		//		fakeIf.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false));
		//		mn.instructions.insert(condLabel.getPrevious(), fakeIf);
		//		mn.instructions.insert(condLabel, new FrameNode(Opcodes.F_FULL, 0, new Object[0], 0, new Object[0]));
	}

	private List<LabelNode> getLabelSeq(MethodNode mn) {
		List<LabelNode> labelSeq = new ArrayList<LabelNode>();
		ListIterator<AbstractInsnNode> ite = mn.instructions.iterator();
		while (ite.hasNext()) {
			AbstractInsnNode insNode = ite.next();
			if (insNode instanceof LabelNode) {
				labelSeq.add((LabelNode) insNode);
			}
		}
		return labelSeq;
	}

	private Set<AbstractInsnNode> getAllGotoOfFor(MethodNode mn, Set<AbstractInsnNode> allIfLabelOfFor) {
		Set<AbstractInsnNode> allGotoOfFor = new HashSet<AbstractInsnNode>();
		ListIterator<AbstractInsnNode> ite = mn.instructions.iterator();
		while (ite.hasNext()) {
			AbstractInsnNode insNode = ite.next();
			if (insNode instanceof JumpInsnNode) {
				JumpInsnNode jumpNode = ((JumpInsnNode) insNode);
				if (jumpNode.getOpcode() == Opcodes.GOTO && allIfLabelOfFor.contains(jumpNode.label)) {
					allGotoOfFor.add(jumpNode);
				}
			}
		}
		return allGotoOfFor;
	}

	/**
	 * @param mn
	 * @param labelSeq
	 * @param retJumpNode
	 *            If true ,elements in result are jumpNode.If false, elements in
	 *            result are labelNode who contains this jumpNode.
	 * @return
	 * @throws Exception
	 */
	private Set<AbstractInsnNode> getAllIfOfFors(MethodNode mn, List<LabelNode> labelSeq, boolean retJumpNode)
			throws Exception {
		Set<AbstractInsnNode> allIfOfFor = new HashSet<AbstractInsnNode>();
		ListIterator<AbstractInsnNode> ite = mn.instructions.iterator();
		LabelNode currentLabel = null;
		while (ite.hasNext()) {
			AbstractInsnNode insNode = ite.next();
			if (insNode instanceof LabelNode) {
				currentLabel = (LabelNode) insNode;
			}
			if (insNode instanceof JumpInsnNode) {
				JumpInsnNode jumpNode = ((JumpInsnNode) insNode);
				if (jumpNode.getOpcode() != Opcodes.GOTO) {
					if (!this.aBeforeB(labelSeq, currentLabel, jumpNode.label)) {
						if (retJumpNode) {
							allIfOfFor.add(jumpNode);
						} else {
							allIfOfFor.add(currentLabel);
						}
					}
				}
			}
		}
		return allIfOfFor;
	}

	private boolean aBeforeB(List<LabelNode> labelSeq, LabelNode a, LabelNode b) throws Exception {
		int indexOfa = labelSeq.indexOf(a);
		int indexOfb = labelSeq.indexOf(b);
		if (indexOfa == -1 || indexOfb == -1) {
			throw new Exception("can't find labelNode when execute aBeforeB");
		}
		if (indexOfa < indexOfb) {
			return true;
		} else {
			return false;
		}
	}

	public static void main(String[] args) throws Exception {

		new MethodModifier(args[0], args[1], args[2], args[3]).modifyMthd();

	}

	private static void asmTest() throws Exception {
		FileUtil.delFolder(ShellConfig.modifyDirPath, true);
		new MethodModifier("D:\\cWS\\eclipse1\\plug.testcase.asm\\target\\classes\\",
				"<neu.lab.plug.testcase.asm.App: void <init>()>",
				"D:\\cWS\\eclipse1\\plug.testcase.asm\\target\\classes",
				"<java.lang.Object: java.lang.String toString()>").modifyMthd();
	}

	private InputStream getErClassStream() throws ZipException, IOException {
		InputStream classInStream;
		String erCls = MthdFormatUtil.sootMthd2cls(erM);
		ZipFile zipFile = null;
		File modifiedClass = new File(ShellConfig.modifyDirPath + erCls.replace(".", File.separator) + ".class");
		if (modifiedClass.exists()) {// Other method in class was modified.
			System.out.println("load " + erCls + " from modifiedClass.");
			classInStream = new FileInputStream(modifiedClass);
		} else {
			File handModifyClass = new File(targetClassDir + erCls.replace(".", File.separator) + ".class");
			if (handModifyClass.exists()) {
				System.out.println("load " + erCls + " from targetClass.");
				classInStream = new FileInputStream(handModifyClass);
			} else if (erJarPath.endsWith(".jar")) {
				System.out.println("load " + erCls + " from " + erJarPath);
				zipFile = new ZipFile(new File(erJarPath));
				ZipEntry entry = zipFile.getEntry(erCls.replace(".", "/") + ".class");
				classInStream = zipFile.getInputStream(entry);
			} else {
				System.out.println("load " + erCls + " from " + erJarPath);
				classInStream = new FileInputStream(
						erJarPath + File.separator + erCls.replace(".", File.separator) + ".class");
			}
		}

		return classInStream;
	}

	private void writeModifiedByteCode(InputStream inClass, String outPath) {
		try {
			ClassReader cr = new ClassReader(inClass);
			PrintWriter p1 = new PrintWriter(new FileWriter(outPath, false));
			cr.accept(new TraceClassVisitor(p1), 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String getOutFilePath() {
		String path = ShellConfig.modifyDirPath + MthdFormatUtil.sootMthd2cls(erM).replace(".", File.separator)
				+ ".class";
		File outFile = new File(path);
		if (!outFile.getParentFile().exists()) {
			outFile.getParentFile().mkdirs();
		}
		return path;
	}

	////////////// abandon/////////////////////////////////////////////////////////////
	/**
	 * LabelNodes whose statements call evoMthd.
	 * 
	 * @param mn
	 * @param evoMthd
	 * @return
	 * @throws Exception
	 */
	private List<LabelNode> getCallLabels(MethodNode mn, String evoMthd) throws Exception {
		List<LabelNode> callLabels = new ArrayList<LabelNode>();
		ListIterator<AbstractInsnNode> ite = mn.instructions.iterator();
		LabelNode currentLabel = null;
		while (ite.hasNext()) {
			AbstractInsnNode insNode = ite.next();
			if (insNode instanceof LabelNode) {
				currentLabel = (LabelNode) insNode;
			}
			if (insNode instanceof MethodInsnNode) {
				MethodInsnNode mthdIns = (MethodInsnNode) insNode;
				String calledMthd = mthdIns.owner.replace("/", ".") + "." + mthdIns.name + mthdIns.desc;
				if (evoMthd.equals(calledMthd)) {
					callLabels.add(currentLabel);
				}
			}
		}
		ite = mn.instructions.iterator();
		currentLabel = null;
		if (callLabels.size() == 0) {// don't have accurate method,find same name method.
			System.out.println("****can't find accurate method " + evoMthd + ",try to find same-name method.");
			while (ite.hasNext()) {
				AbstractInsnNode insNode = ite.next();
				if (insNode instanceof LabelNode) {
					currentLabel = (LabelNode) insNode;
				}
				if (insNode instanceof MethodInsnNode) {
					MethodInsnNode mthdIns = (MethodInsnNode) insNode;
					String calledMthd = mthdIns.name + mthdIns.desc;
					String evoMthdName = evoMthd.substring(evoMthd.lastIndexOf(".") + 1);
					if (evoMthdName.equals(calledMthd)) {
						callLabels.add(currentLabel);
					}
				}
			}
		}
		if (callLabels.size() == 0) {
			System.out.println("*************don't have method " + evoMthd + " may exist in catch body.");
			// throw new Exception("don't have method " + evoMthd);
		}
		return callLabels;
	}

	private ExeLabelPaths getAllExePath(MethodNode mn) {
		ExeLabelPaths paths = new ExeLabelPaths();
		// ListIterator<AbstractInsnNode> ite = mn.instructions.iterator();
		// boolean hasAddFirst = false;
		// LabelNode lastLabel = null;
		// while (ite.hasNext()) {
		// AbstractInsnNode insNode = ite.next();
		// // System.out.println(insNode);
		// if (insNode instanceof LabelNode) {// add sequence node.
		// if (!hasAddFirst) {// first labelNode
		// paths.addFirstNode((LabelNode) insNode);
		// hasAddFirst = true;
		// } else {
		// if (lastLabel != null)
		// paths.addNewNode(lastLabel, (LabelNode) insNode);
		// }
		// lastLabel = (LabelNode) insNode;
		// }
		// if (insNode instanceof JumpInsnNode) {
		// JumpInsnNode jumpNode = ((JumpInsnNode) insNode);
		// if (jumpNode.getOpcode() == Opcodes.GOTO) {
		// paths.addNewNode(lastLabel, jumpNode.label);
		// lastLabel = null;
		// } else {
		// System.out.println("add new Path");
		// paths.addNewBranchNode(lastLabel, jumpNode.label);
		// }
		// }
		// }
		return paths;
	}
	// if (insNode instanceof TypeInsnNode) {
	// TypeInsnNode typeIns = ((TypeInsnNode) insNode);
	//// System.out.println("|"+typeIns.desc+"|");
	//
	// }
	// if (insNode instanceof MethodInsnNode) {
	// MethodInsnNode mthdIns = ((MethodInsnNode) insNode);
	// System.out.println("|"+mthdIns.owner+"|");
	// System.out.println("|"+mthdIns.name+"|");
	// System.out.println("|"+mthdIns.desc+"|");
	// }
}
