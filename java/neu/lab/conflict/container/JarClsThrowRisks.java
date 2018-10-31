package neu.lab.conflict.container;

import neu.lab.conflict.vo.ClassVO;
import neu.lab.conflict.vo.JarClsThrowRisk;

import java.util.HashSet;
import java.util.Set;

public class JarClsThrowRisks {
	private static JarClsThrowRisks instance;
	public static JarClsThrowRisks i() {
		return instance;
	}

	public static void init(FinalClasses finalClasses) {
		if (instance == null)
			instance = new JarClsThrowRisks(finalClasses);

	}

	private JarClsThrowRisks(FinalClasses finalClasses) {
		container = new HashSet<JarClsThrowRisk>();
		for (ClassVO throwCls : finalClasses.getThrowClses()) {
			JarClsThrowRisk risk = getJarClsThrowRisk(throwCls);
			if(risk==null) {
				risk = new JarClsThrowRisk(throwCls.getDepJar());
				container.add(risk);
			}
		}
	}

	private void add(ClassVO throwCls) {
		
	}
	
	private JarClsThrowRisk getJarClsThrowRisk(ClassVO throwCls) {
		for(JarClsThrowRisk risk:container) {
			if(risk.getDepJar()==throwCls.getDepJar()) {
				return risk;
			}
		}
		return null;
	}



	private Set<JarClsThrowRisk> container;
}
