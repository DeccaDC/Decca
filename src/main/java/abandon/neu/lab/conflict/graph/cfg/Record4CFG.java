package abandon.neu.lab.conflict.graph.cfg;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import neu.lab.conflict.graph.IRecord;
import soot.Unit;
import soot.jimple.internal.JInvokeStmt;

public class Record4CFG extends IRecord {
	
	private List<Node4CFG> units;

	public Record4CFG() {
		units = new ArrayList<Node4CFG>();
	}

	public void addPathNode(Node4CFG node) {
		units.add(node);
	}
	
	public Set<String> getMthdsInRecord(){
		Set<String> mthdsInRecord = new HashSet<String>();
		for(Node4CFG node:units) {
			Unit unit = node.getUnit();
			if(unit instanceof JInvokeStmt) {
				JInvokeStmt invoke = (JInvokeStmt)unit;
				mthdsInRecord.add(invoke.getInvokeExpr().getMethod().getSignature());
			}
		}
		return mthdsInRecord;
	}

	@Override
	public Record4CFG clone() {
		Record4CFG clone = new Record4CFG();
		for(Node4CFG unit:units) {
			clone.addPathNode(unit);
		}
		return clone;
	}

}
