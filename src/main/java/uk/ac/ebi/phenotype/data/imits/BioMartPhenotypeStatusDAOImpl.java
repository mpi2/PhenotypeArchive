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

package uk.ac.ebi.phenotype.data.imits;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.conn.ConnectTimeoutException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import uk.ac.ebi.phenotype.pojo.GenomicFeature;
import uk.ac.ebi.phenotype.web.util.BioMartBot;

/**
 * 
 * PhenotypeStatusDAO is the helper class to get access to the latest 
 * phenotype attempts in IMITS using the BioMart
 * 
 * @author Gautier koscielny
 * @since April 2013
 */
@Service
@Qualifier("biomart")
public class BioMartPhenotypeStatusDAOImpl extends BioMartBot implements PhenotypeStatusDAO {

	Logger log = Logger.getLogger(this.getClass().getCanonicalName());

	static String xmlQueryTemplate = null;
	List<ColonyStatus> colonyStatusList;


	public BioMartPhenotypeStatusDAOImpl() {
		super();
		bioMartURL = "http://www.i-dcc.org/biomart/martservice";
		xmlQueryTemplate = 

				"<?xml version=\"1.0\" encoding=\"UTF-8\"?><!DOCTYPE Query>" +
						"<Query  virtualSchemaName = \"default\" formatter = \"TSV\" header = \"0\" uniqueRows = \"0\" count = \"\" datasetConfigVersion = \"0.6\" >"+
						"<Dataset name = \"imits2\" interface = \"default\" >"+
						"<Filter name = \"mgi_accession_id\" value = \"MGI_ID\"/>" +
						"<Attribute name = \"consortium\" />" +
						"<Attribute name = \"marker_symbol\" />" +
						"<Attribute name = \"mgi_accession_id\" />" +
						"<Attribute name = \"mi_plan_status\" />" +
						"<Attribute name = \"pipeline\" />" +
						"<Attribute name = \"escell_clone\" />" +
						"<Attribute name = \"allele_symbol_superscript\" />" +
						"<Attribute name = \"production_centre\" />" +
						"<Attribute name = \"microinjection_status\" />" +
						"<Attribute name = \"mouse_allele_symbol_superscript\" />" +
						"<Attribute name = \"phenotype_colony_name\" />" +
						"<Attribute name = \"phenotype_allele_type\" />" +
						"<Attribute name = \"phenotype_status\" />" +
						"<Attribute name = \"phenotype_is_active\" />" +
						"<Attribute name = \"rederivation_started\" />" +
						"<Attribute name = \"rederivation_complete\" />" +
						"<Attribute name = \"number_of_cre_matings_started\" />" +
						"<Attribute name = \"number_of_cre_matings_successful\" />" +
						"<Attribute name = \"phenotyping_started\" />" +
						"<Attribute name = \"phenotyping_complete\" />" +
						"<Attribute name = \"phenotype_attempt_distribution_centres__dm_is_distributed_by_emma\" />" +
						"<Attribute name = \"deleter_strain_name\" />" +
						"<Attribute name = \"colony_background_strain_name\" />" +
						"<Attribute name = \"phenotype_distribution_centre\" />" +
						"<Attribute name = \"phenotype_attempt_distribution_centres__dm_distribution_network\" />" +
						"<Attribute name = \"colony_name\" />" +
						"</Dataset></Query>";

	}

	public List<ColonyStatus> getColonyStatus(GenomicFeature gene) throws ConnectTimeoutException {

		// prevent errors (test connection time-out)
		System.out.println("calling colony status with biomart dao");
		colonyStatusList = new ArrayList<ColonyStatus>();
		String q = xmlQueryTemplate.replaceFirst("MGI_ID", gene.getId().getAccession());
		log.info("Posting for " + gene.getId().getAccession());
		log.info(q);
		postDataFromText(bioMartURL, q);
		return colonyStatusList;
	}

	@Override
	public void processResponse() throws IOException {

		BufferedReader in;
		InputStream is;
		String read = null;
		int count = 0;
		String[] fields = null;

		try {

			is = responseEntity.getContent();
			in = new BufferedReader(new InputStreamReader(is));

			while ((read = in.readLine()) != null) {

				count++;

				String productionStatus;

				fields = read.split("\t");
				
				log.info(fields.length + "\t" + read);

				if (fields.length == 26) {

					String markerSymbol = fields[1];
					String colonyName = fields[25];
					String alleleSymbolSuperscript = fields[6];
					String productionCenter = fields[7];
					String microinjectionStatus = fields[8];
					String phenotypeColonyName = fields[10];
					String phenotypeAlleleType = fields[11];
					String phenotypeStatus = fields[12];
					String phenotypeIsActive = fields[13];
					int phenotypeStarted = Integer.parseInt(fields[18]);
					int phenotypeComplete = Integer.parseInt(fields[19]);
					String colonyBackgrounStrainName = fields[22];
					String phenotypeCenter = fields[23];

					if (phenotypeCenter.isEmpty()) {
						phenotypeCenter = productionCenter;
					}

					if (phenotypeStatus.equals("Cre Excision Complete") || phenotypeStarted == 1 || phenotypeComplete == 1) {

						log.info(phenotypeStatus);

						productionStatus = "Mice Produced";

						ColonyStatus currentStatus = 
								new ColonyStatus(
										phenotypeColonyName, // colony ID
										phenotypeStatus, // 
										productionStatus,
										phenotypeAlleleType,
										phenotypeStarted,
										phenotypeComplete
										);

						Pattern p1 = Pattern.compile("^tm\\d{1}\\(");
						Matcher m1 = p1.matcher(alleleSymbolSuperscript);

						Pattern p2 = Pattern.compile("^tm\\d{1}[a-z]{1}\\(");
						Matcher m2 = p2.matcher(alleleSymbolSuperscript);


						if (colonyName.equals(phenotypeColonyName)) {
							currentStatus.setAlleleName(markerSymbol+"<sup>"+alleleSymbolSuperscript+"</sup>");
						} else if (m1.find()) {
							currentStatus.setAlleleName(markerSymbol+"<sup>"+alleleSymbolSuperscript.replaceAll("^(tm\\d{1})", "$1" + phenotypeAlleleType)+"</sup>");
						} else if (m2.find()) {
							currentStatus.setAlleleName(markerSymbol+"<sup>"+alleleSymbolSuperscript.replaceAll("^(tm\\d{1})([a-z]{1})", "$1" + phenotypeAlleleType)+"</sup>");
						}
						currentStatus.setBackgroundStrain(colonyBackgrounStrainName);
						currentStatus.setPhenotypeCenter((Utils.imitsCenters.containsKey(phenotypeCenter)) ? Utils.imitsCenters.get(phenotypeCenter) : phenotypeCenter);
						colonyStatusList.add(currentStatus);
					} else if (phenotypeStatus.equals("Phenotype Attempt Registered")) {
						ColonyStatus currentStatus = 
								new ColonyStatus(
										phenotypeColonyName, // colony ID
										phenotypeStatus, // 
										null,
										phenotypeAlleleType,
										phenotypeStarted,
										phenotypeComplete
										);
						colonyStatusList.add(currentStatus);
					}

				}
			}
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (java.lang.ArrayIndexOutOfBoundsException e) {

			e.printStackTrace();
			System.err.println(e.getLocalizedMessage() + " " + e.getMessage());
			System.err.println("String: [" + read + "]\tCount fields:" + fields.length + " " + read);
			//System.exit(0);
			throw new IOException(e.getLocalizedMessage() + " " + e.getMessage());

		} catch (java.lang.NumberFormatException e) {

			e.printStackTrace();
			System.err.println(e.getLocalizedMessage() + " " + e.getMessage());
			System.err.println(fields.length + " " + read);
			//System.exit(0);
			throw new IOException(e.getLocalizedMessage() + " " + e.getMessage());


		} 

	}	
}
