package uk.ac.ebi.phenotype.stats.graphs;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.WordUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import uk.ac.ebi.phenotype.bean.StatisticalResultBean;
import uk.ac.ebi.phenotype.pojo.Allele;
import uk.ac.ebi.phenotype.pojo.OntologyTerm;
import uk.ac.ebi.phenotype.pojo.Parameter;
import uk.ac.ebi.phenotype.pojo.PhenotypeCallSummary;
import uk.ac.ebi.phenotype.pojo.Pipeline;
import uk.ac.ebi.phenotype.pojo.Procedure;
import uk.ac.ebi.phenotype.stats.Constants;



public class PhenomeChartProvider {

	private static final Logger logger = Logger
			.getLogger(PhenomeChartProvider.class);


	public String createPvaluesOverviewChart(String alleleAccession, double minimalPValue, String pointFormat, JSONArray series, JSONArray categories) throws JSONException {

		String chartString="	$(function () { \n"
				+"  pvaluesOverviewChart = new Highcharts.Chart({ \n"
				+"     chart: {\n"
				+"renderTo: 'chart" + alleleAccession+ "',\n"
				+"         type: 'scatter',\n"
				+"         zoomType: 'xy',\n"
				+"         height: 800\n"
			    +"     },\n"
			    +"   title: {\n"
			    +"        text: ' "+"P-values Overview' \n"
			    +"    },\n"
			    +"     subtitle: {\n"
			    +"        text: 'Parameter by parameter' \n"
			    +"    },\n"
			    +"     xAxis: {\n"
			    +"     categories: "+ categories.toString() + ",\n"
			    +"        title: {\n"
			    +"           enabled: true,\n"
			    +"           text: 'Parameters' \n"
			    +"        }, \n"
			    +"       labels: { \n"
			    +"           rotation: -90, \n"
			    +"           align: 'right', \n"
			    +"           style: { \n"
			    +"              fontSize: '11px', \n"
			    +"              fontFamily: 'Verdana, sans-serif' \n"
			    +"         } \n"
			    +"     }, \n"
			    +"      showLastLabel: true \n"
			    +"  }, \n"
			    +"    yAxis: { \n"
			    //+"         min:"+ -Math.log(minimalPValue) + ","
			    +"         title: { \n"
			    +"             text: '"+ Constants.MINUS_LOG10_HTML + "(p-value)"+"' \n"
			    +"           }, \n"
			    + "plotLines : [{\n"
			    + "value : " + -Math.log10(minimalPValue) + ",\n"
			    + "color : 'green', \n"
			    + "dashStyle : 'shortdash',\n"
			    + "width : 2,\n"
			    + "label : { text : 'Significance threshold " + minimalPValue + "' }\n"
			    + "}]\n"
			    +"       }, \n"
			    +"      credits: { \n"
			    +"         enabled: false \n"
			    +"      }, \n"
			    + "     tooltip: {\n"
			    + "        headerFormat: '<span style=\"font-size:10px\">{point.name}</span><table>',\n" 
			    + "        pointFormat: '"+ pointFormat +"',\n"
			    + "        footerFormat: '</table>',\n"
			    + "        shared: 'true',\n"
			    + "        useHTML: 'true',\n"
			    + "     }, \n"
			    +"      plotOptions: { \n"
			    +"        scatter: { \n"
			    +"            marker: { \n"
			    +"                radius: 5, \n"
			    +"                states: { \n"
			    +"                   hover: { \n"
			    +"                      enabled: true, \n"
			    +"                      lineColor: 'rgb(100,100,100)' \n"
			    +"                   } \n"
			    +"                } \n"	    
			    +"            }, \n"
			    +"            states: { \n"
			    +"               hover: { \n"
			    +"                  marker: { \n"
			    +"                     enabled: false \n"
			    +"                  } \n"
			    +"              } \n"
			    +"            }, \n\n"	    
                +"            events: { \n"
                +"               click: function(event) { \n"
                +"                   //var sexString = (event.point.sex == \"both\") ? '&gender=male&gender=female' : '&gender=' + event.point.sex; \n"
                +"                   $.fancybox.open([ \n"
                + "                  {\n"
                + "                     href : base_url + '/charts?accession=' + event.point.geneAccession + "
                + "'&parameter_stable_id=' + event.point.parameter_stable_id + '&allele_accession=' + event.point.alleleAccession + "
                + "'&zygosity=' + event.point.zygosity + '&phenotyping_center=' + event.point.phenotyping_center + "
                + "'&pipeline_stable_id=' + event.point.pipeline_stable_id + '&bare=true', \n"
                + "                     title : event.point.geneAccession \n"
                + "                  } \n"
                + "                  ], \n"
                +"                   { \n"
                +"                     'maxWidth'          : 1000, \n" // 980 too narrow
        		+"                     'maxHeight'         : 900, \n"
        		+"                     'fitToView'         : false, \n"                
                +"                     'width'             : '100%',  \n"
                +"                     'height'            : '85%',  \n"
                +"                     'autoSize'          : false,  \n"
                +"                     'transitionIn'      : 'none', \n"
                +"                     'transitionOut'     : 'none', \n"
                +"                     'type'              : 'iframe', \n"
                +"                     scrolling           : 'auto' \n"
                +"                  }); \n"
                +"               } \n"
			    +"           } \n" // events
			    +"       } \n"                
			    +"   }, \n"
			    +"     series: "+ series.toString() + "\n"
			    +"    }); \n"
			    +"	}); \n";
		return chartString;
	}

	/**
	 * Creates a highCharts Phenome summary view plotting p-values for every 
	 * significant call for every strain from every IMPReSS parameter for a 
	 * specific phenotyping center
	 * @param phenotypingCenter the specific phenotyping center
	 * @param minimalPValue set the minimal threshold
	 * @param series series of categories to plot
	 * @param categories list of categories (one for every MP term)
	 * @return the chart to be displayed
	 * @throws JSONException 
	 */
	public String createPhenomeChart(String phenotypingCenter, double minimalPValue, String pointFormat, JSONArray series, JSONArray categories) throws JSONException {

		String chartString="	$(function () { \n"
				+"  phenomeChart = new Highcharts.Chart({ \n"
				+"     chart: {\n"
				+"renderTo: 'chart" + phenotypingCenter +"',\n"
				+"         type: 'scatter',\n"
				+"         zoomType: 'xy',\n"
				+"         height: 800\n"
			    +"     },\n"
			    +"   title: {\n"
			    +"       text: 'Significant MP calls'\n"
			    +"    },\n"
			    +"     subtitle: {\n"
			    +"        text: 'by Top Level MP Categories'\n"
			    +"    },\n"
			    +"     xAxis: {\n"
			    +"     categories: "+ categories.toString() + ",\n"
			    +"        title: {\n"
			    +"           enabled: true,\n"
			    +"           text: 'Top Level Mammalian Phenotype Ontology Terms' \n"
			    +"        }, \n"
			    +"       labels: { \n"
			    +"           rotation: -90, \n"
			    +"           align: 'right', \n"
			    +"           style: { \n"
			    +"              fontSize: '10px', \n"
			    +"              fontFamily: 'Verdana, sans-serif' \n"
			    +"         } \n"
			    +"     }, \n"
			    +"      showLastLabel: true \n"
			    +"  }, \n"
			    +"    yAxis: { \n"
			    +            "min: 0,\n"
			    +            "max: "+ -Math.log10(1E-21) + ",\n"
			    +"         title: { \n"
			    +"             text: '" + Constants.MINUS_LOG10_HTML + "(p-value)"+"' \n"
			    +"           }, \n"
			    +"       }, \n"
			    +"      credits: { \n"
			    +"         enabled: false \n"
			    +"      }, \n"
			    + "     tooltip: {\n"
			    + "        headerFormat: '<span style=\"font-size:10px\">{point.name}</span><table>',\n" 
			    + "        pointFormat: '"+ pointFormat +"',\n"
			    + "        footerFormat: '</table>',\n"
			    + "        shared: 'true',\n"
			    + "        useHTML: 'true',\n"
			    + "     }, \n"		    
			    +"      plotOptions: { \n"
			    +"        scatter: { \n"
			    +"            marker: { \n"
			    +"                radius: 5, \n"
			    +"                states: { \n"
			    +"                   hover: { \n"
			    +"                      enabled: true, \n"
			    +"                      lineColor: 'rgb(100,100,100)' \n"
			    +"                   } \n"
			    +"                } \n"			    
			    +"            }, \n"
			    +"            states: { \n"
			    +"               hover: { \n"
			    +"                  marker: { \n"
			    +"                     enabled: false \n"
			    +"                  } \n"
			    +"              } \n"
			    +"            }, \n"	    
                +"            events: { \n"
                +"               click: function(event) { \n"
                +"                   //var sexString = (event.point.sex == \"both\") ? '&gender=male&gender=female' : '&gender=' + event.point.sex; \n"
                +"                   $.fancybox.open([ \n"
                + "                  {\n"
                + "                     href : base_url + '/charts?accession=' + event.point.geneAccession +"
                + "'&parameter_stable_id=' + event.point.parameter_stable_id + '&allele_accession=' + event.point.alleleAccession + "
                + "'&zygosity=' + event.point.zygosity + '&phenotyping_center=' + event.point.phenotyping_center + "
                + "'&pipeline_stable_id=' + event.point.pipeline_stable_id + '&bare=true', \n"
                + "                     title : event.point.geneAccession \n"
                + "                  } \n"
                + "                  ], \n"
                +"                   { \n"
                +"                     'maxWidth'          : 1000, \n" // 980 too narrow
        		+"                     'maxHeight'         : 900, \n"
        		+"                     'fitToView'         : false, \n"                
                +"                     'width'             : '100%',  \n"
                +"                     'height'            : '85%',  \n"
                +"                     'autoSize'          : false,  \n"
                +"                     'transitionIn'      : 'none', \n"
                +"                     'transitionOut'     : 'none', \n"
                +"                     'type'              : 'iframe', \n"
                +"                     scrolling           : 'auto' \n"
                +"                  }); \n"
                +"               } \n"
			    +"           } \n" // events
			    +"       } \n"                
			    +"   }, \n"
			    +"     series: "+ series.toString() + "\n"
			    +"    }); \n"
			    +"	}); \n";
		return chartString;
	}

	public String generatePhenomeChart(
			List<PhenotypeCallSummary> calls, 
			String phenotypingCenter,
			double minimalPvalue) throws IOException,
			URISyntaxException {

		String chartString = null;
		
		JSONArray series = new JSONArray();

		JSONArray categories = new JSONArray();
		List<String> topLevelOntologyTermsList = new ArrayList<String>();
		Map<String, List<String>> specificTermMatrix = new HashMap<String, List<String>>();

		Map<String, JSONObject> seriesMap = new HashMap<String, JSONObject>();

		try {

			// build tooltip
			StringBuilder pointFormat = new StringBuilder();

			pointFormat.append("<tr><td style=\"color:{series.color};padding:0\">Top Level MP: {series.name}</td></tr>");
			pointFormat.append("<tr><td style=\"padding:0\">MP Term: {point.mp_term}</td></tr>");
			pointFormat.append("<tr><td style=\"padding:0\">Gene: {point.geneSymbol}</td></tr>");
			pointFormat.append("<tr><td style=\"padding:0\">Zygosity: {point.zygosity}</td></tr>");
			pointFormat.append("<tr><td style=\"padding:0\">P-value: {point.pValue}</td></tr>");
			pointFormat.append("<tr><td style=\"padding:0\">Effect size: {point.effectSize}</td></tr>");
			
			// first grab all categories and associated terms
			
			for (PhenotypeCallSummary call: calls) {

				for (OntologyTerm topLevel: call.getTopLevelPhenotypeTerms()) {

					List<PhenotypeCallSummary> toTopLevelCalls = null;

					String topLevelName = topLevel.getName();
					if (!topLevelOntologyTermsList.contains(topLevelName)) {

						specificTermMatrix.put(topLevelName, new ArrayList<String>());
						topLevelOntologyTermsList.add(topLevelName);
						
						JSONObject scatterJsonObject = new JSONObject();
						seriesMap.put(topLevelName, scatterJsonObject);

						scatterJsonObject.put("type", "scatter");
						scatterJsonObject.put("name", topLevelName);

						JSONArray dataArray=new JSONArray();

						scatterJsonObject.put("data", dataArray);

						series.put(scatterJsonObject);

					}

					if (!specificTermMatrix.get(topLevelName).contains(call.getPhenotypeTerm().getName())) {
						specificTermMatrix.get(topLevelName).add(call.getPhenotypeTerm().getName());
					}
				}
			}

			// Then generate categories for all of them
			int topLevelDim = 0;
			int total = 0;
			for (String topLevelName: topLevelOntologyTermsList) {
				for (String specificTerm: specificTermMatrix.get(topLevelName)) {
					categories.put((topLevelDim+1) + ". " + specificTerm);
					total++;
				}
				topLevelDim++;
			}
			System.out.println("TOTAL CATS=" +total);
			
			// finally extract the data points and generate a point for every
			// top level categories associated.
			for (PhenotypeCallSummary call: calls) {

				for (OntologyTerm topLevel: call.getTopLevelPhenotypeTerms()) {

					String topLevelName = topLevel.getName();
					int firstDim = topLevelOntologyTermsList.indexOf(topLevelName);
					
					// convert to position on x axis
					int index = 0;
					for (int i=0; i<=firstDim; i++) {
						index+= (i != firstDim) ?
								specificTermMatrix.get(topLevelOntologyTermsList.get(i)).size() :
								specificTermMatrix.get(topLevelName).indexOf(call.getPhenotypeTerm().getName());	 
					}
					
					JSONObject dataPoint=new JSONObject();
					dataPoint.put("name", (firstDim+1) +". " + call.getPhenotypeTerm().getName());
					dataPoint.put("mp_term", call.getPhenotypeTerm().getName());
					dataPoint.put("geneSymbol", call.getGene().getSymbol());
					dataPoint.put("geneAccession", call.getGene().getId().getAccession());
					dataPoint.put("alleleAccession", call.getAllele().getId().getAccession());
					dataPoint.put("parameter_stable_id", call.getParameter().getStableId());
					dataPoint.put("pipeline_stable_id", call.getPipeline().getStableId());
					dataPoint.put("phenotyping_center", phenotypingCenter);
					dataPoint.put("x", index);
					dataPoint.put("y", call.getLogValue() + addJitter(call.getEffectSize()));
					dataPoint.put("pValue", call.getpValue());
					dataPoint.put("effectSize", call.getEffectSize());
					dataPoint.put("sex", call.getSex());
					dataPoint.put("zygosity", call.getZygosity());	

					((JSONArray) seriesMap.get(topLevelName).get("data")).put(dataPoint);

				}
			}

			// finally sort by index
			for (String topLevelName: topLevelOntologyTermsList) {

				JSONArray array = ((JSONArray) seriesMap.get(topLevelName).get("data"));
				seriesMap.get(topLevelName).put("data", this.getSortedList(array));
			}

			chartString=createPhenomeChart(phenotypingCenter, minimalPvalue, pointFormat.toString(), series, categories);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return chartString;
	}

	/**
	 * 
	 * @param alleleAccession
	 * @param statisticalResults
	 * @param minimalPvalue
	 * @param pipeline
	 * @return
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public String generatePvaluesOverviewChart(
			Allele allele, 
			Map<String, List<StatisticalResultBean>> statisticalResults,
			double minimalPvalue,
			Pipeline pipeline,
			String phenotypingCenter) throws IOException,
			URISyntaxException {

		String chartString = null;
		
		JSONArray series=new JSONArray();

		JSONArray categories = new JSONArray();
		
		try {

			int index = 0;

			// build tooltip
			
			StringBuilder pointFormat = new StringBuilder();
			
			pointFormat.append("<tr><td style=\"color:{series.color};padding:0\">parameter: {point.name}</td></tr>");
			pointFormat.append("<tr><td style=\"padding:0\">procedure: {series.name}</td></tr>");
			pointFormat.append("<tr><td style=\"padding:0\">sex: {point.controlSex}</td></tr>");
			pointFormat.append("<tr><td style=\"padding:0\">zygosity: {point.zygosity}</td></tr>");
			pointFormat.append("<tr><td style=\"padding:0\">mutants: {point.femaleMutants}f:{point.maleMutants}m</td></tr>");
			pointFormat.append("<tr><td style=\"padding:0\">metadata_group: {point.metadataGroup}</td></tr>");
			pointFormat.append("<tr><td style=\"padding:0\">p-value: {point.pValue}</td></tr>");
			pointFormat.append("<tr><td style=\"padding:0\">Effect size: {point.effectSize}</td></tr>");
			
			
			// Create a statistical series for every procedure in the pipeline
			// Start from the pipeline so that there is no need to keep this 
			// information from the caller side
			// get All procedures and generate a Map Parameter => Procedure
			Map<Parameter, Procedure> parametersToProcedure = new HashMap<Parameter, Procedure>();
			Map<String, Parameter> parametersMap = new HashMap<String, Parameter>();
			for (Procedure procedure: pipeline.getProcedures()) {

				JSONObject scatterJsonObject=new JSONObject();
				// Tooltip first for correct formatting
				/*	        tooltip: {

						     headerFormat: '<b>{series.name}</b><br>',
						     pointFormat: '{point.name}<br/>value: {point.y}'
						                    },*/

				scatterJsonObject.put("type", "scatter");
				scatterJsonObject.put("name", procedure.getName());

				JSONArray dataArray=new JSONArray();

				// create a series here
				for (Parameter parameter: procedure.getParameters()) {

					/*			 data: [{
						        name: 'IMPC_...',
						        x: 1,
						        y: 2
						    }, {
						        name: 'IMPC_...',
						        x: 2,
						        y: 5
						    }]		*/	

					if (statisticalResults.containsKey(parameter.getStableId())) {

						int resultIndex = 0;
						for (StatisticalResultBean statsResult: statisticalResults.get(parameter.getStableId())) {

							if ( statsResult.getIsSuccessful() ) {

								// create the point first
								JSONObject dataPoint=new JSONObject();
								dataPoint.put("name", parameter.getName());
								dataPoint.put("parameter_stable_id", parameter.getStableId());
								dataPoint.put("pipeline_stable_id", pipeline.getStableId());
								
								dataPoint.put("geneAccession", allele.getGene().getId().getAccession());
								dataPoint.put("alleleAccession", allele.getId().getAccession());
								
								dataPoint.put("phenotyping_center", phenotypingCenter);
								
								dataPoint.put("x", index);
								dataPoint.put("y", statsResult.getLogValue());
								dataPoint.put("pValue", statsResult.getpValue());
								dataPoint.put("effectSize", statsResult.getEffectSize());
								
								dataPoint.put("sex", statsResult.getControlSex());
								dataPoint.put("zygosity", statsResult.getZygosity());
								// maybe change for the complete object here
								dataPoint.put("femaleMutants", statsResult.getFemaleMutants());
								dataPoint.put("maleMutants", statsResult.getMaleMutants());
								dataPoint.put("metadataGroup", statsResult.getMetadataGroup());
								dataArray.put(dataPoint);

								if (resultIndex == 0) {
									categories.put(parameter.getStableId());
									index++;
								}

								resultIndex++;


							}
						}
					}

				}

				if (dataArray.length() > 0) {
					scatterJsonObject.put("data", dataArray);
					series.put(scatterJsonObject);
				}
			}

			chartString=createPvaluesOverviewChart(
					allele.getId().getAccession(), // used for chart ID
					minimalPvalue, 
					pointFormat.toString(), 
					series, 
					categories);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return chartString;
	}

	/**
	 * Add jitter to a data point by capping the effect size between 0 and 0.8
	 * @param effectSize the effect size associated to a MP call
	 * @return a jittered value based on the effect size
	 */
	double addJitter(double effectSize) {
		// cap to 0.8 max otherwise this means nothing
		// 2 decimals
		double scale = 8E-2;
		boolean neg = (effectSize < 0);
		return (((Math.abs(effectSize) >= 10) ? ((neg) ? -10 : 10) : effectSize) * scale);
	}
	
	private JSONArray getSortedList(JSONArray array) throws JSONException {
		List<JSONObject> list = new ArrayList<JSONObject>();
		for (int i = 0; i < array.length(); i++) {
			list.add(array.getJSONObject(i));
		}
		Collections.sort(list, new JSONSortBasedonXAxisIndexComparator());

		JSONArray resultArray = new JSONArray(list);

		return resultArray;

	}

	protected class JSONSortBasedonXAxisIndexComparator implements Comparator<JSONObject>
	{

		public int compare(JSONObject a, JSONObject b)
		{
			try {
				int valA = a.getInt("x");
				int valB = b.getInt("x");

				if(valA > valB)
					return 1;
				if(valA < valB)
					return -1;

				return 0; 

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return 0; 
			}
		}
	}

}
