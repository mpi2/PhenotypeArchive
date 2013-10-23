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
 * imageUtils: various util functions
 * 
 */

function imgError(image){
	image.onerror = "";
	image.src = "../img/NoImageAvailable_200x200.png";
	return true;
}

$(document).ready(function(){						
	
	// bubble popup for brief panel documentation
	$.fn.qTip({
		'pageName': 'images',
		'textAlign': 'left',
		'tip': 'topRight'
	});
});

