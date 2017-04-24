
function hideFields() {
    $('.field').hide();
    $('.field input').prop('disabled', true);
}

$(document).ready(function() {
    $('#type').change(function() {
        hideFields();
        var enabledFields = $('#type').find('option:selected').data('show').split(" ");
        enabledFields.forEach(function(f) {
            $('.' + f).show();
            $('#' + f).prop('disabled', false);
        });
    });

    hideFields();
});
