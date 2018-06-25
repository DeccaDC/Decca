package neu.lab.conflict.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import neu.lab.conflict.Conf;
import neu.lab.conflict.soot.JarAna;
import neu.lab.conflict.statics.ClassDup;
import neu.lab.conflict.statics.NodeDup;
import neu.lab.conflict.vo.ClassVO;
import neu.lab.conflict.vo.MethodVO;
import neu.lab.conflict.vo.NodeConflict;

public class Cmp {
	public static void cmp() {
		List<String> path1 = new ArrayList<String>();
		path1.add("");
		Map<String, ClassVO> clsTb1 = JarAna.i().deconstruct(path1);
		List<String> path2 = new ArrayList<String>();
		path2.add(
				"");
		Map<String, ClassVO> clsTb2 = JarAna.i().deconstruct(path2);
		printLeft(clsTb1,clsTb2);
		printLeft(clsTb2,clsTb1);
	}

	private static void printLeft(Map<String, ClassVO> total, Map<String, ClassVO> some) {
		StringBuilder onlyCls = new StringBuilder("=====only classes\n");
		StringBuilder onlyMthd = new StringBuilder("=====only method\n");
		for (String cls : total.keySet()) {
			ClassVO clsInSome = some.get(cls);
			if (clsInSome == null) {
				onlyCls.append(cls + "\n");
			} else {
				ClassVO clsInTotal = total.get(cls);
				for (MethodVO method : clsInTotal.getMthds()) {
					if (!clsInSome.hasMethod(method.getMthdSig()))
						onlyMthd.append(method.getMthdSig()+"\n");
				}

			}
		}
		try {
			PrintWriter printer = new PrintWriter(
					new BufferedWriter(new FileWriter(new File("D:\\cWS\\notepad++\\diff.txt"), true)));
			printer.println(onlyCls);
			printer.println(onlyMthd);
			printer.close();
			}
		catch (Exception e) {
			e.printStackTrace();
		}
	} 
	
}
