package neu.lab.conflict;

import java.io.File;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import neu.lab.conflict.soot.JarAna;
import neu.lab.conflict.soot.SootNRiskCg;
import neu.lab.conflict.util.Conf;
import neu.lab.conflict.util.MavenUtil;
import neu.lab.conflict.writer.JarRchedWriter;

/**
 *
 */
@Mojo(name = "detect", defaultPhase = LifecyclePhase.PRE_INTEGRATION_TEST)
public class DetectMojo extends ConflictMojo {

	@Parameter(property = "resultFileType", defaultValue = "csv")
	protected String resultFileType;

	@Parameter(property = "resultFilePath")
	protected String resultFilePath;
	
	@Parameter(property = "detectClass",defaultValue = "false")
	protected boolean detectClass;
	
	@Parameter(property = "detectJar",defaultValue = "true")
	protected boolean detectJar;

	@Override
	public void run() {
		// new RiskPathWriter().write(Conf.outDir + "detect.txt");

		if (Conf.ANA_FROM_HOST && !MavenUtil.i().getBuildDir().exists()) {
			getLog().warn(MavenUtil.i().getProjectInfo() + " dont't have target! skip");
		} else {
//			if (Conf.CLASS_DUP)
//				FinalClasses.init(DepJars.i());
			if ("xml".equals(resultFileType)) {
				if (null == resultFilePath) {
					resultFilePath = "d:\\ws\\jarRch\\" + File.separator
							+MavenUtil.i().getProjectCor().replace(":", "+") +".xml";
				}
				new JarRchedWriter().writeXml(resultFilePath, append,detectClass);
			} else if ("csv".equals(resultFileType)) {
				if (null == resultFilePath) {
					resultFilePath = "d:\\ws\\jarRch\\" + File.separator
							+MavenUtil.i().getProjectCor().replace(":", "+") +".csv";
				}
				new JarRchedWriter().writeCsv(resultFilePath, append);
			} else if("all".equals(resultFileType)) {
				new JarRchedWriter().writeAll("d:\\ws\\jarRch\\", append);
			}else {
				getLog().error("resultFileType can be xml/csv , can't be " + resultFileType);
			}
			getLog().info("time to deconstruct jar :" + JarAna.runtime);
			getLog().info("time to compute call-graph :" + SootNRiskCg.runtime);
			
		}

	}

}
