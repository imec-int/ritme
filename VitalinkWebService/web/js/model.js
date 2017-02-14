var model = function (model, jQuery) {
    "use strict";
    ////// visible ///////////////////////////////////////////////////////////
    var visible = {};

    // pseudo constants //////////////////////////////////////////////////
    visible.ATTR_NAME = "__ATTR_NAME__";
    visible.ATTR_GENERIC_NAME = "__ATTR_GENERIC_NAME__";
    visible.ATTR_RECORDED_NAME = "__ATTR_RECORDED_NAME__";
    visible.ATTR_BRAND_NAME = "__ATTR_BRAND_NAME__";
    visible.ATTR_ROUTE = "__ATTR_ROUTE__";
    visible.ATTR_FREQUENCY = "__ATTR_FREQUENCY__";
    visible.ATTR_DOSE = "__ATTR_DOSE__";
    visible.ATTR_DRUG_CLASS = "__ATTR_DRUG_CLASS__";
    visible.ATTR_DIAGNOSES = "__ATTR_DIAGNOSES__";
    visible.ATTR_SUBITEM = "__ATTR_SUBITEM__";
    visible.ATTR_DATE_STARTED = "__ATTR_DATE_STARTED__";
    visible.ATTR_INSTRUCTIONS = "__ATTR_INSTRUCTIONS__";
    visible.ATTR_FULLDATA = "__ATTR_FULLDATA__";
    visible.ATTR_VALIDATOR = "__ATTR_VALIDATOR__";
    visible.ATTR_ORG = "__ATTR_ORG__";

    visible.ATTR_TYPE_NUMERIC = "__ATTR_TYPE_NUMERIC__";
    visible.ATTR_TYPE_GENERAL = "__ATTR_TYPE_GENERAL__";
    visible.ATTR_TYPE_CATEGORICAL = "__ATTR_TYPE_CATEGORICAL__";
    visible.ATTR_TYPE_OBJECT = "__ATTR_TYPE_OBJECT__";

    // config - dataset
    visible.DATASET_APP = "__DATASET_APPENDECTOMY__";
    visible.DATASET_CHF1 = "__DATASET_CONGESTIVE_HEART_FAILURE_1__";
    visible.DATASET_CHF2 = "__DATASET_CONGESTIVE_HEART_FAILURE_2__";
    visible.DATASET_PD1 = "__DATASET_PULMONARY_DISEASE_1__";
    visible.DATASET_PD2 = "__DATASET_PULMONARY_DISEASE_2__";
    visible.DATASET_OTHER_SIMPLE = "__DATASET_OTHER_SIMPLE__";
    visible.DATASET_OTHER_COMPLEX = "__DATASET_OTHER_COMPLEX__";
    visible.DATASET_OTHER_EXTRA = "__DATASET_OTHER_EXTRA__";
    visible.DATASET_PD2_CORRECTED = "__DATASET_PULMONARY_DISEASE_2_CORRECTED__";
    visible.DATASET_CHF1_MODIFIED = "__DATASET_CONGESTIVE_HEART_FAILURE_1_MODIFIED__";
    visible.DATASET_KWS = "__DATASET_KWS__";
    visible.DATASET_DEFAULT = "__DATASET_KWS__";

    visible.RECORDED_NAME = "recorded";
    visible.GENERIC_NAME = "generic";
    visible.BRAND_NAME = "brand";

    visible.AFTER_ACTION_GRAYOUT = "__AFTER_ACTION_GRAYOUT__";
    visible.AFTER_ACTION_REMOVE = "__AFTER_ACTION_REMOVE__";

    visible.FILTER_DELAY_SCALE = 4;

    var GENERIC_FORMATTER = {

        _object: {
            toHTML: function (id, value) {
                var result = "<div id='" + id + "'>";
                for (var field in value) {

                    if (value.hasOwnProperty(field)) {
                        if (visible.ITEM_FORMATTER[field]) {
                            result += visible.ITEM_FORMATTER[field].toHTML(id + "-" + field, value[field]);
                        }
                        else {
                            result += "<label for='" + id + "-" + field + "'>" + field + "</label><input id='" + id + "-" + field + "' name='" + id + "-" + field + "' value='" + value[field] + "'>";
                        }
                    }
                }
                result += "</div>";
                return result;
            }
        },

        date: {
            toHTML: function (id, value, label) {
                return "<label for='" + id + "_display'>" + label + "</label><input id='" + id + "_display' value='" + new Date(value).toLocaleDateString() + "'><input id='" + id + "' name='" + id + "' value='" + value + "' type='hidden'>";

            },
            postAdd: function (id, value) {
                $("#" + id + "_display").datepicker({
                    altField: "#" + id,
                    altFormat: "!"
                });
                $("#" + id + "_display").datepicker("setDate", new Date(Number($("#" + id).val())));
            }
        },
        time: {
            toHTML: function (id, value, label) {
                return "<label for='" + id + "_display'>" + label + "</label><input id='" + id + "_display' value='" + new Date(value).toLocaleTimeString() + "' class='timepicker'><input id='" + id + "' name='" + id + "' value='" + value + "' type='hidden'>";

            },
            postAdd: function (id, value) {
                var options = {
                    timeFormat: 'HH:mm:ss',
                    // year, month, day and seconds are not important
                    minTime: new Date(0, 0, 0, 0, 0, 0),
                    maxTime: new Date(0, 0, 0, 23, 50, 0),
                    // time entries start being generated at 6AM but the plugin
                    // shows only those within the [minTime, maxTime] interval
                    startHour: 6,
                    // the value of the first item in the dropdown, when the input
                    // field is empty. This overrides the startHour and startMinute
                    // options
                    startTime: new Date(Number($("#" + id).attr("value"))),
                    // items in the dropdown are separated by at interval minutes
                    interval: 10,
                    defaultTime: new Date(Number($("#" + id).attr("value"))),
                    change: function (time) {
                        $("#" + id).attr("value", time.getTime());

                    }
                };


                $("#" + id + "_display").timepicker(options);

                $("#" + id + "_display").on('focus', function () {
                    $("div.ui-timepicker-container").zIndex($("#" + id + "_display").zIndex() + 1);
                });


            }
        }
    };

    visible.ITEM_FORMATTER = {
        source: {
            toHTML: function (id, value) {
                return "<label for='" + id + "'>" + "Herkomst:" + "</label><input id='" + id + "' name='" + id + "' value='" + value + "'>";
            }
        },
        registrationDate: {
            toHTML: function (id, value) {
                return GENERIC_FORMATTER.date.toHTML(id, value, "Registratiedatum:");

            },
            postAdd: function (id, value) {
                return GENERIC_FORMATTER.date.postAdd(id, value);
            }
        },
        startDate: {
            toHTML: function (id, value) {
                return GENERIC_FORMATTER.date.toHTML(id, value, "Startdatum:");

            },
            postAdd: function (id, value) {
                return GENERIC_FORMATTER.date.postAdd(id, value);
            }
        },
        stopDate: {
            toHTML: function (id, value) {
                return GENERIC_FORMATTER.date.toHTML(id, value, "Einddatum:");

            },
            postAdd: function (id, value) {
                return GENERIC_FORMATTER.date.postAdd(id, value);
            }
        },
        time: {
            toHTML: function (id, value) {
                return GENERIC_FORMATTER.time.toHTML(id, value, "Uur:");
            },
            postAdd: function (id, value) {
                return GENERIC_FORMATTER.time.postAdd(id, value);
            }
        },
        intendedMedication: {
            toHTML: function (id, value) {
                var result = "<div id='" + id + "'>";
                result += "<input id='" + id + "-medicationId' name='" + id + "-medicationId' value='" + value.medicationId + "' type='hidden'>";
                result += "<input id='" + id + "-medicationIdType' name='" + id + "-medicationIdType' value='" + value.medicationIdType + "' type='hidden'>";
                result += "<input id='" + id + "-medicationDescription' name='" + id + "-medicationDescription' value='" + value.medicationDescription + "' type='hidden'>";
                result += "<input id='" + id + "-magistralText' name='" + id + "-magistralText' value='" + value.magistralText + "' type='hidden'>";
                result += "<div>";
                result += "<div class=\"ui-widget\">" +
                    "<label>Medicatie: </label>" +
                    "<select id=\"" + id + "_combobox\">" +
                    "<option value=\"0\">" + value.medicationDescription + "</option>" +

                    "</select>" +
                    "</div>";

                result += "</div>";
                result += "</div>";
                return result;
            },
            postAdd: function (id, value) {
                var items = [];

                (function ($) {
                    $.widget("custom.combobox", {
                        _create: function () {
                            this.wrapper = $("<span>")
                                .addClass("custom-combobox")
                                .insertAfter(this.element);

                            this.element.hide();
                            this._createAutocomplete();
                            this._createShowAllButton();
                        },

                        _createAutocomplete: function () {
                            var selected = this.element.children(":selected"),
                                value = selected.val() ? selected.text() : "";

                            this.input = $("<input>")
                                .appendTo(this.wrapper)
                                .val(value)
                                .attr("title", "")
                                .addClass("custom-combobox-input ui-widget ui-widget-content ui-state-default ui-corner-left")
                                .autocomplete({
                                    delay: 0,
                                    minLength: 0,
                                    source: $.proxy(this, "_source")
                                })
                                .tooltip({
                                    tooltipClass: "ui-state-highlight"
                                });

                            this._on(this.input, {
                                autocompleteselect: function (event, ui) {
                                    ui.item.option.selected = true;
                                    this._trigger("select", event, {
                                        item: ui.item.option
                                    });
                                },

                                autocompletechange: "_removeIfInvalid"
                            });
                        },

                        _createShowAllButton: function () {
                            var input = this.input,
                                wasOpen = false;

                            $("<a>")
                                .attr("tabIndex", -1)
                                .attr("title", "Show All Items")
                                .tooltip()
                                .appendTo(this.wrapper)
                                .button({
                                    icons: {
                                        primary: "ui-icon-triangle-1-s"
                                    },
                                    text: false
                                })
                                .removeClass("ui-corner-all")
                                .addClass("custom-combobox-toggle ui-corner-right")
                                .mousedown(function () {
                                    wasOpen = input.autocomplete("widget").is(":visible");
                                })
                                .click(function () {
                                    input.focus();

                                    // Close if already visible
                                    if (wasOpen) {
                                        return;
                                    }

                                    // Pass empty string as value to search for, displaying all results
                                    input.autocomplete("search", "");
                                });
                        },

                        _source: function (request, response) {
                            var matcher = new RegExp($.ui.autocomplete.escapeRegex(request.term), "i");
                            response(this.element.children("option").map(function () {
                                var text = $(this).text();
                                if (this.value && ( !request.term || matcher.test(text) ))
                                    return {
                                        label: text,
                                        value: text,
                                        option: this
                                    };
                            }));
                        },

                        _removeIfInvalid: function (event, ui) {

                            // Selected an item, nothing to do
                            if (ui.item) {
                                return;
                            }

                            // Search for a match (case-insensitive)
                            var value = this.input.val(),
                                valueLowerCase = value.toLowerCase(),
                                valid = false;
                            this.element.children("option").each(function () {
                                if ($(this).text().toLowerCase() === valueLowerCase) {
                                    this.selected = valid = true;
                                    return false;
                                }
                            });

                            // Found a match, nothing to do
                            if (valid) {
                                return;
                            }

                            // Remove invalid value
                            this.input
                                .val("")
                                .attr("title", value + " didn't match any item")
                                .tooltip("open");
                            this.element.val("");
                            this._delay(function () {
                                this.input.tooltip("close").attr("title", "");
                            }, 2500);
                            this.input.autocomplete("instance").term = "";
                        },

                        _destroy: function () {
                            this.wrapper.remove();
                            this.element.show();
                        }
                    });
                })(jQuery);


                $("#" + id + "_combobox").on('comboboxselect', function () {
                    var index = $("#" + id + "_combobox").val();
                    if (items.length > 0) {
                        $("#" + id + "-medicationId").val(items[index].medicationId);
                        $("#" + id + "-medicationIdType").val(items[index].medicationIdType);
                        $("#" + id + "-medicationDescription").val(items[index].medicationDescription);
                        $("#" + id + "-magistralText").val(items[index].magistralText);
                    }
                });

                $("#" + id + "_combobox").on('select', function () {
                    var index = $("#" + id + "_combobox").val();
                    if (items.length > 0) {
                        $("#" + id + "-medicationId").val(items[index].medicationId);
                        $("#" + id + "-medicationIdType").val(items[index].medicationIdType);
                        $("#" + id + "-medicationDescription").val(items[index].medicationDescription);
                        $("#" + id + "-magistralText").val(items[index].magistralText);
                    }
                });

                feedback.getSubstitutions(value, visible.nihiiOrg, $("#" + id + "_combobox"), items);

                $(function () {
                    $("#" + id + "_combobox").combobox();

                });
            }
        },
        regimenItems: {
            toHTML: function (id, value) {
                var result = "";
                var index;
                for (index = 0; index < value.length; index++) {
                    result += "<div id='" + id + "-" + index + "'>";
                    for (var field in value[index]) {


                        if (value[index].hasOwnProperty(field)) {
                            if (visible.ITEM_FORMATTER[field]) {
                                result += visible.ITEM_FORMATTER[field].toHTML(id + "-" + index + "-" + field, value[index][field]);
                            }
                            else {
                                result += "<label for='" + id + "-" + index + "-" + field + "'>" + field + "</label><input id='" + id + "-" + index + "-" + field + "' name='" + id + "-" + index + "-" + field + "' value='" + value[index][field] + "'>";
                            }

                        }
                    }
                    result += "</div>";

                }
                return result;
            },

            postAdd: function (id, value) {
                var index;
                for (index = 0; index < value.length; index++) {
                    for (var field in value[index]) {


                        if (value[index].hasOwnProperty(field)) {
                            if (visible.ITEM_FORMATTER[field] && visible.ITEM_FORMATTER[field].postAdd) {
                                visible.ITEM_FORMATTER[field].postAdd(id + "-" + index + "-" + field, value[index][field]);
                            }
                        }
                    }
                }
            }

        }


    }
    ;

    // data //////////////////////////////////////////////////////////////
    visible.patientFirstName = "";
    visible.patientLastName = "";
    visible.patientAge = 0;
    visible.patientGender = "";
    visible.nihiiOrg = "";

    visible.dataset = "";
    visible.items = {};

    // diagnoses for items
    visible.diagnoses = {};

    // relationship between items and diagnoses
    visible.diagnosisSet = {};


    // drug classes for the current dataset
    visible.drugClasses = {};
    visible.drugClassSet = {};

    visible.shadows = {};
    visible.itemsToShadows = {};
    visible.shadowsToItems = {};
    visible.hidden = {};

    visible.list1 = {
        id: "list0",
        name: "Therapie",
        source: []
    };
    visible.list2 = {
        id: "list1",
        name: "Vitalink",
        source: []
    };

    visible.getDatasetShortName = function (dataset) {
        switch (dataset) {
            case visible.DATASET_APP:
                return "APPNDCTMY";
            case visible.DATASET_CHF1:
                return "CHF1";
            case visible.DATASET_CHF2:
                return "CHF2";
            case visible.DATASET_PD1:
                return "PD1";
            case visible.DATASET_PD2:
                return "PD2";
            case visible.DATASET_OTHER_SIMPLE:
                return "O_SIMPLE";
            case visible.DATASET_OTHER_COMPLEX:
                return "O_COMPLEX";
            case visible.DATASET_OTHER_EXTRA:
                return "O_EXTRA";
            case visible.DATASET_PD2_CORRECTED:
                return "PD2_C";
            case visible.DATASET_CHF1_MODIFIED:
                return "CHF1_M";
        }
        return undefined;
    };

    /*
     * Create and return viewData object (contains information on what to display)
     *
     * Arguments:
     *     boolean sort - whether to sort data
     *     boolean filter - whether to filter data
     *
     * Preconditions:
     *     visible.multigroup - indicates whether to include shadows
     *     visible.groupBy - indicates what (if any) grouping to use
     *     visible.shadows - shadows populated (e.g. during loadData)
     *
     *     visible.unique1 - contains ids that will end up in unique1
     *     visible.unique2 - contains ids that will end up in unique2
     *     visible.identical - contains ids that will end up in identical
     *
     * Returns:
     *     viewData object (see viewData in controller for object description)
     *
     * Algorithm summary:
     *     build a list of relevantIds (includes shadows if multigroup on)
     *     filter and sort the list
     *     bucket into groups and compute metadata in viewData (e.g. lengths)
     */
    visible.viewData = function (sort, filter) {

        var viewData = {};

        // get every id we need to care about (e.g. list1 + list2 + shadows (if any))
        var relevantIds = visible.list1.source.concat(visible.list2.source);

        // get or hide shadows
        if (visible.multigroup && visible.groupBy) {
            for (var shadowID in visible.shadows) {
                var shadow = visible.shadows[shadowID];
                if (visible.groupBy in shadow.attributes && shadow.attributes[visible.groupBy].length > 1 && shadow.attributes[visible.groupBy][shadow.groupByOffset]) {
                    // if shadow should be shown, update accordingly:
                    relevantIds.push(shadowID);

                    // update information about what is hidden
                    if (shadowID in visible.hidden) {
                        controller.toggleItem($("#" + shadowID), controller.toggleOnDelay, true);
                        if (visible.hidden[shadowID]) {
                            delete visible.hidden[shadowID];
                        }
                    }
                } else {
                    // otherwise, hide the shadow
                    controller.toggleItem($("#" + shadowID), controller.toggleOffDelay, false);
                    visible.hidden[shadowID] = true;
                }
            }
        } else {
            // no multigroup + groupBy = no shadows
            for (var shadowID in visible.shadows) {
                controller.toggleItem($("#" + shadowID), controller.toggleOffDelay, false);
                visible.hidden[shadowID] = true;
            }
        }

        // filter based on unified filter
        if (filter) {
            relevantIds = relevantIds.filter(unifiedFilter);
        }

        // sort data
        if (sort || filter) {
            relevantIds = relevantIds.sort(groupThenSort);
        }

        // initialize default group
        var groups = {};
        viewData.groups = groups;

        groups[visible.DEFAULT_GROUP] = [];

        var groupNames = [];
        // names of groups, used for ranking

        // for calculating lengths of groups
        var groupLengths = {};
        viewData.groupLengths = groupLengths;

        // Note: identicalMarker is a hash to keep track of distinct groups of
        //  identical objects
        groupLengths[""] = {
            "unique1": [],
            "identicalMarker": {},
            "unique2": []
        };

        for (var i in relevantIds) {
            var id = relevantIds[i];
            var item = visible.items[id];

            var trueId = item.isShadow ? parseInt(visible.shadowsToItems[id]) : id;

            // populate "groups"

            var itemGroup = visible.DEFAULT_GROUP;
            if (item.attributes.hasOwnProperty(visible.groupBy)) {
                // put id into its group if it has the grouped attribute
                // groupByOffset = offset into attribute list to get the primary group
                itemGroup = item.attributes[visible.groupBy][item.groupByOffset];

                // initialize array for group if needed
                if (!groups.hasOwnProperty(itemGroup)) {
                    groups[itemGroup] = [];
                    groupNames.push(itemGroup);
                    groupLengths[itemGroup] = {
                        "unique1": [],
                        "identicalMarker": {},
                        "unique2": []
                    };
                }
            }

            // put into retrieved (possibly just created) (or default) group
            groups[itemGroup].push(id);

            // update groupLengths depending on where if will end up

            if (visible.unique1.indexOf(trueId) >= 0) {
                groupLengths[itemGroup].unique1.push(id);
            } else if (visible.unique2.indexOf(trueId) >= 0) {
                groupLengths[itemGroup].unique2.push(id);
            } else if (trueId in visible.identical) {
                // get identical set, see if group marker already there, if not, add this
                var idenList = visible.identical[trueId];
                var j = 0;
                for (; j < idenList.length; j++) {
                    if (idenList[j] in groupLengths[itemGroup].identicalMarker) {
                        break;
                    }
                }

                if (j === idenList.length) {
                    // if didn't find group marker, add this to identicalMarker
                    groupLengths[itemGroup].identicalMarker[trueId] = idenList;
                }
            } // else in similar

        }// end of grouping ids

        // delete default group if unused (all other groups only exist if created)
        if (groups[visible.DEFAULT_GROUP].length === 0) {
            delete groups[visible.DEFAULT_GROUP];
        } else {
            groupNames.push(visible.DEFAULT_GROUP);
        }

        // specify group ranking // assumed item traversal order is equivalent, so is just groupNames
        viewData['groupRank'] = groupNames;

        // add method to get everything in rank order (for convenience)
        viewData.getAll = function () {
            var ret = [];
            for (var i in viewData['groupRank']) {
                var groupName = viewData['groupRank'][i];
                if (viewData['groups'].hasOwnProperty(groupName))
                    ret = ret.concat(viewData['groups'][groupName]);
            }
            return ret;
        };

        return viewData;
    };

    visible.attributes = {};

    visible.groupBy = "";

    // constant for default grouping
    visible.DEFAULT_GROUP = "";

    visible.sortBy = visible.ATTR_NAME;
    visible.filterOn = "";
    visible.multigroup = false;

    visible.afterAction = visible.AFTER_ACTION_GRAYOUT;

    visible.unique1 = [];
    visible.unique2 = [];
    visible.identical = {};

    // number of identical sets (each set will take 1 row in compact view)
    visible.numIdenticalSets = 0;
    visible.similar = {};

    // next id to use when adding items (e.g. if items added dynamically)
    visible.nextID = 0;

    // arrays to preserve order of actions
    visible.accepted = [];
    visible.rejected = [];
    visible.undecided = [];

    visible.displayName = visible.RECORDED_NAME;

    // methods ///////////////////////////////////////////////////////////
    visible.init = function (dataset) {
        resetState();
        var data;
        if (typeof dataset === 'object') {
            data = dataset;
            dataset = "__DATASET_KWS__";
        }
        else {
            data = DATASETS[dataset];
        }
        loadData(dataset, data);
    };

    visible.decide = function (id, src, dst) {
        var index = visible[src].indexOf(parseFloat(id));

        if (index !== -1) {// shadows not included
            visible[src].splice(index, 1);
            visible[dst].push(parseFloat(id));
        }
    };

    visible.getIdentical = function (id, includeShadows, applyFilter) {
        var identical = [];
        var checkID = visible.items[id].isShadow ? visible.getShadowed(id) : id;

        if (checkID in visible.identical) {
            identical = visible.identical[checkID].slice();
        }
        identical.splice(identical.indexOf(parseFloat(checkID)), 1);

        if (includeShadows) {
            var shadows = [];

            for (var i = 0; i < identical.length; i++) {
                shadows = shadows.concat(visible.getShadows(identical[i]));
            }
            identical = identical.concat(shadows);
        }
        return applyFilter ? identical.filter(unifiedFilter) : identical;
    };

    visible.getSimilar = function (id, includeShadows, applyFilter) {
        var similar = [];
        var checkID = visible.items[id].isShadow ? visible.getShadowed(id) : id;

        if (checkID in visible.similar) {
            similar = visible.similar[checkID].items.slice();
        }
        similar.splice(similar.indexOf(parseFloat(checkID)), 1);

        if (includeShadows) {
            var shadows = [];

            for (var i = 0; i < similar.length; i++) {
                shadows = shadows.concat(visible.getShadows(similar[i]));
            }
            similar = similar.concat(shadows);
        }
        return applyFilter ? similar.filter(unifiedFilter) : similar;
    };

    // given an id, return item ids that are related
    visible.getRelated = function (id, includeShadows) {
        if (("" + id)[0] == 'd') {
            // this is for 3 column view for a drug class or diagnosis "group item"
            if (("" + id)[1] == 'c')
                return visible.drugClassSet[id];
            // drug class
            else
                return visible.diagnosisSet[id];
            // diagnosis
        } else {
            var identical = visible.getIdentical(id, includeShadows, true);
            var similar = visible.getSimilar(id, includeShadows, true);
            var hash = {};
            var length = Math.min(identical.length, similar.length);

            // remove duplicates
            for (var i = 0; i < length; i++) {
                hash[identical[i]] = true;
                hash[similar[i]] = true;
            }

            if (length < identical.length) {
                for (; i < identical.length; i++) {
                    hash[identical[i]] = true;
                }
            } else if (length < similar.length) {
                for (; i < similar.length; i++) {
                    hash[similar[i]] = true;
                }
            }

            // convert results into array format
            var related = [];

            for (var hashedID in hash) {
                related.push(hashedID);
            }
            return related;
        }
    };

    visible.getIdenticalSet = function (id, includeShadows) {
        return visible.getShadowSet(id).concat(visible.getIdentical(id, includeShadows));
    };

    visible.getSimilarSet = function (id, includeShadows) {
        return visible.getShadowSet(id).concat(visible.getSimilar(id, includeShadows, true));
    };

    visible.getRelatedSet = function (id, includeShadows) {
        return visible.getShadowSet(id).concat(visible.getRelated(id, includeShadows));
    };

    visible.getShadows = function (id) {
        if (id in visible.itemsToShadows) {
            return visible.itemsToShadows[id];
        }
        return [];
    };

    visible.getShadowed = function (id) {
        return visible.shadowsToItems[id];
    };

    visible.getShadowSet = function (id) {
        if (("" + id)[0] == 'd')// TODO diagnosis version doesn't support multigroup right now
            return [];

        var checkID = visible.items[id].isShadow ? visible.getShadowed(id) : id;

        return [checkID].concat(visible.getShadows(checkID));
    };

    ////// hidden ////////////////////////////////////////////////////////////

    // initialization
    function resetState() {
        visible.items = {};
        visible.list1.source = [];
        visible.list2.source = [];
        visible.diagnoses = {};
        visible.diagnosisSet = {};

        visible.shadows = {};
        visible.itemsToShadows = {};
        visible.shadowsToItems = {};
        visible.hidden = {};

        visible.attributes = {};

        visible.unique1 = [];
        visible.unique2 = [];
        visible.identical = {};
        visible.similar = {};
        visible.nextID = 0;

        visible.accepted = [];
        visible.rejected = [];
        visible.undecided = [];
    }

    function loadData(dataset, data) {


        visible.dataset = dataset;

        populatePatientInformation(data);
        populateLists(data);
        detectAttributes();
        detectRelationships(data);
        detectDiagnoses();
        detectDrugClasses();

        // create "shadows", copies, to show n-group affiliation
        populateShadows();
    }

    // expected column names of csv format - all items have these attributes given in the csv
    var CSVC = {};
    CSVC.ID = "id";
    CSVC.ORIGIN = "origin";
    CSVC.R_NAME = "recorded name";
    CSVC.G_NAME = "generic name";
    CSVC.B_NAME = "brand name";
    CSVC.DOSE = "dose";
    CSVC.ROUTE = "route";
    CSVC.FREQUENCY = "frequency";
    CSVC.DRUG_CLASSES = "drug classes";
    CSVC.DIAGNOSES = "diagnoses";
    CSVC.INSTRUCTIONS = "instructions";
    CSVC.VALIDATOR = "validator";
    CSVC.ORGANISATION = "organisation";

    /*
     * Hard-coded datasets used by Twinlist, in future, should retrieve from other data source
     * Assumptions
     *      patient information contained in dataset
     *      item relationships present as a list of unique1, list of unique2, list of lists of identical items,
     *       similar items given as a list of objects where each object describes what items are similar and what
     *       the differences are
     *      csv format for data with specific column names (see CSVC)
     *      any addtional information not present in the csv format can be given as "other_data"
     *       Assumes the keys match the attribute constants used by Twinlist (e.g. see visible.ATTR_ROUTE, etc.)
     */
    var DATASETS = {
        "__DATASET_APPENDECTOMY__": {
            // patient data
            patientFirstName: "David",
            patientLastName: "Doe",
            patientAge: 55,
            patientGender: "M",

            // item relationships            
            unique1: [0, 5],
            unique2: [9, 10],
            identical: [[1, 11], [4, 8]],
            similar: [
                {items: [2, 7], differences: [visible.ATTR_NAME, visible.ATTR_DOSE]},
                {items: [3, 6], differences: [visible.ATTR_NAME]}],

            // item data
            csv: 'id,origin,recorded name,generic name,brand name,dose,route,frequency,drug classes,diagnoses\n' +
            '0,list0,aspirin,aspirin,Bayer,81 mg,PO,daily,"non-steroidal anti-inflammatory drug,analgesic,antiplatelet,antipyretic",atherosclerotic vascular disease\n' +
            '1,list0,Chantix,varenicline,Chantix,81 mg,PO,daily,antismoking,nicotine dependence\n' +
            '2,list0,Lipitor,atorvastatin,Lipitor,20 mg,PO,daily,anticholesterol,hypercholesterolemia\n' +
            '3,list0,Capoten,captopril,Capoten,25 mg,PO,BID,antihypertensive,hypertension\n' +
            '4,list0,multivitamin,multivitamin,multivitamin,1 tablet,PO,daily,dietary supplement,vitamin deficiency\n' +
            '5,list0,Sonata,zaleplon,Sonata,10 mg,PO,qHS prn,sedative,insomnia\n' +
            '6,list1,captopril,captopril,Capoten,25 mg,PO,BID,antihypertensive,hypertension\n' +
            '7,list1,atorvastatin,atorvastatin,Lipitor,25 mg,PO,qAM,anticholesterol,hypercholesterolemia\n' +
            '8,list1,multivitamin,multivitamin,multivitamin,1 tablet,PO,daily,dietary supplement,vitamin deficiency\n' +
            '9,list1,temazepam,temazepam,Restoril,15 mg,PO,qHS,"sedative,antianxiety",insomnia\n' +
            '10,list1,tramadol,tramadol,Ultram,50 mg,PO,q4h prn pain,analgesic,pain\n' +
            '11,list1,Chantix,varenicline,Chantix,81 mg,PO,daily,antismoking,nicotine dependence'
        },  // end of APP


        "__DATASET_CONGESTIVE_HEART_FAILURE_1__": {
            // patient data
            patientFirstName: "Jim",
            patientLastName: "Jones",
            patientAge: 74,
            patientGender: "M",

            // item relationships
            unique1: [3, 8],
            unique2: [16, 18, 20],
            identical: [[5, 12], [7, 13]],
            similar: [
                {items: [0, 11], differences: [visible.ATTR_FREQUENCY]},
                {items: [1, 22], differences: [visible.ATTR_NAME, visible.ATTR_FREQUENCY]},
                {items: [2, 17], differences: [visible.ATTR_NAME, visible.ATTR_FREQUENCY]},
                {items: [4, 15], differences: [visible.ATTR_NAME, visible.ATTR_FREQUENCY]},
                {items: [6, 14], differences: [visible.ATTR_FREQUENCY]},
                {items: [9, 21], differences: [visible.ATTR_NAME, visible.ATTR_FREQUENCY]},
                {items: [10, 19], differences: [visible.ATTR_NAME, visible.ATTR_DOSE, visible.ATTR_FREQUENCY]}],

            // item data
            csv: 'id,origin,recorded name,generic name,brand name,dose,route,frequency,drug classes,diagnoses\n' +
            '0,list0,acetaminophen,acetaminophen,Tylenol,650 mg,PO,q4h prn,"analgesic,antipyretic",pain\n' +
            '1,list0,Aldactone,spironolactone,Aldactone,100 mg,PO,daily,antihypertensive,hypertension\n' +
            '2,list0,Amaryl,glimepiride,Amaryl,4 mg,PO,daily,antidiabetic,diabetes\n' +
            '3,list0,Ambien,zolpidem,Ambien,10 mg,PO,qHS prn,sedative,insomnia\n' +
            '4,list0,Aricept,donepezil,Aricept,10 mg,PO,daily,acetylcholinesterase inhibitor,dementia\n' +
            '5,list0,aspirin,aspirin,Bayer,81 mg,PO,daily,"non-steroidal anti-inflammatory drug,analgesic,antiplatelet,antipyretic","atherosclerotic vascular disease, pain"\n' +
            '6,list0,cimetidine,cimetidine,Tagamet,800 mg,PO,BID,antacid,GERD\n' +
            '7,list0,Coreg,carvedilol,Coreg,6.25 mg,PO,BID,antihypertensive,hypertension\n' +
            '8,list0,Colace,ducosate,Colace,100 mg,PO,BID,stool softener,constipation\n' +
            '9,list0,Crestor,rosuvastatin,Crestor,20 mg,PO,daily,anticholesterol,hypercholesterolemia\n' +
            '10,list0,Hyzaar,losartan + hydrochlorothiazide,Hyzaar,100 / 25 mg,PO,daily,"antihypertensive,diuretic","antihypertensive, diuretic"\n' +
            '11,list1,acetaminophen,acetaminophen,Tylenol,650 mg,PO,q4h prn headache or pain,"analgesic,antipyretic",pain\n' +
            '12,list1,aspirin,aspirin,Bayer,81 mg,PO,daily,"non-steroidal anti-inflammatory drug,analgesic,antiplatelet,antipyretic",atherosclerotic vascular disease\n' +
            '13,list1,Coreg,carvedilol,Coreg,6.25 mg,PO,BID,antihypertensive,hypertension\n' +
            '14,list1,cimetidine,cimetidine,Tagamet,800 mg,PO,q12h,antacid,GERD\n' +
            '15,list1,donepezil,donepezil,Aricept,10 mg,PO,qAM,acetylcholinesterase inhibitor,dementia\n' +
            '16,list1,furosemide,furosemide,Lasix,40 mg,PO,BID,"diuretic,antihypertensive",congestive heart failure\n' +
            '17,list1,glimepiride,glimepiride,Amaryl,4 mg,PO,qAM,antidiabetic,diabetes\n' +
            '18,list1,lorazepam,lorazepam,Ativan,1 mg,PO,qHS prn insomnia,"sedative,antianxiety",insomnia\n' +
            '19,list1,losartan,losartan,Cozaar,50 mg,PO,qAM,antihypertensive,hypertension\n' +
            '20,list1,magnesium hydroxide,magnesium hydroxide,Milk of magnesia,30 ml,PO,daily prn constipation,"laxative,antacid",constipation\n' +
            '21,list1,rosuvastatin,rosuvastatin,Crestor,20 mg,PO,qAM,anticholesterol,hypercholesterolemia\n' +
            '22,list1,spironolactone,spironolactone,Aldactone,100 mg,PO,qAM,antihypertensive,hypertension',

            // optional data (Note: displayed in item details but not really used)
            other_data: {
                10: {
                    "__ATTR_SUBITEM__": [
                        {name: "losartan", attributes: {"__ATTR_DOSE__": "100 mg"}},
                        {name: "hydrochlorothiazide", attributes: {"__ATTR_DOSE__": "25 mg"}}]
                }
            }
        }, // end of CHF1


        '__DATASET_CONGESTIVE_HEART_FAILURE_2__': {
            // patient data
            patientFirstName: "Mary",
            patientLastName: "Smith",
            patientAge: 65,
            patientGender: "F",

            // item relationships
            unique1: [5, 6, 8],
            unique2: [14, 21],
            identical: [[7, 15], [9, 16]],
            similar: [
                {items: [0, 11], differences: [visible.ATTR_NAME]},
                {items: [1, 12], differences: [visible.ATTR_NAME, visible.ATTR_DOSE]},
                {items: [2, 13], differences: [visible.ATTR_NAME, visible.ATTR_DOSE]},
                {items: [3, 18], differences: [visible.ATTR_NAME, visible.ATTR_FREQUENCY]},
                {items: [4, 19, 20], differences: [visible.ATTR_NAME, visible.ATTR_DOSE, visible.ATTR_FREQUENCY]},
                {items: [10, 17], differences: [visible.ATTR_FREQUENCY]}],

            // item data
            csv: 'id,origin,recorded name,generic name,brand name,dose,route,frequency,drug classes,diagnoses\n' +
            '0,list0,Coreg,carvedilol,Coreg,25 mg,PO,BID,antihypertensive,hypertension\n' +
            '1,list0,HCTZ,hydrochlorothiazide,Hydrodiuril,25 mg,PO,daily,"diuretic,antihypertensive",congestive heart failure\n' +
            '2,list0,Coumadin,warfarin,Coumadin,5 mg,PO,daily,anticoagulant,thrombosis\n' +
            '3,list0,Lasix,furosemide,Lasix,40 mg,PO,daily,"diuretic,antihypertensive",congestive heart failure\n' +
            '4,list0,Percocet,acetaminophen + oxycodone,Percocet,1 tablet,PO,q4h prn pain,analgesic,pain\n' +
            '5,list0,Zantac,ranitidine,Zantac,150 mg,PO,BID,antacid,GERD\n' +
            '6,list0,Dulcolax,bisacodyl,Dulcolax,10 mg,PO,daily prn,laxative,constipation\n' +
            '7,list0,loratadine,loratadine,Claritin,10 mg,PO,daily prn,antihistamine,nasal congestion\n' +
            '8,list0,Metamucil,psyllium husk,Metamucil,1 tbsp,PO,daily,stool softener,constipation\n' +
            '9,list0,pravastatin,pravastatin,Pravachol,40 mg,PO,daily,anticholesterol,hypercholesterolemia\n' +
            '10,list0,zaleplon,zaleplon,Sonata,10 mg,PO,qHS prn,sedative,insomnia\n' +
            '11,list1,carvedilol,carvedilol,Coreg,25 mg,PO,BID,antihypertensive,hypertension\n' +
            '12,list1,hydrochlorothiazide,hydrochlorothiazide,Hydrodiuril,50 mg,PO,daily,"diuretic,antihypertensive",congestive heart failure\n' +
            '13,list1,warfarin,warfarin,Coumadin,4 mg,PO,daily,anticoagulant,thrombosis\n' +
            '14,list1,magnesium hydroxide,magnesium hydroxide,Milk of Magnesia,30 ml,PO,daily prn constipation,"laxative,antacid",constipation\n' +
            '15,list1,loratadine,loratadine,Claritin,10 mg,PO,daily prn,antihistamine,nasal congestion\n' +
            '16,list1,pravastatin,pravastatin,Pravachol,40 mg,PO,daily,anticholesterol,hypercholesterolemia\n' +
            '17,list1,zaleplon,zaleplon,Sonata,10 mg,PO,qHS prn insomnia,sedative,insomnia\n' +
            '18,list1,furosemide,furosemide,Lasix,40 mg,PO,BID,"diuretic,antihypertensive",congestive heart failure\n' +
            '19,list1,oxycodone,oxycodone,Oxycontin,5 mg,PO,q4-6h prn pain,analgesic,pain\n' +
            '20,list1,acetaminophen,acetaminophen,Tylenol,650 mg,PO,q4h prn pain,analgesic,pain\n' +
            '21,list1,pantoprazole,pantoprazole,Protonix,20 mg,PO,BID,antacid,GERD',

            other_data: {
                4: {
                    "__ATTR_SUBITEM__": [
                        {name: "acetaminophen", attributes: {"__ATTR_DOSE__": ""}},
                        {name: "oxycodone", attributes: {"__ATTR_DOSE__": ""}}]
                }
            }
        }, // end of CHF2


        '__DATASET_PULMONARY_DISEASE_1__': {
            // patient data
            patientFirstName: "Penny",
            patientLastName: "Pfeifer",
            patientAge: 63,
            patientGender: "F",

            // item relationships
            unique1: [0, 2, 5, 6, 10, 11, 12, 14],
            unique2: [16, 19, 22, 23, 25, 27],
            identical: [[3, 26], [15, 18]],
            similar: [
                {items: [1, 28], differences: [visible.ATTR_NAME]},
                {items: [4, 13, 17], differences: [visible.ATTR_NAME, visible.ATTR_DOSE, visible.ATTR_FREQUENCY]},
                {items: [7, 20], differences: [visible.ATTR_NAME, visible.ATTR_FREQUENCY]},
                {items: [8, 24], differences: [visible.ATTR_DOSE]},
                {items: [9, 21], differences: [visible.ATTR_NAME, visible.ATTR_FREQUENCY]}],

            // item data
            csv: 'id,origin,recorded name,generic name,brand name,dose,route,frequency,drug classes,diagnoses\n' +
            '0,list0,Abilify,aripiprazole,Abilify,5 mg,PO,daily,"antipsychotic,antidepressant",depression\n' +
            '1,list0,Advair,salmeterol + fluticasone,Advair,250 / 50 mg,PO,BID,bronchodilator,COPD\n' +
            '2,list0,Bactrim,trimethoprim + sulfamethoxazole,Bactrim,2 tablets,PO,q12h,antibiotic,pneumonia\n' +
            '3,list0,multivitamin,multivitamin,multivitamin,1 tablet,PO,daily,dietary supplement,vitamin deficiency\n' +
            '4,list0,Fosamax+D,alendronate + vitamin D,Fosamax+D,1 tablet,PO,daily,bone resorption inhibitor,osteoporosis\n' +
            '5,list0,Hygroton,chlorthalidone,Hygroton,50 mg,PO,daily,"diuretic,antihypertensive",hypertension\n' +
            '6,list0,Lunesta,eszopiclone,Lunesta,2 mg,PO,qHS prn,sedative,insomnia\n' +
            '7,list0,Plavix,clopidogrel,Plavix,75 mg,PO,daily,antiplatelet,atherosclerotic vascular disease\n' +
            '8,list0,prednisone,prednisone,Deltasone,40 mg,PO,taper,corticosteroid,COPD\n' +
            '9,list0,Premarin,conjugated estrogens,Premarin,0.3 mg,PO,daily,sex hormone,menopause symptoms\n' +
            '10,list0,Metamucil,psyllium husk,Metamucil,1 tbsp,PO,daily,stool softener,constipation\n' +
            '11,list0,Nexium,esomeprazole,Nexium,20 mg,PO,daily,antacid,GERD\n' +
            '12,list0,Senokot,sennosides,Senokot,2 tablets,PO,daily prn constipation,laxative,constipation\n' +
            '13,list0,vitamin D,vitamin D,Calciferol,800 IU,PO,daily,dietary supplement,vitamin deficiency\n' +
            '14,list0,Zestril,lisinopril,Zestril,20 mg,PO,daily,antihypertensive,hypertension\n' +
            '15,list0,bupropion,bupropion,Zyban,150 mg,PO,BID,"antidepressant,antismoking",antidepressant\n' +
            '16,list1,acetaminophen,acetaminophen,Tylenol,650 mg,PO,q4h prn headache,"analgesic,antipyretic",pain\n' +
            '17,list1,alendronate,alendronate,Fosamax,10 mg,PO,daily,bone resorption inhibitor,osteoporosis\n' +
            '18,list1,bupropion,bupropion,Zyban,150 mg,PO,BID,"antidepressant,antismoking",antidepressant\n' +
            '19,list1,moxifloxacin,moxifloxacin,Avelox,400 mg,PO,daily,antibiotic,pneumonia\n' +
            '20,list1,clopidogrel,clopidogrel,Plavix,75 mg,PO,qAM,antiplatelet,atherosclerotic vascular disease\n' +
            '21,list1,conjugated estrogens,conjugated estrogens,Premarin,0.3 mg,PO,qAM,sex hormone,menopause symptoms\n' +
            '22,list1,hydrochlorthiazide,hydrochlorthiazide,Hydrodiuril,50 mg,PO,qAM,"diuretic,antihypertensive",hypertension\n' +
            '23,list1,lorazepam,lorazepam,Ativan,0.5 mg,PO,qHS prn,"sedative,antianxiety",anxiety\n' +
            '24,list1,prednisone,prednisone,Deltasone,30 mg,PO,taper,corticosteroid,COPD\n' +
            '25,list1,magnesium hydroxide,magnesium hydroxide,Milk of Magnesia,30 ml,PO,daily prn constipation,"laxative,antacid",constipation\n' +
            '26,list1,multivitamin,multivitamin,multivitamin,1 tablet,PO,daily,dietary supplement,vitamin deficiency\n' +
            '27,list1,pantoprazole,pantoprazole,Protonix,40 mg,PO,qAM,antacid,GERD\n' +
            '28,list1,salmeterol + fluticasone,salmeterol + fluticasone,Advair,250 / 50 mg,PO,BID,bronchodilator,bronchodilator',

            other_data: {
                1: {
                    "__ATTR_SUBITEM__": [
                        {name: "salmeterol", attributes: {"__ATTR_DOSE__": "250 mg"}},
                        {name: "fluticasone", attributes: {"__ATTR_DOSE__": "50 mg"}}]
                },
                2: {
                    "__ATTR_SUBITEM__": [
                        {name: "trimethoprim", attributes: {"__ATTR_DOSE__": ""}},
                        {name: "sulfamethoxazole", attributes: {"__ATTR_DOSE__": ""}}],
                    "__ATTR_DATE_STARTED__": ["started 3 days ago"]
                },
                4: {
                    "__ATTR_SUBITEM__": [
                        {name: "alendronate", attributes: {"__ATTR_DOSE__": ""}},
                        {name: "vitamin D", attributes: {"__ATTR_DOSE__": ""}}]
                },
                28: {
                    "__ATTR_SUBITEM__": [
                        {name: "salmeterol", attributes: {"__ATTR_DOSE__": "250 mg"}},
                        {name: "fluticasone", attributes: {"__ATTR_DOSE__": "50 mg"}}]
                }
            }
        }, // end of PD1


        '__DATASET_PULMONARY_DISEASE_2__': {
            // patient data
            patientFirstName: "Richard",
            patientLastName: "White",
            patientAge: 80,
            patientGender: "M",

            // item relationships
            unique1: [0, 2, 3, 10, 11, 12, 15],
            unique2: [16, 18, 19, 20, 29],
            identical: [[5, 22], [6, 23], [13, 27]],
            similar: [
                {items: [1, 17], differences: [visible.ATTR_NAME, visible.ATTR_DOSE, visible.ATTR_FREQUENCY]},
                {items: [4, 21], differences: [visible.ATTR_NAME, visible.ATTR_DOSE, visible.ATTR_FREQUENCY]},
                {items: [7, 24], differences: [visible.ATTR_NAME]},
                {items: [8, 25], differences: [visible.ATTR_NAME, visible.ATTR_FREQUENCY]},
                {items: [9, 26], differences: [visible.ATTR_NAME]},
                {items: [14, 28], differences: [visible.ATTR_NAME]}],

            // item data
            csv: 'id,origin,recorded name,generic name,brand name,dose,route,frequency,drug classes,diagnoses\n' +
            '0,list0,dabigatran,dabigatran,Pradaxa,150 mg,PO,BID,anticoagulant,atrial fibrillation\n' +
            '1,list0,Zestoretic,hydrochlorothiazide + lisinopril,Zestoretic,20 / 12.5 mg,PO,daily,"antihypertensive,diuretic",hypertension\n' +
            '2,list0,metformin,metformin,Glucophage,850 mg,PO,daily,antidiabetic,diabetes\n' +
            '3,list0,Micronase,glyburide,glyburide,5 mg,PO,daily,antidiabetic,diabetes\n' +
            '4,list0,Toprol XL,metoprolol,Toprol XL,25 mg,PO,daily,antihypertensive,hypertension\n' +
            '5,list0,acetaminophen,acetaminophen,Tylenol,1 g,PO,q6h prn pain,"analgesic,antipyretic",pain\n' +
            '6,list0,tramadol,tramadol,Ultram,50 mg,PO,q6h prn pain,analgesic,pain\n' +
            '7,list0,Plavix,clopidogrel,Plavix,75 mg,PO,daily,antiplatelet,atherosclerotic vascular disease\n' +
            '8,list0,Aricept,donepezil,Aricept,10 mg,PO,daily,acetylcholinesterase inhibitor,dementia\n' +
            '9,list0,Prozac,fluoxetine,Prozac,20 mg,PO,daily,antidepressant,depression\n' +
            '10,list0,vitamin B12,vitamin B12,vitamin B12,1000 mcg,SC,qMonth,dietary supplement,vitamin deficiency\n' +
            '11,list0,Calciferol,vitamin D,Calciferol,600 IU,PO,daily,dietary supplement,osteoporosis\n' +
            '12,list0,calcium carbonate,calcium carbonate,Tums,500 mg,PO,QID,dietary supplement,osteoporosis\n' +
            '13,list0,lorazepam,lorazepam,Ativan,1 mg,PO,q8h prn anxiety,"sedative,antianxiety",anxiety\n' +
            '14,list0,Lipitor,rosuvastatin,Lipitor,40 mg,PO,daily,anticholesterol,hypercholesterolemia\n' +
            '15,list0,Tirosint,levothyroxine,Tirosint,100 mcg,PO,daily,thyroid,hypothyroidism\n' +
            '16,list1,enoxaparin,enoxaparin,Lovenox,40 mg,SC,daily,anticoagulant,atrial fibrillation\n' +
            '17,list1,lisinopril,lisinopril,Zestril,20 mg,PO,daily,antihypertensive,hypertension\n' +
            '18,list1,hydrochlorothiazide,hydrochlorothiazide,Hydrodiuril,12.5 mg,PO,daily,"diuretic,antihypertensive",hypertension\n' +
            '19,list1,insulin sliding scale,insulin sliding scale,Humulin,,SC,q4h prn,antidiabetic,diabetes\n' +
            '20,list1,Lantus,insulin glargine,Lantus,20 mg,SC,qHS,antidiabetic,diabetes\n' +
            '21,list1,metoprolol,metoprolol,Toprol XL,50 mg,PO,BID,antihypertensive,hypertension\n' +
            '22,list1,acetaminophen,acetaminophen,Tylenol,1 g,PO,q6h prn pain,"analgesic,antipyretic",pain\n' +
            '23,list1,tramadol,tramadol,Ultram,50 mg,PO,q6h prn pain,analgesic,pain\n' +
            '24,list1,clopidogrel,clopidogrel,Plavix,75 mg,PO,daily,antiplatelet,atherosclerotic vascular disease\n' +
            '25,list1,donepezil,donepezil,Aricept,10 mg,PO,qHS,acetylcholinesterase inhibitor,dementia\n' +
            '26,list1,fluoxetine,fluoxetine,Prozac,20 mg,PO,daily,antidepressant,depression\n' +
            '27,list1,lorazepam,lorazepam,Ativan,1 mg,PO,q8h prn anxiety,"sedative,antianxiety",anxiety\n' +
            '28,list1,rosuvastatin,rosuvastatin,Lipitor,40 mg,PO,daily,anticholesterol,hypercholesterolemia\n' +
            '29,list1,cephalexin,cephalexin,Biocef,500 mg,PO,q6h,antibiotic,cellulitis',

            other_data: {
                1: {
                    "__ATTR_SUBITEM__": [
                        {name: "hydrochlorothiazide", attributes: {"__ATTR_DOSE__": "20 mg"}},
                        {name: "lisinopril", attributes: {"__ATTR_DOSE__": "12.5 mg"}}]
                },
                29: {
                    "__ATTR_DATE_STARTED__": ["started 1 day ago"]
                },
            }
        }, // end of PD2


        '__DATASET_OTHER_SIMPLE__': {
            // patient data
            patientFirstName: "John",
            patientLastName: "Doe",
            patientAge: 30,
            patientGender: "M",

            // item relationships
            unique1: [4],
            unique2: [9, 10, 13],
            identical: [[1, 7], [2, 12], [3, 11]],
            similar: [
                {items: [0, 6], differences: [visible.ATTR_FREQUENCY, visible.ATTR_DOSAGE]},
                {items: [5, 8], differences: [visible.ATTR_NAME]}],

            // item data
            csv: 'id,origin,recorded name,generic name,brand name,dose,route,frequency,drug classes,diagnoses\n' +
            '0,list0,Acetaminophen,Acetaminophen,Acetaminophen,325 mg,PO,q6h,"analgesic,antipyretic",\n' +
            '1,list0,Darbepoetin,Darbepoetin,Darbepoetin,60 mg,SC,qFriday,ESA,\n' +
            '2,list0,Calcitrol,Calcitrol,Calcitrol,0.25 mg,PO,daily,supplement,\n' +
            '3,list0,Ramipril,Ramipril,Ramipril,5 mg,PO,daily,ACE inhibitor,\n' +
            '4,list0,Meloxicam,Meloxicam,Meloxicam,7.5 mg,PO,daily,"analgesic,NSAID",\n' +
            '5,list0,Folvite,Folvite,Folvite,1 mg,PO,daily,supplement,\n' +
            '6,list1,Acetaminophen,Acetaminophen,Acetaminophen,325 mg,PO,q4h,"analgesic,antipyretic",\n' +
            '7,list1,Darbepoetin,Darbepoetin,Darbepoetin,60 mg,SC,qFriday,ESA,\n' +
            '8,list1,Folic acid,Folic acid,Folic acid,1 mg,PO,daily,supplement,\n' +
            '9,list1,Omeprazole,Omeprazole,Omeprazole,40 mg,PO,daily,proton-pump inhibitor,\n' +
            '10,list1,Ciprofloxacin,Ciprofloxacin,Ciprofloxacin,500 mg,PO,daily,antibiotic,\n' +
            '11,list1,Ramipril,Ramipril,Ramipril,5 mg,PO,daily,ACE inhibitor,\n' +
            '12,list1,Calcitrol,Calcitrol,Calcitrol,0.25 mg,PO,daily,supplement,\n' +
            '13,list1,Ferrous Gloconate,Ferrous Gloconate,Ferrous Gloconate,300 mg,PO,TID,supplement,',

            other_data: {
                10: {
                    "__ATTR_DATE_STARTED__": ["started 4 days ago"]
                }
            }
        }, // end of O_SIMPLE


        '__DATASET_OTHER_COMPLEX__': {
            // patient data
            patientFirstName: "Jane",
            patientLastName: "Doe",
            patientAge: 30,
            patientGender: "F",

            // item relationships
            unique2: [14],
            unique1: [2, 7, 8],
            identical: [[0, 4, 13], [5, 16]],
            similar: [
                {
                    items: [0, 1, 4, 11, 13, 15],
                    differences: [visible.ATTR_NAME, visible.ATTR_FREQUENCY, visible.ATTR_DOSAGE]
                },
                {items: [3, 6, 10], differences: [visible.ATTR_NAME, visible.ATTR_FREQUENCY, visible.ATTR_DOSAGE]},
                {items: [9, 12], differences: [visible.ATTR_NAME]}],

            // item data
            csv: 'id,origin,recorded name,generic name,brand name,dose,route,frequency,drug classes,diagnoses\n' +
            '0,list0,Acetaminophen,Acetaminophen,Acetaminophen,325 mg,PO,q4h,"analgesic,antipyretic",\n' +
            '1,list0,Tylenol,Tylenol,Tylenol,325 mg,PO,q6h,"analgesic,antipyretic",\n' +
            '2,list0,Zyrtec,Zyrtec,Zyrtec,10 mg,PO,daily,"antihistamine",\n' +
            '3,list0,Allegra-D,Allegra-D,Allegra-D,60 / 120 mg,PO,BID,"antihistamine,decongestant",\n' +
            '4,list0,Acetaminophen,Acetaminophen,Acetaminophen,325 mg,PO,q4h,"analgesic,antipyretic",\n' +
            '5,list0,Darbepoetin,Darbepoetin,Darbepoetin,60 mg,SC,qFriday,"ESA",\n' +
            '6,list0,Sudafed,Sudafed,Sudafed,30 mg,PO,q6h,"decongestant",\n' +
            '7,list0,Aspirin,Aspirin,Aspirin,81 mg,PO,daily,"salicylate",\n' +
            '8,list0,Claritin,Claritin,Claritin,10 mg,PO,daily,"antihistamine",\n' +
            '9,list0,Advil,Advil,Advil,200 mg,PO,q4h,"NSAID",\n' +
            '10,list1,Fexofenadine,Fexofenadine,Fexofenadine,60 mg,PO,daily,"antihistamine",\n' +
            '11,list1,Acetaminophen,Acetaminophen,Acetaminophen,3325 mg,PO,q4h,"analgesic,antipyretic",\n' +
            '12,list1,Ibuprofen,Ibuprofen,Ibuprofen,200 mg,PO,q4h,"NSAID",\n' +
            '13,list1,Acetaminophen,Acetaminophen,Acetaminophen,325 mg,PO,q4h,"analgesic,antipyretic",\n' +
            '14,list1,Prednisone,Prednisone,Prednisone,30 mg,PO,daily,"corticosteroid",\n' +
            '15,list1,Acetaminophen,Acetaminophen,Acetaminophen,325 mg,PO,q6h,"analgesic,antipyretic",\n' +
            '16,list1,Darbepoetin,Darbepoetin,Darbepoetin,60 mg,SC,qFriday,"ESA",',

            other_data: {
                3: {
                    '__ATTR_SUBITEM__': [
                        {name: "Fexofenadine", attributes: {"__ATTR_DOSAGE__": ["60 mg"]}},
                        {name: "Pseudoephedrine", attributes: {"__ATTR_DOSAGE__": ["120 mg"]}}]
                }
            }

        }, // end of O_COMPLEX


        '__DATASET_OTHER_EXTRA__': {
            // patient data
            patientFirstName: "Jim",
            patientLastName: "Jones",
            patientAge: 74,
            patientGender: "M",

            // item relationships
            unique1: [2, 7],
            unique2: [14, 15, 17],
            identical: [[4, 10], [6, 11]],
            similar: [
                {items: [1, 20], differences: [visible.ATTR_NAME]},
                {items: [0, 19], differences: [visible.ATTR_NAME, visible.ATTR_FREQUENCY]},
                {items: [3, 13], differences: [visible.ATTR_NAME, visible.ATTR_FREQUENCY]},
                {items: [5, 12], differences: [visible.ATTR_FREQUENCY]},
                {items: [8, 18], differences: [visible.ATTR_NAME, visible.ATTR_FREQUENCY]},
                {items: [9, 16], differences: [visible.ATTR_NAME, visible.ATTR_DOSE, visible.ATTR_FREQUENCY]}],

            // item data
            csv: 'id,origin,recorded name,generic name,brand name,dose,route,frequency,drug classes,diagnoses\n' +
            '0,list0,Aldactone,spironolactone,Aldactone,100 mg,PO,daily,"antihypertensive",hypertension\n' +
            '1,list0,Avelox,moxifloxacin,Avelox,400 mg,PO,daily,"antibiotic",pneumonia\n' +
            '2,list0,Ambien,zolpidem,Ambien,10 mg,PO,qHS prn,"sedative",insomnia\n' +
            '3,list0,Aricept,donepezil,Aricept,10 mg,PO,daily,"acetylcholinesterase inhibitor",dementia\n' +
            '4,list0,aspirin,aspirin,Bayer,81 mg,PO,daily,"non-steroidal anti-inflammatory drug,analgesic,antiplatelet,antipyretic","atherosclerotic vascular disease, pain"\n' +
            '5,list0,cimetidine,cimetidine,Tagamet,800 mg,PO,BID,"antacid",GERD\n' +
            '6,list0,Coreg,carvedilol,Coreg,6.25 mg,PO,BID,"antihypertensive",hypertension\n' +
            '7,list0,Colace,ducosate,Colace,100 mg,PO,BID,"stool softener",constipation\n' +
            '8,list0,Crestor,rosuvastatin,Crestor,20 mg,PO,daily,"anticholesterol",hypercholesterolemia\n' +
            '9,list0,Hyzaar,losartan + hydrochlorothiazide,Hyzaar,100 / 25 mg,PO,daily,"antihypertensive,diuretic","antihypertensive, diuretic"\n' +
            '10,list1,aspirin,aspirin,Bayer,81 mg,PO,daily,"non-steroidal anti-inflammatory drug,analgesic,antiplatelet,antipyretic",atherosclerotic vascular disease\n' +
            '11,list1,Coreg,carvedilol,Coreg,6.25 mg,PO,BID,"antihypertensive",hypertension\n' +
            '12,list1,cimetidine,cimetidine,Tagamet,800 mg,PO,q12h,"antacid",GERD\n' +
            '13,list1,donepezil,donepezil,Aricept,10 mg,PO,qAM,"acetylcholinesterase inhibitor",dementia\n' +
            '14,list1,furosemide,furosemide,Lasix,40 mg,PO,BID,"diuretic,antihypertensive",congestive heart failure\n' +
            '15,list1,lorazepam,lorazepam,Ativan,1 mg,PO,qHS prn insomnia,"sedative,antianxiety",insomnia\n' +
            '16,list1,losartan,losartan,Cozaar,50 mg,PO,qAM,"antihypertensive",hypertension\n' +
            '17,list1,magnesium hydroxide,magnesium hydroxide,Milk of magnesia,30 ml,PO,daily prn constipation,"laxative,antacid",constipation\n' +
            '18,list1,rosuvastatin,rosuvastatin,Crestor,20 mg,PO,qAM,"anticholesterol",hypercholesterolemia\n' +
            '19,list1,spironolactone,spironolactone,Aldactone,100 mg,PO,qAM,"antihypertensive",hypertension\n' +
            '20,list1,moxifloxacin,moxifloxacin,Avelox,400 mg,PO,daily,"antibiotic",pneumonia',

            other_data: {
                1: {
                    '__ATTR_DATE_STARTED__': // always 2 days ago
                        ["Started " +
                        new Date(Date.now() - 172800000).toDateString().split(" ")[1] + ". " +
                        new Date(Date.now() - 172800000).toDateString().split(" ")[2] + ", " +
                        new Date(Date.now() - 172800000).toDateString().split(" ")[3] + " (2 days ago)"]
                },
                9: {
                    '__ATTR_SUBITEM__': [
                        {name: "losartan", attributes: {"__ATTR_DOSE__": "100 mg"}},
                        {name: "hydrochlorothiazide", attributes: {"__ATTR_DOSE__": "25 mg"}}],
                },
                20: {
                    '__ATTR_DATE_STARTED__': // always 1 day ago
                        ["Started " +
                        new Date(Date.now() - 86400000).toDateString().split(" ")[1] + ". " +
                        new Date(Date.now() - 86400000).toDateString().split(" ")[2] + ", " +
                        new Date(Date.now() - 86400000).toDateString().split(" ")[3] + " (1 day ago)"]
                },
            }
        }, // end of O_EXTRA 
        // (Note: was only used to generate a compact screenshot with grouping by drug class + showing dates on antibiotics)


        '__DATASET_PULMONARY_DISEASE_2_CORRECTED__': {
            // patient data
            patientFirstName: "Richard",
            patientLastName: "White",
            patientAge: 80,
            patientGender: "M",

            // item relationships
            unique1: [0, 2, 3, 10, 11, 12, 15],
            unique2: [16, 19, 20, 29],

            identical: [[5, 22], [6, 23], [13, 27]],

            similar: [
                {items: [1, 17, 18], differences: [visible.ATTR_NAME, visible.ATTR_DOSE]},
                {items: [4, 21], differences: [visible.ATTR_NAME, visible.ATTR_DOSE, visible.ATTR_FREQUENCY]},
                {items: [7, 24], differences: [visible.ATTR_NAME]},
                {items: [8, 25], differences: [visible.ATTR_NAME, visible.ATTR_FREQUENCY]},
                {items: [9, 26], differences: [visible.ATTR_NAME]},
                {items: [14, 28], differences: [visible.ATTR_NAME]}],

            // item data
            csv: 'id,origin,recorded name,generic name,brand name,dose,route,frequency,drug classes,diagnoses\n' +
            '0,list0,dabigatran,dabigatran,Pradaxa,150 mg,PO,BID,anticoagulant,atrial fibrillation\n' +
            '1,list0,Zestoretic,hydrochlorothiazide + lisinopril,Zestoretic,20 / 12.5 mg,PO,daily,"antihypertensive,diuretic",hypertension\n' +
            '2,list0,metformin,metformin,Glucophage,850 mg,PO,daily,antidiabetic,diabetes\n' +
            '3,list0,Micronase,glyburide,glyburide,5 mg,PO,daily,antidiabetic,diabetes\n' +
            '4,list0,Toprol XL,metoprolol,Toprol XL,25 mg,PO,daily,antihypertensive,hypertension\n' +
            '5,list0,acetaminophen,acetaminophen,Tylenol,1 g,PO,q6h prn pain,"analgesic,antipyretic",pain\n' +
            '6,list0,tramadol,tramadol,Ultram,50 mg,PO,q6h prn pain,analgesic,pain\n' +
            '7,list0,Plavix,clopidogrel,Plavix,75 mg,PO,daily,antiplatelet,atherosclerotic vascular disease\n' +
            '8,list0,Aricept,donepezil,Aricept,10 mg,PO,daily,acetylcholinesterase inhibitor,dementia\n' +
            '9,list0,Prozac,fluoxetine,Prozac,20 mg,PO,daily,antidepressant,depression\n' +
            '10,list0,vitamin B12,vitamin B12,vitamin B12,1000 mcg,SC,qMonth,dietary supplement,vitamin deficiency\n' +
            '11,list0,Calciferol,vitamin D,Calciferol,600 IU,PO,daily,dietary supplement,osteoporosis\n' +
            '12,list0,calcium carbonate,calcium carbonate,Tums,500 mg,PO,QID,dietary supplement,osteoporosis\n' +
            '13,list0,lorazepam,lorazepam,Ativan,1 mg,PO,q8h prn anxiety,"sedative,antianxiety",anxiety\n' +
            '14,list0,Lipitor,rosuvastatin,Lipitor,40 mg,PO,daily,anticholesterol,hypercholesterolemia\n' +
            '15,list0,Tirosint,levothyroxine,Tirosint,100 mcg,PO,daily,thyroid,hypothyroidism\n' +
            '16,list1,enoxaparin,enoxaparin,Lovenox,40 mg,SC,daily,anticoagulant,atrial fibrillation\n' +
            '17,list1,lisinopril,lisinopril,Zestril,20 mg,PO,daily,antihypertensive,hypertension\n' +
            '18,list1,hydrochlorothiazide,hydrochlorothiazide,Hydrodiuril,12.5 mg,PO,daily,"diuretic,antihypertensive",hypertension\n' +
            '19,list1,insulin sliding scale,insulin sliding scale,Humulin,,SC,q4h prn,antidiabetic,diabetes\n' +
            '20,list1,Lantus,insulin glargine,Lantus,20 mg,SC,qHS,antidiabetic,diabetes\n' +
            '21,list1,metoprolol,metoprolol,Toprol XL,50 mg,PO,BID,antihypertensive,hypertension\n' +
            '22,list1,acetaminophen,acetaminophen,Tylenol,1 g,PO,q6h prn pain,"analgesic,antipyretic",pain\n' +
            '23,list1,tramadol,tramadol,Ultram,50 mg,PO,q6h prn pain,analgesic,pain\n' +
            '24,list1,clopidogrel,clopidogrel,Plavix,75 mg,PO,daily,antiplatelet,atherosclerotic vascular disease\n' +
            '25,list1,donepezil,donepezil,Aricept,10 mg,PO,qHS,acetylcholinesterase inhibitor,dementia\n' +
            '26,list1,fluoxetine,fluoxetine,Prozac,20 mg,PO,daily,antidepressant,depression\n' +
            '27,list1,lorazepam,lorazepam,Ativan,1 mg,PO,q8h prn anxiety,"sedative,antianxiety",anxiety\n' +
            '28,list1,rosuvastatin,rosuvastatin,Lipitor,40 mg,PO,daily,anticholesterol,hypercholesterolemia\n' +
            '29,list1,cephalexin,cephalexin,Biocef,500 mg,PO,q6h,antibiotic,cellulitis',

            other_data: {
                1: {
                    "__ATTR_SUBITEM__": [
                        {name: "hydrochlorothiazide", attributes: {"__ATTR_DOSE__": "20 mg"}},
                        {name: "lisinopril", attributes: {"__ATTR_DOSE__": "12.5 mg"}}]
                },
                29: {
                    "__ATTR_DATE_STARTED__": ["Started " +
                    new Date(Date.now() - 86400000).toDateString().split(" ")[1] + ". " +
                    new Date(Date.now() - 86400000).toDateString().split(" ")[2] + ", " +
                    new Date(Date.now() - 86400000).toDateString().split(" ")[3] + " (1 day ago)"]
                },
            }
        }, // end of PD2_C

        "__DATASET_CONGESTIVE_HEART_FAILURE_1_MODIFIED__": {
            // patient data
            patientFirstName: "Jim",
            patientLastName: "Jones",
            patientAge: 74,
            patientGender: "M",

            // item relationships
            unique1: [3, 8],
            unique2: [16, 18, 20],
            identical: [[5, 12], [7, 13]],
            similar: [
                {items: [0, 11], differences: [visible.ATTR_FREQUENCY]},
                {items: [1, 22], differences: [visible.ATTR_NAME, visible.ATTR_FREQUENCY]},
                {items: [2, 17], differences: [visible.ATTR_NAME, visible.ATTR_FREQUENCY]},
                {items: [4, 15], differences: [visible.ATTR_NAME, visible.ATTR_FREQUENCY]},
                {items: [6, 14], differences: [visible.ATTR_FREQUENCY]},
                {items: [9, 21], differences: [visible.ATTR_NAME, visible.ATTR_FREQUENCY]},
                {items: [10, 19], differences: [visible.ATTR_NAME, visible.ATTR_DOSE, visible.ATTR_FREQUENCY]}],

            // item data
            csv: 'id,origin,recorded name,generic name,brand name,dose,route,frequency,drug classes,diagnoses\n' +
            '0,list0,acetaminophen,acetaminophen,Tylenol,650 mg,PO,q4h prn,"analgesic,antipyretic",pain\n' +
            '1,list0,Aldactone,spironolactone,Aldactone,100 mg,PO,daily,antihypertensive,hypertension\n' +
            '2,list0,Amaryl,glimepiride,Amaryl,4 mg,PO,daily,antidiabetic,diabetes\n' +
            '3,list0,Ambien,zolpidem,Ambien,10 mg,PO,qHS prn,sedative,insomnia\n' +
            '4,list0,Aricept,donepezil,Aricept,10 mg,PO,daily,acetylcholinesterase inhibitor,dementia\n' +
            '5,list0,aspirin,aspirin,Bayer,81 mg,PO,daily,"non-steroidal anti-inflammatory drug,analgesic,antiplatelet,antipyretic","atherosclerotic vascular disease"\n' +
            '6,list0,cimetidine,cimetidine,Tagamet,800 mg,PO,BID,antacid,GERD\n' +
            '7,list0,Coreg,carvedilol,Coreg,6.25 mg,PO,BID,antihypertensive,hypertension\n' +
            '8,list0,Colace,ducosate,Colace,100 mg,PO,BID,stool softener,constipation\n' +
            '9,list0,Crestor,rosuvastatin,Crestor,20 mg,PO,daily,anticholesterol,hypercholesterolemia\n' +
            '10,list0,Cozaar,losartan,Cozaar,25 mg,PO,daily,"antihypertensive","hypertension,congestive heart failure"\n' +
            '11,list1,acetaminophen,acetaminophen,Tylenol,650 mg,PO,q4h prn headache or pain,"analgesic,antipyretic",pain\n' +
            '12,list1,aspirin,aspirin,Bayer,81 mg,PO,daily,"non-steroidal anti-inflammatory drug,analgesic,antiplatelet,antipyretic",atherosclerotic vascular disease\n' +
            '13,list1,Coreg,carvedilol,Coreg,6.25 mg,PO,BID,antihypertensive,hypertension\n' +
            '14,list1,cimetidine,cimetidine,Tagamet,800 mg,PO,q12h,antacid,GERD\n' +
            '15,list1,donepezil,donepezil,Aricept,10 mg,PO,qAM,acetylcholinesterase inhibitor,dementia\n' +
            '16,list1,furosemide,furosemide,Lasix,40 mg,PO,BID,"diuretic,antihypertensive",congestive heart failure\n' +
            '17,list1,glimepiride,glimepiride,Amaryl,4 mg,PO,qAM,antidiabetic,diabetes\n' +
            '18,list1,lorazepam,lorazepam,Ativan,1 mg,PO,qHS prn insomnia,"sedative,antianxiety",insomnia\n' +
            '19,list1,losartan,losartan,Cozaar,50 mg,PO,qAM,antihypertensive,"hypertension,congestive heart failure"\n' +
            '20,list1,magnesium hydroxide,magnesium hydroxide,Milk of magnesia,30 ml,PO,daily prn constipation,"laxative,antacid",constipation\n' +
            '21,list1,rosuvastatin,rosuvastatin,Crestor,20 mg,PO,qAM,anticholesterol,hypercholesterolemia\n' +
            '22,list1,spironolactone,spironolactone,Aldactone,100 mg,PO,qAM,antihypertensive,hypertension',
        }, // end of CHF1_M

        "__DATASET_KWS__": {
            patientFirstName: "NUMMER 35",
            patientLastName: "KWS-TESTPATIENT",
            patientAge: 34,
            patientGender: "man",
            unique1: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 96, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 123, 124, 125, 126, 127, 128, 129, 130],
            unique2: [131, 133, 131, 131, 131, 131, 131, 131, 131, 131, 132, 134, 135, 136, 137, 138, 139, 140, 141],
            identical: [],
            similar: [{
                items: [38, 131],
                differences: [visible.ATTR_NAME, visible.ATTR_DOSE, visible.ATTR_FREQUENCY]
            }, {
                items: [45, 133],
                differences: [visible.ATTR_NAME, visible.ATTR_DOSE, visible.ATTR_FREQUENCY]
            }, {
                items: [103, 131],
                differences: [visible.ATTR_NAME, visible.ATTR_DOSE, visible.ATTR_FREQUENCY]
            }, {
                items: [105, 131],
                differences: [visible.ATTR_NAME, visible.ATTR_DOSE, visible.ATTR_FREQUENCY]
            }, {
                items: [109, 131],
                differences: [visible.ATTR_NAME, visible.ATTR_DOSE, visible.ATTR_FREQUENCY]
            }, {
                items: [110, 131],
                differences: [visible.ATTR_NAME, visible.ATTR_DOSE, visible.ATTR_FREQUENCY]
            }, {
                items: [119, 131],
                differences: [visible.ATTR_NAME, visible.ATTR_DOSE, visible.ATTR_FREQUENCY]
            }, {
                items: [120, 131],
                differences: [visible.ATTR_NAME, visible.ATTR_DOSE, visible.ATTR_FREQUENCY]
            }, {
                items: [121, 131],
                differences: [visible.ATTR_NAME, visible.ATTR_DOSE, visible.ATTR_FREQUENCY]
            }, {items: [122, 131], differences: [visible.ATTR_NAME, visible.ATTR_DOSE, visible.ATTR_FREQUENCY]}],
            csv: "id,origin,recorded name,generic name,brand name,dose,route,frequency,drug classes,diagnoses\n" + "0,list0,\"Caelyx flacon inf. 50 mg/25 ml\",\"Caelyx flacon inf. 50 mg/25 ml\",\"Caelyx flacon inf. 50 mg/25 ml\",[UNIT],\"perf.\",\"[null]\", \"\",\n" + "1,list0,\"Tradonal caps. Retard (vertraagde vrijst.) 100 mg\",\"Tradonal caps. Retard (vertraagde vrijst.) 100 mg\",\"Tradonal caps. Retard (vertraagde vrijst.) 100 mg\",[UNIT],\"or.\",\"[null]\", \"\",\n" + "2,list0,\"Cipramil compr. (deelb.) 20 mg\",\"Cipramil compr. (deelb.) 20 mg\",\"Cipramil compr. (deelb.) 20 mg\",[UNIT],\"or.\",\"Per day\", \"\",\n" + "3,list0,\"Tradonal caps. 50 mg\",\"Tradonal caps. 50 mg\",\"Tradonal caps. 50 mg\",[UNIT],\"or.\",\"[null]\", \"\",\n" + "4,list0,\"Tradonal caps. Retard (vertraagde vrijst.) 150 mg\",\"Tradonal caps. Retard (vertraagde vrijst.) 150 mg\",\"Tradonal caps. Retard (vertraagde vrijst.) 150 mg\",[UNIT],\"or.\",\"[null]\", \"\",\n" + "5,list0,\"REDOMEX\",\"REDOMEX\",\"REDOMEX\",[UNIT],\"or.\",\"Per day\", \"\",\n" + "6,list0,\"Asaflow compr. (maagsapresist.) 80 mg\",\"Asaflow compr. (maagsapresist.) 80 mg\",\"Asaflow compr. (maagsapresist.) 80 mg\",[UNIT],\"or.\",\"Per day\", \"\",\n" + "7,list0,\"Asaflow compr. (maagsapresist.) 80 mg\",\"Asaflow compr. (maagsapresist.) 80 mg\",\"Asaflow compr. (maagsapresist.) 80 mg\",[UNIT],\"or.\",\"Per day\", \"\",\n" + "8,list0,\"DOBUTREX MYLAN (AMP  250 MG/20 ML) 600 mg GLUCOSE  5 % (FL INF    50 ML VIAFLO) 2 ml\",\"DOBUTREX MYLAN (AMP  250 MG/20 ML) 600 mg GLUCOSE  5 % (FL INF    50 ML VIAFLO) 2 ml\",\"DOBUTREX MYLAN (AMP  250 MG/20 ML) 600 mg GLUCOSE  5 % (FL INF    50 ML VIAFLO) 2 ml\",[UNIT],\"perf.\",\"[null]\", \"\",\n" + "9,list0,\"Crestor compr. 20 mg\",\"Crestor compr. 20 mg\",\"Crestor compr. 20 mg\",[UNIT],\"or.\",\"Per day\", \"\",\n" + "10,list0,\"Emconcor compr. Minor (deelb.) 2,5 mg\",\"Emconcor compr. Minor (deelb.) 2,5 mg\",\"Emconcor compr. Minor (deelb.) 2,5 mg\",[UNIT],\"or.\",\"Per day\", \"\",\n" + "11,list0,\"Euthyrox compr. (deelb.) 0,1 mg\",\"Euthyrox compr. (deelb.) 0,1 mg\",\"Euthyrox compr. (deelb.) 0,1 mg\",[UNIT],\"or.\",\"[null]\", \"\",\n" + "12,list0,\"NATR.CHLORIDE 0,9 % (FL INF 1.000 ML VIAFLO) 1 infuus KALIUMCHLORIDE (FL INJ  20 MEQ/20 ML) 2 amp\",\"NATR.CHLORIDE 0,9 % (FL INF 1.000 ML VIAFLO) 1 infuus KALIUMCHLORIDE (FL INJ  20 MEQ/20 ML) 2 amp\",\"NATR.CHLORIDE 0,9 % (FL INF 1.000 ML VIAFLO) 1 infuus KALIUMCHLORIDE (FL INJ  20 MEQ/20 ML) 2 amp\",[UNIT],\"perf.\",\"[null]\", \"\",\n" + "13,list0,\"Calciumcarbonaat 1 g\",\"Calciumcarbonaat 1 g\",\"Calciumcarbonaat 1 g\",[UNIT],\"or.\",\"Per day\", \"\",\n" + "14,list0,\"Miacalcic spuitamp. i.m. - i.v. - s.c. 100 IE/1 ml\",\"Miacalcic spuitamp. i.m. - i.v. - s.c. 100 IE/1 ml\",\"Miacalcic spuitamp. i.m. - i.v. - s.c. 100 IE/1 ml\",[UNIT],\"i.m.\",\"Per day\", \"\",\n" + "15,list0,\"REDOXON\",\"REDOXON\",\"REDOXON\",[UNIT],\"or.\",\"Per day\", \"\",\n" + "16,list0,\"Tradonal caps. Retard (vertraagde vrijst.) 100 mg\",\"Tradonal caps. Retard (vertraagde vrijst.) 100 mg\",\"Tradonal caps. Retard (vertraagde vrijst.) 100 mg\",[UNIT],\"or.\",\"[null]\", \"\",\n" + "17,list0,\"Tradonal caps. Retard (vertraagde vrijst.) 200 mg\",\"Tradonal caps. Retard (vertraagde vrijst.) 200 mg\",\"Tradonal caps. Retard (vertraagde vrijst.) 200 mg\",[UNIT],\"or.\",\"[null]\", \"\",\n" + "18,list0,\"Stilnoct compr. (deelb.) 10 mg\",\"Stilnoct compr. (deelb.) 10 mg\",\"Stilnoct compr. (deelb.) 10 mg\",[UNIT],\"or.\",\"[null]\", \"\",\n" + "19,list0,\"Bisoprolol EG compr. (deelb.) 10 mg\",\"Bisoprolol EG compr. (deelb.) 10 mg\",\"Bisoprolol EG compr. (deelb.) 10 mg\",[UNIT],\"or.\",\"[null]\", \"\",\n" + "20,list0,\"Bisoprolol EG compr. (deelb.) 10 mg\",\"Bisoprolol EG compr. (deelb.) 10 mg\",\"Bisoprolol EG compr. (deelb.) 10 mg\",[UNIT],\"or.\",\"Per 5 weken\", \"\",\n" + "21,list0,\"LEVOPHED (AMP INJ 4 MG BASE/4 ML) 12 mg NATR.CHLORIDE 0,9 % (FL INF    50 ML) 50 ml\",\"LEVOPHED (AMP INJ 4 MG BASE/4 ML) 12 mg NATR.CHLORIDE 0,9 % (FL INF    50 ML) 50 ml\",\"LEVOPHED (AMP INJ 4 MG BASE/4 ML) 12 mg NATR.CHLORIDE 0,9 % (FL INF    50 ML) 50 ml\",[UNIT],\"perf.\",\"[null]\", \"\",\n" + "22,list0,\"CONTRAMAL (AMP INJ 100 MG/2 ML) 400 mg LITICAN (AMP INJ 50 MG/2 ML) 100 mg NATR.CHLORIDE 0,9 % (FL INF   500 ML VIAFLO) 500 ml\",\"CONTRAMAL (AMP INJ 100 MG/2 ML) 400 mg LITICAN (AMP INJ 50 MG/2 ML) 100 mg NATR.CHLORIDE 0,9 % (FL INF   500 ML VIAFLO) 500 ml\",\"CONTRAMAL (AMP INJ 100 MG/2 ML) 400 mg LITICAN (AMP INJ 50 MG/2 ML) 100 mg NATR.CHLORIDE 0,9 % (FL INF   500 ML VIAFLO) 500 ml\",[UNIT],\"perf.\",\"[null]\", \"\",\n" + "23,list0,\"DYNATRA (AMP INJ  50 MG/1,25 ML) 150 mg NATR.CHLORIDE 0,9 % (FL INF    50 ML VIAFLO) 46.3 ml\",\"DYNATRA (AMP INJ  50 MG/1,25 ML) 150 mg NATR.CHLORIDE 0,9 % (FL INF    50 ML VIAFLO) 46.3 ml\",\"DYNATRA (AMP INJ  50 MG/1,25 ML) 150 mg NATR.CHLORIDE 0,9 % (FL INF    50 ML VIAFLO) 46.3 ml\",[UNIT],\"perf.\",\"[null]\", \"\",\n" + "24,list0,\"NATR.CHLORIDE 0,9 % (FL INF 1.000 ML VIAFLO) 6342.4 ml KALIUMCHLORIDE (FL INJ  20 MEQ/20 ML) 20 meq/l\",\"NATR.CHLORIDE 0,9 % (FL INF 1.000 ML VIAFLO) 6342.4 ml KALIUMCHLORIDE (FL INJ  20 MEQ/20 ML) 20 meq/l\",\"NATR.CHLORIDE 0,9 % (FL INF 1.000 ML VIAFLO) 6342.4 ml KALIUMCHLORIDE (FL INJ  20 MEQ/20 ML) 20 meq/l\",[UNIT],\"perf.\",\"[null]\", \"\",\n" + "25,list0,\"AUGMENTIN (FL INJ 1 G - 200 MG) 1 g NATR.CHLORIDE 0,9 % (FL INF    50 ML VIAFLO) 50 ml\",\"AUGMENTIN (FL INJ 1 G - 200 MG) 1 g NATR.CHLORIDE 0,9 % (FL INF    50 ML VIAFLO) 50 ml\",\"AUGMENTIN (FL INJ 1 G - 200 MG) 1 g NATR.CHLORIDE 0,9 % (FL INF    50 ML VIAFLO) 50 ml\",[UNIT],\"perf.\",\"[null]\", \"\",\n" + "26,list0,\"CONTRAMAL (AMP INJ 100 MG/2 ML) 400 mg LITICAN (AMP INJ 50 MG/2 ML) 100 mg NATR.CHLORIDE 0,9 % (FL INF   500 ML VIAFLO) ? ml\",\"CONTRAMAL (AMP INJ 100 MG/2 ML) 400 mg LITICAN (AMP INJ 50 MG/2 ML) 100 mg NATR.CHLORIDE 0,9 % (FL INF   500 ML VIAFLO) ? ml\",\"CONTRAMAL (AMP INJ 100 MG/2 ML) 400 mg LITICAN (AMP INJ 50 MG/2 ML) 100 mg NATR.CHLORIDE 0,9 % (FL INF   500 ML VIAFLO) ? ml\",[UNIT],\"perf.\",\"[null]\", \"\",\n" + "27,list0,\"MESNA  + NATR.CHLORIDE 0.9%\",\"MESNA  + NATR.CHLORIDE 0.9%\",\"MESNA  + NATR.CHLORIDE 0.9%\",[UNIT],\"i.v.\",\"Per day\", \"\",\n" + "28,list0,\"Norit Carbomix gran. 50 g/61,5 g\",\"Norit Carbomix gran. 50 g/61,5 g\",\"Norit Carbomix gran. 50 g/61,5 g\",[UNIT],\"or.\",\"Per day\", \"\",\n" + "29,list0,\"Norit Carbomix gran. 50 g/61,5 g\",\"Norit Carbomix gran. 50 g/61,5 g\",\"Norit Carbomix gran. 50 g/61,5 g\",[UNIT],\"or.\",\"Per day\", \"\",\n" + "30,list0,\"Emconcor compr. Mitis (deelb.) 5 mg\",\"Emconcor compr. Mitis (deelb.) 5 mg\",\"Emconcor compr. Mitis (deelb.) 5 mg\",[UNIT],\"or.\",\"Per day\", \"\",\n" + "31,list0,\"PERIO AID MONDSPOEL.\",\"PERIO AID MONDSPOEL.\",\"PERIO AID MONDSPOEL.\",[UNIT],\"[null]\",\"Per day\", \"\",\n" + "32,list0,\"Stilnoct compr. (deelb.) 10 mg\",\"Stilnoct compr. (deelb.) 10 mg\",\"Stilnoct compr. (deelb.) 10 mg\",[UNIT],\"or.\",\"Per day\", \"\",\n" + "33,list0,\"Seloken compr. (deelb.) 100 mg\",\"Seloken compr. (deelb.) 100 mg\",\"Seloken compr. (deelb.) 100 mg\",[UNIT],\"or.\",\"Per day\", \"\",\n" + "34,list0,\"Litican amp. i.m. - i.v. 50 mg/2 ml\",\"Litican amp. i.m. - i.v. 50 mg/2 ml\",\"Litican amp. i.m. - i.v. 50 mg/2 ml\",[UNIT],\"i.v.\",\"Per day\", \"\",\n" + "35,list0,\"Atropine Sulfate Sterop amp. i.v. - s.c. 0,5 mg/1 ml\",\"Atropine Sulfate Sterop amp. i.v. - s.c. 0,5 mg/1 ml\",\"Atropine Sulfate Sterop amp. i.v. - s.c. 0,5 mg/1 ml\",[UNIT],\"i.v.\",\"Per day\", \"\",\n" + "36,list0,\"Movicol poeder (zakjes) 13 g+ 350 mg+ 178 mg[+3]\",\"Movicol poeder (zakjes) 13 g+ 350 mg+ 178 mg[+3]\",\"Movicol poeder (zakjes) 13 g+ 350 mg+ 178 mg[+3]\",[UNIT],\"or.\",\"Per day\", \"\",\n" + "37,list0,\"KALIUMCHLORIDE\",\"KALIUMCHLORIDE\",\"KALIUMCHLORIDE\",[UNIT],\"perf.\",\"Per day\", \"\",\n" + "38,list0,\"Paracetamol Fresenius Kabi flacon inf. 1 g/100 ml\",\"Paracetamol Fresenius Kabi flacon inf. 1 g/100 ml\",\"Paracetamol Fresenius Kabi flacon inf. 1 g/100 ml\",[UNIT],\"perf.\",\"Per day\", \"\",\n" + "39,list0,\"GLUCION  5 %\",\"GLUCION  5 %\",\"GLUCION  5 %\",[UNIT],\"perf.\",\"Per day\", \"\",\n" + "40,list0,\"Lasix compr. (deelb.) 40 mg\",\"Lasix compr. (deelb.) 40 mg\",\"Lasix compr. (deelb.) 40 mg\",[UNIT],\"or.\",\"Per day\", \"\",\n" + "41,list0,\"Solu-Cortef amp. i.m. - i.v. - inf. 100 mg\",\"Solu-Cortef amp. i.m. - i.v. - inf. 100 mg\",\"Solu-Cortef amp. i.m. - i.v. - inf. 100 mg\",[UNIT],\"i.v.\",\"Per day\", \"\",\n" + "42,list0,\"Linisol amp. i.v. - locoregionale infiltr. - in situ - epiduraal 10 mg/ ml\",\"Linisol amp. i.v. - locoregionale infiltr. - in situ - epiduraal 10 mg/ ml\",\"Linisol amp. i.v. - locoregionale infiltr. - in situ - epiduraal 10 mg/ ml\",[UNIT],\"s.c.\",\"Per day\", \"\",\n" + "43,list0,\"Ventolin doseeraërosol 100 µg/ dosis\",\"Ventolin doseeraërosol 100 µg/ dosis\",\"Ventolin doseeraërosol 100 µg/ dosis\",[UNIT],\"inhal.\",\"Per day\", \"\",\n" + "44,list0,\"Nimotop flacon inf. - i.cistern. 0,2 mg/ ml\",\"Nimotop flacon inf. - i.cistern. 0,2 mg/ ml\",\"Nimotop flacon inf. - i.cistern. 0,2 mg/ ml\",[UNIT],\"perf.\",\"Per day\", \"\",\n" + "45,list0,\"Clamoxyl flacon i.m. - i.v. 1 g\",\"Clamoxyl flacon i.m. - i.v. 1 g\",\"Clamoxyl flacon i.m. - i.v. 1 g\",[UNIT],\"i.v.\",\"Per day\", \"\",\n" + "46,list0,\"Valium amp. i.m. - i.v. - inf. - rectaal 10 mg/2 ml\",\"Valium amp. i.m. - i.v. - inf. - rectaal 10 mg/2 ml\",\"Valium amp. i.m. - i.v. - inf. - rectaal 10 mg/2 ml\",[UNIT],\"i.v.\",\"Per day\", \"\",\n" + "47,list0,\"Valium amp. i.m. - i.v. - inf. - rectaal 10 mg/2 ml\",\"Valium amp. i.m. - i.v. - inf. - rectaal 10 mg/2 ml\",\"Valium amp. i.m. - i.v. - inf. - rectaal 10 mg/2 ml\",[UNIT],\"i.v.\",\"Per day\", \"\",\n" + "48,list0,\"Augmentin compr. (deelb.) 500 mg+ 125 mg\",\"Augmentin compr. (deelb.) 500 mg+ 125 mg\",\"Augmentin compr. (deelb.) 500 mg+ 125 mg\",[UNIT],\"or.\",\"Per day\", \"\",\n" + "49,list0,\"GLUCOSE  5 %\",\"GLUCOSE  5 %\",\"GLUCOSE  5 %\",[UNIT],\"perf.\",\"Per day\", \"\",\n" + "50,list0,\"GLUCOSE  5 %\",\"GLUCOSE  5 %\",\"GLUCOSE  5 %\",[UNIT],\"perf.\",\"Per day\", \"\",\n" + "51,list0,\"Aspegic flacon i.m. - i.v. 500 mg\",\"Aspegic flacon i.m. - i.v. 500 mg\",\"Aspegic flacon i.m. - i.v. 500 mg\",[UNIT],\"i.v.\",\"Per day\", \"\",\n" + "52,list0,\"Amlor caps. 5 mg\",\"Amlor caps. 5 mg\",\"Amlor caps. 5 mg\",[UNIT],\"or.\",\"Per day\", \"\",\n" + "53,list0,\"Zofran amp. i.m. - i.v. - inf. 4 mg/2 ml\",\"Zofran amp. i.m. - i.v. - inf. 4 mg/2 ml\",\"Zofran amp. i.m. - i.v. - inf. 4 mg/2 ml\",[UNIT],\"i.v.\",\"Per day\", \"\",\n" + "54,list0,\"Daflon compr. 450 mg+ 50 mg\",\"Daflon compr. 450 mg+ 50 mg\",\"Daflon compr. 450 mg+ 50 mg\",[UNIT],\"or.\",\"Per day\", \"\",\n" + "55,list0,\"Daflon compr. 450 mg+ 50 mg\",\"Daflon compr. 450 mg+ 50 mg\",\"Daflon compr. 450 mg+ 50 mg\",[UNIT],\"or.\",\"Per day\", \"\",\n" + "56,list0,\"Lambipol compr. (disp.) 200 mg\",\"Lambipol compr. (disp.) 200 mg\",\"Lambipol compr. (disp.) 200 mg\",[UNIT],\"or.\",\"[null]\", \"\",\n" + "57,list0,\"Imodium compr. Instant (orodisp.) 2 mg\",\"Imodium compr. Instant (orodisp.) 2 mg\",\"Imodium compr. Instant (orodisp.) 2 mg\",[UNIT],\"or.\",\"Per day\", \"\",\n" + "58,list0,\"Zyloric compr. (deelb.) 300 mg\",\"Zyloric compr. (deelb.) 300 mg\",\"Zyloric compr. (deelb.) 300 mg\",[UNIT],\"or.\",\"Per day\", \"\",\n" + "59,list0,\"METHYLPREDNISOLONE PO\",\"METHYLPREDNISOLONE PO\",\"METHYLPREDNISOLONE PO\",[UNIT],\"or.\",\"Per day\", \"\",\n" + "60,list0,\"METHYLPREDNISOLONE PO\",\"METHYLPREDNISOLONE PO\",\"METHYLPREDNISOLONE PO\",[UNIT],\"or.\",\"Per day\", \"\",\n" + "61,list0,\"CAPECITABINE\",\"CAPECITABINE\",\"CAPECITABINE\",[UNIT],\"or.\",\"Per day\", \"\",\n" + "62,list0,\"CAPECITABINE\",\"CAPECITABINE\",\"CAPECITABINE\",[UNIT],\"or.\",\"Per day\", \"\",\n" + "63,list0,\"Litican amp. i.m. - i.v. 50 mg/2 ml\",\"Litican amp. i.m. - i.v. 50 mg/2 ml\",\"Litican amp. i.m. - i.v. 50 mg/2 ml\",[UNIT],\"i.v.\",\"Per day\", \"\",\n" + "64,list0,\"Litican amp. i.m. - i.v. 50 mg/2 ml\",\"Litican amp. i.m. - i.v. 50 mg/2 ml\",\"Litican amp. i.m. - i.v. 50 mg/2 ml\",[UNIT],\"i.v.\",\"Per day\", \"\",\n" + "65,list0,\"NATR.CHLORIDE 0.9%\",\"NATR.CHLORIDE 0.9%\",\"NATR.CHLORIDE 0.9%\",[UNIT],\"perf.\",\"Per day\", \"\",\n" + "66,list0,\"spoelen\",\"spoelen\",\"spoelen\",[UNIT],\"[null]\",\"Per day\", \"\",\n" + "67,list0,\"PERIO AID MONDSPOEL.\",\"PERIO AID MONDSPOEL.\",\"PERIO AID MONDSPOEL.\",[UNIT],\"muc.\",\"Per day\", \"\",\n" + "68,list0,\"NATR.CHLORIDE 0.9%\",\"NATR.CHLORIDE 0.9%\",\"NATR.CHLORIDE 0.9%\",[UNIT],\"perf.\",\"Per day\", \"\",\n" + "69,list0,\"VINCRISTINE  + NATR.CHLORIDE 0.9%\",\"VINCRISTINE  + NATR.CHLORIDE 0.9%\",\"VINCRISTINE  + NATR.CHLORIDE 0.9%\",[UNIT],\"perf.\",\"Per day\", \"\",\n" + "70,list0,\"VINCRISTINE  + NATR.CHLORIDE 0.9%\",\"VINCRISTINE  + NATR.CHLORIDE 0.9%\",\"VINCRISTINE  + NATR.CHLORIDE 0.9%\",[UNIT],\"perf.\",\"Per day\", \"\",\n" + "71,list0,\"CYCLOFOSFAMIDE PER OS\",\"CYCLOFOSFAMIDE PER OS\",\"CYCLOFOSFAMIDE PER OS\",[UNIT],\"or.\",\"Per day\", \"\",\n" + "72,list0,\"Litican compr. (deelb.) 50 mg\",\"Litican compr. (deelb.) 50 mg\",\"Litican compr. (deelb.) 50 mg\",[UNIT],\"or.\",\"Per day\", \"\",\n" + "73,list0,\"BEPANTHOL\",\"BEPANTHOL\",\"BEPANTHOL\",[UNIT],\"local\",\"Per day\", \"\",\n" + "74,list0,\"Litican compr. (deelb.) 50 mg\",\"Litican compr. (deelb.) 50 mg\",\"Litican compr. (deelb.) 50 mg\",[UNIT],\"or.\",\"Per day\", \"\",\n" + "75,list0,\"Ultra-K oploss. (oraal) 4,68 g/15 ml\",\"Ultra-K oploss. (oraal) 4,68 g/15 ml\",\"Ultra-K oploss. (oraal) 4,68 g/15 ml\",[UNIT],\"or.\",\"[null]\", \"\",\n" + "76,list0,\"Lipitor compr. 20 mg\",\"Lipitor compr. 20 mg\",\"Lipitor compr. 20 mg\",[UNIT],\"or.\",\"Per day\", \"\",\n" + "77,list0,\"Prograft caps. 1 mg\",\"Prograft caps. 1 mg\",\"Prograft caps. 1 mg\",[UNIT],\"or.\",\"Per day\", \"\",\n" + "78,list0,\"Prograft caps. 1 mg\",\"Prograft caps. 1 mg\",\"Prograft caps. 1 mg\",[UNIT],\"or.\",\"Per day\", \"\",\n" + "79,list0,\"Prograft caps. 1 mg\",\"Prograft caps. 1 mg\",\"Prograft caps. 1 mg\",[UNIT],\"or.\",\"Per day\", \"\",\n" + "80,list0,\"Prograft caps. 1 mg\",\"Prograft caps. 1 mg\",\"Prograft caps. 1 mg\",[UNIT],\"or.\",\"Per day\", \"\",\n" + "81,list0,\"Prograft caps. 5 mg\",\"Prograft caps. 5 mg\",\"Prograft caps. 5 mg\",[UNIT],\"or.\",\"Per 2 dagen\", \"\",\n" + "82,list0,\"Prograft caps. 5 mg\",\"Prograft caps. 5 mg\",\"Prograft caps. 5 mg\",[UNIT],\"or.\",\"Per 2 dagen\", \"\",\n" + "83,list0,\"Prograft caps. 5 mg\",\"Prograft caps. 5 mg\",\"Prograft caps. 5 mg\",[UNIT],\"or.\",\"Per 2 dagen\", \"\",\n" + "84,list0,\"Lipitor compr. 20 mg\",\"Lipitor compr. 20 mg\",\"Lipitor compr. 20 mg\",[UNIT],\"or.\",\"Per day\", \"\",\n" + "85,list0,\"Lipitor compr. 20 mg\",\"Lipitor compr. 20 mg\",\"Lipitor compr. 20 mg\",[UNIT],\"or.\",\"Per day\", \"\",\n" + "86,list0,\"Lipitor compr. 40 mg\",\"Lipitor compr. 40 mg\",\"Lipitor compr. 40 mg\",[UNIT],\"or.\",\"Per day\", \"\",\n" + "87,list0,\"Prograft caps. 5 mg\",\"Prograft caps. 5 mg\",\"Prograft caps. 5 mg\",[UNIT],\"or.\",\"Per day\", \"\",\n" + "88,list0,\"Augmentin flacon i.v. 1 g+ 200 mg\",\"Augmentin flacon i.v. 1 g+ 200 mg\",\"Augmentin flacon i.v. 1 g+ 200 mg\",[UNIT],\"perf.\",\"Per day\", \"\",\n" + "89,list0,\"Aacidexam amp. i.m. - i.v. - in situ 5 mg/1 ml\",\"Aacidexam amp. i.m. - i.v. - in situ 5 mg/1 ml\",\"Aacidexam amp. i.m. - i.v. - in situ 5 mg/1 ml\",[UNIT],\"i.v.\",\"[null]\", \"\",\n" + "90,list0,\"11-PIR-11 BEACON-LOPERAMIDE\",\"11-PIR-11 BEACON-LOPERAMIDE\",\"11-PIR-11 BEACON-LOPERAMIDE\",[UNIT],\"i.v.\",\"[null]\", \"\",\n" + "91,list0,\"Duovent doseeraërosol HFA 20 µg+ 50 µg/ dosis\",\"Duovent doseeraërosol HFA 20 µg+ 50 µg/ dosis\",\"Duovent doseeraërosol HFA 20 µg+ 50 µg/ dosis\",[UNIT],\"inhal.\",\"[null]\", \"\",\n" + "92,list0,\"Fraxiparine spuitamp. s.c. 2850 IE anti-Xa/0,3 ml\",\"Fraxiparine spuitamp. s.c. 2850 IE anti-Xa/0,3 ml\",\"Fraxiparine spuitamp. s.c. 2850 IE anti-Xa/0,3 ml\",[UNIT],\"s.c.\",\"[null]\", \"\",\n" + "93,list0,\"Lanoxin compr. (deelb.) 0,25 mg\",\"Lanoxin compr. (deelb.) 0,25 mg\",\"Lanoxin compr. (deelb.) 0,25 mg\",[UNIT],\"or.\",\"[null]\", \"\",\n" + "94,list0,\"Mondspoelmiddel met difenhydramine en lidocaïne (stomatitiscocktail) ---------------------------------- Difenhydramine HCl 300 mg Lidocaïne HCl 1500 mg Xanthaangom 1800mg Water tot 300 ml\",\"Mondspoelmiddel met difenhydramine en lidocaïne (stomatitiscocktail) ---------------------------------- Difenhydramine HCl 300 mg Lidocaïne HCl 1500 mg Xanthaangom 1800mg Water tot 300 ml\",\"Mondspoelmiddel met difenhydramine en lidocaïne (stomatitiscocktail) ---------------------------------- Difenhydramine HCl 300 mg Lidocaïne HCl 1500 mg Xanthaangom 1800mg Water tot 300 ml\",[UNIT],\"or.\",\"[null]\", \"\",\n" + "95,list0,\"Transtec transdermaal systeem 35 µg/ u\",\"Transtec transdermaal systeem 35 µg/ u\",\"Transtec transdermaal systeem 35 µg/ u\",[UNIT],\"derm.\",\"[null]\", \"\",\n" + "96,list0,\"MORFINE HCL\",\"MORFINE HCL\",\"MORFINE HCL\",[UNIT],\"i.v.\",\"[null]\", \"\",\n" + "97,list0,\"MORFINE HCL\",\"MORFINE HCL\",\"MORFINE HCL\",[UNIT],\"s.c.\",\"[null]\", \"\",\n" + "98,list0,\"SIMVASTAMED\",\"SIMVASTAMED\",\"SIMVASTAMED\",[UNIT],\"or.\",\"[null]\", \"\",\n" + "99,list0,\"Pantomed compr. (maagsapresist.) 20 mg\",\"Pantomed compr. (maagsapresist.) 20 mg\",\"Pantomed compr. (maagsapresist.) 20 mg\",[UNIT],\"or.\",\"[null]\", \"\",\n" + "100,list0,\"Lipitor compr. 40 mg\",\"Lipitor compr. 40 mg\",\"Lipitor compr. 40 mg\",[UNIT],\"or.\",\"Per 2 weken\", \"\",\n" + "101,list0,\"Novorapid patroon spuit s.c. FlexPen 100 E/ ml\",\"Novorapid patroon spuit s.c. FlexPen 100 E/ ml\",\"Novorapid patroon spuit s.c. FlexPen 100 E/ ml\",[UNIT],\"s.c.\",\"[null]\", \"\",\n" + "102,list0,\"Novorapid patroon spuit s.c. FlexPen 100 E/ ml\",\"Novorapid patroon spuit s.c. FlexPen 100 E/ ml\",\"Novorapid patroon spuit s.c. FlexPen 100 E/ ml\",[UNIT],\"s.c.\",\"[null]\", \"\",\n" + "103,list0,\"Perdolan compr. (deelb.) 500 mg\",\"Perdolan compr. (deelb.) 500 mg\",\"Perdolan compr. (deelb.) 500 mg\",[UNIT],\"or.\",\"[null]\", \"\",\n" + "104,list0,\"NATR.CHLORIDE  0,9 %\",\"NATR.CHLORIDE  0,9 %\",\"NATR.CHLORIDE  0,9 %\",[UNIT],\"or.\",\"[null]\", \"\",\n" + "105,list0,\"Dafalgan bruiscompr. (deelb.) 500 mg\",\"Dafalgan bruiscompr. (deelb.) 500 mg\",\"Dafalgan bruiscompr. (deelb.) 500 mg\",[UNIT],\"or.\",\"Per day\", \"\",\n" + "106,list0,\"D-CURE\",\"D-CURE\",\"D-CURE\",[UNIT],\"or.\",\"[null]\", \"\",\n" + "107,list0,\"1 Alpha Leo caps. 0,25 µg\",\"1 Alpha Leo caps. 0,25 µg\",\"1 Alpha Leo caps. 0,25 µg\",[UNIT],\"or.\",\"[null]\", \"\",\n" + "108,list0,\"Advagraf caps. (vertraagde vrijst.) 1 mg\",\"Advagraf caps. (vertraagde vrijst.) 1 mg\",\"Advagraf caps. (vertraagde vrijst.) 1 mg\",[UNIT],\"or.\",\"[null]\", \"\",\n" + "109,list0,\"Dafalgan compr. 500 mg\",\"Dafalgan compr. 500 mg\",\"Dafalgan compr. 500 mg\",[UNIT],\"or.\",\"[null]\", \"\",\n" + "110,list0,\"Dafalgan compr. 500 mg\",\"Dafalgan compr. 500 mg\",\"Dafalgan compr. 500 mg\",[UNIT],\"or.\",\"[null]\", \"\",\n" + "111,list0,\"Novorapid patroon spuit s.c. FlexPen 100 E/ ml\",\"Novorapid patroon spuit s.c. FlexPen 100 E/ ml\",\"Novorapid patroon spuit s.c. FlexPen 100 E/ ml\",[UNIT],\"s.c.\",\"[null]\", \"\",\n" + "112,list0,\"Xanax compr. (deelb.) 1 mg\",\"Xanax compr. (deelb.) 1 mg\",\"Xanax compr. (deelb.) 1 mg\",[UNIT],\"or.\",\"[null]\", \"\",\n" + "113,list0,\"Rilatine compr. (deelb.) 10 mg\",\"Rilatine compr. (deelb.) 10 mg\",\"Rilatine compr. (deelb.) 10 mg\",[UNIT],\"or.\",\"[null]\", \"\",\n" + "114,list0,\"Lyrica caps. 150 mg\",\"Lyrica caps. 150 mg\",\"Lyrica caps. 150 mg\",[UNIT],\"or.\",\"[null]\", \"\",\n" + "115,list0,\"Pantomed compr. (maagsapresist.) 20 mg\",\"Pantomed compr. (maagsapresist.) 20 mg\",\"Pantomed compr. (maagsapresist.) 20 mg\",[UNIT],\"or.\",\"[null]\", \"\",\n" + "116,list0,\"Dafalgan Codeine bruiscompr. (deelb.) 500 mg+ 30 mg\",\"Dafalgan Codeine bruiscompr. (deelb.) 500 mg+ 30 mg\",\"Dafalgan Codeine bruiscompr. (deelb.) 500 mg+ 30 mg\",[UNIT],\"or.\",\"[null]\", \"\",\n" + "117,list0,\"Dafalgan Codeine bruiscompr. (deelb.) 500 mg+ 30 mg\",\"Dafalgan Codeine bruiscompr. (deelb.) 500 mg+ 30 mg\",\"Dafalgan Codeine bruiscompr. (deelb.) 500 mg+ 30 mg\",[UNIT],\"or.\",\"[null]\", \"\",\n" + "118,list0,\"Aacidexam amp. i.m. - i.v. - in situ 5 mg/1 ml\",\"Aacidexam amp. i.m. - i.v. - in situ 5 mg/1 ml\",\"Aacidexam amp. i.m. - i.v. - in situ 5 mg/1 ml\",[UNIT],\"i.v.\",\"[null]\", \"\",\n" + "119,list0,\"Dafalgan compr. 500 mg\",\"Dafalgan compr. 500 mg\",\"Dafalgan compr. 500 mg\",[UNIT],\"or.\",\"[null]\", \"\",\n" + "120,list0,\"Dafalgan compr. 500 mg\",\"Dafalgan compr. 500 mg\",\"Dafalgan compr. 500 mg\",[UNIT],\"or.\",\"[null]\", \"\",\n" + "121,list0,\"Dafalgan compr. 500 mg\",\"Dafalgan compr. 500 mg\",\"Dafalgan compr. 500 mg\",[UNIT],\"or.\",\"[null]\", \"\",\n" + "122,list0,\"Dafalgan compr. 500 mg\",\"Dafalgan compr. 500 mg\",\"Dafalgan compr. 500 mg\",[UNIT],\"or.\",\"[null]\", \"\",\n" + "123,list0,\"Fraxiparine spuitamp. s.c. 3800 IE anti-Xa/0,4 ml\",\"Fraxiparine spuitamp. s.c. 3800 IE anti-Xa/0,4 ml\",\"Fraxiparine spuitamp. s.c. 3800 IE anti-Xa/0,4 ml\",[UNIT],\"s.c.\",\"[null]\", \"\",\n" + "124,list0,\"Dafalgan Codeine compr. 500 mg+ 30 mg\",\"Dafalgan Codeine compr. 500 mg+ 30 mg\",\"Dafalgan Codeine compr. 500 mg+ 30 mg\",[UNIT],\"or.\",\"Per day\", \"\",\n" + "125,list0,\"RIOCIGUAT - EARLY ACCESS\",\"RIOCIGUAT - EARLY ACCESS\",\"RIOCIGUAT - EARLY ACCESS\",[UNIT],\"or.\",\"[null]\", \"\",\n" + "126,list0,\"magistr. test\",\"magistr. test\",\"magistr. test\",[UNIT],\"or.\",\"[null]\", \"\",\n" + "127,list0,\"Advagraf caps. (vertraagde vrijst.) 1 mg\",\"Advagraf caps. (vertraagde vrijst.) 1 mg\",\"Advagraf caps. (vertraagde vrijst.) 1 mg\",[UNIT],\"or.\",\"[null]\", \"\",\n" + "128,list0,\"Dafalgan Codeine compr. 500 mg+ 30 mg\",\"Dafalgan Codeine compr. 500 mg+ 30 mg\",\"Dafalgan Codeine compr. 500 mg+ 30 mg\",[UNIT],\"or.\",\"[null]\", \"\",\n" + "129,list0,\"Daclatasvir 60 mg\",\"Daclatasvir 60 mg\",\"Daclatasvir 60 mg\",[UNIT],\"or.\",\"[null]\", \"\",\n" + "130,list0,\"Bisoprolol EG compr. (deelb.) 2,5 mg\",\"Bisoprolol EG compr. (deelb.) 2,5 mg\",\"Bisoprolol EG compr. (deelb.) 2,5 mg\",[UNIT],\"or.\",\"[null]\", \"\",\n" + "131,list1,\"Dafalgan tab 30x 500mg\",\"Dafalgan tab 30x 500mg\",\"Dafalgan tab 30x 500mg\",[UNIT],\"auric.\",\"Per 7 dagen\", \"\",\n" + "132,list1,\"Marcoumar tab 25x 3mg\",\"Marcoumar tab 25x 3mg\",\"Marcoumar tab 25x 3mg\",[UNIT],\"[null]\",\"[null]\", \"\",\n" + "133,list1,\"Amoxicilline EG bruistab 8x 1 g\",\"Amoxicilline EG bruistab 8x 1 g\",\"Amoxicilline EG bruistab 8x 1 g\",[UNIT],\"[null]\",\"[null]\", \"\",\n" + "134,list1,\"Penicilline flac 100x milj IE poeder\",\"Penicilline flac 100x milj IE poeder\",\"Penicilline flac 100x milj IE poeder\",[UNIT],\"[null]\",\"Per 2 dagen\", \"\",\n" + "135,list1,\"Penicilline flac 100x milj IE poeder\",\"Penicilline flac 100x milj IE poeder\",\"Penicilline flac 100x milj IE poeder\",[UNIT],\"[null]\",\"[null]\", \"\",\n" + "136,list1,\"Zinnat tab 20x 500mg\",\"Zinnat tab 20x 500mg\",\"Zinnat tab 20x 500mg\",[UNIT],\"[null]\",\"[null]\", \"\",\n" + "137,list1,\"Clarithromycine Sandoz tab 21x 500mg\",\"Clarithromycine Sandoz tab 21x 500mg\",\"Clarithromycine Sandoz tab 21x 500mg\",[UNIT],\"[null]\",\"Per day\", \"\",\n" + "138,list1,\"Clindamycine Fresenius amp 10x 300mg/2ml\",\"Clindamycine Fresenius amp 10x 300mg/2ml\",\"Clindamycine Fresenius amp 10x 300mg/2ml\",[UNIT],\"[null]\",\"Per 2 dagen\", \"\",\n" + "139,list1,\"Nystatine susp 24ml 100000 IE/ml\",\"Nystatine susp 24ml 100000 IE/ml\",\"Nystatine susp 24ml 100000 IE/ml\",[UNIT],\"[null]\",\"[null]\", \"\",\n" + "140,list1,\"[intendedname: null]\",\"[intendedname: null]\",\"[intendedname: null]\",[UNIT],\"[null]\",\"Per day\", \"\",\n" + "141,list1,\"Fasigyn tab 4x 500mg\",\"Fasigyn tab 4x 500mg\",\"Fasigyn tab 4x 500mg\",[UNIT],\"[null]\",\"Per 3 dagen\", \"\","
        }
    };

    /*
     * Given a dataset, populate patient information attributes for visible
     */
    function populatePatientInformation(data) {
        visible.patientFirstName = data.patientFirstName;
        visible.patientLastName = data.patientLastName;
        visible.patientAge = data.patientAge;
        visible.patientGender = data.patientGender;
        visible.nihiiOrg = data.nihiiOrg;
    }

    /*
     * Given a dataset, populate the list1, list2, and undecided lists
     */
    function populateLists(data) {

        var objId = 0, name = {}, attributes = {}, item = {};


        // read csv data into array of arrays
        var csv_data = CSVToArray(data['csv']);

        // convert from array of arrays into array of objects
        var extractedItems = arrOfArrsToArrOfObjects(csv_data);

        // for each item, save the attributes into a ListItem object
        for (var csvi = 0; csvi < extractedItems.length; csvi++) {
            var obj = extractedItems[csvi];
            objId = parseInt(obj[CSVC.ID][0]);

            attributes = {};

            // first grab other data if present: our JSON description has priority
            if (data.other_data && data.other_data.hasOwnProperty(objId)) {
                // add information for each optional attribute
                for (var key in data.other_data[objId]) {
                    attributes[key] = data.other_data[objId][key];
                }
            }


            name = {
                recorded: attributes[visible.ATTR_FULLDATA] && attributes[visible.ATTR_FULLDATA].intendedMedication && attributes[visible.ATTR_FULLDATA].intendedMedication.medicationDescription ? attributes[visible.ATTR_FULLDATA].intendedMedication.medicationDescription : obj[CSVC.R_NAME][0],
                generic: attributes[visible.ATTR_FULLDATA] && attributes[visible.ATTR_FULLDATA].intendedMedication && attributes[visible.ATTR_FULLDATA].intendedMedication.medicationDescription ? attributes[visible.ATTR_FULLDATA].intendedMedication.medicationDescription : obj[CSVC.G_NAME][0],
                brand: attributes[visible.ATTR_FULLDATA] && attributes[visible.ATTR_FULLDATA].intendedMedication && attributes[visible.ATTR_FULLDATA].intendedMedication.medicationDescription ? attributes[visible.ATTR_FULLDATA].intendedMedication.medicationDescription : obj[CSVC.B_NAME][0]
            };

            attributes[visible.ATTR_DOSE] = obj[CSVC.DOSE];
            attributes[visible.ATTR_ROUTE] = attributes[visible.ATTR_FULLDATA] && attributes[visible.ATTR_FULLDATA].drugRoute ? [attributes[visible.ATTR_FULLDATA].drugRoute] : obj[CSVC.ROUTE];
            attributes[visible.ATTR_FREQUENCY] = attributes[visible.ATTR_FULLDATA] && attributes[visible.ATTR_FULLDATA].periodicity ? [attributes[visible.ATTR_FULLDATA].periodicity] : obj[CSVC.FREQUENCY];
            attributes[visible.ATTR_DRUG_CLASS] = obj[CSVC.DRUG_CLASSES];
            attributes[visible.ATTR_DIAGNOSES] = obj[CSVC.DIAGNOSES];
            attributes[visible.ATTR_INSTRUCTIONS] = attributes[visible.ATTR_FULLDATA] && attributes[visible.ATTR_FULLDATA].instructionsForPatient ? attributes[visible.ATTR_FULLDATA].instructionsForPatient : (obj[CSVC.INSTRUCTIONS] ? obj[CSVC.INSTRUCTIONS] : []);
            attributes[visible.ATTR_VALIDATOR] = obj[CSVC.VALIDATOR];
            attributes[visible.ATTR_ORG] = obj[CSVC.ORGANISATION];


            item = new ListItem(objId, obj[CSVC.ORIGIN][0], name, attributes);

            if (item.listID == visible.list1.id) {
                visible.list1.source.push(item.id);
            } else {
                visible.list2.source.push(item.id);
            }
            visible.items[item.id] = item;
        }

        visible.undecided = visible.list1.source.concat(visible.list2.source);
        visible.nextID = objId + 1; // indicate next id to use if adding new items
    }

    /*
     * Postconditions:
     *      populate shadows based on groupby - or retrieve if cached
     *      all possible shadows populated since shadows can be reused
     *
     * Also, continue intializing some item attributes
     *  (e.g. isShadow, isShadowed, groupByOffset)
     */
    function populateShadows() {
        // detect all possible groups
        var potentialGroups = {};
        var potentialGroupByAttributes = [];

        for (var attribute in visible.attributes) {
            if (visible.attributes[attribute].type === visible.ATTR_TYPE_CATEGORICAL) {
                potentialGroupByAttributes.push(attribute);
            }
        }

        for (var id in visible.items) {
            for (var i = 0; i < potentialGroupByAttributes.length; i++) {
                var attributes = visible.items[id].attributes;
                var attribute = potentialGroupByAttributes[i];

                if (attribute in attributes) {
                    var values = attributes[attribute];

                    for (var j = 0; j < values.length; j++) {
                        var value = values[j];

                        if (potentialGroups[value] === undefined) {
                            potentialGroups[value] = [];
                        }
                        potentialGroups[value].push(id);
                    }
                }
            }
        }

        // slight optimization: remove 1-item groups when multigrouping
        for (var groupByAttribute in potentialGroups) {
            if (potentialGroups[groupByAttribute].length < 2) {
                delete potentialGroups[groupByAttribute];
            }
        }

        // create shadows
        for (var id in visible.items) {
            var item = visible.items[id];

            // originals will always be associated with the primary group
            item.groupByOffset = 0;

            // originals are not shadows, and currently have no shadows
            item.isShadow = false;
            item.isShadowed = false;

            // search for potential shadows
            for (var attributeName in visible.attributes) {
                var attribute = visible.attributes[attributeName];

                if (attribute.type === visible.ATTR_TYPE_CATEGORICAL) {
                    if (attributeName in item.attributes) {
                        var values = item.attributes[attributeName];

                        if (values.length > 1) {
                            /*
                             * Prepare shadows; these "shadows" will only appear
                             * when the user groups by this particular
                             * attribute.
                             *
                             * For group affiliation [ A, B, C ]:
                             *   - the original will be grouped with A,
                             *   - shadow 0 will be grouped with B, and
                             *   - shadow 1 will be grouped with C.
                             *
                             * Acting on the original will act on all shadows;
                             * acting on a shadow will similarly act on the
                             * original (and all other shadows).
                             */
                            for (var i = 1; i < values.length; i++) {
                                var value = values[i];

                                // don't create unnecessary shadows
                                if (potentialGroups[value] === undefined) {
                                    continue;
                                }

                                // convention: originalID_shadowID
                                var shadowID = id + "_" + (i - 1);

                                // create the shadow from the original
                                var shadow = new ListItem(shadowID, item.listID, item.getNames(), item.attributes);
                                shadow.isShadow = true;
                                shadow.isShadowed = false;
                                shadow.groupByOffset = i;

                                visible.shadows[shadowID] = shadow;

                                // original now has a shadow
                                item.isShadowed = true;

                                // for convenience, hash in items as well
                                visible.items[shadowID] = shadow;

                                // hash from original to shadow
                                if (id in visible.itemsToShadows) {
                                    visible.itemsToShadows[id].push(shadowID);
                                } else {
                                    visible.itemsToShadows[id] = [shadowID];
                                }

                                // hash from shadow to original
                                visible.shadowsToItems[shadowID] = id;

                            }
                        }
                    }
                }
            }
        }
    }

    function detectAttributes() {
        visible.attributes[visible.ATTR_NAME] = {
            type: visible.ATTR_TYPE_GENERAL,
            displayFullDescription: true,
            display: true
        };
        visible.attributes[visible.ATTR_DOSE] = {
            type: visible.ATTR_TYPE_NUMERIC,
            displayFullDescription: true,
            display: true
        };
        visible.attributes[visible.ATTR_ROUTE] = {
            type: visible.ATTR_TYPE_CATEGORICAL,
            display: true,
            displayFullDescription: true,
            rank: []
        };
        visible.attributes[visible.ATTR_FREQUENCY] = {
            type: visible.ATTR_TYPE_GENERAL,
            displayFullDescription: true,
            display: true
        };
        visible.attributes[visible.ATTR_DRUG_CLASS] = {
            type: visible.ATTR_TYPE_CATEGORICAL,
            displayFullDescription: true,
            display: false,
            rank: ["antipyretic", "antibiotic", "analgesic", "antidiabetic", "sedative", "antianxiety", "diuretic", "bronchodilator", "corticosteroid", "antipsychotic", "antihypertensive", "anticoagulant", "antihistamine", "thyroid", "antiplatelet", "non-steroidal anti-inflammatory drug", "antacid", "antidepressant", "acetylcholinesterase inhibitor", "antismoking", "sex hormone", "anticholesterol", "bone resorption inhibitor", "dietary supplement", "laxative", "stool softener", "unknown"]
        };
        visible.attributes[visible.ATTR_DIAGNOSES] = {
            type: visible.ATTR_TYPE_CATEGORICAL,
            displayFullDescription: true,
            display: false
        };
        visible.attributes[visible.ATTR_DATE_STARTED] = {
            type: visible.ATTR_TYPE_GENERAL,
            displayFullDescription: true,
            display: false
        };
        visible.attributes[visible.ATTR_SUBITEM] = {
            type: visible.ATTR_TYPE_GENERAL,
            displayFullDescription: true,
            display: false
        };
        visible.attributes[visible.ATTR_INSTRUCTIONS] = {
            type: visible.ATTR_TYPE_GENERAL,
            displayFullDescription: true,
            display: false
        };
        visible.attributes[visible.ATTR_FULLDATA] = {
            type: visible.ATTR_TYPE_OBJECT,
            displayFullDescription: false,
            display: false
        };
        visible.attributes[visible.ATTR_VALIDATOR] = {
            type: visible.ATTR_TYPE_GENERAL,
            displayFullDescription: true,
            display: false
        };
        visible.attributes[visible.ATTR_ORG] = {
            type: visible.ATTR_TYPE_GENERAL,
            displayFullDescription: true,
            display: false
        };

    }

    /*
     * Given a dataset, extract relationship information between items and
     * populate relationship data structures
     */
    function detectRelationships(data) {
        var identical, similar;

        visible.unique1 = data.unique1;
        visible.unique2 = data.unique2;
        identical = data.identical;
        similar = data.similar;

        if (identical || similar) {
            visible.numIdenticalSets = identical.length;

            for (var i = 0; i < identical.length; i++) {
                var set = identical[i];

                for (var j = 0; j < set.length; j++) {
                    visible.identical[set[j]] = set;
                }
            }

            for (var i = 0; i < similar.length; i++) {
                set = similar[i];

                for (var j = 0; j < set.items.length; j++) {
                    visible.similar[set.items[j]] = set;
                }
            }
        }
    }

    function detectDrugClasses() {
        // hash to remove duplicates
        var tempDrugClasses = {};

        // drug classes have ids like: "dc0"
        var i = 0;

        visible.drugClasses = {};
        visible.drugClassSet = {};

        for (var id in visible.items) {
            var item = visible.items[id];

            var drugClasses = item["attributes"][visible.ATTR_DRUG_CLASS];

            for (var drugClassIndex in drugClasses) {
                var drugClass = drugClasses[drugClassIndex];

                // look up drug class id if there (to add this item to the set)

                // drug class id
                var dcid;

                if (!tempDrugClasses[drugClass]) {
                    dcid = "dc" + i;
                    i++;
                    visible.drugClasses[dcid] = drugClass;
                    visible.drugClassSet[dcid] = [];
                    tempDrugClasses[drugClass] = dcid;
                } else {
                    dcid = tempDrugClasses[drugClass];
                }

                visible.drugClassSet[dcid].push(id);
            }

        }
    }

    function detectDiagnoses() {
        var tempDrugClasses = {};
        // hash to remove duplicates
        var i = 0;
        // drug classes have ids like: "dc0"

        visible.diagnoses = {};
        visible.diagnosisSet = {};

        for (var id in visible.items) {
            var item = visible.items[id];

            var diagnoses = item["attributes"][visible.ATTR_DIAGNOSES];

            // TODO cleaning - rename
            for (var drugClassIndex in diagnoses) {
                var drugClass = diagnoses[drugClassIndex];

                // look up drug class id if there (to add this item to the set)
                var dcid;
                // drug class id
                if (!tempDrugClasses[drugClass]) {
                    dcid = "d" + i;
                    i++;
                    visible.diagnoses[dcid] = drugClass;
                    visible.diagnosisSet[dcid] = [];
                    tempDrugClasses[drugClass] = dcid;
                } else {
                    dcid = tempDrugClasses[drugClass];
                }

                visible.diagnosisSet[dcid].push(id);
            }

        }
    }

// sort
    function groupThenSort(a, b) {
        // if a groupBy is set (e.g. from controller) sort by the grouped by attribute
        var groupOrder = visible.groupBy ? attributeSort(a, b, visible.groupBy, visible.attributes[visible.groupBy].rank) : 0;

        // if no groupOrder, try sortOrder
        //  if no sortOrder, just sort by ids
        if (groupOrder === 0) {
            var sortOrder = visible.sortBy ? attributeSort(a, b, visible.sortBy) : visible.items[a].id - visible.items[b].id;

            // if equal based on sort order, tiebreak with ids
            if (visible.sortBy && sortOrder === 0) {
                return visible.items[a].id - visible.items[b].id;
            }
            return sortOrder;
        }
        return groupOrder;
    }

// sort id a and id b by a given attribute with (if given) ranking for that attribute
    function attributeSort(a, b, attribute, rank) {
        var itemA = visible.items[a];
        var itemB = visible.items[b];
        var attributeA, attributeB;

        if (attribute === visible.ATTR_NAME) {// case-insensitive
            attributeA = itemA.name.toLowerCase();
            attributeB = itemB.name.toLowerCase();
        } else {
            if (itemA.attributes[attribute]) {
                attributeA = itemA.attributes[attribute][(visible.multigroup ? itemA.groupByOffset : 0)];
            } else {
                attributeA = undefined;
            }

            if (itemB.attributes[attribute]) {
                attributeB = itemB.attributes[attribute][(visible.multigroup ? itemB.groupByOffset : 0)];
            } else {
                attributeB = undefined;
            }
        }

        // check undefined, i.e. missing attributes
        if (attributeA == undefined || attributeB == undefined) {

            if (attributeA == undefined)
                console.log("WARNING: undefined attr: item[" + a + "]['attributes'][" + attribute + "] = undefined");
            if (attributeB == undefined)
                console.log("WARNING: undefined attr: item[" + b + "]['attributes'][" + attribute + "] = undefined");

            if (attributeA == undefined && attributeB == undefined)
                return 0;
            return attributeB == undefined ? -1 : 1;
        }

        if (visible.attributes[attribute].type === visible.ATTR_TYPE_NUMERIC) {
            attributeA = parseFloat(attributeA);
            attributeB = parseFloat(attributeB);
        }

        if (rank) {
            var rankA = rank.indexOf(attributeA);
            var rankB = rank.indexOf(attributeB);

            if (rankA < rankB) {
                return -1;
            } else if (rankA > rankB) {
                return 1;
            }
            // if ranks equal, default to alphanumeric order (below)
        }

        if (attributeA < attributeB) {
            return -1;
        } else if (attributeA > attributeB) {
            return 1;
        } else {
            return 0;
        }
    }

// filter - remove decided items if option to remove after decisions is set
    function actionFilter(element, index, array) {
        var $item = $("#" + element);

        if (visible.afterAction === visible.AFTER_ACTION_REMOVE && !$item.hasClass("undecided")) {
            return false;
        }
        return true;
    }

// filter - remove items based on a name filter
    function nameFilter(element, index, array) {
        var $item = $("#" + element);

        if (visible.filterOn.length > 0 && visible.items[element].name.toLowerCase().indexOf(visible.filterOn.toLowerCase()) === -1) {
            return false;
        }
        return true;
    }

// unified filted - apply action filter and name filter
    function unifiedFilter(element, index, array) {
        var keep = actionFilter(element, index, array);

        if (keep) {
            keep = nameFilter(element, index, array);
        }
        controller.toggleItem($("#" + element), keep ? controller.toggleOnDelay * visible.FILTER_DELAY_SCALE : controller.toggleOffDelay / visible.FILTER_DELAY_SCALE, keep);
        return keep;
    }

// expose interface //////////////////////////////////////////////////
    return visible;
}
(window.model = window.model || {}, $, undefined);

// helper object /////////////////////////////////////////////////////////
function ListItem(id, listID, name, attributes) {
    var visible = {};
    var name = name;

    visible.id = id;
    visible.listID = listID;
    visible.__defineGetter__("name", function () {// TODO: note: defineGetter deprecated
        return name[model.displayName ? model.displayName : "recorded"];
    });
    visible.attributes = attributes;

    visible.getNames = function () {
        return name;
    };
    visible.setName = function (value) {
        name = value;
    };

    visible.isModified = false;

    return visible;
}


// This will parse a delimited string into an array of
// arrays. The default delimiter is the comma, but this
// can be overriden in the second argument.
// from http://stackoverflow.com/questions/1293147/javascript-code-to-parse-csv-data
function CSVToArray(strData, strDelimiter) {
    // Check to see if the delimiter is defined. If not,
    // then default to comma.
    strDelimiter = (strDelimiter || ",");

    // Create a regular expression to parse the CSV values.
    var objPattern = new RegExp(
        (
            // Delimiters.
            "(\\" + strDelimiter + "|\\r?\\n|\\r|^)" +

                // Quoted fields.
            "(?:\"([^\"]*(?:\"\"[^\"]*)*)\"|" +

                // Standard fields.
            "([^\"\\" + strDelimiter + "\\r\\n]*))"
        ),
        "gi"
    );


    // Create an array to hold our data. Give the array
    // a default empty first row.
    var arrData = [[]];

    // Create an array to hold our individual pattern
    // matching groups.
    var arrMatches = null;


    // Keep looping over the regular expression matches
    // until we can no longer find a match.
    while (arrMatches = objPattern.exec(strData)) {

        // Get the delimiter that was found.
        var strMatchedDelimiter = arrMatches[1];

        // Check to see if the given delimiter has a length
        // (is not the start of string) and if it matches
        // field delimiter. If id does not, then we know
        // that this delimiter is a row delimiter.
        if (
            strMatchedDelimiter.length &&
            (strMatchedDelimiter != strDelimiter)
        ) {

            // Since we have reached a new row of data,
            // add an empty row to our data array.
            arrData.push([]);

        }


        // Now that we have our delimiter out of the way,
        // let's check to see which kind of value we
        // captured (quoted or unquoted).
        if (arrMatches[2]) {

            // We found a quoted value. When we capture
            // this value, unescape any double quotes.
            var strMatchedValue = arrMatches[2].replace(
                new RegExp("\"\"", "g"),
                "\""
            );

        } else {

            // We found a non-quoted value.
            var strMatchedValue = arrMatches[3];

        }


        // Now that we have our value string, let's add
        // it to the data array.
        arrData[arrData.length - 1].push(strMatchedValue);
    }

    // Return the parsed data.
    return ( arrData );
}

/*
 * convert array of arrays to array of objects (with keys = column names)
 * assumes 0th entry of array is an array of column names
 *
 * e.g.
 *  [
 *      ['id', 'origin'],
 *      ['0', 'list0'],
 *      ['1', 'list0']
 *  ]
 *
 *  ->
 *
 *  [
 *      {
 *          id: 0,
 *          origin: 'list0'
 *      },
 *      {
 *          id: 1,
 *          origin: 'list0'
 *      }
 *  ]
 */
function arrOfArrsToArrOfObjects(arrOfArrs) {
    // 0th row is attribute names in the object
    var ret = [];

    var attributeArr = arrOfArrs[0];

    for (var i = 1; i < arrOfArrs.length; i++) {
        var currentArr = arrOfArrs[i];
        var obj = new Object();
        for (var j = 0; j < currentArr.length; j++) {

            var dataArray = currentArr[j].split(",");

            obj[attributeArr[j]] = dataArray;
        }
        ret.push(obj);
    }

    return ret;
}