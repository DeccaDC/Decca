package neu.lab.conflict.writer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

import neu.lab.conflict.container.NodeConflicts;
import neu.lab.conflict.util.MavenUtil;
import neu.lab.conflict.vo.DepJar;
import neu.lab.conflict.vo.NodeAdapter;
import neu.lab.conflict.vo.NodeConflict;

public class JarDupRiskWriter {

	public void write(String outPath,boolean append) {
		try {
			PrintWriter printer = new PrintWriter(new BufferedWriter(new FileWriter(new File(outPath), append)));
			printer.println("===============projectPath->" + MavenUtil.i().getProjectInfo());
			for (NodeConflict nodeConflict : NodeConflicts.i().getConflicts()) {

				if (nodeConflict.getNodeAdapters().size() == 2) {// only two version
					DepJar[] depJars = nodeConflict.getDepJars().toArray(new DepJar[2]);
					DepJar depJar1 = depJars[0];
					DepJar depJar2 = depJars[1];
					if (depJar1 != null && depJar2 != null) {
						printer.println("=======conflict:" + "<" + depJar1.toString() + ">" + "<" + depJar2.toString() + ">"
								+ " size:" + nodeConflict.getNodeAdapters().size());
						printRisk(printer,depJar1,depJar2);
					}
				}
			}

			printer.println("\n\n");
			printer.close();
		} catch (Exception e) {
			MavenUtil.i().getLog().error("can't write versionupdate:", e);
		}

	}
	public void printRisk(PrintWriter printer,DepJar depJar1,DepJar depJar2) {
		
		printer.println("====Risk for ClassNotFoundException/NotClassDefFoundError:");
		printer.println("  classes that only exist in "+depJar1.toString());
		for(String clsSig:depJar1.getOnlyClses(depJar2)) {
			printer.println(clsSig);
		}
		printer.println("  classes that only exist in "+depJar2.toString());
		for(String clsSig:depJar2.getOnlyClses(depJar1)) {
			printer.println(clsSig);
		}
		
		printer.println("====Risk for NoSuchMethodException/NoSuchMethodError:");
		printer.println("  methods that only exist in "+depJar1.toString());
		for(String clsSig:depJar1.getOnlyMthds(depJar2)) {
			printer.println(clsSig);
		}
		printer.println("  methods that only exist in "+depJar2.toString());
		for(String clsSig:depJar2.getOnlyMthds(depJar1)) {
			printer.println(clsSig);
		}
		printer.println();
	}

}
