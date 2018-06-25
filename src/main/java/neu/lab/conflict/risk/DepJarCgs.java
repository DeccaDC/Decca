package neu.lab.conflict.risk;

import java.util.HashMap;
import java.util.Map;

import neu.lab.conflict.vo.DepJar;

public class DepJarCgs {
	private Map<DepJar, DepJarCg> jar2cg;
	public DepJarCgs() {
		jar2cg = new HashMap<DepJar, DepJarCg>();
	}
	public DepJarCg getDepJarCg(DepJar depJar) {
		DepJarCg cg =  jar2cg.get(depJar);
		if(null==cg) {
			cg = new DepJarCg(depJar);
			jar2cg.put(depJar, cg);
		}
		return cg;
	}
}
