/**
 * Copyright © 2011-2014 EMBL - European Bioinformatics Institute
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

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import javax.sql.DataSource;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class Tools {
	
	// check a string contains only numbers
	public static boolean isNumeric(String input) {
		try {
			Integer.parseInt(input);
			return true;
		}
		catch (NumberFormatException e) {
			// string is not numeric
			return false;
		}
	} 
	public static String superscriptify(String input) {
		return input.replace("<", "<sup")
				.replace(">", "/sup>")
				.replace("<sup", "<sup>")
				.replace("/sup>", "</sup>");		
	}
	
        /**
         * Given two dates (in any order), returns a <code>String</code> in the
         * format "xxx days, yyy hours, zzz minutes, nnn seconds" that equals
         * the absolute value of the time difference between the two days.
         * @param date1 the first operand
         * @param date2 the second operand
         * @return a <code>String</code> in the format "dd:hh:mm:ss" that equals the
         * absolute value of the time difference between the two date.
         */
	public static String dateDiff(Date date1, Date date2) {
            long lower = Math.min(date1.getTime(), date2.getTime());
            long upper = Math.max(date1.getTime(), date2.getTime());
            long diff = upper - lower;
            
            long days = diff / (24 * 60 * 60 * 1000);
            long hours = diff / (60 * 60 * 1000) % 24;
            long minutes = diff / (60 * 1000) % 60;
            long seconds = diff / 1000 % 60;
            
            return String.format("%02d:%02d:%02d:%02d", days, hours, minutes, seconds);
	}
	
	public static String highlightMatchedStrIfFound(String qry, String target, String selector, String cssClass) {
		// the works for multiple words in the qry; it will match multiple places in the target string
		
		String kw = "";
		
		try {
			kw = URLDecoder.decode(qry, "UTF-8");
			//System.out.println("kw decoded: "+ kw);
		}	
		catch( Exception e){
			System.out.println("Failed to decode " + qry);
		}
		
		if ( qry.equals("*:*") ) {
			return target;	
		}
		else if ( kw.startsWith("\"") && kw.endsWith("\"") ) {
			// exact phrase search - with double quotes
			kw = kw.replace("\"", "")
				   .replace("(", "\\(")
				   .replace(")", "\\)");
		}
		else {
			// non phrase search - split string into words and search using OR
			// very loose match not using boundry: ie, matches anywhere in string -> less specificity
			
			StringBuffer patBuff = new StringBuffer();
			int count = 0;
			for ( String s : kw.split(" |,") ){
				count++;
				if ( count != kw.split(" ").length ){
					patBuff.append(s+"|");
				}
				else {
					patBuff.append(s);
				}
			}
			kw = patBuff.toString();
		}
		
		kw = kw.replace("*","")
				.replace("+", "\\+");
		
		//working pattern: vang\-like|2|\\(van|gogh,|Drosophila\\)
		
		// (?im) at the beginning of the regex turns on CASE_INSENSITIVE and MULTILINE modes.
		// $0 in the replacement string is a placeholder whatever the regex matched in this iteration. 
		
		target = target.replaceAll("<(.+)>", "<sup>$1</sup>");
		String result = target.replaceAll("(?im)"+kw, "<" + selector + " class='" + cssClass + "'>$0" + "</" + selector + ">");
		return result;
	}
	
	public static String[][] composeXlsTableData(List<String> rows) {
        int rowNum = rows.size();
        int colNum = (rows.size() > 0) ? rows.get(0).split("\t").length : 0; // title row, tells how many columns
       
        String[][] tableData = new String[rowNum][colNum];

        for (int i = 1; i < rowNum; i ++) {  // starts from 1 to skip title row
            String[] colVals = rows.get(i).split("\t");
            
            for (int j = 0; j < colVals.length; j++) {
                tableData[i][j] = colVals[j];
            }
        }
       
        return tableData;
    }
	
	public static String fetchOutputFieldsCheckBoxesHtml(String corename) {
		
		corename = (corename == null) ? "gene" : corename; 
		
		String htmlStr1 = "";
		String htmlStr2 = "";
		
		// main attrs.
		List<String> mainAttrs = new ArrayList<>();
		// additional information
		List<String> additionalInfos = new ArrayList<>();
					
		if ( corename.equals("gene") || corename.equals("marker_symbol")){
			
			// gene attr fields
			
			// these first 6 ones are checked by default
			mainAttrs.add("mgi_accession_id");
			mainAttrs.add("marker_symbol");
			mainAttrs.add("human_gene_symbol");
			mainAttrs.add("marker_name");
			mainAttrs.add("marker_synonym");
			mainAttrs.add("marker_type");
		    
			mainAttrs.add("latest_es_cell_status");
			mainAttrs.add("latest_mouse_status");
			mainAttrs.add("legacy_phenotype_status");
			mainAttrs.add("latest_phenotype_status"); 
			mainAttrs.add("latest_project_status"); 
			mainAttrs.add("latest_production_centre"); 
			mainAttrs.add("latest_phenotyping_centre"); 

			// gene has QC: ie, a record in experiment core
			additionalInfos.add("ensembl_gene_ids");
			additionalInfos.add("hasQc");

			// annotated mp term 
			additionalInfos.add("p_value");
			additionalInfos.add("mp_id");
			additionalInfos.add("mp_term");
			additionalInfos.add("mp_term_synonym");
			additionalInfos.add("mp_term_definition");

			// mp to hp mapping
			additionalInfos.add("hp_id");
			additionalInfos.add("hp_term");

			// disease fields 
			additionalInfos.add("disease_id");
			additionalInfos.add("disease_term");

			// impc images link
            additionalInfos.add("images_link");
			
			//GO stuff for gene : not shown for now
		}
		else if ( corename.equals("ensembl") ){
			
			// gene attr fields
			
			// these first 6 ones are checked by default
			mainAttrs.add("mgi_accession_id");
			mainAttrs.add("ensembl_gene_id");
			mainAttrs.add("marker_symbol");
			mainAttrs.add("human_gene_symbol");
			mainAttrs.add("marker_name");
			mainAttrs.add("marker_synonym");
			mainAttrs.add("marker_type");
		    
			mainAttrs.add("latest_es_cell_status");
			mainAttrs.add("latest_mouse_status");
			mainAttrs.add("legacy_phenotype_status");
			mainAttrs.add("latest_phenotype_status"); 
			mainAttrs.add("latest_project_status"); 
			mainAttrs.add("latest_production_centre"); 
			mainAttrs.add("latest_phenotyping_centre"); 

			// gene has QC: ie, a record in experiment core
			additionalInfos.add("hasQc");

			// annotated mp term 
			additionalInfos.add("p_value");
			additionalInfos.add("mp_id");
			additionalInfos.add("mp_term");
			additionalInfos.add("mp_term_synonym");
			additionalInfos.add("mp_term_definition");

			// mp to hp mapping
			additionalInfos.add("hp_id");
			additionalInfos.add("hp_term");

			// disease fields 
			additionalInfos.add("disease_id");
			additionalInfos.add("disease_term");

			// impc images link
            additionalInfos.add("images_link");
			
			//GO stuff for gene : not shown for now
		}
		
		else if ( corename.equals("disease") ) {
			mainAttrs.add("disease_id"); 
			mainAttrs.add("disease_term");
			
			additionalInfos.add("mgi_accession_id");	
			additionalInfos.add("marker_symbol");
			additionalInfos.add("human_gene_symbol");

			// annotated and inferred mp term
			additionalInfos.add("p_value");
			additionalInfos.add("mp_id");
			additionalInfos.add("mp_term");
			additionalInfos.add("mp_term_synonym");
			
			// mp to hp mapping
			additionalInfos.add("hp_id");
			additionalInfos.add("hp_term");
			
		}
		else if ( corename.equals("mp") ) {
			mainAttrs.add("mp_id");
			mainAttrs.add("mp_term");
			mainAttrs.add("mp_definition");
			mainAttrs.add("top_level_mp_id");
			mainAttrs.add("top_level_mp_term");
			
			//  mp to hp mapping
			additionalInfos.add("hp_id");
			additionalInfos.add("hp_term");
			
			// gene core stuff 
			additionalInfos.add("mgi_accession_id");	
			additionalInfos.add("marker_symbol");
			//additionalInfos.add("pheno_calls");
			additionalInfos.add("human_gene_symbol");
			
			//disease core stuff
			additionalInfos.add("disease_id");
			additionalInfos.add("disease_term"); 
		}
		else if ( corename.equals("ma") ) {
			mainAttrs.add("ma_id");
			mainAttrs.add("ma_term");
			mainAttrs.add("selected_top_level_ma_id");
			mainAttrs.add("selected_top_level_ma_term");
			
			// gene core stuff 
			additionalInfos.add("mgi_accession_id");	
			additionalInfos.add("marker_symbol");
			additionalInfos.add("human_gene_symbol");
			// impc images link
            additionalInfos.add("images_link");
		}
		else if ( corename.equals("hp") ) {
		
			mainAttrs.add("hp_id");
			mainAttrs.add("hp_term");
					
			//  hp to mp mapping
			mainAttrs.add("mp_id");
			mainAttrs.add("mp_term");
			mainAttrs.add("mp_definition");
			
			additionalInfos.add("top_level_mp_id");
			additionalInfos.add("top_level_mp_term");
			
			// gene core stuff 
			additionalInfos.add("mgi_accession_id");	
			additionalInfos.add("marker_symbol");
			//additionalInfos.add("pheno_calls");
			additionalInfos.add("human_gene_symbol");
			
			//disease core stuff
			additionalInfos.add("disease_id");
			additionalInfos.add("disease_term"); 
		}
		
		
		String dataType = corename.toUpperCase().replaceAll("_"," ");
		
		htmlStr1 += "<div class='cat'>" + dataType + " attributes</div>";
		for ( int i=0; i<mainAttrs.size(); i++ ){
			String checked = ""; 
			String checkedClass = "";
			
			if ( i < 6 ) {
				checked = "checked";
				checkedClass = "default";
			}
			
			String friendlyFieldName = mainAttrs.get(i).replaceAll("_", " ");
			htmlStr1 += "<input type='checkbox' class=" + checkedClass + " name='" + corename + "' value='" + mainAttrs.get(i) + "'" + checked + ">" + friendlyFieldName;
			if ( (i+1) % 3 == 0 ){
				htmlStr1 += "<br>";
			}
		}
		
		htmlStr2 += "<div class='cat'>Additional annotations to " + dataType + "</div>";
		for ( int i=0; i<additionalInfos.size(); i++ ){
			String friendlyFieldName = additionalInfos.get(i).replaceAll("_", " ");
			htmlStr2 += "<input type='checkbox' name='" + corename + "' value='" + additionalInfos.get(i) + "'>" + friendlyFieldName;
			if ( (i+1) % 3 == 0 ){
				htmlStr2 += "<br>";
			}
		}
		
		String hrStr = "<hr>";
		String checkAllBoxStr = "<button type='button' id='chkFields'>Check all fields</button>";
		
		return htmlStr1 + htmlStr2 + hrStr + checkAllBoxStr;
	}
}
