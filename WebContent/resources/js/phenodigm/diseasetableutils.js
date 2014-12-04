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
};

/* Adds on-click functionality to the table to insert and show/hide a child row 
for the row clicked.*/
$.fn.addTableClickCallbackHandler = function (tableId, table) {
    console.log(tableId);
    $(tableId + ' tbody').on('click', 'tr', function () {
        var tr = $(this).closest('tr');
        var row = table.row(tr);
        
        if (row.child.isShown()) {
            // This row is already open - close it
            row.child.hide();
            tr.removeClass('shown');
            tr.find("td#toggleButton i").removeClass("fa-minus-square").addClass("fa-plus-square");
        }
        else {
            // Open this row
            row.child(makeChildRow(tr)).show();
            row.child(insertDiseaseAssociations(tr)).show();
            tr.addClass('shown');
            tr.find("td#toggleButton i").removeClass("fa-plus-square").addClass("fa-minus-square");
        }
    });
};