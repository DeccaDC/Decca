package abandon.neu.lab.conflict.statics;

import java.util.ArrayList;
import java.util.List;

import neu.lab.conflict.vo.NodeAdapter;

public class NodeDup {
	private String groupId;
	private String artifactId;
	private String version;
	private String classifier;

	private List<NodeAdapter> nodes;

	public NodeDup(String groupId, String artifactId, String version, String classifier) {
		this.groupId = groupId;
		this.artifactId = artifactId;
		this.version = version;
		this.classifier = classifier;
		nodes = new ArrayList<NodeAdapter>();
	}

	public boolean isSelf(NodeAdapter nodeAdapter) {
		return groupId.equals(nodeAdapter.getGroupId()) && artifactId.equals(nodeAdapter.getArtifactId())
				&& version.equals(nodeAdapter.getVersion()) && classifier.equals(nodeAdapter.getClassifier());
	}
	public void addNode(NodeAdapter nodeAdapter) {
		 nodes.add(nodeAdapter);
	}

	public boolean isDup() {
		return nodes.size()>1;
	}
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("dup path for:"+groupId+artifactId+version+classifier+"\n");
		for(NodeAdapter node:nodes) {
			sb.append("-->"+node.getWholePath());
			sb.append("\n");
		}
		return sb.toString();
	}
}
