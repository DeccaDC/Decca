package neu.lab.conflict.writer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

import neu.lab.conflict.container.Conflicts;
import neu.lab.conflict.util.MavenUtil;
import neu.lab.conflict.vo.DepJar;
import neu.lab.conflict.vo.NodeAdapter;
import neu.lab.conflict.vo.Conflict;

public class JarDupRiskWriter {

	public void write(String outPath) {
		try {
			PrintWriter printer = new PrintWriter(new BufferedWriter(new FileWriter(new File(outPath), true)));
			printer.println("===============projectPath->" + MavenUtil.i().getProjectInfo());
			//System.out.println("this is my debug3 ");
			for (Conflict nodeConflict : Conflicts.i().getConflicts()) {
				//System.out.println("this is my debug2 " + Conflicts.i().getConflicts().size() + nodeConflict.getNodeAdapters().size());
				//for (  NodeAdapter a : nodeConflict.getNodeAdapters()) {
					//System.out.println(a.toString());
				//}
				// 有疑问，此处会存入2个一模一样的nodeadapter
				if (nodeConflict.getNodeAdapters().size() == 2) {// only two version
					System.out.println("this is my debug ");
					DepJar[] depJars = nodeConflict.getDepJars().toArray(new DepJar[2]);
					DepJar depJar1 = depJars[0];
					DepJar depJar2 = depJars[1];
					if (depJar1 != null && depJar2 != null) {
						printer.println("=======jarConflict:" + "<" + depJar1.toString() + ">" + "<" + depJar2.toString() + ">"
								+ " size:" + nodeConflict.getNodeAdapters().size());
						printRisk(printer,depJar1,depJar2);
					}
				}
			}

			printer.println("\n\n");
			printer.close();
		} catch (Exception e) {
			MavenUtil.i().getLog().error("can't write jarDuplicate:", e);
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
