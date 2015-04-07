package uk.ac.ebi.phenotype.chart;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.ac.ebi.phenotype.dao.DiscreteTimePoint;
import uk.ac.ebi.phenotype.dao.PhenotypePipelineDAO;
import uk.ac.ebi.phenotype.pojo.BiologicalModel;
import uk.ac.ebi.phenotype.pojo.ObservationType;
import uk.ac.ebi.phenotype.pojo.Parameter;
import uk.ac.ebi.phenotype.pojo.Procedure;
import uk.ac.ebi.phenotype.pojo.SexType;
import uk.ac.ebi.phenotype.pojo.ZygosityType;
import uk.ac.ebi.phenotype.service.ImpressService;
import uk.ac.ebi.phenotype.service.dto.ExperimentDTO;
import uk.ac.ebi.phenotype.service.dto.ObservationDTO;

@Service
public class ScatterChartAndTableProvider {
	
	private static final Logger logger = Logger.getLogger(ScatterChartAndTableProvider.class);
	@Autowired 
	PhenotypePipelineDAO ppDAO;
	
	@Autowired 
	ImpressService impressService;
	
	public String createScatter(ExperimentDTO experiment, Float min, Float max, String experimentNumber, Parameter parameter, JSONArray series) {
		
		Procedure proc = ppDAO.getProcedureByStableId(experiment.getProcedureStableId()) ;
		String procedureDescription = "";
		if (proc != null) {
			procedureDescription = String.format("<a href=\"%s\">%s</a>", impressService.getProcedureUrlByKey(((Integer)proc.getStableKey()).toString()), proc.getName());
		}
		
		
		String chartString="	$(function () { "
			+ "  chart71maleWTSI = new Highcharts.Chart({ "
			+ "     chart: {"
			+ "renderTo: 'scatter"
			+ experimentNumber + "',"
			+ "         type: 'scatter',"
			+ "         zoomType: 'xy'"
	
			+ "     },"
			+ "   title: {  text: 'Scatterplot by Date' },"
			+ "     xAxis: {"
			+ "         type: 'datetime',"
			+ "       labels: { "
			+ "           rotation: -45, "
			+ "           align: 'right', "
			+ "           style: { "
			+ "              fontSize: '13px', "
			+ "              fontFamily: 'Verdana, sans-serif' "
			+ "         } "
			+ "     }, "
			+ "      showLastLabel: true "
			+ "  }, "
			+ "    yAxis: { "
			+ " tickAmount: 5,"
			+ (max != null ? "        max: " + max + ", " : "")
			+ (min != null ? "         min: " + min + ", " : "")
			+ "         title: { "
			+ "             text: '" + parameter.getUnit() + "' "
			+ "           } "
			+ "       }, "
			+ "      credits: { "
			+ "         enabled: false "
			+ "      }, "
			+ "      plotOptions: { "
			+ "        scatter: { "
			+ "            marker: { "
			+ "                radius: 5, "
			+ "                states: { "
			+ "                hover: { "
			+ "                    enabled: true, "
			+ "                    lineColor: 'rgb(100,100,100)' "
			+ "               } "
			+ "           } "
			+ "       }, "
			+ "       states: { "
			+ "           hover: { "
			+ "               marker: { "
			+ "                   enabled: false "
			+ "               } "
			+ "           } "
			+ "        } "
			+ "     } "
			+ "   }, "
			+ "     tooltip: { "
			+ "          formatter: function () { "
			+ "              return '<b>' + this.series.name + '</b><br/>' + Highcharts.dateFormat('%e %b %Y', this.x) + ': ' + this.y + ' " + parameter.getUnit() + " '; "
			+ "          } "
			+ "      }, "
			+ "     series: " +
			series.toString()
			+ "    }); "
			+ "	}); ";
				
		System.out.println("charty here " + chartString);
		
		return chartString;
	}

	
	public ScatterChartAndData doScatterData(ExperimentDTO experiment, Float yMin, Float yMax,Parameter parameter, String experimentNumber, BiologicalModel expBiologicalModel)
	throws IOException,	URISyntaxException {
		
		JSONArray series=new JSONArray();
		// maybe need to put these into method that can be called as repeating
		// this - so needs refactoring though there are minor differences?
		Map<String, List<DiscreteTimePoint>> lines = new HashMap<String, List<DiscreteTimePoint>>();
		
		for (SexType sex : experiment.getSexes()) {
			
			List<DiscreteTimePoint> controlDataPoints = new ArrayList<>();
			JSONObject controlJsonObject=new JSONObject();
			JSONArray dataArray=new JSONArray();
			
			try {
				controlJsonObject.put("name", WordUtils.capitalize(sex.name())+" "+"WT");				
				JSONObject markerObject=ChartColors.getMarkerJSONObject(sex, null);
				controlJsonObject.put("marker", markerObject);			
			} catch (JSONException e) {
			e.printStackTrace();
			}
			
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
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			series.put(controlJsonObject);

			TimeSeriesStats stats = new TimeSeriesStats();
			List<DiscreteTimePoint> controlMeans = stats.getMeanDataPoints(controlDataPoints);
			lines.put(WordUtils.capitalize(sex.name())+" WT", controlMeans);
			logger.debug("finished putting control to data points");			
		}
		
		for (SexType sex : experiment.getSexes()) {
			JSONObject expZyg = new JSONObject();
			JSONArray expDataArray = new JSONArray();
			
			for (ZygosityType zType : experiment.getZygosities()) {
				
				try {
					expZyg.put("name", WordUtils.capitalize(sex.name()) + " " + WordUtils.capitalize(zType.getShortName()));
					JSONObject markerObject = ChartColors.getMarkerJSONObject(sex, zType);
					expZyg.put("marker", markerObject);					
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
				
				Set<ObservationDTO> expObservationsSet = Collections.emptySet();
				
				if (zType.equals(ZygosityType.heterozygote)) {
					expObservationsSet = experiment.getHeterozygoteMutants();
				}
                if (zType.equals(ZygosityType.hemizygote)) {
					expObservationsSet = experiment.getHemizygoteMutants();
				}                                
				if (zType.equals(ZygosityType.homozygote)) {
					expObservationsSet = experiment.getHomozygoteMutants();
				}

				for (ObservationDTO expDto : expObservationsSet) {
					String docGender = expDto.getSex();
					if (SexType.valueOf(docGender).equals(sex)) {
						Float dataPoint = expDto.getDataPoint();
						addScatterPoint(expDataArray, expDto, dataPoint);
					}
				}
				
				try {
					expZyg.put("data", expDataArray);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				series.put(expZyg);
			}
		}

		ScatterChartAndData scatterChartAndData = new ScatterChartAndData();
		String chartString = createScatter(experiment, yMin, yMax, experimentNumber, parameter, series);
		scatterChartAndData.setChart(chartString);
		
		List<UnidimensionalStatsObject> unidimensionalStatsObjects=null;
		if(experiment.getObservationType().equals(ObservationType.unidimensional)) {
			unidimensionalStatsObjects = UnidimensionalChartAndTableProvider.createUnidimensionalStatsObjects(experiment, parameter, expBiologicalModel);
			scatterChartAndData.setUnidimensionalStatsObjects(unidimensionalStatsObjects);
		}
		return scatterChartAndData;
	}

	
	private void addScatterPoint(JSONArray dataArray, ObservationDTO control, Float dataPoint) {
		
		JSONArray timeAndValue = new JSONArray();
		Date date = control.getDateOfExperiment();
		//Date.UTC(1970,  9, 27)
		long dateString = date.getTime();
		timeAndValue.put(dateString);
		timeAndValue.put(dataPoint);
		dataArray.put(timeAndValue);
	}
	
}
