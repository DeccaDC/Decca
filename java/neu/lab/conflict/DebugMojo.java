package neu.lab.conflict;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import neu.lab.conflict.container.DepJars;
import neu.lab.conflict.container.NodeAdapters;
import neu.lab.conflict.util.MavenUtil;
import neu.lab.conflict.vo.NodeAdapter;
import neu.lab.conflict.writer.DistanceWriter;
import neu.lab.conflict.writer.JarRiskWriter;
import neu.lab.conflict.writer.RiskPathWriter;
	// abandon
@Mojo(name = "debug", defaultPhase = LifecyclePhase.VALIDATE)
public class DebugMojo extends ConflictMojo {

	// @Parameter( property = "clsRisk", defaultValue = "true" )
	// public boolean clsRisk;
	//
	// @Parameter( property = "mthdRisk", defaultValue = "true" )
	// public boolean mthdRisk;

	@Override
	public void run() {
		// writeDepNum(Conf.outDir + "debug.csv");
		// new
		// neu.lab.conflict.writer.ClassDupRiskWriter().writeByJar(UserConf.getOutDir()
		// +
		// "classDupByJar.txt");

		 printMthdProb();
	}

	private void printNeibor() {
		String outPath = "D:\\ws_testcase\\image\\neibor.txt";
		java.io.File f = new java.io.File(outPath);
		if (!f.getParentFile().exists()) {
			f.getParentFile().mkdirs();
		}
		new DistanceWriter().writeNeibor(outPath, true);
	}

	public void printClsDistance() {
		String outPath = "D:\\ws_testcase\\image\\distance_cls\\"
				+ (MavenUtil.i().getProjectCor() + ".txt").replace(":", "+");
		java.io.File f = new java.io.File(outPath);
		if (!f.getParentFile().exists()) {
			f.getParentFile().mkdirs();
		}
		new DistanceWriter().writeOnceClsDistance(outPath, false);
	}

	public void printMthdDistance() {
		String outPath = "D:\\ws_testcase\\image\\distance_mthd\\"
				+ (MavenUtil.i().getProjectCor() + ".txt").replace(":", "+");
		java.io.File f = new java.io.File(outPath);
		if (!f.getParentFile().exists()) {
			f.getParentFile().mkdirs();
		}
		new DistanceWriter().writeMthdDistance(outPath, false);
	}

	public void printMthdProb() {
		String outDir = "D:\\ws_testcase\\image\\distance\\";
		java.io.File f = new java.io.File(outDir);
		if (!f.exists()) {
			f.mkdirs();
		}
		new JarRiskWriter().write(outDir, false);
	}

	public void printPath() {
		// new RiskPathWriter().writePath("D:\\cWS\\notepad++\\riskPath.xml", false);
		new RiskPathWriter().writeRefRisk("D:\\cWS\\notepad++\\riskPath.xml", true);
	}

	public void writeDepNum(String outPath) {
		try {
			CSVPrinter printer = new CSVPrinter(new FileWriter(outPath, true), CSVFormat.DEFAULT);
			List<String> record = new ArrayList<String>();
			int directDep = 0;
			int allNode = -1 + NodeAdapters.i().getAllNodeAdapter().size();
			int allJar = -1 + DepJars.i().getAllDepJar().size();
			int allUsedJar = -1;
			for (NodeAdapter node : NodeAdapters.i().getAllNodeAdapter()) {
				if (node.getNodeDepth() == 2) {
					directDep++;
				}
				if (node.isNodeSelected()) {
					allUsedJar++;
				}
			}
			record.add(MavenUtil.i().getProjectInfo());
			record.add("" + directDep);
			record.add("" + allNode);
			record.add("" + allJar);
			record.add("" + allUsedJar);
			printer.printRecord(record);
			printer.close();
		} catch (Exception e) {
			MavenUtil.i().getLog().error("can't write debug:", e);
		}
	}
}
