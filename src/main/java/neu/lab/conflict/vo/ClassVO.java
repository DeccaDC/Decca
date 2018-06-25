package neu.lab.conflict.vo;

import java.util.HashSet;
import java.util.Set;

public class ClassVO {
	private String clsSig;
	private Set<MethodVO> mthds;// methods in class
	private DepJar depJar;

	public ClassVO(String clsSig) {
		this.clsSig = clsSig;
		mthds = new HashSet<MethodVO>();
	}

	public DepJar getDepJar() {
		return depJar;
	}

	public void setDepJar(DepJar depJar) {
		this.depJar = depJar;
	}

	public boolean addMethod(MethodVO mthd) {
		return mthds.add(mthd);
	}

	/**
	 * if contains method called mthdSig(may not same method object)
	 * 
	 * @param mthdSig
	 * @return
	 */
	public boolean hasMethod(String mthdSig2) {
		for (MethodVO mthd : mthds) {
			if (mthd.isSameName(mthdSig2))
				return true;
		}
		return false;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ClassVO) {
			ClassVO classVO = (ClassVO) obj;
			return clsSig.equals(classVO.getClsSig());
		} else {
			return false;
		}

	}

	@Override
	public int hashCode() {
		return this.clsSig.hashCode();
	}

	public String getClsSig() {
		return clsSig;
	}

	public void setClsSig(String clsSig) {
		this.clsSig = clsSig;
	}

	public Set<MethodVO> getMthds() {
		return mthds;
	}

	public void setMthds(Set<MethodVO> mthds) {
		this.mthds = mthds;
	}

}
