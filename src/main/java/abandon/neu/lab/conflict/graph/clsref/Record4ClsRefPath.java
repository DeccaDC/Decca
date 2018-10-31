package abandon.neu.lab.conflict.graph.clsref;

import org.dom4j.Element;
import org.dom4j.tree.DefaultElement;

import abandon.neu.lab.conflict.graph.path1.Record4Path;
import neu.lab.conflict.graph.IRecord;

public class Record4ClsRefPath extends Record4Path {
	private boolean isFromHost;

	public Record4ClsRefPath(String pathStr, int pathLen, boolean isFromHost) {
		super(pathStr, pathLen);
		this.isFromHost = isFromHost;
	}

	public Element getPathEle() {
		Element ele = new DefaultElement("path");
		ele.addAttribute("isFromHost", "" + isFromHost);
		ele.addAttribute("pathLength", "" + this.pathLen);
		ele.addText(pathStr);
		return ele;
	}

	@Override
	public IRecord clone() {
		return new Record4ClsRefPath(pathStr, pathLen, isFromHost);
	}

}
