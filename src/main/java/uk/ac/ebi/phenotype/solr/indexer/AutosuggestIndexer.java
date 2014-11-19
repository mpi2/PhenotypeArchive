package uk.ac.ebi.phenotype.solr.indexer;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AutosuggestIndexer {

	private static final Logger LOG = LoggerFactory.getLogger(AutosuggestIndexer.class);

	public static void main(String[] args) throws IOException, SolrServerException {

		OptionParser parser = new OptionParser();
		parser.accepts("solrUrl").withRequiredArg().ofType(String.class);

		OptionSet options = parser.parse(args);
		String solrUrl = (String) options.valueOf("solrUrl");

		System.out.println(solrUrl);

		HttpSolrServer destServer = new HttpSolrServer(solrUrl + "/autosuggest");

		destServer.deleteByQuery("*:*");

		final HashMap<String, String[]> coreFields = new HashMap<String, String[]>();

		String[] geneFields    = { "mgi_accession_id", "marker_symbol", "marker_name", "marker_synonym", "human_gene_symbol" };
		String[] mpFields 	   = { "mp_id", "mp_term", "mp_term_synonym", "top_level_mp_id", "top_level_mp_term", "top_level_mp_term_synonym", "intermediate_mp_id", "intermediate_mp_term", "intermediate_mp_term_synonym", "child_mp_id", "child_mp_term", "child_mp_term_synonym" };
		String[] diseaseFields = { "disease_id", "disease_term", "disease_alts" };
		String[] maFields 	   = { "ma_id", "ma_term", "ma_term_synonym", "child_ma_id", "child_ma_term", "child_ma_term_synonym", "selected_top_level_ma_id", "selected_top_level_ma_term", "selected_top_level_ma_term_synonym"};
		String[] hpFields      = { "mp_id", "mp_term", "hp_id", "hp_term", "hp_synonym" };

		coreFields.put("gene", geneFields);
		coreFields.put("mp", mpFields);
		coreFields.put("disease", diseaseFields);
		coreFields.put("ma", maFields);
		coreFields.put("hp", hpFields);

		final HashMap<String, Integer> valSeen = new HashMap<String, Integer>();

		for (Map.Entry<String, String[]> entry : coreFields.entrySet()) {
			String core = entry.getKey().toString();
			HttpSolrServer srcServer = new HttpSolrServer(solrUrl + "/" + core);
			if ( core.equals("hp") ){
				// phenodigm hp_mp mapping
				srcServer = new HttpSolrServer("http://solrcloudlive.sanger.ac.uk/solr/phenodigm");
			}

			SolrQuery query = new SolrQuery();
			query.setQuery("*:*");
			query.setStart(0);
			query.setRows(99999999);
			if ( core.equals("hp") ){
				query.setFilterQueries("type:hp_mp");
			}

			// retrieves wanted fields
			query.setFields(entry.getValue());

			QueryResponse response = srcServer.query(query);
			SolrDocumentList results = response.getResults();

			valSeen.clear(); // clear when we start with a new core

			List<SolrInputDocument> docs = new ArrayList<>();

			for (int i = 0; i < results.size(); ++i) {
				SolrDocument srcDoc = results.get(i);

				Map<String, Object> docMap = srcDoc.getFieldValueMap();

				for (String fieldName : srcDoc.getFieldNames()) {
					SolrInputDocument doc = new SolrInputDocument();

					if ( core.equals("hp") && fieldName.startsWith("mp_") ){
						continue; // don't want to index these as a doc
					}

					// remove duplicate fieldname - value pair
					// ie, want only one entry for eg, child_mp_term:heart
					if ( valSeen.get(docMap.get(fieldName)) == null ){
						valSeen.put(docMap.get(fieldName).toString(), 1);

						doc.addField("docType", core);
						if ( !core.equals("hp") ){
							doc.addField(fieldName, docMap.get(fieldName));
						}
						else {
							doc.addField(fieldName, docMap.get(fieldName));
							doc.addField("hpmp_id", docMap.get("mp_id"));
							doc.addField("hpmp_term", docMap.get("mp_term"));
						}

						docs.add(doc);
					}
				}

				if (i % 1000 == 0) {
					LOG.info("Trying to add "+docs.size() + " documents to solr core");
					destServer.add(docs);
					destServer.commit(); // periodically flush

					docs = new ArrayList<>();
					LOG.info("== COMMITTED RESULTS "+i+" of "+results.size()+" ====================================================");
				}

			}

			LOG.info("Trying to add "+docs.size() + " documents to solr core");
			destServer.add(docs);
			destServer.commit();
			LOG.info("  done with " + core);
			LOG.info("== COMMITTED FINAL RESULTS "+results.size()+" ====================================================");
		}
	}
}

