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
 * tools.js: various tools used across the web application.
 * Use closure to safely write jQuery as $
 * as the closure creates a function with $ as parameter and is run immediately with the value
 * jQuery which gets mapped to $
 * 
 * Author: Chao-Kung Chen
 */
(function($){	
	
	$.fn.setHashUrl = function(q, core){		
		var hashParams = {};
		hashParams.q = q;
		hashParams.core = core;
		hashParams.fq = MPI2.searchAndFacetConfig.facetParams[core + 'Facet'].fq;
		window.location.hash = $.fn.stringifyJsonAsUrlParams(hashParams);
	}
	
	$.fn.updateBreadCrumb = function(coreName){
		var hashParams = $.fn.parseHashString(window.location.hash.substring(1));
		
		var breadcrumbBox = $('p.ikmcbreadcrumb');		
		var baseLinks = "<a href=" + drupalBaseUrl + ">Home</a> &raquo; <a href=" + baseUrl + "/search>Search</a> &raquo; ";
		
		if ( coreName && ! hashParams.coreName ){			
			hashParams.coreName = coreName;
			hashParams.fq = 'undefined';
		}	
		else if ( !coreName && !hashParams.q){		
			hashParams.q = "*:*";
			hashParams.coreName = 'gene';
			hashParams.fq = 'undefined';
		}		
		baseLinks += fetchFacetLink(hashParams);			
		breadcrumbBox.html(baseLinks);
	}
		
	function fetchFacetLink(hashParams){	
		var coreName = hashParams.coreName;
		var fq = MPI2.searchAndFacetConfig.facetParams[coreName+'Facet'].fq; // default for whole dataset of a facet
		var breadCrumbLabel = MPI2.searchAndFacetConfig.facetParams[coreName+'Facet'].breadCrumbLabel;		
		var url = encodeURI(baseUrl + "/search#q=*:*" + "&core=" + hashParams.coreName + "&fq=" + fq);
		return "<a href=" + url + ">" + breadCrumbLabel + "</a>";		
	}
	
	$.fn.openFacet = function(core){	    	
	    	
    	$('div.facetCatList').hide();
    	$('div.facetCat').removeClass('facetCatUp');
    	
    	// priority order of facet to be opened based on search result
    	if (core == 'gene'){	    		
    		$('div#geneFacet div.facetCatList').show();
    		$('div#geneFacet div.facetCat').addClass('facetCatUp'); 	  		
    	}	
    	else if (core == 'mp'){
    		$('div#mpFacet div.facetCatList').show();		
    		$('div#mpFacet div.facetCat').addClass('facetCatUp'); 
    	}
    	else if (core == 'pipeline'){
    		$('div#pipelineFacet div.facetCatList').show();	
    		$('div#pipelineFacet div.facetCat').addClass('facetCatUp'); 
    	}
    	else if (core == 'images'){
    		$('div#imagesFacet div.facetCatList').show();	
    		$('div#imagesFacet div.facetCat').addClass('facetCatUp'); 
    	}	    	
	}	
	
	$.fn.invokeFacetDataTable = function(oSolrSrchParams, facetDivId, gridName, hashState){
	
    	var self = this;    	
    	var oVal = MPI2.searchAndFacetConfig.facetParams[facetDivId];
    	if ( hashState && facetDivId == 'imagesFacet' ){
    		oVal.forceReloadImageDataTable = true;
    	}
    	
    	var sTopLevelTerm = oVal.topLevelName;	  
    	
 	    /*if ( $('div#'+ facetDivId + ' .facetTable td.highlight').size() == 1 && sTopLevelTerm != '' && !oVal.forceReloadImageDataTable){
 	    	// check if top level name in facet has been selected before for the current facet
 	 	   	// if yes, filter top level set with the selected	 	    	
 	    	//self._applyFacetTopLevelFilter(facetDivId); 	  
 	    	console.log('chk point');
 	    	
 	    }*/
 	    if ( $('table#' + gridName).size() == 1 
 	    		&&  $('div#'+ facetDivId + ' .facetTable td.highlight').size() == 0	 	    		
 	    		&& sTopLevelTerm == undefined
 	    		&& !oVal.forceReloadImageDataTable ){ 	    
 	    	return;
 	    }	 	    
 	    else {	 	    	
 	    	// no filtering
 	    	oVal.topLevelName = ''; // back to default
 	    	
	 	   	var oInfos = {};
	 		oInfos.params = $.fn.stringifyJsonAsUrlParams(oSolrSrchParams);	 		
	 		oInfos.solrCoreName = oVal.solrCoreName;
	 		oInfos.mode = oVal.gridName;		 			
	 		oInfos.dataTablePath = MPI2.searchAndFacetConfig.dataTablePath;	 		
	 			 	
	 		var facetLabel = MPI2.searchAndFacetConfig.facetParams[oVal.solrCoreName+'Facet'].breadCrumbLabel.toLowerCase();
	 			 		
	 	   	var dTable = $('<table></table>').attr({'id':oInfos.mode});	    		    	
	 	   	var thead = oVal.tableHeader;
	 	   	var tds = '';
	 	   	for (var i=0; i<oVal.tableCols; i++){		 	   
			   		tds += "<td></td>";
	 	   	}
	 	   	var tbody = $('<tbody><tr>' + tds + '</tr></tbody>');
	 	   	dTable.append(thead, tbody);
	 	    	
	 	   	//var title = $.fn.upperCaseFirstLetter(oVal.type);	 	   
	 	   	//var gridTitle = $('<div></div>').attr({'class':'gridTitle'}).html(title);
	 	   	var facetCount = oSolrSrchParams.facetCount;	 	   	
	 	   	var gridTitle = $('<div></div>').attr({'class':'gridTitle'});
	 	   	var searchKW = " search keyword: " + oSolrSrchParams.q;
	 	   	
	 	   	$('div#mpi2-search').html('');	
	 	   	
	 	   	// update breadcrumb	 	   	
	 	   	$.fn.updateBreadCrumb(oSolrSrchParams.coreName);
	 	   	
	 	   	if (facetDivId == 'imagesFacet'){		 	   		
				
				// toggles two types of views for images: annotation view, image view
	 	   		var viewLabel, viewMode;
	 	   		if ( oVal.imgViewSwitcherDisplay == 'Annotation View' ){
	 	   			viewLabel = 'Image View: lists annotations to an image';
	 	   			viewMode = 'imageView';
	 	   		} 
	 	   		else {		 	   			
	 	   			viewLabel = 'Annotation View: groups images by annotation';
	 	   			viewMode = 'annotView';		 	   		
	 	   		}
	 	   		
	 	   		if (oVal.showImgView){
	 	   			dTable.find('th:nth-child(2)').text("Image");		 	   			
	 	   		}
	 	   		
	 	   		gridTitle.append($('<div></div>').attr({'id':'imgView','rel':viewMode}).html("<span id='imgViewSubTitle'>"		 	   		
	 	   				+ viewLabel 
	 	   				+ "</span><span id='imgViewSwitcher'>Show " 
	 	   				+ oVal.imgViewSwitcherDisplay + "</span>")).append($('<div></div>').attr({'class':'gridSubTitle'}).html(''));
	 	   		
	 	   		if (facetDivId !== 'imagesFacet' && !hashState ){
	 	   			$.fn.invokeDataTable(oInfos);	
	 	   		}
	 	   		
	 	   		var unit = facetCount > 1 ? 'images' : 'image';
	 	   		var imgParams = "/images?q=*:*&q.option=AND&qf=auto_suggest&defType=edismax&wt=json";
	 			var imgUrl = encodeURI(baseUrl + imgParams);
	 	   		var imgUrlLink = "<a href=" + imgUrl + ">" + facetCount + " " + unit + "</a>";
	 	   		
	 	   		gridTitle.append(imgUrlLink + " for " + searchKW);
	 	   		
	 	   		$('div#mpi2-search').append(gridTitle, dTable);
	 	   		
	 	   		oInfos.showImgView = oVal.showImgView;		 	   		
	 	   		$.fn.invokeDataTable(oInfos);
	 	   		
	 	   		$('span#imgViewSwitcher').click(function(){		 	   			
	 	   			if (oVal.imgViewSwitcherDisplay == 'Annotation View'){
	 	   				oVal.imgViewSwitcherDisplay = 'Image View';	 	   				
	 	   				oVal.showImgView = false;		 	   				
	 	   			}
	 	   			else {
	 	   				oVal.imgViewSwitcherDisplay = 'Annotation View';		 	   				
	 	   				oVal.showImgView = true;	 	   				
	 	   			}		 	   			
	 	   			
	 	   			oVal.forceReloadImageDataTable = true;
	 	   					 	   			
	 	   			if ( !oVal.showImgView ){	 	   				
	 	   				delete(oSolrSrchParams['fq']); // remove default as specific fq will be added later depending on data type		 	   			
	 	   			}
	 	   			
	 	   			$.fn.invokeFacetDataTable(oSolrSrchParams, facetDivId, gridName);
	 	   		})
	 	   	}
	 	   	else {	 	 
	 	   		var unit = facetCount > 1 ? facetLabel : facetLabel.replace(/s$/,'');
	 	   		gridTitle.html(facetCount + ' ' + unit + " for " + searchKW);
	 	   		$('div#mpi2-search').append(gridTitle, dTable);	 
	 	   		$.fn.invokeDataTable(oInfos);
	 	   		
		  	}
 	    } 	    	
	}
		
	$.fn.fetchFilteredDataTable = function(obj, facetDivId, q, facetFilter){
		  
		var facetCount;	
    	var topLevelName;			
		var oSolrSrchParams = {}
		var displayedTopLevelName;
		var type = MPI2.searchAndFacetConfig.facetParams[facetDivId].type;	
		var imgParamStr, imgFacetParams;
		var oVal = MPI2.searchAndFacetConfig.facetParams[facetDivId];
		var dTable = $.fn.fetchEmptyTable(oVal.tableHeader, 
                 oVal.tableCols, oVal.gridName); // skeleton to fill data from server			  	
		
		var title = $.fn.upperCaseFirstLetter(oVal.type);	    	
		//var gridTitle = $('<div></div>').attr({'class':'gridTitle'}).html(title);		
		var gridTitle = $('<div></div>').attr({'class':'gridTitle'}).html("");	
		var searchKw = " AND search keyword: " + q; 
		var resultMsg = $('<div></div>').attr('id', 'resultMsg');
		
		
		// highlight only currently selected top level 
    	var ontology = oVal.ontology;
    	if ( ontology ){
    		topLevelName = obj.attr('rel'); 
    		$('div#'+ facetDivId + ' table td.' + ontology + 'TopLevel').removeClass('highlight');
    		obj.parent().siblings('td.'+ ontology + 'TopLevel').addClass('highlight');
    		facetCount = obj.parent().siblings('td.'+ ontology + 'TopLevel').attr('rel');
    		    		
    		// MP top level term filter
    		if ( ontology == 'mp' ){
				oSolrSrchParams = $.extend({}, oVal.filterParams, MPI2.searchAndFacetConfig.commonSolrParams);
				oSolrSrchParams.fq = "ontology_subset:* AND top_level_mp_term:\"" + topLevelName + "\"";				
				displayedTopLevelName = $('<div></div>').attr({'class':'gridSubTitle'}).html('Top level term: ' + topLevelName.replace(' phenotype', '')); 	
			} 
    	}	
    	else if (facetDivId == 'geneFacet') {
    		topLevelName = obj.attr('rel'); 
    		var topLevelNameOri = topLevelName;
    		
    		$('table#gFacet td').removeClass('highlight');
    		
    		obj.parent().siblings('td.geneSubfacet').addClass('highlight');
			facetCount = obj.parent().siblings('td.geneSubfacet').attr('rel');
    		    		
			// Gene subtype filter					
			var subFacetName = obj.attr('class');
			
			if (topLevelName == '1' && subFacetName.indexOf('phenotype_started') != -1){
				topLevelName = 'Started';
			}
			else if ( topLevelName == '1' && subFacetName.indexOf('phenotype_complete') != -1){    			
    			topLevelName = 'Complete';
    		}
			
			var subFacetFilter = subFacetName + ':"'  + topLevelNameOri + '"';
									
			MPI2.searchAndFacetConfig.facetParams[facetDivId].subFacet_filter_params = subFacetFilter;
			//MPI2.searchAndFacetConfig.facetParams[facetDivId].filterParams.fq += ' ' + subFacetFilter;		
			
			oSolrSrchParams = $.extend({}, MPI2.searchAndFacetConfig.commonSolrParams, 
					MPI2.searchAndFacetConfig.facetParams[facetDivId].filterParams);				
			oSolrSrchParams.fq = subFacetFilter;
						
			var label = $.fn.upperCaseFirstLetter(subFacetName).replace(/_/g, ' '); 
			
			if ( label.indexOf('Imits phenotype') != -1 ){
				label = 'Phenotyping status';
				topLevelName = topLevelName.replace('Phenotype ','');
			}
			else if ( label == 'Status' ){
				label = 'Mouse production status';
			}
			displayedTopLevelName = $('<div></div>').attr({'class':'gridSubTitle'}).html(label + ': ' + topLevelName);			
    	}
    	else if (facetDivId == 'pipelineFacet'){
    		topLevelName = obj.parent().siblings('td[class^=procedure]').text(); 
    		$('table#pipeline td[class^=procedure]').removeClass('highlight');
			obj.parent().siblings('td[class^=procedure]').addClass('highlight');
			facetCount = obj.parent().siblings('td[class^=procedure]').attr('rel');
    					
			var proc_stable_id = obj.attr('rel');
			var hiddenSid = "<span class='hiddenId'>" + proc_stable_id + "</span>";
            oSolrSrchParams = $.extend({}, oVal, MPI2.searchAndFacetConfig.commonSolrParams); 
            oSolrSrchParams.fq = 'procedure_stable_id:' + proc_stable_id;	 
                          			
            displayedTopLevelName = $('<div></div>').attr({'class':'gridSubTitle'}).html('Procedure: ' + topLevelName + hiddenSid); 	
    	}
    	else if (facetDivId == 'imagesFacet'){
    		
    		var oInfos = eval("(" + obj.attr('rel') + ")");
    		oSolrSrchParams.fq = obj.attr('class'); 
    		
    		topLevelName = oInfos.imgSubName;
    			    		
    		$('table#imgFacet td.imgExperiment').removeClass('highlight');
			obj.parent().siblings('td.imgExperiment').addClass('highlight'); 
			facetCount = obj.parent().siblings('td.imgExperiment').attr('rel');			
						
			imgParamStr = oInfos.params + '&facetCount=' + facetCount;								
			var unit = facetCount > 1 ? 'images' : 'image';
			var imgCountInfo = "<a href='" + oInfos.fullLink + "'>" + oInfos.imgCount + " " + unit + "</a>";
			var title = "<span class='imgTitle'>" + oInfos.imgType + ': ' + topLevelName + "</span>";
			
			displayedTopLevelName = $('<div></div>').attr({'class':'gridSubTitle'}).html(imgCountInfo + ' for ' + title + searchKw);	
			resultMsg.append(displayedTopLevelName);
			
			
			// toggles two types of views for images: annotation view, image view	 	   		
 	   		var viewLabel, imgViewSwitcherDisplay, viewMode;
 	   		if ( oVal.showImgView ){
 	   			dTable.find('th:nth-child(2)').text("Image");	
 	   			viewLabel = 'Image View: lists annotations to an image';
 	   			imgViewSwitcherDisplay = 'Show Annotation View'; 
 	   			viewMode = 'imageView';
 	   		}
 	   		else {
 	   			viewLabel = 'Annotation View: groups images by annotation';
 	   			imgViewSwitcherDisplay = 'Show Image View'; 
 	   			viewMode = 'annotView';
 	   		}
 	   		
 	   		gridTitle.append($('<div></div>').attr({'id':'imgView', 'rel':viewMode}).html("<span id='imgViewSubTitle'>" 	   		
 	   				+ viewLabel
 	   				+ "</span><span id='imgViewSwitcher'>" 
 	   				+ imgViewSwitcherDisplay + "</span>")); 	   		   		
 	   		 	   		
 	   		// img view switcher control
 	   		$('span#imgViewSwitcher').click(function(){	 
 	   			console.log('switcher click');
 	   			oVal.showImgView = oVal.showImgView ? false : true;	 
 	   			$.fn.fetchFilteredDataTable(obj, facetDivId, q, 'imgSwitcher');
 	   		});	    	
    	}
    	   	
    	oVal.topLevelName = topLevelName;
		oSolrSrchParams.q = q;			
		oSolrSrchParams.facetCount = facetCount;
		
		var oInfos = {};
		oInfos.solrCoreName = oVal.solrCoreName;
		oInfos.mode = oVal.gridName;
		//oInfos.dataTablePath = baseUrl + '/dataTable';
		oInfos.dataTablePath = MPI2.searchAndFacetConfig.dataTablePath;
		
		var facetLabel = MPI2.searchAndFacetConfig.facetParams[facetDivId].breadCrumbLabel.toLowerCase(); 		
		
		if (facetDivId == 'imagesFacet'){
			oInfos.params = imgParamStr; 				
			oInfos.showImgView = oVal.showImgView;			
		}			
		else {
			oInfos.params = $.fn.stringifyJsonAsUrlParams(oSolrSrchParams);
			if (facetDivId == 'geneFacet'){
				oInfos.params += '&fq=' + MPI2.searchAndFacetConfig.facetParams[facetDivId].filterParams.fq;				
			}
			
			var unit = facetCount > 1 ? facetLabel : facetLabel.replace(/s$/,'');
			var resultCount = "<span>" + facetCount + " " + unit + " for </span>";
			resultMsg.append(resultCount, displayedTopLevelName, searchKw);			
		}
		
		
		// hash state stuff	
		var hashParams = {};
		hashParams.q    = oSolrSrchParams.q;
		hashParams.core = oVal.solrCoreName;
		hashParams.fq   = oSolrSrchParams.fq;
		//var hashParamStr = $.fn.stringifyJsonAsUrlParams(oSolrSrchParams) + "&core=" + oVal.solrCoreName;
		var hashParamStr = $.fn.stringifyJsonAsUrlParams(hashParams);			
				
		if ( (hashParamStr == MPI2.searchAndFacetConfig.lastParams && facetFilter == 'imgSwitcher') || 
				(hashParamStr != MPI2.searchAndFacetConfig.lastParams)){
			$('div#mpi2-search').html('');
			if (facetDivId != 'imagesFacet' ){				
				$('div#mpi2-search').append(resultMsg, dTable);				 
			}
			else {			  		 	   		 	   		
	 	   		$('div#mpi2-search').append(gridTitle, resultMsg, dTable);
	 	   		$('span#imgViewSwitcher').click(function(){	 	   		
	 	   			oVal.showImgView = oVal.showImgView ? false : true;	 
	 	   			$.fn.fetchFilteredDataTable(obj, facetDivId, q, 'imgSwitcher');
	 	   		});	  
			}
		
			// update url params
			window.location.hash = hashParamStr;				
			//console.log($.fn.stringifyJsonAsUrlParams(oSolrSrchParams));	
			MPI2.searchAndFacetConfig.lastParams = hashParamStr;
						
			$.fn.updateBreadCrumb();
			
			$.fn.invokeDataTable(oInfos);		
		}		
    }
	
	$.fn.ieCheck = function(){
		
		if ( $.browser.msie && $.browser.version < 8.0 ){		
			var msg = "<div id='noSupport'>Dear user:<p><p>It appears that you are using Internet Explorer 7 or earlier version.<p>To ensure that IMPC is supporting the best browsing features, functionalities and experiences, " +
				  "and considering the security issues of older IEs, we decided not to support IE7 and earlier versions.<p>We are sorry if this has caused your inconvenience.<p>Here is a list of supported browsers: " +
				  "<a href='http://www.mozilla.org'>Firefox</a>, <a href='http://www.google.com/chrome'>Google chrome</a>, <a href='http://support.apple.com/downloads/#internet'>Apple safari</a>.<p>" +
				  "IMPC team.</div>";
			
			$('div.navbar').siblings('div.container').html(msg);
			return false;
		}
	}
	
	// inverse simple JSON: eg, {a: 'one', b: 'two}
	// cannot do complicated nested associated array
	$.fn.inverseSimpleJSON = function(json){
		var newJson = {};
		for ( var i in json ){
			newJson[json[i]] = i;
		}
		return newJson;
	}
	
	$.fn.endsWith = function(str, suffix){		
	    return str.indexOf(suffix, str.length - suffix.length) !== -1;
	}
	
	$.fn.composeSelectUI = function(aFormats, selName){
    	var oSelect = $('<select></select>').attr({'name': selName});    	
    	
    	for( var i=0; i<aFormats.length; i++){
    		oSelect.append("<option>" + aFormats[i]);
    	}	    	
    	return oSelect;
    }
    
    $.fn.loadFileExporterUI = function(conf){
    	var oFormatSelector = conf.formatSelector;
    	var label = conf.label;    	
    	var iconDiv = $('<div></div>').attr({'class': 'fileIcons'}).html(label);
    	for ( var f in oFormatSelector ){
    		var btn = $('<button></button>').attr({'class': oFormatSelector[f] + ' ' + conf['class']}).html(f);
    		$(iconDiv).append(btn);
    	}
    	return iconDiv;
    }
    
    $.fn.stringifyJsonAsUrlParams = function(json){
    	
    	var aStr = [];
    	for( var i in json ){
    		aStr.push(i + '=' + json[i]);
    	}
    	return aStr.join("&");
    }
        
    $.fn.parseUrlString = function(sUrl){
    	
    	var params = {};
    	//var parts = decodeURI(sUrl).replace(/[?&]+([^=&]+)=([^&]*)/gi, function(m,key,value) {
    	var parts = decodeURI(sUrl).replace(/[?|#&]+([^=&]+)=([^&]*)/gi, function(m,key,value) {	
    	       params[key] = value; //.replace(/"/g,'');
    	});
    	return params;
    }
    
    $.fn.updateFacetAndDataTableDisplay = function(hashParams){
    	var core = hashParams.coreName;
    	var facetDivId = core + 'Facet';
    	var caller = $('div#' + facetDivId);
    	MPI2.searchAndFacetConfig.backButton = 1;
    	
    	if ( typeof core === 'undefined' ){
    		if ( hashParams.q != '*' && hashParams.q != '*:*' && hashParams.q != '' ){    		
    			document.location.reload();    		
    		}
    		else {
    			$('div#geneFacet').find('span.facetCount').click(); // default
    		}
    	}
    	else if ( core == 'gene' ){    		
    		if ( typeof hashParams.fq === 'undefined' ){    			
    			MPI2.setHashChange = 1;     			
    			caller.find('span.facetCount').click();    		
    		}
    		else {	
    			var aKV = hashParams.fq.split(':');    		
    			var sClass = aKV[0];
    			var sRel = aKV[1].replace(/"/g,'');	    		
    			$.fn.fetchFilteredDataTable($('a[rel="' + sRel + '"]'), 'geneFacet', hashParams.q);    			
    		}    		
    	}
    	else if ( core == 'mp' ){    		
    		if ( hashParams.fq == 'ontology_subset:*' ){ 
    			MPI2.setHashChange = 1;  
    			caller.find('span.facetCount').click();    			
    		}
    		else {    		
    			var fqText = hashParams.fq.replace('ontology_subset:* AND top_level_mp_term:', '').replace(/"/g,'');
				$.fn.fetchFilteredDataTable($('a[rel="' + fqText + '"]'), 'mpFacet', hashParams.q);				
    		}
    	}
    	else if ( core == 'pipeline' ){
    		if ( hashParams.fq == 'pipeline_stable_id:IMPC_001' ){
    			MPI2.setHashChange = 1;  
    			caller.find('span.facetCount').click();    			
    		}
    		else {
    			var fqText = hashParams.fq.replace('procedure_stable_id:', '');	    				
				$.fn.fetchFilteredDataTable($('a[rel="' + fqText + '"]'), 'pipelineFacet', hashParams.q);				
    		}
    	}
    	else if ( core == 'images' ){
    		if ( hashParams.fq == MPI2.searchAndFacetConfig.facetParams[facetDivId].fq ){
    			MPI2.setHashChange = 1;  
    			caller.find('span.facetCount').click();    			
    		}
    		else {
    			var fqText = hashParams.fq;    			
				$.fn.fetchFilteredDataTable($('a[class="' + fqText + '"]'), 'imagesFacet', hashParams.q);			
    		}
    	}
    	// open facet if not already    	
    	if ( ! caller.find('.facetCatList').is(':visible') ){    	
    		caller.find('div.facetCat').click();
    	}  
    }
 	
	
    /*$.fn.refactorGridSubTitle = function(){
    	
    	var str = $('div.gridSubTitle').text();
    	
    	if (/^Imits_phe/.exec(str) ){ // gene core
    		console.log('refactor');
    	}
    	else if ( /^Marker type/.exec(str) ){ // gene core
    		return 'marker_type:"' + str.replace('Marker type: ','') + '"';
    	}
    	else if ( /^Status/.exec(str) ){ // gene core
    		return 'status:"' + str.replace('Status: ','') + '"';
    	}
    	else if ( /^Top level term:/.exec(str) ){ // mp core
    		return 'ontology_subset:* AND top_level_mp_term:"' + str.replace('Top level term: ','') + ' phenotype"';
    	}
    	else if ( /hiddenId/.exec($('div.gridSubTitle').html()) ){ // Pipeline core       		
    		return 'procedure_stable_id:' + $('span.hiddenId').text();     		
    	}
    	else if ( /Procedure.+href=/.exec($('div.gridSubTitle').html()) ){ // images core    		
    		return 'expName:"' + $('div.gridSubTitle span.imgTitle').text().replace('Procedure: ', '') + '"';     		
    	}
    	else if ( /^Anatomy/.exec(str) ){ // images core 
    		return 'higherLevelMaTermName:"' + $('div.gridSubTitle span.imgTitle').text().replace('Anatomy: ', '') + '"'; 
    	}
    	else if ( /^Phenotype/.exec(str) ){ // images core 
    		return 'higherLevelMpTermName:"' + $('div.gridSubTitle span.imgTitle').text().replace('Phenotype: ', '') + '"'; 
    	}
    	else if ( /^Gene/.exec(str) ){ // images core 
    		return 'subtype:"' + $('div.gridSubTitle span.imgTitle').text().replace('Gene: ', '') + '"'; 
    	}
    	else { // for top level, so no gridSubTitle
    		return ''; 
    	}
    }
    
    $.fn.checkHashStrForSkippingReload = function(url){
    	var hashParams = $.fn.parseHashString(decodeURI(url));
    	var coreName = hashParams.coreName;    	
    	var str1, str2;
    	//console.log(hashParams.fq);
    	//console.log('fq check: '+ $.fn.refactorGridSubTitle());
    	
    	if ( ( hashParams.fq == 'undefined' ||
    		   hashParams.fq == 'ontology_subset:*'  ||
    		   hashParams.fq == 'pipeline_stable_id:IMPC_001' ||
    		   hashParams.fq == 'annotationTermId:M* OR expName:* OR symbol:* OR higherLevelMaTermName:* OR higherLevelMpTermName:*' ) &&
    		   $.fn.refactorGridSubTitle() == '' ){    	
    		return true;
    	}
    	else if ( hashParams.fq != $.fn.refactorGridSubTitle() ){    	
    		return false;
    	}
    	else {    		
    		return true;
    	}
    } */   
        
    $.fn.parseHashString = function(sHash){
    	
    	var hashParams = {};
    	var aKV = decodeURI(sHash).split("&");    	  
    	var m;
    	for( var i=0; i<aKV.length; i++){
    	
    		if ( aKV[i].indexOf('core=') == 0 ){    			
    			m = aKV[i].match(/core=(.+)/);
    			hashParams.coreName = m[1];
    		}
    		else if ( aKV[i].indexOf('gridSubTitle=') == 0 ){    			
    			m = aKV[i].match(/gridSubTitle=(.+)/); 
    			hashParams.gridSubTitle = m[1];
    		}
    		else if ( aKV[i].indexOf('q=') == 0 ){    			
    			m = aKV[i].match(/q=(.+)/);    			
    			if ( m === null ){
    				m = [];
    				m[1] = '*';    		
    			}    			
    			hashParams.q = m[1];
    		}
    		else if ( aKV[i].indexOf('fq=') == 0 ){  
    			
    			if ( aKV[i] == 'fq=' + MPI2.searchAndFacetConfig.facetParams.imagesFacet.fq
    				|| aKV[i] == 'fq=annotationTermId:M* OR expName:* OR symbol:*' 
    				|| aKV[i].match(/fq=expName.+|fq=higherLevel.+|fq=subtype.+/) 
    				|| aKV[i].match(/fq=ontology_subset:\* AND top_level_mp_term.+/)
    				|| aKV[i].match(/fq=ontology_subset:.+/)
    				|| aKV[i].match(/imits_phenotype.+/)
    				|| aKV[i].match(/marker_type.+/)
    				|| aKV[i].match(/status.+/)
    				|| aKV[i].match(/pipeline_stable_id.+/)
    				|| aKV[i].match(/procedure_stable_id.+/)
    				){
    				hashParams.fq = aKV[i].replace('fq=','');    				
    			}    			
    		}
    	}
    	 
    	return hashParams;
    }
    
    $.fn.fetchEmptyTable = function(theadStr, colNum, id){
    	
    	var table = $('<table></table>').attr({'id':id});
    	var thead = theadStr;
    	var tds = '';
    	for (var i=0; i<colNum; i++){
    		tds += "<td></td>";
    	}
    	var tbody = $('<tbody><tr>' + tds + '</tr></tbody>');	    	    	
    	table.append(thead, tbody);
    	return table;
    },	   	 
    $.fn.invokeDataTable = function(oInfos){   	   	
    	
    	var oDtable = $('table#' + oInfos.mode).dataTable({
    		"bSort" : false,
    		"bProcessing": true,
    		"bServerSide": true,	    		
    		//"sDom": "<'row-fluid'<'span6'><'span6'>>t<'row-fluid'<'span6'i><'span6'p>>",
    		"sDom": "<'row-fluid'<'#exportSpinner'><'#tableTool'>r>t<'row-fluid'<'span6'i><'span6'p>>",    		
			"sPaginationType": "bootstrap",    		
    		"fnDrawCallback": function( oSettings ) {  // when dataTable is loaded
    		
    			// ie fix, as this style in CSS is not working for IE8 
    			if ( $('table#geneGrid').size() == 1 ){
    				$('table#geneGrid th:nth-child(1)').width('45%');
    			}    			  			
    			
    			$('a.interest').click(function(){
    				var mgiId = $(this).attr('id');
    				var label = $(this).text();
    				var regBtn = $(this);
    				$.ajax({
    					url: '/toggleflagfromjs/' + mgiId,                       
    					success: function (response) {
    						function endsWith(str, suffix) {
    							return str.indexOf(suffix, str.length - suffix.length) !== -1;
                        	}
    						if(response === 'null') {
    							window.alert('Null error trying to register interest');
    						} 
    						else {                          
    							if( label == 'Register interest' ) {
    								regBtn.text('Unregister interest');
    							} 
    							else {
    								regBtn.text('Register interest');
    							}                               
    						}                         
                        },
                        error: function () {
                        	window.alert('AJAX error trying to register interest');                     
                        }
                    });
    				return false;    		    	  
    			});
    			
    			initDataTableDumpControl(oInfos);
    		},
    		"sAjaxSource": oInfos.dataTablePath,    		
    		"fnServerParams": function ( aoData ) {
    			aoData.push(	    			 
    			    {"name": "solrParams",
    				 //"value": oInfos.params// + oInfos.facetParams
    				 "value": JSON.stringify(oInfos, null, 2)
    				}	    
    			)		
    		}
    		/*"fnServerData": function ( sSource, aoData, fnCallback, oSettings) {
    			// Add some extra data to the sender     			
    			aoData.push(	    			 
        			    {"name": "solrParams",
        				 //"value": oInfos.params// + oInfos.facetParams
        				 "vMPI.dataTableLoadedalue": JSON.stringify(oInfos, null, 2)
        				}	    
        		);	
    			oSettings.jqXHR = $.ajax( {
    	               // "url": "http://ves-ebi-d0.ebi.ac.uk:8080/phenotype-archive-dev/dataTable",
    	                "data": aoData,
    	                "success": fnCallback,
    	                "success": function(json){
    	                	fnCallback(json);
    	                },
    	                "dataType": "jsonp",
    	                "cache": false
    			} );
    			    			
    			$.getJSON( sSource, aoData, function (json) { 
    				//Do whatever additional processing you want on the callback, then tell DataTables 
    				console.log('CHK');
    				fnCallback(json);
    			} );
    			
    		}  */		
    	});     	    
    	
    	/*var oTableTools = new TableTools( oDtable, {
	        "sSwfPath": "/phenotype-archive/js/vendor/DataTables-1.9.4/extras/TableTools/media/swf/copy_csv_xls_pdf.swf",
	        "aButtons": [
	        			"copy"	
	        ]
    	});*/      
    }
        
    function initDataTableDumpControl(oInfos){
    
    	$('div#saveTable').remove();
    	$('div#toolBox').remove();
    
    	var saveTool = $("<div id='saveTable'></div>").html("Export table <img src='"+baseUrl+"/img/floppy.png' />").corner("4px");    	
    	var toolBox = fetchSaveTableGui();
    	
    	$('div.dataTables_processing').siblings('div#tableTool').append(saveTool, toolBox); 
    	    	
    	$('div#saveTable').click(function(){
    			
        	if ( $('div#toolBox').is(":visible")){
    			$('div#toolBox').hide();
    		}
    		else {
    			$('div#toolBox').show();        			       			
    			
    			// browser-specific position fix
    			if ($.browser.msie  && parseInt($.browser.version, 10) === 8) {
    				$('div#toolBox').css({'top': '-30px', 'left': '65px'});
    			}
    	    	var solrCoreName = oInfos.solrCoreName;
    	    	var iActivePage = $('div.dataTables_paginate li.active a').text();
    	    	
    	    	var iRowStart = iActivePage == 1 ? 0 : iActivePage*10-10;
    	    	//console.log('start: '+ iRowStart);
    	    	var showImgView = $('div.gridTitle div#imgView').attr('rel') == 'imageView' ? true : false;  
    	    	
    	    	$('button.gridDump').unbind('click');
    	    	$('button.gridDump').click(function(){        	    		
    	    		initGridExporter($(this), {        	    							
    					externalDbId: 5,				
    					rowStart: iRowStart,
    					solrCoreName: solrCoreName,        				
    					params: oInfos.params,
    					showImgView: showImgView,
    					gridFields: MPI2.searchAndFacetConfig.facetParams[solrCoreName+'Facet'].gridFields,
    					fileName: solrCoreName + '_table_dump'	
    	    		});   
    	    	}).corner('6px'); 
    		}        		
    	});
    }
    
    function initGridExporter(thisButt, conf){
    	
		var classString = thisButt.attr('class');    		
		var fileType = thisButt.text(); 
		var dumpMode = thisButt.attr('class').indexOf('all') != -1 ? 'all' : 'page';    
		
		var url = baseUrl + '/export';	    		
		var sInputs = '';
		var aParams = [];
		for ( var k in conf ){
			aParams.push(k + "=" + conf[k]); 
			sInputs += "<input type='text' name='" + k + "' value='" + conf[k] + "'>";	    			
		}
		sInputs += "<input type='text' name='fileType' value='" + fileType.toLowerCase() + "'>";
		sInputs += "<input type='text' name='dumpMode' value='" + dumpMode + "'>";
		
		var form = "<form action='"+ url + "' method=get>" + sInputs + "</form>";
		
		
		if (dumpMode == 'all'){ 
			if ($('div.gridSubTitle').html() == '' || $('div.gridSubTitle').size() == 0 ){
				console.log('full');	
				console.log(sInputs);
			}
			else {
				
			}
			
			
			var paramStr = conf['params'] + "&start=" + conf['rowStart'] + "&rows=0";    			
			var url1;
			
			url1 = MPI2.searchAndFacetConfig.solrBaseURL_ebi + conf['solrCoreName'] + "/select?";
			paramStr += "&wt=json";			
			    		
			$.ajax({            	    
    			url: url1,
        	    data: paramStr,
        	    dataType: 'jsonp',
        	    jsonp: 'json.wrf',
        	    timeout: 5000,
        	    success: function (json){ 
					// prewarn users if dataset is big
					if ( json.response.numFound > 3000 ){							
						//console.log(json.response.numFound);
						if ( confirm("Download big dataset would take a while, would you like to proceed?") ){
							_doDataExport(url, form);
						}
					}
					else {
						_doDataExport(url, form);
					}
        	    },
        	    error: function (jqXHR, textStatus, errorThrown) {        	             	        
        	        $('div#facetBrowser').html('Error fetching data ...');
        	    }            	
			});
		}
		else {
			_doDataExport(url, form);
		}

		$('div#toolBox').hide();
    }

    // NOTE that IE8 prevents from download if over https.
    // see http://support.microsoft.com/kb/2549423
    function _doDataExport(url, form){
    	$.ajax({
			type: 'GET',
			url: url,
			cache: false,
			data: $(form).serialize(),
			beforeSend:function(){				
				$('div#exportSpinner').html(MPI2.searchAndFacetConfig.spinnerExport);			
			},
			success:function(data){    				
				$(form).appendTo('body').submit().remove();
				$('div#exportSpinner').html('');
			},
			error:function(){
				//alert("Oops, there is error during data export..");
			}
		});
    }
    
    function fetchSaveTableGui(){
    	
    	var div = $("<div id='toolBox'></div>").corner("4px");
    	div.append($("<div class='dataName'></div>").html("Current paginated entries in table"));    	
    	div.append($.fn.loadFileExporterUI({
			label: 'Export as:',
			formatSelector: {
				TSV: 'tsv_grid',
				XLS: 'xls_grid'	    			 					
			},
			'class': 'gridDump'
		}));
		div.append($("<div class='dataName'></div>").html("All entries in table")); 
		div.append($.fn.loadFileExporterUI({
			label: 'Export as:',
			formatSelector: {
				TSV: 'tsv_all',
				XLS: 'xls_all'	    			 					
			},
			'class': 'gridDump'
		}));
		return div;
    }
       
	$.fn.initDataTable = function(jqObj, customConfig){
	
		// extend dataTable with naturalSort function
		/*jQuery.fn.dataTableExt.oSort['natural-asc']  = function(a,b) {
		    return naturalSort(a,b);
		};	 
		jQuery.fn.dataTableExt.oSort['natural-desc'] = function(a,b) {
		    return naturalSort(a,b) * -1;
		};*/
				
		var params = {	
//				"sDom": "<'row-fluid'<'#foundEntries'><'span6'f>r>t<'row-fluid'<'#tableShowAllLess'><'span6'p>>",
//				 "bPaginate":true,
					"bLengthChange": false,
					"bSort": true,
					"bInfo": false,
					"bAutoWidth": false ,
	    		"iDisplayLength": 10000 , // 10 rows as default 
	    		"bRetrieve": true,
	    		/* "bDestroy": true, */
	    		"bFilter":false,
    		"sPaginationType": "bootstrap",
				};
//				console.log('calling tools datababe ini');
		var oTbl = jqObj.dataTable($.extend({}, params, customConfig)).fnSearchHighlighting();
		return oTbl;
	};		
//    $.fn.dataTableshowAllShowLess = function(oDataTbl, aDataTblCols, display){
//    	    	
//    	var rowFound = oDataTbl.fnSettings().aoData.length;
//    	$('div#foundEntries').html("Total entries found: " + rowFound).addClass('span6');    	
//    	
//		$('div.dataTables_paginate').hide();
//		    		
//		$('div.dataTables_filter input').keyup(function(){
//						
//			if ( !$(this).val() ){								
//				$('div.dataTables_paginate').hide();							
//			}
//			else {
//				// use pagination as soon as users use filter
//				$('div.dataTables_paginate').show();
//			}
//		});	
//				
//		var display = ( display == 'Show fewer entries' || !display ) ? 'Show all entries' : 'Show fewer entries';  		
//			
//		// show all/less toggle only appears when we have > 10 rows in table
//		if ( rowFound > 10 ){			
//			$('div#tableShowAllLess').html("<span>" + display + "</span>").addClass('span6')
//			$.fn.reloadDataTable(oDataTbl, aDataTblCols, display);
//		}
//    }
//    
//    $.fn.reloadDataTable = function(oDataTbl, aDataTblCols, display){
//		$('div#tableShowAllLess').click(function(){    			
//			
//			oDataTbl.fnSettings()._iDisplayLength = display == 'Show all entries' ? -1 : 10;			
//			var selector = oDataTbl.selector;			
//			
//			display = display == 'Show all entries' ? 'Show fewer entries' : 'Show all entries';
//			$(this).find('span').text(display);
//			$(selector).dataTable().fnDraw();			
//		});
//    } ; 	    
	
	function naturalSort (a, b) {
        // setup temp-scope variables for comparison evauluation
        var x = a.toString().toLowerCase() || '', y = b.toString().toLowerCase() || '',
                nC = String.fromCharCode(0),
                xN = x.replace(/([-]{0,1}[0-9.]{1,})/g, nC + '$1' + nC).split(nC),
                yN = y.replace(/([-]{0,1}[0-9.]{1,})/g, nC + '$1' + nC).split(nC),
                xD = (new Date(x)).getTime(), yD = (new Date(y)).getTime();
        // natural sorting of dates
        if ( xD && yD && xD < yD )
                return -1;
        else if ( xD && yD && xD > yD )
                return 1;
        // natural sorting through split numeric strings and default strings
        for ( var cLoc=0, numS = Math.max( xN.length, yN.length ); cLoc < numS; cLoc++ )
                if ( ( parseFloat( xN[cLoc] ) || xN[cLoc] ) < ( parseFloat( yN[cLoc] ) || yN[cLoc] ) )
                        return -1;
                else if ( ( parseFloat( xN[cLoc] ) || xN[cLoc] ) > ( parseFloat( yN[cLoc] ) || yN[cLoc] ) )
                        return 1;
        return 0;
	}
	
	// toggle showing first 10 / all rows in a table
	$.fn.toggleTableRows = function(oTable){
		var rowNum = $(oTable).find('tbody tr').length;
 		
 		var rowToggler;
 		if ( rowNum > 10 ){    			
 			$(oTable).find("tbody tr:gt(9):lt(" + rowNum+ ")").hide();
 			var txtShow10 = 'Show all '+ rowNum + ' records';
 			rowToggler = $('<span></span>').attr({'class':'rowToggler'}).text(txtShow10).toggle(
 				function(){
 					$(oTable).find("tbody tr:gt(9):lt(" + rowNum+ ")").show();
 					$(this).text('Show first 10 records');
 				},
 				function(){
 					$(oTable).find("tbody tr:gt(9):lt(" + rowNum+ ")").hide();
 					$(this).text(txtShow10);
 				}
 			);    			
 		}
 	
 		return rowToggler;
	}
	
	// get unique element from array
	$.fn.getUnique = function(list){
		var u = {}, a = [];
		for(var i = 0, l = list.length; i < l; ++i){
			if(list[i] in u){
				continue;
			}	
			a.push(list[i]);
		    u[list[i]] = 1;	   
		}	
		return a;
	}	
		
	// tooltip
	$.fn.komp2_tooltip = function(options){
		var defaults = {
			title        : '',	
			color        : 'black',
			bgcolor      : '#F4F4F4',
			mozBr        : '4px', // -moz-border-radius
		    webkitBr     : '4px', // -webkit-border-radius
		    khtmlBr      : '4px', // -khtml-border-radius
		    borderRadius : '4px'  // border-radius		    	
		}	
		var o = $.extend(defaults, options);
	
		return this.each(function(){
			var oC = $(this);
			var sTitle = oC.attr('title');
			if ( sTitle ) {
				oC.removeAttr('title');
			}
			else if ( o.title != '' ){
				sTitle = o.title;
			}
			else if ( o.url != '' ){
				// do ajax call
				$.ajax({   					
 					url: o.url,  					
 					success:function(data){ 
 						sTitle = data;
 					}
				});
			}
			 
			oC.hover(
				function(event){
					$('<div id="tooltip" />').appendTo('body').text(sTitle).css(
						{		
						'max-width' : '150px',	
						'font-size' : '10px',
						border : '1px solid gray',
						padding : '3px 5px',
						color : o.color,
						'background-color' : o.bgcolor,
						'z-index' : 999,
						position : 'absolute',
						'-moz-border-radius' : o.mozBr,
					    '-webkit-border-radius' : o.webkitBr,
					    '-khtml-border-radius' : o.khtmlBr,
					    'border-radius' : o.borderRadius
						}).komp2_updatePosition(event);
				},
				function(event){
					$('div#tooltip').remove();
				}
			);
		});
	}
	
	$.fn.komp2_updatePosition = function(event){
		return this.each(function(){
			$('div#tooltip').css({
				left : event.pageX + 10,
				top : event.pageY + 15
			})			
		});
	}
	// end of tooltip

	$.fn.upperCaseFirstLetter = function(str){
	    return str.replace(/\w\S*/g, function(txt){return txt.charAt(0).toUpperCase() + txt.substr(1).toLowerCase();});
	}	
	
})(jQuery);
	

//HIGHLIGHT FCT
$.fn.dataTableExt.oApi.fnSearchHighlighting = function(oSettings) {
    // Initialize regex cache
	if (oSettings == null){
		//console.log('oSettings is null or undefined');
		//was failing if null so added this - but presumably this is needed on the search pages still?
	}else{

    oSettings.oPreviousSearch.oSearchCaches = {};
      
    oSettings.oApi._fnCallbackReg( oSettings, 'aoRowCallback', function( nRow, aData, iDisplayIndex, iDisplayIndexFull) {
        // Initialize search string array
        var searchStrings = [];
        var oApi = this.oApi;
        var cache = oSettings.oPreviousSearch.oSearchCaches;
        // Global search string
        // If there is a global search string, add it to the search string array
        if (oSettings.oPreviousSearch.sSearch) {
            searchStrings.push(oSettings.oPreviousSearch.sSearch);
        }
        // Individual column search option object
        // If there are individual column search strings, add them to the search string array
        if ((oSettings.aoPreSearchCols) && (oSettings.aoPreSearchCols.length > 0)) {
            for (var i in oSettings.aoPreSearchCols) {
                if (oSettings.aoPreSearchCols[i].sSearch) {
                searchStrings.push(oSettings.aoPreSearchCols[i].sSearch);
                }
            }
        }
        // Create the regex built from one or more search string and cache as necessary
        if (searchStrings.length > 0) {
            var sSregex = searchStrings.join("|");
            if (!cache[sSregex]) {
                // This regex will avoid in HTML matches
                cache[sSregex] = new RegExp("("+sSregex+")(?!([^<]+)?>)", 'i');
            }
            var regex = cache[sSregex];
        }
        // Loop through the rows/fields for matches
        $('td', nRow).each( function(i) {
        	
            // Take into account that ColVis may be in use
            var j = oApi._fnVisibleToColumnIndex( oSettings,i);
            // Only try to highlight if the cell is not empty or null
            if (aData[j]) {
                // If there is a search string try to match
                if ((typeof sSregex !== 'undefined') && (sSregex)) {                	
                    this.innerHTML = aData[j].replace( regex, function(matched) {
                        return "<span class='hit'>"+matched+"</span>";
                    });
                }
                // Otherwise reset to a clean string
                else {
                    this.innerHTML = aData[j];
                }
            }
        });
        return nRow;
    }, 'row-highlight');
    return this;
    
	}
};


/* API method to get paging information */
$.fn.dataTableExt.oApi.fnPagingInfo = function ( oSettings ){		
	return {
		"iStart":         oSettings._iDisplayStart,
		"iEnd":           oSettings.fnDisplayEnd(),
		"iLength":        oSettings._iDisplayLength,
		"iTotal":         oSettings.fnRecordsTotal(),
		"iFilteredTotal": oSettings.fnRecordsDisplay(),
		"iPage":          Math.ceil( oSettings._iDisplayStart / oSettings._iDisplayLength ),
		"iTotalPages":    Math.ceil( oSettings.fnRecordsDisplay() / oSettings._iDisplayLength )
	};
}

/* Bootstrap style pagination control */
$.extend( $.fn.dataTableExt.oPagination, {
	"bootstrap": {
		"fnInit": function( oSettings, nPaging, fnDraw ) {
			var oLang = oSettings.oLanguage.oPaginate;
			var fnClickHandler = function ( e ) {
				e.preventDefault();
				if ( oSettings.oApi._fnPageChange(oSettings, e.data.action) ) {
					fnDraw( oSettings );
				}
			};

			$(nPaging).addClass('pagination pagination-small').append(
					'<ul>'+
					'<li class="prev disabled"><a href="#">&larr; '+oLang.sPrevious+'</a></li>'+
					'<li class="next disabled"><a href="#">'+oLang.sNext+' &rarr; </a></li>'+
					'</ul>'
			);
			var els = $('a', nPaging);
			$(els[0]).bind( 'click.DT', { action: "previous" }, fnClickHandler );
			$(els[1]).bind( 'click.DT', { action: "next" }, fnClickHandler );
		},

		"fnUpdate": function ( oSettings, fnDraw ) {
			var iListLength = 5;
			var oPaging = oSettings.oInstance.fnPagingInfo();
			var an = oSettings.aanFeatures.p;
			var i, j, sClass, iStart, iEnd, iHalf=Math.floor(iListLength/2);

			if ( oPaging.iTotalPages < iListLength) {
				iStart = 1;
				iEnd = oPaging.iTotalPages;
			}
			else if ( oPaging.iPage <= iHalf ) {
				iStart = 1;
				iEnd = iListLength;
			} else if ( oPaging.iPage >= (oPaging.iTotalPages-iHalf) ) {
				iStart = oPaging.iTotalPages - iListLength + 1;
				iEnd = oPaging.iTotalPages;
			} else {
				iStart = oPaging.iPage - iHalf + 1;
				iEnd = iStart + iListLength - 1;
			}

			for ( i=0, iLen=an.length ; i<iLen ; i++ ) {
				// Remove the middle elements
				$('li:gt(0)', an[i]).filter(':not(:last)').remove();

				// Add the new list items and their event handlers
				for ( j=iStart ; j<=iEnd ; j++ ) {
					sClass = (j==oPaging.iPage+1) ? 'class="active"' : '';
					$('<li '+sClass+'><a href="#">'+j+'</a></li>')
						.insertBefore( $('li:last', an[i])[0] )
						.bind('click', function (e) {
							e.preventDefault();
							oSettings._iDisplayStart = (parseInt($('a', this).text(),10)-1) * oPaging.iLength;
							fnDraw( oSettings );
						} );
				}

				// Add / remove disabled classes from the static elements
				if ( oPaging.iPage === 0 ) {
					$('li:first', an[i]).addClass('disabled');
				} else {
					$('li:first', an[i]).removeClass('disabled');
				}

				if ( oPaging.iPage === oPaging.iTotalPages-1 || oPaging.iTotalPages === 0 ) {
					$('li:last', an[i]).addClass('disabled');
				} else {
					$('li:last', an[i]).removeClass('disabled');
				}
			}
		}
	}
} );

//Set the classes that TableTools uses to something suitable for Bootstrap
/*$.extend( true, $.fn.DataTable.TableTools.classes, {
	"container": "btn-group",
	"buttons": {
		"normal": "btn",
		"disabled": "btn disabled"
	},
	"collection": {
		"container": "DTTT_dropdown dropdown-menu",
		"buttons": {
			"normal": "",
			"disabled": "disabled"
		}
	}
} );
*/
// Have the tableTools collection use a bootstrap compatible dropdown
$.extend( true, $.fn.DataTable.TableTools.DEFAULTS.oTags, {
	"collection": {
		"container": "ul",
		"button": "li",
		"liner": "a"
	}
} );

$.extend( $.fn.dataTableExt.oStdClasses, {
    "sWrapper": "dataTables_wrapper form-inline"
} );

// Sort image columns based on the content of the title tag
$.extend( $.fn.dataTableExt.oSort, {
    "alt-string-pre": function ( a ) {
        return a.match(/alt="(.*?)"/)[1].toLowerCase();
    },

    "alt-string-asc": function( a, b ) {
        return ((a < b) ? -1 : ((a > b) ? 1 : 0));
    },

    "alt-string-desc": function(a,b) {
        return ((a < b) ? 1 : ((a > b) ? -1 : 0));
    }
} ); 

