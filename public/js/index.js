

function getFilter(term) {
    return function() {
        if ($(this).data("search").toLowerCase().indexOf(term) !== -1) {
            $(this).removeClass("hidden");
        } else {
            $(this).addClass("hidden");
        }
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
    $("#search").bind("mouseenter mouseleave keyup keydown", function() {
        var value = $(this).val();
        var filter = getFilter(value.trim().toLowerCase());
        rows.each(filter);
        collapseEmptyRegions();
    });

    // Trigger initial update to resync after e.g autofill
    $("#search").trigger("keydown");
    collapseEmptyRegions();
});
