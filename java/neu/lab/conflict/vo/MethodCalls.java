package neu.lab.conflict.vo;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MethodCalls {
	
	private Set<MethodCall> container;

	public MethodCalls() {
		container = new HashSet<MethodCall>();
	}
	public void addRelation(MethodCall rlt) {
		container.add(rlt);
	}
	public void addAllRlt(List<MethodCall> rlts) {
		container.addAll(rlts);
	}
	public Set<String> getRiskCalls(String mthdSig){
		Set<String> riskCalls = new HashSet<String>();
		for(MethodCall rlt:container) {
			if(rlt.getTgt().equals(mthdSig)) {
				riskCalls.add(rlt.getSrc());
			}
		}
		return riskCalls;
	}
	public int size() {
		return container.size();
	}
}
