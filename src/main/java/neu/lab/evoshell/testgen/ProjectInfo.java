package neu.lab.evoshell.testgen;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

public class ProjectInfo {
	private static ProjectInfo instance = new ProjectInfo();

	LinkedHashMap<String, ClassVO> sig2class;
	LinkedHashMap<String, MethodVO> sig2method;
	Set<String> clsesInPath;
	String entryCls;

	private ProjectInfo() {
		sig2class = new LinkedHashMap<String, ClassVO>();
		sig2method = new LinkedHashMap<String, MethodVO>();
	}

	public void setEntryCls(String entryCls) {
		this.entryCls = entryCls;
	}

	public boolean isEntryPck(String pck) {
		return SootUtil.cls2pck(entryCls, ".").equals(pck);
	}

	public void addInheritInfo(String superCls, String childCls) {
		ClassVO superVO = sig2class.get(superCls);
		ClassVO childVO = sig2class.get(childCls);
		if (superVO != null && childVO != null) {
			superVO.addChild(childVO);
		}
	}

	public boolean isClsInPath(String clsSig) {
		return clsesInPath.contains(clsSig);
	}

	public void initClsesInPath(List<String> mthdInPath) {
		clsesInPath = new HashSet<String>();
		for (String mthd : mthdInPath) {
			clsesInPath.add(SootUtil.mthdSig2cls(mthd));
		}
	}

	public ClassVO getClassVO(String clsSig) {
		ClassVO clsVO = sig2class.get(clsSig);
		if (clsVO == null) {
//			for (StackTraceElement ele : Thread.currentThread().getStackTrace()) {
//				System.out.println("lzw trace:" + ele);
//			}
			System.out.println("can't find classVo for:" + clsSig);
		}
		return clsVO;
	}

	public MethodVO getMethodVO(String mthdSig) {
		return sig2method.get(mthdSig);
	}

	public void addClass(ClassVO cls) {
		sig2class.put(cls.getSig(), cls);
	}

	public void addMethod(MethodVO mthd) {
		sig2method.put(mthd.getSig(), mthd);
	}

	public static ProjectInfo i() {
		return instance;
	}

	public Collection<MethodVO> getAllMethod() {
		return sig2method.values();
	}

	public boolean isInvokeable(MethodVO mthd) {
		ClassVO classVO = mthd.getCls();
		boolean isEntryPck = isEntryPck(SootUtil.cls2pck(classVO.getSig(), "."));
		if (isEntryPck) {
			return !classVO.isPrivate() && !mthd.isPrivate();
		} else {
			return classVO.isPublic() && mthd.isPublic();
		}
	}

	//	Map<String,MethodVO> 
}
