package neu.lab.conflict.writer;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import abandon.neu.lab.conflict.distance.MethodProbDistances;
import neu.lab.conflict.container.Conflicts;
import neu.lab.conflict.risk.jar.DepJarJRisk;
import neu.lab.conflict.util.MavenUtil;
import neu.lab.conflict.vo.Conflict;

/**
 * write result that calculated by jar-used.
 * 
 * @author asus
 *
 */
public class JarRiskWriter {

	public void write(String outDir, boolean append) {
		String projectSig = (MavenUtil.i().getProjectCor()).replace(":", "+");
		for (Conflict conflict : Conflicts.i().getConflicts()) {

			String conflictSig = conflict.getSig().replace(":", "+");
			for (DepJarJRisk jarRisk : conflict.getJRisk().getJarRisks()) {
				String outPath = outDir + projectSig + "@" + conflictSig + "@" + jarRisk.getVersion() + ".txt";
				writeJarRisk(jarRisk, outPath, append);
			}

		}

	}

	/**
	 * com.cloudera.oryx+oryx-api+2.4.0@neu.lab:plug.testcase.homemade.b@1.0
	 * 
	 * @param outPath
	 * @param append
	 */
	public void writeJarRisk(DepJarJRisk jarRisk, String outPath, boolean append) {
		MethodProbDistances distances = jarRisk.getMethodProDistances();
		if (!distances.isEmpty()) {
			PrintWriter printer;
			try {
				printer = new PrintWriter(new BufferedWriter(new FileWriter(outPath)));
				printer.println(distances);
				printer.close();
			} catch (IOException e) {
				MavenUtil.i().getLog().error("can't write jarRisk ", e);
			}
		}
	}

}
