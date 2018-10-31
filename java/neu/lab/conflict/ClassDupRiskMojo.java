package neu.lab.conflict;


import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import neu.lab.conflict.util.UserConf;
import neu.lab.conflict.writer.ClassDupRiskWriter;

@Mojo(name = "classDupRisk", defaultPhase = LifecyclePhase.VALIDATE)
public class ClassDupRiskMojo extends ConflictMojo{

	@Override
	public void run() {
		//abandon
		//win下运行此Mojo
		//new ClassDupRiskWriter().writeByJar(UserConf.getOutDir() + "classDupByJar.txt");
		//mac下测试此Mojo
		new ClassDupRiskWriter().writeByJar(UserConf.getOutDir4Mac() + "classDupByJar.txt");
	}

}
