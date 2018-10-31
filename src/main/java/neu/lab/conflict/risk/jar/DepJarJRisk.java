package neu.lab.conflict.risk.jar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import abandon.neu.lab.conflict.distance.MethodProbDistances;
import neu.lab.conflict.GlobalVar;
import neu.lab.conflict.container.DepJars;
import neu.lab.conflict.graph.Dog;
import neu.lab.conflict.graph.Graph4distance;
import neu.lab.conflict.graph.Graph4path;
import neu.lab.conflict.graph.IBook;
import neu.lab.conflict.graph.IGraph;
import neu.lab.conflict.graph.IRecord;
import neu.lab.conflict.graph.Node4distance;
import neu.lab.conflict.graph.Record4distance;
import neu.lab.conflict.soot.SootJRiskCg;
import neu.lab.conflict.soot.SootRiskMthdFilter;
import neu.lab.conflict.soot.SootRiskMthdFilter2;
import neu.lab.conflict.soot.tf.JRiskDistanceCgTf;
import neu.lab.conflict.util.Conf;
import neu.lab.conflict.util.MavenUtil;
import neu.lab.conflict.vo.DepJar;
import neu.lab.conflict.vo.MethodCall;

/**
 * 依赖风险jar
 * 
 * @author wangchao
 *
 */
public class DepJarJRisk {
	private DepJar depJar; // 依赖jar
	private ConflictJRisk conflictRisk; // 有风险的冲突jar
	private Set<String> thrownMthds; // 抛弃的方法
	// private Set<String> rchedMthds;
	private Graph4distance graph4distance; // 图
	private Map<String, IBook> books; // book记录用

	/*
	 * 构造函数
	 */
	public DepJarJRisk(DepJar depJar, ConflictJRisk conflictRisk) {
		this.depJar = depJar;
		this.conflictRisk = conflictRisk;
		// calculate thrownMthd

		// calculate call-graph

	}

	/*
	 * 得到版本
	 */
	public String getVersion() {
		return depJar.getVersion();
	}

	/**
	 * 得到抛弃的方法
	 * 
	 * @return
	 */
	public Set<String> getThrownMthds() {
		// "<neu.lab.plug.testcase.homemade.host.prob.ProbBottom: void m()>"

		// if (thrownMthds == null) {

		// //TODO1
		// thrownMthds = new HashSet<String>();
		// thrownMthds.add("<com.fasterxml.jackson.core.JsonFactory: boolean
		// requiresPropertyOrdering()>");

		thrownMthds = conflictRisk.getUsedDepJar().getRiskMthds(depJar.getAllMthd());
		MavenUtil.i().getLog().info("riskMethod size before filter: " + thrownMthds.size());
//			MavenUtil.i().getLog().info("contains : " + thrownMthds.contains("<com.fasterxml.jackson.databind.node.JsonNodeFactory: com.fasterxml.jackson.databind.node.NumericNode numberNode(java.math.BigInteger)>"));
		if (thrownMthds.size() > 0)
			new SootRiskMthdFilter().filterRiskMthds(thrownMthds);
		MavenUtil.i().getLog().info("riskMethod size after filter1: " + thrownMthds.size());
////			MavenUtil.i().getLog().info("contains : " + thrownMthds.contains("<com.fasterxml.jackson.databind.node.JsonNodeFactory: com.fasterxml.jackson.databind.node.NumericNode numberNode(java.math.BigInteger)>"));
		if (thrownMthds.size() > 0)
			new SootRiskMthdFilter2().filterRiskMthds(this, thrownMthds);
		MavenUtil.i().getLog().info("riskMethod size after filter2: " + thrownMthds.size());
//		MavenUtil.i().getLog().info("contains : " + thrownMthds.contains(
//				"<org.apache.http.impl.client.CloseableHttpClient: org.apache.http.client.methods.CloseableHttpResponse execute(org.apache.http.HttpHost,org.apache.http.HttpRequest,org.apache.http.protocol.HttpContext)>"));
//		MavenUtil.i().getLog().info("contains : " + thrownMthds.contains(
//				"<org.apache.http.impl.client.HttpClientBuilder: org.apache.http.impl.client.CloseableHttpClient build()>"));
		// //TODO1
		// if(thrownMthds.contains("<com.fasterxml.jackson.core.JsonFactory: boolean
		// requiresPropertyOrdering()>")) {
		// MavenUtil.i().getLog().info("thronMethods has
		// <com.fasterxml.jackson.core.JsonFactory: boolean
		// requiresPropertyOrdering()>");
		// }

		// }
		return thrownMthds;
	}

	/**
	 * 用传入的depJar去得到抛弃的方法
	 * 
	 * @param depJar
	 * @return
	 */
	public Set<String> getThrownMthds(DepJar enterDepJar) {
		thrownMthds = conflictRisk.getUsedDepJar().getRiskMthds(depJar.getAllMthd());
		MavenUtil.i().getLog().info("riskMethod size before filter: " + thrownMthds.size());
//		MavenUtil.i().getLog().info("contains : " + thrownMthds.contains("<com.fasterxml.jackson.databind.node.JsonNodeFactory: com.fasterxml.jackson.databind.node.NumericNode numberNode(java.math.BigInteger)>"));
		if (thrownMthds.size() > 0)
				new SootRiskMthdFilter().filterRiskMthds(thrownMthds, enterDepJar);
		MavenUtil.i().getLog().info("riskMethod size after filter1: " + thrownMthds.size());
//		MavenUtil.i().getLog().info("contains : " + thrownMthds.contains("<com.fasterxml.jackson.databind.node.JsonNodeFactory: com.fasterxml.jackson.databind.node.NumericNode numberNode(java.math.BigInteger)>"));
		if (thrownMthds.size() > 0)
				new SootRiskMthdFilter2().filterRiskMthds(this, thrownMthds);
		MavenUtil.i().getLog().info("riskMethod size after filter2: " + thrownMthds.size());
		MavenUtil.i().getLog().info("contains : " + thrownMthds.contains(
				"<org.apache.http.impl.client.CloseableHttpClient: org.apache.http.client.methods.CloseableHttpResponse execute(org.apache.http.HttpHost,org.apache.http.HttpRequest,org.apache.http.protocol.HttpContext)>"));
		MavenUtil.i().getLog().info("contains : " + thrownMthds.contains(
				"<org.apache.http.impl.client.HttpClientBuilder: org.apache.http.impl.client.CloseableHttpClient build()>"));
		return thrownMthds;
	}

	public MethodProbDistances getMethodProDistances() {
		MethodProbDistances distances = new MethodProbDistances();
		Map<String, IBook> books = getBooks4distance();
		for (IBook book : books.values()) {
			// MavenUtil.i().getLog().info("book:"+book.getNodeName());
			for (IRecord iRecord : book.getRecords()) {

				Record4distance record = (Record4distance) iRecord;
				// MavenUtil.i().getLog().info("record:"+record.getName());
				distances.addDistance(record.getName(), book.getNodeName(), record.getDistance());
				distances.addProb(record.getName(), book.getNodeName(), record.getBranch());
			}
		}
		return distances;
	}

	public MethodProbDistances getMethodProDistances(Map<String, IBook> books) {
		MethodProbDistances distances = new MethodProbDistances();
		for (IBook book : books.values()) {
			// MavenUtil.i().getLog().info("book:"+book.getNodeName());
			for (IRecord iRecord : book.getRecords()) {

				Record4distance record = (Record4distance) iRecord;
				// MavenUtil.i().getLog().info("record:"+record.getName());
				distances.addDistance(record.getName(), book.getNodeName(), record.getDistance());
				distances.addProb(record.getName(), book.getNodeName(), record.getBranch());
			}
		}
		return distances;
	}

	public Set<String> getMethodBottom(Map<String, IBook> books) {
		Set<String> bottomMethods = new HashSet<String>();
		for (IBook book : books.values()) {
			// MavenUtil.i().getLog().info("book:"+book.getNodeName());
			for (IRecord iRecord : book.getRecords()) {
				Record4distance record = (Record4distance) iRecord;
				bottomMethods.add(record.getName());
			}
		}
		return bottomMethods;
	}

	public Collection<String> getPrcDirPaths() throws Exception {
		List<String> classpaths;
		if (GlobalVar.useAllJar) {
			classpaths = depJar.getRepalceCp();
		} else {
			MavenUtil.i().getLog().info("not add all jar to process");
			classpaths = new ArrayList<String>();
			// keep first is self
			classpaths.addAll(this.depJar.getJarFilePaths(true));
			classpaths.addAll(this.depJar.getFatherJarCps(false));

		}

//		MavenUtil.i().getLog().info("classpath for "+this.toString());
//		for(String path:classpaths) {
//			System.out.println("argsList.add(\"-process-dir\");");
//			System.out.println("argsList.add(\"" + path.replace("\\", "\\\\") + "\");");
//		}
//		
		return classpaths;

	}

	public DepJar getEntryJar() {
		return DepJars.i().getHostDepJar();
	}

	public DepJar getConflictJar() {
		return depJar;
	}

	/**
	 * 得到距离图
	 * 
	 * @return
	 */
	public Graph4distance getGraph4distance() {
		// if (graph4distance == null) {
		Set<String> thrownmethods = getThrownMthds();
		if (thrownmethods.size() > 0) {
//				for(String riskMthd:getThrownMthds()) {
//					System.out.println(riskMthd);
//				}
//			for (String mthd : getThrownMthds()) {
//				MavenUtil.i().getLog().info("first riskmthd:" + mthd);
//				// 测试
//			}
			// MavenUtil.i().getLog().info("first riskmthd:" +
			// getThrownMthds().iterator().next());
			IGraph iGraph = SootJRiskCg.i().getGraph4distance(this, new JRiskDistanceCgTf(this, thrownmethods));
			if (iGraph != null) {
				graph4distance = (Graph4distance) iGraph;
			} else {
				graph4distance = new Graph4distance(new HashMap<String, Node4distance>(), new ArrayList<MethodCall>());
			}
		} else {
			graph4distance = new Graph4distance(new HashMap<String, Node4distance>(), new ArrayList<MethodCall>());
		}
		// }
		return graph4distance;
	}

	/**
	 * 得到距离图 多态
	 * 
	 * @return
	 */
	public Graph4distance getGraph4distance(DepJar useDepJar) {
		// if (graph4distance == null) {
		Set<String> thrownmethods = getThrownMthds(useDepJar);
		if (thrownmethods.size() > 0) {
//				for(String riskMthd:getThrownMthds()) {
//					System.out.println(riskMthd);
//				}
//			for (String mthd : getThrownMthds()) {
//				MavenUtil.i().getLog().info("first riskmthd:" + mthd);
//				// 测试
//			}
			// MavenUtil.i().getLog().info("first riskmthd:" +
			// getThrownMthds().iterator().next());
			IGraph iGraph = SootJRiskCg.i().getGraph4distance(this, new JRiskDistanceCgTf(this, thrownmethods));
			if (iGraph != null) {
				graph4distance = (Graph4distance) iGraph;
			} else {
				graph4distance = new Graph4distance(new HashMap<String, Node4distance>(), new ArrayList<MethodCall>());
			}
		} else {
			graph4distance = new Graph4distance(new HashMap<String, Node4distance>(), new ArrayList<MethodCall>());
		}
		// }
		return graph4distance;
	}

	public Graph4path getGraph4mthdPath() {
//		if (getThrownMthds().size() > 0) {
//			IGraph iGraph = SootJRiskCg.i().getGraph4branch(this,new JRiskMthdPathCgTf(this));
//			if(iGraph!=null)
//				return (Graph4path)iGraph;
//		}
//		return new Graph4path(new HashMap<String, Node4path>(), new ArrayList<MethodCall>());
		return getGraph4distance().getGraph4path();
	}

	private Map<String, IBook> getBooks4distance() {
		if (this.books == null) {
			if (getThrownMthds().size() > 0) {
				// calculate distance

				books = new Dog(getGraph4distance()).findRlt(getGraph4distance().getHostNds(), Conf.DOG_DEP_FOR_DIS,
						Dog.Strategy.NOT_RESET_BOOK);

//				GraphPrinter.printGraph(graph4branch, "d:\\graph_distance.txt",getGraph4branch().getHostNds());
			} else {
				books = new HashMap<String, IBook>();
			}
		}
		return books;
	}

	@Override
	public String toString() {
		return depJar.toString() + " in conflict " + conflictRisk.getConflict().toString();
	}

}
