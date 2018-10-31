package neu.lab.conflict;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import abandon.neu.lab.conflict.distance.MethodProbDistances;
import neu.lab.conflict.container.Conflicts;
import neu.lab.conflict.container.DepJars;
import neu.lab.conflict.graph.Book4path;
import neu.lab.conflict.graph.Dog;
import neu.lab.conflict.graph.Graph4distance;
import neu.lab.conflict.graph.Graph4path;
import neu.lab.conflict.graph.GraphPrinter;
import neu.lab.conflict.graph.IBook;
import neu.lab.conflict.graph.IRecord;
import neu.lab.conflict.graph.Record4distance;
import neu.lab.conflict.graph.Record4path;
import neu.lab.conflict.graph.Dog.Strategy;
import neu.lab.conflict.risk.jar.DepJarJRisk;
import neu.lab.conflict.util.Conf;
import neu.lab.conflict.util.DebugUtil;
import neu.lab.conflict.util.MavenUtil;
import neu.lab.conflict.util.MySortedMap;
import neu.lab.conflict.util.SootUtil;
import neu.lab.conflict.util.UserConf;
import neu.lab.conflict.vo.Conflict;
import neu.lab.conflict.vo.DepJar;
import neu.lab.evoshell.CallPathFile;

/**
 * 测试用例生成器
 * @author wangchao
 *
 */
public class TestCaseGenerator {
	String outDir;
	boolean append;

	/**
	 * 公有构造函数
	 * @param outDir
	 * @param append
	 */
	public TestCaseGenerator(String outDir, boolean append) {
		super();
		this.outDir = outDir;
		this.append = append;
	}

	/*
	 * 
	 */
	public void writePath() {
		java.io.File f = new java.io.File(outDir);
		if (!f.exists()) {
			f.mkdirs();
		}

		String projectSig = (MavenUtil.i().getProjectCor()).replace(":", "+");	//项目标记
		for (Conflict conflict : Conflicts.i().getConflicts()) {
			String conflictSig = conflict.getSig().replace(":", "+");
			for (DepJarJRisk jarRisk : conflict.getJRisk().getJarRisks()) {
				//				String outPath = outDir +"p_"+ projectSig + "@" + conflictSig + "@" + jarRisk.getVersion() + ".txt";
				boolean hasPrint = writeJarRisk(jarRisk, outDir, projectSig + "@" + conflictSig, append);
				//TODO print one?
				if (hasPrint)
					break;
			}

		}
	}

	public void validatePath() {
		File pathDir = new File(outDir);
		for (File child : pathDir.listFiles()) {
			if (child.getName().startsWith("p_")) {
				try {
					new CallPathFile(child.getAbsolutePath()).validatePaths(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private boolean writeJarRisk(DepJarJRisk jarRisk, String outDir, String projectConflict, boolean append) {
		MavenUtil.i().getLog().info("find riskPath for:" + projectConflict);
		String distanceFile = outDir + "d_" + projectConflict + "@" + jarRisk.getVersion() + ".txt";
		String pathFile = outDir + "p_" + projectConflict + "@" + jarRisk.getVersion() + ".txt";
		try {
			Graph4distance distanceGraph = jarRisk.getGraph4distance();
			
			if (distanceGraph.getAllNode().isEmpty()) {
				return false;
			}
			//TODO debug distance graph.
			GraphPrinter.printGraph(distanceGraph, UserConf.getOutDir4Mac() + "graph.txt", distanceGraph.getHostNds());
			Map<String, IBook> distanceBooks = new Dog(distanceGraph).findRlt(distanceGraph.getHostNds(),
					Conf.DOG_DEP_FOR_DIS, Strategy.NOT_RESET_BOOK);
			Set<String> nds2remain = new HashSet<String>();
			for (IBook book : distanceBooks.values()) {
				nds2remain.add(book.getNodeName());
				for (IRecord iRecord : book.getRecords()) {
					Record4distance record = (Record4distance) iRecord;
					MavenUtil.i().getLog().info("record name test :" + record.getName());
					nds2remain.add(record.getName());
				}
			}
			for (String name : nds2remain) {
				System.out.println("test" + name);
			}
			Graph4path pathGraph = distanceGraph.getGraph4path();
			if (pathGraph.getAllNode().isEmpty()) {
				return false;
			}

			MethodProbDistances distances = jarRisk.getMethodProDistances(distanceBooks);
			if (!distances.isEmpty()) {
				PrintWriter printer;
				try {
					printer = new PrintWriter(new BufferedWriter(new FileWriter(distanceFile)));
					printer.println(distances);
					printer.close();
				} catch (IOException e) {
					MavenUtil.i().getLog().error("can't write distanceFile ", e);
				}
			}

			//			Graph4path pathGraph = jarRisk.getGraph4mthdPath();
			Set<String> hostNds = pathGraph.getHostNds();
			//TODO debug path graph.
//			GraphPrinter.printGraph(pathGraph, UserConf.getOutDir4Mac() + "graph_mthdPath.txt", hostNds);

			Map<String, IBook> books = new Dog(pathGraph).findRlt(hostNds, Conf.DOG_DEP_FOR_PATH,
					Strategy.NOT_RESET_BOOK);
			//TODO debug book
//			DebugUtil.print(UserConf.getOutDir4Mac() + "debug.txt", books.get(
//					"<com.google.common.truth.extensions.proto.LiteProtoSubject: void isNotEqualTo(java.lang.Object)>")
//					.toString());
//			DebugUtil.print("d://debug.txt", books.get(
//					"<com.google.common.truth.Subject: void failWithRawMessage(java.lang.String,java.lang.Object[])>")
//					.toString());

			MySortedMap<Integer, Record4path> dis2records = new MySortedMap<Integer, Record4path>();
			// List<Record4mthdPath> records = new ArrayList<Record4mthdPath>();

			for (String topMthd : books.keySet()) {
				if (hostNds.contains(topMthd)) {
					Book4path book = (Book4path) (books.get(topMthd));
					for (IRecord iRecord : book.getRecords()) {
						Record4path record = (Record4path) iRecord;
						dis2records.add(record.getPathlen(), record);
					}
				}
			}
			if (dis2records.size() > 0) {
				PrintWriter printer = new PrintWriter(new BufferedWriter(new FileWriter(pathFile)));
				printer.println("classPath:" + DepJars.i().getUsedJarPathsStr());
				printer.println("pomPath:" + MavenUtil.i().getBaseDir());
				for (Record4path record : dis2records.flat()) {
					printer.println("pathLen:" + record.getPathlen() + "\n" + addJarPath(record.getPathStr()));
				}
				printer.close();
				return true;
			}
			// printer.println(distances);

		} catch (IOException e) {
			MavenUtil.i().getLog().error("can't write jarRisk ", e);

		}
		return false;
	}

	private String addJarPath(String mthdCallPath) {
		StringBuilder sb = new StringBuilder();
		String[] mthds = mthdCallPath.split("\\n");
		for (int i = 0; i < mthds.length - 1; i++) {
			// last method is risk method,don't need calculate.
			String mthd = mthds[i];
			String cls = SootUtil.mthdSig2cls(mthd);
			DepJar depJar = DepJars.i().getClassJar(cls);
			String jarPath = "";
			if (depJar != null)
				jarPath = depJar.getJarFilePaths(true).get(0);
			sb.append(mthd + " " + jarPath + "\n");
		}
		sb.append(mthds[mthds.length - 1]);
		return sb.toString();
	}
}
