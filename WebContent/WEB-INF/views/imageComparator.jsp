<%@ page language="java" contentType="text/html; charset=US-ASCII"
    pageEncoding="US-ASCII"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=US-ASCII">
<title>Image Picker</title>
<style type="text/css">
<!--
html,body{height:100%}
body {margin:0; padding:0; background:#333 url(img/sw_page_bg.png); overflow:hidden; font-family:Helvetica, Arial, sans-serif; font-size:16px;}
/* custom fonts */
/* @font-face {
	font-family: 'eurof55-webfont';
	src: url('fonts/eurof55-webfont.eot');
	src: local('☺'), url('fonts/eurof55-webfont.woff') format('woff'), url('fonts/eurof55-webfont.ttf') format('truetype'), url('fonts/eurof55-webfont.svg#webfont8xigBfG2') format('svg');
}
@font-face {
	font-family: 'eurof35-webfont';
	src: url('fonts/eurof35-webfont.eot');
	src: local('☺'), url('fonts/eurof35-webfont.woff') format('woff'), url('fonts/eurof35-webfont.ttf') format('truetype'), url('fonts/eurof35-webfont.svg#webfont8xigBfG2') format('svg');
}
@font-face {
	font-family: 'graublauweb-webfont';
	src: url('fonts/graublauweb-webfont.eot');
	src: local('☺'), url('fonts/graublauweb-webfont.woff') format('woff'), url('fonts/graublauweb-webfont.ttf') format('truetype'), url('fonts/graublauweb-webfont.svg#webfont8xigBfG2') format('svg');
} */
.clear{clear:both;}
a:link,a:visited,a:hover{color:#ddd;}
a:hover{color:#fff; text-decoration:none;}
#bg{position:fixed; left:585px; top:0; width:100%; height:100%;}
#bgimg{display:none; cursor:pointer; -ms-interpolation-mode: bicubic;} /* special IE fix for resized images */
#preloader{position:absolute; z-index:2; width:140px; padding:20px; top:20px; left:50px; background:#000; color:#666; font-family:graublauweb-webfont, Helvetica, Arial, sans-serif; font-size:16px; -moz-border-radius:5px; -khtml-border-radius:5px; -webkit-border-radius:5px; border-radius:5px;}
#preloader img{margin-right:20px;}
#toolbar{display:inline-block; padding:4px 15px; margin:20px 15px; background:#262626 url(img/sw_btn_bg.png) repeat-x; -moz-border-radius:8px; -khtml-border-radius:8px; -webkit-border-radius:8px; border-radius:8px; font-family:graublauweb-webfont, Helvetica, Arial, sans-serif; font-size:12px; color:#fff; cursor:pointer;}
#outer_container{position:relative; margin:0; width:700px; padding:0 100px 0 0; z-index:2; background:url(img/empty.gif);} /* fucking IE needs a background value to understand hover area */
#customScrollBox{position:relative; overflow:hidden; background:url(img/sw_l_bg.png) repeat-y;}
#customScrollBox .container{position:relative; width:585px; top:0; float:left;}
#customScrollBox .content{clear:both;}
#customScrollBox .content h1{padding:5px; margin:10px; color:#fff; font-family:eurof55-webfont, Helvetica, Arial, sans-serif; font-size:48px;}
#customScrollBox .content h2{padding:5px; margin:10px 10px 0 10px; color:#fff; font-family:eurof35-webfont, Helvetica, Arial, sans-serif; font-size:24px;}
#customScrollBox .content p{padding:5px; margin:0 10px 10px 10px; color:#ddd; font-family:graublauweb-webfont, Helvetica, Arial, sans-serif; line-height:26px;}
.light{font-family:eurof35-webfont, Helvetica, Arial, sans-serif;}
.grey{color:#999;}
.lightgrey{color:#ddd;}
.s36{font-size:36px;}
.s24{font-size:24px;}
#customScrollBox a.thumb_link{position:relative; margin:0 0 1px 1px; display:block; float:left;}
#customScrollBox img{border:none;}
#customScrollBox a.thumb_link .selected{position:absolute; top:0; left:0; width:145px; height:91px; background:url(img/sw_thumb_selected.png) no-repeat; display:none;}
#dragger_container{position:relative; width:30px; height:580px; float:left; margin:10px 0 0 0; background:url(img/sw_dragger_bg.png) repeat-y center;}
#dragger{position:absolute; width:30px; height:59px; background:url(img/round_custom_scrollbar_bg.png) no-repeat center center; cursor:pointer;}
#arrow_indicator{position:absolute; z-index:1; width:50px; padding:10px; top:50%; margin-top:-25px; left:20px; background:url(img/sw_transparent_black_bg.png); -moz-border-radius:5px; -khtml-border-radius:5px; -webkit-border-radius:5px; border-radius:5px; display:none;}
#nextimage_tip{position:fixed; z-index:1; padding:0 20px; line-height:40px; color:#fff; height:40px; top:50%; margin-top:-20px; right:20px; background:url(img/sw_transparent_black_bg.png); -moz-border-radius:5px; -khtml-border-radius:5px; -webkit-border-radius:5px; border-radius:5px; display:none; font-family:graublauweb-webfont, Helvetica, Arial, sans-serif;}
.with_border{border:1px solid #000;}
.with_shadow{-moz-box-shadow:0 0 40px #000; -webkit-box-shadow:0 0 40px #000; box-shadow:0 0 40px #000;}
-->
</style>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
 <!--  <link rel="stylesheet" type="text/css" href="css/bootstrap.css">
  <link rel="stylesheet" type="text/css" href="css/bootstrap-responsive.css">
  <link rel="stylesheet" type="text/css" href="examples.css">
  <link rel="stylesheet" type="text/css" href="image-picker/image-picker.css"> -->
 <link rel="stylesheet" href="${baseUrl}/css/vendor/jquery.ui/jquery.ui.core.css">
<link rel="stylesheet" href="${baseUrl}/css/vendor/jquery.ui/jquery.ui.slider.css">

<script src="//ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script>
<script src="//ajax.googleapis.com/ajax/libs/jqueryui/1.10.3/jquery-ui.min.js"></script>
<script type="text/javascript" src="${baseUrl}/js/vendor/jquery/jquery.easing.1.3.js"></script>
<script type="text/javascript" src="${baseUrl}/js/vendor/jquery/jquery.mousewheel.min.js"></script>

</head>
<body>
<div id="outer_container">
<div id="customScrollBox">
	<div class="container">
    	<div class="content">
        	<!-- <h1>SIDE<span class="lightgrey">WAYS</span> <br /><span class="light"><span class="grey"><span class="s36">JQUERY FULLSCREEN IMAGE GALLERY</span></span></span></h1>
            <p>A simple, yet elegant fullscreen image gallery created with the jQuery framework and some simple CSS. <a href="http://manos.malihu.gr/sideways-jquery-fullscreen-image-gallery" target="_blank">Full post and download files.</a></p>
             --><div id="toolbar"></div><div class="clear"></div>
             <a href="img/space/Universe_and_planets_digital_art_wallpaper_lucernarium.jpg" class="thumb_link"><span class="selected"></span><img src="img/space/Universe_and_planets_digital_art_wallpaper_lucernarium_thumb.jpg" title="Supremus Lucernarium" alt="Supremus Lucernarium" class="thumb" /></a>
            <a href="img/space/Universe_and_planets_digital_art_wallpaper_denebola.jpg" class="thumb_link"><span class="selected"></span><img src="img/space/Universe_and_planets_digital_art_wallpaper_denebola_thumb.jpg" title="Denebola" alt="Denebola" class="thumb" /></a>
            <a href="img/space/Universe_and_planets_digital_art_wallpaper_lux.jpg" class="thumb_link"><span class="selected"></span><img src="img/space/Universe_and_planets_digital_art_wallpaper_lux_thumb.jpg" title="Lux Aeterna" alt="Lux Aeterna" class="thumb" /></a>
            <a href="img/space/Universe_and_planets_digital_art_wallpaper_dk.jpg" class="thumb_link"><span class="selected"></span><img src="img/space/Universe_and_planets_digital_art_wallpaper_dk_thumb.jpg" title="X-Wing on patrol" alt="X-Wing on patrol" class="thumb" /></a>
            <a href="img/space/Universe_and_planets_digital_art_wallpaper_albireo.jpg" class="thumb_link"><span class="selected"></span><img src="img/space/Universe_and_planets_digital_art_wallpaper_albireo_thumb.jpg" title="Albireo Outpost" alt="Albireo Outpost" class="thumb" /></a>
            <a href="img/space/Universe_and_planets_digital_art_wallpaper_church.jpg" class="thumb_link"><span class="selected"></span><img src="img/space/Universe_and_planets_digital_art_wallpaper_church_thumb.jpg" title="Church of Heaven" alt="Church of Heaven" class="thumb" /></a>
            <a href="img/space/Universe_and_planets_digital_art_wallpaper_Decampment.jpg" class="thumb_link"><span class="selected"></span><img src="img/space/Universe_and_planets_digital_art_wallpaper_Decampment_thumb.jpg" title="Decampment" alt="Decampment" class="thumb" /></a>
            <a href="img/space/Universe_and_planets_digital_art_wallpaper_Hibernaculum.jpg" class="thumb_link"><span class="selected"></span><img src="img/space/Universe_and_planets_digital_art_wallpaper_Hibernaculum_thumb.jpg" title="Hibernaculum" alt="Hibernaculum" class="thumb" /></a>
            <a href="img/space/Universe_and_planets_digital_art_wallpaper_moons.jpg" class="thumb_link"><span class="selected"></span><img src="img/space/Universe_and_planets_digital_art_wallpaper_moons_thumb.jpg" title="Aurea Mediocritas" alt="Aurea Mediocritas" class="thumb" /></a>
            <a href="img/space/Universe_and_planets_digital_art_wallpaper_praedestinatio.jpg" class="thumb_link"><span class="selected"></span><img src="img/space/Universe_and_planets_digital_art_wallpaper_praedestinatio_thumb.jpg" title="Praedestinatio" alt="Praedestinatio" class="thumb" /></a>
            <a href="img/space/Universe_and_planets_digital_art_wallpaper_transitorius.jpg" class="thumb_link"><span class="selected"></span><img src="img/space/Universe_and_planets_digital_art_wallpaper_transitorius_thumb.jpg" title="Transitorius" alt="Transitorius" class="thumb" /></a>
            <a href="img/space/Universe_and_planets_digital_art_wallpaper_victimofgravity.jpg" class="thumb_link"><span class="selected"></span><img src="img/space/Universe_and_planets_digital_art_wallpaper_victimofgravity_thumb.jpg" title="Victim of Gravity" alt="Victim of Gravity" class="thumb" /></a>
            <a href="img/space/Universe_and_planets_digital_art_wallpaper_lucernarium.jpg" class="thumb_link"><span class="selected"></span><img src="img/space/Universe_and_planets_digital_art_wallpaper_lucernarium_thumb.jpg" title="Supremus Lucernarium" alt="Supremus Lucernarium" class="thumb" /></a>
            <a href="img/space/Universe_and_planets_digital_art_wallpaper_denebola.jpg" class="thumb_link"><span class="selected"></span><img src="img/space/Universe_and_planets_digital_art_wallpaper_denebola_thumb.jpg" title="Denebola" alt="Denebola" class="thumb" /></a>
            <a href="img/space/Universe_and_planets_digital_art_wallpaper_lux.jpg" class="thumb_link"><span class="selected"></span><img src="img/space/Universe_and_planets_digital_art_wallpaper_lux_thumb.jpg" title="Lux Aeterna" alt="Lux Aeterna" class="thumb" /></a>
            <a href="img/space/Universe_and_planets_digital_art_wallpaper_dk.jpg" class="thumb_link"><span class="selected"></span><img src="img/space/Universe_and_planets_digital_art_wallpaper_dk_thumb.jpg" title="X-Wing on patrol" alt="X-Wing on patrol" class="thumb" /></a>
            <a href="img/space/Universe_and_planets_digital_art_wallpaper_albireo.jpg" class="thumb_link"><span class="selected"></span><img src="img/space/Universe_and_planets_digital_art_wallpaper_albireo_thumb.jpg" title="Albireo Outpost" alt="Albireo Outpost" class="thumb" /></a>
            <a href="img/space/Universe_and_planets_digital_art_wallpaper_church.jpg" class="thumb_link"><span class="selected"></span><img src="img/space/Universe_and_planets_digital_art_wallpaper_church_thumb.jpg" title="Church of Heaven" alt="Church of Heaven" class="thumb" /></a>
            <a href="img/space/Universe_and_planets_digital_art_wallpaper_Decampment.jpg" class="thumb_link"><span class="selected"></span><img src="img/space/Universe_and_planets_digital_art_wallpaper_Decampment_thumb.jpg" title="Decampment" alt="Decampment" class="thumb" /></a>
            <a href="img/space/Universe_and_planets_digital_art_wallpaper_Hibernaculum.jpg" class="thumb_link"><span class="selected"></span><img src="img/space/Universe_and_planets_digital_art_wallpaper_Hibernaculum_thumb.jpg" title="Hibernaculum" alt="Hibernaculum" class="thumb" /></a>
            <a href="img/space/Universe_and_planets_digital_art_wallpaper_moons.jpg" class="thumb_link"><span class="selected"></span><img src="img/space/Universe_and_planets_digital_art_wallpaper_moons_thumb.jpg" title="Aurea Mediocritas" alt="Aurea Mediocritas" class="thumb" /></a>
            <a href="img/space/Universe_and_planets_digital_art_wallpaper_praedestinatio.jpg" class="thumb_link"><span class="selected"></span><img src="img/space/Universe_and_planets_digital_art_wallpaper_praedestinatio_thumb.jpg" title="Praedestinatio" alt="Praedestinatio" class="thumb" /></a>
            <a href="img/space/Universe_and_planets_digital_art_wallpaper_transitorius.jpg" class="thumb_link"><span class="selected"></span><img src="img/space/Universe_and_planets_digital_art_wallpaper_transitorius_thumb.jpg" title="Transitorius" alt="Transitorius" class="thumb" /></a>
            <a href="img/space/Universe_and_planets_digital_art_wallpaper_victimofgravity.jpg" class="thumb_link"><span class="selected"></span><img src="img/space/Universe_and_planets_digital_art_wallpaper_victimofgravity_thumb.jpg" title="Victim of Gravity" alt="Victim of Gravity" class="thumb" /></a>
            <a href="img/space/Universe_and_planets_digital_art_wallpaper_dk.jpg" class="thumb_link"><span class="selected"></span><img src="img/space/Universe_and_planets_digital_art_wallpaper_dk_thumb.jpg" title="X-Wing on patrol" alt="X-Wing on patrol" class="thumb" /></a>
            <a href="img/space/Universe_and_planets_digital_art_wallpaper_albireo.jpg" class="thumb_link"><span class="selected"></span><img src="img/space/Universe_and_planets_digital_art_wallpaper_albireo_thumb.jpg" title="Albireo Outpost" alt="Albireo Outpost" class="thumb" /></a>
            <a href="img/space/Universe_and_planets_digital_art_wallpaper_church.jpg" class="thumb_link"><span class="selected"></span><img src="img/space/Universe_and_planets_digital_art_wallpaper_church_thumb.jpg" title="Church of Heaven" alt="Church of Heaven" class="thumb" /></a>
            <a href="img/space/Universe_and_planets_digital_art_wallpaper_Decampment.jpg" class="thumb_link"><span class="selected"></span><img src="img/space/Universe_and_planets_digital_art_wallpaper_Decampment_thumb.jpg" title="Decampment" alt="Decampment" class="thumb" /></a>
            <a href="img/space/Universe_and_planets_digital_art_wallpaper_Hibernaculum.jpg" class="thumb_link"><span class="selected"></span><img src="img/space/Universe_and_planets_digital_art_wallpaper_Hibernaculum_thumb.jpg" title="Hibernaculum" alt="Hibernaculum" class="thumb" /></a>
            <a href="img/space/Universe_and_planets_digital_art_wallpaper_moons.jpg" class="thumb_link"><span class="selected"></span><img src="img/space/Universe_and_planets_digital_art_wallpaper_moons_thumb.jpg" title="Aurea Mediocritas" alt="Aurea Mediocritas" class="thumb" /></a>
            <a href="img/space/Universe_and_planets_digital_art_wallpaper_praedestinatio.jpg" class="thumb_link"><span class="selected"></span><img src="img/space/Universe_and_planets_digital_art_wallpaper_praedestinatio_thumb.jpg" title="Praedestinatio" alt="Praedestinatio" class="thumb" /></a>
            <a href="img/space/Universe_and_planets_digital_art_wallpaper_transitorius.jpg" class="thumb_link"><span class="selected"></span><img src="img/space/Universe_and_planets_digital_art_wallpaper_transitorius_thumb.jpg" title="Transitorius" alt="Transitorius" class="thumb" /></a>
            <a href="img/space/Universe_and_planets_digital_art_wallpaper_victimofgravity.jpg" class="thumb_link"><span class="selected"></span><img src="img/space/Universe_and_planets_digital_art_wallpaper_victimofgravity_thumb.jpg" title="Victim of Gravity" alt="Victim of Gravity" class="thumb" /></a>
            <a href="img/space/Universe_and_planets_digital_art_wallpaper_victimofgravity.jpg" class="thumb_link"><span class="selected"></span><img src="img/space/Universe_and_planets_digital_art_wallpaper_victimofgravity_thumb.jpg" title="Victim of Gravity" alt="Victim of Gravity" class="thumb" /></a>
            <a href="img/space/Universe_and_planets_digital_art_wallpaper_lucernarium.jpg" class="thumb_link"><span class="selected"></span><img src="img/space/Universe_and_planets_digital_art_wallpaper_lucernarium_thumb.jpg" title="Supremus Lucernarium" alt="Supremus Lucernarium" class="thumb" /></a>
            <a href="img/space/Universe_and_planets_digital_art_wallpaper_denebola.jpg" class="thumb_link"><span class="selected"></span><img src="img/space/Universe_and_planets_digital_art_wallpaper_denebola_thumb.jpg" title="Denebola" alt="Denebola" class="thumb" /></a>
            <a href="img/space/Universe_and_planets_digital_art_wallpaper_lux.jpg" class="thumb_link"><span class="selected"></span><img src="img/space/Universe_and_planets_digital_art_wallpaper_lux_thumb.jpg" title="Lux Aeterna" alt="Lux Aeterna" class="thumb" /></a>
            <a href="img/space/Universe_and_planets_digital_art_wallpaper_dk.jpg" class="thumb_link"><span class="selected"></span><img src="img/space/Universe_and_planets_digital_art_wallpaper_dk_thumb.jpg" title="X-Wing on patrol" alt="X-Wing on patrol" class="thumb" /></a>
            <a href="img/space/Universe_and_planets_digital_art_wallpaper_albireo.jpg" class="thumb_link"><span class="selected"></span><img src="img/space/Universe_and_planets_digital_art_wallpaper_albireo_thumb.jpg" title="Albireo Outpost" alt="Albireo Outpost" class="thumb" /></a>
            <a href="img/space/Universe_and_planets_digital_art_wallpaper_church.jpg" class="thumb_link"><span class="selected"></span><img src="img/space/Universe_and_planets_digital_art_wallpaper_church_thumb.jpg" title="Church of Heaven" alt="Church of Heaven" class="thumb" /></a>
            <a href="img/space/Universe_and_planets_digital_art_wallpaper_Decampment.jpg" class="thumb_link"><span class="selected"></span><img src="img/space/Universe_and_planets_digital_art_wallpaper_Decampment_thumb.jpg" title="Decampment" alt="Decampment" class="thumb" /></a>
            <a href="img/space/Universe_and_planets_digital_art_wallpaper_praedestinatio.jpg" class="thumb_link"><span class="selected"></span><img src="img/space/Universe_and_planets_digital_art_wallpaper_praedestinatio_thumb.jpg" title="Praedestinatio" alt="Praedestinatio" class="thumb" /></a>
            <a href="img/space/Universe_and_planets_digital_art_wallpaper_transitorius.jpg" class="thumb_link"><span class="selected"></span><img src="img/space/Universe_and_planets_digital_art_wallpaper_transitorius_thumb.jpg" title="Transitorius" alt="Transitorius" class="thumb" /></a>
            <a href="img/space/Universe_and_planets_digital_art_wallpaper_victimofgravity.jpg" class="thumb_link"><span class="selected"></span><img src="img/space/Universe_and_planets_digital_art_wallpaper_victimofgravity_thumb.jpg" title="Victim of Gravity" alt="Victim of Gravity" class="thumb" /></a>
            <p class="clear"></p>
            <p>Created by <a href="http://manos.malihu.gr" target="_blank">malihu</a> and his cats on a hot summer day.</p>
        </div>
	</div>
    <div id="dragger_container"><div id="dragger"></div></div>
</div>
</div>
<div id="bg">
    <img src="img/space/Universe_and_planets_digital_art_wallpaper_lucernarium.jpg" title="Supremus Lucernarium" id="bgimg" />
	<div id="preloader"><img src="img/ajax-loader_dark.gif" width="32" height="32" align="absmiddle" />LOADING...</div>
    <div id="arrow_indicator"><img src="img/sw_arrow_indicator.png" width="50" height="50"  /></div>
    <div id="nextimage_tip">Click for next image</div>
</div>
<script>
	//set default view mode
	$defaultViewMode="full"; //full (fullscreen background), fit (fit to window), original (no scale)
	//cache vars
	$bg=$("#bg");
	$bgimg=$("#bg #bgimg");
	$preloader=$("#preloader");
	$outer_container=$("#outer_container");
	$outer_container_a=$("#outer_container a.thumb_link");
	$toolbar=$("#toolbar");
	$nextimage_tip=$("#nextimage_tip");
	
$(window).load(function() {
	console.log('window loading');
	$toolbar.data("imageViewMode",$defaultViewMode); //default view mode
	ImageViewMode($toolbar.data("imageViewMode"));
	//cache vars
	$customScrollBox=$("#customScrollBox");
	$customScrollBox_container=$("#customScrollBox .container");
	$customScrollBox_content=$("#customScrollBox .content");
	$dragger_container=$("#dragger_container");
	$dragger=$("#dragger");
	
	CustomScroller();
	
	function CustomScroller(){
		outerMargin=0;
		innerMargin=20;
		$customScrollBox.height($(window).height()-outerMargin);
		$dragger_container.height($(window).height()-innerMargin);
		visibleHeight=$(window).height()-outerMargin;
		if($customScrollBox_container.height()>visibleHeight){ //custom scroll depends on content height
			console.log('dragging');
			$dragger_container,$dragger.css("display","block");
			totalContent=$customScrollBox_content.height();
			draggerContainerHeight=$(window).height()-innerMargin;
			animSpeed=400; //animation speed
			easeType="easeOutCirc"; //easing type
			bottomSpace=1.05; //bottom scrolling space
			targY=0;
			draggerHeight=$dragger.height();
			$dragger.draggable({ 
				axis: "y", 
				containment: "parent", 
				drag: function(event, ui) {
					Scroll();
				}, 
				stop: function(event, ui) {
					DraggerOut();
				}
			});

			//scrollbar click
			$dragger_container.click(function(e) {
				console.log('click');
				var mouseCoord=(e.pageY - $(this).offset().top);
				var targetPos=mouseCoord+$dragger.height();
				if(targetPos<draggerContainerHeight){
					$dragger.css("top",mouseCoord);
					Scroll();
				} else {
					$dragger.css("top",draggerContainerHeight-$dragger.height());
					Scroll();
				}
			});

			//mousewheel
			$(function($) {
				$customScrollBox.bind("mousewheel", function(event, delta) {
					vel = Math.abs(delta*10);
					$dragger.css("top", $dragger.position().top-(delta*vel));
					Scroll();
					if($dragger.position().top<0){
						$dragger.css("top", 0);
						$customScrollBox_container.stop();
						Scroll();
					}
					if($dragger.position().top>draggerContainerHeight-$dragger.height()){
						$dragger.css("top", draggerContainerHeight-$dragger.height());
						$customScrollBox_container.stop();
						Scroll();
					}
					return false;
				});
			});

			function Scroll(){
				var scrollAmount=(totalContent-(visibleHeight/bottomSpace))/(draggerContainerHeight-draggerHeight);
				var draggerY=$dragger.position().top;
				targY=-draggerY*scrollAmount;
				var thePos=$customScrollBox_container.position().top-targY;
				$customScrollBox_container.stop().animate({top: "-="+thePos}, animSpeed, easeType); //with easing
			}

			//dragger hover
			$dragger.mouseup(function(){
				DraggerOut();
			}).mousedown(function(){
				DraggerOver();
			});

			function DraggerOver(){
				$dragger.css("background", "url(img/round_custom_scrollbar_bg_over.png)");
			}

			function DraggerOut(){
				$dragger.css("background", "url(img/round_custom_scrollbar_bg.png)");
			}
		} else { //hide custom scrollbar if content is short
			$dragger,$dragger_container.css("display","none");
		}
	}

	//resize browser window functions
	$(window).resize(function() {
		FullScreenBackground("#bgimg"); //scale bg image
		$dragger.css("top",0); //reset content scroll
		$customScrollBox_container.css("top",0);
		$customScrollBox.unbind("mousewheel");
		CustomScroller();
	});
	
	LargeImageLoad($bgimg);
});
	
	//loading bg image
	$bgimg.load(function() {
		LargeImageLoad($(this));
	});
	
	function LargeImageLoad($this){
		$preloader.fadeOut("fast"); //hide preloader
		$this.removeAttr("width").removeAttr("height").css({ width: "", height: "" }); //lose all previous dimensions in order to rescale new image data
		$bg.data("originalImageWidth",$this.width()).data("originalImageHeight",$this.height());
		if($bg.data("newTitle")){
			$this.attr("title",$bg.data("newTitle")); //set new image title attribute
		}
		FullScreenBackground($this); //scale new image
		$bg.data("nextImage",$($outer_container.data("selectedThumb")).next().attr("href")); //get and store next image
		if(typeof itemIndex!="undefined"){
			if(itemIndex==lastItemIndex){ //check if it is the last image
				$bg.data("lastImageReached","Y");
				$bg.data("nextImage",$outer_container_a.first().attr("href")); //get and store next image
			} else {
				$bg.data("lastImageReached","N");
			}
		} else {
			$bg.data("lastImageReached","N");
		}
		$this.fadeIn("slow"); //fadein background image
		if($bg.data("nextImage") || $bg.data("lastImageReached")=="Y"){ //don't close thumbs pane on 1st load
			SlidePanels("close"); //close the left pane
		}
		NextImageTip();
	}

	//slide in/out left pane
	$outer_container.hover(
		function(){ //mouse over
			SlidePanels("open");
		},
		function(){ //mouse out
			SlidePanels("close");
		}
	);
	
	//Clicking on thumbnail changes the background image
	$outer_container_a.click(function(event){
		event.preventDefault();
		var $this=this;
		$bgimg.css("display","none");
		$preloader.fadeIn("fast"); //show preloader
		//style clicked thumbnail
		$outer_container_a.each(function() {
    		$(this).children(".selected").css("display","none");
  		});
		$(this).children(".selected").css("display","block");
		//get and store next image and selected thumb 
		$outer_container.data("selectedThumb",$this); 
		$bg.data("nextImage",$(this).next().attr("href")); 	
		$bg.data("newTitle",$(this).children("img").attr("title")); //get and store new image title attribute
		itemIndex=getIndex($this); //get clicked item index
		lastItemIndex=($outer_container_a.length)-1; //get last item index
		$bgimg.attr("src", "").attr("src", $this); //switch image
	}); 

	//clicking on large image loads the next one
	$bgimg.click(function(event){
		var $this=$(this);
		if($bg.data("nextImage")){ //if next image data is stored
			$this.css("display","none");
			$preloader.fadeIn("fast"); //show preloader
			$($outer_container.data("selectedThumb")).children(".selected").css("display","none"); //deselect thumb
			if($bg.data("lastImageReached")!="Y"){
				$($outer_container.data("selectedThumb")).next().children(".selected").css("display","block"); //select new thumb
			} else {
				$outer_container_a.first().children(".selected").css("display","block"); //select new thumb - first
			}
			//store new selected thumb
			var selThumb=$outer_container.data("selectedThumb");
			if($bg.data("lastImageReached")!="Y"){
				$outer_container.data("selectedThumb",$(selThumb).next()); 
			} else {
				$outer_container.data("selectedThumb",$outer_container_a.first()); 
			}
			$bg.data("newTitle",$($outer_container.data("selectedThumb")).children("img").attr("title")); //get and store new image title attribute
			if($bg.data("lastImageReached")!="Y"){
				itemIndex++;
			} else {
				itemIndex=0;
			}
			$this.attr("src", "").attr("src", $bg.data("nextImage")); //switch image
		}
	});
	
	//function to get element index (fuck you IE!)
	function getIndex(theItem){
		for ( var i = 0, length = $outer_container_a.length; i < length; i++ ) {
			if ( $outer_container_a[i] === theItem ) {
				return i;
			}
		}
	}
	
	//toolbar (image view mode button) hover
	$toolbar.hover(
		function(){ //mouse over
			$(this).stop().fadeTo("fast",1);
		},
		function(){ //mouse out
			$(this).stop().fadeTo("fast",0.8);
		}
	); 
	$toolbar.stop().fadeTo("fast",0.8); //set its original state
	
	//Clicking on toolbar changes the image view mode
	$toolbar.click(function(event){
		if($toolbar.data("imageViewMode")=="full"){
			ImageViewMode("fit");
		} else if($toolbar.data("imageViewMode")=="fit") {
			ImageViewMode("original");
		} else if($toolbar.data("imageViewMode")=="original"){
			ImageViewMode("full");
		}
	});

	//next image balloon tip
	function NextImageTip(){
		if($bg.data("nextImage")){ //check if this is the first image
			$nextimage_tip.stop().css("right",20).fadeIn("fast").fadeOut(2000,"easeInExpo",function(){$nextimage_tip.css("right",$(window).width());});
		}
	}

	//slide in/out left pane function
	function SlidePanels(action){
		var speed=900;
		var easing="easeInOutExpo";
		if(action=="open"){
			$("#arrow_indicator").fadeTo("fast",0);
			$outer_container.stop().animate({left: 0}, speed,easing);
			$bg.stop().animate({left: 585}, speed,easing);
		} else {
			$outer_container.stop().animate({left: -710}, speed,easing);
			$bg.stop().animate({left: 0}, speed,easing,function(){$("#arrow_indicator").fadeTo("fast",1);});
		}
	}

//Image scale function
function FullScreenBackground(theItem){
	var winWidth=$(window).width();
	var winHeight=$(window).height();
	var imageWidth=$(theItem).width();
	var imageHeight=$(theItem).height();
	if($toolbar.data("imageViewMode")!="original"){ //scale
		$(theItem).removeClass("with_border").removeClass("with_shadow"); //remove extra styles of orininal view mode
		var picHeight = imageHeight / imageWidth;
		var picWidth = imageWidth / imageHeight;
		if($toolbar.data("imageViewMode")!="fit"){ //image view mode: full
			if ((winHeight / winWidth) < picHeight) {
				$(theItem).css("width",winWidth).css("height",picHeight*winWidth);
			} else {
				$(theItem).css("height",winHeight).css("width",picWidth*winHeight);
			};
		} else { //image view mode: fit
			if ((winHeight / winWidth) > picHeight) {
				$(theItem).css("width",winWidth).css("height",picHeight*winWidth);
			} else {
				$(theItem).css("height",winHeight).css("width",picWidth*winHeight);
			};
		}
		//center it
		$(theItem).css("margin-left",((winWidth - $(theItem).width())/2)).css("margin-top",((winHeight - $(theItem).height())/2));
	} else { //no scale
		//add extra styles for orininal view mode
		$(theItem).addClass("with_border").addClass("with_shadow");
		//set original dimensions
		$(theItem).css("width",$bg.data("originalImageWidth")).css("height",$bg.data("originalImageHeight"));
		//center it
		$(theItem).css("margin-left",((winWidth-$(theItem).outerWidth())/2)).css("margin-top",((winHeight-$(theItem).outerHeight())/2));
	}
}

//image view mode function - full or fit
function ImageViewMode(theMode){
	$toolbar.data("imageViewMode", theMode); //store new mode
	FullScreenBackground($bgimg); //scale bg image
	//re-style button
	if(theMode=="full"){
		$toolbar.html("<span class='lightgrey'>IMAGE VIEW MODE &rsaquo;</span> FULL");
	} else if(theMode=="fit") {
		$toolbar.html("<span class='lightgrey'>IMAGE VIEW MODE &rsaquo;</span> FIT");
	} else {
		$toolbar.html("<span class='lightgrey'>IMAGE VIEW MODE &rsaquo;</span> ORIGINAL");
	}
}

//preload script images
var images=["img/ajax-loader_dark.gif","img/round_custom_scrollbar_bg_over.png"];
$.each(images, function(i) {
  images[i] = new Image();
  images[i].src = this;
});
</script>
</body>

</html>