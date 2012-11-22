/**
 * Copyright © 2011-2012 EMBL - European Bioinformatics Institute
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License.  
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.ebi.generic.util;

import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.util.Map;
import java.util.HashMap;
import java.io.FileOutputStream;

/**
* Creating spreadsheet using Apache POI.
* code modified from Apache POI timesheet example
*/

public class ExcelWorkBook {

	Workbook wb = null;
	private String[] titles = null;
	private Object[][] tableData = null;
	private String sheetTitle = null;
	
	public ExcelWorkBook(String[] titles, Object[][] tableData, String sheetTitle) throws Exception {
        
		this.titles = titles;
		this.tableData = tableData;	
		this.sheetTitle = sheetTitle;
		
		// create new workbook
		this.wb = new HSSFWorkbook();

		// create new sheet
		Sheet sheet = wb.createSheet(sheetTitle); // do not exceed 31 characters
		PrintSetup printSetup = sheet.getPrintSetup();
		printSetup.setLandscape(true);
		sheet.setFitToPage(true);
		sheet.setHorizontallyCenter(true);
				
    	//header row
    	Row headerRow = sheet.createRow(0);
    	//headerRow.setHeightInPoints(40);
    	Cell headerCell;
    	for (int i = 0; i < titles.length; i++) {
    		headerCell = headerRow.createCell(i);
    		headerCell.setCellValue(titles[i]);
        	//headerCell.setCellStyle(styles.get("header"));
    	}

    	// data rows
    	// Create a row and put some cells in it. Rows are 0 based.
    	// Then set value for that created cell
    	for (int i=0; i<tableData.length; i++) {
    		Row row = sheet.createRow(i+1);  // data starts from row 1	 		
    		for (int j = 0; j < tableData[i].length; j++) {  
    			Cell cell = row.createCell(j);   
    			cell.setCellValue((String)tableData[i][j]);  
    			System.out.println((String)tableData[i][j]);
    		}
    	}    
	}

	public Workbook fetchWorkBook() {
		return this.wb;
	}	
}