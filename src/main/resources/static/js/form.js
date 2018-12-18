$(document).ready(function() {
    var btn = $('#submit');
    btn.parents('form').submit(function() {
        btn.attr('disabled',true);
        btn.text('Submitting...');
    });
});