package neu.lab.conflict.vo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dom4j.Element;
import org.dom4j.tree.DefaultElement;

import abandon.neu.lab.conflict.graph.clsref.Graph4ClsRef;
import abandon.neu.lab.conflict.graph.clsref.Node4ClsRef;
import abandon.neu.lab.conflict.risk.node.ConflictNRisk;
import abandon.neu.lab.conflict.risk.node.DepJarNRisk;
import abandon.neu.lab.conflict.risk.ref.tb.NoLimitRefTb;
import javassist.ClassPool;
import neu.lab.conflict.container.AllCls;
import neu.lab.conflict.container.AllRefedCls;
import neu.lab.conflict.container.DepJars;
import neu.lab.conflict.container.NodeAdapters;
import neu.lab.conflict.soot.JarAna;
import neu.lab.conflict.util.MavenUtil;
import neu.lab.conflict.util.SootUtil;

/**
 * @author asus
 *
 */
public class DepJar {
	private String groupId;
	private String artifactId;// artifactId
	private String version;// version
	private String classifier;
	private List<String> jarFilePaths;// host project may have multiple source.
	private Map<String, ClassVO> clsTb;// all class in jar
	private Set<NodeAdapter> nodeAdapters;// all
	private DepJarNRisk jarRisk;
	private Set<String> allMthd;

	public DepJar(String groupId, String artifactId, String version, String classifier, List<String> jarFilePaths) {
		this.groupId = groupId;
		this.artifactId = artifactId;
		this.version = version;
		this.classifier = classifier;
		this.jarFilePaths = jarFilePaths;
	}

	/**
	 * get jar may have risk thinking same class in different dependency,selected jar may have risk; 
	 * Not thinking same class in different dependency,selected jar is safe
	 * 
	 * @return
	 */
	public boolean isRisk() {
		return !this.isSelected();
	}
	
	/**
	 *	all class in jar中是不是包含某一class 
	 *
	 */
	public boolean containsCls(String clsSig) {
		return this.getClsTb().containsKey(clsSig);
	}

	public Element getRchNumEle() {
		Element nodeEle = new DefaultElement("version");
		nodeEle.addAttribute("versionId", getVersion());
		nodeEle.addAttribute("loaded", "" + isSelected());
		for (NodeAdapter node : this.getNodeAdapters()) {
			nodeEle.add(node.getPathElement());
		}
		return nodeEle;
	}

	public Element getClsConflictEle(int num) {
		Element nodeEle = new DefaultElement("jar-" + num);
		nodeEle.addAttribute("id", toString());
		for (NodeAdapter node : this.getNodeAdapters()) {
			nodeEle.add(node.getPathElement());
		}
		return nodeEle;
	}

	public DepJarNRisk getJarRiskAna(ConflictNRisk conflictRiskAna) {
		// if (jarRisk == null) {
		// jarRisk = new DepJarCg(this);
		// }
		//
		// return jarRisk;
		return new DepJarNRisk(this, conflictRiskAna);
	}

	public Set<NodeAdapter> getNodeAdapters() {
		if (nodeAdapters == null)
			nodeAdapters = NodeAdapters.i().getNodeAdapters(this);
		return nodeAdapters;
	}

	public String getAllDepPath() {
		StringBuilder sb = new StringBuilder(toString() + ":");
		for (NodeAdapter node : getNodeAdapters()) {
			sb.append("  [");
			sb.append(node.getWholePath());
			sb.append("]");
		}
		return sb.toString();

	}

	/**
	 * @return the import path of depJar.
	 */
	public String getValidDepPath() {
		StringBuilder sb = new StringBuilder(toString() + ":");
		for (NodeAdapter node : getNodeAdapters()) {
			if (node.isNodeSelected()) {
				sb.append("  [");
				sb.append(node.getWholePath());
				sb.append("]");
			}
		}
		return sb.toString();

	}

	public NodeAdapter getSelectedNode() {
		for (NodeAdapter node : getNodeAdapters()) {
			if (node.isNodeSelected()) {
				return node;
			}
		}
		return null;
	}

	public boolean isProvided() {
		for (NodeAdapter node : getNodeAdapters()) {
			if (node.isNodeSelected()) {
				return "provided".equals(node.getScope());
			}
		}
		return false;
	}

	public boolean isSelected() {
		for (NodeAdapter nodeAdapter : getNodeAdapters()) {
			if (nodeAdapter.isNodeSelected())
				return true;
		}
		return false;
	}

	/**
	 * 得到这个jar所有类的集合
	 * @return
	 */
	public Map<String, ClassVO> getClsTb() {
		if (clsTb == null) {
			if (null == this.getJarFilePaths(true)) {
				// no file
				clsTb = new HashMap<String, ClassVO>();
				MavenUtil.i().getLog().warn("can't find jarFile for:" + toString());
			} else {
				clsTb = JarAna.i().deconstruct(this.getJarFilePaths(true));
				if (clsTb.size() == 0) {
					MavenUtil.i().getLog().warn("get empty clsTb for " + toString());
				}
				for (ClassVO clsVO : clsTb.values()) {
					clsVO.setDepJar(this);
				}
			}
		}
		return clsTb;
	}

	public ClassVO getClassVO(String clsSig) {
		return getClsTb().get(clsSig);
	}

	/**
	 * 得到这个jar的所有方法
	 * @return
	 */
	public Set<String> getAllMthd() {
		if (allMthd == null) {
			allMthd = new HashSet<String>();
			for (ClassVO cls : getClsTb().values()) {
				for (MethodVO mthd : cls.getMthds()) {
					allMthd.add(mthd.getMthdSig());
				}
			}
		}
		return allMthd;
	}
	
	public boolean containsMthd(String mthd) {
		return getAllMthd().contains(mthd);
	}
	
	/**
	 * 得到本depjar独有的cls
	 * @param otherJar
	 * @return
	 */
	public Set<String> getOnlyClses(DepJar otherJar) {
		Set<String> onlyCls = new HashSet<String>();
		Set<String> otherAll = otherJar.getAllCls(true);
		for (String clsSig : getAllCls(true)) {
			if (!otherAll.contains(clsSig)) {
				onlyCls.add(clsSig);
			}
		}
		return onlyCls;
	}
	
	/**
	 * 得到本depjar独有的mthds
	 * @param otherJar
	 * @return
	 */
	public Set<String> getOnlyMthds(DepJar otherJar) {
		Set<String> onlyMthds = new HashSet<String>();
		for (String clsSig : getClsTb().keySet()) {
			ClassVO otherCls = otherJar.getClassVO(clsSig);
			if (otherCls != null) {
				ClassVO cls = getClassVO(clsSig);
				for (MethodVO mthd : cls.getMthds()) {
					if (!otherCls.hasMethod(mthd.getMthdSig())) {
						onlyMthds.add(mthd.getMthdSig());
					}
				}
			}
		}
		return onlyMthds;
	}



	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DepJar) {
			return isSelf((DepJar) obj);

		}
		return false;
	}

	@Override
	public int hashCode() {
		return groupId.hashCode() * 31 * 31 + artifactId.hashCode() * 31 + version.hashCode()
				+ classifier.hashCode() * 31 * 31 * 31;
	}

	@Override
	public String toString() {
		return groupId + ":" + artifactId + ":" + version + ":" + classifier;
	}

	public String getGroupId() {
		return groupId;
	}

	public String getArtifactId() {
		return artifactId;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getClassifier() {
		return classifier;
	}

	public boolean isSame(String groupId2, String artifactId2, String version2, String classifier2) {
		return groupId.equals(groupId2) && artifactId.equals(artifactId2) && version.equals(version2)
				&& classifier.equals(classifier2);
	}

	/**
	 * 是否为同一个
	 * @param dep
	 * @return
	 */
	public boolean isSelf(DepJar dep) {
		return isSame(dep.getGroupId(), dep.getArtifactId(), dep.getVersion(), dep.getClassifier());
	}
/**
 * 没有比较版本
 * @param depJar
 * @return
 */
	public boolean isSameLib(DepJar depJar) {
		return getGroupId().equals(depJar.getGroupId()) && getArtifactId().equals(depJar.getArtifactId());
	}

	public void setClsTb(Map<String, ClassVO> clsTb) {
		this.clsTb = clsTb;
	}

	public boolean hasClsTb() {
		return null != this.clsTb;
	}
	
	/**
	 * 得到testMthds中哪些mthds存在于本jar
	 * @param testMthds
	 * @return
	 */
	public List<String> getInnerMthds(Collection<String> testMthds) {
		Set<String> jarMthds = getAllMthd();
		List<String> innerMthds = new ArrayList<String>();
		for (String mthd : testMthds) {
			if (jarMthds.contains(mthd))
				innerMthds.add(mthd);
		}
		return innerMthds;
	}

	/**
	 * note:from the view of usedJar. e.g.
	 * getReplaceJar().getRiskMthds(getRchedMthds());
	 * 
	 * @param testMthds
	 * @return
	 */
	public Set<String> getRiskMthds(Collection<String> testMthds) {
		Set<String> riskMthds = new HashSet<String>();
		for (String testMthd : testMthds) {
			if (!this.containsMthd(testMthd) && AllRefedCls.i().contains(SootUtil.mthdSig2cls(testMthd))) {
				// don't have method,and class is used. 使用这个类，但是没有方法
				if (this.containsCls(SootUtil.mthdSig2cls(testMthd))) {
					// has class.don't have method.	有这个类，没有方法
					riskMthds.add(testMthd);
				} else if (!AllCls.i().contains(SootUtil.mthdSig2cls(testMthd))) {
					// This jar don't have class,and all jar don't have class.	这个jar没有这个class，所有的jar都没有
					riskMthds.add(testMthd);
				}
			}
		}
		// if (diffMthd.contains("<init>") || diffMthd.contains("<clinit>")) {
		return riskMthds;
	}
	public Set<String> getRiskMthds(Collection<String> testMthds, DepJar depJar) {
		Set<String> riskMthds = new HashSet<String>();
		for (String testMthd : testMthds) {
			if (!this.containsMthd(testMthd) && AllRefedCls.i(depJar).contains(SootUtil.mthdSig2cls(testMthd))) {
				// don't have method,and class is used. 使用这个类，但是没有方法
				if (this.containsCls(SootUtil.mthdSig2cls(testMthd))) {
					// has class.don't have method.	有这个类，没有方法
					riskMthds.add(testMthd);
				} else if (!AllCls.i().contains(SootUtil.mthdSig2cls(testMthd))) {
					// This jar don't have class,and all jar don't have class.	这个jar没有这个class，所有的jar都没有
					riskMthds.add(testMthd);
				}
			}
		}
		// if (diffMthd.contains("<init>") || diffMthd.contains("<clinit>")) {
		return riskMthds;
	}
	/**
	 * 暂时不明白用途
	 * @param usedJar
	 * @return
	 */
	public Set<String> getThrownMthds(DepJar usedJar) {
		Set<String> thrownMthds = new HashSet<String>();
		Set<String> usedMthds = new HashSet<String>();
		for (String mthd : this.getAllMthd()) {
			if (!usedMthds.contains(mthd)) {
				thrownMthds.add(mthd);
			}
		}
		return thrownMthds;
	}

	/**
	 * methods that this jar don't have.
	 * 
	 * @param testMthds
	 * @return
	 */
	public Set<String> getOutMthds(Collection<String> testMthds) {
		Set<String> jarMthds = getAllMthd();
		Set<String> outMthds = new HashSet<String>();
		for (String mthd : testMthds) {
			if (!jarMthds.contains(mthd))
				outMthds.add(mthd);
		}
		return outMthds;
	}
	

	
	private NoLimitRefTb refTb;

	public NoLimitRefTb getRefTb() {
		if (refTb == null) {
			refTb = new NoLimitRefTb();
			try {
				ClassPool pool = new ClassPool();
				for (String path : this.getJarFilePaths(true)) {
					pool.appendClassPath(path);
				}
				for (String jarCls : getAllCls(true)) {
					refTb.addByEr(jarCls, pool.get(jarCls).getRefClasses());
				}
			} catch (Exception e) {
				MavenUtil.i().getLog().error("get refedCls error:", e);
			}
		}
		return refTb;
	}

	public Set<String> getAllCls(boolean useTarget) {
		return SootUtil.getJarsClasses(this.getJarFilePaths(useTarget));
	}

	/**
	 * @param useTarget:
	 *            host-class-name can get from source directory(false) or target
	 *            directory(true). using source directory: advantage: get class
	 *            before maven-package disadvantage:class can't deconstruct by
	 *            soot;miss class that generated.
	 * @return
	 */
	public List<String> getJarFilePaths(boolean useTarget) {
		if (!useTarget) {// use source directory
			// if node is inner project,will return source directory(using source directory
			// can get classes before maven-package)
			if (isHost())
				return MavenUtil.i().getSrcPaths();
		}
		return jarFilePaths;
	}

	public boolean isHost() {
		if (getNodeAdapters().size() == 1) {
			NodeAdapter node = getNodeAdapters().iterator().next();
			if (MavenUtil.i().isInner(node))
				return true;
		}
		return false;
	}

	/**
	 * graph-nodes contains all the jar packaged in jar-with-dependency.
	 * specially,this dep-jar will replace selected-jar.
	 * 
	 * @return
	 */
	public Graph4ClsRef getWholeClsRefG() {
		Graph4ClsRef graph = new Graph4ClsRef();
		try {
			ClassPool pool = new ClassPool();
			Set<String> allSysCls = new HashSet<String>();		//all system classes
			for (DepJar jar : DepJars.i().getAllDepJar()) {
				if (jar == this || (jar.isSelected() && !jar.isSameLib(this))) {	//jar和本jar相等，或者(jar被选中且和本jar不是相同lib版本)
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
				for (Object ee : pool.get(sysCls).getRefClasses()) {	//返回此类中引用的所有类的名称的集合。该集合包括此类的名称。
					if (!sysCls.equals(ee)) {// don't add relation of self.
						Node4ClsRef node = (Node4ClsRef) graph.getNode((String) ee);
						if (node != null)
							node.addInCls(sysCls);
					}
				}
			}

		} catch (Exception e) {
			MavenUtil.i().getLog().error("get refedCls error:", e);
		}
		return graph;
	}

	/**
	 * use this jar replace version of used-version ,then return path of
	 * all-used-jar
	 * 使用这个jar替代了旧版本，然后返回所有的旧jar的路径
	 * @return
	 * @throws Exception
	 */
	public List<String> getRepalceCp() throws Exception {
		List<String> paths = new ArrayList<String>();
		paths.addAll(this.getJarFilePaths(true));
		boolean hasRepalce = false;
		for (DepJar usedDepJar : DepJars.i().getUsedDepJars()) {
			if (this.isSameLib(usedDepJar)) {// used depJar instead of usedDepJar.
				if (hasRepalce) {
					MavenUtil.i().getLog().warn("when cg, find multiple usedLib for " + toString());	//有重复的使用路径
					throw new Exception("when cg, find multiple usedLib for " + toString());
				}
				hasRepalce = true;
			} else {
				for (String path : usedDepJar.getJarFilePaths(true)) {
					paths.add(path);
				}
				// paths.addAll(usedDepJar.getJarFilePaths(true));
			}
		}
		if (!hasRepalce) {
			MavenUtil.i().getLog().warn("when cg,can't find mutiple usedLib for " + toString());
			throw new Exception("when cg,can't find mutiple usedLib for " + toString());
		}
		return paths;
	}

	/**
	 * @return include self
	 */
	public Set<String> getFatherJarCps(boolean includeSelf) {
		Set<String> fatherJarCps = new HashSet<String>();
		for (NodeAdapter node : this.nodeAdapters) {
			fatherJarCps.addAll(node.getAncestorJarCps(includeSelf));
		}
		return fatherJarCps;
	}
}
