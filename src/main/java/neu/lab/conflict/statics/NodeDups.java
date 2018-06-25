package neu.lab.conflict.statics;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import neu.lab.conflict.container.NodeAdapters;
import neu.lab.conflict.vo.NodeAdapter;
import neu.lab.conflict.vo.NodeConflict;

public class NodeDups {
	private List<NodeDup> container;

	public NodeDups(NodeAdapters nodeAdapters) {
		container = new ArrayList<NodeDup>();
		for (NodeAdapter node : nodeAdapters.getAllNodeAdapter()) {
			addNodeDup(node);
		}
		Iterator<NodeDup> ite = container.iterator();
		while (ite.hasNext()) {
			NodeDup nodeDup = ite.next();
			if (!nodeDup.isDup()) {// delete conflict if there is only one version
				ite.remove();
			}
		}
	}

	public List<NodeDup> getAllNodeDup() {
		return container;
	}

	public void addNodeDup(NodeAdapter nodeAdapter) {
		NodeDup nodeDup = null;
		for (NodeDup existDup : container) {
			if (existDup.isSelf(nodeAdapter)) {
				nodeDup = existDup;
			}
		}
		if (null == nodeDup) {
			nodeDup = new NodeDup(nodeAdapter.getGroupId(), nodeAdapter.getArtifactId(), nodeAdapter.getVersion(),
					nodeAdapter.getClassifier());
			container.add(nodeDup);
		}
		nodeDup.addNode(nodeAdapter);
	}
}
