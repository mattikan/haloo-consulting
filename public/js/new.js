
function hideFields() {
    $('.field').hide();
    $('.field input').prop('disabled', true);
}

$(document).ready(function() {
    $('#submit').hide();
    $('#type').change(function() {
        hideFields();
        $('#submit').show();
        var enabledFields = $('#type').find('option:selected').data('show').split(" ");
        enabledFields.forEach(function(f) {
            $('.' + f).show();
            $('#' + f).prop('disabled', false);
        });
    });

    $('#generate-id').click(function () {
        $.ajax({
            url: "/generate_id",
            method: "POST",
            dataType: "text",
            contentType: "application/json",
            data: JSON.stringify({
                year: $('#year').val(),
                authors: [$('#author').val()]
            }),
            success: function(newId) { $('#id').val(newId) }
        });
    });

    hideFields();
});
