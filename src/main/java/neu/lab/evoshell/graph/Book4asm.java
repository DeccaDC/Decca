package neu.lab.evoshell.graph;


import java.util.List;

import org.objectweb.asm.tree.LabelNode;


public class Book4asm extends IBook{

	private LabelNode labelNode;
	
	public Book4asm(Node4asm node) {
		super(node);
		labelNode  = node.getLabelNode();
	}

	@Override
	public void afterAddAllChildren() {
		//GRAPHTODO
	}

	@Override
	public void addChild(IBook doneChildBook) {
		for(IRecord iRecord:doneChildBook.getRecords()) {
			Record4asm record4asm = (Record4asm)iRecord;
			this.records.add(record4asm.clone());
		}
	}
	/**
	 * @param list
	 *            :multiple loop path.path don't includes start and end. e.g.
	 *            if there are A->B->C->A,A->D->E->A ,there B->C,D->E in list.
	 */
	@Override
	public void dealLoop(List<List<String>> list) {
		//GRAPHTODO
		//execute to this method ,path haven't added self.
		for(IRecord iRecord:this.getRecords()) {
			Record4asm record4asm = (Record4asm)iRecord;
			
		}
		
	}
	
}
