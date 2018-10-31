package neu.lab.evoshell.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;

public class Graph4asm implements IGraph {
	Map<String, Node4asm> name2node;// hashCode as name
	private LabelNode firstLabel;

	public Graph4asm(MethodNode mn) {
		name2node = new HashMap<String, Node4asm>();
		InsnList insns = mn.instructions;
		ListIterator<AbstractInsnNode> ite = insns.iterator();
		List<LabelNode> labelSeq = new ArrayList<LabelNode>();
		// add node
		while (ite.hasNext()) {
			AbstractInsnNode insNode = ite.next();
			if (insNode instanceof LabelNode) {
				name2node.put("" + insNode.hashCode(), new Node4asm((LabelNode) insNode));
				labelSeq.add((LabelNode) insNode);
			}
		}
		// add Edge
		ite = insns.iterator();
		Map<LabelNode, List<JumpInsnNode>> label2jumps = new HashMap<LabelNode, List<JumpInsnNode>>();
		LabelNode currentLabel = null;
		while (ite.hasNext()) {
			AbstractInsnNode insNode = ite.next();
			if (insNode instanceof LabelNode) {
				currentLabel = (LabelNode) insNode;
			}
			if (insNode instanceof JumpInsnNode) {
				List<JumpInsnNode> jumps = label2jumps.get(currentLabel);
				if (jumps == null) {
					jumps = new ArrayList<JumpInsnNode>();
					label2jumps.put(currentLabel, jumps);
				}
				jumps.add((JumpInsnNode) insNode);
			}
		}
		for (LabelNode label : labelSeq) {
			List<JumpInsnNode> jumps = label2jumps.get(label);
			if (jumps == null) {// don't have jump
				addNextNode(labelSeq, label);
			} else {// have jump
				boolean hasGoto = false;
				for (JumpInsnNode jump : jumps) {// add all label to jump
					if (jump.getOpcode() == Opcodes.GOTO) {
						hasGoto = true;
					}
					name2node.get("" + label.hashCode()).addNextLabel(jump.label);
				}
				if (!hasGoto) {
					addNextNode(labelSeq, label);
				}
			}
		}
		firstLabel = labelSeq.get(0);
	}

	private void addNextNode(List<LabelNode> labelSeq, LabelNode frontNode) {
		int labelIndex = labelSeq.indexOf(frontNode);
		if (labelIndex != labelSeq.size() - 1) {// not final label,add next label.
			name2node.get("" + frontNode.hashCode()).addNextLabel(labelSeq.get(labelIndex + 1));
		}
	}

	@Override
	public INode getNode(String nodeName) {
		return name2node.get(nodeName);
	}

	@Override
	public Collection<String> getAllNode() {
		return name2node.keySet();
	}

}
