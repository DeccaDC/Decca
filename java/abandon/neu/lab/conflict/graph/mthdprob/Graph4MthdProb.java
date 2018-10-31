package abandon.neu.lab.conflict.graph.mthdprob;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import neu.lab.conflict.graph.IGraph;
import neu.lab.conflict.graph.INode;
import neu.lab.conflict.vo.MethodCall;

public class Graph4MthdProb implements IGraph{
	
	private Map<String, Node4MthdProb> name2node;
	
	public Graph4MthdProb(Collection<Node4MthdProb> nodes,Collection<MethodCall> calls) {
		name2node = new HashMap<String, Node4MthdProb>();
		for (Node4MthdProb node : nodes) {
			name2node.put(node.getName(), node);
		}
		for (MethodCall call : calls) {
			addEdge(call);
		}
	}
	
	public Set<String> getHostNds(){
		Set<String> hostNds = new HashSet<String>();
		for(Node4MthdProb node:name2node.values()) {
			if(node.isHostNode())
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
