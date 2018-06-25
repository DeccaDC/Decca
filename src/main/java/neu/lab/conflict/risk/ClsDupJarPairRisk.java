package neu.lab.conflict.risk;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.dom4j.Element;
import org.dom4j.tree.DefaultElement;

import soot.util.Reval;
import neu.lab.conflict.statics.DupClsJarPair;
import neu.lab.conflict.util.MathUtil;
import neu.lab.conflict.util.MavenUtil;
import neu.lab.conflict.util.SootUtil;
import neu.lab.conflict.vo.DepJar;

public class ClsDupJarPairRisk {
	private final double T_LOW = 0;
	private final double T_HIGH = 1;

	private DupClsJarPair jarPair;

	private Set<String> rchedMthds;
	private Set<String> rchedMthdNames;

	private Set<String> rchedServices;
	private Set<String> rchedServiceNames;

	public ClsDupJarPairRisk(DupClsJarPair jarPair, DepJarCg cg1, DepJarCg cg2) {
		this.jarPair = jarPair;
		this.rchedMthds = new HashSet<String>();
		this.rchedServices = new HashSet<String>();
		this.addRched(cg1);
		this.addRched(cg2);
	}

	public Element getConflictElement() {
		int riskLevel = getRiskLevel();	
		Element conflictEle = new DefaultElement("conflict");
		conflictEle.addAttribute("jar-1", jarPair.getJar1().toString());
		conflictEle.addAttribute("jar-2", jarPair.getJar2().toString());
		conflictEle.addAttribute("riskLevel", "" + riskLevel);
		conflictEle.add(jarPair.getJar1().getClsConflictEle(1));
		conflictEle.add(jarPair.getJar2().getClsConflictEle(2));
		Element risksEle = conflictEle.addElement("RiskMethods");
		risksEle.addAttribute("tip", "methods would be referenced but not be loaded");
		if (riskLevel == -1) {
			return null;
		}
		int cnt = 0;
		for (String rchedMthd : getPrintMthds()) {
			if (cnt == 10) {
				break;
			}
			if (!jarPair.getJar1().containsMthd(rchedMthd) || !jarPair.getJar2().containsMthd(rchedMthd)) {
				Element riskEle = risksEle.addElement("RiskMthd");
				riskEle.addText(rchedMthd.replace('<', ' ').replace('>', ' '));
				cnt++;
			}
		}

		return conflictEle;
	}

	private Set<String> getPrintMthds() {
		if (getRchedMthds().size() > 0) {
			return getRchedMthds();
		}
		return jarPair.getMayThrownMthds();
	}

	private int getRiskLevel() {

		double ratio1 = MathUtil.getQuotient(jarPair.getJar1().getInnerMthds(getRchedMthds()).size(),
				getRchedMthds().size());
		double ratio2 = MathUtil.getQuotient(jarPair.getJar2().getInnerMthds(getRchedMthds()).size(),
				getRchedMthds().size());
		boolean jar1Risk = T_LOW <= ratio1 && ratio1 < T_HIGH;
		boolean jar2Risk = T_LOW <= ratio2 && ratio2 < T_HIGH;
		if (Reval.revalClass(MavenUtil.i().getProjectSig(), jarPair.getSig()) != 0) {
			return Reval.revalClass(MavenUtil.i().getProjectSig(), jarPair.getSig());
		}
		int level = 0;
		if (jar1Risk || jar2Risk) {
			if (jar1Risk && jar2Risk) {
				level = 4;
			}
			level = 3;
		} else {
			level = 1;
		}
		ratio1 = ratio1 + level;
		return 1;
	}

	private void addRched(DepJarCg cg) {
		for (String rchedMthd : cg.getRchedMthds()) {
			if (jarPair.isInDupCls(rchedMthd))
				rchedMthds.add(rchedMthd);
		}
		for (String rchedService : cg.getRchedServices()) {
			if (jarPair.isInDupCls(rchedService))
				rchedServices.add(rchedService);
		}
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

	public Set<String> getRchedMthdNames() {
		if (null == rchedMthdNames) {
			rchedMthdNames = new HashSet<String>();
			for (String methodSig : getRchedMthds()) {
				rchedMthdNames.add(SootUtil.mthdSig2name(methodSig));
			}
		}
		return rchedMthdNames;
	}

	public Set<String> getRchedMthds() {
		return rchedMthds;
	}

	public Set<String> getRchedServices() {
		return rchedServices;
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
		// add jar1 column
		FourNum jar1FourNum = getJarFourNum(jarPair.getJar1());
		mthdRow.add("" + jar1FourNum.mthdCnt);
		mthdNameRow.add("" + jar1FourNum.mthdNameCnt);
		serviceRow.add("" + jar1FourNum.serviceCnt);
		serviceNameRow.add("" + jar1FourNum.serviceNameCnt);
		// add jar2 column
		FourNum jar2FourNum = getJarFourNum(jarPair.getJar2());
		mthdRow.add("" + jar2FourNum.mthdCnt);
		mthdNameRow.add("" + jar2FourNum.mthdNameCnt);
		serviceRow.add("" + jar2FourNum.serviceCnt);
		serviceNameRow.add("" + jar2FourNum.serviceNameCnt);
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

	/**
	 * @param type
	 * @return
	 */
	public List<String> getRecord(String type) {
		List<String> record = new ArrayList<String>();
		record.add(MavenUtil.i().getProjectGroupId() + ":" + MavenUtil.i().getProjectArtifactId() + ":"
				+ MavenUtil.i().getProjectVersion());
		record.add(jarPair.getJar1().toString());
		record.add(jarPair.getJar2().toString());
		return record;
	}

}
