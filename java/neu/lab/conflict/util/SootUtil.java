package neu.lab.conflict.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import neu.lab.conflict.vo.ClassVO;
import neu.lab.conflict.vo.MethodVO;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.SourceLocator;

/**
 * @author asus
 *
 */
public class SootUtil {
	public static void modifyLogOut() {
		File outDir = MavenUtil.i().getBuildDir();
		if (!outDir.exists()) {
			outDir.mkdirs();
		}
		try {
			soot.G.v().out = new PrintStream(new File(outDir.getAbsolutePath() + File.separator + "soot.log"));
		} catch (FileNotFoundException e) {
			soot.G.v().out = System.out;
		}
	}

	/**
	 * @param mthdSig
	 *            e.g.:<org.slf4j.event.SubstituteLoggingEvent: org.slf4j.event.Level
	 *            getLevel()>
	 * @return e.g.: org.slf4j.event.Level getLevel();
	 */
	public static String mthdSig2name(String mthdSig) {
		return mthdSig.substring(mthdSig.indexOf(":") + 1, mthdSig.indexOf(")") + 1);
	}

	/**
	 * @param mthdSig
	 *            e.g.:<org.slf4j.event.SubstituteLoggingEvent: org.slf4j.event.Level
	 *            getLevel()>
	 * @return e.g.:org.slf4j.event.SubstituteLoggingEvent
	 */
	public static String mthdSig2cls(String mthdSig) {
		return mthdSig.substring(1, mthdSig.indexOf(":"));
	}

	public static Set<String> getJarsClasses(List<String> paths) {
		Set<String> allCls = new HashSet<String>();
		for (String path : paths) {
			allCls.addAll(getJarClasses(path));
		}
		return allCls;
	}

	public static List<String> getJarClasses(String path) {
		if (new File(path).exists()) {
			if (!path.endsWith("tar.gz") && !path.endsWith(".pom") && !path.endsWith(".war")) {
				return SourceLocator.v().getClassesUnder(path);
			} else {
				MavenUtil.i().getLog().warn(path + "is illegal classpath");
			}
		} else {
			MavenUtil.i().getLog().warn(path + "doesn't exist in local");
		}
		return new ArrayList<String>();
	}

	public static Map<String, ClassVO> getClassTb(List<String> jarPaths) {
		Map<String, ClassVO> clsTb = new HashMap<String, ClassVO>();
		for (String clsSig : SootUtil.getJarsClasses(jarPaths)) {
			SootClass sootClass = Scene.v().getSootClass(clsSig);
			ClassVO clsVO = new ClassVO(sootClass.getName());
			clsTb.put(sootClass.getName(), clsVO);
			if (Conf.ONLY_GET_SIMPLE) {// only add simple method in simple class
				if (isSimpleCls(sootClass)) {
					for (SootMethod sootMethod : sootClass.getMethods()) {
						if (sootMethod.getParameterCount() == 0)
							clsVO.addMethod(new MethodVO(sootMethod.getSignature(), clsVO));
					}
				}
			} else {// add all method
				for (SootMethod sootMethod : sootClass.getMethods()) {
					clsVO.addMethod(new MethodVO(sootMethod.getSignature(), clsVO));
				}
			}
		}
		return clsTb;
	}

	public static boolean isSimpleCls(SootClass sootClass) {
		for (SootMethod sootMethod : sootClass.getMethods()) {
			if (sootMethod.isConstructor() && sootMethod.getParameterCount() == 0) // exist constructor that doesn't
																					// need param
				return true;
		}
		return false;
	}
}
