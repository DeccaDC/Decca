package neu.lab.evoshell.testgen;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import soot.Scene;
import soot.SceneTransformer;
import soot.SootClass;
import soot.SootMethod;
import soot.util.Chain;

public class SootTf extends SceneTransformer {

	public SootTf() {
		
	}

	@Override
	protected void internalTransform(String arg0, Map<String, String> arg1) {
		//form all class
		Chain<SootClass> allClass = Scene.v().getClasses();
		for (SootClass sootClass : allClass) {
			ClassVO clsVO = new ClassVO(sootClass);
			ProjectInfo.i().addClass(clsVO);
			for(MethodVO mthd:clsVO.getMthds()) {
				ProjectInfo.i().addMethod(mthd);
			}
//			for (SootMethod method : sootClass.getMethods()) {
//				System.out.println("static:" + method.isStatic() + " " + "public:" + method.isPublic() + " "
//						+ method.getSignature());
//			}
		}
		//make the CHA.
		for(SootClass sootClass : allClass) {
			Set<SootClass> allSuper = new HashSet<SootClass>();
			getSuper(sootClass,allSuper);
			for(SootClass superClass:allSuper) {
				ProjectInfo.i().addInheritInfo(superClass.getName(), sootClass.getName());
			}
		}
	}
	
	private void getSuper(SootClass cls, Set<SootClass> allSuper) {
		Set<SootClass> allDirectSuper = new HashSet<SootClass>();

		if (cls.hasSuperclass()) {
			allDirectSuper.add(cls.getSuperclass());
			allSuper.add(cls.getSuperclass());
		}

		Chain<SootClass> superInters = cls.getInterfaces();
		if (null != superInters) {
			for (SootClass superInter : superInters) {
				allDirectSuper.add(superInter);
				allSuper.add(superInter);
			}
		}
		if (!allDirectSuper.isEmpty()) {
			for (SootClass superC : allDirectSuper) {
				getSuper(superC, allSuper);
			}
		}
		
	}
}
