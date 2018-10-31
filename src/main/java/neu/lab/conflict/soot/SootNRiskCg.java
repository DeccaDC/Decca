package neu.lab.conflict.soot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import abandon.neu.lab.conflict.graph.path1.Graph4MthdPath;
import abandon.neu.lab.conflict.graph.path1.Node4MthdPath1;
import abandon.neu.lab.conflict.risk.node.NodeNRisk;
import neu.lab.conflict.util.SootUtil;
import neu.lab.conflict.util.Conf;
import neu.lab.conflict.util.MavenUtil;
import neu.lab.conflict.vo.DepJar;
import neu.lab.conflict.vo.MethodCall;
import soot.MethodOrMethodContext;
import soot.PackManager;
import soot.Scene;
import soot.SceneTransformer;
import soot.SootClass;
import soot.SootMethod;
import soot.Transform;
import soot.jimple.toolkits.callgraph.CHATransformer;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;
import soot.util.queue.QueueReader;

public class SootNRiskCg extends SootAna {
	public static long runtime = 0;
	private static SootNRiskCg instance = new SootNRiskCg();

	private SootNRiskCg() {

	}

	public static SootNRiskCg i() {
		return instance;
	}

	public void cmpCg(NodeNRisk nodeAnaUnit) {
		MavenUtil.i().getLog().info("use soot to compute reach methods for " + nodeAnaUnit.toString());

		try {
			long startTime = System.currentTimeMillis();

			NRiskCgTf transformer = new NRiskCgTf(nodeAnaUnit);
			PackManager.v().getPack("wjtp").add(new Transform("wjtp.myTrans", transformer));

			SootUtil.modifyLogOut();

			soot.Main.main(getArgs(nodeAnaUnit.getPrcDirPaths().toArray(new String[0])).toArray(new String[0]));

			nodeAnaUnit.setRchedMthds(transformer.getRchMthds());

			nodeAnaUnit.setGraph(transformer.getGraph());

			nodeAnaUnit.setRchedServices(transformer.getRchServices());

			soot.G.reset();

			runtime = runtime + (System.currentTimeMillis() - startTime) / 1000;
		} catch (Exception e) {
			MavenUtil.i().getLog().info("don't have entry for:" + nodeAnaUnit.toString());
			nodeAnaUnit.setRchedMthds(new HashSet<String>());

			nodeAnaUnit.setGraph(new Graph4MthdPath(new HashSet<Node4MthdPath1>(), new ArrayList<MethodCall>()));

			nodeAnaUnit.setRchedServices(new HashSet<String>());
			soot.G.reset();
		}

	}

	@Override
	protected void addCgArgs(List<String> argsList) {
		argsList.addAll(Arrays.asList(new String[] { "-p", "cg", "off", }));
	}

}

class NRiskCgTf extends SceneTransformer {

	private NodeNRisk nodeRiskAna;
	private Set<String> entryClses;
	private Set<String> conflictJarClses;// classes in duplicated jar

	private Set<String> rchMthds;// in conflict-jar,reached from entry
	private Set<String> rchServices;// in conflict-jar,reached from entry,called by other-jar-method

	// private Set<String> thrownMthds;
	// private Set<String> risk1Mthds;// thrown,rch from host
	//
	// private Set<String> risk2Mthds;// reached and thrown and called by method in
	// other jar.
	// private List<MethodCall> riskCalls;// source is from other jar,target is to
	// risk1Mthds.

	public NRiskCgTf(NodeNRisk nodeRiskAna) throws Exception {
		super();
		this.nodeRiskAna = nodeRiskAna;
		this.rchMthds = new HashSet<String>();
		this.rchServices = new HashSet<String>();
		// riskCalls = new ArrayList<MethodCall>();
		// risk1Mthds = new HashSet<String>();
		// risk2Mthds = new HashSet<String>();

//		entryClses.addAll(SootUtil.getJarsClasses(nodeRiskAna.getTopNode().getFilePath()));
		entryClses = nodeRiskAna.getTopNode().getDepJar().getAllCls(true);
		if (entryClses.size() == 0) {
			throw new Exception("don't have entry!");
		}

		conflictJarClses = new HashSet<String>();
//		conflictJarClses.addAll(SootUtil.getJarsClasses(nodeRiskAna.getBottomNode().getFilePath()));
		conflictJarClses.addAll(nodeRiskAna.getBottomNode().getDepJar().getAllCls(true));
		MavenUtil.i().getLog()
				.info("entry-Class size:" + entryClses.size() + " duplicate-class size:" + conflictJarClses.size());
	}

	@Override
	protected void internalTransform(String phaseName, Map<String, String> options) {
		MavenUtil.i().getLog().info("NRiskCgTf start..");
		DepJar depJar = nodeRiskAna.getJarRiskAna().getDepJar();
		if (!depJar.hasClsTb()) {
			depJar.setClsTb(SootUtil.getClassTb(depJar.getJarFilePaths(true)));
		}
		Map<String, String> cgMap = new HashMap<String, String>();
		cgMap.put("enabled", "true");
		cgMap.put("apponly", "true");
		
		List<SootMethod> entryMthds = new ArrayList<SootMethod>();
		for (SootClass sootClass : Scene.v().getApplicationClasses()) {
//			MavenUtil.i().getLog().info(sootClass.getName());
			if (entryClses.contains(sootClass.getName())) {// entry class
				for (SootMethod method : sootClass.getMethods()) {
					entryMthds.add(method);
				}
			}
		}

		Scene.v().setEntryPoints(entryMthds);
		CHATransformer.v().transform("wjtp", cgMap);

		QueueReader<MethodOrMethodContext> entryRchMthds = Scene.v().getReachableMethods().listener();

		while (entryRchMthds.hasNext()) {
			SootMethod method = entryRchMthds.next().method();
			if (isInConflictJar(method)) {// is method in duplicate jar
				rchMthds.add(method.getSignature());
			}

		}
		MavenUtil.i().getLog().debug("soot reachMethod size:" + Scene.v().getReachableMethods().size()
				+ ";reachedMethod in jar size:" + rchMthds.size());

		CallGraph cg = Scene.v().getCallGraph();
		Iterator<Edge> ite = cg.iterator();
		while (ite.hasNext()) {
			Edge edge = ite.next();
			if (rchMthds.contains(edge.tgt().getSignature()) && !isInConflictJar(edge.src())
					&& isInConflictJar(edge.tgt())) {
				rchServices.add(edge.tgt().getSignature());
			}
		}
		MavenUtil.i().getLog().info("NRiskCgTf end.");
	}

	public Graph4MthdPath getGraph() {
		Set<Node4MthdPath1> nds = new HashSet<Node4MthdPath1>();
		List<MethodCall> calls = new ArrayList<MethodCall>();

		// form calls and nds
		CallGraph cg = Scene.v().getCallGraph();
		Iterator<Edge> ite = cg.iterator();
		while (ite.hasNext()) {
			
			Edge edge = ite.next();
			if (Conf.FLT_INTERFACE) {
				if (edge.kind().name().equals("INTERFACE"))
					continue;
			}

			String srcClsName = edge.src().getDeclaringClass().getName();
			String tgtClsName = edge.tgt().getDeclaringClass().getName();
			String srcMthdName = edge.src().getSignature();
			String tgtMthdName = edge.tgt().getSignature();
			
//			MavenUtil.i().getLog().info(srcMthdName+"->"+tgtMthdName);

			calls.add(new MethodCall(srcMthdName, tgtMthdName));
			nds.add(new Node4MthdPath1(srcMthdName, entryClses.contains(srcClsName), conflictJarClses.contains(srcClsName)));
			nds.add(new Node4MthdPath1(tgtMthdName, entryClses.contains(tgtClsName), conflictJarClses.contains(tgtClsName)));

		}

		return new Graph4MthdPath(nds, calls);
	}

	private boolean isInConflictJar(SootMethod method) {
		if (conflictJarClses.contains(method.getDeclaringClass().getName())) {
			return true;
		}
		return false;
	}

	public Set<String> getRchMthds() {
		return rchMthds;
	}

	public Set<String> getRchServices() {
		return rchServices;
	}

	//
	// public List<MethodCall> getRiskCalls() {
	// return riskCalls;
	// }
	//
	// public Set<String> getRisk1Mthds() {
	// return risk1Mthds;
	// }
	//
	// public Set<String> getRisk2Mthds() {
	// return risk2Mthds;
	// }

}
