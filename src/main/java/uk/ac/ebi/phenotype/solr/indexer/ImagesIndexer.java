package uk.ac.ebi.phenotype.solr.indexer;

import static uk.ac.ebi.phenotype.solr.indexer.utils.OntologyUtils.BATCH_SIZE;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import uk.ac.ebi.phenotype.service.ImageService;
import uk.ac.ebi.phenotype.service.ObservationService;
import uk.ac.ebi.phenotype.service.dto.ImageDTO;
import uk.ac.ebi.phenotype.service.dto.ObservationDTO;
import uk.ac.ebi.phenotype.service.dto.SangerImageDTO;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * class to load the image data into the solr core - use for impc data first
 * then we can do sanger images as well?
 * 
 * @author jwarren
 * 
 */
public class ImagesIndexer extends AbstractIndexer {

	private static final Logger logger = LoggerFactory.getLogger(ImagesIndexer.class);

	@Autowired
	@Qualifier("observationIndexing")
	private SolrServer observationService;

	@Autowired
	@Qualifier("impcImagesIndexing")
	SolrServer server;

	@Autowired
	@Qualifier("komp2DataSource")
	DataSource komp2DataSource;

	@Resource(name = "globalConfiguration")
	private Map<String, String> config;


	public ImagesIndexer() {

		super();
	}


	public static void main(String[] args)
	throws IOException, SolrServerException {

		OptionParser parser = new OptionParser();

		// parameter to indicate which spring context file to use
		parser.accepts("context").withRequiredArg().ofType(String.class);

		OptionSet options = parser.parse(args);
		String context = (String) options.valuesOf("context").get(0);

		logger.info("Using application context file {}", context);

		ApplicationContext applicationContext;
		try {

			// Try context as a file resource
			applicationContext = new FileSystemXmlApplicationContext("file:" + context);

		} catch (RuntimeException e) {

			logger.warn("An error occurred loading the app-config file: {}", e.getMessage());

			// Try context as a class path resource
			applicationContext = new ClassPathXmlApplicationContext(context);

			logger.warn("Using classpath app-config file: {}", context);

		}
		// Wire up spring support for this application

		ImagesIndexer main = new ImagesIndexer();
		applicationContext.getAutowireCapableBeanFactory().autowireBeanProperties(main, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, true);

		// System.out.println("solrUrl="+solrUrl);
		try {
			main.run();
		} catch (IndexerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		logger.info("Process finished.  Exiting.");
	}


	@Override
	public void run()
	throws IndexerException {

		String impcMediaBaseUrl = config.get("impcMediaBaseUrl");
		System.out.println("omeroRootUrl=" + impcMediaBaseUrl);
		final String getExtraImageInfoSQL = "SELECT FULL_RESOLUTION_FILE_PATH, omero_id, " + ImageDTO.DOWNLOAD_FILE_PATH + ", " + ImageDTO.FULL_RESOLUTION_FILE_PATH + " FROM image_record_observation " + " WHERE " + ImageDTO.DOWNLOAD_FILE_PATH + " = ?";
		try (PreparedStatement statement = komp2DataSource.getConnection().prepareStatement(getExtraImageInfoSQL)) {

			// TODO: Need to batch these up to do a set of images at a time
			// (currently works, but the number of images will grow beyond what
			// can be handled in a single query)
			SolrQuery query = ImageService.allImageRecordSolrQuery();
			int pos = 0;
			long total = Integer.MAX_VALUE;
			query.setRows(BATCH_SIZE);
			while (pos < total) {
				query.setStart(pos);
				QueryResponse response = null;
	
					response = observationService.query(query);
				
				total = response.getResults().getNumFound();
				List<ImageDTO> imageList = response.getBeans(ImageDTO.class);
				for (ImageDTO imageDTO : imageList) {

					String downloadFilePath = imageDTO.getDownloadFilePath();
					// System.out.println("trying downloadfilePath="+downloadFilePath);
					statement.setString(1, downloadFilePath);

					ResultSet resultSet = statement.executeQuery();
					// System.out.println("imageDTO="+imageDTO);
					while (resultSet.next()) {
						String fullResFilePath = resultSet.getString("FULL_RESOLUTION_FILE_PATH");
						// System.out.println("fullResFilePath="+fullResFilePath);
						imageDTO.setFullResolutionFilePath(fullResFilePath);
						int omeroId = resultSet.getInt("omero_id");
						imageDTO.setOmeroId(omeroId);
						// need to add a full path to image in omero as part of
						// api
						// e.g.
						// https://wwwdev.ebi.ac.uk/mi/media/omero/webgateway/render_image/4855/
						if (omeroId != 0 && downloadFilePath != null) {
							// System.out.println("setting downloadurl="+impcMediaBaseUrl+"/render_image/"+omeroId);
							// /webgateway/archived_files/download/
							imageDTO.setDownloadUrl(impcMediaBaseUrl + "/archived_files/download/" + omeroId);
							imageDTO.setJpegUrl(impcMediaBaseUrl + "/render_image/" + omeroId);

						} else {
							System.out.println("omero id is null for " + downloadFilePath);
						}
					}

					

				}
				pos += BATCH_SIZE;
				server.addBeans(imageList);
				if (pos % 1000 == 0) {
					System.out.println(" added ImageDTO" + pos + " beans");
				}

			}

		
			server.commit();
		} catch (SolrServerException | IOException | SQLException e) {
			e.printStackTrace();
			throw new IndexerException(e);
		}

	}


	public List<ImageDTO> getAllImageDTOs()
	throws SolrServerException {

		SolrQuery query = ImageService.allImageRecordSolrQuery();
		return observationService.query(query).getBeans(ImageDTO.class);

	}


	@Override
	protected Logger getLogger() {

		return logger;
	}

}
