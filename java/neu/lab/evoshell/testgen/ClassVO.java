package neu.lab.evoshell.testgen;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import soot.SootClass;
import soot.SootMethod;

public class ClassVO {

	private String sig;
	private List<ClassVO> children;//direct and indirect
	private LinkedHashSet<MethodVO> mthds;
	private boolean isConcrete;
	private boolean isPublic;
	private boolean isPrivate;

	public ClassVO(SootClass stCls) {
		this.sig = stCls.getName();
		this.isPublic = stCls.isPublic();
		this.isPrivate = stCls.isPrivate();
		mthds = new LinkedHashSet<MethodVO>();
		this.isConcrete = stCls.isConcrete();
		for (SootMethod method : stCls.getMethods()) {
			mthds.add(new MethodVO(this, method));
		}
		children = new ArrayList<ClassVO>();
	}

	public boolean isPublic() {
		return isPublic;
	}

	public boolean isPrivate() {
		return isPrivate;
	}

	private String getType2Cons() {
		if (isConcrete()) {
			return sig;
		} else {
			return getBestChildType();
		}
	}

	public void addChild(ClassVO childVO) {
		children.add(childVO);
	}

	private String getBestChildType() {
		//find type in path
		for (ClassVO childVO : children) {
			if (ProjectInfo.i().isClsInPath(childVO.getSig()))
				return childVO.getConcreteType();
		}
		//return a concrete.
		return getConcreteType();
	}

	/**if class is concrete,then return.
	 * else return a concrete subclass.
	 * @return
	 */
	private String getConcreteType() {
		if (isConcrete()) {
			return sig;
		} else {
			for (ClassVO childVO : children) {
				if (childVO.isConcrete)
					return childVO.getSig();
			}
		}
		return null;
	}

	public boolean isConcrete() {
		return isConcrete;
	}

	public String getSig() {
		return sig;
	}

	public Collection<MethodVO> getMthds() {
		return mthds;
	}

	/**type of this object may be not the type of construct.
	 * if this type is abstract,the type to construct should be its subType.
	 * @param typeToCons
	 * @return
	 */
	public MethodVO getBestCons(boolean scanStatic) {
		//find static constructor.
		if (scanStatic) {
			List<MethodVO> consesOfStatic = getConsesOfStatic();

			if (this.isConcrete && !consesOfStatic.isEmpty()) {
				//this class is concrete and has constructor of this type.
				System.out.println(sig + " has static constructor.");
				return getMinParamCons(consesOfStatic);
			}
			for (ClassVO child : children) {
				consesOfStatic.addAll(child.getConsesOfStatic());
			}
			if (!consesOfStatic.isEmpty()) {
				//has constructor of this subType.
				System.out.println(sig + " has static constructor of subType.");
				return getMinParamCons(consesOfStatic);
			}

		}
		//find new constructor of this type.
		if (this.isConcrete) {
			List<MethodVO> consesOfnew = getConsesOfnew();
			if (!consesOfnew.isEmpty()) {
				//has constructor of this type.
				System.out.println(sig + " has new constructor.");
				return getMinParamCons(consesOfnew);

			}
		}
		//find new constructor of subType.
		List<MethodVO> consesOfnew = new ArrayList<MethodVO>();
		for (ClassVO child : children) {
			if (child.isConcrete) {
				consesOfnew.addAll(consesOfnew);
			}
		}
		if (!consesOfnew.isEmpty()) {
			//has new constructor of subType.
			System.out.println(sig + " has new constructor of subType.");
			return getMinParamCons(consesOfnew);

		}
		//		String typeToCons = getType2Cons();
		//		System.out.println("best consType for " + sig + " is " + typeToCons);
		//		if (typeToCons == null) {
		//			return null;
		//		}
		//		//public new
		//		List<MethodVO> consesOfNew = ProjectInfo.i().getClassVO(typeToCons).getConsesOfnew();
		//		//		System.out.println(notStaticConses.size());
		//		if (!consesOfNew.isEmpty()) {
		//			return getMinParamCons(consesOfNew);
		//		}

		return null;
	}

	/**
	 * @return static constructor of this type.
	 */
	private List<MethodVO> getConsesOfStatic() {
		List<MethodVO> consOfStatic = new ArrayList<MethodVO>();
		for (MethodVO method : ProjectInfo.i().getAllMethod()) {
			if (method.getReturnType().equals(sig) && method.isStatic() && ProjectInfo.i().isInvokeable(method)) {
				//				System.out.println("find cons:" + method.getSig());
				consOfStatic.add(method);
			}
		}

		//		for (MethodVO method : ProjectInfo.i().getAllMethod()) {
		//			//if this type and entryClass is on same package,get default and protected static construct.
		////			if(method.getCls().getSig().equals("org.apache.http.impl.client.CloseableHttpResponseProxy")) {
		////				System.out.println("isEntry:"+ProjectInfo.i().isEntryPck(SootUtil.cls2pck(method.getCls().getSig(), ".")));
		////			}
		//			if (ProjectInfo.i().isEntryPck(SootUtil.cls2pck(method.getCls().getSig(), ".")))
		//				if (method.getReturnType().equals(sig) && method.isStatic() && !method.isPrivate()) {
		//					consOfStatic.add(method);
		//				}
		//		}
		return consOfStatic;
	}

	/**
	 * @return new constructor of this type.
	 */
	private List<MethodVO> getConsesOfnew() {
		List<MethodVO> publicCons = new ArrayList<MethodVO>();
		//public constructor
		for (MethodVO methodVO : mthds) {
			if (methodVO.isConstructor() && methodVO.isPublic()) {
				publicCons.add(methodVO);
			}
		}
		//if this type and entryClass is on same package,get default and protected new construct.
		if (ProjectInfo.i().isEntryPck(SootUtil.cls2pck(sig, "."))) {
			for (MethodVO methodVO : mthds) {
				if (methodVO.isConstructor() && !methodVO.isPrivate()) {
					publicCons.add(methodVO);
				}
			}
		}

		return publicCons;
	}

	/**return constructor that has minimum parameter.
	 * @param cons
	 * @return
	 */
	private MethodVO getMinParamCons(List<MethodVO> conses) {
		MethodVO minParamCons = null;
		int paraNum = Integer.MAX_VALUE;
		for (MethodVO cons : conses) {
			if (cons.getParamNum() < paraNum) {
				paraNum = cons.getParamNum();
				minParamCons = cons;
			}
		}
		return minParamCons;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((sig == null) ? 0 : sig.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ClassVO other = (ClassVO) obj;
		if (sig == null) {
			if (other.sig != null) {
				return false;
			}
		} else if (!sig.equals(other.sig)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("info for ");
		sb.append(sig);
		sb.append(" isConcrete:");
		sb.append(isConcrete);
		sb.append(System.lineSeparator());

		for (ClassVO child : children) {
			sb.append("child:");
			sb.append(child.getSig());
			sb.append(" isConcrete:");
			sb.append(child.isConcrete);
			sb.append(System.lineSeparator());
		}
		for (MethodVO mthd : mthds) {
			sb.append("mthd:");
			sb.append(mthd.getSig());
			sb.append(" isConstructor:");
			sb.append(mthd.isConstructor());
			sb.append(System.lineSeparator());
		}
		return sb.toString();
	}

}
