var feedback = function () {
    "use strict";

    var reportAccepted = function (acceptedIds, data) {


        window.java.reportAccepted(acceptedIds, data.map(function (a) {
            return JSON.stringify(a);
        }));

    };

    var getSubstitutions = function (medication, nihiiOrg, result, hook) {
        var request = $.ajax({
            url: "/vitalinkwebservice/api/v1/substitute-medication/JSON/" + nihiiOrg,
            type: "POST",
            data: JSON.stringify(medication),
            dataType: "json",
            contentType: "application/json"
        });

        request.done(function (msg) {
            hook.push(medication);
            for (var i = 0; i < msg.medicationList.length; i++) {
                $("<option value=\"" + (i + 1) + "\">" + msg.medicationList[i].medicationDescription + "</option>").appendTo(result);
                hook.push(msg.medicationList[i]);
            }


        });

        request.fail(function (jqXHR, textStatus) {
            alert("Request failed: " + textStatus);
        });
    };
    return {reportAccepted: reportAccepted, getSubstitutions: getSubstitutions};
}();