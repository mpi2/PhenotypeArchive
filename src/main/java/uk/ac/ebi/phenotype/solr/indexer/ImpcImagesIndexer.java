package uk.ac.ebi.phenotype.solr.indexer;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import uk.ac.ebi.phenotype.service.ImageService;
import uk.ac.ebi.phenotype.service.MaOntologyService;
import uk.ac.ebi.phenotype.service.dto.AlleleDTO;
import uk.ac.ebi.phenotype.service.dto.ImageDTO;
import uk.ac.ebi.phenotype.solr.indexer.beans.OntologyTermBean;
import uk.ac.ebi.phenotype.solr.indexer.exceptions.IndexerException;
import uk.ac.ebi.phenotype.solr.indexer.exceptions.ValidationException;
import uk.ac.ebi.phenotype.solr.indexer.utils.IndexerMap;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * class to load the image data into the solr core - use for impc data first
 * then we can do sanger images as well?
 *
 * @author jwarren
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
	
	@Autowired
	MaOntologyService maService;

	@Resource(name = "globalConfiguration")
	private Map<String, String> config;

	private Map<String, List<AlleleDTO>> alleles;
	private Map<String, ImageBean> imageBeans;
	String excludeProcedureStableId="IMPC_PAT_002";

	private Map<String, String> parameterStableIdToMaTermIdMap;


	public ImpcImagesIndexer() {
		super();
	}


	@Override
	public void validateBuild() throws IndexerException {

		Long numFound = getDocumentCount(server);

		if (numFound <= MINIMUM_DOCUMENT_COUNT)
			throw new IndexerException(new ValidationException("Actual impc_images document count is " + numFound + "."));

		if (numFound != documentCount)
			logger.warn("WARNING: Added " + documentCount + " impc_images documents but SOLR reports " + numFound + " documents.");
		else
			logger.info("validateBuild(): Indexed " + documentCount + " impc_images documents.");
	}


	public static void main(String[] args) throws IndexerException {

		ImpcImagesIndexer main = new ImpcImagesIndexer();
		main.initialise(args);
		main.run();
		main.validateBuild();

		logger.info("Process finished.  Exiting.");
	}


	@Override
	public void run() throws IndexerException {

		int count = 0;

		logger.info("running impc_images indexer");

		try{
	    	parameterStableIdToMaTermIdMap=this.populateParameterStableIdToMaIdMap();
	    	} catch(SQLException e){
	    		e.printStackTrace();
	    	}
		logger.info("populating image urls from db");
		imageBeans = populateImageUrls();
		logger.info("Image beans map size=" + imageBeans.size());

		if (imageBeans.size() < 100) {
			logger.error("Didn't get any image entries from the db with omero_ids set so exiting the impc_image Indexer!!");
		}

		logger.info("populating alleles");
		this.alleles = populateAlleles();
		logger.info("populated alleles");

		String impcMediaBaseUrl = config.get("impcMediaBaseUrl");
		logger.info("omeroRootUrl=" + impcMediaBaseUrl);

		try {

			server.deleteByQuery("*:*");

			SolrQuery query = ImageService.allImageRecordSolrQuery().setRows(Integer.MAX_VALUE);

			List<ImageDTO> imageList = observationService.query(query).getBeans(ImageDTO.class);
			for (ImageDTO imageDTO : imageList) {

				String downloadFilePath = imageDTO.getDownloadFilePath();
				if (imageBeans.containsKey(downloadFilePath)) {

					ImageBean iBean = imageBeans.get(downloadFilePath);
					String fullResFilePath = iBean.fullResFilePath;
					if(iBean.image_link!=null){
						imageDTO.setImageLink(iBean.image_link);
					}
					imageDTO.setFullResolutionFilePath(fullResFilePath);

					int omeroId = iBean.omeroId;
					imageDTO.setOmeroId(omeroId);

					
					if (omeroId == 0 || imageDTO.getProcedureStableId().equals(excludeProcedureStableId) || downloadFilePath.endsWith(".pdf") ){//if(downloadFilePath.endsWith(".pdf")){//|| (imageDTO.getParameterStableId().equals("IMPC_ALZ_075_001") && imageDTO.getPhenotypingCenter().equals("JAX"))) {
						// Skip records that do not have an omero_id
						System.out.println("skipping omeroId="+omeroId+"param and center"+imageDTO.getParameterStableId()+imageDTO.getPhenotypingCenter());
						//logger.warn("Skipping record for image record {} -- missing omero_id or excluded procedure", fullResFilePath);
						continue;
					}

					// need to add a full path to image in omero as part of api
					// e.g. https://wwwdev.ebi.ac.uk/mi/media/omero/webgateway/render_image/4855/
					if (omeroId != 0 && downloadFilePath != null) {
						// logger.info("setting downloadurl="+impcMediaBaseUrl+"/render_image/"+omeroId);
						// /webgateway/archived_files/download/
						if(downloadFilePath.endsWith(".pdf")){
							//http://wwwdev.ebi.ac.uk/mi/media/omero/webclient/annotation/119501/
							imageDTO.setDownloadUrl(impcMediaBaseUrl + "/webclient/annotation/" + omeroId);
							imageDTO.setJpegUrl(impcMediaBaseUrl + "/render_image/" + 119501);//pdf thumnail placeholder
						}else{
							imageDTO.setDownloadUrl(impcMediaBaseUrl + "/archived_files/download/" + omeroId);
							imageDTO.setJpegUrl(impcMediaBaseUrl + "/render_image/" + omeroId);
						}
					} else {
						logger.info("omero id is null for " + downloadFilePath);
					}

					// add the extra stuf we need for the searching and faceting here
					if (imageDTO.getGeneAccession() != null && !imageDTO.getGeneAccession().equals("")) {

						String geneAccession = imageDTO.getGeneAccession();
						if (alleles.containsKey(geneAccession)) {
							populateImageDtoStatuses(imageDTO, geneAccession);

							if (imageDTO.getSymbol() != null) {
								String symbolGene = imageDTO.getSymbol() + "_" + imageDTO.getGeneAccession();
								imageDTO.setSymbolGene(symbolGene);
							}
						}
					}
					
					if (imageDTO.getParameterAssociationStableId()!=null && !imageDTO.getParameterAssociationStableId().isEmpty()) {
						
						ArrayList<String>maIds=new ArrayList<>();
						ArrayList<String>maTerms=new ArrayList<>();
						ArrayList<String>maTermSynonyms=new ArrayList<>();
						ArrayList<String>topLevelMaIds=new ArrayList<>();
						ArrayList<String>topLevelMaTerm=new ArrayList<>();
						ArrayList<String>topLevelMaTermSynonym=new ArrayList<>();
						for (String paramString : imageDTO.getParameterAssociationStableId()) {
							if (parameterStableIdToMaTermIdMap
									.containsKey(paramString)) {
								String maTermId = parameterStableIdToMaTermIdMap
										.get(paramString);
									maIds.add(maTermId);
								OntologyTermBean maTermBean = maService
										.getTerm(maTermId);
								if (maTermBean != null) {
									maTerms.add(maTermBean.getName());
									maTermSynonyms.addAll(maTermBean
											.getSynonyms());
									List<OntologyTermBean> topLevels = maService
											.getTopLevel(maTermId);
									for (OntologyTermBean topLevel : topLevels) {
										//System.out.println(topLevel.getName());
										topLevelMaIds.add(topLevel.getId());
										topLevelMaTerm.add(topLevel.getName());
										topLevelMaTermSynonym.addAll(topLevel
												.getSynonyms());
									}
								}
//									<field name="selected_top_level_ma_id" type="string" indexed="true" stored="true" required="false" multiValued="true" />
//									<field name="selected_top_level_ma_term" type="string" indexed="true" stored="true" required="false" multiValued="true" />
//									<field name="selected_top_level_ma_term_synonym" type="string" indexed="true" stored="true" required="false" multiValued="true" />

								//selected_top_level_ma_term
								//String selectedTopLevelMaTerm=
							}
							// IndexerMap.get
						}
						if (!maIds.isEmpty()) {
							imageDTO.setMaTermId(maIds);
						}
						if (!maTerms.isEmpty()) {
							imageDTO.setMaTerm(maTerms);
						}
						if (!maTermSynonyms.isEmpty()) {
							imageDTO.setMaTermSynonym(maTermSynonyms);
						}
						if (!topLevelMaIds.isEmpty()) {
							imageDTO.setTopLevelMaId(topLevelMaIds);
						}
						if(!topLevelMaTerm.isEmpty()){
							imageDTO.setTopLevelMaTerm(topLevelMaTerm);
						}
						if(!topLevelMaTermSynonym.isEmpty()){
							imageDTO.setTopLevelMaTermSynonym(topLevelMaTermSynonym);
						}
					}
					server.addBean(imageDTO);
					count++;
				}



				if (count % 10000 == 0) {
					logger.info(" added ImageDTO" + count + " beans");
				}
			}

			server.commit();
			documentCount = count;

		} catch (SolrServerException | IOException e) {
			throw new IndexerException(e);
		}

	}


	/**
	 * Get the image urls from the db using the download_path from Harwell for images that have an omero_id that should have already bean set by the
	 * python scripts that update the impc_images dev.
	 *
	 * @throws IndexerException
	 */
	private Map<String, ImageBean> populateImageUrls() throws IndexerException {

		Map<String, ImageBean> imageBeansMap = new HashMap<>();
		final String getExtraImageInfoSQL = "SELECT "
			+ ImageDTO.OMERO_ID + ", "
			+ ImageDTO.DOWNLOAD_FILE_PATH + ", "
			+ ImageDTO.IMAGE_LINK + ", "
			+ ImageDTO.FULL_RESOLUTION_FILE_PATH
			+ " FROM image_record_observation WHERE omero_id is not null AND omero_id != 0";

		try (PreparedStatement statement = komp2DataSource.getConnection().prepareStatement(getExtraImageInfoSQL)) {
			ResultSet resultSet = statement.executeQuery();

			while (resultSet.next()) {

				ImageBean bean = new ImageBean();
				bean.omeroId = resultSet.getInt(ImageDTO.OMERO_ID);
				bean.fullResFilePath = resultSet.getString(ImageDTO.FULL_RESOLUTION_FILE_PATH);
				bean.image_link = resultSet.getString(ImageDTO.IMAGE_LINK);
				imageBeansMap.put(resultSet.getString(ImageDTO.DOWNLOAD_FILE_PATH), bean);

			}

		} catch (Exception e) {
			throw new IndexerException(e);
		}

		return imageBeansMap;
	}


	private class ImageBean {
		int omeroId;
		String fullResFilePath;
		String image_link;
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
					// logger.info("adding status="+allele.getStatus());
					img.addStatus(allele.getStatus());
				}
				// <!-- latest project status (ES cells/mice production status)
				// -->
				// <field column="status"
				// xpath="/response/result/doc/str[@name='status']"
				// />
				//
				if (allele.getImitsPhenotypeStarted() != null) {
					img.addImitsPhenotypeStarted(allele.getImitsPhenotypeStarted());
				}
				// <!-- latest mice phenotyping status for faceting -->
				// <field column="imits_phenotype_started"
				// xpath="/response/result/doc/str[@name='imits_phenotype_started']"
				// />
				// <field column="imits_phenotype_complete"
				if (allele.getImitsPhenotypeComplete() != null) {
					img.addImitsPhenotypeComplete(allele.getImitsPhenotypeComplete());
				}
				// xpath="/response/result/doc/str[@name='imits_phenotype_complete']"
				// />
				// <field column="imits_phenotype_status"
				if (allele.getImitsPhenotypeStatus() != null) {
					img.addImitsPhenotypeStatus(allele.getImitsPhenotypeStatus());
				}
				// xpath="/response/result/doc/str[@name='imits_phenotype_status']"
				// />
				//
				// <!-- phenotyping status -->
				// <field column="latest_phenotype_status"
				// xpath="/response/result/doc/str[@name='latest_phenotype_status']"
				// />
				if (allele.getLegacyPhenotypeStatus() != null) {
					img.setLegacyPhenotypeStatus(allele.getLegacyPhenotypeStatus());
				}
				// <field column="legacy_phenotype_status"
				// xpath="/response/result/doc/int[@name='legacy_phenotype_status']"
				// />
				//
				// <!-- production/phenotyping centers -->
				img.setLatestProductionCentre(allele.getLatestProductionCentre());
				// <field column="latest_production_centre"
				// xpath="/response/result/doc/arr[@name='latest_production_centre']/str"
				// />
				// <field column="latest_phenotyping_centre"
				img.setLatestPhenotypingCentre(allele.getLatestPhenotypingCentre());
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
					img.setLegacyPhenotypeStatus(allele.getLegacyPhenotypeStatus());
				}
			}
		}
	}
	
	public Map<String,String> populateParameterStableIdToMaIdMap() throws SQLException{
    	System.out.println("populating parameterStableId to MA map");
		 Map<String,String> paramToMa = new HashMap<String, String>();
		String query="SELECT * FROM phenotype_parameter pp INNER JOIN phenotype_parameter_lnk_ontology_annotation pploa ON pp.id=pploa.parameter_id INNER JOIN phenotype_parameter_ontology_annotation ppoa ON ppoa.id=pploa.annotation_id WHERE ppoa.ontology_db_id=8 LIMIT 1000";
		try (PreparedStatement statement = komp2DataSource.getConnection().prepareStatement(query)){
		    ResultSet resultSet = statement.executeQuery();
		   
			while (resultSet.next()) {
				String parameterStableId=resultSet.getString("stable_id");
				String maAcc=resultSet.getString("ontology_acc");
				System.out.println("adding "+parameterStableId+" "+maAcc);
				paramToMa.put(parameterStableId, maAcc);
			}
		}
		System.out.println("paramToMa size="+paramToMa.size());
		return paramToMa;
	}

}
