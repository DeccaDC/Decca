package abandon.neu.lab.conflict.risk.ref;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.dom4j.Element;
import org.dom4j.tree.DefaultElement;

import neu.lab.conflict.vo.DepJar;
import neu.lab.conflict.vo.NodeAdapter;

public class DepJarRRisk {

	private DepJar depJar;
	private ConflictRRisk conflictRisk;
	private List<NodeRRisk> nodeRisks;
	private Set<String> throwClses;

	public DepJarRRisk(DepJar depJar, ConflictRRisk conflictRisk) {
		this.depJar = depJar;
		this.conflictRisk = conflictRisk;
		nodeRisks = new ArrayList<NodeRRisk>();
		for (NodeAdapter node : depJar.getNodeAdapters()) {
			nodeRisks.add(new NodeRRisk(node, this));
		}
	}

	public Set<String> getThrowedClses() {
		if (throwClses == null) {
			throwClses = depJar.getOnlyClses(conflictRisk.getUsedDepJar());
		}
		return throwClses;
	}
	
	public Element getRiskEle() {
		Element ele = new DefaultElement("jarRisk");
		ele.addAttribute("id", depJar.toString());
		for (NodeRRisk nodeRiskAna : nodeRisks) {
			ele.add(nodeRiskAna.getRiskEle());
		}
		return ele;
	}

}
