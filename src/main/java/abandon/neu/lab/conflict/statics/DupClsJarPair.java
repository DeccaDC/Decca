package abandon.neu.lab.conflict.statics;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import abandon.neu.lab.conflict.risk.node.ClsDupJarPairRisk;
import abandon.neu.lab.conflict.risk.node.DepJarNRisks;
import neu.lab.conflict.util.SootUtil;
import neu.lab.conflict.vo.ClassVO;
import neu.lab.conflict.vo.DepJar;
import neu.lab.conflict.vo.MethodVO;

/**
 * two jar that have different name and same class.
 * 
 * @author asus
 *
 */
public class DupClsJarPair {
	private DepJar jar1;
	private DepJar jar2;
	private Set<String> clsSigs;

	public DupClsJarPair(DepJar jarA, DepJar jarB) {
		jar1 = jarA;
		jar2 = jarB;
		clsSigs = new HashSet<String>();
	}

	public boolean isInDupCls(String rhcedMthd) {
		return clsSigs.contains(SootUtil.mthdSig2cls(rhcedMthd));
	}

	public void addClass(String clsSig) {
		clsSigs.add(clsSig);
	}

	public boolean isSelf(DepJar jarA, DepJar jarB) {
		return (jar1.equals(jarA) && jar2.equals(jarB)) || (jar1.equals(jarB) && jar2.equals(jarA));
	}

	public DepJar getJar1() {
		return jar1;
	}

	public DepJar getJar2() {
		return jar2;
	}

	public String getRiskString() {
		StringBuilder sb = new StringBuilder("classConflict:");
		sb.append("<" + jar1.toString() + ">");
		sb.append("<" + jar2.toString() + ">\n");
		sb.append(getJarString(jar1, jar2));
		sb.append(getJarString(jar2, jar1));
		return sb.toString();
	}

	private String getJarString(DepJar total, DepJar some) {
		StringBuilder sb = new StringBuilder();
		List<String> onlyMthds = getOnlyMethod(total, some);
		sb.append("   methods that only exist in " + total.getValidDepPath() + "\n");
		if (onlyMthds.size() > 0) {
			for (String onlyMthd : onlyMthds) {
				sb.append(onlyMthd + "\n");
			}
		}
		return sb.toString();
	}

	private List<String> getOnlyMethod(DepJar total, DepJar some) {
		List<String> onlyMthds = new ArrayList<String>();
		for (String clsSig : clsSigs) {
			ClassVO classVO = total.getClassVO(clsSig);
			if (classVO != null) {
				for (MethodVO mthd : classVO.getMthds()) {
					if (!some.getClassVO(clsSig).hasMethod(mthd.getMthdSig()))
						onlyMthds.add(mthd.getMthdSig());
				}
			}
		}
		return onlyMthds;
	}

	public ClsDupJarPairRisk getPairRisk(DepJarNRisks jarCgs) {
		return new ClsDupJarPairRisk(this, jarCgs.getDepJarCg(getJar1()), jarCgs.getDepJarCg(getJar2()));
	}

	// @Override
	// public int hashCode() {
	// return jar1.hashCode() + jar2.hashCode();
	// }
	//
	// @Override
	// public boolean equals(Object obj) {
	// if (obj instanceof JarCmp) {
	// JarCmp other = (JarCmp) obj;
	// return (jar1.equals(other.getJar1()) && jar2.equals(other.getJar2()))
	// || (jar1.equals(other.getJar2()) && jar2.equals(other.getJar1()));
	// }
	// return false;
	// }
}
