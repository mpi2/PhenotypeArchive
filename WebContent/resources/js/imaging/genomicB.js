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
		if(originalUrl.indexOf('http://www.derkholm.net:8080/das/') != -1){
			localUrl = originalUrl.replace('http://www.derkholm.net:8080/das/', root + '/derkholm/das/');
		}
		if(originalUrl.indexOf('http://www.derkholm.net:9080/das/') != -1){
			localUrl = originalUrl.replace('http://www.derkholm.net:9080/das/', root + '/derkholm/das/');
		}
		if(originalUrl.indexOf('http://beta.mousephenotype.org/mi/ws/das-ws/das/ikmcalleles/') != -1){
			localUrl = originalUrl.replace('http://beta.mousephenotype.org/mi/ws/das-ws/das/ikmcalleles/',root + '/mi/ws/das-ws/das/ikmcalleles/');
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

	// Create the link to ensembl to the mouse gene location
	function makeLink(link_start, link_end, id, link_text){
		var link=link_start+id+link_end;
		return '<a target="_blank" href="'+link+'">'+link_text+'</a>';
	}

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

	// Pass error text from JS into the correct HTML location 
	function errorsToPanels(errortext){
		$('.topic').prepend($("<div/>").html(errortext));
		$('.row-fluid').each(function(idx,row){$(row).hide();});
	}

	// jw stuff for getting it to open at mgi accession
	// Global gene_id variable must be set on the calling page 
	var mgiAccession=gene_id;
	if(mgiAccession=='' || !mgiAccession) {
		var noId='Something is wrong - there should be a gene_id for this page to work';
		errorsToPanels(noId);
	} else {
		mgiAccession=mgiAccession.replace('%3A',':');
		var locationrequest= convertToHttps('http://www.sanger.ac.uk/mouseportal/solr/select');
		jQuery(function($) {
			$.ajax({
				'url': locationrequest,
				'data': {'wt':'json', 'q':mgiAccession},
				'success': function(data) { 

					if(data.response.numFound == 0){
						errorsToPanels('No gene found for this id '+mgiAccession);
					}

					var doc = data.response.docs[0];
					var chromosome = doc.chromosome;
					var start = doc.coord_start;
					var stop = doc.coord_end;

					// If reverse strand, swap the coordinates
					if(stop<start){
						start = doc.coord_end;
						stop = doc.coord_start;
					}
					
					//get all info here from solr response

					if(doc.ensembl_gene_id) {
						var ensembl_gene_id=doc.ensembl_gene_id[0];
			       }

					var vega_gene_id='-';
					if(doc.vega_gene_id){
						vega_gene_id=doc.vega_gene_id[0];
						//http://vega.sanger.ac.uk/Mus_musculus/geneview?gene=OTTMUSG00000026431&db=core
						vega_gene_id=makeLink('http://vega.sanger.ac.uk/Mus_musculus/geneview?gene=','&db=core', vega_gene_id, vega_gene_id);
					}
					$("#vega_gene_id").html(vega_gene_id);

					//http://www.ncbi.nlm.nih.gov/sites/entrez?db=gene&cmd=Retrieve&dopt=Graphics&list_uids=22337
					var ncbi_gene_id='-';

					if(doc.ncbi_gene_id){
						ncbi_gene_id=doc.ncbi_gene_id[0];
						var ncbi_link='http://www.ncbi.nlm.nih.gov/sites/entrez?db=gene&cmd=Retrieve&dopt=Graphics&list_uids=';
						ncbi_gene_id=makeLink(ncbi_link,'',ncbi_gene_id, ncbi_gene_id);
					}
					$("#ncbi_gene_id").html(ncbi_gene_id);

					if(doc.ccds_id){
						var ccds_id=doc.ccds_id[0];
						$("#ccds_id").html(makeLink('http://www.ncbi.nlm.nih.gov/CCDS/CcdsBrowse.cgi?REQUEST=CCDS&DATA=','',ccds_id,ccds_id));
					}

					chromosome = $('#chr').html();
					start = parseInt($('#geneStart').html());
					stop = parseInt($('#geneEnd').html());

					if(!supportsSvg()){

						// This browser cannot support the interactive browser

						var dontUseIEString='<div class="alert alert-info">For a more interactive and informative gene image please use a newish browser e.g. <a href="http://www.mozilla.com/firefox/">Firefox</a> 3.6+, <a href="http://www.google.com/chrome">Google Chrome</a>, and <a href="http://www.apple.com/safari/">Safari</a> 5 or newer</div>';
						var gbrowseimage='<a href="http://gbrowse.informatics.jax.org/cgi-bin/gb2/gbrowse/mousebuild38/?start='+start+';stop='+stop+';ref='+chromosome+'"><img border="0" src="http://gbrowse.informatics.jax.org/cgi-bin/gb2/gbrowse_img/mousebuild38/?t=MGI_Genome_Features;name='+chromosome+':'+start+'..'+stop+';width=400"></a>';
						$('#svgHolder').html(dontUseIEString+gbrowseimage);
						forceWidth: jQuery('div.row-fluid').width() * 0.6575;

						// Remove the the info bar and help text about the genome 
						// browser being interactive because the IE fallback 
						// browser (gbrowse) isn't interactive
						$('#genomicBrowserInfo').html('');
					} else {
						
						// Display the interactive browser

						var b= new Browser({
							chr:        chromosome,
							viewStart:  start-1000,
							viewEnd:    stop+1000,
							noPersist: true,
							coordSystem: {
								speciesName: 'Mouse',
								taxon: 10090,
								auth: 'NCBIM',
								version: 37
							},
							chains: {
								mm8ToMm9: new Chainset(getProxyUri('http://www.derkholm.net:8080/das/mm8ToMm9/'), 'NCBIM36', 'NCBIM37', {
									speciesName: 'Mouse',
									taxon: 10090,
									auth: 'NCBIM',
									version: 36
								})
							},
							sources: [{
								name: 'Genome',
								uri: getProxyUri( 'http://www.derkholm.net:9080/das/mm9comp/'),
								desc: 'Mouse reference genome build NCBIm37',
								tier_type: 'sequence',
								provides_entrypoints: true
								},
								{
									name: 'Genes',
									desc: 'Gene structures from Ensembl 58',
									uri: getProxyUri('http://www.derkholm.net:8080/das/mmu_58_37k/'),
									collapseSuperGroups: true,
									provides_karyotype: true,
									provides_search: true
								},
								{
									name: 'ikmc alleles',     
									desc: 'ikmc alleles',
									uri: getProxyUri('http://beta.mousephenotype.org/mi/ws/das-ws/das/ikmcalleles/'),collapseSuperGroups: true   
								},
								{
									name: 'CpG',
									desc: 'CpG observed/expected ratio',
									uri: 'http://www.derkholm.net:9080/das/mm9comp/',
									stylesheet_uri: 'http://www.derkholm.net/dalliance-test/stylesheets/cpg.xml'
								}
							],
							searchEndpoint: new DASSource(getProxyUri('http://www.derkholm.net:8080/das/mmu_58_37k/')),
							karyoEndpoint: new DASSource(getProxyUri('http://www.derkholm.net:8080/das/mmu_58_37k/')),
							browserLinks: {
								Ensembl: 'http://www.ensembl.org/Mus_musculus/Location/View?r=${chr}:${start}-${end}',
								UCSC: 'http://genome.ucsc.edu/cgi-bin/hgTracks?db=mm9&position=chr${chr}:${start}-${end}'
							},
							forceWidth: jQuery('div.row-fluid').width() * 0.6575
						}); //end var b= new Browser({
					} //end if(!supportsSvg()){...}else{
				},
				'dataType': 'jsonp',
				'jsonp': 'json.wrf'
			}); // end $.ajax({
		}); // end jQuery(function($) {
	}
});
