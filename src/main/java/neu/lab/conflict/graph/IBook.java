package neu.lab.conflict.graph;

import java.util.Collection;

public abstract class IBook {

	protected INode node;
	protected Collection<IRecord> records;
	
	public IBook(INode node) {
		this.node = node;
	}

	public abstract void afterAddAllChildren();// when dog is back,add self information to book.

	public abstract void addChild(IBook doneChildBook);// add child book path to self.

	public String getNodeName() {
		return node.getName();
	}

	public Collection<IRecord> getRecords() {
		return records;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("book for " + this.getNodeName() + "\n");
		for (IRecord recordI : this.getRecords()) {
			sb.append(recordI.toString() + "\n");
		}
		return sb.toString();
	}

}
