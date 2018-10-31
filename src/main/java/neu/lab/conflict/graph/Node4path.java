package neu.lab.conflict.graph;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import neu.lab.conflict.GlobalVar;

public class Node4path implements INode {
	private String name;
	private boolean isHost;
	private boolean isRisk;
	private Set<String> outs;
	//	private Set<String> ins;

	public Node4path(String name, boolean isHost, boolean isRisk) {
		super();
		this.name = name;
		this.isHost = isHost;
		this.isRisk = isRisk;
		if (GlobalVar.useTreeSet)
			outs = new TreeSet<String>();
		else
			outs = new HashSet<String>();
		//		ins = new TreeSet<String>();
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
		return new Book4path(this);
	}

	@Override
	public IRecord formNewRecord() {
		return new Record4path(this.name, this.name, 1);
	}

	public void addOutNd(String tgt) {
		outs.add(tgt);
	}

	public boolean isRisk() {
		return isRisk;
	}

	public boolean isHostNode() {
		return isHost;
	}
	//	public void addInNd(String src) {
	//		ins.add(src);
	//		
	//	}
	//	public int getInCnt() {
	//		return ins.size();
	//	}

	public void filterEdge(Set<String> nds2remain) {
		Iterator<String> ite = outs.iterator();
		while (ite.hasNext()) {
			String node = ite.next();
			if (!nds2remain.contains(node)) {
				ite.remove();
			}
		}
		//		ite = ins.iterator();
		//		while(ite.hasNext()) {
		//			String node = ite.next();
		//			if(!nds2remain.contains(node)) {
		//				ite.remove();
		//			}
		//		}
	}
}
