package neu.lab.conflict;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import neu.lab.conflict.soot.JarAna;
import neu.lab.conflict.soot.SootNRiskCg;
import neu.lab.conflict.util.Conf;
import neu.lab.conflict.util.MavenUtil;
import neu.lab.conflict.util.UserConf;
import neu.lab.conflict.writer.ClassDupRiskWriter;

@Mojo(name = "classDetect", defaultPhase = LifecyclePhase.PRE_INTEGRATION_TEST)
public class ClsDetectMojo extends ConflictMojo{
	@Override
	public void run() {
		// abandon
		// new RiskPathWriter().write(Conf.outDir + "detect.txt");
		if (Conf.ANA_FROM_HOST&&!MavenUtil.i().getBuildDir().exists()) {
			getLog().warn(MavenUtil.i().getProjectInfo()+" dont't have target!  skip");
		}else {
			new ClassDupRiskWriter().writeRchNum(UserConf.getOutDir() + "classRch.csv",append);
			getLog().info("jarDeconstrction time:" + JarAna.runtime);
			getLog().info("call graph time:" + SootNRiskCg.runtime);
		}
	}
}
