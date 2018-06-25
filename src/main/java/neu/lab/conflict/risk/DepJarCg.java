package neu.lab.conflict.risk;

import java.util.Set;

import neu.lab.conflict.vo.ClassVO;
import neu.lab.conflict.vo.DepJar;
import neu.lab.conflict.vo.NodeAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

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
public class DepJarCg {

	private DepJar depJar;
	private Set<ClassVO> onlyClses;// exist in jar,but not in final
//	private Map<String, List<MethodVO>> onlyMthds;// method exist in jar class,but not in final same name
//													// class;key:className,value:risk methods in class
	private Set<String> rchedMthds;// reached method in call-graph computed(entry class is host class)
	private Set<String> rchedServices;
	
	private Set<String> risk1Mthds;// reached and thrown
	private Set<String> risk2Mthds;// reached and thrown and called by method in other jar.

	private List<NodeCg> nodeRiskAnas;

	public DepJarCg(DepJar depJar) {
		this.depJar = depJar;
		onlyClses = new HashSet<ClassVO>();
//		onlyMthds = new HashMap<String, List<MethodVO>>();

//		initOnly(clsTb);
		getNodeRiskAnas();
		sum();
	}

	private void sum() {
		rchedMthds = new HashSet<String>();
		rchedServices = new HashSet<String>();
		for (NodeCg nodeRiskAna : nodeRiskAnas) {
			rchedMthds.addAll(nodeRiskAna.getRchedMthds());
			rchedServices.addAll(nodeRiskAna.getRchedServices());
		}
	}

	public void addClass(ClassVO cls) {
		onlyClses.add(cls);
	}

	public List<NodeCg> getNodeRiskAnas() {
		if (nodeRiskAnas == null) {
			nodeRiskAnas = new ArrayList<NodeCg>();
			for (NodeAdapter node : depJar.getNodeAdapters()) {
				nodeRiskAnas.add(node.getNodeRiskAna(this));
			}
		}
		return nodeRiskAnas;
	}

	public String getRiskString() {
		StringBuilder sb = new StringBuilder("risk for jar:");
		sb.append(depJar.toString() + "\n");
		sb.append("reached size: " + rchedMthds.size() + " reached_thrown size:" + getRisk1Mthds().size()
				+ " reached_thrown_service:" + getRisk2Mthds().size() + "\n");
		for (NodeCg nodeRiskAna : getNodeRiskAnas()) {
			sb.append(nodeRiskAna.getRiskString());
			sb.append("\n");
		}
		return sb.toString();
	}

	public Set<String> getRchedMthds() {
		return rchedMthds;
	}

	public Set<String> getRisk1Mthds() {
		if (risk1Mthds == null) {
			risk1Mthds = new HashSet<String>();
		}
		return risk1Mthds;
	}

	public Set<String> getRisk2Mthds() {
		if (risk2Mthds == null) {
			risk2Mthds = new HashSet<String>();
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
//public Set<String> getThrownMthds() {
//Set<String> riskMthds = new HashSet<String>();
//for (ClassVO onlyCls : onlyClses) {// all method in only class is risk
//	for (MethodVO riskMthd : onlyCls.getMthds()) {
//		riskMthds.add(riskMthd.getMthdSig());
//	}
//}
//for (String bothCls : onlyMthds.keySet()) {
//	for (MethodVO riskMthd : onlyMthds.get(bothCls)) {
//		riskMthds.add(riskMthd.getMthdSig());
//	}
//}
//return riskMthds;
//}

//private void initOnly(Map<String, ClassVO> clsTb) {
//for (ClassVO jarCls : depJar.getClsTb().values()) {
//	ClassVO finalCls = clsTb.get(jarCls.getClsSig());
//	if (finalCls == null) {// class only in jar
//		addClass(jarCls);
//	} else {// class both in jar and final
//		for (MethodVO jarMthd : jarCls.getMthds()) {
//			if (!finalCls.hasMethod(jarMthd.getMthdSig()))
//				addMethod(jarMthd);
//		}
//	}
//}
//}

//public void addMethod(MethodVO mthd) {
//String clsSig = mthd.getClassVO().getClsSig();
//List<MethodVO> riskMthds = onlyMthds.get(clsSig);
//if (riskMthds == null) {
//	riskMthds = new ArrayList<MethodVO>();
//	onlyMthds.put(clsSig, riskMthds);
//}
//riskMthds.add(mthd);
//}

// public int getRiskMthdNum() {
// int num = 0;
// for (ClassVO cls : onlyClses) {
// for (MethodVO mthd : cls.getMthds()) {
// Set<String> mthdRiskCalls = riskCalls.getRiskCalls(mthd.getMthdSig());
// if (mthdRiskCalls.size() > 0) {
// num++;
// }
// }
// }
// for (String clsName : onlyMthds.keySet()) {
// for (MethodVO method : onlyMthds.get(clsName)) {
// Set<String> mthdRiskCalls = riskCalls.getRiskCalls(method.getMthdSig());
// if (mthdRiskCalls.size() > 0) {
// num++;
// }
// }
// }
// return num;
// }

// public String getRiskStr() {
// StringBuilder sb = new StringBuilder("risk for ");
// sb.append(depJar.toString() + ":" + getRiskMthdNum() + "\n");
// sb.append("only class in jar:" + "\n");
// for (ClassVO cls : onlyClses) {
// sb.append("-");
// sb.append(cls.getClsSig());
// sb.append("\n");
// for (MethodVO mthd : cls.getMthds()) {
// sb.append(mthdRiskStr(mthd));
// }
// }
// sb.append("only method in jar:" + "\n");
// for (String clsName : onlyMthds.keySet()) {
// sb.append("-");
// sb.append(clsName);
// sb.append("\n");
// for (MethodVO method : onlyMthds.get(clsName)) {
// sb.append(mthdRiskStr(method));
// }
//
// }
// return sb.toString();
// }

// private String mthdRiskStr(MethodVO method) {
// StringBuilder sb = new StringBuilder("");
// sb.append("---");
// sb.append(method.getMthdSig());
// sb.append("\n");
// for (String callMthd : riskCalls.getRiskCalls(method.getMthdSig())) {
// sb.append("---------called by " + callMthd + "\n");
// }
// return sb.toString();
// }

// public String getRiskCallPath() {
// StringBuilder sb = new StringBuilder(depJar.toString() + "\n");
// // for(String ) {
// //
// // }
// return sb.toString();
// }