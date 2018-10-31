package abandon.neu.lab.conflict.graph.path1;

import java.util.ArrayList;

import neu.lab.conflict.graph.IBook;
import neu.lab.conflict.graph.IRecord;

public abstract class Book4Path extends IBook{
	public Book4Path(Node4Path node) {
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
	
	@Override
	public void addChild(IBook doneChildBook) {
		for (IRecord recordI : doneChildBook.getRecords()) {
			records.add(recordI.clone());
		}
	}

	private void addNdToAllPath(String node) {
		for (IRecord recordI : records) {
			Record4Path mthdPathRecord = (Record4Path) recordI;
			mthdPathRecord.addTail(node);
		}
	}
}
