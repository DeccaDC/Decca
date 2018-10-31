package abandon.neu.lab.conflict.graph.cfg;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import neu.lab.conflict.graph.Dog;
import neu.lab.conflict.graph.Dog.Strategy;
import neu.lab.conflict.graph.IBook;
import neu.lab.conflict.graph.IGraph;
import neu.lab.conflict.graph.INode;
import neu.lab.conflict.graph.IRecord;
import soot.Unit;
import soot.toolkits.graph.BriefUnitGraph;

public class Graph4CFG implements IGraph {

	private Map<String, Node4CFG> name2node;// name is hashCode of unit.
	private List<String> entries;

	public Graph4CFG(BriefUnitGraph unitGraph) {
		name2node = new HashMap<String, Node4CFG>();
		entries = new ArrayList<String>();
		// initial node
		initNode(unitGraph);
		// initial edge
		initEdge(unitGraph);
		// initial entries
		initEntry(unitGraph);
	}

	public Map<String,Double> getMthd2Prob(){
		Map<String,IBook> books = new Dog(this).findRlt(entries, Integer.MAX_VALUE,Strategy.NOT_RESET_BOOK );
		Map<String,Integer> mthd2cnt = new HashMap<String,Integer>();//key:method;value:counter of method existing in path.
		double pathCnt = 0.0;
		//get count.
		for(String unitHash:entries) {
			Book4CFG book = (Book4CFG)books.get(unitHash);
			for(IRecord iRecord:book.getRecords()) {
				pathCnt++;
				Record4CFG record = (Record4CFG)iRecord;
				for(String mthd:record.getMthdsInRecord()) {
					Integer cnt = mthd2cnt.get(mthd);
					if(cnt==null) {
						mthd2cnt.put(mthd, 1);
					}else {
						mthd2cnt.put(mthd, cnt++);
					}
				}
			}
		}
		//get probability
		Map<String,Double> mthd2prob = new HashMap<String,Double>();

		return mthd2prob;
	}

	private void initEntry(BriefUnitGraph unitGraph) {
		for (Unit entryUnit : unitGraph.getHeads()) {
			entries.add("" + entryUnit.hashCode());
		}
	}

	private void initEdge(BriefUnitGraph unitGraph) {
		Iterator<Unit> unitIte = unitGraph.iterator();
		while (unitIte.hasNext()) {
			Unit unit = unitIte.next();
			for (Unit suc : unitGraph.getSuccsOf(unit)) {
				name2node.get("" + unit.hashCode()).addNext("" + suc.hashCode());
			}
		}
	}

	private void initNode(BriefUnitGraph unitGraph) {
		Iterator<Unit> unitIte = unitGraph.iterator();
		while (unitIte.hasNext()) {
			Unit unit = unitIte.next();
			name2node.put("" + unit.hashCode(), new Node4CFG(unit));
		}
	}

	@Override
	public INode getNode(String nodeName) {
		return name2node.get(nodeName);
	}

	@Override
	public Collection<String> getAllNode() {
		return name2node.keySet();
	}

}
