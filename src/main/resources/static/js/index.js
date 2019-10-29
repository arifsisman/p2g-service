$(document).ready(function () {
    
    
    $(".app-link").hover(function () {
        link = $(this);
        app = $(link).attr('id');
        color = "black";
        if (app === 'spotify') {
            color = "green";
        }
        if (app === 'youtube') {
            color = "red";
        }
        if (app === 'soundcloud') {
            color = "orange";
        }
        $(".footer-div > .footer-label").css('color', color);
    }, function () {
        $(".footer-div > .footer-label").css('color', 'black');
    });



});