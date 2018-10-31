package neu.lab.evoshell.graph;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.tree.LabelNode;

public class Record4asm extends IRecord {
	private LinkedList<LabelNode> nodes;

	public Record4asm() {
		nodes = new LinkedList<LabelNode>();
	}

	public void addFirst(LabelNode newNode) {
		nodes.addFirst(newNode);
	}

	public void addTotail(LabelNode newNode) {
		nodes.add(newNode);
	}

	@Override
	public Record4asm clone() {
		Record4asm clone = new Record4asm();
		for (LabelNode node : nodes) {
			clone.addTotail(node);
		}
		return clone;
	}

	public LabelNode getLast() {
		return nodes.peekLast();
	}

	public LabelNode getFirst() {
		return nodes.peekFirst();
	}

	public boolean contains(LabelNode node) {
		return nodes.contains(node);
	}

	private int getLabelPos(LabelNode labelNode) {
		int pos = -1;
		for (int i = 0; i < this.nodes.size(); i++) {
			if (labelNode == this.nodes.get(i)) {
				pos = i;
				break;
			}
		}
		return pos;
	}

	public int getLabelPos(List<LabelNode> labels) {
		int pos = -1;
		for (LabelNode label : labels) {
			int newPos = getLabelPos(label);
			if (newPos != -1) {
				if (pos == -1 || newPos < pos) {
					pos = newPos;
				}
			}
		}
		return pos;
	}

	public String getPathStr(Map<LabelNode, Integer> label2num) {
		StringBuilder sb = new StringBuilder();
		for (LabelNode node : this.nodes) {
			sb.append(label2num.get(node) + "->");
		}
		return sb.toString();
	}

	public String getPathStr() {
		StringBuilder sb = new StringBuilder();
		for (LabelNode node : this.nodes) {
			sb.append(node + "->\n");
		}
		return sb.toString();
	}
}
