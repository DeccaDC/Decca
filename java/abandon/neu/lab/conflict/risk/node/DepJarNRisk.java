package abandon.neu.lab.conflict.risk.node;

import java.util.Set;

import org.dom4j.Element;
import org.dom4j.tree.DefaultElement;

import neu.lab.conflict.vo.ClassVO;
import neu.lab.conflict.vo.DepJar;
import neu.lab.conflict.vo.NodeAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * risk caused by jar omitted
 * 
 * @author asus
 *
 */
/**
 * @author asus
 *
 */
public class DepJarNRisk {

	private DepJar depJar;
	private ConflictNRisk conflictAna;
	private Set<ClassVO> onlyClses;// exist in jar,but not in final
	// private Map<String, List<MethodVO>> onlyMthds;// method exist in jar
	// class,but not in final same name
	// // class;key:className,value:risk methods in class
	private Set<String> rchedMthds;// reached method in call-graph computed(entry class is host class)
	private Set<String> rchedServices;

	private Set<String> risk1Mthds;// reached and thrown
	private Set<String> risk2Mthds;// reached and thrown and called by method in other jar.

	private List<NodeNRisk> nodeRiskAnas;

	public DepJarNRisk(DepJar depJar, ConflictNRisk conflictAna) {
		this.depJar = depJar;
		onlyClses = new HashSet<ClassVO>();
		this.conflictAna = conflictAna;
		// onlyMthds = new HashMap<String, List<MethodVO>>();

		// initOnly(clsTb);
		getNodeRiskAnas();
		sum();
	}

	public Element getRiskPathEle() {
		Element ele = new DefaultElement("jarRisk");
		ele.addAttribute("id", depJar.toString());
		ele.addAttribute("reached_size", "" + rchedMthds.size());
		ele.addAttribute("reached_thrown_size", "" + getRisk1Mthds().size());
		ele.addAttribute("reached_thrown_service", "" + getRisk2Mthds().size());
		for (NodeNRisk nodeRiskAna : getNodeRiskAnas()) {
			ele.add(nodeRiskAna.getRiskPathEle());
		}
		return ele;
	}

	/**
	 * @return which jar will replace this jar when code is running.
	 */
	public DepJar getReplaceJar() {
		return conflictAna.getUsedDepJar();
	}

	private void sum() {
		rchedMthds = new HashSet<String>();
		rchedServices = new HashSet<String>();
		for (NodeNRisk nodeRiskAna : nodeRiskAnas) {
			rchedMthds.addAll(nodeRiskAna.getRchedMthds());
			rchedServices.addAll(nodeRiskAna.getRchedServices());
		}
	}

	public void addClass(ClassVO cls) {
		onlyClses.add(cls);
	}

	public List<NodeNRisk> getNodeRiskAnas() {
		if (nodeRiskAnas == null) {
			nodeRiskAnas = new ArrayList<NodeNRisk>();
			for (NodeAdapter node : depJar.getNodeAdapters()) {
				nodeRiskAnas.add(node.getNodeRiskAna(this));
			}
		}
		return nodeRiskAnas;
	}

	public Set<String> getRchedMthds() {
		return rchedMthds;
	}

	public Set<String> getRisk1Mthds() {
		if (risk1Mthds == null) {
			risk1Mthds = new HashSet<String>();
			for (NodeNRisk nodeRisk : getNodeRiskAnas()) {
				risk1Mthds.addAll(nodeRisk.getRisk1Mthds());
			}
		}
		return risk1Mthds;
	}

	public Set<String> getRisk2Mthds() {
		if (risk2Mthds == null) {
			risk2Mthds = new HashSet<String>();
			for (NodeNRisk nodeRisk : getNodeRiskAnas()) {
				risk2Mthds.addAll(nodeRisk.getRisk2Mthds());
			}
		}
		return risk2Mthds;
	}

	public Set<String> getRchedServices() {
		return rchedServices;
	}

	public DepJar getDepJar() {
		return depJar;
	}

}