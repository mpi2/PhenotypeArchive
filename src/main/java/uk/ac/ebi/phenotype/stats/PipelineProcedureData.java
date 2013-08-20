package uk.ac.ebi.phenotype.stats;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import uk.ac.ebi.phenotype.pojo.Parameter;
import uk.ac.ebi.phenotype.pojo.PhenotypeCallSummary;
import uk.ac.ebi.phenotype.pojo.Procedure;

public class PipelineProcedureData {

	
	private static final Logger log = Logger
			.getLogger(PipelineProcedureData.class);

	// null is returned which doesn't sit well with code so this number
	// indicates no week for a procedure to be carried out on a mouse
	public static final Integer NO_WEEK_FOR_PROCEDURE = 100000;

	List<PipelineProcedureTableView> listOfTables = new ArrayList<PipelineProcedureTableView>();

	public List<PipelineProcedureTableView> getListOfTables() {
		return listOfTables;
	}

	public void setListOfTables(List<PipelineProcedureTableView> listOfTables) {
		this.listOfTables = listOfTables;
	}

	public int getHighestNumberOfProceduresPerWeek() {
		return highestNumberOfProceduresPerWeek;
	}

	public void setHighestNumberOfProceduresPerWeek(int highestNumberOfProceduresPerWeek) {
		this.highestNumberOfProceduresPerWeek = highestNumberOfProceduresPerWeek;
	}

	private String pipelineName;
	private List<PhenotypeCallSummary> phenoCallSummaries;// e.g. Ak2<tm1a>

	// a SortedMap instance so it's sorted by week automatically
	private Map<Integer, List<Procedure>> proceduresByWeek;

	// includes or excludes the weeks row n the bottom
	int highestNumberOfProceduresPerWeek = 0; 
	
	// max number of processes/rows height we want minus the weeks/chevrons
	private int MAXHeight = 3;

	public PipelineProcedureData(String pipeLineName, List<PhenotypeCallSummary> allPhenotypeSummariesForGene) {
		this.pipelineName = pipeLineName;
		this.phenoCallSummaries = allPhenotypeSummariesForGene;
		proceduresByWeek = new TreeMap<Integer, List<Procedure>>();
	}

	/**
	 * Add a procedure identified by each age in weeks to this object so we can
	 * then order those objects
	 * 
	 * @param week
	 * @param proc
	 */
	public void add(Integer week, Procedure proc) {
		if (proceduresByWeek.containsKey(week)) {
			proceduresByWeek.get(week).add(proc);
		} else {
			List<Procedure> newProcedures = new ArrayList<Procedure>();
			newProcedures.add(proc);
			proceduresByWeek.put(week, newProcedures);
		}
	}

	public String getPipelineName() {
		return pipelineName;
	}

	public void setPipelineName(String pipelineName) {
		this.pipelineName = pipelineName;
	}

	public Map<Integer, List<Procedure>> getProceduresByWeek() {
		return proceduresByWeek;
	}

	public void setProceduresByWeek(
			Map<Integer, List<Procedure>> proceduresByWeek) {
		this.proceduresByWeek = proceduresByWeek;
	}

	private Map<Integer, List<Procedure>> getProcedureData() {

		Set<Integer> keySet = proceduresByWeek.keySet();

		for (Integer key : keySet) {
			int weekSize = proceduresByWeek.get(key).size();
			if (weekSize > highestNumberOfProceduresPerWeek) {
				highestNumberOfProceduresPerWeek = weekSize;
			}
		}

		return proceduresByWeek;
	}

	/**
	 * now we have all the data we need someone needs to call this method to
	 * calculate all the divs and rows and tables needed etc
	 */
	public void calculateDataStructure() {
		Map<Integer, List<Procedure>> procedureData = this.getProcedureData();

		List<List<Data>> columns = new ArrayList<List<Data>>();
		for (Integer week : procedureData.keySet()) {
			List<Data> column = new ArrayList<Data>();
			List<Procedure> weekData = procedureData.get(week);
			column = addExtraRowsIfNeeded(column, weekData);

			if (weekData.size() > MAXHeight) {

				// split the columns first
				int numberOfColumns = weekData.size() / MAXHeight;

				for (int i = 0; i < numberOfColumns + 1; i++) {
					List<Data> columnSmaller = new ArrayList<Data>();
					int indexFrom = i * MAXHeight;
					int indexTo = (i + 1) * MAXHeight;

					// reset indexTo to be size of array so we don't get an
					// outof bounds exception
					if (indexTo > weekData.size()) {
						indexTo = weekData.size();
					}

					List<Procedure> smallerWeekData = weekData.subList(indexFrom, indexTo);
					columnSmaller = addExtraRowsIfNeeded(columnSmaller,smallerWeekData);
					
					// if first column of a new set that is split over multiple
					// columns for one week then tell the column creator how
					// many colspans the bottom cell representing weeks should
					// have
					int colspan = 0;

					if (i == 0) {
						colspan = numberOfColumns + 1;
					}

					createColumn(columns, week, columnSmaller, smallerWeekData, colspan);
				}
			} else {
				createColumn(columns, week, column, weekData, 1);
			}

		}

		listOfTables = this.transformToTable(columns);
	}

	private List<Data> addExtraRowsIfNeeded(List<Data> column, List<Procedure> weekData) {
		if (weekData.size() < MAXHeight) {
			column = this.addExtraRows(column, weekData.size());
		}

		return column;
	}

	/**
	 * make Data with string and colspan for a column of procedures
	 * 
	 * @param columns
	 * @param week
	 * @param columnSmaller
	 * @param weekData
	 * @param colspan
	 */
	private void createColumn(List<List<Data>> columns, Integer week, List<Data> columnSmaller, List<Procedure> weekData, int colspan) {
		for (Procedure proc : weekData) {

			// if our phenocall summary contains an param id for this procedure
			// then add the param id here

			String phenoCallparam = this.phenoCallsHasParameter(proc);

			if ( ! phenoCallparam.equals("")) {
				columnSmaller.add(new Data(proc.getName(), phenoCallparam));
			}else{
				columnSmaller.add(new Data(proc.getName()));
			}
			
		}

		// if week is the no weeks static integer then there is no week assigned
		// and we need to put in the unassigned week group label
		if (week == PipelineProcedureData.NO_WEEK_FOR_PROCEDURE) {
			columnSmaller.add(new Data("unassigned week", colspan));
		} else {
			columnSmaller.add(new Data("wk " + week, colspan));
		}
		columns.add(columnSmaller);
	}

	/**
	 * return the parameter id of the any pheno call that matches this procedure
	 * 
	 * @param proc
	 * @return
	 */
	private String phenoCallsHasParameter(Procedure proc) {
		String id = "";

		if (phenoCallSummaries != null) {
			for (Parameter procParam : proc.getParameters()) {
				for (PhenotypeCallSummary summary : phenoCallSummaries) {
					if (summary.getParameter().getStableId()==procParam.getStableId()){
						return procParam.getStableId();
					}
				}
			}
		}

		return id;
	}

	/**
	 * transform the week data into a form we can use to write the html rows -
	 * splits data up into tables for seperate rows in page if needs be
	 * 
	 * @param columns
	 * @return
	 */
	private List<PipelineProcedureTableView> transformToTable(List<List<Data>> columns) {
		int lastSpan = 0;
		int spansUsed = 0;
		List<PipelineProcedureTableView> tables = new ArrayList<PipelineProcedureTableView>();
		int acceptableTableLength = 9;

		// if table too long then split into two tables currently over 9 columns
		// width
		if (columns.size() > acceptableTableLength) {
			int numberOfTables = columns.size() / acceptableTableLength;

			// +1 as int will be one below whats needed
			for (int i = 0; i < numberOfTables + 1; i++) {
				int indexFrom = i * acceptableTableLength;
				int indexTo = (i + 1) * acceptableTableLength;

				if (indexTo > columns.size()) {
					indexTo = columns.size();
				}

				List<List<Data>> smallerTableData = columns.subList(indexFrom, indexTo);

				// need to get the last in the column and set it's colspan to
				// the remainder of the last colspn
				for (int j = 0; j < smallerTableData.size(); j++) {
					List<Data> column = smallerTableData.get(j);
					Data lastCellOfColumn = column.get(MAXHeight);

					// if first column of a new table and the
					// colspan has been set to zero change it to
					// the last colspan minus any that have
					// already been used on the previous table!
					if (j == 0) {
						if (lastCellOfColumn.getColspan() == 0) {
							int spansLeft = lastSpan - spansUsed;
							lastCellOfColumn.setColspan(spansLeft);
						}
					}

					if (lastCellOfColumn.getColspan() != 0) {
						lastSpan = lastCellOfColumn.getColspan();
					}
					spansUsed++;
				}

				transformColumnsToTables(smallerTableData, tables);
			}

		} else {
			transformColumnsToTables(columns, tables);
		}
		return tables;

	}

	/**
	 * 
	 * @param smallerTableData
	 *            - data for sub table
	 * @param tables
	 *            data structure that we add the new tables to
	 */
	private void transformColumnsToTables(List<List<Data>> smallerTableData,
			List<PipelineProcedureTableView> tables) {

		// we know the height of the table is maxHeight or less - add argument
		// later we know the length of the desired table is number of columns

		PipelineProcedureTableView table = new PipelineProcedureTableView();
		int tableHeight = MAXHeight;
		int tableLength = smallerTableData.size();

		// tableHeight plus 1 to get the wks we added
		for (int i = 0; i < tableHeight + 1; i++) {

			Row row = new Row();

			for (int j = 0; j < tableLength; j++) {
				Data tempData = smallerTableData.get(j).get(i);
				row.getCellData().add(tempData);
			}
			table.getRows().add(row);
		}

		tables.add(table);
	}

	private List<Data> addExtraRows(List<Data> column, int sizeOfCurrentList) {
		int rowsToAdd = MAXHeight - sizeOfCurrentList;

		for (int i = 0; i < rowsToAdd; i++) {
			column.add(new Data(""));
		}

		return column;
	}

}
