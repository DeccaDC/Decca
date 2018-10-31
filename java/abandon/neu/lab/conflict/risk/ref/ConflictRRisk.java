package abandon.neu.lab.conflict.risk.ref;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Element;
import org.dom4j.tree.DefaultElement;

import neu.lab.conflict.vo.Conflict;
import neu.lab.conflict.vo.DepJar;

public class ConflictRRisk {

	private Conflict conflict;
	private List<DepJarRRisk> jarRisks;

	public ConflictRRisk(Conflict conflict1) {
		this.conflict = conflict1;
		jarRisks = new ArrayList<DepJarRRisk>();
		for(DepJar jar:conflict.getDepJars()) {
			jarRisks.add(new DepJarRRisk(jar,this));
		}
	}
	
	public DepJar getUsedDepJar() {
		return conflict.getUsedDepJar();
	}
	
	public Element getRiskEle() {
		Element ele = new DefaultElement("conflictRisk");
		ele.addAttribute("id", conflict.toString());
		for (DepJarRRisk jarRiskAna : jarRisks) {
			ele.add(jarRiskAna.getRiskEle());
		}
		return ele;
	}
}
