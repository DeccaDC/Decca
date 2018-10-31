package neu.lab.evoshell.testgen;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import neu.lab.evoshell.FileUtil;
import neu.lab.evoshell.ShellConfig;

public class CodeGenerator {
	
	private List<String> mthdInPath;
	String[] classPaths;
	private String entryMthdName;//isNotEqualTo
	//	private NeededObj neededCaller;//yaml.YAMLFactory
	private String entryClass;
	private List<NeededObj> neededParam;

	public CodeGenerator(List<String> mthdInPath, String[] classPaths) {
		this.mthdInPath = mthdInPath;
		this.classPaths = classPaths;
		
		ProjectInfo.i().initClsesInPath(mthdInPath);
		ProjectInfo.i().setEntryCls(SootUtil.mthdSig2cls(mthdInPath.get(0)));
		new SootExe().initProjectInfo(classPaths);
//		ClassVO cls = ProjectInfo.i().getClassVO("org.ff4j.spring.boot.autoconfigure.FF4JWebConfiguration");
//		System.out.println("look a class:"+cls.toString());
//		decomposeEntryMethod(callPath.get(0));
		MethodVO entryMthd = ProjectInfo.i().getMethodVO(mthdInPath.get(0));
		entryClass = entryMthd.getCls().getSig();
		entryMthdName = entryMthd.getName();
		neededParam = new ArrayList<NeededObj>();
		for (String paramType : entryMthd.getParamTypes()) {
			neededParam.add(new NeededObj(paramType, 0));
		}
	}

//	//	<yaml.YAMLFactory: yaml.YAMLGenerator _createGenerator(io.Writer,io.IOContext)> 
//	private void decomposeEntryMethod(String mthdSig) {
//		//		<yaml.YAMLFactory
//		//		 yaml.YAMLGenerator _createGenerator(io.Writer,io.IOContext)> 
//		String[] class_suf = mthdSig.split(":");
//		entryClass = class_suf[0].substring(1);
//		//		_createGenerator
//		//		io.Writer,io.IOContext)> 
//		String[] mthd_params = class_suf[1].split(" ")[2].split("\\(");
//		entryMthdName = mthd_params[0];
//		neededParam = new ArrayList<NeededObj>();
//		for (String paramType : mthd_params[1].replace(")>", "").split(",")) {
//			neededParam.add(new NeededObj(paramType, 0));
//		}
//		
//	}

	private String getCaller() {
		if (ProjectInfo.i().getMethodVO(mthdInPath.get(0)).isStatic()) {
			return entryClass;
		} else {
			if(isCons(mthdInPath.get(0))) {
				return new NeededObj(entryClass, 0).getStatement(ProjectInfo.i().getMethodVO(mthdInPath.get(0)),false);
			}else {
				return new NeededObj(entryClass, 0).getStatement(null,true);
			}
			
		}
	}

	private String getCode() {
		StringBuilder sb = new StringBuilder("package ");
		sb.append(SootUtil.cls2pck(SootUtil.mthdSig2cls(mthdInPath.get(0)), "."));
		sb.append(";");
		sb.append(System.lineSeparator());
		sb.append(System.lineSeparator());
		sb.append("public class ");
		sb.append(ShellConfig.testSuiteName);
		sb.append("{");
		sb.append(System.lineSeparator());
		sb.append("	public static void main(String[] args)  throws Throwable  {");
		sb.append(System.lineSeparator());
		sb.append("        ");
		sb.append(getCaller());
		if(!isCons(mthdInPath.get(0))) {
			sb.append(getMthdInvoke());
		}
		sb.append(";");
		sb.append(System.lineSeparator());
		sb.append("	}");
		sb.append(System.lineSeparator());
		sb.append("}");
		return sb.toString();
	}
	
	private boolean isCons(String mthd) {
		return mthd.contains("<init>(");
	}
	
	private String getMthdInvoke() {
		StringBuilder sb = new StringBuilder();
		sb.append(".");
		sb.append(entryMthdName);
		sb.append("(");
		for (NeededObj paramObj : this.neededParam) {
			sb.append(paramObj.getStatement(null,true));
			sb.append(",");
		}
		if(!this.neededParam.isEmpty()) {//delete last ,
			sb.deleteCharAt(sb.length() - 1);
		}
		sb.append(")");
		return sb.toString();
	}

	public void writeCode(String outPath) {
		new File(outPath).getParentFile().mkdirs();
		try {
			PrintWriter printer = new PrintWriter(new BufferedWriter(new FileWriter(outPath)));
			printer.println(getCode());
			printer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//TODO printDebugCopy
		FileUtil.copyFile(outPath, ShellConfig.tmpWsDir + "myTest.java");
	}

	@Override
	public String toString() {
		return "CodeGenerator [classPaths=" + classPaths + ", mthdName=" + entryMthdName + ", paramTypes=" + neededParam
				+ "]";
	}

	public static void main(String[] args) {
		//		String mthdSig = "<com.fasterxml.jackson.dataformat.yaml.YAMLFactory: com.fasterxml.jackson.dataformat.yaml.YAMLGenerator "
		//				+ "_createGenerator(java.io.Writer,com.fasterxml.jackson.core.io.IOContext)>";
		//		System.out.println(new CodeGenerator(mthdSig, null).toString());
		List<String> callPath = new ArrayList<String>();
		String[] classPaths = null;

		int pathSeq = 0;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(
					"D:\\ws_testcase\\image\\path\\p_com.google.truth.extensions+truth-liteproto-extension+0.41@com.google.guava+guava@25.1-android.txt"));
			String line = reader.readLine();
			while (line != null) {
				if (!line.equals("")) {
					if (line.startsWith("classPath:")) {
						classPaths = line.replace("classPath:", "").split(";");
					} else if (line.startsWith("pathLen:")) {
						pathSeq++;
						if (pathSeq == 2)
							break;
					} else if (pathSeq == 1) {
						callPath.add(line);
					}

				}
				line = reader.readLine();
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(new CodeGenerator(callPath, classPaths).getCode());
	}
}
