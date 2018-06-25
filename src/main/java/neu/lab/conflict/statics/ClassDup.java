package neu.lab.conflict.statics;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import neu.lab.conflict.vo.ClassVO;
import neu.lab.conflict.vo.DepJar;
import neu.lab.conflict.vo.MethodVO;

public class ClassDup {
	private String clsSig;
	private List<DepJar> depJars;

	public List<DepJar> getDepJars() {
		return depJars;
	}

	public ClassDup(String clsSig) {
		this.clsSig = clsSig;
		depJars = new ArrayList<DepJar>();
	}

	public boolean isSelf(String otherSig) {
		return clsSig.equals(otherSig);
	}

	public String getClsSig() {
		return clsSig;
	}

	public String getRiskString() {
		StringBuilder sb = new StringBuilder("risk for class:" + clsSig);
		for (String mthd : getAllMthd()) {
			String mthdStr = getMthdStr(mthd);
			if (null != mthdStr)
				sb.append("\n m:" + mthdStr + " ");
		}
		return sb.toString();
	}

	/**
	 * @return union of class method (class in jar1,class in jar2)
	 */
	private Set<String> getAllMthd() {
		Set<String> allMthds = new HashSet<String>();
		for (DepJar depJar : depJars) {
			ClassVO clsVO = depJar.getClassVO(clsSig);
			if (clsVO != null) {
				for (MethodVO mthd : clsVO.getMthds()) {
					allMthds.add(mthd.getMthdSig());
				}
			}
		}
		return allMthds;
	}

	private String getMthdStr(String mthdSig) {
		List<DepJar> yesJars = new ArrayList<DepJar>();
		List<DepJar> noJars = new ArrayList<DepJar>();
		for (DepJar depJar : depJars) {
			ClassVO clsVO = depJar.getClassVO(clsSig);
			if(clsVO != null) {
				if (clsVO.hasMethod(mthdSig))
					yesJars.add(depJar);
				else
					noJars.add(depJar);
			}
		}
		if (noJars.size() == 0)// both have
			return null;
		else {
			StringBuilder sb = new StringBuilder("risk for method" + mthdSig);
			sb.append("\nhave jar:");
			for (DepJar yesJar : yesJars) {
				sb.append("\n" + yesJar.getValidDepPath());
			}
			sb.append("\nhaven't jar:");
			for (DepJar noJar : noJars) {
				sb.append("\n" + noJar.getValidDepPath());
			}
			return sb.toString();
		}

	}

	// private boolean bothHas(String mthdSig) {
	// boolean bothHas = true;
	// for (DepJar depJar : depJars) {
	// ClassVO clsVO = depJar.getClsTb().get(clsSig);
	// if (!clsVO.hasMethod(mthdSig))
	// bothHas = false;
	// }
	// return bothHas;
	// }

	public void addDepJar(DepJar depJar) {
		// boolean isNew = true;
		// for(DepJar oldDep:depJars) {
		// if(oldDep.isSameLib(depJar))
		// isNew = false;
		// }
		// if (isNew)
		depJars.add(depJar);
	}

	public boolean isDup() {
		return depJars.size() > 1;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(clsSig + " exist in :");
		for (DepJar depJar : depJars) {
			sb.append(" (" + depJar.toString() + ")");
		}
		return sb.toString();
	}
}
