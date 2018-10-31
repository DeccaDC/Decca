package neu.lab.evoshell;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

public class CallPathDir {
	public static int validateCnt = 1;
	public static Set<String> riskMthds = new TreeSet<String>();//pomFile-riskMethod
	
	public static void main(String[] args) throws IOException {
//		ExecUtil.exeCmd("java -cp D:\\cWS\\eclipse1\\riddle\\target\\classes neu.lab.evoshell.NotFinish",ShellConfig.evoTimeOut,null);
//		ExecUtil.exeCmd("javac -verbose -classpath D:\\ws_testcase\\tempWs\\testcase\\;D:\\ws_testcase\\tempWs\\modifyCp\\;D:\\ws_testcase\\reportProject\\truth-release_0_41\\extensions\\liteproto\\target\\classes;D:\\cEnvironment\\repository\\org\\checkerframework\\checker-qual\\2.0.0\\checker-qual-2.0.0.jar;D:\\cEnvironment\\repository\\junit\\junit\\4.12\\junit-4.12.jar;D:\\cEnvironment\\repository\\com\\googlecode\\java-diff-utils\\diffutils\\1.3.0\\diffutils-1.3.0.jar;D:\\cEnvironment\\repository\\com\\google\\code\\findbugs\\jsr305\\1.3.9\\jsr305-1.3.9.jar;D:\\cEnvironment\\repository\\com\\google\\protobuf\\protobuf-java\\3.3.1\\protobuf-java-3.3.1.jar;D:\\cEnvironment\\repository\\org\\checkerframework\\checker-compat-qual\\2.0.0\\checker-compat-qual-2.0.0.jar;D:\\cEnvironment\\repository\\org\\codehaus\\mojo\\animal-sniffer-annotations\\1.14\\animal-sniffer-annotations-1.14.jar;D:\\cEnvironment\\repository\\com\\google\\auto\\value\\auto-value\\1.5.3\\auto-value-1.5.3.jar;D:\\cEnvironment\\repository\\com\\google\\errorprone\\error_prone_annotations\\2.2.0\\error_prone_annotations-2.2.0.jar;D:\\cEnvironment\\repository\\com\\google\\j2objc\\j2objc-annotations\\1.1\\j2objc-annotations-1.1.jar;D:\\cEnvironment\\repository\\com\\google\\guava\\guava\\23.6-android\\guava-23.6-android.jar;D:\\cEnvironment\\repository\\com\\google\\truth\\truth\\0.41\\truth-0.41.jar;D:\\cEnvironment\\repository\\org\\hamcrest\\hamcrest-core\\1.3\\hamcrest-core-1.3.jar D:\\ws_testcase\\tempWs\\testcase\\com\\google\\common\\truth\\extensions\\proto\\Test1.java",ShellConfig.evoTimeOut,"d:\\log.txt");
		FileSyn fileSyn = new FileSyn("D:\\ws_testcase\\image\\syn_modifyEvo.txt");
		//TODO pathDir
		File pathDir = new File(ShellConfig.tmpWsDir);
//		File pathDir = new File("D:\\ws_testcase\\distance_path\\path");
		for(File child:pathDir.listFiles()) {
			if(child.getName().startsWith("p_")) {
				try {
					new CallPathFile(child.getAbsolutePath()).validatePaths(false);
				} catch (Exception e) {
					e.printStackTrace();
				}
				fileSyn.add(child.getAbsolutePath());
			}
		}
	}
}
