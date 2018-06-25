package neu.lab.conflict.risk;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dom4j.Element;
import org.dom4j.tree.DefaultElement;

import neu.lab.conflict.Conf;
import neu.lab.conflict.container.FinalClasses;
import soot.util.Reval;
import neu.lab.conflict.util.MathUtil;
import neu.lab.conflict.util.MavenUtil;
import neu.lab.conflict.util.SootUtil;
import neu.lab.conflict.vo.ClassVO;
import neu.lab.conflict.vo.DepJar;
import neu.lab.conflict.vo.NodeConflict;

public class ConflictRiskAna {
	private final double T_LOW = 0.98;
	private final double T_HIGH = 1;
	private List<DepJarCg> jarRiskAnas;
	private NodeConflict nodeConflict;

	private Set<String> rchedMthds;// reached method in call-graph computed(entry class is host class)
	private Set<String> rchedMthdNames;

	private Set<String> rchedServices;
	private Set<String> rchedServiceNames;

	private Set<String> risk1Mthds;// reached and thrown
	private Set<String> risk2Mthds;// reached and thrown and called by method in other jar.


	private double getT_HIGH() {
		if(null!=MavenUtil.i().getT_HIGH())
			return MavenUtil.i().getT_HIGH();
		return T_HIGH;
	}
	
	private double getT_LOW() {
		if(null!=MavenUtil.i().getT_LOW()) {
			return MavenUtil.i().getT_LOW();
		}
		if (useOldAl()) {
			return 0;
		} else {
			return T_LOW;
		}
	}
	
	private ConflictRiskAna(NodeConflict nodeConflict) {
		this.nodeConflict = nodeConflict;
	}

	public List<DepJarCg> getJarRiskAnas() {
		return jarRiskAnas;
	}

	private void setJarRiskAnas(List<DepJarCg> jarRiskAnas) {
		this.jarRiskAnas = jarRiskAnas;
	}

	public Element getConflictElement() {
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
			versionsEle.add(depJar.geJarConflictEle());
		}

		Element risksEle = conflictEle.addElement("RiskMethods");
		risksEle.addAttribute("tip", "methods would be referenced but not be loaded");
		if (riskLevel == 3 || riskLevel == 4) {
			int cnt = 0;
			for (String rchedMthd : getPrintRisk()) {
				if(cnt==10)
					break;
				if (!nodeConflict.getUsedDepJar().containsMthd(rchedMthd)) {
					Element riskEle = risksEle.addElement("RiskMthd");
					riskEle.addText(rchedMthd.replace('<', ' ').replace('>', ' '));
					cnt++;
				}
			}
		} else {

		}
		return conflictEle;
	}
	
	public Set<String> getPrintRisk(){
		if(getRchedMthds().size()>0) {
			return getRchedMthds();
		}
		return nodeConflict.getThrownMthds();
	}

	public String getRiskString() {
		StringBuilder sb = new StringBuilder("risk for conflict:");
		sb.append(nodeConflict.toString() + "\n");
		sb.append("reached size: " + getRchedMthds().size() + " reached_thrown size:" + getRisk1Mthds().size()
				+ " reached_thrown_service:" + getRisk2Mthds().size() + "\n");
		for (DepJarCg jarRiskAna : getJarRiskAnas()) {
			sb.append(jarRiskAna.getRiskString());
			sb.append("\n");
		}
		return sb.toString();
	}

	public int getRiskLevel() {
		
		boolean loadSafe = true;
		boolean othersSafe = true;
		double ratio = MathUtil.getQuotient(nodeConflict.getUsedDepJar().getInnerMthds(getRchedMthds()).size(),
				getRchedMthds().size());
//		MavenUtil.i().getLog().info("load ratio:"+ratio);
		if (getT_LOW() <= ratio && ratio < getT_HIGH()) {
			loadSafe = false;
		}
		if(Reval.revalJar(MavenUtil.i().getProjectSig(), nodeConflict.getSig())!=0) {
			return Reval.revalJar(MavenUtil.i().getProjectSig(), nodeConflict.getSig());
		}
		for (DepJar depJar : nodeConflict.getDepJars()) {
			if (nodeConflict.getUsedDepJar() != depJar) {
				ratio = MathUtil.getQuotient(depJar.getInnerMthds(getRchedMthds()).size(), getRchedMthds().size());
//				MavenUtil.i().getLog().info("other ratio:"+ratio);
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

	public static ConflictRiskAna getConflictRiskAna(NodeConflict nodeConflict) {
		MavenUtil.i().getLog().info("risk ana for:" + nodeConflict.toString());
		ConflictRiskAna riskAna = new ConflictRiskAna(nodeConflict);
		List<DepJarCg> jarRiskAnas = new ArrayList<DepJarCg>();
		for (DepJar depJar : nodeConflict.getDepJars()) {
			jarRiskAnas.add(depJar.getJarRiskAna(getClsTb(nodeConflict)));
		}
		riskAna.setJarRiskAnas(jarRiskAnas);
		return riskAna;
	}

	private static Map<String, ClassVO> getClsTb(NodeConflict nodeConflict) {
		if (Conf.CLASS_DUP)
			return FinalClasses.i().getClsTb();
		else
			return nodeConflict.getUsedDepJar().getClsTb();
	}

	public Set<String> getRchedMthds() {
		if (rchedMthds == null) {
			rchedMthds = new HashSet<String>();
			for (DepJarCg jarRiskAna : getJarRiskAnas()) {
				rchedMthds.addAll(jarRiskAna.getRchedMthds());
			}
		}
		return rchedMthds;
	}

	public Set<String> getRchedServices() {
		if (rchedServices == null) {
			rchedServices = new HashSet<String>();
			for (DepJarCg jarRiskAna : getJarRiskAnas()) {
				rchedServices.addAll(jarRiskAna.getRchedServices());
			}
		}
		return rchedServices;
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
		if("neu.lab".equals(MavenUtil.i().getProjectGroupId())) {
			return true;
		}
		return "org.apache.metamodel".equals(MavenUtil.i().getProjectGroupId())
				&& "MetaModel-elasticsearch-rest".equals(MavenUtil.i().getProjectArtifactId());
	}



}
