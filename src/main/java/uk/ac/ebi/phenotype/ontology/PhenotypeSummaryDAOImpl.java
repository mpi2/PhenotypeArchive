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
package uk.ac.ebi.phenotype.ontology;

import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import uk.ac.ebi.phenotype.pojo.ZygosityType;
import uk.ac.ebi.phenotype.service.PostQcService;

import javax.annotation.Resource;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

@Service
public class PhenotypeSummaryDAOImpl implements PhenotypeSummaryDAO {
	
	@Resource(name="globalConfiguration")
	private Map<String, String> config;

	@Autowired
	@Qualifier("postqcService")
	private PostQcService gpService;
	
	public PhenotypeSummaryDAOImpl() throws MalformedURLException {
	}
	
	@Override
	public String getSexesRepresentationForPhenotypesSet(SolrDocumentList resp) {
		String resume = ""; 
		if (resp.size() > 0) {

			for (int i = 0; i < resp.size(); i++) {
				SolrDocument doc = resp.get(i);

				if ("male".equalsIgnoreCase((String) doc.getFieldValue("sex")))
					resume += "m";
				else if ("female".equalsIgnoreCase((String) doc.getFieldValue("sex")))
					resume += "f";

				if (resume.contains("m") && resume.contains("f")) // we can stop when we have both sexes already
					return "both sexes";
			}

			if (resume.contains("m") && !resume.contains("f"))
				return "male";

			if (resume.contains("f") && !resume.contains("m"))
				return "female";
		}	
		return null;
	}

	@Override
	public HashSet<String> getDataSourcesForPhenotypesSet(SolrDocumentList resp) {
		HashSet <String> data = new HashSet <String> ();
		if (resp.size() > 0) {
			for (int i = 0; i < resp.size(); i++) {
				SolrDocument doc = resp.get(i);
				data.add((String) doc.getFieldValue("resource_name"));
			}
		}	
		return data;
	}
	

	@Override
	public PhenotypeSummaryBySex getSummaryObjects(String gene)
	throws Exception {

		HashMap<String, String> summary = gpService.getTopLevelMPTerms(gene, null);
		PhenotypeSummaryBySex resSummary = new PhenotypeSummaryBySex();
		for (String id : summary.keySet()) {
			SolrDocumentList resp = gpService.getPhenotypesForTopLevelTerm(gene, id, null);
			String sex = getSexesRepresentationForPhenotypesSet(resp);
			HashSet<String> ds = getDataSourcesForPhenotypesSet(resp);
			long n = resp.getNumFound();
			PhenotypeSummaryType phen = new PhenotypeSummaryType(id, summary.get(id), sex, n, ds);
			resSummary.addPhenotye(phen);
		}
		return resSummary;
	}
	

	@Override
	public HashMap<ZygosityType, PhenotypeSummaryBySex> getSummaryObjectsByZygosity(String gene) throws Exception {
		HashMap< ZygosityType, PhenotypeSummaryBySex> res =  new HashMap<>();
		for (ZygosityType zyg : ZygosityType.values()){
			PhenotypeSummaryBySex resSummary = new PhenotypeSummaryBySex();
			HashMap<String, String> summary = gpService.getTopLevelMPTerms(gene, zyg);	
			for (String id: summary.keySet()){
				SolrDocumentList resp = gpService.getPhenotypesForTopLevelTerm(gene, id, zyg);
				String sex = getSexesRepresentationForPhenotypesSet(resp);
				HashSet<String> ds = getDataSourcesForPhenotypesSet(resp);
				long n = resp.getNumFound();
				PhenotypeSummaryType phen = new PhenotypeSummaryType(id, summary.get(id), sex, n, ds);
				resSummary.addPhenotye(phen);
			}
			if (resSummary.getTotalPhenotypesNumber() > 0){
				res.put(zyg, resSummary);
			}
		}
		return res;
	}
}
