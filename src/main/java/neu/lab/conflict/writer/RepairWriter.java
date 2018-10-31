package neu.lab.conflict.writer;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import neu.lab.conflict.container.Conflicts;
import neu.lab.conflict.risk.jar.ConflictJRisk;
import neu.lab.conflict.util.MavenUtil;
import neu.lab.conflict.vo.Conflict;

public class RepairWriter {

	public void write(String outPath) {
		String projectName = MavenUtil.i().getProjectGroupId() + MavenUtil.i().getProjectArtifactId()
				+ MavenUtil.i().getProjectVersion();
		String distanceFile = outPath + projectName + ".txt";
		PrintWriter printer = null;
		try {
			printer = new PrintWriter(new BufferedWriter(new FileWriter(distanceFile)));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		for (Conflict conflict : Conflicts.i().getConflicts()) {
			ConflictJRisk conflictJRisk = conflict.getJRisk();
			int level = conflictJRisk.getRiskLevel();
			printer.println("冲突>>>>" + conflict.toString());
			printer.println("冲突风险等级>>>>" + level);
			printer.println("========================");
			printer.println();
			printer.close();
		}
	}
}
