package uk.ac.ebi.phenotype.web.controller;

import java.io.BufferedOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
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
}
