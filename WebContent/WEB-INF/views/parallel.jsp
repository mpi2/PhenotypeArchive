<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:genericpage>
    <jsp:attribute name="title">Parrallel Coordinates Chart for ${procedure}</jsp:attribute>
    <jsp:attribute name="bodyTag"><body  class="gene-node no-sidebars small-header"></jsp:attribute>
    
    <jsp:attribute name="header">
            
            <!-- CSS Local Imports -->
            <link rel="stylesheet" type="text/css" href="${baseUrl}/css/parallel.css"/> 
            <link rel="stylesheet" href="${baseUrl}/css/vendor/slick.grid.css" type="text/css" media="screen" charset="utf-8" />
            <link rel="stylesheet" href="${baseUrl}/css/parallelCoordinates/style.css" type="text/css" charset="utf-8" />
            
            <!-- JavaScript Local Imports -->
            <script type="text/javascript" src="${baseUrl}/js/vendor/d3/d3.v3.js"></script>
    				<script type="text/javascript" src="${baseUrl}/js/vendor/d3/d3.js"></script>
					  <script type="text/javascript" src="${baseUrl}/js/vendor/d3/d3.csv.js"></script>
					  <script type="text/javascript" src="${baseUrl}/js/vendor/d3/d3.layout.js"></script>
					  <script type="text/javascript" src="${baseUrl}/js/charts/parallel/parallel-coordinates-veggie.js"></script>
					
					  <script type="text/javascript" src="${baseUrl}/js/vendor/jquery/jquery.js"></script>
					  <script type="text/javascript" src="${baseUrl}/js/vendor/underscore.js"></script>
					  <script type="text/javascript" src="${baseUrl}/js/vendor/backbone.js"></script>
					
					  <script src="${baseUrl}/js/vendor/jquery/jquery-ui-1.8.16.custom.min.js"></script>
					  <script type="text/javascript" src="${baseUrl}/js/charts/parallel/filter.js"></script>
					  
					  <!-- SlickGrid -->
 						<script src="${baseUrl}/js/vendor/jquery/jquery.event.drag-2.0.min.js"></script>
  					<script src="${baseUrl}/js/vendor/slick/slick.core.js"></script>
  					<script src="${baseUrl}/js/vendor/slick/slick.grid.js"></script>
  					<script src="${baseUrl}/js/vendor/slick/slick.dataview.js"></script>
  					<script src="${baseUrl}/js/vendor/slick/slick.pager.js"></script>
  					<script src="${baseUrl}/js/charts/parallel/grid.js"></script>
  					<script src="${baseUrl}/js/charts/parallel/pie.js"></script>
  					<script src="${baseUrl}/js/charts/parallel/options.js"></script>
  					<!-- script src="${baseUrl}/js/charts/parallel/food-table.js"></script -->
    </jsp:attribute>
    
    <jsp:body>
    
		
			<div class="content">
			<div class="section"> <div class="inner">
				<div id="nav">
				  <h1>${procedure}</h1>
				  <div class="widget right toggle">
					  <input type="range" min="0" max="1" value="0.2" step="0.01" name="power" list="powers" id="line_opacity"></input>
				   <br/>
					  Opacity: <span id="opacity_level">20%</span>
					</div>
				  <div><a href="#" id="shadows" class="right toggle">Shadows</a></div>
				  <div><a href="#" id="inverted" class="right toggle">Dark</a></div>
				  <div><a href="#" id="no_ticks" class="right toggle">Hide Ticks</a></div>
				  
				</div>
			
			<div id="row-fluid">
			  <div class="widgets">
			   
			    <div id="totals" class="widget right">Total Selected<br/></div>
			    <div id="pie" class="widget right">Group Breakdown<br/></div>
			    <a href="#" id="export_selected" class="button green filter_control">Export</a>
			    <a href="#" id="remove_selected" class="button red filter_control">Remove</a>
			    <a href="#" id="keep_selected" class="button green filter_control">Keep</a>
			    <div id="pager" class="info"></div>
			        <div id="legend">
			    </div>
			  </div>
		  <div id="parallel"> </div>
		  <div id="myGrid"></div>
		  
		  
		  
		  <script type="text/javascript">

		  ${dataJs}
		  $(function() {
		    var dimensions = new Filter();
		    var highlighter = new Selector();
		
		    dimensions.set({data: foods });
		
		    var columns = _(foods[0]).keys();
		    var axes = _(columns).without('name', 'group');
		
		    var foodgroups =
		      [ "Mutant", "WT"];
		    
		    var colors = {
				      "Mutant" : '#0978A1',
					    "WT" : '#EF7B0B'
		    }
		    
		    _(foodgroups).each(function(group) {
		      $('#legend').append("<div class='item'><div class='color' style='background: " + colors[group] + "';></div><div class='key'>" + group + "</div></div>");
		    });
		
		    var pc = parallel(dimensions, colors);
		    var pie = piegroups(foods, foodgroups, colors, 'group');
		    var totals = pietotals(
		      ['in', 'out'],
		      [_(foods).size(), 0]
		    );
		
		    var slicky = new grid({
		      model: dimensions,
		      selector: highlighter,
		      width: '100%',
		      columns: columns
		    });
		    
		    // vertical full screen
		    var parallel_height = $(window).height() - 64 - 12 - 120 - 320;
		    if (parallel_height < 340) parallel_height = 340;  // min height
		    $('#parallel').css({
		        height: parallel_height + 'px',
		        width: '100%'
		    });
		    
		    slicky.update();
		    pc.render();
		
		    dimensions.bind('change:filtered', function() {
		      var data = dimensions.get('data');
		      var filtered = dimensions.get('filtered');
		      var data_size = _(data).size();
		      var filtered_size = _(filtered).size();
		      pie.update(filtered);
		      totals.update([filtered_size, data_size - filtered_size]);
		      
		      var opacity = _([2/Math.pow(filtered_size,0.37), 100]).min();
		      $('#line_opacity').val(opacity).change();
		    });
		    
		    highlighter.bind('change:selected', function() {
		      var highlighted = this.get('selected');
		      pc.highlight(highlighted);
		    });
		
		    $('#remove_selected').click(function() {
		      dimensions.outliers();
		      pc.update(dimensions.get('data'));
		      pc.render();
		      dimensions.trigger('change:filtered');
		      return false;
		    });
		    
		    $('#keep_selected').click(function() {
		      dimensions.inliers();
		      pc.update(dimensions.get('data'));
		      pc.render();
		      dimensions.trigger('change:filtered');
		      return false;
		    });
		    
		    $('#export_selected').click(function() {
		      var data = dimensions.get('filtered');
		      var keys = _.keys(data[0]);
		      var csv = _(keys).map(function(d) { return '"' + addslashes(d) + '"'; }).join(",");
		      _(data).each(function(row) {
		        csv += "\n";
		        csv += _(keys).map(function(k) {
		          var val = row[k];
		          if (_.isString(val)) {
		            return '"' + addslashes(val) + '"';
		          }
		          if (_.isNumber(val)) {
		            return val;
		          }
		          if (_.isNull(val)) {
		            return "";
		          }
		        }).join(",");
		      });
		      var uriContent = "data:application/octet-stream," + encodeURIComponent(csv);
		      var myWindow = window.open(uriContent, "Nutrient CSV");
		      myWindow.focus();
		      return false;
		    });
		
		    $('#line_opacity').change(function() {
		      var val = $(this).val();
		      $('#parallel .foreground path').css('stroke-opacity', val.toString());
		      $('#opacity_level').html((Math.round(val*10000)/100) + "%");
		    });
		    
		    $('#parallel').resize(function() {
		      // vertical full screen
		      pc.render();
		      var val = $('#line_opacity').val();
		      $('#parallel .foreground path').css('stroke-opacity', val.toString());
		    });
		    
		    $('#parallel').resizable({
		      handles: 's',
		      resize: function () { return false; }
		    });
		    
		    $('#myGrid').resizable({
		      handles: 's'
		    });
		
		    function addslashes( str ) {
		      return (str+'')
		        .replace(/\"/g, "\"\"")        // escape double quotes
		        .replace(/\0/g, "\\0");        // replace nulls with 0
		    };
		  
		  });
		  </script>
		</div>

	</div>
	</div>
	</div>
    </jsp:body>
    
</t:genericpage>