package neu.lab.conflict.writer;

import java.io.CharArrayWriter;
import java.io.FileWriter;
import java.io.Writer;
import java.util.Set;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.dom4j.tree.DefaultElement;

import neu.lab.conflict.container.Conflicts;
import neu.lab.conflict.risk.jar.ConflictJRisk;
import neu.lab.conflict.risk.jar.DepJarJRisk;
import neu.lab.conflict.util.MavenUtil;
import neu.lab.conflict.vo.Conflict;

public class RiskLevelWriter {

	/**
	 * 输出到XML中
	 * 
	 * @param outPath
	 * @param append
	 */
	public void writeRiskLevelXML(String outPath, boolean append, boolean subdivisionLevel) {
		try {
			Writer fileWriter;
			String fileName = MavenUtil.i().getProjectGroupId() + ":" + MavenUtil.i().getProjectArtifactId() + ":"
					+ MavenUtil.i().getProjectVersion();
			if (outPath == null) {
				fileWriter = new CharArrayWriter();
			} else {
				fileWriter = new FileWriter(outPath + fileName.replace('.', '_').replace(':', '_') + ".xml", append);
			}
			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setNewlines(true);
			XMLWriter xmlWriter = new XMLWriter(fileWriter, format);
			Document document = DocumentHelper.createDocument();
			document.addComment("Our goal\r\n"
					+ "Decca aims to detect dependency conflict issues and assess their severity levels according to their impacts on the system and maintenance costs. The severity levels are defined as follows:\r\n"
					+ "\r\n"
					+ "Level 1: It is a benign conflict, because the feature set referenced by host project is a subset of the actual loaded feature set. Besides, the shadowed version completely cover the feature set used by the host project. This indicates that any orders of the specification of these duplicate classes on the classpath will not induce serious runtime errors. Therefore, this is a benign conflict and will not affect the system reliability at runtime.\r\n"
					+ "\r\n"
					+ "Level 2: It is a benign conflict, because the feature set referenced by host project is a subset of the actual loaded feature set. However, the shadowed feature set doesn’t cover the referenced feature set. It is considered as a potential risk for system reliability since different orders of the specifications of these duplicate classes on the classpath (e.g., in different running environment or building platform) might induce runtime errors. Compared with warnings at Level 1, warnings at Level 2 needs more costs to maintain.\r\n"
					+ "\r\n"
					+ "Level 3: It is a harmful conflict, as the actual loaded feature set does not consume the feature set referenced by host project. The runtime errors will occur when the expected feature cannot be accessed. However, in this case, the shadowed feature set completely cover the feature set referenced by host project. Therefore, it can be solved by adjusting the dependency order on the classpath, without changing any source code.\r\n"
					+ "\r\n"
					+ "Level 4: It is a harmful conflict, as the actual loaded feature set does not cover the referenced feature set. Besides, the shadowed feature set does not consume the referenced feature set neither. Therefore, this type of conflicts can not be easily resolved by adjusting the dependency orders on the classpath. In this case, to solve these issues, it requires more efforts to ensure the multiple versions of classes could be referenced by host project.");
			Element root = document.addElement("project");
			root.addAttribute("project",
					MavenUtil.i().getProjectGroupId() + ":" + MavenUtil.i().getProjectArtifactId());
			root.addAttribute("projectInfo", MavenUtil.i().getProjectInfo());
			for (Conflict conflict : Conflicts.i().getConflicts()) {
				root.add(PrintConflictRiskLevel(conflict, subdivisionLevel));
			}
			xmlWriter.write(document);
			xmlWriter.close();
		} catch (Exception e) {
			MavenUtil.i().getLog().error("can't write jar duplicate risk:", e);
		}
	}

	/**
	 * method:输出所有遍历方法的风险等级，无风险1/2，有风险3/4 author:wangchao time:2018-9-24 13:25:18
	 */
	private Element PrintConflictRiskLevel(Conflict conflict, boolean subdivisionLevel) {
		Element elements = new DefaultElement("conflicts");
		Element element = new DefaultElement("conflictJar");
		elements.add(element);
		element.addAttribute("groupId-artifactId", conflict.getSig());
		element.addAttribute("versions", conflict.getVersions().toString());
		ConflictJRisk conflictJRisk = conflict.getJRisk();
		int riskLevel = 0;
		Set<String> usedRiskMethods = conflictJRisk.getConflictLevel();
		if (subdivisionLevel) {
			riskLevel = conflictJRisk.getRiskLevel();
		} else {
			if (usedRiskMethods.isEmpty()) {
				riskLevel = 1;
			} else {
				riskLevel = 3;
			}
		}
		
		element.addAttribute("riskLevel", riskLevel + "");

		element.add(AddPath(conflict));
		Element risksEle = element.addElement("RiskMethods");

		for (String method : usedRiskMethods) {
			Element riskMethod = new DefaultElement("RiskMethod");
			risksEle.add(riskMethod);
			riskMethod.addText(method.replace('<', ' ').replace('>', ' '));
		}
		if (riskLevel == 1) {
			risksEle.addAttribute("tip", "jar was be referenced and be loaded !");
		} else if (riskLevel == 2) {
			risksEle.addAttribute("tip", "jar was be referenced and be loaded !");
		} else if (riskLevel == 3) {
			risksEle.addAttribute("tip", "methods would be referenced but not be loaded !");
		} else if (riskLevel == 4) {
			risksEle.addAttribute("tip", "methods would be referenced but not be loaded !");
		}
		return elements;
	}

	/**
	 * method:添加jar包path author:wangchao time:2018-9-23 13:30:09
	 */
	private Element AddPath(Conflict conflict) {
		Element elements = new DefaultElement("versions");
		// 冲突的jar包
		ConflictJRisk conflictJRisk = conflict.getJRisk();
		for (DepJarJRisk jarRisk : conflictJRisk.getJarRisks()) {
			Element element = new DefaultElement("version");
			elements.add(element);
			element.addAttribute("versionId", jarRisk.getVersion());
			element.addAttribute("loaded", "" + jarRisk.getConflictJar().isSelected());
			Element path = new DefaultElement("path");
			element.add(path);
			path.addText(jarRisk.getConflictJar().getAllDepPath());
		}
		return elements;
	}
}
