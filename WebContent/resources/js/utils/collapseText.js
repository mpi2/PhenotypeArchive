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
 * collapseText: dynamic collapse of text (applicable everywhere)
 * Author: Chao-Kung Chen
 * Date: 2012/08/08
 * 
 */
$.widget("IMPC.collapseText", {
	options: {
		// default options - can be overridden by caller options	
		size: 50, // default length of characters in text to be displayed 
		showMore: "<span class='expandText'> ... [<span class='txtCollapse'>&nbsp;show&nbsp;rest&nbsp;</span>]</span>",
		showLess: "<span class='collapseText'> [<span class='txtCollapse'>&nbsp;show&nbsp;less&nbsp;</span>]</span>"				
	},	
	_create: function(){
		this.element.addClass('collapseText');		
		this.options.originalText = this.element.text();
		this._collapse();		
	},	
	_collapse: function(){		
		var displayText = this.options.originalText.substring(0, this.options.size);
				
		var self = this;
		self.element.text(displayText).append(self.options.showMore);	
		self.element.find('span.expandText').click(function(){
			self._expand();
		});
	},
	_expand: function(){		
		var self = this;
		self.element.text(self.options.originalText).append(self.options.showLess);
		self.element.find('span.collapseText').click(function(){
			self._collapse();
		});
	},
	destroy: function(){
		this.element.removeClass('collapseText');
		// call the base destroy function
		$.Widget.prototype.destroy.call( this );
	}
	
});