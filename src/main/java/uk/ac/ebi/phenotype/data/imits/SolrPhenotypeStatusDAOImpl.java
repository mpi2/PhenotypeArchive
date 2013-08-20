/**
 * Copyright © 2011-2013 EMBL - European Bioinformatics Institute
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

package uk.ac.ebi.phenotype.data.imits;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.http.conn.ConnectTimeoutException;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import uk.ac.ebi.generic.util.JSONRestUtil;
import uk.ac.ebi.phenotype.pojo.GenomicFeature;

/**
 * 
 * PhenotypeStatusDAO is the helper class to get access to the latest phenotype
 * attempts in IMITS using the Solr allele core
 * 
 * @author Gautier koscielny
 * @since July 2013
 */
@Service
@Qualifier("solr")
public class SolrPhenotypeStatusDAOImpl implements PhenotypeStatusDAO {

	@Resource(name="globalConfiguration")
	private Map<String, String> config;
	
	Logger log = Logger.getLogger(this.getClass().getCanonicalName());
	List<ColonyStatus> colonyStatusList;

	public SolrPhenotypeStatusDAOImpl() {
		super();
	}

	public List<ColonyStatus> getColonyStatus(GenomicFeature gene)
			throws ConnectTimeoutException {

		// prevent errors (test connection time-out)
System.out.println("calling get colonystatus in solrPhenotypeStatus");
		colonyStatusList = new ArrayList<ColonyStatus>();

		log.info("Posting for " + gene.getId().getAccession());
		String solrUrl = config.get("internalSolrUrl")+ "/allele";
		String accession = gene.getId().getAccession();
		String urlString = solrUrl
				+ "/select/?q=mgi_accession_id:\""
				+ accession
				+ "\"&rows=100&version=2.2&start=0&indent=on&wt=json&defType=edismax";
	
		try {
			JSONObject results = JSONRestUtil.getResults(urlString);
			JSONArray docs = results.getJSONObject("response").getJSONArray(
					"docs");
			for (Object doc : docs) {
				JSONObject allele = (JSONObject) doc;
				String alleleName = "";
				String alleleType = "";
				String strain = "";
				String colonyId = "";
				String phenotypeStatus = "";
				String phenotypeCenter = "";
				String productionStatus="";
				int phenotypeStarted = 0;
				int phenotypeComplete = 0;
				if (allele.containsKey("product_type")) {
					String productType = allele.getString("product_type");
					if (productType.equalsIgnoreCase("mouse")) {

						if (allele.containsKey("allele_name")) {
							alleleName = allele.getString("allele_name");
						}
						if (allele.containsKey("allele_type")) {
							alleleType = allele.getString("allele_type");
						}
						if (allele.containsKey("strain")) {
							strain = allele.getString("strain");
						}
						if (allele.containsKey("current_pa_status") && !allele.getString("current_pa_status").equals("")) {
							phenotypeStatus = allele
									.getString("current_pa_status");// is it
																	// this
																	// field we
																	// need?
																	// best_status_pa_cre_ex_required
							if (phenotypeStatus.contains("Started")) {
								phenotypeStarted = 1;
							}
							if (phenotypeStatus.contains("Completed")) {
								phenotypeComplete = 1;
							}
						}else if(allele.containsKey("best_status_pa_cre_ex_not_required")){
							phenotypeStatus=allele.getString("best_status_pa_cre_ex_not_required");
							if (phenotypeStatus.contains("Started")) {
								phenotypeStarted = 1;
							}
							if (phenotypeStatus.contains("Completed")) {
								phenotypeComplete = 1;
							}
						}
						


//						String markerSymbol = fields[1];
//						String colonyName = fields[25];
//						String alleleSymbolSuperscript = fields[6];
//						String productionCenter = fields[7];
//						String microinjectionStatus = fields[8];
//						String phenotypeColonyName = fields[10];
//						String phenotypeAlleleType = fields[11];
//						String phenotypeStatus = fields[12];
//						String phenotypeIsActive = fields[13];
//						int phenotypeStarted = Integer.parseInt(fields[18]);
//						int phenotypeComplete = Integer.parseInt(fields[19]);
//						String colonyBackgrounStrainName = fields[22];
//						String phenotypeCenter = fields[23];

						if (phenotypeStatus.equals("Cre Excision Complete") || phenotypeStarted == 1 || phenotypeComplete == 1) {

							log.info(phenotypeStatus);

							productionStatus = "Mice Produced";

							ColonyStatus currentStatus = 
									new ColonyStatus(
											"", // colony ID
											phenotypeStatus, // 
											productionStatus,
											alleleType,
											phenotypeStarted,
											phenotypeComplete
											);
							currentStatus.setAlleleName(alleleName);
//							Pattern p1 = Pattern.compile("^tm\\d{1}\\(");
//							Matcher m1 = p1.matcher(alleleSymbolSuperscript);
//
//							Pattern p2 = Pattern.compile("^tm\\d{1}[a-z]{1}\\(");
//							Matcher m2 = p2.matcher(alleleSymbolSuperscript);


//							if (colonyName.equals(phenotypeColonyName)) {
//								currentStatus.setAlleleName(markerSymbol+"<sup>"+alleleSymbolSuperscript+"</sup>");
//							} else if (m1.find()) {
//								currentStatus.setAlleleName(markerSymbol+"<sup>"+alleleSymbolSuperscript.replaceAll("^(tm\\d{1})", "$1" + phenotypeAlleleType)+"</sup>");
//							} else if (m2.find()) {
//								currentStatus.setAlleleName(markerSymbol+"<sup>"+alleleSymbolSuperscript.replaceAll("^(tm\\d{1})([a-z]{1})", "$1" + phenotypeAlleleType)+"</sup>");
//							}
							currentStatus.setBackgroundStrain(strain);
							currentStatus.setPhenotypeCenter((Utils.imitsCenters.containsKey(phenotypeCenter)) ? Utils.imitsCenters.get(phenotypeCenter) : phenotypeCenter);
							colonyStatusList.add(currentStatus);
						} else if (phenotypeStatus.equals("Phenotype Attempt Registered")) {
							ColonyStatus currentStatus = 
									new ColonyStatus(
											"", // colony ID
											phenotypeStatus, // 
											null,
											alleleType,
											phenotypeStarted,
											phenotypeComplete
											);
							currentStatus.setAlleleName(alleleName);
							colonyStatusList.add(currentStatus);
						}
	
//						ColonyStatus tempColony = new ColonyStatus("",
//								phenotypeStatus, productType, alleleType,
//								phenotypeStarted, phenotypeComplete);
//						tempColony.setBackgroundStrain(strain);
//						colonyStatusList.add(tempColony);
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// q=MGI%5C%3A1921677&version=2.2&start=0&rows=10&indent=on
		// log.info(q);
		// postDataFromText(bioMartURL, q);
		return colonyStatusList;
	}
}
