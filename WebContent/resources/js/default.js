(function ($) {
    
function initFooterSitemap() {
    /* copy the mainnavigation to the footer */
    var mainnavi = $('#mn').html();
    $('#footersitemap').html(mainnavi);
}
    
//function initWindow() {
//    /* if the body is not high enough, put the footer to the bottom of the window */
//    var wrapperHeight  = parseInt($('#wrapper').innerHeight());
//    var viewportHeight = parseInt(window.innerHeight);   
//    if (viewportHeight - 50 > wrapperHeight) {
//        $('body').addClass('footerToBottom'); 
//    } else {
//        $('body').removeClass('footerToBottom');
//    }
//}
    
function initWindow() {
    if (!$('body').hasClass('searchpage') && !$('body').hasClass('chartpage')) {
        /* if the body is not high enough, put the footer to the bottom of the window */
        var wrapperHeight  = parseInt($('#wrapper').innerHeight());
        var viewportHeight = parseInt(window.innerHeight);   
        if (viewportHeight - 50 > wrapperHeight) {
            $('body').addClass('footerToBottom'); 
        } else {
            $('body').removeClass('footerToBottom');
        }
    }
}

function initScrolling() {
    /* smooth scrolling for anchor links */
    $('a[href^="#"]').on('click',function (e) {
        e.preventDefault();
        var target = this.hash,
        $target = $(target);
        var topposition = $target.offset().top - 70;
        $('html, body').stop().animate({ 'scrollTop': topposition }, 1000, 'swing', function () {
            /*window.location.hash = target;*/
        });
    });    
}

function initSlider() {
    /* Slider on the frontpage */
    $('.sliderbar li:not(.active)').on('click',function(e) {
        $('.sliderbar li.active').removeClass('active');
        $(this).addClass('active');
        var nr = $(this).index('.sliderbar li');
        showSlide(nr); 
    });
    $('.sliderbar li:first').addClass('active');
}

function showSlide(nr) {
    /* function for the frontpage slider */
    $('.newslide.active').fadeOut(500, function() {
        $('.newslide.active').removeClass('active');
        $('.newslide').eq(nr).hide();
        $('.newslide').eq(nr).addClass('active').fadeIn(500);
    });  
}

function initTooltips() {
    /* different qTip Tooltips */	
    $('[title!=""]').qtip({    	
        style: { classes: 'qtipimpc' },      
        position: { my: 'top right', at: 'bottom center' }
    });    
    $('.has-tooltip').each(function() {
        $(this).qtip({
            style: { classes: 'qtipimpc' },
            position: { my: 'top right', at: 'bottom center' },
            content: { text: $(this).next('.data-tooltip')}
        });
    });
    $('.form-item').has('.description').each(function() {
        $(this).qtip({
            style: { classes: 'qtipimpc' },
            position: { my: 'center left', at: 'right center' },
            content: { text: $('.description',this) }
        });
    });
    $('.status').qtip({
        style: { classes: 'qtipimpc flat' },      
        position: { my: 'top center', at: 'bottom center' }
    });
}

function wdmHeatmap() {
    /* just pimping the Heatmap, so Ajax Tooltips are shown in this example */
    $('td.clickable').qtip({
        show: { event: 'mouseover', effect: false, solo: true },
        hide: { event: 'mouseleave' },
        style: { classes: 'qtipimpc autowidth' },
        position: { my: 'top center', at: 'bottom center' },
        content: {
            text: function(event, api) {
                $.ajax({ url: 'heatmap-tooltip.php' })
                    .done(function(html) {
                        api.set('content.text', html)
                    })
                    .fail(function(xhr, status, error) {
                        api.set('content.text', status + ': ' + error)
                    });    
                return 'Loading...';
            }
        }    
    });
}

function initAccordions() {
    /* Accordeon toggle */
    $('.accordion-heading').on('click',function() {
       $(this).next('.accordion-body').toggle('slow'); 
       $(this).closest('.accordion-group').toggleClass('open');
    });
}

function initSections() {
    /* Section toggle */
    $('.section.collapsed .title').on('click',function() {
        $(this).closest('.section').toggleClass('open');
        $(this).next('.inner').fadeToggle(function() { $(window).resize(); if ($('.ajaxtabs .tabs a.active',this).length == 0) { $('.ajaxtabs a:first',this).trigger('click'); } });
        
    });
    var shortDelay = setTimeout(function() { $('.section.collapsed:first .title').trigger('click'); }, 1500);
}

function initFancybox() {
    /* Fancybox (popup) */
    $('a[href$=".jpg"],.fancyframe').fancybox({'titlePosition':'inside','titleFormat':formatFancyboxTitle});    
    $('.fancybox').fancybox({'type':'image','titlePosition':'inside','titleFormat':formatFancyboxTitle});
}

function formatFancyboxTitle(title, currentObject, currentIndex, currentOpts) {
    if (title) {
        return title;
    } else if ($(currentObject).next('.data-title').html()) {
        // Look, if this fancybox link is followed by an data-title div
        return $(currentObject).next('.data-title').html();
    }
    //return '<div id="tip7-title"><span><a href="javascript:;" onclick="$.fancybox.close();"><img src="/data/closelabel.gif" /></a></span>' + (title && title.length ? '<b>' + title + '</b>' : '' ) + 'Image ' + (currentIndex + 1) + ' of ' + currentArray.length + '</div>';
}

function initTablesort() {
    /* Tablesort plugin */
    $('table.tablesorter').tablesorter({
        'cssHeader': 'headerSort'
    }); 
}

function initTableFilter() {
    /* Filter a table (used on gene page) */
    $('.filtertype').on('click',function() {
       $(this).toggleClass('open'); 
       $(this).next('.filteroptions').toggleClass('open'); 
    });
}

function wdmTestumgebung() {
    /* only for Nicolas to test something */
    if ($('body').hasClass('searchpage')) {
        $('#m_search').addClass('active-trail');
    } else if ($('body').hasClass('page-node')) {
        $('#m_about').addClass('active-trail');
        $('#m_members').addClass('active');
    } else if ($('body').hasClass('front')) {
        $('#m_home').addClass('active');
    } else if ($('body').hasClass('page-news')) {
        $('#m_news').addClass('active');
    } else if ($('body').hasClass('page-news-only')) {
        $('#m_newsonly').addClass('active');
        $('#m_news').addClass('active-trail');        
    }
    
}

function initReduceLongTables() {
    /* Cut long tables and expan them, if needed */
    $('table.reduce').each(function() {
        if ($('tr',this).length > 3) {
            $('tr:gt(3)',this).hide();
            $('tr:last',this).after('<tr class="loadmore"><td colspan="100%"><i class="fa fa-th"></i> show all entries</td></tr>');
        }
    });
    $('tr.loadmore').on('click',function() {
        $(this).prevAll().fadeIn('slow');
        $(this).remove();
        $(this).closest('table').removeClass('reduce');       
    });
    $('.reduce.tablesorter th').on('mouseenter',function() {
        $('tr.loadmore').trigger('click');
    });
}

function initRowtoggle() {
    /* Some tables need to toggle some rows */
   $('tr.clickable').on('click',function() {
       $(this).toggleClass('open');
       if ($(this).hasClass('open')) {
           $('.fa-plus',this).removeClass('fa-plus').addClass('fa-minus');
       } else {
           $('.fa-minus',this).removeClass('fa-minus').addClass('fa-plus');
       }
       $(this).next('tr').fadeToggle('slow');    
   });
}

function initNiceButtons() {
    /* Gives buttons an icon, if they haven't one */
    $('.btn:not(:has(i))').prepend('<i class="fa fa-caret-right"></i>');
}

function initRangeslider() {
    /* jquery UI Slider (for the heatmap) */   
   $('#rangeslider').slider({
       min: 0,
       max: 1,
       step: 0.001,
       slide: function(event,ui) {
           $('#rangeinput').val(ui.value);
       }
   });
   $('#rangeinput').val( $('#rangeslider').slider('value'));
}

function initHighchartDefaults() {    
    /* Defaults settings for Highcharts */
    Highcharts.setOptions({
        chart: {
            style: {
                fontFamily: '"Source Sans Pro",Arial,Helvetica,sans-serif'
            }
        }   
    });    
}

function initTabs() {
    /* Ajax Tabs */
    $('.ajaxtabs').each(function() {
        var ajaxtabs = $(this);
        $('.tabs a',this).on('click',function(e) {
            e.preventDefault();
            var index = $(this).index('.tabs a');
            var tabcontent = $('.tabcontent',ajaxtabs).eq(index);
            
            // show selected .tabcontent
            $('.tabs a',ajaxtabs).removeClass('active');
            $('.tabcontent',ajaxtabs).removeClass('active');
            $(this).addClass('active');
            $(tabcontent).addClass('active').html('<i class="fa fa-spinner fa-spin"></i>');
            
            // load ajax content
            var url = $(tabcontent).attr('data-ajax-url');
            $.ajax({url: url }).done(function(data){ tabcontent.html(data); $(window).resize(); }).error(function(jqXHR,status,msg) { alert('Ajax Error: '+msg); }); 
            
            
        });
    });
    $('.ajaxtabs a:first').not('.collapsed .ajaxtabs a:first').trigger('click');
}

/* Document ready event */
$(document).ready(function() {
    
    /* to delete */
    wdmTestumgebung();
    wdmHeatmap();
    
    /* inits */
    initFooterSitemap();
    initWindow();
    initScrolling();
    initSlider();
    initTooltips();
    initAccordions();
    initSections();
    initFancybox();
    initTablesort();
    initTableFilter();
    initReduceLongTables();
    initRowtoggle();
    initNiceButtons();
    initRangeslider();
    initHighchartDefaults();  
    
    initTabs();         
    
});

/* Window scrolling event */
$(window).scroll(function() {
    
    if ($(window).scrollTop() > 150) {
      $('body').addClass('pinned');
    } else { 
      $('body').removeClass('pinned');  
    }

});

/* Window resize event */
$(window).resize(function() {
    
    initWindow();

});

})(jQuery);