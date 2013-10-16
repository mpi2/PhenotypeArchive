
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
		'facetPanel'         : '<h6>Browse IMPC data with facets</h6>'
							+ '<ul><li>Click on a facet/subfacet to open or hide it.</li>'
							+ '    <li>Ways to display facet result:'
							+ '        <ul><li>Click on the <b>facet counts</b> on the right.</li>'
							+ '            <li>Click on <b>checkbox</b> on the left.</li>'
							+'         </ul></li>'
							+'     <li>Click on the <b>info button</b> for detailed description.</li>'
							+ '</ul>', 
			
		'facetPanelDocUrl'   : '/data/documentation/facetPanel.html',	
};
MDOC.gene = {		
		'generalPanel'         : defaultDesc,
		'generalPanelDocUrl'   : '/data/documentation/geneGeneralPanel.html',
		'preQcPanel'           : defaultDesc,
		'preQcPanelDocUrl'     : '/data/documentation/preQcPanel.html',
		'mpPanel'              : defaultDesc,
		'mpPanelDocUrl'        : '/data/documentation/geneMpPanel.html',
		'imagePanel'           : defaultDesc,
		'imagePanelDocUrl'     : '/data/documentation/geneImagePanel.html',
		'expressionPanel'      : defaultDesc,
		'expressionPanelDocUrl': '/data/documentation/geneExpressionPanel.html',
		'allelePanel'          : defaultDesc,
		'allelePanelDocUrl'    : '/data/documentation/allelePanel.html'
};
MDOC.mp = {
		'generalPanel'         : defaultDesc,
		'generalPanelDocUrl'   : '/data/documentation/mpGeneralPanel.html',
		'genePanel'            : defaultDesc,
		'genePanelDocUrl'      : '/data/documentation/mpGenePanel.html',
		'relatedMpPanel'       : defaultDesc,
		'relatedMpPanelDocUrl' : '/data/documentation/relatedMpPanel.html',
		'imagePanel'           : defaultDesc,
		'imagePanelDocUrl'     : '/data/documentation/mpImagePanel.html'
};
MDOC.ma = {
		'generalPanel'         : defaultDesc,
		'generalPanelDocUrl'   : '/data/documentation/maGeneralPanel.html',
		'mpPanel'              : defaultDesc,
		'mpPanelDocUrl'        : '/data/documentation/maMpPanel.html',
		'relatedMaPanel'       : defaultDesc,
		'relatedMaPanelDocUrl' : '/data/documentation/maRelatedMaPanel.html',
		'expressionPanel'      : defaultDesc,
		'expressionPanelDocUrl': '/data/documentation/maExpressionPanel.html'
};


