package uk.ac.ebi.phenotype.pojo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TableObject {
	private String title;
	private List<String> columnHeaders;
	private String sexType;

	public List<String> getColumnHeaders() {
		return columnHeaders;
	}

	public void setColumnHeaders(List<String> columnHeaders) {
		this.columnHeaders = columnHeaders;
	}

	public String getSexType() {
		return sexType;
	}

	public void setSexType(String sexType) {
		this.sexType = sexType;
	}

	public List<String> getRowHeaders() {
		return rowHeaders;
	}

	public void setRowHeaders(List<String> rowHeaders) {
		this.rowHeaders = rowHeaders;
	}

	public List<List<String>> getCellData() {
		return cellData;
	}

	public void setCellData(List<List<String>> cellData) {
		this.cellData = cellData;
	}

	private List<String> rowHeaders;
	private List<List<String>> cellData = new ArrayList<List<String>>();
	private String renderTo;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getRenderTo() {
		return renderTo;
	}

	public void setRenderTo(String renderTo) {
		this.renderTo = renderTo;
	}

	public void addRow(List<String> row) {
		this.cellData.add(row);
	}

	public void addColumn(List<String> column) {
		int colIndex = 0;
		if (this.cellData.size() > 0) {
			colIndex = this.cellData.get(0).size();// get the length of the rows
		}

		
			
			for (int i=0; i<column.size();i++) {
				List<String> newRow;
				if (this.cellData.size() < column.size()) {
				newRow = new ArrayList<String>();
				}else{
					newRow=this.cellData.get(i);
				}
				String cell=column.get(i);
					System.out.println("adding cell with new row=" + cell);
					newRow.add( colIndex, cell);
				this.cellData.add(newRow);
			}
		}

	public List<String> get(int row) {
		if(cellData!=null && cellData.size()>=row){
			return cellData.get(row);
		}
		return null;
	}
	
}
