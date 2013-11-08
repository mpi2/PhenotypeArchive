package uk.ac.ebi.phenotype.stats;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang.WordUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.apache.log4j.Logger;
import org.json.JSONArray;

import uk.ac.ebi.phenotype.pojo.BiologicalModel;
import uk.ac.ebi.phenotype.pojo.Parameter;
import uk.ac.ebi.phenotype.pojo.SexType;
import uk.ac.ebi.phenotype.pojo.ZygosityType;

public class ScatterGraph {

private static final Logger log = Logger.getLogger(ScatterGraph.class);
	
	/**
	 * Does things like get the min and max
	 * @param sexType
	 * @param zyList
	 * @param mouseDataPointSets
	 *            - list of floats for WT then hom or het
	 * @param biologicalModel
	 * @param parameterUnit
	 * @param xAxisCategoriesList
	 *            - bare categories from database e.g. WT, HOM
	 * @param continuousBarCharts
	 * @param max
	 * @return map containing min and max values
	 */
	public ChartData processScatterChartData(String title, SexType sexType,
			Parameter parameter, Set<ZygosityType> set,
			List<String> zyList, List<List<MouseDataPoint>> mouseDataPointSets,
			BiologicalModel expBiologicalModel, Boolean byMouseId) {
		// http://localhost:8080/phenotype-archive/stats/genes/MGI:1929878?parameterId=ESLIM_015_001_018
		// List<ChartData> chartsAndTables = new ArrayList<ChartData>();
		Float max = new Float(0);
		Float min = new Float(100000000);
		Map<String, Float> minMax = new HashMap<String, Float>();
		String yAxisLabel="";
		String parameterUnitxAxis = parameter.checkParameterUnit(1);
		String parameterUnitYAxis = parameter.checkParameterUnit(2);
			if(parameterUnitYAxis==null) {
					yAxisLabel=parameterUnitxAxis;
			}else {
					yAxisLabel=parameterUnitYAxis;
			}
		List<String> categoriesList = new ArrayList<String>();
		// List<String> categoriesListBarChart = new ArrayList<String>();

		// always add the control columns - one for boxmodel and one for
		// observations
		categoriesList.add("WT");

		// add two columns for each zyg
		for (ZygosityType zType : set) {
			if (zyList.isEmpty() || zyList.contains(zType.name())) {
				categoriesList.add(zType.name().substring(0, 3).toUpperCase());
				String alleleComposition =expBiologicalModel.getAllelicComposition();
				if (zType.equals(ZygosityType.homozygote)) {// if homozygote
															// don't need the
															// second part of
															// the string after
															// the forward slash
					alleleComposition = alleleComposition.substring(0,
							alleleComposition.indexOf("/"));
				}
			}
		}
		log.debug("raw data=" + mouseDataPointSets);
		// first list is control/wt then mutant for hom or het or both

		for (List<MouseDataPoint> listOfFloats : mouseDataPointSets) {
			// Get a DescriptiveStatistics instance
			DescriptiveStatistics stats = new DescriptiveStatistics();

			// Add the data from the array
			for (MouseDataPoint point : listOfFloats) {
				stats.addValue(point.getDataPoint());
			}

			Float maxTemp = new Float(stats.getMax());

			if (maxTemp > max) {
				max = maxTemp;// count
			}

			Float minTemp = new Float(stats.getMin());
			if (minTemp < min) {
				min = minTemp;// count
			}
			minMax.put("min", min);
			minMax.put("max", max);

		}
		

		String chartString = createScatterPlotChartsString(categoriesList,
				title, sexType, yAxisLabel, mouseDataPointSets,
				byMouseId);
		// continuousCharts.add(chartString);
		ChartData cNTable = new ChartData();
		// cNTable.setTable(table);
		cNTable.setChart(chartString);
		cNTable.setMin(min);
		cNTable.setMax(max);
		return cNTable;
	}
	
	/**
	 * 
	 * @param title
	 *            main title of the graph
	 * @param yAxisTitle
	 *            - unit of measurement - how to get this from the db?
	 * @param scatterColumns
	 * @param dateGraph TODO
	 * @param xAisxCcategoriesList
	 *            e.g. WT, WT, HOM, HOM for each column to be displayed
	 * @param theoreticalMean
	 *            - not sure if we need this - draws a line across the graph
	 *            currently red
	 * @return
	 */
	private String createScatterPlotChartsString(
			List<String> xAxisCategoriesList, String title, SexType sex,
			String yAxisTitle,
			List<List<MouseDataPoint>> scatterColumns,
			boolean byMouseId) {
		String xAxisTitle = "Mouse";
		JSONArray categoriesArray = new JSONArray(xAxisCategoriesList);
		String categories = categoriesArray.toString();// "['WT', 'WT', 'HOM', 'HOM']";
		System.out.println("categories=" + categories);
		System.out.println("scatter columns size=" + scatterColumns.size());

		//we need categories on the xAxis " categories: ['mouseId1','mouseId2','mouseId3'] , "
		
//		mouseIdStrings.add("mouseId1");
//		mouseIdStrings.add("mouseId2");
//		mouseIdStrings.add("mouseId3");
		//below mouseId1 is column 0 etc
//		series: [{
//            name: 'WT',
//            color: 'rgba(223, 83, 83, .5)',
//            data: [[0,161.2], [0, 159.5], [1,181.2], [1, 199.5],[2,161.2], [2, 159.5] ]
//
//        }, 
//         {
//            name: 'HOM',
//            color: 'rgba(119, 152, 191, .5)',
//            data: [[0,261.2], [0, 259.5], [1,261.2], [1, 259.5],[2,361.2], [2, 359.5] ]
//        }]
//		Map<String, Integer> mouseIdToColumn = new TreeMap<>();
//		Map<Date, Integer> mouseDateToColumn = new TreeMap<>();//sort date by natural order
//		List<String> mouseIdStrings=new ArrayList<>();
//       
        	
//        	xAxis: {
//            type: 'datetime',
//            dateTimeLabelFormats: { // don't display the dummy year
//                month: '%e. %b',
//                year: '%b'
//            }
        	//}
        
       	
		List<String> mouseIdStrings=naturallyOrderColumns(scatterColumns, byMouseId);
		
//		for (List<MouseDataPoint> mouseList : scatterColumns) {
//			for (MouseDataPoint mouseDataPoint : mouseList) {
//				Integer mouseColumn = null;
//
//				if (mouseIdToColumn.containsKey(mouseDataPoint.getMouseId())) {
//					mouseColumn = mouseIdToColumn.get(mouseDataPoint
//							.getMouseId());
//					mouseDataPoint.setColumn(mouseColumn);
//				} else {
//					mouseColumn = mouseIdToColumn.size();
//					mouseDataPoint.setColumn(mouseColumn);
//					mouseIdToColumn.put(mouseDataPoint.getMouseId(),
//							mouseColumn);
//
//				}
//			}
//		}
        
		
		//so we know that the columns should equal the number of mouseIds we have
//		for(int column=0; column<mouseIdToColumn.keySet().size(); column++) {
//			//get mice id for column index 0 then 1 etc and add to the mouseId list so it shoud correspond to the correct columns
//			for(String key: mouseIdToColumn.keySet()) {
//				int value=mouseIdToColumn.get(key);
//				if(value==column) {
//					System.out.println("column found "+column + "mouseId="+key);
//					mouseIdStrings.add(key);
//					}
//			}
//		}
		
		JSONArray mouseIdArrayJson = new JSONArray(mouseIdStrings);
		//then we need the values for each mouse in order in an array for each contol or zygosity set WT, HOM, HET is our xAxisCategories list
		String seriesString=" series: [ ";
		int i=0;
		//Date.UTC(1970,  9, 27)
		for(String xAxisCategory: xAxisCategoriesList) {
			seriesString+="{ name: '"
					+ xAxisCategory+" ' "
				//	+ "', color: 'rgba(223, 83, 83, .5)' "
					+", "
					+ "data: [";
					
					String data="";
					for(MouseDataPoint mouseDataPoint: scatterColumns.get(i)) {
						if(byMouseId) {
						data+="["+mouseDataPoint.getColumn() +"," +mouseDataPoint.getDataPoint()+"],"; 
						}
						else {
							Date date = mouseDataPoint.getDateOfExperiment();
							//Date.UTC(1970,  9, 27)
							long dateString = date.getTime();
							// highcharts expect date as milliseconds since 1970
							data+="["+dateString + ", " +mouseDataPoint.getDataPoint()+"],"; 
					//		System.out.println("data="+data + "  from  " + mouseDataPoint.getDateOfExperiment());
						}
					}
					seriesString+=data;
					seriesString+= " ] }, ";
				
			i++;
			}
		seriesString+="]";
		//use catagories like this instead for mouseId strings http://jsfiddle.net/QBvLS/
		
		String dateToolTip="tooltip: { "+
                " formatter: function() { "
                    +  "  return '<b>'+ this.series.name +'</b><br/>'+ "
                     +"   Highcharts.dateFormat('%e %b %Y', this.x) +': '+ this.y + ' "+yAxisTitle+" '; "
             +  " }"
          +"  },";
		String normalToolTip=" tooltip: {"
		         +"  formatter: function() { "
		            +"  return '<b>'+ this.series.name +'</b><br/>Mouse Id:'+"
		    +   "      this.x +': '+ this.y +'  "+yAxisTitle+"   '; "
		+     " } "
		+     "  }, ";
		
		String tooltip=dateToolTip;//default is date so datetooltip is default
		
		String categoriesString=" ";
		if(	byMouseId) {
			categoriesString=" categories:"+mouseIdArrayJson +" , ";
			tooltip=normalToolTip;
		};
		
		String scatterChartString = "{ chart: { type: 'scatter', zoomType: 'xy' }, title: { text: '"
				+ title
				+ "' }, subtitle: { text: '"
				+ WordUtils.capitalize(sex.name())
				+ "' }, xAxis: { "+
				" type: 'datetime',"
				+"title: { enabled: true, text: '"
				+ xAxisTitle
				+ "' },  "
				
			+categoriesString+
				
				" labels: { rotation: -45, align: 'right', style: { fontSize: '13px',  fontFamily: 'Verdana, sans-serif' }   }, "
	
					+"showLastLabel: true }, yAxis: { max: 2,  min: 0, title: { text: '"
				+ yAxisTitle
				+ "' } },  credits: { enabled: false },  plotOptions: { scatter: { marker: { radius: 5, states: { hover: { enabled: true, lineColor: 'rgb(100,100,100)' } } }, states: { hover: { marker: { enabled: false } } }"
				+" } },"+
				tooltip
					+
		            
				seriesString
//				+ " series: [{ name: '"
//				+ xAxisCategoriesList.get(0)
//				+ "', color: 'rgba(223, 83, 83, .5)', "
//				+ "data:"
//				+ controlScatterString
//				+ "}, "
//				+ // end of female
//				"{ name: '"
//				+ xAxisCategoriesList.get(1)
//				+ "', color: 'rgba(119, 152, 191, .5)', data:"
//				+ mutantScatterString + " }" + // end of male
//				"] " 
				
				
				
				+ // end of series
				"}); }";
		return scatterChartString;
//		$(function () {
//		    var chart;
//		    $(document).ready(function() {
//		        chart = new Highcharts.Chart({
//		            chart: {
//		                renderTo: 'container',
//		                type: 'scatter',
//		                zoomType: 'xy'
//		            },
//		            title: {
//		                text: 'Height Versus Weight of 507 Individuals by Gender'
//		            },
//		            subtitle: {
//		                text: 'Source: Heinz  2003'
//		            },
//		            xAxis: {
//		                title: {
//		                    enabled: true,
//		                    text: 'Height (cm)'
//		                },
//		                categories: ['mouse1','mouse2','mouse3']
//		            },
//		            yAxis: {
//		                title: {
//		                    text: 'Weight (kg)'
//		                }
//		            },
//		            tooltip: {
//		                formatter: function() {
//		                        return ''+
//		                        this.x +' cm, '+ this.y +' kg';
//		                }
//		            },
//		            legend: {
//		                layout: 'vertical',
//		                align: 'left',
//		                verticalAlign: 'top',
//		                x: 100,
//		                y: 70,
//		                floating: true,
//		                backgroundColor: '#FFFFFF',
//		                borderWidth: 1
//		            },
//		            plotOptions: {
//		                scatter: {
//		                    marker: {
//		                        radius: 5,
//		                        states: {
//		                            hover: {
//		                                enabled: true,
//		                                lineColor: 'rgb(100,100,100)'
//		                            }
//		                        }
//		                    },
//		                    states: {
//		                        hover: {
//		                            marker: {
//		                                enabled: false
//		                            }
//		                        }
//		                    }
//		                }
//		            },
//		            series: [{
//		                name: 'WT',
//		                color: 'rgba(223, 83, 83, .5)',
//		                data: [[0,161.2], [0, 159.5], [1,181.2], [1, 199.5],[2,161.2], [2, 159.5] ]
//		    
//		            }, 
//		             {
//		                name: 'HOM',
//		                color: 'rgba(119, 152, 191, .5)',
//		                data: [[0,261.2], [0, 259.5], [1,261.2], [1, 259.5],[2,361.2], [2, 359.5] ]
//		            }]
//		        });
//		    });
//		    
//		});
	}
	
	private List<String> naturallyOrderColumns(
			List<List<MouseDataPoint>> scatterColumns, boolean byMouseId) {

		Map<String, Integer> mouseIdToColumn = new TreeMap<>();
		Map<Date, Integer> mouseDateToColumn = new TreeMap<>();// sort date by
																// natural order
		List<String> mouseIdStrings = new ArrayList<>();

		// loop through all points and get a map that as TreeMap is
		// naturally ordered by date or id char
		for (List<MouseDataPoint> mouseList : scatterColumns) {

			if (!byMouseId) {

				for (MouseDataPoint mouseDataPoint : mouseList) {
					if (mouseDateToColumn.containsKey(mouseDataPoint
							.getDateOfExperiment())) {
						// do nothing if date key exists
					} else {
						// dont order until keys are ordered after we have all
						// keys
						mouseDateToColumn.put(
								mouseDataPoint.getDateOfExperiment(), 0);
					}
				}

			} else {
				// do string natural ordering as not dates
				for (MouseDataPoint mouseDataPoint : mouseList) {
					if (mouseIdToColumn.containsKey(mouseDataPoint
							.getMouseId())) {
						// do nothing if date key exists
					} else {
						// dont order until keys are ordered after we have all
						// keys
						mouseIdToColumn.put(
								mouseDataPoint.getMouseId(), 0);
					}
				}
			}
		}

		int column = 0;
		if(!byMouseId) {
		SimpleDateFormat shortFormat=new SimpleDateFormat("EEE, MMM d, ''yy");
		for (Date experimentDate : mouseDateToColumn.keySet()) {
			mouseIdStrings.add(shortFormat.format(experimentDate));
			mouseDateToColumn.put(experimentDate, column);
			column++;
		}
		}else {
	
		for (String mouseId : mouseIdToColumn.keySet()) {
			mouseIdStrings.add(mouseId);
			mouseIdToColumn.put(mouseId, column);
			column++;
		}
		}

		for (List<MouseDataPoint> mouseList : scatterColumns) {

			
				for (MouseDataPoint mouseDataPoint : mouseList) {
					
					if (!byMouseId) {
					setColumnsByDate(mouseDateToColumn, mouseDataPoint);
					}else {	
						setColumnsByMouseId(mouseIdToColumn, mouseDataPoint);
					}
				}
			}
		
		return mouseIdStrings;
	}
	
	private void setColumnsByMouseId(Map<String, Integer> mouseIdToColumn,
			MouseDataPoint mouseDataPoint) {
		Integer mouseColumn;
		if (mouseIdToColumn.containsKey(mouseDataPoint
				.getMouseId())) {
			mouseColumn = mouseIdToColumn.get(mouseDataPoint
					.getMouseId());
			mouseDataPoint.setColumn(mouseColumn);
		} else {
			System.err.println("something wrong - mouse Id should be in map");

		}
	}

	private void setColumnsByDate(Map<Date, Integer> mouseDateToColumn,
			MouseDataPoint mouseDataPoint) {
		Integer mouseColumn;
		if (mouseDateToColumn.containsKey(mouseDataPoint
				.getDateOfExperiment())) {
			mouseColumn = mouseDateToColumn.get(mouseDataPoint
					.getDateOfExperiment());
			mouseDataPoint.setColumn(mouseColumn);
		} else {
			System.err.println("something wrong - mouse exp date should be in map");

		}
	}
	
}
