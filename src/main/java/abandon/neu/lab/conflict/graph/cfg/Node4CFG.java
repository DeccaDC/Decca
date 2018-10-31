package abandon.neu.lab.conflict.graph.cfg;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import abandon.neu.lab.conflict.graph.path1.Node4Path;
import neu.lab.conflict.graph.IBook;
import neu.lab.conflict.graph.IRecord;
import soot.Unit;

public class Node4CFG extends Node4Path {

	private Unit unit;
	private List<String> nextUnits;

	public Node4CFG(Unit unit) {
		this.unit = unit;
		nextUnits = new ArrayList<String>();
	}

	@Override
	public String getName() {
		return "" + this.unit.hashCode();
	}

	@Override
	public Collection<String> getNexts() {
		return nextUnits;
	}

	public void addNext(String nextUnit) {
		nextUnits.add(nextUnit);
	}
	
	public Unit getUnit() {
		return unit;
	}

	@Override
	public IBook getBook() {
		return new Book4CFG(this);
	}

	@Override
	public IRecord formNewRecord() {
		Record4CFG record = new Record4CFG();
		record.addPathNode(this);
		return record;
	}

}
