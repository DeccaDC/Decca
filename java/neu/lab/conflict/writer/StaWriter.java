package neu.lab.conflict.writer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

import abandon.neu.lab.conflict.statics.ClassDup;
import abandon.neu.lab.conflict.statics.ClassDups;
import abandon.neu.lab.conflict.statics.NodeDup;
import abandon.neu.lab.conflict.statics.NodeDups;
import neu.lab.conflict.container.DepJars;
import neu.lab.conflict.container.NodeAdapters;
import neu.lab.conflict.container.Conflicts;
import neu.lab.conflict.util.MavenUtil;
import neu.lab.conflict.util.UserConf;
import neu.lab.conflict.vo.Conflict;

public class StaWriter {
	private Conflicts nodeConflicts;
	private NodeDups nodeDups;
	private ClassDups classDups;

	public StaWriter() {
		nodeConflicts = Conflicts.i();
		nodeDups = new NodeDups(NodeAdapters.i());
		classDups = new ClassDups(DepJars.i());
	}

	public void writeResult() {
		// writeLevel();
		writeDetail();
	}

	public void writeDetail() {
		try {
			PrintWriter printer = new PrintWriter(
					new BufferedWriter(new FileWriter(new File(UserConf.getOutDir4Mac()/*getOutDir()*/ + "detail.txt"), true)));
			printer.println("===============projectPath->" + MavenUtil.i().getProjectInfo());
			if (nodeConflicts.getConflicts().size() > 0) {
				printer.print("+confJar  ");
			}
			if (nodeDups.getAllNodeDup().size() > 0) {
				printer.print("+dupJar  ");
			}
			if (classDups.getAllClsDup().size() > 0) {
				printer.print("+dupCls  ");
			}
			if (nodeConflicts.getConflicts().size() == 0 && classDups.getAllClsDup().size() == 0) {
				printer.print("+correct  ");
				if(nodeDups.getAllNodeDup().size() == 0) {
					printer.print("+abs-correct  ");
				}
			}
			printer.println();
			printer.println("=====dependency conflict:");
			for (Conflict nodeConflict : nodeConflicts.getConflicts()) {
				printer.println(nodeConflict.toString());
			}
			printer.println("=====dependency duplicate:");
			for (NodeDup nodeDup : nodeDups.getAllNodeDup()) {
				printer.println(nodeDup.toString());
			}
			printer.println("=====class duplicate:");
			for (ClassDup classDup : classDups.getAllClsDup()) {
				printer.println(classDup.toString());
			}
			printer.println("\n\n\n\n");
			printer.close();
		} catch (Exception e) {
			MavenUtil.i().getLog().error("can't write statistic detail:", e);
		}
	}

	private void writeLevel() {
		writeLevel("allProjects.txt");
		if (nodeConflicts.getConflicts().size() > 0) {
			writeLevel("conflictJarProjects.txt");
		}
		if (nodeDups.getAllNodeDup().size() > 0) {
			writeLevel("duplicateJarProjects.txt");
		}
		if (classDups.getAllClsDup().size() > 0) {
			writeLevel("duplicateClassProjects.txt");
		}
	}

	private void writeLevel(String outName) {
		try {
			PrintWriter printer = new PrintWriter(
					new BufferedWriter(new FileWriter(new File(UserConf.getOutDir() + outName), true)));
			printer.println(MavenUtil.i().getProjectInfo());
			printer.close();

		} catch (Exception e) {
			MavenUtil.i().getLog().error("can't write statistic level:", e);
		}
	}
}
