package neu.lab.evoshell.testgen;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**method statement
 * @author asus
 *
 */
public class NeededObj {
	private static final int maxDepth = 2;

	private String classSig;
	private ClassVO clsVO;
	private int depth;

	public NeededObj(String classSig, int depth) {
		super();
		this.classSig = classSig;
		this.clsVO = ProjectInfo.i().getClassVO(classSig);
		
		this.depth = depth;
	}
	

	public String getStatement(MethodVO bestCons,boolean scanStatic) {
		if (simpleType.containsKey(classSig)) {
			return simpleType.get(classSig);
		} else {//not simple class.
			if (depth < maxDepth) {//depth is low.
				if(bestCons==null) {
					 bestCons = clsVO.getBestCons(scanStatic);
				}
				if (bestCons == null) {
					//can't find construct.
					System.out.println("best cons for " + classSig + " is null");
					return "null";
				}

				System.out.println("best cons for " + classSig + " is " + bestCons.getSig());
				StringBuilder sb = new StringBuilder(getConsPre(bestCons));
				sb.append("(");
				Collection<NeededObj> paramObjs = getConsParamObs(bestCons);
				for (NeededObj objInCons : paramObjs) {
					sb.append(objInCons.getStatement(null,true));
					sb.append(",");
				}
				if(!paramObjs.isEmpty()) {//delete last ,
					sb.deleteCharAt(sb.length() - 1);
				}
				sb.append(")");
				return sb.toString();
			} else {
				return "null";
			}
		}
	}

	/**	LiteProtoTruth.assertThat
	  new LiteProtoTruth
	 * @return
	 */
	private String getConsPre(MethodVO bestCons) {
		if (bestCons.isStatic()) {
			//LiteProtoTruth.assertThat
			return bestCons.getCls().getSig().replace("$", ".") + "." + bestCons.getName();
		} else {
			//			new LiteProtoTruth
			return "new " + bestCons.getCls().getSig().replace("$", ".");
		}

	}

	public Collection<NeededObj> getConsParamObs(MethodVO bestCons) {
		List<NeededObj> paramObjs = new ArrayList<NeededObj>();
		for (String param : bestCons.getParamTypes()) {
			paramObjs.add(new NeededObj(param, this.depth + 1));
		}
		return paramObjs;
	}
	public static final Map<String, String> simpleType;
	static {
		simpleType = new HashMap<String, String>();
		simpleType.put("boolean", "true");
		simpleType.put("byte", "1024");
		simpleType.put("char", "c");
		simpleType.put("short", "0");
		simpleType.put("int", "0");
		simpleType.put("long", "0");
		simpleType.put("float", "0.0");
		simpleType.put("double", "0.0");
		simpleType.put("java.lang.String", "\"test\"");
		simpleType.put("java.lang.Object", "new Object()");
		simpleType.put("java.io.InputStream", "(java.io.InputStream)null");
		simpleType.put("java.util.Set", "new java.util.HashSet()");
		simpleType.put("java.util.List", "new java.util.ArrayList()");
		
		simpleType.put("io.grpc.Channel", "io.grpc.inprocess.InProcessChannelBuilder.forName(\"test\").build()");
		simpleType.put("org.openimaj.video.capture.Device", "null");
		simpleType.put("org.openqa.selenium.safari.SafariOptions", "new org.openqa.selenium.safari.SafariOptions()");
		
	}
}
