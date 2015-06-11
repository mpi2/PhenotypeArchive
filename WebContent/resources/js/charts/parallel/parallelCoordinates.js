$(document).ready(function(){	
	// text
    var dataset = [ 5, 10, 15, 20, 25 ];
    d3.select("#main").selectAll("p")
	    .data(dataset)
	    .enter()
	    .append("p")
	    .text(function(d) { return "I can count up to " + d; })
	    .style("color", function(d) {
	        if (d > 15) {   //Threshold of 15
	            return "red";
	        } else {
	            return "black";
	        }
    });
    
    // bars
    d3.select("#d3div").selectAll("div")
    	.data(dataset)
    	.enter()
    	.append("div")
    	.attr("class", "bar")
    	.style("height", function(d) {
    	    var barHeight = d * 5;  //Scale up by factor of 5
    	    return barHeight + "px";
    	});
    
    // circles
    var w = 500;
    var h = 50;
    var svg = d3.select("#d3svg").append("svg");
    svg.attr("width", 500)
    	.attr("height", 50);
    var circles = svg.selectAll("circle")
    	.data(dataset)
    	.enter()
    	.append("circle");
    
    circles.attr("cx", function(d, i) {
        return (i * 50) + 25;
    });
    circles.attr("cy", h/2)
	   .attr("r", function(d) {
	        return d;
	   });
    
    // bars
    
    var data = [ 5, 10, 13, 19, 21, 25, 22, 18, 15, 13,
                    11, 12, 15, 20, 18, 17, 16, 18, 23, 25 ];
    var barPadding = 1; 
    w = 500;
    h = 100;
    
    svg = d3.select("#bars")
	    .append("svg")
	    .attr("width", w)
	    .attr("height", h);
    
    svg.selectAll("rect")
	    .data(data)
	    .enter()
	    .append("rect")
	    .attr("x", function(d, i) {
	    	return i * (w / data.length);
	    })
	    .attr("y", function(d) {
		    return h - d*4;  //Height minus data value
		})
	    .attr("width", w / data.length - barPadding)
		.attr("height", function(d) {
		    return d *4;  // <-- Times four!
		})
		.attr("fill", function(d) {
		    return "rgb(0, 0, " + (d * 10) + ")";
		});
    
    svg.selectAll("text")
	   .data(data)
	   .enter()
	   .append("text")
	   .text(function(d) {
	   		return d;
	   })
	   .attr("x", function(d, i) {
	   		return i * (w / data.length) + 5;
	   })
	   .attr("y", function(d) {
	   		return h - (d * 4) + 15;
	   })
	   .attr("font-family", "sans-serif")
	   .attr("font-size", "11px")
	   .attr("fill", "white");

    
    //scatterplot
    svg = d3.select("#scatterplot")
    .append("svg")
    .attr("width", w)
    .attr("height", h);
    dataset = [
               [ 5,     20 ],
               [ 480,   90 ],
               [ 250,   50 ],
               [ 100,   33 ],
               [ 330,   95 ],
               [ 410,   12 ],
               [ 475,   44 ],
               [ 25,    67 ],
               [ 85,    21 ],
               [ 220,   88 ]
           ];
    svg.selectAll("circle")
	    .data(dataset)
	    .enter()
	    .append("circle")
	    .attr("cx", function(d) {
	        return d[0];
	    })
	    .attr("cy", function(d) {
	        return d[1];
	    })
	    .attr("r", 5);
    
});




