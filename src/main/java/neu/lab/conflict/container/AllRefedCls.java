package neu.lab.conflict.container;

import java.util.HashSet;
import java.util.Set;

import javassist.ClassPool;
import neu.lab.conflict.GlobalVar;
import neu.lab.conflict.util.MavenUtil;
import neu.lab.conflict.vo.DepJar;

/**
 * 所有被引用的cls
 * @author wangchao
 *
 */
public class AllRefedCls {
	private static AllRefedCls instance;
	private Set<String> refedClses;

	private AllRefedCls() {
		long start = System.currentTimeMillis();
		refedClses = new HashSet<String>();
		try {
			ClassPool pool = new ClassPool();
			for (String path : DepJars.i().getUsedJarPaths()) {
				pool.appendClassPath(path);
			}
			for (String cls : AllCls.i().getAllCls()) {
				refedClses.add(cls);
				if (pool.getOrNull(cls) != null) {
//					System.out.println();
					refedClses.addAll(pool.get(cls).getRefClasses());
				} else {
					MavenUtil.i().getLog().warn("can't find " + cls + " in pool when form reference.");
				}
			}
		} catch (Exception e) {
			MavenUtil.i().getLog().error("get refedCls error:", e);
		}
		long runtime = (System.currentTimeMillis() - start) / 1000;
		GlobalVar.time2calRef+=runtime;
	}
	private AllRefedCls(DepJar depJar) {
		long start = System.currentTimeMillis();
		refedClses = new HashSet<String>();
		try {
			ClassPool pool = new ClassPool();
			for (String path : DepJars.i().getUsedJarPaths(depJar)) {
				pool.appendClassPath(path);
			}
			for (String cls : AllCls.i().getAllCls()) {
				refedClses.add(cls);
				if (pool.getOrNull(cls) != null) {
//					System.out.println();
					refedClses.addAll(pool.get(cls).getRefClasses());
				} else {
					MavenUtil.i().getLog().warn("can't find " + cls + " in pool when form reference.");
				}
			}
		} catch (Exception e) {
			MavenUtil.i().getLog().error("get refedCls error:", e);
		}
		long runtime = (System.currentTimeMillis() - start) / 1000;
		GlobalVar.time2calRef+=runtime;
	}

	public static AllRefedCls i() {
		if (instance == null) {
			instance = new AllRefedCls();
		}
		return instance;
	}
	
	public static void init(DepJar depJar) {
		instance = new AllRefedCls(depJar);
	}
	
	public static AllRefedCls i(DepJar depJar) {
		instance = new AllRefedCls(depJar);
		return instance;
	}

	public boolean contains(String cls) {
		return refedClses.contains(cls);
	}

}
