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

    function toggleTable(id) {
        $("#" + id + "_toggle").on({'click':function(event){
        event.preventDefault();
        $("#" + id + " .rest").toggle();
        
        if($("#" + id + "_toggle").hasClass("toggle_closed")) {
            $("#" + id + "_toggle").removeClass("toggle_closed");
            $("#" + id + "_toggle").addClass("toggle_open");
            var type = $( "#" + id + "_toggle" ).data( "type" );
            var count = $( "#" + id + "_toggle" ).data( "count" );
            $("#" + id + "_toggle").text("Hide " + type);
        }
        else {
            $("#" + id + "_toggle").removeClass("toggle_open");
            $("#" + id + "_toggle").addClass("toggle_closed");
            var type = $( "#" + id + "_toggle" ).data( "type" );
            var count = $( "#" + id + "_toggle" ).data( "count" );
            $("#" + id + "_toggle").text("Show all " + count + " " + type);

            $(".hide_target").each(function( index ) {
                $( this ).hide();
                $( this ).removeClass("toggle_open");
                $( this ).addClass("toggle_closed");
            });
            
            $("[id*=toggle_closed_detail_]").each(function( index ) {
                 $( this ).text("view");
                 $( this ).removeClass("toggle_open");
                 $( this ).addClass("toggle_closed_detail");
            });         
            
            $('html, body').animate({ scrollTop: 0 }, 0);
          }
        
        }});
    }

    function toggleTableDetails() {
        $(".toggle_closed_detail").each(function( index ) {
            var anchor = $( this );
            
               $( this ).on({'click':function(event){
             
               var count = $( this ).data( "count" );             
             
               $( "#hide_target_" + count ).toggle();     

                if(anchor.hasClass("toggle_closed_detail")) {
                    anchor.removeClass("toggle_closed_detail");
                    anchor.addClass("toggle_open");
                    $( this ).text("hide");
                }
                else {
                    anchor.removeClass("toggle_open");
                    anchor.addClass("toggle_closed_detail");
                    $( this ).text("view");
                }
            
               event.preventDefault();
            }});
        });
    }
    
    toggleTable("mutagenesis_table");
    toggleTableDetails();

});