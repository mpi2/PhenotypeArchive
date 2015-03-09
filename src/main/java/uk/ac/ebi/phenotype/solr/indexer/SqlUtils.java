package uk.ac.ebi.phenotype.solr.indexer;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by jmason on 26/03/2014.
 */
public class SqlUtils {

    /**
     * Overloaded helper methods for preparing SQL statement
     * @param s statement to use to insert
     * @param var variable being inserted
     * @param index position in the statement to insert the variable
     * @throws java.sql.SQLException
     */
    static public void setSqlParameter(PreparedStatement s, Integer var, int index) throws SQLException {
        if(var==null) {
            s.setNull(index, java.sql.Types.INTEGER);
        } else {
            s.setInt(index, var);
        }
    }
    static public void setSqlParameter(PreparedStatement s, String var, int index) throws SQLException {
        if(var==null) {
            s.setNull(index, java.sql.Types.VARCHAR);
        } else {
            s.setString(index, var);
        }
    }
    static public void setSqlParameter(PreparedStatement s, Boolean var, int index) throws SQLException {
        if(var==null) {
            s.setNull(index, java.sql.Types.BOOLEAN);
        } else {
            s.setBoolean(index, var);
        }
    }
    static public void setSqlParameter(PreparedStatement s, Float var, int index) throws SQLException {
        if(var==null) {
            s.setNull(index, java.sql.Types.FLOAT);
        } else {
            s.setFloat(index, var);
        }
    }
    static public void setSqlParameter(PreparedStatement s, Double var, int index) throws SQLException {
        if(var==null) {
            s.setNull(index, java.sql.Types.DOUBLE);
        } else {
            s.setDouble(index, var);
        }
    }
}
