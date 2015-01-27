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
import uk.ac.ebi.phenotype.service.dto.AlleleDTO;
import uk.ac.ebi.phenotype.service.dto.ImageDTO;
import uk.ac.ebi.phenotype.solr.indexer.utils.IndexerMap;

import javax.annotation.Resource;
import javax.sql.DataSource;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * class to load the image data into the solr core - use for impc data first
 * then we can do sanger images as well?
 *
 * @author jwarren
 *
 */
public class ImpcImagesIndexer extends AbstractIndexer {

	private static final Logger logger = LoggerFactory
			.getLogger(ImpcImagesIndexer.class);

	@Autowired
	@Qualifier("observationIndexing")
	private SolrServer observationService;

	@Autowired
	@Qualifier("impcImagesIndexing")
	SolrServer server;

	@Autowired
	@Qualifier("alleleIndexing")
	SolrServer alleleIndexing;

	@Autowired
	@Qualifier("komp2DataSource")
	DataSource komp2DataSource;

	@Resource(name = "globalConfiguration")
	private Map<String, String> config;

	private Map<String, List<AlleleDTO>> alleles;
	private Map<String, ImageBean> imageBeans;

	public ImpcImagesIndexer() {

		super();
	}

	public static final long MIN_EXPECTED_ROWS = 9500;

	@Override
	public void validateBuild() throws IndexerException {
		SolrQuery query = new SolrQuery().setQuery("*:*").setRows(0);
		try {
			Long numFound = server.query(query).getResults().getNumFound();
			if (numFound < MIN_EXPECTED_ROWS) {
				throw new IndexerException("validateBuild(): Expected "
						+ MIN_EXPECTED_ROWS + " rows but found " + numFound
						+ " rows.");
			}
			logger.info("MIN_EXPECTED_ROWS: " + MIN_EXPECTED_ROWS
					+ ". Actual rows: " + numFound);
		} catch (SolrServerException sse) {
			throw new IndexerException(sse);
		}
	}

	public static void main(String[] args) throws IOException,
			SolrServerException {

		OptionParser parser = new OptionParser();

		// parameter to indicate which spring context file to use
		parser.accepts("context").withRequiredArg().ofType(String.class);

		OptionSet options = parser.parse(args);
		String context = (String) options.valuesOf("context").get(0);

		logger.info("Using application context file {}", context);

		ApplicationContext applicationContext;
		try {

			// Try context as a file resource
			applicationContext = new FileSystemXmlApplicationContext("file:"
					+ context);

		} catch (RuntimeException e) {

			logger.warn("An error occurred loading the app-config file: {}",
					e.getMessage());

			// Try context as a class path resource
			applicationContext = new ClassPathXmlApplicationContext(context);

			logger.warn("Using classpath app-config file: {}", context);

		}
		// Wire up spring support for this application

		ImpcImagesIndexer main = new ImpcImagesIndexer();
		applicationContext.getAutowireCapableBeanFactory()
				.autowireBeanProperties(main,
						AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, true);

		// System.out.println("solrUrl="+solrUrl);
		try {
			main.run();
			main.validateBuild();
		} catch (IndexerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		logger.info("Process finished.  Exiting.");
	}

	@Override
	public void run() throws IndexerException {
		System.out.println("running impc_images indexer");
		System.out.println("populating image urls from db");
		imageBeans=populateImageUrls();
		System.out.println("Image beans map size="+imageBeans.size());
		if(imageBeans.size()<100){
			System.err.println("Didn't get any image entries from the db with omero_ids set so exiting the impc_image Indexer!!");
		}
		System.out.println("populating alleles");
		this.alleles = populateAlleles();
		System.out.println("populated alleles");
		String impcMediaBaseUrl = config.get("impcMediaBaseUrl");
		System.out.println("omeroRootUrl=" + impcMediaBaseUrl);
		try {

			server.deleteByQuery("*:*");
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
					if (imageBeans.containsKey(downloadFilePath)) {
						// System.out.println("imageDTO="+imageDTO);
						ImageBean iBean = imageBeans.get(downloadFilePath);
						String fullResFilePath = iBean.fullResFilePath;
						// System.out.println("fullResFilePath="+fullResFilePath);
						imageDTO.setFullResolutionFilePath(fullResFilePath);
						int omeroId = iBean.omeroId;
						imageDTO.setOmeroId(omeroId);
						// need to add a full path to image in omero as part of
						// api
						// e.g.
						// https://wwwdev.ebi.ac.uk/mi/media/omero/webgateway/render_image/4855/
						if (omeroId != 0 && downloadFilePath != null) {
							// System.out.println("setting downloadurl="+impcMediaBaseUrl+"/render_image/"+omeroId);
							// /webgateway/archived_files/download/
							imageDTO.setDownloadUrl(impcMediaBaseUrl
									+ "/archived_files/download/" + omeroId);
							imageDTO.setJpegUrl(impcMediaBaseUrl
									+ "/render_image/" + omeroId);

						} else {
							System.out.println("omero id is null for "
									+ downloadFilePath);
						}

						// add the extra stuf we need for the searching and
						// faceting
						// here
						if (imageDTO.getGeneAccession() != null
								&& !imageDTO.getGeneAccession().equals("")) {

							String geneAccession = imageDTO.getGeneAccession();
							if (alleles.containsKey(geneAccession)) {
								populateImageDtoStatuses(imageDTO,
										geneAccession);

								if (imageDTO.getSymbol() != null) {
									String symbolGene = imageDTO.getSymbol()
											+ "_" + imageDTO.getGeneAccession();
									imageDTO.setSymbolGene(symbolGene);
									//System.out.println("setting symbol_gene="+symbolGene);
								}
							}

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
		} catch (SolrServerException | IOException e) {
			e.printStackTrace();
			throw new IndexerException(e);
		}

	}

	/**
	 * Get the image urls from the db using the download_path from Harwell for images that have an omero_id that should have already bean set by the
	 * python scripts that update the impc_images dev.
	 * 
	 * @throws IndexerException
	 */
	private  Map<String, ImageBean> populateImageUrls() throws IndexerException {
		Map<String, ImageBean> imageBeansMap=new HashMap<>();
		final String getExtraImageInfoSQL = "SELECT FULL_RESOLUTION_FILE_PATH, omero_id, "
				+ ImageDTO.DOWNLOAD_FILE_PATH
				+ ", "
				+ ImageDTO.FULL_RESOLUTION_FILE_PATH
				+ " FROM image_record_observation "
				+ " WHERE omero_id is not null";
		try (PreparedStatement statement = komp2DataSource.getConnection()
				.prepareStatement(getExtraImageInfoSQL)) {
			ResultSet resultSet = statement.executeQuery();
			 
			while (resultSet.next()) {

				int omeroId = resultSet.getInt("omero_id");
				String downloadFilePath = resultSet
						.getString(ImageDTO.DOWNLOAD_FILE_PATH);
				String fullResFilePath = resultSet
						.getString(ImageDTO.FULL_RESOLUTION_FILE_PATH);
				ImageBean bean = new ImageBean();
				bean.omeroId = omeroId;
				bean.fullResFilePath = fullResFilePath;
				imageBeansMap.put(downloadFilePath, bean);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new IndexerException(e);
		}
		return imageBeansMap;
	}

	private class ImageBean {
		int omeroId;
		String fullResFilePath;
	}

	@Override
	protected Logger getLogger() {

		return logger;
	}

	public Map<String, List<AlleleDTO>> populateAlleles()
			throws IndexerException {

		return IndexerMap.getGeneToAlleles(alleleIndexing);
	}

	private void populateImageDtoStatuses(ImageDTO img, String geneAccession) {

		if (alleles.containsKey(geneAccession)) {
			List<AlleleDTO> localAlleles = alleles.get(geneAccession);
			for (AlleleDTO allele : localAlleles) {

				// so some of the fields below a that we have multiples for for
				// SangerIMages we only have one for ObservationDTOs??????
				// <field column="marker_symbol"
				// xpath="/response/result/doc/str[@name='marker_symbol']" />
				// <field column="marker_name"
				if (allele.getMarkerSymbol() != null) {
					img.addSymbol(allele.getMarkerSymbol());
				}
				if (allele.getMarkerName() != null) {
					img.addMarkerName(allele.getMarkerName());
				}

				// // xpath="/response/result/doc/str[@name='marker_name']" />
				// // <field column="marker_synonym"
				if (allele.getMarkerSynonym() != null) {
					img.addMarkerSynonym(allele.getMarkerSynonym());
				}
				// //
				// xpath="/response/result/doc/arr[@name='marker_synonym']/str"
				// // />
				// // <field column="marker_type"
				if (allele.getMarkerType() != null) {
					img.addMarkerType(allele.getMarkerType());

				}

				// xpath="/response/result/doc/str[@name='marker_type']" />
				if (allele.getHumanGeneSymbol() != null) {
					img.addHumanGeneSymbol(allele.getHumanGeneSymbol());
				}
				// <field column="human_gene_symbol"
				// xpath="/response/result/doc/arr[@name='human_gene_symbol']/str"
				// />
				//
				if (allele.getStatus() != null) {
					// System.out.println("adding status="+allele.getStatus());
					img.addStatus(allele.getStatus());
				}
				// <!-- latest project status (ES cells/mice production status)
				// -->
				// <field column="status"
				// xpath="/response/result/doc/str[@name='status']"
				// />
				//
				if (allele.getImitsPhenotypeStarted() != null) {
					img.addImitsPhenotypeStarted(allele
							.getImitsPhenotypeStarted());
				}
				// <!-- latest mice phenotyping status for faceting -->
				// <field column="imits_phenotype_started"
				// xpath="/response/result/doc/str[@name='imits_phenotype_started']"
				// />
				// <field column="imits_phenotype_complete"
				if (allele.getImitsPhenotypeComplete() != null) {
					img.addImitsPhenotypeComplete(allele
							.getImitsPhenotypeComplete());
				}
				// xpath="/response/result/doc/str[@name='imits_phenotype_complete']"
				// />
				// <field column="imits_phenotype_status"
				if (allele.getImitsPhenotypeStatus() != null) {
					img.addImitsPhenotypeStatus(allele
							.getImitsPhenotypeStatus());
				}
				// xpath="/response/result/doc/str[@name='imits_phenotype_status']"
				// />
				//
				// <!-- phenotyping status -->
				// <field column="latest_phenotype_status"
				// xpath="/response/result/doc/str[@name='latest_phenotype_status']"
				// />
				if (allele.getLegacyPhenotypeStatus() != null) {
					img.setLegacyPhenotypeStatus(allele
							.getLegacyPhenotypeStatus());
				}
				// <field column="legacy_phenotype_status"
				// xpath="/response/result/doc/int[@name='legacy_phenotype_status']"
				// />
				//
				// <!-- production/phenotyping centers -->
				img.setLatestProductionCentre(allele
						.getLatestProductionCentre());
				// <field column="latest_production_centre"
				// xpath="/response/result/doc/arr[@name='latest_production_centre']/str"
				// />
				// <field column="latest_phenotyping_centre"
				img.setLatestPhenotypingCentre(allele
						.getLatestPhenotypingCentre());
				// xpath="/response/result/doc/arr[@name='latest_phenotyping_centre']/str"
				// />
				//
				// <!-- alleles of a gene -->
				img.setAlleleName(allele.getAlleleName());
				// <field column="allele_name"
				// xpath="/response/result/doc/arr[@name='allele_name']/str" />
				//
				// </entity>
				img.setSubtype(allele.getMarkerType());
				img.addLatestPhenotypeStatus(allele.getLatestPhenotypeStatus());
				if (img.getLegacyPhenotypeStatus() != null) {
					img.setLegacyPhenotypeStatus(allele
							.getLegacyPhenotypeStatus());
				}
			}
		}

	}

}
