$(function () {
    $(".menu").on("mouseover",function(){
        $(this).children(".sub-menu-list").show();
    });
    $(".menu").on("mouseleave",function(){
        $(this).children(".sub-menu-list").hide();
    });

});
