package neu.lab.conflict.soot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import abandon.neu.lab.conflict.risk.node.NodeNRisk;
import neu.lab.conflict.util.MavenUtil;
import neu.lab.conflict.vo.MethodCall;
import soot.MethodOrMethodContext;
import soot.Scene;
import soot.SceneTransformer;
import soot.SootClass;
import soot.SootMethod;
import soot.SourceLocator;
import soot.jimple.toolkits.callgraph.CHATransformer;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;
import soot.util.queue.QueueReader;

/**
 * collect risk call caused by throw action
 * 
 * @author asus
 *
 */
public abstract class ThrowRiskTf extends SceneTransformer {
	protected NodeNRisk sootAnaUnit;
	protected Set<MethodCall> riskRlts;
	protected Set<String> riskJarClses;// class in risk jar
	private Set<String> entryClses;
	protected Set<String> thrownMthds;// method only in risk jar

	protected Set<String> riskMthds;

	public void init(NodeNRisk sootAnaUnit, Set<MethodCall> relations, Set<String> thrownMthds) {
		this.sootAnaUnit = sootAnaUnit;
		this.riskRlts = relations;
		this.thrownMthds = thrownMthds;

		entryClses = getJarCls(sootAnaUnit.getTopNode().getFilePath());
		MavenUtil.i().getLog().info("entry-class size:" + entryClses.size());

		riskJarClses = getJarCls(sootAnaUnit.getBottomNode().getFilePath());
		MavenUtil.i().getLog().info("riskJar-class size:" + riskJarClses.size());
	}

	@Override
	protected void internalTransform(String phaseName, Map option) {
		Map<String, String> cgMap = new HashMap<String, String>();
		cgMap.put("enabled", "true");
		cgMap.put("apponly", "true");
		List<SootMethod> entryMthds = new ArrayList<SootMethod>();
		for (SootClass sootClass : Scene.v().getApplicationClasses()) {
			if (entryClses.contains(sootClass.getName())) {// entry class
				for (SootMethod method : sootClass.getMethods()) {
					entryMthds.add(method);
				}
			}
		}
		Scene.v().setEntryPoints(entryMthds);
		CHATransformer.v().transform("wjtp", cgMap);

		CallGraph cg = Scene.v().getCallGraph();
		Iterator<Edge> ite = cg.iterator();
		riskMthds = getRiskMthds();
		while (ite.hasNext()) {
			Edge edge = ite.next();
			if (isRiskCall(edge)) {
				riskRlts.add(new MethodCall(edge.src().getSignature(), edge.tgt().getSignature()));
			}
		}
	}

	/**
	 * when thrown method is reached ,method is risk.
	 * 
	 * @return
	 */
	private Set<String> getRiskMthds() {
		Set<String> riskMthds = new HashSet<String>();
		QueueReader<MethodOrMethodContext> entryRchMthds = Scene.v().getReachableMethods().listener();
		while (entryRchMthds.hasNext()) {
			SootMethod method = entryRchMthds.next().method();
			if (thrownMthds.contains(method.getSignature()))
				riskMthds.add(method.getSignature());
		}
		return riskMthds;
	}

	protected abstract boolean isRiskCall(Edge edge);

	private Set<String> getJarCls(List<String> jarPaths) {
		Set<String> jarClses = new HashSet<String>();
		for (String jarPath : jarPaths) {
			for (String cls : SourceLocator.v().getClassesUnder(jarPath)) {
				jarClses.add(cls);
			}
		}
		return jarClses;
	}
}
