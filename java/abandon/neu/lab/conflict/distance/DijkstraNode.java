package abandon.neu.lab.conflict.distance;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import abandon.neu.lab.conflict.graph.path1.Node4MthdPath1;
import neu.lab.conflict.graph.INode;

public class DijkstraNode {
	
	private String name;
	private Map<String,Double> distances;//key is all in-method.All value is 1.
	
	public DijkstraNode(String name) {
		this.name = name;
		distances = new HashMap<String,Double>();
	}
	
	public String getName() {
		return name;
	}

	public DijkstraNode(INode node) {
		distances = new HashMap<String,Double>();
		this.name = node.getName();
		for(String inNd:node.getNexts()) {
			distances.put(inNd, new Double(1));
		}
	}

	public void addIn(String inNd) {
		distances.put(inNd, new Double(1));
	}

	public Double getDistance(String otherNd) {
		Double distance = distances.get(otherNd);
		if(null!=distance)
			return distance;
		else 
			return Double.MAX_VALUE;
	}
	
	public Set<String> getNeighbors() {
		return distances.keySet();
	}
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		DijkstraNode other = (DijkstraNode) obj;
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		return true;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("node:"+name+","+System.lineSeparator());
		for(String inNd:distances.keySet()) {
			sb.append(inNd+",");
			sb.append(System.lineSeparator());
		}
		return sb.toString();
	}
	
}
