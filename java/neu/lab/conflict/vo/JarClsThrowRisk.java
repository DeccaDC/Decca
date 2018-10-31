package neu.lab.conflict.vo;

import java.util.List;

/**
 * risk caused by class omitted
 * 
 * @author asus
 *
 */
public class JarClsThrowRisk {
	private DepJar depJar;
	private List<ClassVO> throwCls;
	private MethodCalls riskCalls;

	public JarClsThrowRisk(DepJar depJar) {
		this.depJar = depJar;
	}
	
	public void addThrowCls() {
		
	}

	public DepJar getDepJar() {
		return depJar;
	}

	public List<ClassVO> getThrowCls() {
		return throwCls;
	}

	public MethodCalls getRiskCalls() {
		return riskCalls;
	}
	
}
