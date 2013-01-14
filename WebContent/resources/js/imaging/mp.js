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
 * 
 * mp.js: draw a chart using HighChart library
 */
jQuery(document).ready(	function() {
	
	if ($('#ovRight').is(':empty')){
							$('#ovRight').append('<div id="chart" style="width: 100%; height: 200px"></div>');
								
							 var chart1 = new Highcharts.Chart({
					            chart: {
					                renderTo: 'chart',
					                type: 'scatter',
					                zoomType: 'xy'
					            },
					            title: {
					                text: 'Charts Coming Soon!'
					            },
					            subtitle: {
					                text: 'Mouse Data'
					            },
					            xAxis: {
					                title: {
					                    enabled: true,
					                    text: 'x Axis'
					                },
					                startOnTick: true,
					                endOnTick: true,
					                showLastLabel: true
					            },
					            yAxis: {
					                title: {
					                    text: 'Y Axis'
					                },
					            plotBands: [{ // range band
					                from: 55,
					                to: 65,
					                color: 'rgba(68, 170, 213, 0.1)',
					                label: {
					                    text: 'Real Data Coming Soon!',
					                    style: {
					                        color: '#606060'
					                    }
					                }
					            }]
					            },
					            tooltip: {
					                formatter: function() {
					                        return ''+
					                        this.x +' cm, '+ this.y +' kg';
					                }
					            },
					            legend: {
					                layout: 'vertical',
					                align: 'left',
					                verticalAlign: 'top',
					                x: 100,
					                y: 70,
					                floating: true,
					                backgroundColor: '#FFFFFF',
					                borderWidth: 1
					            },
					            plotOptions: {
					                scatter: {
					                    marker: {
					                        radius: 5,
					                        states: {
					                            hover: {
					                                enabled: true,
					                                lineColor: 'rgb(100,100,100)'
					                            }
					                        }
					                    },
					                    states: {
					                        hover: {
					                            marker: {
					                                enabled: false
					                            }
					                        }
					                    }
					                }
					            },
					            series: [{
					                name: '',
					                color: 'rgba(223, 83, 83, .5)',
					                data: []
					    
					            }, {
					                name: '',
					                color: 'rgba(119, 152, 191, .5)',
					                data: []
					            }]
					        });
					    
							
							
							
	}
	
}

);