var parametersUrl= "data/parameters";
var variablesUrl= "data/variables";
var comparisonUrl= "data/comparison";
var infoUrl= "data/info";

var accordionOptions= {collapsible: true,
		       active: false,
		       heightStyle: "content"}

var getParametersContent;
var getVariablesContent;
var getComparisonContent;
var getInfoContent;

var currentParameter;
var currentFilters= [];
var currentKeyword= "";

$(document).ready(setup);

function setup() {
    var offset = 220;
    var duration = 500;
    jQuery(window).scroll(function() {
	if (jQuery(this).scrollTop() > offset) {
	    jQuery('.back-to-top').fadeIn(duration);
	} else {
	    jQuery('.back-to-top').fadeOut(duration);
	}
    });
    
    $(window).bind('keypress', interceptEnterKey);
    jQuery('.back-to-top').click(function(event) {
	event.preventDefault();
	jQuery('html, body').animate({scrollTop: 0}, duration);
	return false;
    });
    $(".instruction").hide();
    $(".show_hide").show();    
    $('.show_hide').click(function(){
	$(".instruction").fadeToggle();
    });
    setTemplates();
}
function interceptEnterKey(e) {
   if ( e.keyCode == 13 ) {
       keywordSearch();
       e.preventDefault();
   }
}
function setTemplates() {
    var paramsTemplate= $('#params-template').html();
    var varsTemplate= $('#vars-template').html();
    var compareTemplate= $('#compare-template').html();
    var infoTemplate= $('#info-template').html();
    getParametersContent= Handlebars.compile(paramsTemplate);
    getVariablesContent= Handlebars.compile(varsTemplate);
    getComparisonContent= Handlebars.compile(compareTemplate);
    getInfoContent= Handlebars.compile(infoTemplate);
    getParameters({});
}
function getParameters(params) { 
    $.getJSON(parametersUrl, params, setParameters); 
}
function setParameters(data) {    
    $('#left-nav').html(getParametersContent(data));
    $('#accordion').unbind();
    $('#accordion h3').unbind();
    var options= accordionOptions;
    if (currentParameter != null) {
	options["active"]= getCurrentParamIndex(data);
    }
    $('#accordion').accordion(accordionOptions);
    $.each(currentFilters, function(i,v) {
	$('#'+v).attr('checked', 'checked'); 
    });
    $('#search-box').val(currentKeyword);
    $(".filterValues input[type='checkbox']").click(keywordSearch);
    $('#accordion h3').click(function() {
	currentKeyword= "";
	$('#search-box').val(currentKeyword);
	$('#right-content').empty(); 
	currentParameter= $(this).attr("uuid");
	var opening= $(this).hasClass('ui-state-active');
	if (opening) { 
	    currentFilters= [];
	    getParameters({"id": currentParameter});
	    getVariables({"id":currentParameter}); 
	}
	else {
	    currentParameter= null;
	}
    });
    $("#compare").click(getComparison);
}
function getCurrentParamIndex(data) {
    var params= $.map(data["parameters"], function(x) { return x["parameter"]; });
    return $.inArray(currentParameter, params);
}
function getVariables(json) {
    $(".filterValues input[type='checkbox']").attr('checked', false);
    $('#right-content').empty(); 
    $.getJSON(variablesUrl, json).done(setVariableNames);
}
function keywordSearch() {
    $('#vars').empty(); 
    var params= filterMap();
    currentKeyword= $('#search-box').val();
    params["id"]= currentParameter;
    params["keyword"]= currentKeyword;
    getParameters(params);
    getVariables(params);
}
function setVariableNames(data) {
    filterIndex= data.filterIndex;
    $('#compare').show();
    $('#right-content').html(getVariablesContent(data));
    activateVarTableAccordion();
    $('a.infolink').unbind().click(infoLinkClick);
    $('#search-form').show();
    $('#search-box').val('');
    $('#keyword-search').unbind().click(keywordSearch);
}
function activateVarTableAccordion() {
    $("#content-table > tbody > tr:not(.variableName)").hide();
    $("#content-table tr:first-child").show();
    $("#content-table tr.variableName").click(function(){
	$(this).next().fadeToggle();
    });
}
function filterMap() {
    currentFilters= [];
    var items=$(".filterValues input[type='checkbox']:checked")
	.map(function () { return $(this).val(); }).get();
    var X= {};
    if (items.length > 0) {
	$.each(items, function(j, item) { 
	    currentFilters.push(item);
	    var i= item.split('---');
	    var theFilter= i[0];
	    var theFilterValue= i[1];
	    X[theFilter]= theFilterValue;
	});	
    }
    return X;
}
function getComparison() {
    var selectedVars= $(".variable input[type='checkbox']:checked")
	.map(function () { return this.value; }).get();
    if (selectedVars.length > 0) {
	$.getJSON(comparisonUrl, {"vars": selectedVars}, setComparison); 
    }
}
function setComparison(data) {
    var w= window.open('', '', 'height=500, width='+screen.width);
    w.document.open();
    w.document.write(getComparisonContent(data));
    w.document.close();
    return false;
}
function infoLinkClick(e) {
    e.preventDefault();
    getInfo($(this).attr('uuid'));
}
function getInfo(item) {
    $.getJSON(infoUrl+"/"+item, setInfo);
}
function setInfo(data) {
    var w =  window.open('','','width=450,height=400');
    var head= getInfoContent(data);
    var scripts= "<script src='js/jquery-1.8.3.js'></script>"+
	"<script src='js/jquery-ui-1.9.2.custom.js'></script>"+ 
	"<script src='js/handlebars.js'></script>"+
	"<script src='js/popup.js'></script> "+
	"<script id='info-template' type='text/x-handlebars-template'>"+
	$('#info-template').html()+"</script>";
    var tail= "</body></html>";
    var content= head+scripts+tail;
    w.document.open();    
    w.document.write(content);
    w.document.close();
    return false;
}


