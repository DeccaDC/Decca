package abandon.neu.lab.conflict.graph.clsref;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import abandon.neu.lab.conflict.graph.path1.Node4Path;
import neu.lab.conflict.graph.IBook;
import neu.lab.conflict.graph.IRecord;

public class Node4ClsRef extends Node4Path {

	public boolean isHostNode() {
		return isHostNode;
	}

	private String clsName;
	private boolean isHostNode;
	private List<String> inClses;// class who references this class

	public Node4ClsRef(String clsName, boolean isHostNode) {
		this.clsName = clsName;
		this.isHostNode = isHostNode;
		inClses = new ArrayList<String>();
	}

	public void addInCls(String refCls) {
		inClses.add(refCls);
	}

	/**
	 * delete referNode that is only a String(not node). e.g.,java.lang.Object
	 * 
	 * @param name2node
	 */
	public void delGhostRefer(Map<String, Node4ClsRef> name2node) {
		Iterator<String> referIte = inClses.iterator();
		while (referIte.hasNext()) {
			if (null == name2node.get(referIte.next()))
				referIte.remove();
		}
	}

	/**
	 * delete referNode who is in host.
	 * 
	 * @param name2node
	 */
	public void delHostRefer(Map<String, Node4ClsRef> name2node) {
		Iterator<String> referIte = inClses.iterator();
		while (referIte.hasNext()) {
			Node4ClsRef referNode = name2node.get(referIte.next());
			if (referNode != null && referNode.isHostNode)
				referIte.remove();
		}
	}

	@Override
	public String getName() {
		return clsName;
	}

	@Override
	public Collection<String> getNexts() {
		return inClses;
	}

	@Override
	public IBook getBook() {
		return new Book4ClsRefPath(this);
	}

	@Override
	public IRecord formNewRecord() {
		return new Record4ClsRefPath(getName(), 1, isHostNode);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(clsName + " " + isHostNode+System.lineSeparator());
		for (String inCls : this.inClses) {
			sb.append("inCls:" + inCls+System.lineSeparator());
		}
		return sb.toString();
	}
}
