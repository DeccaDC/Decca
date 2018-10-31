package neu.lab.conflict.graph;

import java.util.Collection;

public interface INode {
	
	public String getName();
	
	public Collection<String> getNexts();//next nodes that dog should go when writes book about this node.
	
	public IBook getBook();
	
	//if this node is a end node,node should form a new record.Else nodes change the copy of end node.
	//call by afterAddAllChildren.
	public IRecord formNewRecord();
	
	
}
