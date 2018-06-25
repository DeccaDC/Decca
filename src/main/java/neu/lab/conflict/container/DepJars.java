package neu.lab.conflict.container;

import java.util.HashSet;
import java.util.Set;

import neu.lab.conflict.util.MavenUtil;
import neu.lab.conflict.vo.DepJar;
import neu.lab.conflict.vo.NodeAdapter;

public class DepJars {
	private static DepJars instance;

	public static DepJars i() {
		return instance;
	}

	public static void init(NodeAdapters nodeAdapters) {
		if (instance == null) {
			instance = new DepJars(nodeAdapters);
		}
	}

	private Set<DepJar> container;

	private DepJars(NodeAdapters nodeAdapters) {
		container = new HashSet<DepJar>();
		for (NodeAdapter nodeAdapter : nodeAdapters.getAllNodeAdapter()) {
			container.add(new DepJar(nodeAdapter.getGroupId(), nodeAdapter.getArtifactId(), nodeAdapter.getVersion(),
					nodeAdapter.getClassifier(), nodeAdapter.getFilePath()));
		}
	}

	public DepJar getDep(String groupId, String artifactId, String version, String classifier) {
		for (DepJar dep : container) {
			if (dep.isSame(groupId, artifactId, version, classifier)) {
				return dep;
			}
		}
		MavenUtil.i().getLog().warn("cant find dep:" + groupId + ":" + artifactId + ":" + version + ":" + classifier);
		return null;
	}

	public Set<DepJar> getAllDepJar() {
		return container;
	}

	public DepJar getDep(NodeAdapter nodeAdapter) {
		return getDep(nodeAdapter.getGroupId(), nodeAdapter.getArtifactId(), nodeAdapter.getVersion(),
				nodeAdapter.getClassifier());
	}

}
