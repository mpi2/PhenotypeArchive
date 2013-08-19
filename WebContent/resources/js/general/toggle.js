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
		
$('.accordion').on('show hide', function(e){
    $(e.target).siblings('.accordion-heading').find('.accordion-toggle i').toggleClass('icon-chevron-down icon-chevron-right', 0);
});

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

});