package uk.ac.ebi.phenotype.web.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.ac.ebi.phenotype.dao.AnalyticsDAO;


/**
 * Wrap the data release version from the database
 */
@Component
public class DataReleaseVersionManager {

	@Autowired
	private AnalyticsDAO analyticsDAO;

	private String releaseVersion="";

	public DataReleaseVersionManager() {}

	public String getReleaseVersion() {
		// 0.1% of the time or on first request, refresh the version number
		if (releaseVersion.equals("") || Math.random()<0.001) {
			releaseVersion = analyticsDAO.getCurrentRelease();
		}

		return releaseVersion;
	}


	public void setReleaseVersion(String releaseVersion) {

		this.releaseVersion = releaseVersion;
	}
}
