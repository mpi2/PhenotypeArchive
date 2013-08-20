package uk.ac.ebi.phenotype.stats;

import java.util.ArrayList;
import java.util.List;

public class Row {
		private List<Data> cellData=new ArrayList<Data>();

		public List<Data> getCellData() {
			return cellData;
		}

		public void setCellData(List<Data> cellData) {
			this.cellData = cellData;
		}
		
	
		
}
