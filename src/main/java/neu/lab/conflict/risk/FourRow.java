package neu.lab.conflict.risk;

import java.util.List;

public class FourRow {
	public List<String> mthdRow;
	public List<String> mthdNameRow;
	public List<String> serviceRow;
	public List<String> serviceNameRow;
	public FourRow(List<String> mthdRow, List<String> mthdNameRow, List<String> serviceRow,
			List<String> serviceNameRow) {
		this.mthdRow = mthdRow;
		this.mthdNameRow = mthdNameRow;
		this.serviceRow = serviceRow;
		this.serviceNameRow = serviceNameRow;
	}
	
}
