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
 * toggle: used for the image dropdown.
 * 
 */
jQuery(document).ready(	function() {

//code for setting ENU links on Gene Page	
	
	$.ajax({
		url: '../genesAllele/' + gene_id,    
		timeout: 2000,
		success: function (response) {
			$('#allele').html(response);
			
		}
		,error: function(x, t, m) {
	      //  if(t==="timeout") { 
	        //log error to gene page so we know this is down not just 0.
			var errorMsg='<td>ENU Link:</td><td class="gene-data" id="allele_links"><font color="red"><font color="red">Error trying to retrieve allele product infomation</font></td>';
	    	$('#allele').html(errorMsg);
	    }
	});



});