package neu.lab.evoshell.graph;

import java.util.Collection;
import java.util.List;

public abstract class IBook {

	protected INode node;
	protected Collection<IRecord> records;

	public IBook(INode node) {
		this.node = node;
	}

	public abstract void afterAddAllChildren();// called when dog is back,new a record or add self information to book.

	public abstract void addChild(IBook doneChildBook);// add child book path to self.

	/**
	 * @param list
	 *            :multiple loop path.path don't includes start and end. e.g.
	 *            if there are A->B->C->A,A->D->E->A ,there B->C,D->E in list.
	 */
	public abstract void dealLoop(List<List<String>> list);// dealLoopPath

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
