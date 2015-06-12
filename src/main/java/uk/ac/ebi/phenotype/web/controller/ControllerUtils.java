/*******************************************************************************
 * Copyright 2015 EMBL - European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 *******************************************************************************/
package uk.ac.ebi.phenotype.web.controller;

import java.io.BufferedOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import au.com.bytecode.opencsv.CSVWriter;


public class ControllerUtils {
	
	
	public static void writeAsCSV(String toWrite, String fileName, HttpServletResponse response) throws IOException{
		
	    response.setContentType("text/csv;charset=utf-8"); 
	    response.setHeader("Content-Disposition","attachment; filename="+fileName);
	    OutputStream resOs= response.getOutputStream();  
	    OutputStream buffOs= new BufferedOutputStream(resOs);   
	    OutputStreamWriter outputwriter = new OutputStreamWriter(buffOs);  
        outputwriter.write(toWrite);  
	    outputwriter.close();
	    outputwriter.close();
	}
	
	
	public static void writeAsCSV(List<String[]> toWrite, String fileName, HttpServletResponse response) throws IOException{
		
	    response.setContentType("text/csv;charset=utf-8"); 
	    response.setHeader("Content-Disposition","attachment; filename="+fileName);
	    OutputStream resOs= response.getOutputStream();  
	    OutputStream buffOs= new BufferedOutputStream(resOs);   
	    CSVWriter writer = new CSVWriter(new OutputStreamWriter(buffOs));
	    writer.writeAll(toWrite);
		writer.close();
	}
	
	
	public static void writeAsCSVMultipleTables(List<List<String[]>> toWrite, String fileName, HttpServletResponse response) throws IOException{
		
	    response.setContentType("text/csv;charset=utf-8"); 
	    response.setHeader("Content-Disposition","attachment; filename="+fileName);
	    OutputStream resOs= response.getOutputStream();  
	    OutputStream buffOs= new BufferedOutputStream(resOs);   
	    CSVWriter writer = new CSVWriter(new OutputStreamWriter(buffOs));
	    for (List<String[]> table : toWrite){
	    	writer.writeAll(table);
	    	writer.writeNext(new String[0]);
	    }
		writer.close();
	}
	
	
}


