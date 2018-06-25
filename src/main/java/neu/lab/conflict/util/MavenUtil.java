package neu.lab.conflict.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

import neu.lab.conflict.ConflictMojo;
import neu.lab.conflict.vo.NodeAdapter;

public class MavenUtil {
	private static MavenUtil instance = new MavenUtil();

	public static MavenUtil i() {
		return instance;
	}

	private MavenUtil() {
	}

	private ConflictMojo mojo;

	public boolean isInner(NodeAdapter nodeAdapter) {
		return nodeAdapter.isSelf(mojo.project);
		// for (MavenProject mavenProject : mojo.reactorProjects) {
		// if (nodeAdapter.isSelf(mavenProject))
		// return true;
		// }
		// return false;
	}

	public MavenProject getMavenProject(NodeAdapter nodeAdapter) {
		for (MavenProject mavenProject : mojo.reactorProjects) {
			if (nodeAdapter.isSelf(mavenProject))
				return mavenProject;
		}
		return null;
	}

	public void setMojo(ConflictMojo mojo) {
		this.mojo = mojo;
	}

	public void resolve(Artifact artifact) throws ArtifactResolutionException, ArtifactNotFoundException {
		mojo.resolver.resolve(artifact, mojo.remoteRepositories, mojo.localRepository);
	}

	public Log getLog() {
		return mojo.getLog();
	}

	public Artifact getArtifact(String groupId, String artifactId, String versionRange, String type, String classifier,
			String scope) {
		try {
			return mojo.factory.createDependencyArtifact(groupId, artifactId,
					VersionRange.createFromVersionSpec(versionRange), type, classifier, scope);
		} catch (InvalidVersionSpecificationException e) {
			getLog().error("cant create Artifact!", e);
			return null;
		}
	}

	public File getBuildDir() {
		return mojo.buildDir;
	}

	public List<String> getSrcPaths() {
		List<String> srcPaths = new ArrayList<String>();
		for (String srcPath : this.mojo.compileSourceRoots) {
			if (new File(srcPath).exists())
				srcPaths.add(srcPath);
		}
		return srcPaths;
	}

	public String getProjectInfo() {
		return mojo.project.getGroupId() + ":" + mojo.project.getArtifactId() + ":" + mojo.project.getVersion() + "@"
				+ mojo.project.getFile().getAbsolutePath();
	}
	
	public String getProjectGroupId() {
		return mojo.project.getGroupId();
	}
	
	public String getProjectSig() {
		return mojo.project.getGroupId() + ":" + mojo.project.getArtifactId() + ":" + mojo.project.getVersion();
	}

	public String getProjectArtifactId() {
		return mojo.project.getArtifactId();
	}
	
	public String getProjectVersion() {
		return mojo.project.getVersion();
	}
	public ConflictMojo getMojo() {
		return mojo;
	}
	public Double getT_LOW() {
		return mojo.T_LOW;
	}
	public Double getT_HIGH() {
		return mojo.T_HIGH;
	}
}
