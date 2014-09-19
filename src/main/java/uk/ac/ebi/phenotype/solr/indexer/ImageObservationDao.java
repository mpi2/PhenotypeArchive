package uk.ac.ebi.phenotype.solr.indexer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import uk.ac.ebi.phenotype.service.dto.ImageDTO;

public class ImageObservationDao {

	private String dbUrl;
	private String user;
	private String pass;

	public ImageObservationDao(String dbUrl, String user, String pass) {
		this.dbUrl = dbUrl;
		this.user = user;
		this.pass = pass;
	}

	public List<ImageDTO> setExtraFields(List<ImageDTO> imageDTOsFromExperimentCore) {
		String queryString = "SELECT FULL_RESOLUTION_FILE_PATH, omero_id,"+ImageDTO.DOWNLOAD_FILE_PATH + ","+ImageDTO.FULL_RESOLUTION_FILE_PATH + "  FROM image_record_observation where "+ImageDTO.DOWNLOAD_FILE_PATH+"=?";
				
		try (PreparedStatement statement = getConnection().prepareStatement(queryString)) {
			for(ImageDTO imageDTO: imageDTOsFromExperimentCore){
				String downloadFilePath=imageDTO.getDownloadFilePath();
			statement.setString(1, downloadFilePath);
			
			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				imageDTO.setFullResolutionFilePath(resultSet.getString("FULL_RESOLUTION_FILE_PATH"));
				imageDTO.setOmeroId(resultSet.getInt("omero_id"));
			}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		

		return imageDTOsFromExperimentCore;
	}

	private Connection getConnection() {
		try {
			System.out.println("making import connection url=" + dbUrl
					+ " user=" + user + " pass=" + pass);
			Connection conn = DriverManager.getConnection(dbUrl, user, pass);
			return conn;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public List<String> getNewImageDownloadPaths() {
		List<String> downloadUrls = new ArrayList<String>();
		String queryString = "SELECT DOWNLOAD_FILE_PATH FROM komp2.image_record_observation";
		Statement stmt = null;
		try (Connection conn = this.getConnection()) {
			stmt = conn.createStatement();

			boolean ok = false;

			ok = stmt.execute(queryString);

			ResultSet rSet = stmt.getResultSet();
			while (rSet.next()) {
				downloadUrls.add(rSet.getString("DOWNLOAD_FILE_PATH"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return downloadUrls;
	}
	
	public List<String> getSolrDocomentsBeans() {
		List<String> downloadUrls = new ArrayList<String>();
		String queryString = "SELECT DOWNLOAD_FILE_PATH FROM komp2.image_record_observation";
		Statement stmt = null;
		try (Connection conn = this.getConnection()) {
			stmt = conn.createStatement();

			boolean ok = false;

			ok = stmt.execute(queryString);

			ResultSet rSet = stmt.getResultSet();
			while (rSet.next()) {
				downloadUrls.add(rSet.getString("DOWNLOAD_FILE_PATH"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return downloadUrls;
	}


}
