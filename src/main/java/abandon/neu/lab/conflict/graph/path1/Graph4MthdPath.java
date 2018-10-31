package abandon.neu.lab.conflict.graph.path1;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import abandon.neu.lab.conflict.graph.filter.FilterInvoker;

import java.util.Set;

import neu.lab.conflict.graph.IGraph;
import neu.lab.conflict.graph.INode;
import neu.lab.conflict.util.Conf;
import neu.lab.conflict.util.MavenUtil;
import neu.lab.conflict.vo.MethodCall;

public class Graph4MthdPath implements IGraph{
	
	private Map<String, Node4MthdPath1> name2node;

	public Graph4MthdPath(Set<Node4MthdPath1> nodes, List<MethodCall> calls) {

		
		MavenUtil.i().getLog().debug("graph-before-filter nodes size:" + nodes.size() + " calls size:" + calls.size());
		if (Conf.FLT_CALL)
			filtCalls(calls);
		name2node = new HashMap<String, Node4MthdPath1>();
		for (Node4MthdPath1 node : nodes) {
			name2node.put(node.getName(), node);
		}
		for (MethodCall call : calls) {
			addEdge(call);
		}
		if (Conf.FLT_DANGER_IMPL) {
			filterDangerImpl();
		}
//		if (Conf.FLT_NODE)
//			filterNode(risk2mthds);
		MavenUtil.i().getLog()
				.debug("graph-after-filter nodes size:" + name2node.size() + " calls size:" + calls.size());
	}

	private void filterDangerImpl() {
		for (String ndName : name2node.keySet()) {
			Node4MthdPath1 node = name2node.get(ndName);
			Map<String, Integer> name2cnt = node.calNameCnt();
			for (String outName : name2cnt.keySet()) {
				if (name2cnt.get(outName) >= Conf.DANGER_IMPL_T) {// delete out-method contains this name
					Iterator<String> outIte = node.getOutNds().iterator();
					while (outIte.hasNext()) {
						String outSig = outIte.next();
						if (outSig.contains(outName)) {
							outIte.remove();
							name2node.get(outSig).delInNd(ndName);
						}
					}
				}
			}
		}
	}

	private void filtCalls(List<MethodCall> calls) {
		FilterInvoker invoker = new FilterInvoker();
		Iterator<MethodCall> ite = calls.iterator();
		while (ite.hasNext()) {
			MethodCall call = ite.next();
			if (invoker.shouldFlt(call.getSrc()) || invoker.shouldFlt(call.getTgt()))
				ite.remove();
		}
	}

	private void filterNode(Set<String> risk2mthds) {
		// no in-method(except in host) or no out-method(except in conflict) should
		// filter
		int delNum;
		do {
			delNum = 0;
			Iterator<Entry<String, Node4MthdPath1>> ite = name2node.entrySet().iterator();
			while (ite.hasNext()) {
				Node4MthdPath1 node = ite.next().getValue();
				if (node.isHostNode()) {// host method

				} else if (risk2mthds.contains(node.getName())) {// risk2method

				} else {
					// no in-degree or no out-degree should delete
					if (node.getInNds().size() == 0) {
						for (String out : node.getOutNds()) {
							name2node.get(out).delInNd(node.getName());
						}
						ite.remove();
						delNum++;
					} else if (node.getOutNds().size() == 0) {
						for (String in : node.getInNds()) {
							name2node.get(in).delOutNd(node.getName());
						}
						ite.remove();
						delNum++;
					}
				}
			}
		} while (delNum > 0);
	}

	@Override
	public INode getNode(String name) {
		return name2node.get(name);
	}

	public void addEdge(MethodCall call) {
		String src = call.getSrc();
		String tgt = call.getTgt();
		name2node.get(src).addOutNd(tgt);
		name2node.get(tgt).addInNd(src);
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (String name : name2node.keySet()) {
			sb.append("============================\n");
			sb.append(name2node.get(name).toString());
		}
//		for (String node : books.keySet()) {
//			sb.append(books.get(node).toString());
//			sb.append("\n");
//		}
		return sb.toString();
	}

	@Override
	public Collection<String> getAllNode() {
		return name2node.keySet();
	}
}
