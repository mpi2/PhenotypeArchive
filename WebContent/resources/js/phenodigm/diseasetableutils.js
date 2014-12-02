/* Formatting function for row details*/
function makeChildRow(clicked) {

    var targetRowId = $(clicked).attr("targetRowId");
    var geneId = $(clicked).attr("geneid");
    var diseaseId = $(clicked).attr("diseaseid");
    var requestPageType = $(clicked).attr("requestpagetype");

//                console.log('Row ' + targetRowId + ' clicked');
    var formatted = '<table cellpadding="4" cellspacing="0" border="0">' +
            '<tr id="' + targetRowId + '" geneid="' + geneId + '" diseaseid="' + diseaseId + '" requestpagetype="' + requestPageType + '">' +
            '<td id="loadingPlaceholder" colspan="4"><i class="fa fa-spinner fa-spin"></i></td>' +
            '</tr>' +
            '</table>';
//                console.log(formatted);
    return formatted;
}


function insertDiseaseAssociations(clicked) {

    var targetRowId = $(clicked).attr("targetRowId");
    var targetRow = $('#' + targetRowId);
    var geneId = $(clicked).attr("geneid");
    var diseaseId = $(clicked).attr("diseaseid");
    var requestPageType = $(clicked).attr("requestpagetype");
//                console.log(requestPageType + " page getDiseaseAssociations for: " + geneId + " " + diseaseId);

    var uri = baseUrl + '/phenodigm/diseaseGeneAssociations';
    $.get(uri, {
        geneId: geneId,
        diseaseId: diseaseId,
        requestPageType: requestPageType
    }, function (response) {
//                    console.log(response);
        //add the response html to the target row
        $(targetRow).remove('#loadingPlaceholder').html(response);
    });
}
;