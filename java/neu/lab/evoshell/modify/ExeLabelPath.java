package neu.lab.evoshell.modify;

import java.util.List;
import java.util.Map;

import org.objectweb.asm.tree.LabelNode;

public interface ExeLabelPath {

	public LabelNode getLast();

	public LabelNode getFirst();

	public boolean contains(LabelNode node);

	public void addToFirst(LabelNode newNode);

	public void addTotail(LabelNode newNode);

	public int getLabelPos(List<LabelNode> labels);

	public String getPathStr(Map<LabelNode, Integer> label2num);

	public String getPathStr();
}
