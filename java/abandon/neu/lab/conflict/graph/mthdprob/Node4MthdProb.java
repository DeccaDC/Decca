package abandon.neu.lab.conflict.graph.mthdprob;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import neu.lab.conflict.graph.IBook;
import neu.lab.conflict.graph.INode;
import neu.lab.conflict.graph.IRecord;
import neu.lab.conflict.util.MavenUtil;
import neu.lab.conflict.util.SootUtil;

public class Node4MthdProb implements INode {

	private String name;
	private boolean isHostNode;
	private boolean isConflictNode;
	private Set<String> outNds;
	//<targetMethod,count of method that have same name>.
	private Map<String, Integer> tgt2cnt;

	public Node4MthdProb(String name, boolean isHostNode, boolean isConflictNode) {
		this.name = name;
		this.isHostNode = isHostNode;
		this.isConflictNode = isConflictNode;
		outNds = new HashSet<String>();
	}
	
	public Double getCallProb(String mthdSig) {
		Integer cnt = getTgt2cnt().get(SootUtil.mthdSig2name(mthdSig));
		if(cnt==null) {
			MavenUtil.i().getLog().warn("no name-count for method:"+mthdSig);
		}
		return 1.0/cnt;
	}

	private Map<String, Integer> getTgt2cnt() {
		if (tgt2cnt == null) {
			tgt2cnt = new HashMap<String, Integer>();
			for(String tgtNd:outNds) {
				String tgtName = SootUtil.mthdSig2name(tgtNd);
				Integer cnt = tgt2cnt.get(tgtName);
				if(cnt==null)
					tgt2cnt.put(tgtName, 1);
				else 
					tgt2cnt.put(tgtName, cnt+1);
				
			}
		}
		return tgt2cnt;
	}

	public boolean isHostNode() {
		return isHostNode;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Collection<String> getNexts() {
		return outNds;
	}

	@Override
	public IBook getBook() {
		return new Book4MthdProb(this);
	}

	@Override
	public IRecord formNewRecord() {
		return new Record4MthdProb(name,1.0,0.0);
	}
	
	@Override
	public int hashCode() {
		return name.hashCode();
	}
	@Override
	public boolean equals(Object other) {
		if(other instanceof Node4MthdProb) {
			Node4MthdProb otherNode = (Node4MthdProb)other;
			return name.equals(otherNode.getName());
		}
		return false;
	}

	public void addOutNd(String tgt) {
		outNds.add(tgt);		
	}

}
