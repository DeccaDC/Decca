package neu.lab.conflict;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import neu.lab.conflict.container.DepJars;
import neu.lab.conflict.container.NodeAdapters;
import neu.lab.conflict.container.NodeConflicts;
import neu.lab.conflict.statics.ClassDups;
import neu.lab.conflict.statics.DupClsJarPair;
import neu.lab.conflict.statics.DupClsJarPairs;
import neu.lab.conflict.util.MavenUtil;
import neu.lab.conflict.util.UserConf;
import neu.lab.conflict.vo.DepJar;
import neu.lab.conflict.vo.NodeAdapter;
import neu.lab.conflict.vo.NodeConflict;
import neu.lab.conflict.writer.ClassDupRiskWriter;

@Mojo(name = "debug", defaultPhase = LifecyclePhase.VALIDATE)
public class DebugMojo extends ConflictMojo {

	@Parameter(property = "conflicts")
	public String conflicts;

	@Override
	public void run() {
		// writeDepNum(Conf.outDir + "debug.csv");
		// new ClassDupRiskWriter().writeByJar(UserConf.getOutDir() +
		// "classDupByJar.txt");
		try {
			if (conflicts != null) {
				calLevel(conflicts);
			} else {
				writeConflict();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void calLevel(String conflicts) throws Exception {
		String[] conflictSigs = conflicts.split(";");
		Set<String> conflictSigSet = new HashSet<String>();
		for (String conflictSig : conflictSigs) {
			conflictSigSet.add(conflictSig);
		}
		PrintWriter printer = new PrintWriter(new FileWriter("D:\\cWS\\notepad++\\debug.txt"));
		for (NodeConflict conflict : NodeConflicts.i().getConflicts()) {
			if (conflictSigSet.contains(conflict.getSig())) {
				printer.println(conflict.getSig()+" "+conflict.getRiskAna().getRiskLevel());
			}
		}
		printer.close();
	}

	private void writeConflict() throws Exception {
		PrintWriter printer = new PrintWriter(new FileWriter("D:\\cWS\\notepad++\\debug.txt"));
		StringBuilder sb = new StringBuilder();
		for (NodeConflict conflict : NodeConflicts.i().getConflicts()) {
			
			printer.println(conflict.getSig());
			sb.append(System.lineSeparator());
			sb.append(conflict.getSig()+System.lineSeparator());
			for(DepJar depJar : conflict.getDepJars()) {
				sb.append(depJar.toString()+"  "+depJar.isSelected()+System.lineSeparator());
				for (NodeAdapter node : depJar.getNodeAdapters()) {
					sb.append(node.getWholePath()+System.lineSeparator());
				}
			}
		}
		ClassDups classDups = new ClassDups(DepJars.i());
		DupClsJarPairs jarPairs = new DupClsJarPairs(classDups);
		for (DupClsJarPair jarPair : jarPairs.getAllJarPair()) {
			if(-1==soot.util.Reval.revalClass(MavenUtil.i().getProjectSig(), jarPair.getSig()))
				continue;
			printer.println(jarPair.getSig());
			sb.append(System.lineSeparator());
			sb.append(jarPair.getSig()+System.lineSeparator());
			
			sb.append(jarPair.getJar1().toString()+System.lineSeparator());
			for(NodeAdapter node:jarPair.getJar1().getNodeAdapters()) {
				sb.append(node.getWholePath()+System.lineSeparator());
			}
			
			sb.append(jarPair.getJar2().toString()+System.lineSeparator());
			for(NodeAdapter node:jarPair.getJar2().getNodeAdapters()) {
				sb.append(node.getWholePath()+System.lineSeparator());
			}
		}
		printer.println();
		printer.println();
		printer.println(MavenUtil.i().getProjectInfo());
		printer.println(sb.toString());
		printer.close();
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
