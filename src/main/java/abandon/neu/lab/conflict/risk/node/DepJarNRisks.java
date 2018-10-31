package abandon.neu.lab.conflict.risk.node;

import java.util.HashMap;
import java.util.Map;

import neu.lab.conflict.vo.DepJar;

public class DepJarNRisks {
	private Map<DepJar, DepJarNRisk> jar2cg;
	public DepJarNRisks() {
		jar2cg = new HashMap<DepJar, DepJarNRisk>();
	}
	public DepJarNRisk getDepJarCg(DepJar depJar) {
		DepJarNRisk riskAna =  jar2cg.get(depJar);
		if(null==riskAna) {
			riskAna = new DepJarNRisk(depJar,null);
			jar2cg.put(depJar, riskAna);
		}
		return riskAna;
	}
}
