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
	
	$.fn.composeFacetFilterControl = function(oChkbox, q){		
		do_ParentFilterDisplay(oChkbox, q);		
	}
	
	function do_ParentFilterDisplay(oChkbox, q) {
		var labels = oChkbox.attr('rel').split("|");
		var facet = labels[0];
		var field = labels[1];
		var value = labels[2];
		var thisLi = $('ul#facetFilter li.' + facet);
						
		if ( oChkbox.is(':checked') ){		
			
			// add filter
			$.fn.addFacetFilter(oChkbox, q);		
						
			// compose solr query		
			compose_solr_facetFilter(facet, q, oChkbox);
		}
		else {			
			// uncheck checkbox with matching value			
			thisLi.find('ul li').each(function(){
				if ( $(this).find('a').attr('rel') == oChkbox.attr('rel') ){			
					$(this).remove();					
					oChkbox.parent().find('td.highlight').removeClass('highlight');
				}
			});			
			
			// hide facet filter container if no filter chosen for that facet 
			if ( thisLi.find('li').size() == 0 ){
				thisLi.find('ul').remove();
				thisLi.hide();	
								
				// load all result of facet via change hash# 
				$.fn.setHashUrl(q, facet);									
			}	
			else {
				// update facet filter
				compose_solr_facetFilter(facet, q, oChkbox);
			}
			
			// say no filter if none is chosen for all facets 
			$.fn.checkFilters();
		}		
	}
	
	function compose_solr_facetFilter(facet, q, oChkbox) {				
				
		var fqFieldVals = {};
		$('ul#facetFilter li.' + facet + ' li a').each(function(){	
			
			var linkTxt = $(this).text().replace(' ', '');
			var aVals = $(this).attr('rel').split("|");		
			var fqField = aVals[1];
						
			// fq filter used for solr query
			var fqFieldOri = fqField;
			fqField = fqField.indexOf('imits_phenotype') != -1 ? 'imits_phenotype' : fqField;
			if ( typeof fqFieldVals[fqField] === 'undefined' ){
				fqFieldVals[fqField] = [];					
			}				
			
			var val;
			if ( fqField == 'top_level_mp_term' || fqField == 'higherLevelMpTermName' ) {
				val = aVals[2] + ' phenotype';				
				fqFieldVals[fqField].push(fqFieldOri + ':"' + val + '"');
			}
			else if ( fqField == 'procedure_stable_id' ){
				var names = aVals[2].split('___');
				psid = names[1];				
				val = names[0];
				fqFieldVals[fqField].push(fqFieldOri + ':"' + psid + '"');
			}
			/*else if ( fqField == 'phenotyping_center' ){
				val = aVals[2];				
				fqFieldVals[fqField].push('(' + fqFieldOri + ':"' + val + '" AND production_center:*)');
			}
			else if ( fqField == 'production_center' ){
				val = aVals[2];				
				fqFieldVals[fqField].push('(' + fqFieldOri + ':"' + val + '")');				
			}*/
			else {
				val = aVals[2];
				fqFieldVals[fqField].push(fqFieldOri + ':"' + val + '"');
			}				
			
		});	
		
		var fqStr = $.fn.compose_AndOrStr(fqFieldVals);
		
		var facetDivId = facet+'Facet';

    	if ( facetDivId == 'maFacet' ||  facetDivId == 'mpFacet' ){
    		fqStr = MPI2.searchAndFacetConfig.facetParams[facetDivId].subset + ' AND ' + fqStr;					
		}
    	
    	// update hash tag so that we know there is hash change, which then triggers loadDataTable
    	if (q == '*:*'){
    		window.location.hash = 'q=' + q + '&fq=' + fqStr + '&core=' + facet;
    	}
    	else {
    		window.location.hash = 'fq=' + fqStr + '&core=' + facet;
    	}
	}
	
	$.fn.compose_AndOrStr = function(json){
		var andOrStrList = [];
		
		for ( var i in json ){			
			andOrStrList.push('(' + json[i].join(' OR ') + ')' );			
		}		
		return andOrStrList.join(' AND ');		
	}
	
	$.fn.removeFacetFilter = function(facet) {
		$('ul#facetFilter li.' + facet + ' ul').remove();
		$('ul#facetFilter li.' + facet).hide();
		
		// uncheck all checkboxes of this facet
		$('div#'+ facet + 'Facet input').attr('checked', false);
		
		$.fn.checkFilters();
	}
	
	$.fn.addFacetFilter = function(oChkbox, q){
	
		var labels = oChkbox.attr('rel').split('|');
		// add filter
		var facet = labels[0];
		var field = labels[1];
		var value = labels[2];
		var thisLi = $('ul#facetFilter li.' + facet);
		
		var display = MPI2.searchAndFacetConfig.facetFilterLabel[field];
		if (field == 'imits_phenotype_started' && value == 1 ){
			value = 'started';
		}
		
		var filterTxt = ( facet == 'gene' || facet == 'images' ) ? display + ' : ' + value : value;					
		if (facet == 'pipeline'){
			var names = filterTxt.split('___');
			filterTxt = names[0];
		}
	
		var a = $('<a></a>').attr({'rel':oChkbox.attr('rel')}).text(filterTxt);
		var del = $('<img>').attr('src', baseUrl + '/img/scissors-15x15.png');
		
		var filter = $('<li></li>').append(del, a);			
		
		add_uncheck_js(a, del, filter, oChkbox, q);
				
		if ( thisLi.find('ul').size() == 0 ){			
			var ul = $('<ul></ul>').html(filter);
			thisLi.append(ul);								
		}
		else if ( thisLi.find('ul').html() == '' ){
			thisLi.find('ul').append(filter)
		}
		else {
			// double check this filter not already exists: eg, check same filter as the exclusive subFacet did
			thisLi.find('ul li a').each(function(){
				if ($(this).text() != filterTxt){
					thisLi.find('ul').append(filter);
				}
			});								
		}
					
		thisLi.show();
		$('ul#facetFilter li.none').hide();			
	} 
	
	$.fn.checkFilters = function(){
		if ( $('ul#facetFilter ul').size() == 0 ){
			$('ul#facetFilter li.none').show();
		}
	}
		
	function add_uncheck_js(oLia, del, filter, oChkbox,q) {
		del.click(function(){	
			oChkbox.attr("checked", false);			
			oChkbox.parent().find('td.highlight').removeClass('highlight');
			filter.remove();
			$.fn.composeFacetFilterControl(oChkbox, q);
		});
	}
	
	$.fn.qTip = function(oConf){
		// pageName: gene | mp | ma
		$('div.documentation a').each(function(){
						
			var key = $(this).attr('class');
			$(this).attr('href', MDOC[oConf.pageName][key+'DocUrl']);
			
			$(this).qtip({
			 	content: MDOC[oConf.pageName][key],
			   	style: { 
			   		delay: 1,
			    	width: 250,
			      	padding: 8,
			      	background: '#EEE9E9',
			      	color: 'black',
			      	textAlign: oConf.textAlign,
			      	border: {
			        	width: 1,
			         	radius: 5,
			         	color: '#EEE9E9'
			   	  	},
			    	tip: oConf.tip,//'bottomMiddle', //'bottomLeft',
			    	name: 'dark' // Inherit the rest of the attributes from the preset dark style
			   	},				 
				show: {		            
		               event: 'mouseover',
					   delay: 0
		        },
		        hide: {		        		
			           event: 'mouseout' 
		        },
				position: {
			    	corner: {
			        	target: 'bottomLeft',
			        	tooltip: 'topRight'
			        },
			    	adjust: {
			        	x: oConf.posX,
			        	y: oConf.posY
			        }
			    }	   
			});			
		});
	}
	
	$.fn.setHashUrl = function(q, core){		
		var hashParams = {};
		//hashParams.q = q;
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
    	else if (core == 'ma'){
    		$('div#maFacet div.facetCatList').show();		
    		$('div#maFacet div.facetCat').addClass('facetCatUp'); 
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
    				|| aKV[i].match(/fq=\(?marker_type:* -marker_type:"heritable phenotypic marker"\)?/) 
    				|| aKV[i].match(/fq=\(?annotationTermId:M* OR expName:* OR symbol:*\)?/) 
    				|| aKV[i].match(/fq=\(?expName.+\)?|fq=\(?higherLevel.+\)?|fq=\(?subtype.+\)?/) 
    				|| aKV[i].match(/fq=ontology_subset:\* AND \(?top_level_mp_term.+\)?/)
    				|| aKV[i].match(/fq=ontology_subset:IMPC_Terms AND \(?selected_top_level_ma_term.+\)?/)
    				|| aKV[i].match(/fq=\({0,}production_center:.+\)?/)
    				|| aKV[i].match(/fq=\({0,}phenotyping_center:.+\)?/)
    				|| aKV[i].match(/fq=\(?ontology_subset:.+/)
    				|| aKV[i].match(/\(?imits_phenotype.+\)?/)
    				|| aKV[i].match(/\(?marker_type.+\)?/)
    				|| aKV[i].match(/\(?status.+\)?/)
    				|| aKV[i].match(/\(?pipeline_stable_id.+\)?/)
    				|| aKV[i].match(/\(?procedure_stable_id.+\)?/)
    				){
    				hashParams.fq = aKV[i].replace('fq=','');    				
    			}    			
    		}
    		/*else if ( aKV[i].indexOf('ftOpen') == 0 ){  
    			hashParams.ftOpen = true;
    		}*/
    		
    	}
    	 
    	return hashParams;
    }
    
    $.fn.fetchEmptyTable = function(theadStr, colNum, id, pageReload){
    	
    	var table = $('<table></table>').attr({'id':id});
    	var thead = theadStr;
    	var tds = '';
    	for (var i=0; i<colNum; i++){
    		tds += "<td></td>";
    	}
    	var tbody = $('<tbody><tr>' + tds + '</tr></tbody>');	    	    	
    	table.append(thead, tbody);
    	return table;
    }  
       
    $.fn.parseUrlForFacetCheckboxAndTermHighlight = function(q, fqStr, facet, pageReload){
    	var self = this;
    	    
    	fqStr = fqStr.replace(MPI2.searchAndFacetConfig.facetParams[facet].filterParams.fq, '');
    	
    	// unhightlight all from this facet
		$('table#'+ facet +'Tbl td').removeClass('highlight');    
	   
		var pat = '(\\b\\w*\\b):"([a-zA-Z0-9_\/ ]*)"';		
		var regex = new RegExp(pat, "gi");		    	
		var result;
		var objList = [];
    	while ( result = regex.exec(fqStr) ) {		    		
    		var wantStr = result[1]+ '|' + result[2];
    		    	
    		if ( facet == 'pipelineFacet' ){
    			wantStr = result[2];
    		}	
    		else if ( facet == 'imagesFacet' || facet == 'mpFacet' ){
    			wantStr = wantStr.replace(' phenotype', '');
    		}
    		
    		var obj = $('table#'+ facet + 'Tbl tr').find('input[rel*="'+wantStr+'"]');
    		
    		// tick checkbox if not already
    		if ( obj.is(':checked') ){
    	   		// do nothing for now		
    		}
    		else {    		
    			obj.attr('checked', true);
    		}
    		objList.push(obj);    		
    		
    		// highlight this facet term
    		var aObjs = obj.parent().siblings('td');    		
			$(aObjs[0]).addClass('highlight');	    		
			
    		// also add to unordered list
    		$.fn.addFacetFilter(obj, q);
    	} 

		// Work out which subfacet needs to be open:
		// This is for gene and images cores only where there are collapsed subfacets by default.
		// Ie, if a particular subfacet was open, we need to reopen it now when page reloads
    	// But ignore this bit if we are dealing with hash change in url
    	if ( typeof pageReload != 'undefined' ){
    		_setFacetToOpen(objList, facet);
    	}
    }
    
    function _setFacetToOpen(objList, facet){
    	
    	if ( (facet == 'imagesFacet' || facet == 'geneFacet') && objList.length != 0){
	    	// first change arrow image to collapse and make all images subfacets hidden
			$('table#' + facet + 'Tbl').find('tr.subFacet').addClass('trHidden');
			$('table#' + facet + 'Tbl').find('tr.facetSubCat td').removeClass('unCollapse');   
    	}
    	
    	var subFacetName;
    	if ( objList.length == 0 ){
    		// open gene phenotyping status subfacet by default
    		subFacetName = 'phenotyping'; 
    		_arrowSwitch(subFacetName); 
    	}
    	else {
	    	// only for gene and images facets
	    	for (var i=0; i<objList.length; i++){
	    		subFacetName = objList[i].attr('class');
	    		_arrowSwitch(subFacetName);	    
	    	}  
    	}
    }
  
    function _arrowSwitch(subFacetName){
    	$('tr.facetSubCat').each(function(){    	
			if ( $(this).hasClass(subFacetName) ){
				$(this).find('td').addClass('unCollapse'); 
			}
		}); 
    	$('tr.' + subFacetName).show()
    }   
    
    function _prepare_resultMsg_and_dTableSkeleton(q, fqStr, facetDivId){
    
    	var oVal = MPI2.searchAndFacetConfig.facetParams[facetDivId];
    	var dTable = $.fn.fetchEmptyTable(oVal.tableHeader, 
                oVal.tableCols, oVal.gridName);
    	
    	var imgViewSwitcher = '';
    	if ( facetDivId == 'imagesFacet' ){
    		imgViewSwitcher = _load_imgViewSwitcher(dTable, oVal);    		
    		$("div#resultMsg").prepend(imgViewSwitcher);
    	}    
    	var searchKw = " AND search keyword: ";		
		searchKw += q == '*:*' ? '""' : '"' + q + '"';			
			
		var userFqStr = $.fn.relabelFilterForUsers(fqStr);
		if ( facetDivId == 'pipelineFacet' ){
			userFqStr = convert_proc_id_2_name(userFqStr);
		}
		
		// some quicky/lazy cosmetic change
		if (userFqStr == 'gene_subtype:* -gene_subtype:"heritable phenotypic marker"' || userFqStr == '*:*' ){
			userFqStr = '';
			searchKw = searchKw.replace(' AND ', '');
		}		
		
    	var dataCount = "<span id='resultCount'><span id='annotCount'></span><a></a></span>";    	
    	var resultMsg = $("<div id='resultMsg'></div>").append(imgViewSwitcher, dataCount, ' for ' + userFqStr + decodeURI(searchKw));
    	    	
    	$('div#mpi2-search').html('');
    	$('div#mpi2-search').append(resultMsg, dTable);   
    }
    
    function convert_proc_id_2_name(userFqStr){
    	
    	var pat = '(\\b\\w*\\b):"([a-zA-Z0-9_]*)"';    
		var regex = new RegExp(pat, "gi");		    	
		var result;
		var fqFieldVals = {};
		
    	while ( result = regex.exec(userFqStr) ) {
    		var field = result[1];
    		var id = result[2];
    		$('table#pipelineFacetTbl td a').each(function(){
    			if ( $(this).attr('rel') == id ){
    				var name = $(this).parent().siblings('td[class^=procedure]').text();
    				if ( typeof fqFieldVals[field] === 'undefined' ){
    					fqFieldVals[field] = [];										
    				}
    				fqFieldVals[field].push(field + ':"' + name + '"');
    			}
    		});
    	}	
    	return $.fn.compose_AndOrStr(fqFieldVals);		
    }
    
    $.fn.relabelFilterForUsers = function(fqStr){
    	
    	var oldStr = fqStr;
    	for ( var i in MPI2.searchAndFacetConfig.facetFilterLabel ){    
    		var regex = new RegExp('\\b'+i+'\\b', "gi");	
    		fqStr = fqStr.replace(regex, MPI2.searchAndFacetConfig.facetFilterLabel[i]);    		
    	}
    
    	fqStr = fqStr.replace(/\"1\"/g, '"Started"');
    	
    	return fqStr;    	
    }
    
    $.fn.loadDataTable = function(q, fqStr, facetDivId){
    		    	
    	_prepare_resultMsg_and_dTableSkeleton(q, fqStr, facetDivId);
    	 	  	
    	var oVal = MPI2.searchAndFacetConfig.facetParams[facetDivId];
    	var oInfos = {};
		oInfos.solrCoreName = oVal.solrCoreName;
		oInfos.mode = oVal.gridName;		
		oInfos.dataTablePath = MPI2.searchAndFacetConfig.dataTablePath;		
		var oParams = MPI2.searchAndFacetConfig.facetParams[facetDivId].srchParams;		
		oParams.fq = encodeURI(fqStr); 
		oParams.q = q;
		oParams.rows = 10;
		
		/*currHashParams = $.extend({}, currHashParams, MPI2.searchAndFacetConfig.facetParams[facetDivId].srchParams, 
   	    		MPI2.searchAndFacetConfig.facetParams[facetDivId].filterParams);
		*/
		var userFqStr;
		if ( facetDivId == 'imagesFacet' ) {
			oInfos.showImgView = true;	
		}
		
		oInfos.params = $.fn.stringifyJsonAsUrlParams(oParams);
		oInfos.q = q;
		oInfos.fq = fqStr;
				
		$.fn.updateBreadCrumb(oVal.solrCoreName);	
		$.fn.openFacet(oVal.solrCoreName);
		$.fn.invokeDataTable(oInfos);
    }   
    
    function _load_imgViewSwitcher(oDTable){		 	   		
    	// toggles two types of views for images: annotation view, image view	 	   		
   		var viewLabel, imgViewSwitcherDisplay, viewMode;
   		 	   		
   		oConf = MPI2.searchAndFacetConfig.facetParams.imagesFacet;
   		
   		if ( oConf.showImgView ){
   			oDTable.find('th:nth-child(2)').text("Image");
   		}
   		else {
   			oDTable.find('th:nth-child(2)').text("Example Images");
   		}   		
   		//viewLabel = oConf.viewLabel; //'Image View: lists annotations to an image';
   		//imgViewSwitcherDisplay = oConf.imgViewSwitcherDisplay; //'Show Annotation View'; 
   		//viewMode = oConf.viewMode; //'imageView';   		
   				   		
   		var imgViewSwitcher = $('<div></div>').attr({'id':'imgView','rel':oConf.viewMode}).html(
   			"<span id='imgViewSubTitle'>" + oConf.viewLabel + "</span>" +
   			"<span id='imgViewSwitcher'>" + oConf.imgViewSwitcherDisplay + "</span>");   		 		
   		   		
   		return imgViewSwitcher;
	} 	
    
    $.fn.setDefaultImgSwitcherConf = function(){
    	var oConf = MPI2.searchAndFacetConfig.facetParams.imagesFacet;
    	oConf.imgViewSwitcherDisplay = 'Show Annotation View';
		oConf.viewLabel = 'Image View: lists annotations to an image';
		oConf.viewMode = 'imageView';
		oConf.showImgView = true;		 
    }
    
    $.fn.invokeDataTable = function(oInfos){   	   	
    	//console.log(oInfos);
    	var oDtable = $('table#' + oInfos.mode).dataTable({
    		"bSort" : false,
    		"bProcessing": true,
    		"bServerSide": true,	    		
    		//"sDom": "<'row-fluid'<'span6'><'span6'>>t<'row-fluid'<'span6'i><'span6'p>>",
    		"sDom": "<'row-fluid'<'#exportSpinner'><'#tableTool'>r>t<'row-fluid'<'span6'i><'span6'p>>",    		
			"sPaginationType": "bootstrap",    		
    		"fnDrawCallback": function( oSettings ) {  // when dataTable is loaded
    		
    			// bring in some control logic for image view switcher when dataTable is loaded
    			if (oInfos.solrCoreName == 'images'){    				
    				$('span#imgViewSwitcher').click(function(){	
    		   			
    		   			var oConf = MPI2.searchAndFacetConfig.facetParams.imagesFacet;  
    		   			
    		   			if ( oConf.imgViewSwitcherDisplay == 'Show Annotation View'){
    		   			
    		   				oConf.imgViewSwitcherDisplay = 'Show Image View'; 
    		   				oConf.viewLabel = 'Annotation View: groups images by annotation';    		   				
    		   				oConf.viewMode = 'annotView';    		   				
    		   				oConf.showImgView = false;
    		   				oInfos.showImgView = false; 
    		   			}
    		   			else {
    		   				$.fn.setDefaultImgSwitcherConf(); 
    		   				oInfos.showImgView = true;   		   				
    		   			}  
    		   			
    		   			_prepare_resultMsg_and_dTableSkeleton(oInfos.q, oInfos.fq, oInfos.solrCoreName+'Facet');
    		   			$.fn.invokeDataTable(oInfos);    		   					
    		   		});   
    			}    
    			
    			displayDataTypeResultCount(oInfos, this.fnSettings().fnRecordsTotal());
    		
    			
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
   
    //var dataCount = "<span id='resultCount'><span id='annotCount'></span><a></a></span>";      	
	//var resultMsg = $("<div id='resultMsg'></div>").append(d
    function displayDataTypeResultCount(oInfos, count) {	
    	
		var dataType = MPI2.searchAndFacetConfig.facetParams[oInfos.solrCoreName+'Facet'].type;
		dataType = count > 1 ? dataType : dataType.replace(/s$/, '');
		var txt = count + ' ' + dataType;
		if ( oInfos.solrCoreName == 'images' ){
						
			var imgUrl = baseUrl + "/imagesb?" + oInfos.params;
			
			if ( MPI2.searchAndFacetConfig.facetParams.imagesFacet.showImgView ){
	   			// record img count, as in annotation view, the count is number of annotations and not images
				MPI2.searchAndFacetConfig.lastImgCount = count;
				$('span#resultCount span#annotCount').text('');
				$('span#resultCount a').attr({'href':imgUrl}).text(txt);
			}			
			else {							
				$('span#annotCount').text(count + ' annotations / ');
				txt = MPI2.searchAndFacetConfig.lastImgCount + ' ' + dataType;			
				$('span#resultCount a').attr({'href':imgUrl}).text(txt);
			}			
			
			if ( count == 0 ){		
				$('span#resultCount a').removeAttr('href').css({'text-decoration':'none','cursor':'normal','color':'gray'});
				$('span#annotCount').text(  oInfos.showImgView ? '' : '0 annotation / ');
			}
		}
		else {
			$('span#resultCount a').css({'text-decoration':'none','color':'gray'}).text(txt);			
		}	
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
    	    	var showImgView = $('div.resultMsg div#imgView').attr('rel') == 'imageView' ? true : false;  
    	    	
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
	
	$.fn.inArray = function(item, list) {
	    var length = list.length;
	    for(var i=0; i<length; i++) {
	        if(list[i] == item) {	        	
	        	return true;
	        }
	    }
	 
	    return false;
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

