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

	$('a.interest').click(function(){
		var termId = $(this).attr('id');
		var endpoint = null;
	     
	     if ( /^MP:/.exec(termId) ){
	     	endpoint = "/togglempflagfromjs/";
	     }
	     else if ( /^MGI:/.exec(termId) ){
	     	endpoint = "/toggleflagfromjs/";
	     }
		
		
		var label = $(this).text();
		var regBtn = $(this);
		$.ajax({
			url: endpoint + termId,                           
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
        	console.log('error on registering interest');                     
        }
    });
	return false;    		    	  
});


});
