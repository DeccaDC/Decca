package abandon.neu.lab.conflict.risk.ref;

import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.dom4j.Element;
import org.dom4j.tree.DefaultElement;

import abandon.neu.lab.conflict.graph.clsref.Graph4ClsRef;
import abandon.neu.lab.conflict.graph.clsref.Record4ClsRefPath;
import abandon.neu.lab.conflict.risk.ref.tb.LimitRefTb;
import neu.lab.conflict.container.AllCls;
import neu.lab.conflict.graph.Dog;
import neu.lab.conflict.graph.IBook;
import neu.lab.conflict.graph.IRecord;
import neu.lab.conflict.util.Conf;
import neu.lab.conflict.vo.NodeAdapter;

public class NodeRRisk {
	private NodeAdapter node;
	private DepJarRRisk jarRisk;
	private LimitRefTb limitRefTb;

	public NodeRRisk(NodeAdapter node1, DepJarRRisk depJarRefRisk) {
		this.node = node1;
		this.jarRisk = depJarRefRisk;
	}

	public Element getRiskEle() {
		return getRiskRefPathEle() ;
	}
	
	private Element getRiskRefEle() {
		Element ele = new DefaultElement("nodeRisk");
		ele.addAttribute("id", node.getWholePath());
		Element clsesEle = ele.addElement("riskClasses");
		for (String riskCls : getlimitRefTb()) {
			Set<String> ers = limitRefTb.getErs(riskCls);
			if (ers.size() > 0) {
				Element clsEle = clsesEle.addElement("riskClass");
				clsEle.addAttribute("name", riskCls);
				clsEle.addAttribute("otherHas", "" + AllCls.i().contains(riskCls));
				for (String erCls : ers) {
					Element sourceEle = clsEle.addElement("source");
					sourceEle.addText(erCls);
				}
			}

		}
		return ele;
	}
	
	private Element getRiskRefPathEle() {
		Element ele = new DefaultElement("nodeRisk");
		ele.addAttribute("id", node.getWholePath());
		Element clsesEle = ele.addElement("riskClasses");
		if(getlimitRefTb().eeSize()>0) {
			Map<String, IBook> books = new Dog(getClsRefGraph()).findRlt(getlimitRefTb().getRefedEes(),Conf.DOG_DEP_FOR_DIS,Dog.Strategy.NOT_RESET_BOOK);
			for (String riskCls : getlimitRefTb()) {
				Set<String> ers = limitRefTb.getErs(riskCls);
				if (ers.size() > 0) {
					Element clsEle = clsesEle.addElement("riskClass");
					clsEle.addAttribute("name", riskCls);
					clsEle.addAttribute("otherHas", "" + AllCls.i().contains(riskCls));
					
					if(books.get(riskCls)!=null) {
						for(IRecord record:books.get(riskCls).getRecords()) {
							Record4ClsRefPath pathRecord = (Record4ClsRefPath)record;
							clsEle.add(pathRecord.getPathEle());
						}
					}
				}
			}
		}
		return ele;
	}

	public Graph4ClsRef getClsRefGraph() {
		return Graph4ClsRef.getGraph(this.node,true);
	}

	private LimitRefTb getlimitRefTb() {
		if (null == limitRefTb) {
			LinkedList<NodeAdapter> ancestors = node.getAncestors(false);// from down to top
			limitRefTb = new LimitRefTb(getThrowedClses());
			for (NodeAdapter node : ancestors) {
				limitRefTb.union(node.getDepJar().getRefTb());
			}
		}
		return limitRefTb;
	}

	private Set<String> getThrowedClses() {
		return jarRisk.getThrowedClses();
	}

}
