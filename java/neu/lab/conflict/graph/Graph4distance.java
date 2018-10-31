package neu.lab.conflict.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import neu.lab.conflict.vo.MethodCall;

public class Graph4distance implements IGraph {

	Map<String, Node4distance> name2node;

	public Graph4distance(Map<String, Node4distance> name2node, Collection<MethodCall> calls) {
		this.name2node = name2node;
		for (MethodCall call : calls) {
			addEdge(call);
		}
	}
	
	public Graph4path getGraph4path() {
		Map<String, Node4path> name2pathNode = new HashMap<String, Node4path>();
		//add node
		for (String nd2remain : name2node.keySet()) {
			Node4distance distanceNode = name2node.get(nd2remain);
			name2pathNode.put(nd2remain, new Node4path(nd2remain, distanceNode.isHostNode(), distanceNode.isRisk()));
		}
		//add relation
		for (String nd2remain : name2node.keySet()) {
			Node4distance distanceNode = name2node.get(nd2remain);
			for(String out:distanceNode.getNexts()) {
					name2pathNode.get(nd2remain).addOutNd(out);
			}
		}
		return new Graph4path(name2pathNode);
	}

	public Graph4path getGraph4path(Set<String> nds2remain) {
		Map<String, Node4path> name2pathNode = new HashMap<String, Node4path>();
		//add node
		for (String nd2remain : nds2remain) {
			Node4distance distanceNode = name2node.get(nd2remain);
			name2pathNode.put(nd2remain, new Node4path(nd2remain, distanceNode.isHostNode(), distanceNode.isRisk()));
		}
		//add relation
		for (String nd2remain : nds2remain) {
			Node4distance distanceNode = name2node.get(nd2remain);
			for(String out:distanceNode.getNexts()) {
				if(nds2remain.contains(out)) {
					name2pathNode.get(nd2remain).addOutNd(out);
//					name2pathNode.get(out).addInNd(nd2remain);
				}
			}
		}
		return new Graph4path(name2pathNode);
	}

	public Set<String> getHostNds() {
		Set<String> hostNds = new HashSet<String>();
		for (Node4distance node : name2node.values()) {
			if (node.isHostNode())
				hostNds.add(node.getName());
		}
		return hostNds;
	}

	private void addEdge(MethodCall call) {
		name2node.get(call.getSrc()).addOutNd(call.getTgt());
	}

	@Override
	public INode getNode(String nodeName) {
		return name2node.get(nodeName);
	}

	@Override
	public Collection<String> getAllNode() {
		return name2node.keySet();
	}

}
