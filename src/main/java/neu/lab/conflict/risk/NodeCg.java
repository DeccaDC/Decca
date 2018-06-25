package neu.lab.conflict.risk;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import neu.lab.conflict.Conf;
import neu.lab.conflict.graph.Book;
import neu.lab.conflict.graph.Dog;
import neu.lab.conflict.graph.Graph;
import neu.lab.conflict.graph.Node;
import neu.lab.conflict.graph.Path;
import neu.lab.conflict.soot.SootCg;
import neu.lab.conflict.vo.MethodCall;
import neu.lab.conflict.vo.NodeAdapter;

/**
 * @author asus
 *
 */
public class NodeCg {
	private LinkedList<NodeAdapter> anaAncestors;// there is order(from down to up)
	private Set<String> rchedMthds;// reached method in call-graph computed
	private Set<String> rchedServices;//

	private Set<String> risk1Mthds;// reached and thrown
	private Set<String> risk2Mthds;// reached and thrown and called by method in other jar.
	private DepJarCg jarRiskAna;
	private Graph graph;

	private Map<String, Book> books;// reached path of method in risk2Mthds

	public NodeCg(NodeAdapter nodeAdapter, DepJarCg jarRiskAna) {
		this.jarRiskAna = jarRiskAna;
		LinkedList<NodeAdapter> ancestors = nodeAdapter.getAncestors(true);
		if (ancestors.size() == 1) {// manageNode that don't have ancestor don‘t need analysis.
			this.anaAncestors = ancestors;
			rchedMthds = new HashSet<String>();
			rchedServices = new HashSet<String>();
			risk1Mthds = new HashSet<String>();
			risk2Mthds = new HashSet<String>();
			graph = new Graph(new HashSet<Node>(), new ArrayList<MethodCall>());
		} else {
			if (Conf.ANA_FROM_HOST) {// entry class is host class.
				this.anaAncestors = ancestors;
			} else {// entry class is up-jar-class.
				this.anaAncestors = new LinkedList<NodeAdapter>();
				this.anaAncestors.add(ancestors.get(0));
				this.anaAncestors.add(ancestors.get(1));
			}
			SootCg.i().cmpCg(this);
		}

	}

	public NodeAdapter getBottomNode() {
		return anaAncestors.getFirst();
	}

	public NodeAdapter getTopNode() {
		return anaAncestors.getLast();
	}

	public List<String> getJarFilePaths() {
		List<String> paths = new ArrayList<String>();
		for (NodeAdapter nodeAdapter : anaAncestors) {
			paths.addAll(nodeAdapter.getFilePath());
		}
		return paths;
	}

	public String getRiskString() {
		List<String> confuseMthds = new ArrayList<String>();
		StringBuilder sb = new StringBuilder("risk for node:");
		sb.append(toString() + "\n");
		sb.append("reached size: " + rchedMthds.size() + " reached_thrown size:" + getRisk1Mthds().size()
				+ " reached_thrown_service:" + getRisk2Mthds().size() + "\n");
		for (String risk2Mthd : risk2Mthds) {
			Book book = getBooks().get(risk2Mthd);
			if (book == null) {
				confuseMthds.add(risk2Mthd);
			} else {
				List<Path> riskPath = book.getRiskPath();
				if (riskPath.size() == 0) {
					// this method is reached by host on soot-call-graph,but can't find path for
					// it.Confusion may be cause by algorithm or by call filter
					confuseMthds.add(risk2Mthd);
				} else {
					sb.append("reached path for:" + risk2Mthd + "\n");
					for (Path path : riskPath) {
						sb.append("---->" + path.toString() + "\n");
					}
				}
			}
		}
		if (Conf.PRINT_CONFUSED_METHOD) {
			if (confuseMthds.size() != 0) {
				sb.append("can't find path for " + confuseMthds.size() + " risk method+\n");
				for (String confuseMthd : confuseMthds) {
					sb.append("confuseMthd:" + confuseMthd + "\n");
				}
			}
		}

		return sb.toString();
	}

	@Override
	public String toString() {
		String str = "";
		for (NodeAdapter nodeAdapter : anaAncestors) {
			str = nodeAdapter.toString() + "->" + str;
		}
		return str;
	}

	public Set<String> getRchedMthds() {
		return rchedMthds;
	}

	public void setRchedMthds(Set<String> rchedMthds) {
		this.rchedMthds = rchedMthds;
	}

	public Set<String> getRisk1Mthds() {
		if (risk1Mthds == null) {
			risk1Mthds = new HashSet<String>();
		}
		return risk1Mthds;
	}

	public Set<String> getRisk2Mthds() {
		if (risk2Mthds == null) {
			risk2Mthds = new HashSet<String>();
		}
		return risk2Mthds;
	}

	public Map<String, Book> getBooks() {
		if (books == null)
			books = new Dog(graph).findRlt(getRisk2Mthds());
		return books;
	}

	public Graph getGraph() {
		return graph;
	}

	public void setGraph(Graph graph) {
		this.graph = graph;
	}

	public Set<String> getRchedServices() {
		return rchedServices;
	}

	public void setRchedServices(Set<String> rchedServices) {
		this.rchedServices = rchedServices;
	}

	public DepJarCg getJarRiskAna() {
		return jarRiskAna;
	}

}
