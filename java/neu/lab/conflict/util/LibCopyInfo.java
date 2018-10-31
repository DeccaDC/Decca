package neu.lab.conflict.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class LibCopyInfo {
	private static Map<String, Set<String>> pjt2clses;
	static {
		pjt2clses = new HashMap<String, Set<String>>();
		put("org.apache.storm:storm-kafka-monitor:1.1.2",
				new String[] { "org.apache.curator.framework.imps.CuratorFrameworkImpl" });
		put("com.alibaba:dubbo-rpc-thrift:2.6.2", new String[] { "org.apache.http.impl.conn.LoggingOutputStream" ,"org.apache.http.impl.conn.LoggingInputStream"});
		put("org.apache.servicecomb:swagger-generator-core:1.0.0-m2",
				new String[] { "com.fasterxml.jackson.dataformat.yaml.YAMLFactory" });
		put("us.codecraft:webmagic-selenium:0.7.3", new String[] { "com.gargoylesoftware.htmlunit.WebResponse" });
		put("org.numenta:htm.java:0.6.13",
				new String[] { "org.nustaq.serialization.FSTObjectInput", "org.nustaq.serialization.FSTObjectInput$2",
						"org.nustaq.serialization.FSTObjectInput$MyObjectStream" });
		put("org.wisdom-framework:wisdom-test:0.10.0",
				new String[] { "org.wisdom.maven.osgi.BundlePackager" });
	}

	private static void put(String pjt, String[] clses) {
		Set<String> clsSet = new HashSet<String>();
		clsSet.addAll(Arrays.asList(clses));
		pjt2clses.put(pjt, clsSet);
	}

	public static boolean isLibCopy(String projectCor, String clsName) {
		Set<String> libClses = pjt2clses.get(projectCor);
		if (libClses == null)
			return false;
		if (libClses.contains(clsName))
			return true;
		return false;
	}

}
