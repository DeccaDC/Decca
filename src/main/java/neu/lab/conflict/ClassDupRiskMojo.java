package neu.lab.conflict;


import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import neu.lab.conflict.util.MavenUtil;
import neu.lab.conflict.writer.ClassDupRiskWriter;

@Mojo(name = "classDupRisk", defaultPhase = LifecyclePhase.VALIDATE)
public class ClassDupRiskMojo extends ConflictMojo{
	
	@Parameter(property = "resultFilePath")
	protected String resultFilePath;
	@Parameter( property = "append", defaultValue = "false" )
	public boolean append;
	
	@Override
	public void run() {
		if(resultFilePath==null)
			resultFilePath = MavenUtil.i().getBuildDir().getAbsolutePath()+"\\result.txt";
		new ClassDupRiskWriter().writeByJar(resultFilePath,append);		
	}

}
