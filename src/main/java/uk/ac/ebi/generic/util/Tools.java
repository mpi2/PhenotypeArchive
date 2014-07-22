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

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

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
		String kw;
		if ( qry.equals("*:*") ) {
			return target;	
		}
		else if ( qry.startsWith("%22") && qry.endsWith("%22") ) {
			kw = qry.replaceAll("%22|\\*", "").replaceAll("%20"," ");
		}
		else {
			kw = StringUtils.join(qry.split("%20"), "|").replaceAll("\\*","");
		}
		//System.out.println("-------" + qry + " vs " + kw);
		// (?im) at the beginning of the regex turns on CASE_INSENSITIVE and MULTILINE modes.
		// $0 in the replacement string is a placeholder whatever the regex matched in this iteration. 
		return target.replaceAll("(?im)"+kw, "<" + selector + " class='" + cssClass + "'>$0" + "</" + selector + ">");
	}
}
