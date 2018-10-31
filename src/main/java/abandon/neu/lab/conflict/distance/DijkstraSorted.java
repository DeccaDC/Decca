package abandon.neu.lab.conflict.distance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import neu.lab.conflict.graph.IGraph;

public class DijkstraSorted extends Dijkstra {
	public DijkstraSorted(IGraph graph) {
		super(graph);
	}
	public Map<String, Double> getDistanceTb(String startNd) {
		TreeSet<NameAndDist> doingNds = initDoingDistes(startNd);// doing-distances;
		Map<String, Double> doneNds = new HashMap<String, Double>();
		while (!doingNds.isEmpty()) {
			NameAndDist min = doingNds.pollFirst();
			if (min.distance.equals(Double.MAX_VALUE)) {// left-node is all unreachable.
				break;
			} else {
				doneNds.put(min.name, min.distance);// move from doing to done.
				// update doing.
				updateDoingDistes(min.name, doingNds, doneNds);
			}
		}
		return doneNds;
	}

	private void updateDoingDistes(String doneNdName, TreeSet<NameAndDist> doingNds, Map<String, Double> doneNds) {
		DijkstraNode doneNd = name2node.get(doneNdName);
		Set<String> neighbors = doneNd.getNeighbors();
		List<NameAndDist> newDistes = new ArrayList<NameAndDist>();
		Iterator<NameAndDist> ite = doingNds.iterator();
		while (ite.hasNext()) {
			NameAndDist doingNd = ite.next();
			if (neighbors.contains(doingNd.name)) {// the neighbor of doneNode.
				Double newDist = doneNds.get(doneNdName) + doneNd.getDistance(doingNd.name);
				if (newDist < doingNd.distance) {// should update.
					ite.remove();
					newDistes.add(new NameAndDist(doingNd.name, newDist));
				}
			}
		}
		doingNds.addAll(newDistes);
	}

	private TreeSet<NameAndDist> initDoingDistes(String startName) {
		TreeSet<NameAndDist> doingDistes = new TreeSet<NameAndDist>();// doing-distances;
		DijkstraNode startNd = name2node.get(startName);
		for (String nd : getAllNd()) {
			doingDistes.add(new NameAndDist(nd, startNd.getDistance(nd)));
		}
		doingDistes.add(new NameAndDist(startName, new Double(0)));
		return doingDistes;
	}
}
