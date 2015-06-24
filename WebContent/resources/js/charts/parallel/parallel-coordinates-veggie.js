(function(d3) {

// Example for hover-over lines 
// http://bl.ocks.org/mbostock/3709000

	window.parallel = function(model, colors) {
    var self = {},
        dimensions,
        dragging = {},
        highlighted = null,
        container = d3.select("#parallel");

    var line = d3.svg.line().interpolate('cardinal').tension(0.85),
        axis = d3.svg.axis().orient("left"),
        background,
        foreground;
  
    var cars = model.get('data');
    
    self.update = function(data) {
      cars = data;
    };
    
    self.render = function() {
    
      container.select("svg").remove();
      
      var bounds = [ $(container[0]).width(), $(container[0]).height() ],
          m = [170, 10, 10, 10],
          w = bounds[0] - m[1] - m[3],
          h = bounds[1] - m[0] - m[2];

      var x = d3.scale.ordinal().rangePoints([0, w], 1),
          y = {};

      var svg = container.append("svg:svg")
          .attr("width", w + m[1] + m[3])
          .attr("height", h + m[0] + m[2])
          .append("svg:g")
          .attr("transform", "translate(" + m[3] + "," + m[0] + ")");

      // Extract the list of dimensions and create a scale for each.
      x.domain(dimensions = d3.keys(cars[0]).filter(function(d) {
        return d != "name" && d != "group" &&  d != "id" && 
        	(y[d] = d3.scale.linear()
            .domain(d3.extent(cars, function(p) { return +p[d]; }))
            .range([h, 0]));
      }));
      
      // Add grey background lines for context.
      background = svg.append("svg:g")
          .attr("class", "background")
          .selectAll("path")
          .data(cars)
          .enter().append("svg:path")
          .attr("d", path);

      // Add blue foreground lines for focus.
      foreground = svg.append("svg:g")
          .attr("class", "foreground")
          .selectAll("path")
          .data(cars)
          .enter().append("svg:path")
          .attr("d", path)
          .attr("style", function(d) {
            return "stroke:" + colors[d.group] + ";";
          });
      
      var series = svg.selectAll(".series")
      	.data(cars)
      	.enter().append("svg:g")
      	.attr("class", "series");

      var point  = series.selectAll(".point")
	  	.data(dimensions.map(function(p) {
	  		return p;	  		
    	 }))
	  	.enter().append("svg:circle")
	   	.attr("cx", function (d,i,j) { 
	       	  console.log("Dimensions for " + d + " point pos: " + i + "  " + j + " + y= " + cars[j][d] + "  ==  " + y[d](cars[j][d]) + " x=" + x(i)); 
		 	  return x(i);
		}) 
		.attr("cy", function (d,i,j) { 
		  	  return y[d](cars[j][d]) ;
		})
		.attr("r", function (d,i,j) { 
		  	  return getRadius(cars[j][d]) ;
		});
      
      
      // Add a group element for each dimension.
      var g = svg.selectAll(".dimension")
          .data(dimensions)
          .enter().append("svg:g")
          .attr("class", "dimension")
          .attr("transform", function(d) { return "translate(" + x(d) + ")"; })
          .call(d3.behavior.drag()
          .on("dragstart", function(d) {
              dragging[d] = this.__origin__ = x(d);
              background.attr("visibility", "hidden");
          })
          .on("drag", function(d) {
              dragging[d] = Math.min(w, Math.max(0, this.__origin__ += d3.event.dx));
              foreground.attr("d", path);
              dimensions.sort(function(a, b) { return position(a) - position(b); });
              x.domain(dimensions);
              g.attr("transform", function(d) { return "translate(" + position(d) + ")"; })
          })
          .on("dragend", function(d) {
              delete this.__origin__;
              delete dragging[d];
              transition(d3.select(this)).attr("transform", "translate(" + x(d) + ")");
              transition(foreground).attr("d", path);
              background
                  .attr("d", path)
                  .transition()
                  .delay(500)
                  .duration(0)
                  .attr("visibility", null);
          }));
      
     
      // Add an axis and title.
      g.append("svg:g")
          .attr("class", "axis")
          .each(function(d) { d3.select(this).call(axis.scale(y[d])); })
          .append("svg:text")
          .attr("text-anchor", "start")
          .attr("y", 0).attr("transform", function(d) {
              return "rotate(-90)" 
          })
          .text(String);

      // Add and store a brush for each axis.
      g.append("svg:g")
          .attr("class", "brush")
          .each(function(d) { d3.select(this).call(y[d].brush = d3.svg.brush().y(y[d]).on("brush", brush)); })
          .selectAll("rect")
          .attr("x", -12)
          .attr("width", 24);
      
      function position(d) {
        var v = dragging[d];
        return v == null ? x(d) : v;
      }
      
      // Returns the path for a given data point.
      function path(d) {
    	  
        //return line(dimensions.map(function(p) { return [position(p), y[p](d[p])]; }));
    	return line(dimensions.map(function(p) {
    	    // check for undefined values
    	    if (d[p] == null) {
    	    //	return [x(p), null];
    	    }
    	   return [x(p), y[p](d[p])];
    	 }));
      }
      
      // Handles a brush event, toggling the display of foreground lines.
      function brush() {
        var actives = dimensions.filter(function(p) {
          return !y[p].brush.empty();
         })
         
        var extents = actives.map(function(p) {
          return y[p].brush.extent();
        });
        
        /** To be factored **/
        var filter = {};
        _(actives).each(function(key, i) {
          filter[key] = {
            min: extents[i][0],
            max: extents[i][1]
          };
        });
        model.set({filter: filter});
        /***/
        foreground.style("display", function(d) {
          return actives.every(function(p, i) {
            return extents[i][0] <= d[p] && d[p] <= extents[i][1];
          }) ? null : "none";
        });
      }
      
      function transition(g) {
        return g.transition().duration(500);
      }
      
      function getRadius(y) {
    	  if (y == null){
    		  return 2;
    	  }
          return 0;
        }
      
      self.highlight = function(i) {
        if (typeof i == "undefined") {
          d3.select("#parallel .foreground").style("opacity", function(d, j) {
            return "1";
          });
          highlighted.remove();
        } else {
          d3.select("#parallel .foreground").style("opacity", function(d, j) {
            return "0.35";
          });
          if (highlighted != null) {
            highlighted.remove();
          }
          highlighted = svg.append("svg:g")
                   .attr("class", "highlight")
                   .selectAll("path")
                   .data([model.get('filtered')[i]])
                   .enter().append("svg:path")
                   .attr("d", path)
                   .attr("style", function(d) {
                	   return "stroke:" + colors[d.group] + ";";
                   });
          
        }
      };
    };
    
    return self;
  };
  
})(d3);
