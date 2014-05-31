var parametersUrl= "data/parameters";
var variablesUrl= "data/variables";
var comparisonUrl= "data/comparison";
var comparisonGetKey= "vars";
var infoUrl= "data/info";

var accordionOptions= {collapsible: true,
		       active: false,
		       heightStyle: "content"}
var preferredFilters= [
        'Data Source', 'Parameter',
        'Project', 'Spatial Resolution',
        'Temporal Resolution', 'Location'];

var getParametersContent;
var getVariablesContent;
var getComparisonContent;
var getInfoContent;

var currentParameter;
var currentFilters= [];
var currentKeyword= "";

$(document).ready(setup);

function setup() {
    var offset= 220;
    var duration= 500;
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
    setTemplates();
    renderInfoIfRequested("variable"); // Must match "infolink" class in index.html.
    renderInfoIfRequested("uuid"); // Must match "infolink" class in index.html.
    renderComparisonIfRequested(comparisonGetKey);
    $('#category-instructions').show();
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
    // Sort filters before displaying.
    // Ideally the back-end would provide us a sorted array to begin with.
    for (var i= 0; i < data.parameters.length; i++) {
        var params= data.parameters[i];
        // Check for empty object per http://stackoverflow.com/a/2866221.
        if (!$.isEmptyObject(params.filters)) {
            var sorted= objectToSortedArray(params.filters, 'name', preferredFilters, false);
            params.filters= liftPreferred(sorted, 'name', preferredFilters);
        }
    }

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
	$('#right-content-data').empty(); 
	currentParameter= $(this).attr("uuid");
	var opening= $(this).hasClass('ui-state-active');
	if (opening) { 
        $('#category-instructions').hide();
	    currentFilters= [];
	    getParameters({"id": currentParameter});
	    getVariables({"id":currentParameter}); 
	}
	else {
        $('#category-instructions').show();
	    currentParameter= null;
	}
    });
    $("#compare").click(openComparisonWindow);
}
function liftPreferred(array, key, preferredValues) {
    // Given an array of objects, move to the front the objects whose
    // value accessed by the given key is among the preferred values,
    // in the same order as the preferred values.

    var preferred= [];
    var others= $.extend(true, [], array); // Deep copy per http://stackoverflow.com/a/122704.
    for (var i= 0; i < preferredValues.length; i++) {
        for (var j= 0; j < others.length; j++) {
            if (preferredValues[i] == others[j][key]) {
                preferred.push(others[j]);
                others.splice(j, 1); // Pop from middle per http://stackoverflow.com/a/5767357.
                break;
            }
        }
    }
    return preferred.concat(others);
}
function objectToSortedArray(object, key, preferredValues, reverse) {
    // Convert unsortable object to sortable array per http://stackoverflow.com/a/1069840.
    var sortable= [];
    for (var k in object) {
        if (object.hasOwnProperty(k)) {
            sortable.push(object[k]);
        }
    }

    // Sort by property per http://stackoverflow.com/q/5073799.
    reverse= reverse ? -1 : 1;
    sortable.sort(function (a, b) {
        // Case-insensitive sort per http://stackoverflow.com/a/8996984.
        var lowerA= a[key].toLowerCase();
        var lowerB= b[key].toLowerCase();
        if (lowerA < lowerB) {
            return -1 * reverse;
        } else if (lowerA < lowerB) {
            return 1 * reverse;
        } else {
            return 0;
        }
    });

    return sortable;
}
function getCurrentParamIndex(data) {
    var params= $.map(data["parameters"], function(x) { return x["parameter"]; });
    return $.inArray(currentParameter, params);
}
function getVariables(json) {
    $(".filterValues input[type='checkbox']").attr('checked', false);
    $('#right-content-data').empty(); 
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
    $('#right-content-data').html(getVariablesContent(data));
    activateVarTableAccordion();
    $('#search-form').show();
    $('#search-box').val('');
    $('#keyword-search').unbind().click(keywordSearch);
}
function activateVarTableAccordion() {
    $("#content-table > tbody > tr:not(.variableName)").hide();
    $("#content-table tr:first-child").show();
    $("#content-table tr.variableName").click(function(){
	$(this).next().fadeToggle();
	toggleArrow($(this).find('.variable-arrow'));
    });
}
function toggleArrow(arrow) {
    // Code points per http://stackoverflow.com/a/5853994.
    var RIGHT_ARROW_UNICODE= '\u25B6'; // Not used.
    var DOWN_ARROW_UNICODE= '\u25BC';
    var DOWN_ARROW_HTML= '&#9660;';
    var RIGHT_ARROW_HTML= '&#9658;';

    // Compare Unicode per http://stackoverflow.com/a/30020.
    if (arrow.html() !== DOWN_ARROW_UNICODE) {
        arrow.html(DOWN_ARROW_HTML);
    } else {
        arrow.html(RIGHT_ARROW_HTML);
    }
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
function openComparisonWindow() {
    var selectedVars= $(".variable input[type='checkbox']:checked")
	.map(function () { return this.value; }).get();
    if (selectedVars.length > 0) {
       // Valid URL per http://stackoverflow.com/a/7872230.
       window.open("?" + comparisonGetKey + "=" + selectedVars.join(","))
    }
}
function getComparison(selectedVars) {
    $.getJSON(comparisonUrl, {"vars": selectedVars}, setComparison);
}
function setComparison(data) {
    // Replace content of entire page per http://stackoverflow.com/a/4292640.
    document.write(getComparisonContent(transposeAndSimplify(data)));
    document.close();
    return false;
}
function transposeAndSimplify(data) {
    // Transpose the data and strip out the unnecessary
    // parts for easier loading into the HTML table, which
    // is built row-by-row, not column-by-column.
    var relations = [];
    relations.push(data.variables[0].quickFacts[0].relation);
    for (var i = 0; i < data.variables.length; i++) {
        relations.push(data.variables[i].name);
    }
    var quickFacts = [];
    for (var i = 0; i < data.variables[0].quickFacts.length; i++) {
        if (i === 0) {
            continue;  // Already added above.
        }
        var variables = [];
        for (var j = 0; j < data.variables.length; j++ ) {
            variables.push(data.variables[j].quickFacts[i].value);
        }
        quickFacts.push({ relation: [data.variables[0].quickFacts[i].relation],
                          variables: variables });
    }
    var transposed = { relations: relations,
                       quickFacts: quickFacts };
    return transposed;
}
function getInfo(item) {
    $.getJSON(infoUrl+"/"+item, setInfo);
}
function setInfo(data) {
    var head= getInfoContent(data);
    var scripts= "<script src='js/jquery-1.8.3.js'></script>"+
	"<script src='js/jquery-ui-1.9.2.custom.js'></script>"+ 
	"<script src='js/handlebars.js'></script>"+
	"<script id='info-template' type='text/x-handlebars-template'>"+
	$('#info-template').html()+"</script>";
    var tail= "</body></html>";
    var content= head+scripts+tail;
    // Replace content of entire page per http://stackoverflow.com/a/4292640.
    document.write(content);
    document.close();
    return false;
}
function renderInfoIfRequested(key) {
    var val= getQueryVariable(key);
    if (val) {
        getInfo(val);
    }
}
function renderComparisonIfRequested(key) {
    var val= getQueryVariable(key);
    if (val) {
        var selectedVars= val.split(",");
        getComparison(selectedVars);
    }
}
// Per http://stackoverflow.com/q/901115/#comment17845348_901115.
function getQueryVariable(variable) {
    var query= window.location.search.substring(1);
    var vars= query.split("&");
    for (var i= 0; i < vars.length; i++) {
        var pair= vars[i].split("=");
        if (pair[0] === variable) {
            return pair[1];
        }
    }
    return false;
}


