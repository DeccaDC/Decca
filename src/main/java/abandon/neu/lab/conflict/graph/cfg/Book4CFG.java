package abandon.neu.lab.conflict.graph.cfg;

import java.util.ArrayList;

import neu.lab.conflict.graph.IBook;
import neu.lab.conflict.graph.IRecord;

public class Book4CFG extends IBook{
	public Book4CFG(Node4CFG node) {
		super(node);
		this.records = new ArrayList<IRecord>();
	}
	@Override
	public void afterAddAllChildren() {
		if (records.isEmpty()) {
			IRecord path = node.formNewRecord();
			records.add(path);
		} else {
			addNdToAllPath(node.getName());
		}
	}

	private void addNdToAllPath(String name) {
		for (IRecord recordI : records) {
			Record4CFG record = (Record4CFG) recordI;
			record.addPathNode((Node4CFG)node);
		}
	}


	@Override
	public void addChild(IBook doneChildBook) {
		for (IRecord recordI : doneChildBook.getRecords()) {
			records.add(recordI.clone());
		}
	}

}
