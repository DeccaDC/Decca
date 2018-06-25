package neu.lab.conflict.writer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import neu.lab.conflict.Conf;
import neu.lab.conflict.container.NodeConflicts;
import neu.lab.conflict.util.MavenUtil;
import neu.lab.conflict.vo.DepJar;
import neu.lab.conflict.vo.NodeConflict;

public class RiskPathWriter {

	public void write(String outPath) {
		try {
			List<NodeConflict> conflicts = NodeConflicts.i().getConflicts();
			PrintWriter printer = new PrintWriter(
					new BufferedWriter(new FileWriter(new File(outPath), true)));
			printer.println("===============projectPath->" + MavenUtil.i().getProjectInfo());
			if (conflicts.size() == 0) {
				printer.println("NO_CONFLICT");
			} else {
				for (NodeConflict conflict : conflicts) {
					printer.println(conflict.getRiskAna().getRiskString());
				}
			}
			printer.close();
		}catch(Exception e) {
			MavenUtil.i().getLog().error("can't write jar duplicate risk:", e);
		}

	}

	private void writeConflict(NodeConflict conflict) {
		// printer.println(conflict.toString());
		//
		// Set<String> rchMthds = conflict.getRchedMthds();
		//
		// DepJar usedJar = conflict.getUsedDepJar();
		// writeJarRch(rchMthds, usedJar);
		// for (DepJar depJar : conflict.getDepJars()) {
		// if (depJar != usedJar) {
		// writeJarRch(rchMthds, depJar);
		// }
		// }
		// printer.println("\n");
		//
		// for (JarRiskAna jarRisk : conflict.getJarRiskAnas()) {
		//// printer.println(jarRisk.getRiskStr());
		// printer.println();
		// }
	}

	// private void writeJarRch(Set<String> rchMthds, DepJar depJar) {
	// Set<String> onlyMthds = min(rchMthds, depJar.getAllMthd());
	// printer.println("reachmethod dont exist in " + depJar.toString() + " (" +
	// onlyMthds.size() + "/"
	// + rchMthds.size() + ")");
	// for (String onlyMthd : onlyMthds) {
	// printer.println("-" + onlyMthd);
	// }
	// printer.println();
	// }

	// private Set<String> min(Set<String> total, Set<String> some) {
	// Set<String> only = new HashSet<String>();
	// for (String str : total) {
	// if (!some.contains(str))
	// only.add(str);
	// }
	// return only;
	// }

}
