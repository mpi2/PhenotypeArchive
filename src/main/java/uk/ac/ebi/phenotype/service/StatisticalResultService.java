package uk.ac.ebi.phenotype.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.springframework.stereotype.Service;

import uk.ac.ebi.phenotype.pojo.GenomicFeature;
import uk.ac.ebi.phenotype.pojo.Parameter;
import uk.ac.ebi.phenotype.service.GenotypePhenotypeService.GenotypePhenotypeField;
import uk.ac.ebi.phenotype.web.pojo.GeneRowForHeatMap;
import uk.ac.ebi.phenotype.web.pojo.HeatMapCell;

@Service
public class StatisticalResultService {

	private HttpSolrServer solr;
	
	public final static String PHENOTYPING_CENTER = "phenotyping_center"; //
	public final static String ZYGOSITY = "zygosity"; //
	public final static String MARKER_SYMBOL = "marker_symbol";//
	public final static String ALLELE_NAME = "allele_name";//
	public final static String SEX = "sex"; //
	public final static String PROJECT_NAME = "project_name";//
	public final static String STRAIN_NAME = "strain_name"; //
	public final static String PROJECT_FULLNAME = "project_fullname";//
	public final static String EFFECT_SIZE = "effect_size";///
	public final static String MARKER_ACCESSION_ID = "marker_accession_id";//
	public final static String ALLELE_SYMBOL = "allele_symbol";//
	public final static String MP_TERM_NAME = "mp_term_name"; //
	public final static String RESOURCE_NAME = "resource_name"; //
	public final static String DOC_ID = "doc_id";//
	public final static String ALLELE_ACCESSION_ID = "allele_accession_id";//
	public final static String STRAIN_ACCESSION_ID = "strain_accession_id"; //
	public final static String P_VALUE = "p_value";//
	public final static String PCS_ID="doc_id";// use doc_id which is the phenotype call summary Id..
	public final static String RESOURCE_FULLNAME = "resource_fullname";//
	public final static String MP_TERM_ID = "mp_term_id"; //
	public final static String PROJECT_EXTERNAL_ID = "project_external_id";//
	public final static String TOP_LEVEL_MP_TERM_NAME = "top_level_mp_term_name";//
	public final static String TOP_LEVEL_MP_TERM_ID = "top_level_mp_term_id";//
	public final static String INTERMEDIATE_MP_TERM_NAME = "intermediate_mp_term_name";//
	public final static String INTERMEDIATE_MP_TERM_ID = "intermediate_mp_term_id";//
	public final static String PARAMETER_STABLE_ID = "parameter_stable_id";//
	public final static String PARAMETER_NAME = "parameter_name";//
	public final static String PROCEDURE_STABLE_ID = "procedure_stable_id";//
	public final static String PROCEDURE_STABLE_KEY = "procedure_stable_key";		//
	public final static String PROCEDURE_NAME = "procedure_name";		//
	public final static String PIPELINE_STABLE_ID = "pipeline_stable_id";		//
	public final static String PIPELINE_STABLE_KEY = "pipeline_stable_key";		//
	public final static String PIPELINE_NAME = "pipeline_name";		//
	
	
	public StatisticalResultService(String solrUrl){
		solr = new HttpSolrServer(solrUrl);
	}
	
	 public GeneRowForHeatMap getResultsForGeneHeatMap(String accession, GenomicFeature gene, List<Parameter> parameters){
         GeneRowForHeatMap row=new GeneRowForHeatMap(accession);
         if(gene!=null){
         	row.setSymbol(gene.getSymbol());
         }else{
         	System.err.println("error no symbol for gene "+accession);
         }
         List<HeatMapCell> results=new ArrayList<HeatMapCell>();
//search by gene and a list of params            
   //or search on gene and then loop through params to add the results if available order by ascending p value means we can just pick off the first entry for that param
         //http://wwwdev.ebi.ac.uk/mi/impc/dev/solr/genotype-phenotype/select/?q=marker_accession_id:%22MGI:104874%22&rows=10000000&version=2.2&start=0&indent=on&wt=json
         
         Map<String,HeatMapCell> paramMap=new HashMap<>();//map to contain parameters with their associated status or pvalue as a string
         for(Parameter param: parameters){
             //System.out.println("adding param to paramMap="+paramId);
             paramMap.put(param.getStableId(),null);
         }
         
        SolrQuery q = new SolrQuery()
		.setQuery(GenotypePhenotypeField.MARKER_ACCESSION_ID + ":\"" + accession+"\"").setSort(GenotypePhenotypeField.P_VALUE, SolrQuery.ORDER.asc)
		.setRows(10000);
		QueryResponse response=null;

         try {
             response = solr.query(q);
             results = response.getBeans(HeatMapCell.class);
         for(HeatMapCell cell:results){
             //System.out.println(doc.getFieldValues("p_value"));
            
             String paramStableId=cell.getParameterStableId();
             //System.out.println("comparing"+cell.getParameterStableId()+"|");
             if(paramMap.containsKey(cell.getParameterStableId())){
             System.out.println("cell mp Term name="+cell.getMpTermName());
             System.out.println("cell p value="+cell.getpValue());
                System.out.println(cell.getpValue()+"found");
                    paramMap.put(paramStableId, cell);
                    if(row.getLowestPValue()>cell.getpValue()){
                 	   row.setLowestPValue(cell.getpValue());
                    }
             }
         }
         
         row.setParamToCellMap(paramMap);
         } catch (SolrServerException ex) {
             Logger.getLogger(GenotypePhenotypeService.class.getName()).log(Level.SEVERE, null, ex);
         }
         
         
         return row;
     }
}
