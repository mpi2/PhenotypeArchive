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
 * genomicB.js
 * Contains utility methods for the phenotype archive gene details page
 * 
 */
// Convert a URL to HTTPS if necessary
function convertToHttps(url){
	var protocol = location.protocol;
	if(protocol=='https:'){
		url=url.replace('http:', 'https:');
	}
	return url;
}

// Transform original url into a url that we proxy through to avoid
// CORS errors and get round ajax permission restrictions
function getProxyUri(originalUrl) {
	
	var root = location.protocol + '//' + location.host;
	var localUrl = originalUrl;

	//if on localhost just return the original url
	if(root == 'http://localhost:8080' || root == 'https://localhost:8080') {
		return originalUrl;
	}else{
		if(originalUrl.indexOf('http://www.ebi.ac.uk/mi/ws/dazzle-ws/das/') != -1){
			localUrl = originalUrl.replace('http://www.ebi.ac.uk/mi/ws/dazzle-ws/das/', root + '/mi/ws/dazzle-ws/das/');
		}
		if(originalUrl.indexOf('http://beta.mousephenotype.org/mi/ws/das-ws/das/ikmcallelesm38/') != -1){
			localUrl = originalUrl.replace('http://beta.mousephenotype.org/mi/ws/das-ws/das/ikmcallelesm38/',root + '/mi/ws/das-ws/das/ikmcallelesm38/');
		}
		if(originalUrl.indexOf("http://gbrowse.informatics.jax.org/cgi-bin/gbrowse_img/thumbs_current") != -1){
			localUrl = originalUrl.replace('http://gbrowse.informatics.jax.org/cgi-bin/gbrowse_img/thumbs_current',root + '/jax/cgi-bin/gbrowse_img/thumbs_current/');
		}
	}
	localUrl = convertToHttps(localUrl);
	return localUrl;
}

// Document onload functions 
jQuery(document).ready(function() {
	// See here for this solution to detecting IE. 
	// http://stackoverflow.com/questions/4169160/javascript-ie-detection-why-not-use-simple-conditional-comments
	function supportsSvg(){
		var isIE = /*@cc_on!@*/false;
		if(isIE){
			return false;
		}else{
			return true;
		}
	}

	var chromosome = $('#chr').html();
	var start = parseInt($('#geneStart').html());
	var stop = parseInt($('#geneEnd').html());
	if(!supportsSvg()){

		// This browser cannot support the interactive browser
		// Remove the the info bar and help text about the genome 
		// browser being interactive because the IE fallback 
		// browser (gbrowse) isn't interactive

		var dontUseIEString='<div class="alert alert-info">For a more interactive and informative gene image please use a newish browser e.g. <a href="http://www.mozilla.com/firefox/">Firefox</a> 3.6+, <a href="http://www.google.com/chrome">Google Chrome</a>, and <a href="http://www.apple.com/safari/">Safari</a> 5 or newer</div>';
		var gbrowseimage='<a href="http://gbrowse.informatics.jax.org/cgi-bin/gb2/gbrowse/mousebuild38/?start='+start+';stop='+stop+';ref='+chromosome+'"><img border="0" src="http://gbrowse.informatics.jax.org/cgi-bin/gb2/gbrowse_img/mousebuild38/?t=MGI_Genome_Features;name='+chromosome+':'+start+'..'+stop+';width=400"></a>';
		$('#svgHolder').html(dontUseIEString+gbrowseimage);
		forceWidth: jQuery('div.row-fluid').width() * 0.98;
		$('#genomicBrowserInfo').html('');

	} else {
					
		// Display the interactive browser

		var b =	new Browser({
			chr:        chromosome,
			viewStart:  start-1000,
			viewEnd:    stop+1000,
			noPersist: true,
			zoomMax: 280,
			coordSystem: {
				speciesName: 'Mouse',
				taxon: 10090,
				auth: 'NCBIM',
				version: 38
			},
			sources: [
			{
				name: 'Genome',
				uri: getProxyUri( 'http://www.ebi.ac.uk/mi/ws/dazzle-ws/das/mmu_68_38k/'),
				desc: 'Mouse reference genome build NCBIm38',
				tier_type: 'sequence',
				provides_entrypoints: true
				},
				{
					name: 'Genes',
					desc: 'Gene structures from Ensembl 58',
					uri: getProxyUri('http://www.ebi.ac.uk/mi/ws/dazzle-ws/das/mmu_68_38k/'),
					collapseSuperGroups: true,
					provides_karyotype: true,
					provides_search: true
				},
				{
					name: 'ikmc alleles',     
					desc: 'ikmc alleles',
					uri: getProxyUri('http://beta.mousephenotype.org/mi/ws/das-ws/das/ikmcallelesm38/'),collapseSuperGroups: true   
				}
			],
			searchEndpoint: new DASSource(getProxyUri('http://www.ebi.ac.uk/mi/ws/dazzle-ws/das/mmu_68_38k/')),
			karyoEndpoint: new DASSource(getProxyUri('http://www.ebi.ac.uk/mi/ws/dazzle-ws/das/mmu_68_38k/')),
			browserLinks: {
				Ensembl: 'http://www.ensembl.org/Mus_musculus/Location/View?r=${chr}:${start}-${end}',
				UCSC: 'http://genome.ucsc.edu/cgi-bin/hgTracks?db=mm10&position=chr${chr}:${start}-${end}'
			},
			forceWidth: jQuery('div.row-fluid').width() * 0.98,
			 disableDefaultFeaturePopup: true

		}); //new Browser({

		//override the default popups here from feature-popup.js and having set the browser init option to disableDefaultFeaturePopup: true
		b.addFeatureListener(function(ev, feature, group) {

			//  var log = document.getElementById('clickLog');
			 // var msg = makeElement('p', miniJSONify(hit));
			  //console.log("click handler works!"+ miniJSONify(feature));

			    if (!feature) feature = {};
			   if (!group) group = {};
			    group = {};
			    b.removeAllPopups();

			    var table = makeElement('table', null, {className: 'table table-striped table-condensed'});
			    table.style.width = '100%';
			    table.style.margin = '0px';

			    var name = pick(group.type, feature.type);
			  //  var fid = pick(group.label, feature.label, group.id, feature.id);
			    //mpi2 specific
			    var fid = pick(feature.label,group.label, group.id, feature.id);
			    
			    if (fid && fid.indexOf('__dazzle') != 0) {
			        name = name + ': ' + fid;
			    }

			    var idx = 0;
			    if (feature.method) {
			        var row = makeElement('tr', [
			            makeElement('th', 'Method'),
			            makeElement('td', feature.method)
			        ]);
			        row.style.backgroundColor = b.tierBackgroundColors[idx % b.tierBackgroundColors.length];
			        table.appendChild(row);
			        ++idx;
			    }
			    {
			        var loc;
			        if (group.segment) {
			            loc = group;
			        } else {
			            loc = feature;
			        }
			        var row = makeElement('tr', [
			            makeElement('th', 'Location'),
			            makeElement('td', loc.segment + ':' + loc.min + '-' + loc.max)
			        ]);
			        row.style.backgroundColor = b.tierBackgroundColors[idx % b.tierBackgroundColors.length];
			        table.appendChild(row);
			        ++idx;
			    }
			    if (feature.score !== undefined && feature.score !== null && feature.score != '-') {
			        var row = makeElement('tr', [
			            makeElement('th', 'Score'),
			            makeElement('td', '' + feature.score)
			        ]);
			        row.style.backgroundColor = b.tierBackgroundColors[idx % b.tierBackgroundColors.length];
			        table.appendChild(row);
			        ++idx;
			    }
			    {
			        var links = maybeConcat(group.links, feature.links);
			        if (links && links.length > 0) {
			            var row = makeElement('tr', [
			                makeElement('th', 'Links'),
			                makeElement('td', links.map(function(l) {
			                	
			                	 if(l.desc=='Cassette Image'){
			                         // console.debug(l.desc);
			                		 //mpi2 speicifc
			                      return makeElement('div',makeElement('a', makeElement('img', l.desc, {width:320, src: l.uri}), {href:l.uri, target: '_new'}));//'<img src="http://www.knockoutmouse.org/targ_rep/alleles/37256/allele-image" alt="some_text"/>');
			                      }
			                    return makeElement('div', makeElement('a', l.desc, {href: l.uri, target: '_new'}));
			                }))
			            ]);
			            row.style.backgroundColor = b.tierBackgroundColors[idx % b.tierBackgroundColors.length];
			            table.appendChild(row);
			            ++idx;
			        }
			    }
			    {
			        var notes = maybeConcat(group.notes, feature.notes);
			        for (var ni = 0; ni < notes.length; ++ni) {
			            var k = 'Note';
			            var v = notes[ni];
			            var m = v.match(TAGVAL_NOTE_RE);
			            if (m) {
			                k = m[1];
			                v = m[2];
			            }

			            var row = makeElement('tr', [
			                makeElement('th', k),
			                makeElement('td', v)
			            ]);
			            row.style.backgroundColor = b.tierBackgroundColors[idx % b.tierBackgroundColors.length];
			            table.appendChild(row);
			            ++idx;
			        }
			    }

			    b.popit(ev, name, table, {width: 400});
			});
		
		
	} //if(!supportsSvg()){...}else{

}); //jQuery(function($) { 

