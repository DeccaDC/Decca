package neu.lab.conflict.vo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import neu.lab.conflict.risk.ConflictRiskAna;
import neu.lab.conflict.util.MavenUtil;

public class NodeConflict {
	private String groupId;
	private String artifactId;

	private Set<NodeAdapter> nodes;
	private Set<DepJar> depJars;
	private DepJar usedDepJar;
	// private ConflictRiskAna riskAna;

	public NodeConflict(String groupId, String artifactId) {
		nodes = new HashSet<NodeAdapter>();
		this.groupId = groupId;
		this.artifactId = artifactId;
	}

	public DepJar getUsedDepJar() {
		if (null == usedDepJar) {
			for (DepJar depJar : depJars) {
				if (depJar.isSelected()) {
					if (null != usedDepJar)
						MavenUtil.i().getLog()
								.warn("duplicate used version for dependency:" + groupId + ":" + artifactId);
					usedDepJar = depJar;
				}
			}
		}
		return usedDepJar;

	}
	
	public Set<String> getThrownMthds(){
		Set<String> thrownMthds = new HashSet<String>();
		for(DepJar depJar:depJars) {
			for(String mthd:depJar.getAllMthd()) {
				if(!getUsedDepJar().containsMthd(mthd)) {
					thrownMthds.add(mthd);
				}
			}
		}
		return thrownMthds;
	}

	public void addNode(NodeAdapter nodeAdapter) {
		nodes.add(nodeAdapter);
	}

	public boolean sameArtifact(String groupId2, String artifactId2) {
		return groupId.equals(groupId2) && artifactId.equals(artifactId2);
	}

	public Set<DepJar> getDepJars() {
		if (depJars == null) {
			depJars = new HashSet<DepJar>();
			for (NodeAdapter nodeAdapter : nodes) {
				depJars.add(nodeAdapter.getDepJar());
			}
		}
		return depJars;
	}

	public Set<NodeAdapter> getNodeAdapters() {
		return this.nodes;
	}

	public boolean isConflict() {
		return getDepJars().size() > 1;
	}

	public ConflictRiskAna getRiskAna() {
		// if(riskAna==null) {
		// riskAna = ConflictRiskAna.getConflictRiskAna(this);
		// }
		// return riskAna;
		return ConflictRiskAna.getConflictRiskAna(this);
	}

	@Override
	public String toString() {
		String str = groupId + ":" + artifactId + " conflict version:";
		for (DepJar depJar : depJars) {
			str = str + depJar.getVersion() + ":" + depJar.getClassifier() + "-";
		}
		str = str + "-->used jar:" + getUsedDepJar().getVersion() + ":" + getUsedDepJar().getClassifier();
		return str;
	}

	public String getGroupId() {
		return groupId;
	}

	public String getArtifactId() {
		return artifactId;
	}
	
	public String getSig() {
		return groupId+":"+artifactId;
	}
	/**
	 * @return first version is the used version
	 */
	public List<String> getVersions(){
		List<String> versions = new ArrayList<String>();
		versions.add(getUsedDepJar().getVersion());
		for(DepJar depJar:depJars) {
			String version = depJar.getVersion();
			if(!versions.contains(version)) {
				versions.add("/"+version);
			}
		}
		return versions;
	}
}
