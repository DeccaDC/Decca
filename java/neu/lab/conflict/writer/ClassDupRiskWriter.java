package neu.lab.conflict.writer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import abandon.neu.lab.conflict.risk.node.DepJarNRisks;
import abandon.neu.lab.conflict.risk.node.FourRow;
import abandon.neu.lab.conflict.statics.ClassDup;
import abandon.neu.lab.conflict.statics.ClassDups;
import abandon.neu.lab.conflict.statics.DupClsJarPair;
import abandon.neu.lab.conflict.statics.DupClsJarPairs;
import neu.lab.conflict.container.DepJars;
import neu.lab.conflict.util.MavenUtil;
import neu.lab.conflict.util.UserConf;

public class ClassDupRiskWriter {

	private ClassDups classDups;

	private DupClsJarPairs jarPairs;

	public ClassDupRiskWriter() {
		classDups = new ClassDups(DepJars.i());
	}


	public void writeByClass() {
		try {
			PrintWriter printer = new PrintWriter(
					new BufferedWriter(new FileWriter(new File(UserConf.getOutDir() + "classDupRisk.txt"), true)));
			printer.println("===============projectPath->" + MavenUtil.i().getProjectInfo());

			printer.println("=====class duplicate:");
			for (ClassDup classDup : classDups.getAllClsDup()) {
				printer.println(classDup.getRiskString());
			}
			printer.println("\n\n\n\n");
			printer.close();
		} catch (Exception e) {
			MavenUtil.i().getLog().error("can't write classDupByClass:", e);
		}
	}

	public void writeByJar(String outPath) {
		try {
			PrintWriter printer = new PrintWriter(new BufferedWriter(new FileWriter(new File(outPath), true)));
			printer.println("===============projectPath->" + MavenUtil.i().getProjectInfo());

			for (DupClsJarPair jarPair : getJarPairs().getAllJarPair()) {
				printer.println(jarPair.getRiskString());
			}
			printer.println("\n\n\n\n");
			printer.close();
		} catch (Exception e) {
			MavenUtil.i().getLog().error("can't write classDupByJar:", e);
		}
	}

	public void writeRchNum(String outPath, boolean append) {
		try {
			CSVPrinter printer = new CSVPrinter(new FileWriter(outPath, append), CSVFormat.DEFAULT);
			DepJarNRisks jarCgs = new DepJarNRisks();
			for (DupClsJarPair jarPair : getJarPairs().getAllJarPair()) {
				FourRow fourRow = jarPair.getPairRisk(jarCgs).getFourRow();
				printer.printRecord(fourRow.mthdRow);
				printer.printRecord(fourRow.mthdNameRow);
				printer.printRecord(fourRow.serviceRow);
				printer.printRecord(fourRow.serviceNameRow);
			}
			printer.close();
		} catch (Exception e) {
			MavenUtil.i().getLog().error("can't write reach class number:", e);
		}
	}

	private DupClsJarPairs getJarPairs() {
		if (jarPairs == null)
			jarPairs = new DupClsJarPairs(classDups);
		return jarPairs;
	}
}
