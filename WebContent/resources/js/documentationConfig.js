
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
 * searchAndFacetConfig: definition of variables for the search and facet 
 * see searchAndFacet directory
 * 
 * Author: Chao-Kung Chen
 * 
 */

if(typeof(window.MDOC) === 'undefined') {
    window.MDOC = {};
}

MDOC.search = {
		'facetPanel'         : '<div class="briefDocCap">Browse IMPC data with facets</div>'
							+ '<ul><li>Click on a facet/subfacet to open or hide it.</li>'
							+ '    <li>Ways to display facet result:'
							+ '        <ul><li>Click on the <b>facet counts</b> on the right.</li>'
							+ '            <li>Click on <b>checkbox</b> on the left.</li>'
							+'         </ul></li>'
							+'     <li>Click on the <b>info button</b> for detailed description.</li>'
							+ '</ul>', 
			
		'facetPanelDocUrl'   : baseUrl + '/documentation/search-help.html',	
};
MDOC.gene = {		
		'generalPanel'         : '<p>Details about the gene including: Gene name, accession IDs, location, links and a genome browser.</p><p>Click the help icon for more detail.</p>',
		'generalPanelDocUrl'   : baseUrl + '/documentation/gene-help.html#details',
		'preQcPanel'           : '<p>Preliminary analysis of the IMPC data displayed in a heatmap.</p><p>Click the help icon for more detail.</p>',
		'preQcPanelDocUrl'     : baseUrl + '/documentation/gene-help.html#pre-qc',
		'mpPanel'              : '<p>Mammalian Phenotype (MP) associations made to this gene.</p><p>Click the help icon for more detail.</p>',
		'mpPanelDocUrl'        : baseUrl + '/documentation/gene-help.html#phenotypes',
		'imagePanel'           : '<p>Images associated to this gene.  Note that the phenotype associations made in some of the image descriptions have not been verified for accuracy.</p><p>Click the help icon for more detail.</p>',
		'imagePanelDocUrl'     : baseUrl + '/documentation/gene-help.html#phenotype-images',
		'expressionPanel'      : '<p>Expression images associated to this gene.',
		'expressionPanelDocUrl': baseUrl + '/documentation/gene-help.html#expression-images',
		'allelePanel'          : '<p>Alleles and ES cells of this gene produced from the IKMC project.  When available to order a link to the correspondiong repository will be included.</p><p>Click the help icon for more detail.</p>',
		'allelePanelDocUrl'    : baseUrl + '/documentation/gene-help.html#alleles'
};
MDOC.phenotypes = {
		'generalPanel'         : "<p> Phenotype details panel.<p> <p>Click the help icon for more detail.</p>",
		'generalPanelDocUrl'   : baseUrl + '/documentation/phenotype-help.html',
		'relatedMpPanel'       : "<p>Allele associated with current phenotype. You can filter the table using the dropdown checkbox filters over the table, sort by one column and export the data. <p>Click the help icon for more detail.</p>",
		'relatedMpPanelDocUrl' : baseUrl + '/documentation/phenotype-help.html#associations'
};
MDOC.images = {
		'generalPanel'         : "<p>All images associated with current phenotype.</p> <p>Click the help icon for more detail.</p>",
		'generalPanelDocUrl'   : baseUrl + '/documentation/image-help.html',
};

MDOC.scatter = MDOC.stats = {
		'generalPanel'         : '<p>Details about the graphs.</p> <p>Click the help icon for more detail.</p>',
		'generalPanelDocUrl'   : baseUrl + '/documentation/graph-help.html#types'
};
