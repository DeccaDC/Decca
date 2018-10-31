package neu.lab.conflict.vo;

import java.util.HashSet;
import java.util.Set;


public class MethodVO {
	private String mthdSig;
	private Set<String> inMthds;
	private ClassVO cls;

	public MethodVO(String mthdSig,ClassVO cls) {
		this.mthdSig = mthdSig;
		inMthds = new HashSet<String>();
		this.cls = cls;
	}
	public ClassVO getClassVO() {
		return cls;
	}
	public String getMthdSig() {
		return mthdSig;
	}

	public void setMthdSig(String mthdSig) {
		this.mthdSig = mthdSig;
	}
	public void addInMthds(String mthdSig) {
		if (null == this.inMthds)
			inMthds = new HashSet<String>();
		inMthds.add(mthdSig);
	}
	public Set<String> getInMthds() {
		return inMthds;
	}

	public void setInMthds(Set<String> inMthds) {
		this.inMthds = inMthds;
	}
	public boolean isSameName(String mthdSig2) {
		return mthdSig.equals(mthdSig2);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof MethodVO) {
			MethodVO method = (MethodVO) obj;
			return mthdSig.equals(method.getMthdSig());
		} else {
			return false;
		}

	}

	@Override
	public int hashCode() {
		return this.mthdSig.hashCode();
	}
	
}
