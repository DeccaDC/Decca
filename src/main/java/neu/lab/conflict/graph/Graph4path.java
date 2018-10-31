package neu.lab.conflict.graph;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import neu.lab.conflict.vo.MethodCall;

public class Graph4path implements IGraph{
	
	Map<String,Node4path> name2node;
	public Graph4path(Map<String, Node4path> name2node, Collection<MethodCall> calls) {
		this.name2node = name2node;
		for (MethodCall call : calls) {
			addEdge(call);
		}
	}
	public Graph4path(Map<String, Node4path> name2node) {
		this.name2node = name2node;
	}

	private void addEdge(MethodCall call) {
		name2node.get(call.getSrc()).addOutNd(call.getTgt());
//		name2node.get(call.getTgt()).addInNd(call.getSrc());
	}

	public Set<String> getHostNds() {
		Set<String> hostNds = new HashSet<String>();
		for (Node4path node : name2node.values()) {
			if (node.isHostNode())
				hostNds.add(node.getName());
		}
		return hostNds;
	}
	
	/**nodes in nds2remain will be remain.
	 * @param nds2remain
	 */
	public void filterGraph(Set<String> nds2remain) {
		//delete node
		Iterator<Entry<String,Node4path>> ite = name2node.entrySet().iterator();
		while(ite.hasNext()) {
			Entry<String,Node4path> entry = ite.next();
			if(!nds2remain.contains(entry.getKey())) {//node to delete.
				ite.remove();
			}
		}
		//delete edge
		ite = name2node.entrySet().iterator();
		while(ite.hasNext()) {
			Entry<String,Node4path> entry = ite.next();
			entry.getValue().filterEdge(nds2remain);
		}
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
