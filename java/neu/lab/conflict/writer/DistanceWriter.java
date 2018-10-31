package neu.lab.conflict.writer;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import abandon.neu.lab.conflict.distance.ClassDistances;
import abandon.neu.lab.conflict.distance.Dijkstra;
import abandon.neu.lab.conflict.distance.DijkstraMap;
import abandon.neu.lab.conflict.distance.MethodDistances;
import abandon.neu.lab.conflict.distance.NodeDistances;
import abandon.neu.lab.conflict.graph.clsref.Graph4ClsRef;
import abandon.neu.lab.conflict.graph.clsref.Node4ClsRef;
import abandon.neu.lab.conflict.risk.node.DepJarNRisk;
import abandon.neu.lab.conflict.risk.node.NodeNRisk;
import javassist.ClassPool;
import neu.lab.conflict.container.AllCls;
import neu.lab.conflict.container.Conflicts;
import neu.lab.conflict.container.DepJars;
import neu.lab.conflict.util.MavenUtil;
import neu.lab.conflict.vo.Conflict;
import neu.lab.conflict.vo.DepJar;

public class DistanceWriter {

	/**
	 * add throw-jar to the classpool of javassist.
	 * 
	 * @param outPath
	 * @param append
	 */
	public void writeClsDistance(String outPath, boolean append) {
		try {
			// boolean exit = false;
			NodeDistances distances = new ClassDistances();
			PrintWriter printer = new PrintWriter(new FileWriter(outPath, append));
			for (Conflict conflict : Conflicts.i().getConflicts()) {
				for (DepJar depJar : conflict.getDepJars()) {
					if (depJar != conflict.getUsedDepJar()) {
						Collection<String> thrownClses = AllCls.i()
								.getNotInClses(depJar.getOnlyClses(conflict.getUsedDepJar()));
						if (thrownClses.size() > 0) {
							// System.out.println();
							Dijkstra dj = new DijkstraMap(depJar.getWholeClsRefG());
							distances.addDistances(dj.getDistanceTb(thrownClses));
						}
					}
				}
			}
			printer.println(distances.toString());
			printer.close();
		} catch (Exception e) {
			MavenUtil.i().getLog().error("can't write distance risk:", e);
		}

	}

	public void writeMthdDistance(String outPath, boolean append) {
		try {
			// boolean exit = false;
			PrintWriter printer = new PrintWriter(new FileWriter(outPath, append));
			NodeDistances distances = new MethodDistances();
			for (Conflict conflict : Conflicts.i().getConflicts()) {
				for (DepJarNRisk jarRisk : conflict.getNRisk().getJarRiskAnas()) {
					for (NodeNRisk nodeRisk : jarRisk.getNodeRiskAnas()) {
						MavenUtil.i().getLog().info("risk Mthd:" + nodeRisk.getRisk2Mthds().size());
						MavenUtil.i().getLog().info("dj start:");
						Dijkstra dj = new DijkstraMap(nodeRisk.getGraph());
						MavenUtil.i().getLog().info("write graph start:");
						// DebugUtil.i().print("d:\\djGraph.txt", dj.toString(), false);
						StringBuilder sb = new StringBuilder();
						for (String startNd : nodeRisk.getRisk2Mthds()) {
							sb.append(startNd);
							sb.append(System.lineSeparator());
						}
						// DebugUtil.i().print("d:\\startNds.txt", sb.toString(), false);
						MavenUtil.i().getLog().info("write graph end:");
						distances.addDistances(dj.getDistanceTb(nodeRisk.getRisk2Mthds()));
						MavenUtil.i().getLog().info("dj end:");
						// break;
					}
					// break;
				}
				// break;
			}
			printer.println(distances.toString());
			printer.close();
		} catch (Exception e) {
			MavenUtil.i().getLog().error("can't write distance risk:", e);
		}
	}

	/**
	 * add used-jar to the class-pool of java-assist.
	 * 
	 * @param outPath
	 * @param append
	 */
	public void writeOnceClsDistance(String outPath, boolean append) {
		try {
			// get class that not in selected-jars.
			Collection<String> thrownClses = new HashSet<String>();
			for (Conflict conflict : Conflicts.i().getConflicts()) {
				for (DepJar depJar : conflict.getDepJars()) {
					if (depJar != conflict.getUsedDepJar()) {
						thrownClses.addAll(AllCls.i().getNotInClses(depJar.getOnlyClses(conflict.getUsedDepJar())));
					}
				}
			}
			if (thrownClses.size() > 0) {
				// form class-graph
				Graph4ClsRef graph = new Graph4ClsRef();
				ClassPool pool = new ClassPool();
				Set<String> allSysCls = new HashSet<String>();
				for (String thrownCls : thrownClses) {
					graph.addNode(thrownCls);
				}
				for (DepJar jar : DepJars.i().getAllDepJar()) {
					if (jar.isSelected()) {
						for (String path : jar.getJarFilePaths(true)) {
							pool.appendClassPath(path);
						}
						for (String jarCls : jar.getAllCls(true)) {
							graph.addNode(jarCls);
							allSysCls.add(jarCls);
						}
					}
				}
				for (String sysCls : allSysCls) {// each er
					for (Object ee : pool.get(sysCls).getRefClasses()) {
						if (!sysCls.equals(ee)) {// don't add relation of self.
							Node4ClsRef node = (Node4ClsRef) graph.getNode((String) ee);
							if (node != null)
								node.addInCls(sysCls);
						}
					}
				}
				// calculate distance
				Dijkstra dj = new DijkstraMap(graph);
				NodeDistances distances = new ClassDistances();
				distances.addDistances(dj.getDistanceTb(thrownClses));
				PrintWriter printer = new PrintWriter(new FileWriter(outPath, append));
				printer.println(distances.toString());
				printer.close();
			}

		} catch (Exception e) {
			MavenUtil.i().getLog().error("can't write distance risk:", e);
		}

	}

	/**
	 * D:\cWS\eclipse1\testcase.top\pom.xml,
	 * neu.lab.testcase.middle.ClassMiddle,
	 * D:\cEnvironment\repository\neu\lab\testcase.middle\1.0\testcase.middle-1.0.jar
	 * 
	 * @param outPath
	 * @param append
	 */
	public void writeNeibor(String outPath, boolean append) {
		try {
			// get class that not in selected-jars.
			Collection<String> thrownClses = new HashSet<String>();
			for (Conflict conflict : Conflicts.i().getConflicts()) {
				for (DepJar depJar : conflict.getDepJars()) {
					if (depJar != conflict.getUsedDepJar()) {
						thrownClses.addAll(AllCls.i().getNotInClses(depJar.getOnlyClses(conflict.getUsedDepJar())));
					}
				}
			}
			if (thrownClses.size() > 0) {
				// form class-graph
				Graph4ClsRef graph = new Graph4ClsRef();
				ClassPool pool = new ClassPool();
				Set<String> allSysCls = new HashSet<String>();
				for (String thrownCls : thrownClses) {
					graph.addNode(thrownCls);
				}
				for (DepJar jar : DepJars.i().getAllDepJar()) {
					if (jar.isSelected()) {
						for (String path : jar.getJarFilePaths(true)) {
							pool.appendClassPath(path);
						}
						for (String jarCls : jar.getAllCls(true)) {
							graph.addNode(jarCls);
							allSysCls.add(jarCls);
						}
					}
				}
				for (String sysCls : allSysCls) {// each er
					for (Object ee : pool.get(sysCls).getRefClasses()) {
						if (!sysCls.equals(ee)) {// don't add relation of self.
							Node4ClsRef node = (Node4ClsRef) graph.getNode((String) ee);
							if (node != null)
								node.addInCls(sysCls);
						}
					}
				}
				// calculate distance
				Dijkstra dj = new DijkstraMap(graph);
				Map<String,Map<String,Double>> b2t2d = dj.getDistanceTb(thrownClses);
				//map class to jarpath
				Map<String,String> cls2jarPath = new HashMap<String,String>();
				for (DepJar depJar : DepJars.i().getAllDepJar()) {
					if (depJar.isSelected()) {
						for(String cls:depJar.getAllCls(true)) {
							cls2jarPath.put(cls, depJar.getJarFilePaths(true).get(0));
						}
					}
				}
				//write result
				PrintWriter printer = new PrintWriter(new FileWriter(outPath, append));
				String pomPath = MavenUtil.i().getBaseDir().getAbsolutePath()+"\\pom.xml";
				//get all bottom that can be reached by hostNode.
				Set<String> rchedBottom = new HashSet<String>();
				for(String bottom:b2t2d.keySet()) {
					Map<String,Double> t2d = b2t2d.get(bottom);
					for(String top:t2d.keySet()) {
						if(MavenUtil.i().isHostClass(top)&&!t2d.get(top).equals(Double.MAX_VALUE)) {
							rchedBottom.add(bottom);
						}
					}
				}
				for(String bottom:rchedBottom) {
					Map<String,Double> t2d = b2t2d.get(bottom);
					for(String top:t2d.keySet()) {
						if(t2d.get(top)==1) {
							printer.println(pomPath+","+top+","+cls2jarPath.get(top));
						}
					}
				}
				
				printer.close();
			}

		} catch (Exception e) {
			MavenUtil.i().getLog().error("can't write distance risk:", e);
		}

	}
}
