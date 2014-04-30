/**
 * Copyright © 2011-2014 EMBL - European Bioinformatics Institute
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
		url: 'https://databases.apf.edu.au/mutations/snpRow/getSnpCount?mgiAccessionId=' + gene_id,    
		timeout: 3000,
		success: function (response) {
			console.log("success response="+response.count);
                    if(response.count>0){
			$('#enu').html('&nbsp&nbsp&nbsp<a href="https://databases.apf.edu.au/mutations/snpRow/list?mgiAccessionId='+gene_id+'">ENU('+response.count+')</a>');
                    }else{
                        $('#enu').html('');
                    }
		}
		,error: function(x, t, m) {
	      //  if(t==="timeout") { 
	        //log error to gene page so we know this is down not just 0.
			var errorMsg='<font color="red">Error trying to retrieve ENU Links( '+t+')</font>';
	    	$('#enu').html(errorMsg);
	    }
	});


});
