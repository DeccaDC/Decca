package neu.lab.conflict.vo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import abandon.neu.lab.conflict.risk.node.ConflictNRisk;
import abandon.neu.lab.conflict.risk.ref.ConflictRRisk;
import neu.lab.conflict.risk.jar.ConflictJRisk;
import neu.lab.conflict.util.MavenUtil;

public class Conflict {
	private String groupId;
	private String artifactId;

	private Set<NodeAdapter> nodes;
	private Set<DepJar> depJars;
	private DepJar usedDepJar;
	// private ConflictRiskAna riskAna;

	public Conflict(String groupId, String artifactId) {
		nodes = new HashSet<NodeAdapter>();
		this.groupId = groupId;
		this.artifactId = artifactId;
	}

	/**
	 * 得到使用的DepJar
	 * @return
	 */
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
	
	/**
	 * 设置usedDepJar
	 */
	public void setUsedDepJar(DepJar depJar) {
		usedDepJar = depJar;
	}
	
	/**
	 * 得到除了被选中的jar以外的其他被依赖的jar包
	 * @return
	 */
	public Set<DepJar> getOtherDepJar4Use() {
		Set<DepJar> usedDepJars = new HashSet<DepJar>();
		for (DepJar depJar : depJars) {
			System.out.println("conflict.getotherdepjar4use" + depJar.toString());
			if (depJar.isSelected()) {
				System.out.println("select depJar" + depJar.toString());
			}
			else {
				usedDepJars.add(depJar);
			}
		}
		return usedDepJars;
	}

	public void addNode(NodeAdapter nodeAdapter) {
		nodes.add(nodeAdapter);
	}

	/**
	 * 同一个构件
	 * @param groupId2
	 * @param artifactId2
	 * @return
	 */
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

	//abandon
	public ConflictNRisk getNRisk() {
		// if(riskAna==null) {
		// riskAna = ConflictRiskAna.getConflictRiskAna(this);
		// }
		// return riskAna;
		return new ConflictNRisk(this);
	}
	
	//abandon
	public ConflictRRisk getRefRisk() {
		return new ConflictRRisk(this);
	}
	
	public ConflictJRisk getJRisk() {
		return new ConflictJRisk(this);
	}
	
	

	@Override
	public String toString() {
		String str = groupId + ":" + artifactId + " conflict version:";
		for (DepJar depJar : depJars) {
			str = str + depJar.getVersion() + ":" + depJar.getClassifier() + "-";
		}
		str = str + "---used jar:" + getUsedDepJar().getVersion() + ":" + getUsedDepJar().getClassifier();
		return str;
	}

	public String getGroupId() {
		return groupId;
	}

	public String getArtifactId() {
		return artifactId;
	}
	
	public String getSig() {
		return getGroupId()+":"+getArtifactId();
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
