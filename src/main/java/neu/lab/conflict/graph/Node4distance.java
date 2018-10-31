package neu.lab.conflict.graph;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import neu.lab.conflict.GlobalVar;
import neu.lab.conflict.util.SootUtil;

public class Node4distance implements INode {
	private String name;
	private boolean isAccessHost;//not private
	private boolean isRisk;
	private int cfgBranch;

	private Integer branch;// cfgBranch + polyBranch
	private Set<String> outs;

	public Node4distance(String name, boolean isAccessHost, boolean isRisk, int cfgBranch) {
		super();
		this.name = name;
		this.isAccessHost = isAccessHost;
		this.isRisk = isRisk;
		this.cfgBranch = cfgBranch;
		if (GlobalVar.useTreeSet)
			outs = new TreeSet<String>();
		else
			outs = new HashSet<String>();

	}

	public Integer getBranch() {
		if (branch == null) {
			Map<String, Integer> tgt2cnt = new HashMap<String, Integer>();
			// traverse all out
			for (String tgtNd : outs) {
				String tgtName = SootUtil.mthdSig2name(tgtNd);
				Integer cnt = tgt2cnt.get(tgtName);
				if (cnt == null)
					tgt2cnt.put(tgtName, 1);
				else
					tgt2cnt.put(tgtName, cnt + 1);

			}
			// get polymorphic
			int polyBranch = 0;
			for (String tgtName : tgt2cnt.keySet()) {
				if (tgt2cnt.get(tgtName) > 1) {
					polyBranch++;
				}
			}
			branch = cfgBranch + polyBranch;
		}
		return branch;
	}

	public boolean isRisk() {
		return isRisk;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Collection<String> getNexts() {
		return outs;
	}

	@Override
	public IBook getBook() {
		return new Book4distance(this);
	}

	@Override
	public IRecord formNewRecord() {
		return new Record4distance(name, 0, 0);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Node4distance other = (Node4distance) obj;
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		return true;
	}

	public boolean isHostNode() {
		return isAccessHost;
	}

	public void addOutNd(String tgt) {
		outs.add(tgt);
	}

}
