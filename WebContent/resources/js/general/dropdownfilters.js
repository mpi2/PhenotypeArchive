$(document).ready(function(){						
						
						$.fn.qTip('gene');	// bubble popup for brief panel documentation					
						//function to fire off a refresh of a table and it's dropdown filters
				
						
						/* var oDataTable = $('table#phenotypes').dataTable();
						oDataTable.fnDestroy();  */
						// use jquery DataTable for table searching/sorting/pagination
						var aDataTblCols = [0,1,2,3,4,5,6];
						var oDataTable = $.fn.initDataTable($('table#phenotypes'), {
			  						"aoColumns": [
			  							{ "sType": "html", "mRender":function( data, type, full ) {
			  						        return (type === "filter") ? $(data).text() : data;
			  						    }},
			  							{ "sType": "html", "mRender":function( data, type, full ) {
			  						        return (type === "filter") ? $(data).text() : data;
			  						    }},
			  						    { "sType": "string"},
			  						    { "sType": "string"},
			  							{ "sType": "alt-string", "bSearchable" : false },
			  						   /*  { "sType": "string"}, */
			  						    { "sType": "html"}
			  						    , { "sType": "string", "bSortable" : false }

			  						],
			  						"bDestroy": true,
			  						"bFilter":false
			  					});
						
						$('[rel=tooltip]').tooltip();
													    		
			    		//$.fn.dataTableshowAllShowLess(oDataTable, aDataTblCols, null);
			    		$('div#exportIconsDiv').append($.fn.loadFileExporterUI({
			    			label: 'Export table as:',
			    			formatSelector: {
			    				TSV: 'tsv_phenoAssoc',
			    				XLS: 'xls_phenoAssoc'	    			 					
			    			},
			    			class: 'fileIcon'
			    		}));
			    		
			    		var mgiGeneId = window.location.href.split("/")[window.location.href.split("/").length-1];
			    		alert(mgiGeneId);
			    		
			    		initFileExporter({
			    			mgiGeneId: mgiGeneId,
			    			externalDbId: 3,
			    			fileName: 'phenotype_associations_for_'+mgiGeneId.replace(/:/g,'_'),
			    			solrCoreName: 'genotype-phenotype',
			    			dumpMode: 'all',
			    			baseUrl: baseUrl,
			    			page:"gene",
			    			gridFields: 'marker_symbol,allele_symbol,zygosity,sex,procedure_name,resource_fullname,parameter_stable_id,marker_accession_id, parameter_name,parameter_name,mp_term_name',
			    			params: "qf=auto_suggest&defType=edismax&wt=json&rows=100000&q=*:*&fq=(marker_accession_id:\"" + mgiGeneId + "\")"
			    		});
			    		function initFileExporter(conf){

			    			$('button.fileIcon').click(function(){
			    				var fileType = $(this).text();
			    				var url = baseUrl + '/export';	 
			    				var sInputs = '';
			    				for ( var k in conf ){
			    					sInputs += "<input type='text' name='" + k + "' value='" + conf[k] + "'>";	 
			    				}
			    				sInputs += "<input type='text' name='fileType' value='" + fileType.toLowerCase() + "'>";
			    				  
//			    				$("<form action='"+ url + "' method=get>" + sInputs + "</form>").appendTo('body').submit().remove(); 
			    				var form = $("<form action='"+ url + "' method=get>" + sInputs + "</form>");		
			    				_doDataExport(url, form);
			    				
			    			}).corner('6px');	 
			    		}  

			    		 function _doDataExport(url, form){
			    		    	$.ajax({
			    					type: 'GET',
			    					url: url,
			    					cache: false,
			    					data: $(form).serialize(),
			    					success:function(data){    				
			    						$(form).appendTo('body').submit().remove();
			    					},
			    					error:function(){
			    						//alert("Oops, there is error during data export..");
			    					}
			    				});
			    		 }
			    		
			    		function refreshPhenoTable(newUrl){
			    			 //alert(newUrl);
			    			  $.ajax({
			    				  url: newUrl,
			    				  cache: false
			    				}).done(function( html ) {
			    				  $("#phenotypes_wrapper").html(html);//phenotypes wrapper div has been created by the original datatable so we need to replace this div with the new table and content
			    				  var aDataTblCols = [0,1,2,3,4,5];
									var oDataTable = $.fn.initDataTable($('table#phenotypes'), {
						  						"aoColumns": [
						  							{ "sType": "html", "mRender":function( data, type, full ) {
						  						        return (type === "filter") ? $(data).text() : data;
						  						    }},
						  							{ "sType": "html", "mRender":function( data, type, full ) {
						  						        return (type === "filter") ? $(data).text() : data;
						  						    }},
						  						    { "sType": "string"},
						  							{ "sType": "alt-string", "bSearchable" : false },
						  						   /*  { "sType": "string"}, */
						  						    { "sType": "html"}
						  						    , { "sType": "string", "bSortable" : false }

						  						],
						  						"bDestroy": true,
						  						"bFilter":false
						  					});
			    				  	//alert('calling new table in genes.jsp');
									//$oDataTable.fnDraw(); 
			    				});
			    		}
			    		//http://stackoverflow.com/questions/5990386/datatables-search-box-outside-datatable
			    		//to move the input text or reassign the div that does it and hide the other one??
			    		//put filtering in another text field than the default so we can position it with the other controls like dropdown ajax filters for project etc
						/* $('#myInputTextField').keypress(function(){
							oDataTable.fnFilter( $(this).val() );
						}); */
					
			    		//stuff for dropdown tick boxes here
			    		var multipleSelectA =$('#top_level_mp_term_name');
						var multipleSelectB =  $('#resource_fullname');
						createDropdown(multipleSelectA,"Top Level MP: All", multipleSelectB);
						createDropdown(multipleSelectB, "Source: All", multipleSelectA);
						
			    		function createDropdown(multipleSelect1, emptyText,  multipleSelect2){
			    		$(multipleSelect1).dropdownchecklist( { firstItemChecksAll: false, emptyText: emptyText, icon: {}, 
			    			minWidth: 150, onItemClick: function(checkbox, selector){
			    			
			    			var justChecked = checkbox.prop("checked");
			    			console.log("justChecked="+justChecked);
			    			console.log("checked="+ checkbox.val());
			    			 var values = [];
			    			
			    		        for(var  i=0; i < selector.options.length; i++ ) {
			    		            if (selector.options[i].selected && (selector.options[i].value != "")) {
			    		                values .push(selector.options[i].value);
			    		            }
			    		        }
			    			
			    		        if(justChecked){
			    		        	
			    		        	/*  if(checkbox.val()!="All"){
				    		        		console.log("all not checked");
				    		        		//need to remove the all from the values as this is unchecked by clicking something other than all
				    		        		 var index = $.inArray("All", values);
				    		        		 console.log("All index="+index);
			    		        				values.splice(index, 1+1);//+1 as we have an "all" option added by the plugin
				    		        	} */
			    		        	 values.push( checkbox.val());
			    		        }else{//just unchecked value is in the array so we remove it as already ticked
			    		        	
 		    		        	 var index = $.inArray(checkbox.val(), values);
				    		         console.log("index="+index);
///			    		        	values.splice(index, 1+1);//+1 as we have an "all" option added by the plugin
				    		       values.splice(index, 1);//I think the + 1 was actually only introducing a bug.
			    		        }  
			    		        
			    		      /*    if(index == -1)//just checked value not in the array so we add it
			    		        {
			    		        	
			    		         
			    		        } */
			    			  	console.log("values="+values );
			    			  	//refactor to call the method from here with arrays?
			    			  			var array1=$(multipleSelect2).val() || [];
			    			  			console.log("id="+multipleSelect1.attr('id'));
			    			refreshGenesPhenoFrag(multipleSelect1.attr('id'), values, multipleSelect2.attr('id') , array1  );
			    			
			    		}
			    		} );
			    		}
						
			    		// $("select[multiple]").bsmSelect();
			    		//if filter parameters are already set then we need to set them as selected in the dropdowns
			    		var previousParams=$("#filterParams").html();
			    		//alert('previous='+previousParams);
			    		
			    		function refreshGenesPhenoFrag(name1, array1, name2, array2) {
			  					var rootUrl=window.location.href;
			    			 console.log("genesPhenFrag method called with array1="+array1+" and array2="+array2);
			    			 //if array1 or array2 contains "All" then empty them as we don't want to use a filter for that field
			    			 /* var index = $.inArray("All",array1);
			    		         console.log("index="+index);
			    		         if(index != -1)//All is in array
			    		        {
			    		        	array1=[];//empty array
			    		         
			    		        }
			    		         
			    		         var index = $.inArray("All",array2);
			    		         console.log("index="+index);
			    		         if(index != -1)//All is in array
			    		        {
			    		        	array2=[];//empty array
			    		         
			    		        } */
			    			  var newUrl=rootUrl.replace("genes", "genesPhenoFrag");
			    			// alert( $("option:selected").parent().attr("id"));
			    			 var output ='?';
			    			//http://wwwdev.ebi.ac.uk/mi/solr/genotype-phenotype/select/?q=marker_accession_id:MGI:98373&rows=100&version=2.2&start=0&indent=on&defType=edismax&wt=json&facet=true&facet.field=project_name&facet.field=top_level_mp_term_name&fq=top_level_mp_term_name:(%22vision/eye%20phenotype%22%20OR%20%22craniofacial%20phenotype%22)
			    			//var array1=$("#resource_fullname").val() || [];
			    			if(array1.length==1){//if only one entry for this parameter then don't use brackets and or
			    				 output+='&fq='+name1+':"'+array1[0]+'"';
			    			} 
							if(array1.length>1)	{
			    				output+='&fq='+name1+':(';//note " before and after value for solr handle spaces
			    			 		for(var i=0; i<array1.length; i++){
			    						 
			    							 //if(i==0)output+=' " ';
			    						 output+='"'+array1[i]+'"';
			    						 if(i<array1.length-1){
			    							 output+=' OR ';
			    						 }else{
			    							 output+=')';
			    						 }
			    						 //console.log('logging='+array1[i]);
			    			 }
			    		}
			    			 var output2 ='';//='"'+ ($("#top_level_mp_term_name").val() || []).join('"&fq=top_level_mp_term_name:"')+'"';
			    			 //var array2=$("#top_level_mp_term_name").val() || [];
			    				if(array2.length==1){//if only one entry for this parameter then don't use brackets and or
				    				 output+='&fq='+name2+':"'+array2[0]+'"';
				    			}
			    				if(array2.length>1){
				    				output+='&fq='+name2+':(';//note " before and after value for solr handle spaces
			    			 			for(var i=0; i<array2.length; i++){
			    			 				 output+='"'+array2[i]+'"';
				    						 if(i<array2.length-1){
				    							 output+=' OR ';
				    						 }else{
				    							 output+=')';
				    						 }
			    			 		}
			    			 }
			    			 newUrl+=output+output2;
			    			 refreshPhenoTable(newUrl);
			    			  return false;
			    			}
			    		
			    		$(".filterTrigger").click(function() {
					        //Do stuff when clicked
							//the id is set as the field to be filtered on
							//set the value of the current id of the trigger
							var filter=$(this).attr("id");
							//var currentTickedValues=$('#top_level_mp_term_name').val() || [];//use this if we want to retain already ticked boxes
							//currentTickedValues.push(filter);
							$('#top_level_mp_term_name').val(filter);
							//$("#top_level_mp_term_name").val(...) // do something to the original select, for example, changing which items are selected
							$("#top_level_mp_term_name").dropdownchecklist("refresh");
							$('#resource_fullname').val([]);
							//$("#top_level_mp_term_name").val(...) // do something to the original select, for example, changing which items are selected
							$("#resource_fullname").dropdownchecklist("refresh");
							var array1= $('#top_level_mp_term_name').val() || [];
							var array2=[];//set array for second dropdown to empty so we get the same numbers as in Illincas links if want to keep we need this code -> $('#resource_fullname').val() || [];
							refreshGenesPhenoFrag('top_level_mp_term_name', array1, 'resource_fullname' , array2  );
					    });
						
});
