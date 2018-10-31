package neu.lab.conflict.graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


import soot.PackManager;
import soot.Scene;
import soot.SceneTransformer;
import soot.SootClass;
import soot.SootMethod;
import soot.SourceLocator;
import soot.Transform;
import soot.Unit;
import soot.jimple.internal.JInvokeStmt;
import soot.toolkits.graph.BriefUnitGraph;

public class CfgGraphTest {

	public static void main(String[] args) {
		String path = "";
		String binPath = path + "\\plug.testcase.homemade.host-1.0.jar";

		List<String> argsList = getArgs(binPath);
		PackManager.v().getPack("wjtp").add(new Transform("wjtp.myTrans", new TestTransformer()));

		args = argsList.toArray(new String[0]);
		Long startTime = System.currentTimeMillis();
		soot.Main.main(args);// 解析系统中存在的node以及node之间的关系


	}

	private static List<String> getArgs(String path) {
		// String[] jarFilePaths = Detective.findJarPath(new File(path)).toArray(new
		// String[0]);
		// String[] jarFilePaths = { "D:\\cTestWs\\jxpath\\main\\commons-jxpath-1.3.jar"
		// };
		List<String> argsList = new ArrayList<String>();
		argsList.add("-process-dir");
		argsList.add("D:\\cWS\\eclipse1\\CgCall\\bin");
		// argsList.add(path);
		addGenArg(argsList);
		addCgArgs(argsList);
		addIgrArgs(argsList);

		return argsList;
	}

	protected static void addCgArgs(List<String> argsList) {
		argsList.addAll(Arrays.asList(new String[] { "-p", "cg", "off", }));

		// argsList.addAll(Arrays.asList(new String[] { "-p", "cg",
		// "all-reachable:true", }));// 对所有的appclass进行调用分析
		// argsList.addAll(Arrays.asList(new String[] { "-p", "cg.cha", "apponly:true",
		// }));
	}

	private static void addClassPath(List<String> argsList, String[] jarFilePaths) {
		for (String jarFilePath : jarFilePaths) {
			argsList.add("-process-dir");
			argsList.add(jarFilePath);
		}
	}

	protected static void addGenArg(List<String> argsList) {

		// argsList.add("-pp");// 将soot的classPath中的类用于解析
		argsList.add("-ire");// 忽略classPath中的无效实体
		argsList.add("-app");// 所有的类都将作为appClass
		argsList.add("-allow-phantom-refs");// 允许无效的类型解析
		argsList.add("-w");// 整个项目解析

	}

	protected static void addIgrArgs(List<String> argsList) {
		argsList.addAll(Arrays.asList(new String[] { "-p", "wjop", "off", }));
		argsList.addAll(Arrays.asList(new String[] { "-p", "wjap", "off", }));
		argsList.addAll(Arrays.asList(new String[] { "-p", "jtp", "off", }));
		argsList.addAll(Arrays.asList(new String[] { "-p", "jop", "off", }));
		argsList.addAll(Arrays.asList(new String[] { "-p", "jap", "off", }));
		argsList.addAll(Arrays.asList(new String[] { "-p", "bb", "off", }));
		argsList.addAll(Arrays.asList(new String[] { "-p", "tag", "off", }));
		argsList.addAll(Arrays.asList(new String[] { "-f", "n", }));// 关闭文件的输出
	}
}



class TestTransformer extends SceneTransformer {

	public TestTransformer() {
		super();
	}

	@Override
	protected void internalTransform(String phaseName, Map option) {
		Map<String, String> cgMap = new HashMap<String, String>();
		cgMap.put("enabled", "true");
		cgMap.put("apponly", "true");
		cgMap.put("all-reachable", "true");
		// List<SootMethod> entryMthds = new ArrayList<SootMethod>();
		for (SootClass sootClass : Scene.v().getApplicationClasses()) {
			for (SootMethod method : sootClass.getMethods()) {
				System.out.println(method.getSignature());
			}
		}
//		<pck1.Entrance: void main(java.lang.String[])>
//		<pck1.Entrance: void m1(pck1.Inter)>
//		<pck1.Entrance: void m3(java.lang.String)>
		SootMethod method = Scene.v().getMethod("<pck1.CfgTest: void m4()>");
		BriefUnitGraph graph = new BriefUnitGraph(method.retrieveActiveBody());
		for (Unit head : graph.getHeads()) {
			System.out.println("head " + head);
		}
		Iterator<Unit> unitIte = graph.iterator();
		while (unitIte.hasNext()) {
			Unit unit = unitIte.next();
			System.out.println("====" + unit + " " + unit.getClass().getName()+" "+unit.hashCode());
			if(unit instanceof JInvokeStmt) {
				JInvokeStmt invoke = (JInvokeStmt)unit;
				System.out.println(invoke.getInvokeExpr().getMethod().getSignature());
			}

//			for (Unit pre : graph.getPredsOf(unit)) {
//				System.out.println("pre " + pre);
//			}
//			for (Unit suc : graph.getSuccsOf(unit)) {
//				System.out.println("suc " + suc);
//			}
		}


	}

	private Set<String> getJarCls(String jarPath) {
		Set<String> jarClses = new HashSet<String>();
		for (String cls : SourceLocator.v().getClassesUnder(jarPath)) {
			jarClses.add(cls);
		}
		System.out.println("entry class size:" + jarClses.size());
		return jarClses;
	}
}

