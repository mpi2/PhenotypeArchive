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

package uk.ac.ebi.phenotype.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;

/**
 *
 * @author mrelac
 */
public class Utils { 
    protected static Logger logger = Logger.getLogger(Utils.class);
    
    /**
     * Given an <code>Object</code> that may be null or may be a float or double, this
     * method attempts to convert the value to a <code>Double</code>. If successful,
     * the <code>Double</code> value is returned; otherwise, <code>null</code> is returned.
     * NOTE: the [non-null] object is first converted to a string and is trimmed of whitespace.
     * @param o the object to try to convert
     * @return the converted value, if <em>o</em> is a <code>Float or Double</code>; null otherwise
     */
    public static Double tryParseDouble(Object o) {
        if (o == null)
            return null;
        
        Double retVal = null;
        try {
            retVal = Double.parseDouble(o.toString().trim());
        }
        catch (NumberFormatException nfe ) { }
        
        return retVal;
    }
    
    /**
     * Given an <code>Object</code> that may be null or may be an Integer, this
     * method attempts to convert the value to an <code>Integer</code>. If successful,
     * the <code>Integer</code> value is returned; otherwise, <code>null</code> is returned.
     * NOTE: the [non-null] object is first converted to a string and is trimmed of whitespace.
     * @param o the object to try to convert
     * @return the converted value, if <em>o</em> is an <code>Integer</code>; null otherwise
     */
    public static Integer tryParseInt(Object o) {
        if (o == null)
            return null;
        
        Integer retVal = null;
        try {
            retVal = Integer.parseInt(o.toString().trim().replace(",", ""));    // Remove commas. Number strings like '48,123' don't parse.
        }
        catch (NumberFormatException nfe ) { }
        
        return retVal;
    }
    
    /**
     * Given a date string, this method attempts to convert the date to a <code>
     * java.sql.Date</code> object and, if successful, returns the date. If
     * not successful, returns null.
     * 
     * @param dateString The date string against which to attempt conversion
     * @return the <code>java.sql.Date</code> date, if successful; null otherwise
     */
    public static java.sql.Date tryParseToDbDate(String dateString) {
        java.sql.Date retVal = null;
        
        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = df.parse(dateString);
            retVal = new java.sql.Date(date.getTime());
        }
        catch(ParseException e) { }
        
        return retVal;
    }
    
    /**
     * Returns a string, wrapped in <code>wrapper</code>. If value was null,
     * an empty pair of quotes is returned.
     * @param value value to be wrapped
     * @param wrapper the wrapper
     * @return 
     */
    public static String wrap(String value, String wrapper) {
        String s = (value == null ? "" : value);
        return wrapper + s + wrapper;
    }
    
    /**
     * Given a comma-separated string of values, returns a string containing
     * all of the values, with whitespace removed (using trim()). If <b>intArray
     * </b> is null or empty, an empty string is returned. If <b>intArray</b>
     * contains only a single int, that value, less whitespace, is returned.
     * 
     * @param intArray string of comma-separated values to clean (trim)
     * @return   a string containing all of the values, with whitespace removed
     * (using trim()).
     */
    public static String cleanIntArray(String intArray) {
        if (intArray == null)
            return "";
        
        String retVal = "";
        String[] sA = intArray.split(",");
        if (sA.length == 0)
            return intArray.trim();
        
        for (String s : sA) {
            if ( ! retVal.isEmpty())
                retVal += ",";
            retVal += s.trim();
        }
        
        return retVal;
    }
    
    /**
     * Return the string representation of the specified <code>milliseconds</code>
     * in hh:mm:ss format. NOTE: year, month, and day do not participate in the
     * computation. If milliseconds is longer than 24 hours, incorrect results
     * will be returned.
     * 
     * @param milliseconds
     * @return 
     */
    public static String msToHms(Long milliseconds) {
        String result = String.format("%02d:%02d:%02d", 
            TimeUnit.MILLISECONDS.toHours(milliseconds),
            TimeUnit.MILLISECONDS.toMinutes(milliseconds) - 
            TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliseconds)),
            TimeUnit.MILLISECONDS.toSeconds(milliseconds) - 
            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds)));

        return result;
    }
}
