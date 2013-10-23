
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

var defaultDesc = 'Brief info about this panel';

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
		'generalPanel'         : defaultDesc,
		'generalPanelDocUrl'   : baseUrl + '/documentation/gene-help.html#details',
		'preQcPanel'           : defaultDesc,
		'preQcPanelDocUrl'     : baseUrl + '/documentation/gene-help.html#pre-qc',
		'mpPanel'              : defaultDesc,
		'mpPanelDocUrl'        : baseUrl + '/documentation/gene-help.html#phenotypes',
		'imagePanel'           : defaultDesc,
		'imagePanelDocUrl'     : baseUrl + '/documentation/gene-help.html#phenotype-images',
		'expressionPanel'      : defaultDesc,
		'expressionPanelDocUrl': baseUrl + '/documentation/gene-help.html#expression-images',
		'allelePanel'          : defaultDesc,
		'allelePanelDocUrl'    : baseUrl + '/documentation/gene-help.html#alleles'
};
MDOC.phenotypes = {
		'generalPanel'         : defaultDesc,
		'generalPanelDocUrl'   : baseUrl + '/documentation/phenotype-help.html',
		'relatedMpPanel'       : defaultDesc,
		'relatedMpPanelDocUrl' : baseUrl + '/documentation/phenotype-help.html#associations'
};
MDOC.ma = {
		'generalPanel'         : defaultDesc,
		'generalPanelDocUrl'   : baseUrl + '/documentation/maGeneralPanel.html',
		'mpPanel'              : defaultDesc,
		'mpPanelDocUrl'        : baseUrl + '/documentation/maMpPanel.html',
		'relatedMaPanel'       : defaultDesc,
		'relatedMaPanelDocUrl' : baseUrl + '/documentation/maRelatedMaPanel.html',
		'expressionPanel'      : defaultDesc,
		'expressionPanelDocUrl': baseUrl + '/documentation/maExpressionPanel.html'
};


