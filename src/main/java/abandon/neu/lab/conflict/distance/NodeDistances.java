package abandon.neu.lab.conflict.distance;

import java.util.HashMap;
import java.util.Map;

import neu.lab.conflict.util.MavenUtil;

public abstract class NodeDistances {

	protected Map<String, Map<String, Double>> b2t2d;// <bottom,<top,distance>>		<底,<顶,距离>>

	//构造函数
	public NodeDistances() {
		b2t2d = new HashMap<String, Map<String, Double>>();
	}

	// public static NodeDistances i() {
	// if (instance == null) {
	// instance = new NodeDistances();
	// }
	// return instance;
	// }
	
	//是否为空
	public boolean isEmpty() {
		return b2t2d.isEmpty();
	}

	/**
	 * 增加距离
	 * @param bottom 底
	 * @param top 顶
	 * @param newDis 新距离
	 */
	public void addDistance(String bottom, String top, Double newDis) {
		Map<String, Double> t2d = b2t2d.get(bottom);
		if (t2d == null) {
			t2d = new HashMap<String, Double>();
			b2t2d.put(bottom, t2d);
		}
		Double oldDis = t2d.get(top);
		if (oldDis == null) {
			t2d.put(top, newDis);
		} else {// put min
			if (newDis < oldDis)
				t2d.put(top, newDis);
		}
	}

	public void addDistances(Map<String, Map<String, Double>> newData) {
		for (String bottom : newData.keySet()) {
			if (b2t2d.containsKey(bottom)) {// has this bottom.
				Map<String, Double> oldT2d = b2t2d.get(bottom);
				Map<String, Double> newT2d = newData.get(bottom);
				for (String top : newT2d.keySet()) {
					if (oldT2d.containsKey(top)) {// has this top
						if (newT2d.get(top) < oldT2d.get(top)) {// put the min.
							oldT2d.put(top, newT2d.get(top));
						}
					} else {// new target
						oldT2d.put(top, newT2d.get(top));
					}
				}
			} else {// new source
				b2t2d.put(bottom, newData.get(bottom));
			}
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (String source : b2t2d.keySet()) {
			Map<String, Double> dises = b2t2d.get(source);
			for (String target : dises.keySet()) {
				sb.append(source + "," + target + "," + dises.get(target) + "," + isHostNode(target));
				sb.append(System.lineSeparator());
			}
		}
		return sb.toString();
	}

	public abstract boolean isHostNode(String nodeName);
}
