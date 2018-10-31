package neu.lab.conflict;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import neu.lab.conflict.writer.RepairWriter;

@Mojo(name = "conflictRepair", defaultPhase = LifecyclePhase.VALIDATE)
public class ConflictRepairMojo extends ConflictMojo {

	@Override
	public void run() {
		// TODO Auto-generated method stub
		RepairWriter repairWriter = new RepairWriter();
		repairWriter.write("D:\\riddle_risklevel\\outlevel\\");	//服务器
//		repairWriter.write("/Users/wangchao/Develop/neu/");		//mac
//		riskLevelWriter.writeRiskLevelXML("/Users/wangchao/Develop/neu/RiskLevel.xml", append);	//mac下路径
		//riskLevelWriter.writeRiskLevel("D:\\riddle\\RiskLevel.xml", append);	//win下路径
	}

}
