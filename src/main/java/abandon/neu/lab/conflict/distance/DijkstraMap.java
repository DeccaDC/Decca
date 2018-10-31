package abandon.neu.lab.conflict.distance;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import abandon.neu.lab.conflict.graph.path1.Graph4MthdPath;
import neu.lab.conflict.graph.IGraph;

import java.util.Set;

public class DijkstraMap extends Dijkstra{
	public DijkstraMap() {
		
	}
	public DijkstraMap(IGraph graph) {
		super(graph);
	}
	public Map<String, Double> getDistanceTb(String startNd) {
		Map<String, Double> doingNds = initDoingDistes(startNd);// doing-distances;
		Map<String, Double> doneNds = new HashMap<String, Double>();
		while (!doingNds.isEmpty()) {
			Entry<String, Double> min = pollMinEntry(doingNds);
			if (min.getValue().equals(Double.MAX_VALUE)) {// left-node is all unreachable.
				break;
			} else {
				doneNds.put(min.getKey(), min.getValue());// move from doing to done.
				// update doing.
				updateDoingDistes(min.getKey(), doingNds, doneNds);
			}
		}
		return doneNds;
	}

	private Entry<String, Double> pollMinEntry(Map<String, Double> doingNds) {
		Double min = Double.MAX_VALUE;
		for (String doingNd : doingNds.keySet()) {
			Double dist = doingNds.get(doingNd);
			if (dist < min) {
				min = dist;
			}
		}
		Entry<String, Double> result = null;
		Iterator<Entry<String, Double>> ite = doingNds.entrySet().iterator();
		while (ite.hasNext()) {
			Entry<String, Double> entry = ite.next();
			if (entry.getValue().equals(min)) {
				result = entry;
				ite.remove();
				break;
			}

		}
		return result;
	}

	private void updateDoingDistes(String doneNdName, Map<String, Double> doingNds, Map<String, Double> doneNds) {
		DijkstraNode doneNd = name2node.get(doneNdName);
		Set<String> neighbors = doneNd.getNeighbors();
		for (String neighbor : neighbors) {
			Double oldDist = doingNds.get(neighbor);
			if (oldDist != null) {
				Double newDist = doneNds.get(doneNdName) + doneNd.getDistance(neighbor);
				if (newDist < oldDist) {
					doingNds.put(neighbor, newDist);
				}
			}
		}
	}

	private Map<String, Double> initDoingDistes(String startName) {
		Map<String, Double> doingDistes = new HashMap<String, Double>();// doing-distances;
		DijkstraNode startNd = name2node.get(startName);
		for (String nd : getAllNd()) {
			doingDistes.put(nd, startNd.getDistance(nd));
		}
		doingDistes.put(startName, new Double(0));
		return doingDistes;
	}
}
