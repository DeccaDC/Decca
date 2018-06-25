package neu.lab.conflict.visitor;

import java.util.HashSet;
import java.util.Set;

import org.apache.maven.shared.dependency.tree.DependencyNode;
import org.apache.maven.shared.dependency.tree.traversal.DependencyNodeVisitor;

import neu.lab.conflict.Conf;
import neu.lab.conflict.container.NodeAdapters;
import neu.lab.conflict.util.MavenUtil;
import neu.lab.conflict.vo.NodeAdapter;

public class NodeAdapterCollector implements DependencyNodeVisitor {
	private static Set<String> longTimeLib;// lib that takes a long time to get call-graph.
	static {
		longTimeLib = new HashSet<String>();
		longTimeLib.add("org.scala-lang:scala-library");
		longTimeLib.add("org.clojure:clojure");
	}
	private NodeAdapters nodeAdapters;

	public NodeAdapterCollector(NodeAdapters nodeAdapters) {
		this.nodeAdapters = nodeAdapters;
	}

	public boolean visit(DependencyNode node) {

		MavenUtil.i().getLog().debug(node.toNodeString() + " type:" + node.getArtifact().getType() + " version"
				+ node.getArtifact().getVersionRange() + " selected:" + (node.getState() == DependencyNode.INCLUDED));
		
		if(Conf.DEL_LONGTIME) {
			if (longTimeLib.contains(node.getArtifact().getGroupId() + ":" + node.getArtifact().getArtifactId())) {
				return false;
			}
		}
		
		if (Conf.DEL_OPTIONAL) {
			if (node.getArtifact().isOptional()) {
				return false;
			}
		}
		if (MavenUtil.i().getMojo().ignoreProvidedScope) {
			if ("provided".equals(node.getArtifact().getScope())) {
				return false;
			}
		}
		if (MavenUtil.i().getMojo().ignoreTestScope) {
			if ("test".equals(node.getArtifact().getScope())) {
				return false;
			}
		}
		if (MavenUtil.i().getMojo().ignoreRuntimeScope) {
			if ("runtime".equals(node.getArtifact().getScope())) {
				return false;
			}
		}

		nodeAdapters.addNodeAapter(new NodeAdapter(node));
		return true;
	}

	public boolean endVisit(DependencyNode node) {
		return true;
	}
}
