package abandon.neu.lab.conflict.distance;

import java.util.HashMap;
import java.util.Map;

import neu.lab.conflict.util.MavenUtil;
import neu.lab.conflict.util.SootUtil;

public class MethodProbDistances extends NodeDistances {

	protected Map<String, Map<String, Double>> b2t2prob;// <bottom,<top,probability>>	<底,<顶,可能性>>

	public MethodProbDistances() {
		b2t2prob =  new HashMap<String, Map<String, Double>>();
	}
	
	public void addProb(String bottom, String top, double newLen) {
		Map<String, Double> t2prob = b2t2prob.get(bottom);
		if (t2prob == null) {
			t2prob = new HashMap<String, Double>();
			b2t2prob.put(bottom, t2prob);
		}
		Double oldProb = t2prob.get(top);
		if (oldProb == null) {
			t2prob.put(top, newLen);
		} else {// put min
			if (newLen < oldProb)
				t2prob.put(top, newLen);
		}
	}

	/**
	 * 是不是根节点
	 */
	@Override
	public boolean isHostNode(String nodeName) {
		return MavenUtil.i().isHostClass(SootUtil.mthdSig2cls(nodeName));
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (String source : b2t2d.keySet()) {
			Map<String, Double> t2dis = b2t2d.get(source);
			Map<String, Double> t2prob = b2t2prob.get(source);
			for (String target : t2prob.keySet()) {
				sb.append(source + "," + target + "," +t2dis.get(target)  + "," + isHostNode(target) + ","
						+t2prob.get(target) );
				sb.append(System.lineSeparator());
			}
		}
		return sb.toString();
	}
	
	public String toStringTest() {
		StringBuilder sb = new StringBuilder();
		for (String source : b2t2prob.keySet()) {
			Map<String, Double> t2prob = b2t2prob.get(source);
			for (String target : t2prob.keySet()) {
				sb.append(source + "," + target + "," + "," + isHostNode(target) + ","
						+t2prob.get(target) );
				sb.append(System.lineSeparator());
			}
		}
		return sb.toString();
	
	}
}
