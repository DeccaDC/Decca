package neu.lab.conflict.graph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import neu.lab.conflict.util.Conf;
import neu.lab.conflict.util.MavenUtil;

public class Book4path extends IBook {
	Set<String> visitedFathes;//record father who copy book from this node,when all father has visited,this book can be deleted.

	public Book4path(Node4path node) {
		super(node);
		this.records = new ArrayList<IRecord>();
		visitedFathes = new HashSet<String>();
	}

	@Override
	public void afterAddAllChildren() {
		if (getNode().isRisk()) {
			this.records.add(getNode().formNewRecord());
		}
	}

	private Node4path getNode() {
		return (Node4path) this.node;
	}

	@Override
	public void addChild(IBook doneChildBook) {
		//		if ("<com.google.common.truth.extensions.proto.LiteProtoSubject: void isNotEqualTo(java.lang.Object)>"
		//				.equals(this.getNodeName())) {
		//			if ("<com.google.common.truth.Subject: void failWithRawMessage(java.lang.String,java.lang.Object[])>"
		//					.equals(doneChildBook.getNodeName())) {
		//				MavenUtil.i().getLog().info("add neeeded :" + doneChildBook.toString());
		//			}
		//		}
		for (IRecord iRecord : doneChildBook.getRecords()) {
			Record4path record = (Record4path) iRecord;
			addRecord(record.getRiskMthd(), this.getNodeName() + "\n" + record.getPathStr(), record.getPathlen() + 1);
		}
	}

	private void addRecord(String riskMthd, String pathStr, int length) {
		if (Conf.findAllpath) {
			this.records.add(new Record4path(riskMthd, pathStr, length));
		} else {//find shortest path
			for (IRecord iRecord : this.records) {
				Record4path record = (Record4path) iRecord;
				if (riskMthd.equals(record.getRiskMthd())) {
					if (length < record.getPathlen()) {
						record.setPathStr(pathStr);
						record.setPathlen(length);
					}
					return;
				}
			}
			this.records.add(new Record4path(riskMthd, pathStr, length));
		}
	}

}
