package neu.lab.conflict;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import neu.lab.conflict.util.MavenUtil;
import neu.lab.conflict.util.UserConf;
import neu.lab.conflict.writer.JarDupRiskWriter;

@Mojo(name = "jarDupRisk", defaultPhase = LifecyclePhase.VALIDATE)
public class JarDupRiskMojo extends ConflictMojo{
	@Parameter(property = "resultFilePath")
	protected String resultFilePath;
	@Parameter( property = "append", defaultValue = "false" )
	public boolean append;
	@Override
	public void run() {
		if(resultFilePath==null)
			resultFilePath = MavenUtil.i().getBuildDir().getAbsolutePath()+"\\jarDupRisk.txt";
		new JarDupRiskWriter().write(resultFilePath,append);
	}

}
