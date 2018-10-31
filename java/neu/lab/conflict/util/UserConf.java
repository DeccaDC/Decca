package neu.lab.conflict.util;
/**
 * 用户配置文件
 * @author wangchao
 *
 */
public class UserConf {

	//win下的输出路径，最好使用相对路径便于跨环境使用
	private static String outDir = "D:\\ws\\sta\\";
	//mac下的输出路径，
	private static String outDir4Mac = "/Users/wangchao/Develop/neu/";
	
	public static String getOutDir4Mac() {
		return outDir4Mac;
	}

	public static void setOutDir4Mac(String outDir4Mac) {
		UserConf.outDir4Mac = outDir4Mac;
	}

	public static String getOutDir() {
		return outDir;
	}

	public static void setOutDir(String outDir) {
		UserConf.outDir = outDir;
	}

}
