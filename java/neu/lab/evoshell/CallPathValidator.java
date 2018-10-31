package neu.lab.evoshell;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.exec.ExecuteException;

import neu.lab.evoshell.testgen.CodeGenerator;
import neu.lab.evoshell.testgen.SootUtil;

/**
 * @author asus validate this path whether executes to end.
 */
public class CallPathValidator {

	private static final int NO_ERROR = 0;
	private static final int MODIFY_METHOD_ERROR = 1;
	private static final int EVOSUITE_ERROR = 2;

	//	public static final String modifyDir = "D:\\ws_testcase\\modifyCp";

	//	String cp2exeTest;
	String oriProjectCp;
	//	String cp2test;

	String pomPath;// D:\\cWS\\eclipse1\\testcase.top
	String entryClass;// neu.lab.testcase.top.MthdTop
	String riskMthd;// <neu.lab.testcase.bottom.MthdBottom: void m2()>
	String distanceFile;// "D:\ws_testcase\image\distance_mthdBranch\neu.lab+testcase.top+1.0@neu.lab+testcase.bottom@1.0.txt"

	String[] mthdInPath;
	String[] jarInPaths;

	List<String> modifyMthdCmds;
	String evoCmd;

	public void validateCallPath(boolean exeEvo) {
		//		FileUtil.delFolder(folderPath);
		System.out.println("validate for " + CallPathDir.validateCnt);
		System.out.println("pom:" + pomPath);
		for (String mthd : this.mthdInPath) {
			System.out.println(mthd);
		}

		modifyMthdOnPath();// make class don't have branch.

		boolean hasTrigger = false;
		//TODO exeEvosuite
		if (exeEvo&&!isEvoBugConf()) {
			try {
				exeEvosuite();
				TestCaseUtil.moveEvo(getEvoTcPath(), getFinalTcPath());
				exeTestCase(getFinalTcSig());
			} catch (ExecuteException e) {
				System.out.println("execute evoTest may trigger bug!");
				hasTrigger = checkErrorTraceFile();
				if (hasTrigger) {
					System.out.println("execute evoTest trigger bug!");
				}
			} catch (Exception e) {
				System.out.println("can't move evoTest !");
			}
		}

		//homemade
		if (!hasTrigger) {
			try {
				new CodeGenerator(Arrays.asList(mthdInPath), oriProjectCp.split(";")).writeCode(getFinalTcPath());
				exeTestCase(getFinalTcSig());
			} catch (ExecuteException e) {
				System.out.println("execute test may trigger bug!");
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			hasTrigger = checkErrorTraceFile();
			if (hasTrigger) {
				System.out.println("execute test2 trigger bug!");
			}
		}
		
		//		if(hasTrigger) {
		//			System.out.println("trigger bug successfully!");
		//		}else {
		//			System.out.println("trigger bug fail!");
		//		}

		System.out.println();
		CallPathDir.validateCnt++;
		CallPathDir.riskMthds.add(pomPath + " " + riskMthd);
		//		cmpResult(resultFlag, resultException);
	}


	/**
	 * @return true if trigger the risk.
	 */
	private boolean checkErrorTraceFile() {
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(ShellConfig.errorTracePath));
		} catch (FileNotFoundException e) {
			System.out.println("can't find trace file.");
			return false;
		}
		try {
			String line = reader.readLine();
			while (line != null) {
				if (line.contains("java.lang.NoClassDefFoundError") || line.contains("java.lang.ClassNotFoundException")
						|| line.contains("java.lang.NoSuchMethodError")
						|| line.contains("java.lang.NoSuchMethodException")
						|| line.contains("java.lang.AbstractMethodError")) {
					return true;
				}
				line = reader.readLine();
			}
			return false;
		} catch (Exception e) {
			System.out.println("confuse exception.");
			return false;
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public CallPathValidator(String oriProjectCp, String pomPath, String distanceFile, String[] mthds,
			String[] jarPaths) {
		super();
		this.oriProjectCp = sortCp(oriProjectCp);
		this.pomPath = pomPath;
		this.entryClass = MthdFormatUtil.sootMthd2cls(mthds[0]);
		this.riskMthd = mthds[mthds.length - 1];
		this.distanceFile = distanceFile;
		this.mthdInPath = mthds;
		this.jarInPaths = jarPaths;
		this.modifyMthdCmds = new ArrayList<String>();

	}

	private void exeEvosuite() throws ExecuteException, IOException {
		evoCmd = ShellConfig.mvnPath + " " + ShellConfig.evisuiteGoal + " -f=" + pomPath + " -Dmodify_cp="
				+ ShellConfig.modifyDirPath + " -Dbase_dir=" + ShellConfig.testcaseDirPath + " -Dclass=" + entryClass
				+ " -Dcriterion=MTHD_PROB_RISK -Drisk_method=\"" + riskMthd + "\" -Dmthd_prob_distance_file="
				+ distanceFile + " -Dmaven.test.skip=true -e";
		ExecUtil.exeCmd(evoCmd, ShellConfig.evoTimeOut, null);
	}

	private void modifyMthdOnPath() {
		FileUtil.delFolder(ShellConfig.modifyDirPath, true);
		FileUtil.delFolder(ShellConfig.testcaseDirPath, true);
		new File(ShellConfig.modifyDirPath).mkdirs();
		new File(ShellConfig.testcaseDirPath).mkdirs();
		for (int i = 0; i < mthdInPath.length - 1; i++) {
			if (!isBugMthd(mthdInPath[i])) {
				String cmd = "java -cp " + getCp2modify() + " neu.lab.evoshell.modify.MethodModifier \""
						+ getTargetDir() + "\" \"" + mthdInPath[i] + "\" \"" + jarInPaths[i] + "\" \""
						+ mthdInPath[i + 1] + "\"";
				modifyMthdCmds.add(cmd);
				try {
					ExecUtil.exeCmd(cmd, 0, ShellConfig.garbageLogPath);
				} catch (ExecuteException e) {
					//					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**move target to first.
	 * @param originCps
	 * @return
	 */
	private String sortCp(String originCpStr) {
		LinkedList<String> cps = new LinkedList<String>();
		for (String originCp : originCpStr.split(";")) {
			if (originCp.contains("\\target\\")) {
				cps.addFirst(originCp);
			} else {
				cps.add(originCp);
			}
		}
		StringBuilder sb = new StringBuilder();
		for (String cp : cps) {
			sb.append(cp);
			sb.append(";");
		}
		if (!cps.isEmpty()) {
			sb.deleteCharAt(sb.length() - 1);
		}
		return sb.toString();
	}

	private String getCp2modify() {
		return ShellConfig.getRiddleCp() + oriProjectCp;
	}

	private String getTargetDir() {
		return pomPath + "\\target\\classes\\";
	}

	private void compileTestCase(String filePath) {
		String cmd = "javac -classpath " + getCp2exeTest() + " " + filePath;
		try {
			ExecUtil.exeCmd(cmd);
		} catch (ExecuteException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String getCp2exeTest() {
		return ShellConfig.testcaseDirPath + File.pathSeparator + ShellConfig.modifyDirPath + File.pathSeparator
				+ this.oriProjectCp;
	}

	/**
	 * @return D:\ws_testcase\testcase\org\apache\storm\kafka\monitor\Test1.java
	 */
	private String getFinalTcPath() {
		return ShellConfig.testcaseDirPath + SootUtil.cls2pck(this.entryClass, "\\") + "\\" + ShellConfig.testSuiteName
				+ ".java";
	}

	/**org.apache.storm.kafka.monitor.
	 * @return
	 */
	private String getFinalTcSig() {
		return SootUtil.cls2pck(this.entryClass, ".") + "." + ShellConfig.testSuiteName;
	}

	private String getEvoTcPath() {
		return ShellConfig.testcaseDirPath + "evosuite-tests\\" + entryClass.replace(".", "\\") + "_ESTest.java";
	}

	private void exeTestCase(String classSig) throws ExecuteException, IOException {
		compileTestCase(getFinalTcPath());
		String cmd = "java -cp " + getCp2exeTest() + " " + classSig;
		ExecUtil.exeCmd(cmd, ShellConfig.testExeTimeout, ShellConfig.errorTracePath);
	}

	//	public static void main(String[] args) throws Exception {
	//		// BufferedReader reader = new BufferedReader(new FileReader(
	//		// "D:\\ws_testcase\\image\\path\\neu.lab+testcase.top+1.0@neu.lab+testcase.bottom@1.0.txt"));
	//
	//	}

	private static void test() {
		// BufferedReader reader = new BufferedReader(new FileReader(
		// ));
		// String line = reader.readLine();
		// List<String> mthds = new ArrayList<String>();
		// List<String> jarPaths = new ArrayList<String>();
		// while (line != null) {
		// if (!line.equals("")) {
		// if (line.startsWith("pathLen")) {
		//
		// } else {
		// System.out.println(line);
		// String[] mthd_path = line.split("> ");
		// if (mthd_path.length == 2) {
		// mthds.add(mthd_path[0] + ">");
		// jarPaths.add(mthd_path[1]);
		// } else {
		// mthds.add(mthd_path[0]);
		// // new CallPathValidator("", mthds.toArray(new String[0]),
		// jarPaths.toArray(new
		// // String[0]))
		// // .modifyMthdOnPath();
		// break;
		// }
		// }
		// }
		// line = reader.readLine();
		// }
		// reader.close();
	}
	
	private boolean isEvoBugConf() {
		if(this.distanceFile.contains("org.ff4j+ff4j-spring-boot-autoconfigure+1.7.1@ch.qos.logback+logback-core")) {
			return true;
		}
		if(this.distanceFile.contains("com.hotels.styx+styx-api-testsupport+0.7.7@com.google.guava+guava")) {
			return true;
		}
		return false;
	}

	private static Set<String> bugMthd = new HashSet<String>();
	static {
		bugMthd.add(
				"<io.swagger.jaxrs.Reader: io.swagger.models.Swagger read(java.lang.Class,java.lang.String,java.lang.String,boolean,java.lang.String[],java.lang.String[],java.util.Map,java.util.List,java.util.Set)>");
		bugMthd.add(
				"<io.swagger.util.BaseReaderUtils: java.util.Map parseExtensions(io.swagger.annotations.Extension[])>");
		bugMthd.add("<us.codecraft.webmagic.downloader.selenium.WebDriverPool: void closeAll()>");
		bugMthd.add("<com.gargoylesoftware.htmlunit.WebClient: void closeAllWindows()>");
		bugMthd.add(
				"<org.wisdom.maven.osgi.BundlePackager: void bundle(java.io.File,java.io.File,org.wisdom.maven.osgi.Reporter)>");
		bugMthd.add("<org.wisdom.maven.osgi.DependencyEmbedder: void parse(java.lang.String)>");
		bugMthd.add("<com.rackspacecloud.blueflood.service.ZKShardLockManager: void shutdownUnsafe()>");
//		bugMthd.add("<org.slf4j.impl.StaticLoggerBinder: org.slf4j.ILoggerFactory getLoggerFactory()>");
	}

	private boolean isBugMthd(String mthd) {
		if (bugMthd.contains(mthd)) {
			return true;
		}
		if(mthd.startsWith("<io.swagger.jaxrs.Reader")) {
			return true;
		}
		if(mthd.startsWith("<org.slf4j.LoggerFactory")) {
			return true;
		}
		if (mthd.contains("ch.qos.logback.classic.util.ContextInitializer")) {
			return true;
		}
		if (mthd.contains("org.nustaq.serialization.FSTObjectInput")) {
			return true;
		}
		return false;
	}

}
