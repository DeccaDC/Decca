package neu.lab.conflict.vo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.dependency.tree.DependencyNode;
import org.dom4j.Element;
import org.dom4j.tree.DefaultElement;

import abandon.neu.lab.conflict.risk.node.DepJarNRisk;
import abandon.neu.lab.conflict.risk.node.NodeNRisk;
import neu.lab.conflict.container.DepJars;
import neu.lab.conflict.container.NodeAdapters;
import neu.lab.conflict.util.ClassifierUtil;
import neu.lab.conflict.util.MavenUtil;

/**
 * @author asus
 *
 */
public class NodeAdapter {
	protected DependencyNode node;
	protected DepJar depJar;
	protected List<String> filePaths;

	public NodeAdapter(DependencyNode node) {
		this.node = node;
		if (node != null)
			resolve();
	}

	public Element getPathElement() {
		Element pathEle = new DefaultElement("path");
		pathEle.addText(getWholePath());
		return pathEle;
	}

	private void resolve() {
		try {
			if (!isInnerProject()) {// inner project is target/classes
				if (null == node.getPremanagedVersion()) {
					// artifact version of node is the version declared in pom.	节点的构件版本是POM中声明的版本。
					if (!node.getArtifact().isResolved())
						MavenUtil.i().resolve(node.getArtifact());
				} else {
					Artifact artifact = MavenUtil.i().getArtifact(getGroupId(), getArtifactId(), getVersion(),
							getType(), getClassifier(), getScope());
					if (!artifact.isResolved())
						MavenUtil.i().resolve(artifact);	//解析这个构件
				}
			}
		} catch (ArtifactResolutionException e) {
			MavenUtil.i().getLog().warn("cant resolve " + this.toString());
		} catch (ArtifactNotFoundException e) {
			MavenUtil.i().getLog().warn("cant resolve " + this.toString());
		}
	}

	public String getGroupId() {
		return node.getArtifact().getGroupId();
	}

	public String getScope() {
		return node.getArtifact().getScope();
	}

	public String getArtifactId() {
		return node.getArtifact().getArtifactId();
	}

	public String getVersion() {
		if (null != node.getPremanagedVersion()) {
			return node.getPremanagedVersion();
		} else {
			return node.getArtifact().getVersion();
		}
	}

	/**
	 * version changes because of dependency management
	 * 被dependency management更改过版本
	 * @return
	 */
	public boolean isVersionChanged() {
		return null != node.getPremanagedVersion();
	}

	protected String getType() {
		return node.getArtifact().getType();
	}

	public String getClassifier() {
		return ClassifierUtil.transformClf(node.getArtifact().getClassifier());
	}

	/**
	 * used version is select from this node,if version was from management ,this node will return false.
	 * 这个版本的node是否被使用，如果被management更改过版本，将返回false
	 * @return
	 */
	public boolean isNodeSelected() {
		if (isVersionChanged())
			return false;
		return node.getState() == DependencyNode.INCLUDED;
	}

//	/**
//	 * used version is select from this node
//	 * 
//	 * @return
//	 */
//	public boolean isVersionSelected() {
//		return getDepJar().isSelected();
//	}

	public String getManagedVersion() {
		return node.getArtifact().getVersion();
	}

	/**
	 * abandon
	 * @param jarRiskAna
	 * @return
	 */
	public NodeNRisk getNodeRiskAna(DepJarNRisk jarRiskAna) {
		return new NodeNRisk(this, jarRiskAna);
	}

	/**
	 * @param includeSelf
	 *            :whether includes self
	 * @return ancestors(from down to top) 从下至上
	 */
	public LinkedList<NodeAdapter> getAncestors(boolean includeSelf) {
		LinkedList<NodeAdapter> ancestors = new LinkedList<NodeAdapter>();
		if (includeSelf)
			ancestors.add(this);
		NodeAdapter father = getParent();
		while (null != father) {
			ancestors.add(father);
			father = father.getParent();
		}
		return ancestors;
	}
	/**
	 * jarClasspaths
	 * @param includeSelf
	 * @return
	 */
	public Collection<String> getAncestorJarCps(boolean includeSelf){
		List<String> jarCps = new ArrayList<String>();
		if (includeSelf)
			jarCps.addAll(this.getFilePath());
		NodeAdapter father = getParent();
		while (null != father) {
			jarCps.addAll(father.getFilePath());
			father = father.getParent();
		}
		return jarCps;
	}

	/**
	 * 得到父节点
	 * @return
	 */
	public NodeAdapter getParent() {
		if (null == node.getParent())
			return null;
		return NodeAdapters.i().getNodeAdapter(node.getParent());
	}

	/**
	 * 得到文件路径
	 * @return
	 */
	public List<String> getFilePath() {
		if (filePaths == null) {
			filePaths = new ArrayList<String>();
			if (isInnerProject()) {// inner project is target/classes
				filePaths.add(MavenUtil.i().getMavenProject(this).getBuild().getOutputDirectory());
				// filePaths = UtilGetter.i().getSrcPaths();
			} else {// dependency is repository address

				try {
					if (null == node.getPremanagedVersion()) {
						filePaths.add(node.getArtifact().getFile().getAbsolutePath());
					} else {
						Artifact artifact = MavenUtil.i().getArtifact(getGroupId(), getArtifactId(), getVersion(),
								getType(), getClassifier(), getScope());
						if (!artifact.isResolved())
							MavenUtil.i().resolve(artifact);
						filePaths.add(artifact.getFile().getAbsolutePath());
					}
				} catch (ArtifactResolutionException e) {
					MavenUtil.i().getLog().warn("cant resolve " + this.toString());
				} catch (ArtifactNotFoundException e) {
					MavenUtil.i().getLog().warn("cant resolve " + this.toString());
				}

			}
		}
		MavenUtil.i().getLog().debug("node filepath for " + toString() + " : " + filePaths);
		return filePaths;

	}

	public boolean isInnerProject() {
		return MavenUtil.i().isInner(this);
	}

	public boolean isSelf(DependencyNode node2) {
		return node.equals(node2);
	}

	public boolean isSelf(MavenProject mavenProject) {
		return isSelf(mavenProject.getGroupId(), mavenProject.getArtifactId(), mavenProject.getVersion(),
				ClassifierUtil.transformClf(mavenProject.getArtifact().getClassifier()));
	}

	public boolean isSelf(String groupId2, String artifactId2, String version2, String classifier2) {
		return getGroupId().equals(groupId2) && getArtifactId().equals(artifactId2) && getVersion().equals(version2)
				&& getClassifier().equals(classifier2);
	}

	public MavenProject getSelfMavenProject() {
		return MavenUtil.i().getMavenProject(this);
	}

	public DepJar getDepJar() {
		if (depJar == null)
			depJar = DepJars.i().getDep(this);
		return depJar;
	}

	@Override
	public String toString() {
		String scope = getScope();
		if (null == scope)
			scope = "";
		return getGroupId() + ":" + getArtifactId() + ":" + getVersion() + ":" + getClassifier() + ":" + scope;
	}

	public String getWholePath() {
		StringBuilder sb = new StringBuilder(toString());
		NodeAdapter father = getParent();
		while (null != father) {
			sb.insert(0, father.toString() + " + ");
			father = father.getParent();
		}
		return sb.toString();
	}

	public int getNodeDepth() {
		int depth = 1;
		NodeAdapter father = getParent();
		while (null != father) {
			depth++;
			father = father.getParent();
		}
		return depth;
	}
}
