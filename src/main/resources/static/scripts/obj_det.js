function onSucces(){
    alert("data sended ");
}
function onError(){
    alert("data not sended ")
}

function sendData(url,data){
    $.ajax({
        type: 'PUT',
        url: url,
        data: JSON.stringify({"value":data}),
        dataType: 'json',
        contentType: 'application/json',
        success: function(){alert("succes");},
        error:onError
    });
}

function getData(url,data){
    $.ajax({
        type: 'GET',
        url: url,
        dataType: 'json',
        contentType: 'application/json',
        success: onSucces,
        error:onError
    });

}

$(function(){
    $("#parameters .dropdown .dropdown-menu a").click(function(){
        var dropdown_item = $(this);
        var label = dropdown_item.parent().parent().parent().parent().find("div label");
        label.text(dropdown_item.text())
        label.attr("value",dropdown_item.attr("value"));
        var url = "/channels/"+$("#ch_id").attr("value")+label.attr("href");
        if (label.attr('id')!="ch_id"){
            sendData(url,label.attr("value"));
        }
    });
});