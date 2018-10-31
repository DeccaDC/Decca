package abandon.neu.lab.conflict.graph.path1;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import neu.lab.conflict.graph.IBook;
import neu.lab.conflict.graph.IRecord;
import neu.lab.conflict.util.SootUtil;

public class Node4MthdPath1 extends Node4Path{
	private String name;
	private boolean isHostNode;
	private boolean isConflictNode;
	private Set<String> inNds;
	private Set<String> outNds;
	public Node4MthdPath1(String name,boolean isHostNode,boolean isConflictNode) {
		this.name = name;
		this.isHostNode = isHostNode;
		this.isConflictNode = isConflictNode;
		inNds = new HashSet<String>();
		outNds = new HashSet<String>();
	}

	public Set<String> getOutNds() {
		return outNds;
	}
	
	public boolean isConflictNode() {
		return isConflictNode;
	}

	public String getName() {
		return name;
	}
	public Set<String> getInNds(){
		return inNds;
	}
	public boolean isHostNode() {
		return isHostNode;
	}
	public void addInNd(String inNd) {
		inNds.add(inNd);
	}
	public void delInNd(String delNd) {
		inNds.remove(delNd);
	}
	public void addOutNd(String outNd) {
		outNds.add(outNd);
	}
	public void delOutNd(String delNd) {
		outNds.remove(delNd);
	}
	@Override
	public int hashCode() {
		return name.hashCode();
	}
	@Override
	public boolean equals(Object other) {
		if(other instanceof Node4MthdPath1) {
			Node4MthdPath1 otherNode = (Node4MthdPath1)other;
			return name.equals(otherNode.getName());
		}
		return false;
	}

	public Map<String, Integer> calNameCnt() {
		Map<String, Integer> result = new HashMap<String, Integer>();
		if (outNds != null) {

			for (String mthdSig : outNds) {
				String mthdName = SootUtil.mthdSig2name(mthdSig);
				Integer cnt = result.get(mthdName);
				if (null == cnt) {
					result.put(mthdName, new Integer(1));
				} else {
					result.put(mthdName, cnt + 1);
				}
			}
		}
		return result;
	}
	public String toString() {
		StringBuilder sb = new StringBuilder(name+" isHost:"+isHostNode+"\n");
		sb.append("--inMthd:\n");
		for(String in:inNds) {
			sb.append(in);
			sb.append("\n");
		}
		sb.append("--outMthd:\n");
		for(String out:outNds) {
			sb.append(out);
			sb.append("\n");
		}
		return sb.toString();
	}

	@Override
	public Collection<String> getNexts() {
		return getInNds();
	}

	@Override
	public IBook getBook() {
		return new Book4MthdPath(this);
	}

	@Override
	public IRecord formNewRecord() {
		return new Record4MthdPath(getName(), isHostNode(), 1);
	}
}
