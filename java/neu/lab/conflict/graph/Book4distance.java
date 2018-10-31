package neu.lab.conflict.graph;

import java.util.ArrayList;


public class Book4distance extends IBook {

	public Book4distance(INode node) {
		super(node);
		this.records = new ArrayList<IRecord>();
	}

	@Override
	public void afterAddAllChildren() {
		if(getNode().isRisk()) {
			this.addRecord(getNodeName(), 0, 0);
		}
	}

	@Override
	public void addChild(IBook doneChildBook) {
		int thisBranch = getNode().getBranch();
		for (IRecord recordI : doneChildBook.getRecords()) {
			Record4distance record = (Record4distance) recordI;
			addRecord(record.getName(), record.getBranch()+thisBranch, record.getDistance()+1);
		}
	}
	
	private Node4distance getNode() {
		return (Node4distance)this.node;
	}

	private void addRecord(String nodeName, double branch, double distance) {
		for (IRecord iRecord : this.records) {
			Record4distance record = (Record4distance) iRecord;
			if (nodeName.equals(record.getName())) {
				record.updateBranch(branch);
				record.updateDistance(distance);
				return;
			}
		}
		this.records.add(new Record4distance(nodeName, branch, distance));
	}

}
