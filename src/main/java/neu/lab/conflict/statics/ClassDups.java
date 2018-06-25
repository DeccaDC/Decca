package neu.lab.conflict.statics;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import neu.lab.conflict.container.DepJars;
import neu.lab.conflict.util.SootUtil;
import neu.lab.conflict.vo.DepJar;

public class ClassDups {
	private List<ClassDup> container;

	public ClassDups(DepJars depJars) {
		container = new ArrayList<ClassDup>();
		for (DepJar depJar : depJars.getAllDepJar()) {
			if (depJar.isSelected()) {
				List<String> allCls = SootUtil.getJarClasses(depJar);
				for (String cls : allCls) {
					addCls(cls, depJar);
				}
			}
		}
		Iterator<ClassDup> ite = container.iterator();
		while (ite.hasNext()) {
			ClassDup conflict = ite.next();
			if (!conflict.isDup()) {// delete conflict if there is only one version
				ite.remove();
			}
		}
	}

	public List<ClassDup> getAllClsDup() {
		return container;
	}

	private void addCls(String classSig, DepJar depJar) {
		ClassDup clsDup = null;
		for (ClassDup existDup : container) {
			if (existDup.isSelf(classSig))
				clsDup = existDup;
		}
		if (null == clsDup) {
			clsDup = new ClassDup(classSig);
			container.add(clsDup);
		}
		clsDup.addDepJar(depJar);
	}

}
