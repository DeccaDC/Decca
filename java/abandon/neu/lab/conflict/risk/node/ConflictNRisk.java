package abandon.neu.lab.conflict.risk.node;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dom4j.Element;
import org.dom4j.tree.DefaultElement;

import neu.lab.conflict.container.FinalClasses;
import neu.lab.conflict.util.Conf;
import neu.lab.conflict.util.MathUtil;
import neu.lab.conflict.util.MavenUtil;
import neu.lab.conflict.util.SootUtil;
import neu.lab.conflict.vo.ClassVO;
import neu.lab.conflict.vo.DepJar;
import neu.lab.conflict.vo.Conflict;

public class ConflictNRisk {
	private final double T_LOW = 0.98;
	private final double T_HIGH = 1;
	private List<DepJarNRisk> jarRiskAnas;
	private Conflict nodeConflict;

	private Set<String> rchedMthds;// reached method in call-graph computed(entry class is host class)
	private Set<String> rchedMthdNames;

	private Set<String> rchedServices;
	private Set<String> rchedServiceNames;

	private Set<String> risk1Mthds;// reached and thrown
	private Set<String> risk2Mthds;// reached and thrown and called by method in other jar.

//	public static ConflictRiskAna getConflictRiskAna(Conflict nodeConflict) {
//		MavenUtil.i().getLog().info("conflict risk ana for:" + nodeConflict.toString());
//		ConflictRiskAna riskAna = new ConflictRiskAna(nodeConflict);
//		return riskAna;
//	}

	public ConflictNRisk(Conflict nodeConflict) {
		this.nodeConflict = nodeConflict;
		jarRiskAnas = new ArrayList<DepJarNRisk>();
		for (DepJar depJar : nodeConflict.getDepJars()) {
			jarRiskAnas.add(depJar.getJarRiskAna(this));
		}
	}

	public Element getRiskPathEle() {
		Element ele = new DefaultElement("conflictRisk");
		ele.addAttribute("id", nodeConflict.toString());
		ele.addAttribute("reached_size", "" + getRchedMthds().size());
		ele.addAttribute("reached_thrown_size", "" + getRisk1Mthds().size());
		ele.addAttribute("reached_thrown_service", "" + getRisk2Mthds().size());
		for (DepJarNRisk jarRiskAna : getJarRiskAnas()) {
			ele.add(jarRiskAna.getRiskPathEle());
		}
		return ele;
	}

	public Element getRchNumEle() {
		Element conflictEle = new DefaultElement("conflictJar");
		conflictEle.addAttribute("groupId-artifactId", nodeConflict.getGroupId() + ":" + nodeConflict.getArtifactId());
		StringBuilder versions = new StringBuilder();
		for (String version : nodeConflict.getVersions()) {
			versions.append(version);
		}
		conflictEle.addAttribute("versions", versions.toString());
		int riskLevel = getRiskLevel();
		conflictEle.addAttribute("riskLevel", "" + riskLevel);
		Element versionsEle = conflictEle.addElement("versions");
		for (DepJar depJar : nodeConflict.getDepJars()) {
			versionsEle.add(depJar.getRchNumEle());
		}

		Element risksEle = conflictEle.addElement("RiskMethods");
		risksEle.addAttribute("tip", "method that may be used but will not be loaded !");
		if (riskLevel == 3 || riskLevel == 4) {
			for (String rchedMthd : getRchedMthds()) {
				if (!nodeConflict.getUsedDepJar().containsMthd(rchedMthd)) {
					Element riskEle = risksEle.addElement("RiskMthd");
					riskEle.addText(rchedMthd.replace('<', ' ').replace('>', ' '));
				}
			}
		} else {

		}
		return conflictEle;
	}

	public int getRiskLevel() {
		boolean loadSafe = true;
		boolean othersSafe = true;
		double ratio = MathUtil.getQuotient(nodeConflict.getUsedDepJar().getInnerMthds(getRchedMthds()).size(),
				getRchedMthds().size());
		if (getT_LOW() <= ratio && ratio < getT_HIGH()) {
			loadSafe = false;
		}
		for (DepJar depJar : nodeConflict.getDepJars()) {
			if (nodeConflict.getUsedDepJar() != depJar) {
				ratio = MathUtil.getQuotient(depJar.getInnerMthds(getRchedMthds()).size(), getRchedMthds().size());
				if (getT_LOW() <= ratio && ratio < getT_HIGH()) {
					othersSafe = false;
					break;
				}
			}
		}
		if (loadSafe) {
			if (othersSafe)
				return 1;
			else
				return 2;
		} else {
			if (othersSafe)
				return 3;
			else
				return 4;

		}
	}

	public FourRow getFourRow() {
		List<String> mthdRow = getRecord("mthd");
		List<String> mthdNameRow = getRecord("mthdName");
		List<String> serviceRow = getRecord("service");
		List<String> serviceNameRow = getRecord("serviceName");
		// add origin column
		mthdRow.add("" + this.getRchedMthds().size());
		mthdNameRow.add("" + this.getRchedMthdNames().size());
		serviceRow.add("" + this.getRchedServices().size());
		serviceNameRow.add("" + this.getRchedServiceNames().size());
		// add load column
		FourNum loadFourNum = getJarFourNum(nodeConflict.getUsedDepJar());
		mthdRow.add("" + loadFourNum.mthdCnt);
		mthdNameRow.add("" + loadFourNum.mthdNameCnt);
		serviceRow.add("" + loadFourNum.serviceCnt);
		serviceNameRow.add("" + loadFourNum.serviceNameCnt);
		// add other column
		String otherMthd = "";
		String otherMthdName = "";
		String otherService = "";
		String otherServiceName = "";
		for (DepJar depJar : nodeConflict.getDepJars()) {
			if (nodeConflict.getUsedDepJar() != depJar) {
				FourNum fourNum = getJarFourNum(depJar);
				otherMthd = otherMthd + "/" + fourNum.mthdCnt;
				otherMthdName = otherMthdName + "/" + fourNum.mthdNameCnt;
				otherService = otherService + "/" + fourNum.serviceCnt;
				otherServiceName = otherServiceName + "/" + fourNum.serviceNameCnt;
			}
		}
		mthdRow.add(otherMthd);
		mthdNameRow.add(otherMthdName);
		serviceRow.add(otherService);
		serviceNameRow.add(otherServiceName);
		return new FourRow(mthdRow, mthdNameRow, serviceRow, serviceNameRow);
	}

	private FourNum getJarFourNum(DepJar jar) {
		Set<String> jarMthdSigs = jar.getAllMthd();
		Set<String> jarMthdNames = new HashSet<String>();
		for (String methodSig : jarMthdSigs) {
			jarMthdNames.add(SootUtil.mthdSig2name(methodSig));
		}

		FourNum fourNum = new FourNum();
		for (String rchMthd : this.getRchedMthds()) {
			if (jarMthdSigs.contains(rchMthd))
				fourNum.mthdCnt++;
		}
		for (String rchMthdName : this.getRchedMthdNames()) {
			if (jarMthdNames.contains(rchMthdName))
				fourNum.mthdNameCnt++;
		}
		for (String rchService : this.getRchedServices()) {
			if (jarMthdSigs.contains(rchService))
				fourNum.serviceCnt++;
		}
		for (String rchServiceName : this.getRchedServiceNames()) {
			if (jarMthdNames.contains(rchServiceName))
				fourNum.serviceNameCnt++;
		}
		return fourNum;
	}

	private List<String> getRecord(String staType) {
		List<String> record = new ArrayList<String>();
		record.add(MavenUtil.i().getProjectGroupId() + ":" + MavenUtil.i().getProjectArtifactId() + ":"
				+ MavenUtil.i().getProjectVersion());
		record.add(nodeConflict.getGroupId() + ":" + nodeConflict.getArtifactId());
		record.add(staType);
		return record;
	}

	private static Map<String, ClassVO> getClsTb(Conflict nodeConflict) {
		if (Conf.CLASS_DUP)
			return FinalClasses.i().getClsTb();
		else
			return nodeConflict.getUsedDepJar().getClsTb();
	}

	public Set<String> getRchedMthds() {
		if (rchedMthds == null) {
			rchedMthds = new HashSet<String>();
			for (DepJarNRisk jarRiskAna : getJarRiskAnas()) {
				rchedMthds.addAll(jarRiskAna.getRchedMthds());
			}
		}
		return rchedMthds;
	}

	public Set<String> getRchedServices() {
		if (rchedServices == null) {
			rchedServices = new HashSet<String>();
			for (DepJarNRisk jarRiskAna : getJarRiskAnas()) {
				rchedServices.addAll(jarRiskAna.getRchedServices());
			}
		}
		return rchedServices;
	}

	public Set<String> getRisk1Mthds() {
		if (risk1Mthds == null) {
			risk1Mthds = new HashSet<String>();
			for (DepJarNRisk jarAna : getJarRiskAnas()) {
				risk1Mthds.addAll(jarAna.getRisk1Mthds());
			}
		}
		return risk1Mthds;
	}

	public Set<String> getRisk2Mthds() {
		if (risk2Mthds == null) {
			risk2Mthds = new HashSet<String>();
			for (DepJarNRisk jarAna : getJarRiskAnas()) {
				risk2Mthds.addAll(jarAna.getRisk2Mthds());
			}
		}
		return risk2Mthds;
	}

	public Set<String> getRchedMthdNames() {
		if (null == rchedMthdNames) {
			rchedMthdNames = new HashSet<String>();
			for (String methodSig : getRchedMthds()) {
				rchedMthdNames.add(SootUtil.mthdSig2name(methodSig));
			}
		}
		return rchedMthdNames;
	}

	public Set<String> getRchedServiceNames() {
		if (null == rchedServiceNames) {
			rchedServiceNames = new HashSet<String>();
			for (String methodSig : getRchedServices()) {
				rchedServiceNames.add(SootUtil.mthdSig2name(methodSig));
			}
		}
		return rchedServiceNames;
	}

	private boolean useOldAl() {
		return MavenUtil.i().getProjectGroupId().equals("org.apache.metamodel")
				&& MavenUtil.i().getProjectArtifactId().equals("MetaModel-elasticsearch-rest");
	}

	private double getT_HIGH() {
		return T_HIGH;
	}

	private double getT_LOW() {
		if (useOldAl()) {
			return 0;
		} else {
			return T_LOW;
		}
	}

	public DepJar getUsedDepJar() {
		return nodeConflict.getUsedDepJar();
	}

	public List<DepJarNRisk> getJarRiskAnas() {
		return jarRiskAnas;
	}

	private void setJarRiskAnas(List<DepJarNRisk> jarRiskAnas) {
		this.jarRiskAnas = jarRiskAnas;
	}

}
