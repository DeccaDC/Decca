package neu.lab.conflict.statics;

import java.util.ArrayList;
import java.util.List;

import neu.lab.conflict.vo.DepJar;

public class DupClsJarPairs {
	private List<DupClsJarPair> container;

	public DupClsJarPairs(ClassDups classDups) {
		container = new ArrayList<DupClsJarPair>();
		for (ClassDup classDup : classDups.getAllClsDup()) {
			List<DepJar> jars = classDup.getDepJars();
			for (int i = 0; i < jars.size() - 1; i++) {
				for (int j = i + 1; j < jars.size(); j++) {
					DupClsJarPair jarCmp = getJarCmp(jars.get(i), jars.get(j));
					jarCmp.addClass(classDup.getClsSig());
				}
			}
		}
	}

	public List<DupClsJarPair> getAllJarPair() {
		return container;
	}

	private DupClsJarPair getJarCmp(DepJar jarA, DepJar jarB) {
		for (DupClsJarPair jarCmp : container) {
			if (jarCmp.isSelf(jarA, jarB))
				return jarCmp;
		}
		// can't find jarCmp in container,create a new one.
		DupClsJarPair jarCmp = new DupClsJarPair(jarA, jarB);
		container.add(jarCmp);
		return jarCmp;
	}
}
