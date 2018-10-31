package neu.lab.evoshell;

public class ShellConfig {

	public static final String testSuiteName = "Test1";
	public static final long testExeTimeout = 30 * 1000;//ms
	public static final long evoTimeOut = 90 * 1000;//ms

	public static final String mvnPath = "D:\\cTool\\apache-maven-3.2.5\\bin\\mvn.bat";
	public static final String evisuiteGoal = "org.evosuite.plugins:evosuite-maven-plugin:8.15:generate";

	public static final String tmpWsDir = "D:\\ws_testcase\\tempWs\\";
	//	public static final String outDir = "D:\\ws_testcase\\image\\path\\";
	public static final String modifyDirPath = tmpWsDir + "modifyCp\\";
	public static final String testcaseDirPath = tmpWsDir + "testcase\\";
	public static final String errorTracePath = tmpWsDir + "TraceLog.txt";
	public static final String garbageLogPath = tmpWsDir + "GarbageLog.txt";

	public static String mvnRep = "D:\\cEnvironment\\repository\\";
	private static String[] cpSuffixes = { "org\\ow2\\asm\\asm\\6.0\\asm-6.0.jar;",
			"org\\ow2\\asm\\asm-util\\6.0\\asm-util-6.0.jar;", "org\\ow2\\asm\\asm-tree\\6.0\\asm-tree-6.0.jar;",
			"neu\\lab\\riddle\\1.0\\riddle-1.0.jar;" };

	public static String getRiddleCp() {
		StringBuilder sb = new StringBuilder();
		for (String suffix : cpSuffixes) {
			sb.append(mvnRep);
			sb.append(suffix);
		}
		return sb.toString();
	}

	//	public static final String reachedResult = "D:\\ws_testcase\\image\\reachedMethods.txt";
	//	public static final String cmpResult = "D:\\ws_testcase\\image\\cmpResult.txt";
	//	public static final String callPathDir = "D:\\ws_testcase\\image\\path\\";
	//	public static final String distanceDir = "D:\\ws_testcase\\distance_mthdBranch\\";
	//	public static final String javaHome = "D:\\cEnvironment\\jdk1.8.111\\bin\\";
}
