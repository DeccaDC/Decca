package neu.lab.conflict.writer;

import java.io.CharArrayWriter;
import java.io.FileWriter;
import java.io.Writer;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import abandon.neu.lab.conflict.risk.node.DepJarNRisks;
import abandon.neu.lab.conflict.risk.node.FourRow;
import abandon.neu.lab.conflict.statics.ClassDups;
import abandon.neu.lab.conflict.statics.DupClsJarPair;
import abandon.neu.lab.conflict.statics.DupClsJarPairs;
import neu.lab.conflict.container.DepJars;
import neu.lab.conflict.container.Conflicts;
import neu.lab.conflict.util.MavenUtil;
import neu.lab.conflict.vo.Conflict;

public class JarRchedWriter {
	public void writeCsv(String outPath, boolean append) {
		try {
			// final String[] header = { "projectId", "conflictId", "type", "origin",
			// "load", "other" };
			// CSVFormat format = CSVFormat.DEFAULT.withHeader(header);

			CSVPrinter printer = new CSVPrinter(new FileWriter(outPath, append), CSVFormat.DEFAULT);
			for (Conflict conflict : Conflicts.i().getConflicts()) {
				FourRow fourRow = conflict.getNRisk().getFourRow();
				printer.printRecord(fourRow.mthdRow);
				printer.printRecord(fourRow.mthdNameRow);
				printer.printRecord(fourRow.serviceRow);
				printer.printRecord(fourRow.serviceNameRow);
				printer.flush();
			}
			printer.close();
		} catch (Exception e) {
			MavenUtil.i().getLog().error("can't write risk result:", e);
		}
	}

	public void writeXml(String outPath, boolean append, boolean detectClass) {
		try {
			Writer fileWriter;
			if (outPath == null) {
				fileWriter = new CharArrayWriter();
			} else {
				fileWriter = new FileWriter(outPath, append);
			}
			OutputFormat format = OutputFormat.createPrettyPrint();
			XMLWriter xmlWriter = new XMLWriter(fileWriter, format);
			Document document = DocumentHelper.createDocument();
			Element root = document.addElement("project");
			root.addAttribute("project", MavenUtil.i().getProjectGroupId()+":"+ MavenUtil.i().getProjectArtifactId());
			root.addAttribute("projectInfo", MavenUtil.i().getProjectInfo());
			// add jar conflict
			Element jarConfs = root.addElement("conflicts");
//			jarConfs.addAttribute("type", "jar");
			for (Conflict conflict : Conflicts.i().getConflicts()) {
				jarConfs.add(conflict.getNRisk().getRchNumEle());
			}
			if (detectClass) {// add class conflict
				Element clsConfs = root.addElement("conflicts");
				clsConfs.addAttribute("type", "class");
				ClassDups classDups = new ClassDups(DepJars.i());
				DupClsJarPairs jarPairs = new DupClsJarPairs(classDups);
				DepJarNRisks jarCgs = new DepJarNRisks();
				for (DupClsJarPair jarPair : jarPairs.getAllJarPair()) {
					clsConfs.add(jarPair.getPairRisk(jarCgs).getConflictElement());
				}
			}
			xmlWriter.write(document);
			xmlWriter.close();
			if (null == outPath) {//output to console
				MavenUtil.i().getLog().info(fileWriter.toString());
			} else {//output to file
				MavenUtil.i().getLog().info("The result was exported to the file " + outPath);
			}
			fileWriter.close();

		} catch (Exception e) {
			MavenUtil.i().getLog().error("can't write risk result:", e);
		}
	}

	public void writeAll(String string, boolean append) {
		
	}

}
