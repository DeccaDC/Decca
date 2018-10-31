package neu.lab.conflict;

import java.io.File;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import neu.lab.evoshell.FileUtil;
import neu.lab.evoshell.ShellConfig;

@Mojo(name = "gene", defaultPhase = LifecyclePhase.VALIDATE)
public class GeneMojo extends ConflictMojo{
	@Override
	public void run() {
		FileUtil.delFolder(ShellConfig.tmpWsDir, true);
		new File(ShellConfig.tmpWsDir).mkdirs();
		TestCaseGenerator testCaseGenerator = new TestCaseGenerator(ShellConfig.tmpWsDir, append);
		testCaseGenerator.writePath();
		testCaseGenerator.validatePath();
	}
}
