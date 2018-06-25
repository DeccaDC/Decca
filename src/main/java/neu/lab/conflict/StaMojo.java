package neu.lab.conflict;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import neu.lab.conflict.util.MavenUtil;
import neu.lab.conflict.writer.StaWriter;

@Mojo(name = "sta", defaultPhase = LifecyclePhase.VALIDATE)
public class StaMojo extends ConflictMojo{

	@Parameter( property = "append", defaultValue = "false" )
	public boolean append;
	@Parameter(property = "resultFilePath")
	protected String resultFilePath;
	
	@Override
	public void run() {
		if(resultFilePath==null)
			resultFilePath = MavenUtil.i().getBuildDir().getAbsolutePath()+"\\conflict.txt";
		new StaWriter().writeResult(resultFilePath,append);
	}
	
}
