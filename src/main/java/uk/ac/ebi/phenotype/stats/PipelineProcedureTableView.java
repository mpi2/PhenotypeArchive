package uk.ac.ebi.phenotype.stats;

import java.util.ArrayList;
import java.util.List;



public class PipelineProcedureTableView {
 private List<Row> rows=new ArrayList<Row>();

public List<Row> getRows() {
	return rows;
}

public void setRows(List<Row> rows) {
	this.rows = rows;
}
}
