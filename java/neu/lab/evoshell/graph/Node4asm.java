package neu.lab.evoshell.graph;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.objectweb.asm.tree.LabelNode;

import neu.lab.evoshell.modify.ExeLabelPath;

public class Node4asm implements INode {
	private String name;
	private LabelNode labelNode;
	private Set<String> nextLabels;

	public Node4asm(LabelNode label) {
		this.name = "" + label.hashCode();
		this.labelNode = label;
		nextLabels = new HashSet<String>();
	}
	

	public LabelNode getLabelNode() {
		return labelNode;
	}


	@Override
	public String getName() {
		return name;
	}

	public void addNextLabel(LabelNode label) {
		nextLabels.add("" + label.hashCode());
	}

	@Override
	public Collection<String> getNexts() {
		return nextLabels;
	}

	@Override
	public IBook getBook() {
		return new Book4asm(this);
	}


	@Override
	public IRecord formNewRecord() {
		Record4asm record = new Record4asm();
		record.addTotail(labelNode);
		return record;
	}


}
