package abandon.neu.lab.conflict.graph.clsref;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import abandon.neu.lab.conflict.risk.ref.tb.RefTb;
import neu.lab.conflict.graph.IGraph;
import neu.lab.conflict.graph.INode;
import neu.lab.conflict.util.MavenUtil;
import neu.lab.conflict.vo.NodeAdapter;

public class Graph4ClsRef implements IGraph {
	private Map<String, Node4ClsRef> name2node;

	public Graph4ClsRef() {
		name2node = new HashMap<String, Node4ClsRef>();
	}

	public void addNode(String nodeName, boolean isHostNode) {
		if (name2node.get(nodeName) != null) {
			MavenUtil.i().getLog().info("duplicate class for " + nodeName + " when forming class-refrence-graph");
		}
		name2node.put(nodeName, new Node4ClsRef(nodeName, isHostNode));
	}

	public void addErs(String ee, Collection<String> ers) {
		Node4ClsRef eeNode = name2node.get(ee);
		if (eeNode != null) {
			for (String er : ers) {
				eeNode.addInCls(er);
			}
		}
	}

	public void addEes(String er, Collection<String> ees) {
		for (String ee : ees) {
			this.mustGetNode(ee).addInCls(er);
		}
	}

	public static Graph4ClsRef getGraph(NodeAdapter node, boolean filterEdge) {
		LinkedList<NodeAdapter> ancestors = node.getAncestors(true);// from down to top
		Graph4ClsRef graph = new Graph4ClsRef();
		// add node to graph
		for (int i = 0; i < ancestors.size(); i++) {
			for (String clsName : ancestors.get(i).getDepJar().getAllCls(true)) {
				if (i == ancestors.size() - 1) {// host node
					graph.addNode(clsName, true);
				} else {// else nodes
					graph.addNode(clsName, false);
				}
			}

		}
		// add edge to garph
		for (int i = 1; i < ancestors.size(); i++) {// set i from 1 to ignore conflict jar
			NodeAdapter ancestorNode = ancestors.get(i);
			RefTb refTb = ancestorNode.getDepJar().getRefTb();
			for (String ee : refTb) {
				graph.addErs(ee, refTb.getErs(ee));
			}
		}
		if (filterEdge)
			graph.filterEdge();
		return graph;
	}

	private void filterEdge() {
		// filter edge that host to host.Edge that is in conflict-jar needn't be
		// filtered because conflict-jar wasn't added in graph-factory.
		for (Node4ClsRef node : name2node.values()) {
			node.delGhostRefer(name2node);
			if (node.isHostNode()) {
				node.delHostRefer(name2node);
			}
		}
	}

	@Override
	public INode getNode(String nodeName) {
		return name2node.get(nodeName);
	}

	/**
	 * if there's not a node,then create one.
	 * 
	 * @return
	 */
	public Node4ClsRef mustGetNode(String nodeName) {
		Node4ClsRef node = name2node.get(nodeName);
		if (node == null) {
			node = new Node4ClsRef(nodeName, MavenUtil.i().isHostClass(nodeName));
			name2node.put(nodeName, node);
		}
		return node;
	}

	public void addNode(String nodeName) {
		name2node.put(nodeName, new Node4ClsRef(nodeName, MavenUtil.i().isHostClass(nodeName)));
	}

	@Override
	public Collection<String> getAllNode() {
		return name2node.keySet();
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(String name:name2node.keySet()) {
			sb.append(name2node.get(name).toString());
			sb.append(System.lineSeparator());
		}
		return sb.toString();
	}

}
