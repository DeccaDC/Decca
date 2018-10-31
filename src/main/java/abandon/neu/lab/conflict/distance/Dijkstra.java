package abandon.neu.lab.conflict.distance;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import abandon.neu.lab.conflict.graph.path1.Graph4MthdPath;
import abandon.neu.lab.conflict.graph.path1.Node4MthdPath1;
import neu.lab.conflict.graph.IGraph;

public abstract class Dijkstra {

	protected Map<String, DijkstraNode> name2node;

	public Dijkstra() {
		name2node = new HashMap<String, DijkstraNode>();
	}

	public Dijkstra(IGraph graph) {
		name2node = new HashMap<String, DijkstraNode>();
		for (String node : graph.getAllNode()) {
			// neu.lab.conflict.util.MavenUtil.i().getLog().info(node);
			name2node.put(node, new DijkstraNode(graph.getNode(node)));
		}
	}

	public void addNode(DijkstraNode node) {
		name2node.put(node.getName(), node);
	}

	public Map<String, Map<String, Double>> getDistanceTb(Collection<String> startNds) {
		Map<String, Map<String, Double>> distances = new HashMap<String, Map<String, Double>>();
		for (String startNd : startNds) {
			distances.put(startNd, getDistanceTb(startNd));
		}
		
//		Map<String, Double> t2d = distances.get("org.apache.xerces.xinclude.XIncludeTextReader");
//		if(t2d!=null) {
//			System.out.println("distance:"+t2d.get("org.apache.xerces.xinclude.XIncludeTextReader"));
//		}
		return distances;
	}

	public abstract Map<String, Double> getDistanceTb(String startNd);


	protected Set<String> getAllNd() {
		return name2node.keySet();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("graph:" + System.lineSeparator());
		for (DijkstraNode node : name2node.values()) {
			sb.append(node.toString());
			sb.append(System.lineSeparator());
		}
		return sb.toString();
	}

}
