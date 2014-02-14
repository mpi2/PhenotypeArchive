package uk.ac.ebi.phenotype.stats.graphs;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.WordUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import uk.ac.ebi.phenotype.dao.DiscreteTimePoint;
import uk.ac.ebi.phenotype.pojo.BiologicalModel;
import uk.ac.ebi.phenotype.pojo.Parameter;
import uk.ac.ebi.phenotype.pojo.SexType;
import uk.ac.ebi.phenotype.pojo.ZygosityType;
import uk.ac.ebi.phenotype.stats.ChartData;
import uk.ac.ebi.phenotype.stats.ExperimentDTO;
import uk.ac.ebi.phenotype.stats.ObservationDTO;
import uk.ac.ebi.phenotype.stats.timeseries.TimeSeriesStats;

@Service
public class ScatterChartAndTableProvider {
	
	private static final Logger logger = Logger
			.getLogger(ScatterChartAndTableProvider.class);
	
	
	public String createScatter(String experimentNumber, Parameter parameter, JSONArray series) {
		
		String chartString="	$(function () { "
			  +"  chart71maleWTSI = new Highcharts.Chart({ "
			    +"     chart: {"
			    +"renderTo: 'chart"
				+ experimentNumber+"',"
			    +"         type: 'scatter',"
			    +"         zoomType: 'xy'"
			    
			    +"     },"
			      +"   title: {"
			      +"       text: ' "+parameter.getName() 
			      +"'    },"
			    +"     subtitle: {"
			     +"        text: ' "+parameter.getStableId()+" ' "
			     +"    },"
			    +"     xAxis: {"
			    +"         type: 'datetime',"
			     +"        title: {"
			      +"           enabled: true,"
			      +"           text: 'Mouse' "
			      +"        }, "
			      +"       labels: { "
			      +"           rotation: -45, "
			      +"           align: 'right', "
			      +"           style: { "
			       +"              fontSize: '13px', "
			       +"              fontFamily: 'Verdana, sans-serif' "
			       +"         } "
			       +"     }, "
			       +"      showLastLabel: true "
			       +"  }, "
			     +"    yAxis: { "
//			     +"        max: 18.05, "
//			    +"         min: 15.9, "
			    +"         title: { "
			    +"             text: 'pg' "
			    +"           } "
			    +"       }, "
			   +"      credits: { "
			    +"         enabled: false "
			    +"      }, "
			   +"      plotOptions: { "
			     +"        scatter: { "
			     +"            marker: { "
			      +"                radius: 5, "
			      +"              states: { "
			         +"                hover: { "
			         +"                    enabled: true, "
			          +"                   lineColor: 'rgb(100,100,100)' "
			          +"               } "
			          +"           } "
			          +"       }, "
			          +"       states: { "
			          +"           hover: { "
			          +"               marker: { "
			          +"                   enabled: false "
			          +"               } "
			          +"           } "
			          +"        } "
			          +"     } "
			          +"   }, "
			    +"     tooltip: { "
			   +"          formatter: function () { "
			   +"              return '<b>' + this.series.name + '</b><br/>' + Highcharts.dateFormat('%e %b %Y', this.x) + ': ' + this.y + ' pg '; "
			   +"          } "
			   +"      }, "
			    +"     series: "+
			     series.toString()
			    +"    }); "
			    +"	}); ";
		return chartString;
	}
	
//	   +"     series: [{ "
//	     +"        name: 'WT ', "
//	    +"         data: [ "
//	    +"             [1079481600000, 17.12], "
//	    +"             [1161126000000, 17.6], "
//	    +"             [1079481600000, 17.1], "
//	    +"             [1003359600000, 16.8], "
//	     +"            [1003359600000, 16.8], "
//	    +"             [1075248000000, 17.36], ] "
//	    +"      }, { "
//	      +"       name: 'HOM ', "
//	       +"           data: [ "
//	    +"             [1003359600000, 16.8], "
//	   +"              [1003359600000, 17.7], "
//	    +"              [1003359600000, 16.5], "
//	   +"              [1003359600000, 16.9], "
//	    +"             [1003359600000, 17.6], ] "
//	    +"       }, ] "
//	    +"    }); "
//	    +"	}); ";
	
	public ScatterChartAndData doScatterData(ExperimentDTO experiment,
			Parameter parameter, String experimentNumber, BiologicalModel expBiologicalModel) throws IOException,
			URISyntaxException {
		
		
		ChartData chartNTableForParameter = null;
		
		
		String subtitle="subtitle";
		
				JSONArray series=new JSONArray();
//		        series: [{
//	            name: 'WT ',
//	            data: [
//	                [1079481600000, 17.12],
//	                [1161126000000, 17.6],
//	                [1079481600000, 17.1],
//	                [1003359600000, 16.8],
//	                [1003359600000, 16.8],
//	                [1075248000000, 17.36], ]
//	        }, {
//	            name: 'HOM ',
//	            data: [
//	                [1003359600000, 16.8],
//	                [1003359600000, 17.7],
//	                [1003359600000, 16.5],
//	                [1003359600000, 16.9],
//	                [1003359600000, 17.6], ]
//	        }, ]
//	    });
				
				
					

		// maybe need to put these into method that can be called as repeating
		// this - so needs refactoring though there are minor differences?
		Map<String, List<DiscreteTimePoint>> lines = new HashMap<String, List<DiscreteTimePoint>>();
//SexType sex=SexType.male;
		for (SexType sex : experiment.getSexes()) {
			List<DiscreteTimePoint> controlDataPoints = new ArrayList<>();
			// loop over the control points and add them
			JSONObject controlJsonObject=new JSONObject();
			JSONArray dataArray=new JSONArray();
			int colorIndex=0;
			try {
				
				controlJsonObject.put("name", sex+" "+"WT");
				
				controlJsonObject.put("color", ChartColors.getWTColor(ChartColors.alphaScatter));
				if(sex.equals(SexType.male)) {
					controlJsonObject.put("symbol", "triangle");
				}
				
			} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			}
			//int i=0;
			for (ObservationDTO control : experiment.getControls(sex)) {
				
				
				
				String docGender = control.getSex();
				
				if (SexType.valueOf(docGender).equals(sex)) {
					Float dataPoint = control.getDataPoint();
					logger.debug("data value=" + dataPoint);
							addScatterPoint(dataArray, control, dataPoint);
				}
				
			}
			try {
				controlJsonObject.put("data", dataArray);
				//if we want opacity not 100% we can use code like below?
				//controlJsonObject.put("color", "rgba(223, 83, 83, .5)");
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			series.put(controlJsonObject);

			logger.debug("finished putting control to data points");
			TimeSeriesStats stats = new TimeSeriesStats();
			List<DiscreteTimePoint> controlMeans = stats
					.getMeanDataPoints(controlDataPoints);

			lines.put(WordUtils.capitalize(sex.name())+" Control", controlMeans);
			
			JSONObject expZyg=new JSONObject();
			JSONArray expDataArray=new JSONArray();
			for (ZygosityType zType : experiment.getZygosities()) {
				colorIndex++;
				try {
					expZyg.put("name", sex+" "+zType);
					String markerString=ChartColors.getMarkerString(sex, zType);
					expZyg.put("color", ChartColors.getMutantColor(ChartColors.alphaScatter));
					if(sex.equals(SexType.male)) {
						expZyg.put("symbol", "triangle");
					}
					
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Set<ObservationDTO> expObservationsSet = Collections.emptySet();
				if (zType.equals(ZygosityType.heterozygote)
						|| zType.equals(ZygosityType.hemizygote)) {
					expObservationsSet = experiment.getHeterozygoteMutants();
				}
				if (zType.equals(ZygosityType.homozygote)) {
					expObservationsSet = experiment.getHomozygoteMutants();
				}

				for (ObservationDTO expDto : expObservationsSet) {
					// get the attributes of this data point
					String docGender = expDto.getSex();
					if (SexType.valueOf(docGender).equals(sex)) {
						Float dataPoint = expDto.getDataPoint();
						addScatterPoint(expDataArray, expDto, dataPoint);

					}
				}
				try {
					expZyg.put("data", expDataArray);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				series.put(expZyg);
			}
		}// end of gender

			ScatterChartAndData scatterChartAndData=new ScatterChartAndData();
			String chartString=createScatter(experimentNumber, parameter, series);
			scatterChartAndData.setChart(chartString);
				

		return scatterChartAndData;
	}

	private void addScatterPoint(JSONArray dataArray, ObservationDTO control,
			Float dataPoint) {
		JSONArray timeAndValue = new JSONArray();
		Date date = control.getDateOfExperiment();
		//Date.UTC(1970,  9, 27)
		long dateString = date.getTime();
		timeAndValue.put(dateString);
		timeAndValue.put(dataPoint);
		dataArray.put(timeAndValue);
	}
	
	
	
//	$(function () {
//	    chart71maleWTSI = new Highcharts.Chart({
//	        chart: {
//	            renderTo: 'container',
//	            type: 'scatter',
//	            zoomType: 'xy'
//	        },
//	        title: {
//	            text: 'Mean corpuscular hemoglobin'
//	        },
//	        subtitle: {
//	            text: 'Female'
//	        },
//	        xAxis: {
//	            type: 'datetime',
//	            title: {
//	                enabled: true,
//	                text: 'Mouse'
//	            },
//	            labels: {
//	                rotation: -45,
//	                align: 'right',
//	                style: {
//	                    fontSize: '13px',
//	                    fontFamily: 'Verdana, sans-serif'
//	                }
//	            },
//	            showLastLabel: true
//	        },
//	        yAxis: {
//	            max: 18.05,
//	            min: 15.9,
//	            title: {
//	                text: 'pg'
//	            }
//	        },
//	        credits: {
//	            enabled: false
//	        },
//	        plotOptions: {
//	            scatter: {
//	                marker: {
//	                    radius: 5,
//	                    states: {
//	                        hover: {
//	                            enabled: true,
//	                            lineColor: 'rgb(100,100,100)'
//	                        }
//	                    }
//	                },
//	                states: {
//	                    hover: {
//	                        marker: {
//	                            enabled: false
//	                        }
//	                    }
//	                }
//	            }
//	        },
//	        tooltip: {
//	            formatter: function () {
//	                return '<b>' + this.series.name + '</b><br/>' + Highcharts.dateFormat('%e %b %Y', this.x) + ': ' + this.y + ' pg ';
//	            }
//	        },
//	        series: [{
//	            name: 'WT ',
//	            data: [
//	                [1079481600000, 17.12],
//	                [1161126000000, 17.6],
//	                [1079481600000, 17.1],
//	                [1003359600000, 16.8],
//	                [1003359600000, 16.8],
//	                [1075248000000, 17.36], ]
//	        }, {
//	            name: 'HOM ',
//	            data: [
//	                [1003359600000, 16.8],
//	                [1003359600000, 17.7],
//	                [1003359600000, 16.5],
//	                [1003359600000, 16.9],
//	                [1003359600000, 17.6], ]
//	        }, ]
//	    });
//	});

}
