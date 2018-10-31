package neu.lab.conflict;


import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import neu.lab.conflict.util.UserConf;


@Mojo(name = "debug2", defaultPhase = LifecyclePhase.VALIDATE)
public class Debug2Mojo extends ConflictMojo {

	@Override
	public void run() {
		//win下运行此Mojo
		//TestCaseGenerator testCaseGenerator = new TestCaseGenerator("D:\\ws_testcase\\image\\path\\", append);
		//mac下运行此Mojo
		TestCaseGenerator testCaseGenerator = new TestCaseGenerator(UserConf.getOutDir4Mac(), append);
		testCaseGenerator.writePath();
	}

}
