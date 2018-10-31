package abandon.neu.lab.conflict.graph.mthdprob;

import java.util.ArrayList;

import neu.lab.conflict.graph.IBook;
import neu.lab.conflict.graph.IRecord;

public class Book4MthdProb extends IBook {

	public Book4MthdProb(Node4MthdProb node) {
		super(node);
		this.records = new ArrayList<IRecord>();
	}

	@Override
	public void afterAddAllChildren() {
		// if (records.isEmpty()) {
		// IRecord path = node.formNewRecord();
		// records.add(path);
		// }
	}

	@Override
	public void addChild(IBook doneChildBook) {
		Double this2done = ((Node4MthdProb) node).getCallProb(doneChildBook.getNodeName());
		addRecord(doneChildBook.getNodeName(), this2done,1.0);
		for (IRecord recordI : doneChildBook.getRecords()) {
			Record4MthdProb record = (Record4MthdProb) recordI;
			Double this2tgt = this2done * record.getProb();
			addRecord(record.getTgtMthd(), this2tgt,record.getDistance()+1);
		}
	}

	public void addRecord(String nodeName, Double prob,Double distance) {
		for (IRecord iRecord : this.records) {
			Record4MthdProb record = (Record4MthdProb) iRecord;
			if (nodeName.equals(record.getTgtMthd())) {
				record.updateProb(prob);
				record.updateDistance(distance);
				return;
			}
		}
		this.records.add(new Record4MthdProb(nodeName, prob, distance));
	}

}
