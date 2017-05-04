

function getFilter(term) {
    return function() {
        return $(this).data("search").toLowerCase().indexOf(term) !== -1;
    }
}

function getTagFilter(term) {
    return function() {
        return $(this).data("tags").indexOf(term) !== -1;
    }
}

function collapseEmptyRegions() {
    var tables = $("section > table");
    tables.each(function() {
        var rows = $(this).find("tr:not(.hidden)");
        if (rows.length < 2) {
            $(this).addClass("hidden");
            $(this).parent().children(".no-search-results").removeClass("hidden");
        } else {
            $(this).removeClass("hidden");
            $(this).parent().children(".no-search-results").addClass("hidden");
        }
    });
}

$(document).ready(function() {
    var rows = $(".ref-row");

    var always = function () { return true; };
    var textFilter = always;
    var tagFilter = always;

    function composeFilter() {
        return function () {
            if (textFilter.bind(this)() && tagFilter.bind(this)()) {
                $(this).removeClass("hidden");
            } else {
                $(this).addClass("hidden");
            }
        }
    }

    function applyFilter() {
        rows.each(composeFilter());
        collapseEmptyRegions();
    }

    $("#search").bind("change keyup keydown", function() {
        var value = $(this).val().trim().toLowerCase();

        if (!value || value === "") {
            textFilter = always;
        } else {
            textFilter = getFilter(value);
        }

        applyFilter();
    });

    $(".tag.btn").click(function() {
        var tagValue = $(this).data("tag");

        $(".tag.btn.active").removeClass("active");
        $(this).addClass("active");

        if (!tagValue || tagValue === "") {
            tagFilter = always;
        } else {
            tagFilter = getTagFilter(tagValue);
        }

        applyFilter();
    });

    // Trigger initial update to resync after e.g autofill
    $("#search").trigger("keydown");
    collapseEmptyRegions();
});
