package neu.lab.conflict.container;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import neu.lab.conflict.vo.NodeAdapter;
import neu.lab.conflict.util.Conf;
import neu.lab.conflict.vo.Conflict;

public class Conflicts {
	private static Conflicts instance;

	public static void init(NodeAdapters nodeAdapters) {
		//if (instance == null) {   不使用单例模式
			instance = new Conflicts(nodeAdapters);
		//}
	}

	public static Conflicts i() {
		return instance;
	}

	private List<Conflict> container;

	/**
	 * must initial NodeAdapters before this construct
	 */
	private Conflicts(NodeAdapters nodeAdapters) {
		container = new ArrayList<Conflict>();
		for (NodeAdapter node : nodeAdapters.getAllNodeAdapter()) {
				addNodeAdapter(node);
		}

		// delete conflict if there is only one version 如果只有一个版本就删除冲突
		Iterator<Conflict> ite = container.iterator();
		while (ite.hasNext()) {
			Conflict conflict = ite.next();
			if (!conflict.isConflict()||!wantCal(conflict)) {	//如果这个方法不是需要的冲突
				ite.remove();
			}
		}
	}
	
	/**this method use to debug.
	 * 
	 * @param conflict
	 * @return
	 */
	private boolean wantCal(Conflict conflict) {

		if(Conf.callConflict==null||"".equals(Conf.callConflict)) {
			return true;
		}else {
			if(conflict.getSig().equals(Conf.callConflict.replace("+", ":"))) 
				return true;
			return false;
		}
		
//		
//		return true;
	}

	public List<Conflict> getConflicts() {
		return container;
	}

	/**
	 * 如果容器中已经存在一个conflict和本nodeAdapter是相同的构件
	 * 则为这个conflict添加本节点适配器
	 * 如果容器中不存在
	 * 则本nodeAdapter作为一个conflict加入容器
	 * 然后为这个conflict加入本节点
	 * @param nodeAdapter
	 */
	private void addNodeAdapter(NodeAdapter nodeAdapter) {
		Conflict conflict = null;
		for (Conflict existConflict : container) {
			if (existConflict.sameArtifact(nodeAdapter.getGroupId(), nodeAdapter.getArtifactId())) {
				conflict = existConflict;
			}
		}
		if (null == conflict) {
			conflict = new Conflict(nodeAdapter.getGroupId(), nodeAdapter.getArtifactId());
			container.add(conflict);
		}
		conflict.addNode(nodeAdapter);
	}

	@Override
	public String toString() {
		String str = "project has " + container.size() + " conflict-dependency:+\n";
		for (Conflict conflictDep : container) {
			str = str + conflictDep.toString() + "\n";
		}
		return str;
	}
}
