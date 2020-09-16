
$('#channelSelector').on('click','input',function(event) {
    $('#channelsForms').find('.d-block').addClass('d-none')
    $('#channelsForms').find('.d-block').removeClass('d-block')
    $('#'+event.target.value).removeClass('d-none').addClass('d-block')
    console.log('pressed button--->'+event.target.value)
});
function findActiveButton(){
    return $('#channelSelector').find('.active').find('input').attr('value');
}
function findVisibleForm(){
    return $('#channelsForms').find('.visible').find('div').attr('id');
}

function makeVisibleOnlyMyForm(){

}


$(document).ready(function(){
    $('#'+findActiveButton()).addClass('d-block');
});

