package neu.lab.conflict.vo;

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.shared.dependency.tree.DependencyNode;

import neu.lab.conflict.util.MavenUtil;

/**
 * some depjar may be from dependency management instead of dependency tree.We
 * design ManageNodeAdapter for depJar of this type.
 * 
 * @author asus
 *
 */
public class ManageNodeAdapter extends NodeAdapter {
	private String groupId;
	private String artifactId;// artifactId
	private String version;// version
	private String classifier;
	private String type;
	private String scope;
	private Artifact artifact;

	public ManageNodeAdapter(NodeAdapter nodeAdapter) {
		super(null);
		groupId = nodeAdapter.getGroupId();
		artifactId = nodeAdapter.getArtifactId();
		version = nodeAdapter.getManagedVersion();
		classifier = nodeAdapter.getClassifier();
		type = nodeAdapter.getType();
		scope = nodeAdapter.getScope();

		try {
			artifact = MavenUtil.i().getArtifact(getGroupId(), getArtifactId(), getVersion(), getType(),
					getClassifier(), getScope());
			if (!artifact.isResolved())
				MavenUtil.i().resolve(artifact);

		} catch (ArtifactResolutionException e) {
			MavenUtil.i().getLog().warn("cant resolve " + this.toString());
		} catch (ArtifactNotFoundException e) {
			MavenUtil.i().getLog().warn("cant resolve " + this.toString());
		}
	}

	public String getGroupId() {
		return groupId;
	}

	public String getArtifactId() {
		return artifactId;
	}

	public String getVersion() {
		return version;
	}

	public String getClassifier() {
		return classifier;
	}

	public boolean isNodeSelected() {
		return true;
	}

	public boolean isVersionSelected() {
		return true;
	}

	public String getManagedVersion() {
		return version;
	}

	public NodeAdapter getParent() {
		return null;
	}

	protected String getType() {
		return type;
	}

	public String getScope() {
		return scope;
	}

	public boolean isVersionChanged() {
		return false;
	}

	public List<String> getFilePath() {
		if (filePaths == null) {
			filePaths = new ArrayList<String>();
			if (isInnerProject()) {// inner project is target/classes
				// filePaths = UtilGetter.i().getSrcPaths();
				filePaths.add(MavenUtil.i().getMavenProject(this).getBuild().getOutputDirectory());
			} else {// dependency is repository address
				String path = artifact.getFile().getAbsolutePath();
				filePaths.add(path);
			}
		}
		MavenUtil.i().getLog().debug("node filepath for " + toString() + " : " + filePaths);
		return filePaths;
	}

	public boolean isSelf(DependencyNode node2) {
		return false;
	}
}
