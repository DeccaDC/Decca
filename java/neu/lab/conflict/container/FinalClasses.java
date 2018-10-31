package neu.lab.conflict.container;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import neu.lab.conflict.util.MavenUtil;
import neu.lab.conflict.vo.ClassVO;
import neu.lab.conflict.vo.DepJar;

/**
 * class in final package
 * 
 * @author asus
 *
 */
public class FinalClasses {
	private static FinalClasses instance;

	public static void init(DepJars depJars) {
		if (instance == null) {
			instance = new FinalClasses(depJars);
		}
	}

	public static FinalClasses i() {
		// if(instance==null)
		// instance = new FinalClasses(DepJars.i());
		return instance;
	}

	private Map<String, ClassVO> loadClses;// key:classname,value:class object

	private List<ClassVO> throwClses;//class was thrown because duplicate. 

	public Map<String, ClassVO> getClsTb() {
		return loadClses;
	}

	/**
	 * must initial DepJars before this construct
	 */
	private FinalClasses(DepJars depJars) {
		loadClses = new HashMap<String, ClassVO>();
		throwClses = new ArrayList<ClassVO>();
		for (DepJar depJar : depJars.getAllDepJar()) {
			if (depJar.isSelected()) {
				for (ClassVO newCls : depJar.getClsTb().values()) {
					String key = newCls.getClsSig();
					ClassVO oldCls = loadClses.get(key);
					if (oldCls == null)
						loadClses.put(newCls.getClsSig(), newCls);
					else
						loadAndThrow(oldCls, newCls);
				}
			}
		}
		MavenUtil.i().getLog().info("final class size:" + loadClses.size());
	}

	private void loadAndThrow(ClassVO oldCls, ClassVO newCls) {
		if (loadNewCls(oldCls, newCls)) {
			loadClses.put(newCls.getClsSig(), newCls);
			throwClses.add(oldCls);
		} else {
			throwClses.add(newCls);
		}
	}

	private boolean loadNewCls(ClassVO oldCls, ClassVO newCls) {
		return false;
	}

	public List<ClassVO> getThrowClses() {
		return throwClses;
	}

}
