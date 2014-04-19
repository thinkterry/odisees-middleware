var infoUrl= "data/info";
var getInfoContent;

$(document).ready(setup);

function setup() {
    var infoTemplate= $('#info-template').html();
    getInfoContent= Handlebars.compile(infoTemplate);
    $('a.infolink').unbind().click(infoLinkClick);
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
