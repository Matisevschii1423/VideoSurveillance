function sendData(url,value,onSucces,onError){
    $.ajax({
        type: 'PUT',
        url: url,
        async:true,
        data: value,
        dataType: 'json',
        contentType: 'application/json',
        success:function(data){onSucces(data);},
        error: function(data){onError(data);}
    });
}

function getData(url,onSucces,onError){
    console.log(url);
    var tmp;
    $.ajax({
        type: 'GET',
        url: url,
        async:true,
        dataType: 'json',
        contentType: 'application/json',
        success:function(data){onSucces(data);},
        error:function(data){onError(data);}
    });

    return tmp;
}

function getName(){
    var sel_ch = $('#ch_selector button.active');
    var url = sel_ch.attr('href')+sel_ch.attr('value')+'/name';
    getData(url,
        function(data){
        console.log(data);
            $('#ch_name').val(data.value);
        },function(data){
        console.log(data);
            $('#ch_name').val('');
        });

}

function getStream1(){
    var sel_ch = $('#ch_selector button.active');
    var url = sel_ch.attr('href')+sel_ch.attr('value')+'/streams/0/inputUrl';
    getData(url,function(data){
            console.log(data);
            $('#input_stream_1').val(data.value);
        },function(data){
            console.log(data);
            $('#input_stream_1').val('');
        });
}
function getStream2(){
    var sel_ch = $('#ch_selector button.active');
    var url = sel_ch.attr('href')+sel_ch.attr('value')+'/streams/1/inputUrl';
    getData(url,function(data){
            console.log(data);
            $('#input_stream_2').val(data.value);
        },function(data){
            console.log(data);
            $('#input_stream_2').val('');
        });
}
function getEnableStream1(){
    var sel_ch = $('#ch_selector button.active');
    var url = sel_ch.attr('href')+sel_ch.attr('value')+'/streams/0/enable';
    getData(url,function(data){
            if (data.succes == true){
                if (eval(data.value) == false){
                    console.log(data.value);
                    $('#enable_input_stream_1').text('Enable');
                    $('#enable_input_stream_1').attr('value',false);
                }
                if (eval(data.value) == true){
                    console.log(data.value);
                    $('#enable_input_stream_1').text('Disable');
                    $('#enable_input_stream_1').attr('value',true);
                }
            }else{
                $('#enable_input_stream_1').text('UNKNOWN');
                $('#enable_input_stream_1').attr('value','');
            }
        },function(data){
            console.log(data);
            $('#enable_input_stream_1').text('UNKNOWN');
            $('#enable_input_stream_1').attr('value','');
        });
}
function getEnableStream2(){
    var sel_ch = $('#ch_selector button.active');
    var url = sel_ch.attr('href')+sel_ch.attr('value')+'/streams/1/enable';
    getData(url,function(data){
            if (data.succes == true){
                if (eval(data.value) == false){
                    console.log(data.value);
                    $('#enable_input_stream_2').text('Enable');
                    $('#enable_input_stream_2').attr('value',false);
                }
                if (eval(data.value) == true){
                    console.log(data.value);
                    $('#enable_input_stream_2').text('Disable');
                    $('#enable_input_stream_2').attr('value',true);
                }
            }else{
                $('#enable_input_stream_2').text('UNKNOWN');
                $('#enable_input_stream_2').attr('value','');
            }
        },function(data){
            console.log(data);
            $('#enable_input_stream_2').text('UNKNOWN');
            $('#enable_input_stream_2').attr('value','');
        });
}

function getStateStream1(){
    var sel_ch = $('#ch_selector button.active');
    var url = sel_ch.attr('href')+sel_ch.attr('value')+'/streams/0/running';
    getData(url,function(data){
                if (data.succes == true){
                    if (eval(data.value) == false){
                        console.log(data.value);
                        $('#state_stream_1').removeClass('text-success').addClass('text-danger')
                        $('#state_stream_1').text('Not connected');
                        $('#state_stream_1').attr('value',false);
                    }
                    if (eval(data.value) == true){
                        console.log(data.value);
                        $('#state_stream_1').removeClass('text-danger').addClass('text-success');
                        $('#state_stream_1').text('Connected');
                        $('#state_stream_1').attr('value',true);
                    }
                }else{
                    $('#state_stream_1').removeClass('text-success').addClass('text-danger');
                    $('#state_stream_1').text('UNKNOWN');
                    $('#state_stream_1').attr('value','');
                }
            },function(data){
                console.log(data);
                $('#state_stream_1').removeClass('text-success').addClass('text-danger');
                $('#state_stream_1').text('UNKNOWN');
                $('#state_stream_1').attr('value','');
            });
}

function getStateStream2(){
    var sel_ch = $('#ch_selector button.active');
    var url = sel_ch.attr('href')+sel_ch.attr('value')+'/streams/1/running';
    getData(url,function(data){
                if (data.succes == true){
                    if (eval(data.value) == false){
                        console.log(data.value);
                        $('#state_stream_2').removeClass('text-success').addClass('text-danger');
                        $('#state_stream_2').text('Not connected');
                        $('#state_stream_2').attr('value',false);
                    }
                    if (eval(data.value) == true){
                        console.log(data.value);
                        $('#state_stream_2').removeClass('text-danger').addClass('text-success');
                        $('#state_stream_2').text('Connected');
                        $('#state_stream_2').attr('value',true);
                    }
                }else{
                    $('#state_stream_2').removeClass('text-success').addClass('text-danger');
                    $('#state_stream_2').text('UNKNOWN');
                    $('#state_stream_2').attr('value','');
                }
            },function(data){
                console.log(data);
                $('#state_stream_2').removeClass('text-success').addClass('text-danger');
                $('#state_stream_2').text('UNKNOWN');
                $('#state_stream_2').attr('value','');
            });
}

function clearSpan(span){
    span.removeClass('badge-danger');
    span.removeClass('success');
    span.text('');
}

function refreshAll(){
    getName();
    getStream1();
    getStream2();
    getEnableStream1();
    getEnableStream2();
    getStateStream1()
    getStateStream2()
}

$('#ch_selector button').on('click',function(){
    var ch_id = $(this).attr('value');
    $(this).attr('aria-pressed',"true");
    $(this).addClass('active');
    $('#ch_selector').children().each(function(){
        if ($(this).attr('value')!=ch_id){
            $(this).attr('aria-pressed',"false");
            $(this).removeClass('active');
        }
    });
    refreshAll();
});

$('#ch_name').on('change',function(){
    console.log($(this).val());
    var sel_ch = $('#ch_selector button.active');
    var url = sel_ch.attr('href')+sel_ch.attr('value')+'/name';
    var data = JSON.stringify({'value':$(this).val()});
    console.log(url);
    sendData(url,data,
        function(data){
            if (data.succes == true){
                var span = $('#ch_name_span');
                span.removeClass('badge-danger').addClass('badge-success');
                span.text('ok');
                $('#ch_selector button.active').text(data.value);
                setTimeout(function(){clearSpan(span);},3000);
            }else{
                var span = $('#ch_name_span');
                span.removeClass('badge-success').addClass('badge-danger');
                span.text('error');
                setTimeout(function(){clearSpan(span);},3000);
            }
        },function(data){
            $('#ch_name_span').removeClass('badge-success').addClass('badge-danger');
        });
});

$('#input_stream_1').on('change',function(){
        console.log($(this).val());
        var sel_ch = $('#ch_selector button.active');
        var url = sel_ch.attr('href')+sel_ch.attr('value')+'/name';
        var data = JSON.stringify({'value':$(this).val()});
        console.log(url);
        sendData(url,data,
            function(data){
                if (data.succes == true){
                    $('#stream_1_span').removeClass('badge-danger').addClass('badge-success');
                    $('#stream_1_span').text('ok');
                }else{
                    $('#stream_1_span').removeClass('badge-success').addClass('badge-danger');
                    $('#stream_1_span').text('error');
                }
            },function(data){
                $('#stream_1_span').removeClass('badge-success').addClass('badge-danger');
            });
});

$('#input_stream_2').on('change',function(){
            console.log($(this).val());
            var sel_ch = $('#ch_selector button.active');
            var url = sel_ch.attr('href')+sel_ch.attr('value')+'/name';
            var data = JSON.stringify({'value':$(this).val()});
            console.log(url);
            sendData(url,data,
                function(data){
                    if (data.succes == true){
                        $('#stream_2_span').removeClass('badge-danger').addClass('badge-success');
                        $('#stream_2_span').text('ok');
                    }else{
                        $('#stream_2_span').removeClass('badge-success').addClass('badge-danger');
                        $('#stream_2_span').text('error');
                    }
                },function(data){
                    $('#stream_2_span').removeClass('badge-success').addClass('badge-danger');
                });
});

$('#enable_input_stream_1').on('click',function(){
    var sel_ch = $('#ch_selector button.active');
    var url = sel_ch.attr('href')+sel_ch.attr('value')+'/streams/0/enable';
    var value = ($(this).attr('value')=='true');
    var outdata = JSON.stringify({'value':!value});
    console.log("data 2 send-->"+outdata);
    sendData(url,outdata,function(data){
        if (data.succes == true){
            if (eval(data.value) == false){
                console.log("received data -->"+data.value);
                $('#enable_input_stream_1').text('Enable');
                $('#enable_input_stream_1').attr('value',false);
            }
            if (eval(data.value) == true){
                console.log(data.value);
                $('#enable_input_stream_1').text('Disable');
                $('#enable_input_stream_1').attr('value',true);
            }
            }else{
                $('#enable_input_stream_1').text('UNKNOWN');
                $('#enable_input_stream_1').attr('value','');
            }
        },function(data){
            console.log("received data -->"+data.value);
            $('#enable_input_stream_1').text('UNKNOWN');
            $('#enable_input_stream_1').attr('value','');
        });
});
$('#enable_input_stream_2').on('click',function(){
    var sel_ch = $('#ch_selector button.active');
    var url = sel_ch.attr('href')+sel_ch.attr('value')+'/streams/1/enable';
    var value = ($(this).attr('value')=='true');
    var outdata = JSON.stringify({'value':!value});
    console.log("data 2 send-->"+outdata)
    sendData(url,outdata,function(data){
        if (data.succes == true){
            if (eval(data.value) == false){
                console.log("received data -->"+data.value);
                $('#enable_input_stream_2').text('Enable');
                $('#enable_input_stream_2').attr('value',false);
            }
            if (eval(data.value) == true){
                console.log(data.value);
                $('#enable_input_stream_2').text('Disable');
                $('#enable_input_stream_2').attr('value',true);
            }
            }else{
                $('#enable_input_stream_2').text('UNKNOWN');
                $('#enable_input_stream_2').attr('value','');
            }
        },function(data){
            console.log("received data -->"+data.value);
            $('#enable_input_stream_2').text('UNKNOWN');
            $('#enable_input_stream_2').attr('value','');
        });
});
