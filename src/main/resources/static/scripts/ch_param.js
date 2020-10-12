
var server = 'http://192.168.0.48:8088/janus';
var selectedStream = null;
var janus = null;
var streaming = null;
var bitrateTimer = null;
//var stream = null;
var opaqueId = "raztot-" + Janus.randomString(12);
var simulcastStarted = false, svcStarted = false;

function startJanus(callback) {
    if (streaming != null) {
        Janus.log("Plugin attached! (" + streaming.getPlugin() + ", id=" + streaming.getId() + ")");
        typeof callback === 'function' && callback(true);
        return;
    }

    // Initialize the library (all console debuggers enabled)
    Janus.init({
        debug: "all",
        callback: function () {
            // Create session
            janus = new Janus({
                server: server,
                success: function () {
                    // Attach to streaming plugin
                    janus.attach({
                        plugin: "janus.plugin.streaming",
                        opaqueId: opaqueId,
                        success: function (pluginHandle) {
                            streaming = pluginHandle;
                            Janus.log("Plugin attached! (" + streaming.getPlugin() + ", id=" + streaming.getId() + ")");
                            //startStream();
                        },
                        error: function (error) {
                            Janus.error("  -- Error attaching plugin... ", error);
                            alert("Error attaching plugin ... " + error);
                        },
                        onmessage: function (msg, jsep) {
                            Janus.debug(" ::: Got a message :::");
                            Janus.debug(msg);
                            var result = msg["result"];
                            if (result !== null && result !== undefined) {
                                if (result["status"] !== undefined && result["status"] !== null) {
                                    var status = result["status"];
                                    if (status === 'stopped'){
                                        stopStream();
                                    }
                                }
                            } else if (msg["error"] !== undefined && msg["error"] !== null) {
                                alert(msg["error"]);
                                stopStream();
                                return;
                            }
                            if (jsep !== undefined && jsep !== null) {
                                Janus.debug("Handling SDP as well...");
                                Janus.debug(jsep);
                                // Offer from the plugin, let's answer
                                streaming.createAnswer(
                                    {
                                        jsep: jsep,
                                        // We want recvonly audio/video and, if negotiated, datachannels
                                        media: { audioSend: false, videoSend: false, data: true },
                                        success: function (jsep) {
                                            Janus.debug("Got SDP!");
                                            Janus.debug(jsep);
                                            var body = { "request": "start" };
                                            streaming.send({ "message": body, "jsep": jsep });
                                        },
                                        error: function (error) {
                                            Janus.error("WebRTC error:", error);
                                            alert("WebRTC error... " + JSON.stringify(error));
                                        }
                                    });
                            }
                        },
                        onremotestream: function (stream) {
                            Janus.debug(" ::: Got a remote stream :::");
                            Janus.debug(stream);
                            Janus.attachMediaStream($('#video_player').get(0), stream);
                            //$("#video_player").get(0).load();
                            var videoTracks = stream.getVideoTracks();
                            if(videoTracks && videoTracks.length &&(Janus.webRTCAdapter.browserDetails.browser === "chrome" ||
																	Janus.webRTCAdapter.browserDetails.browser === "firefox" ||
																	Janus.webRTCAdapter.browserDetails.browser === "safari")) {
										bitrateTimer = setInterval(function() {
											// Display updated bitrate, if supported
											var bitrate = streaming.getBitrate();
											//$('#bitrate').text("Bitrate : "+bitrate);
											// Check if the resolution changed too
											var width = $("#video_player").get(0).videoWidth;
											var height = $("#video_player").get(0).videoHeight;
											//if(width > 0 && height > 0)
												//$('#resolution').text("Resolution : "+width+'x'+height).show();

										}, 1000);
									}
                        },
                        ondataopen: function (data) {
                            Janus.log("The DataChannel is available!");
                        },
                        ondata: function (data) {
                            Janus.debug("We got data from the DataChannel! " + data);
                            $('#datarecv').val(data);
                        },
                        oncleanup: function () {
                            Janus.log(" ::: Got a cleanup notification :::");
                            if (bitrateTimer !== null && bitrateTimer !== undefined)
                                clearInterval(bitrateTimer);
                            bitrateTimer = null;
                            simulcastStarted = false;
                        }
                    });
                },
                error: function (error) {
                    Janus.error(error);
                    alert(error+"asdadsads");
                },
                destroyed: function () {
                    // window.location.reload();
                }
            });
        }
    });
}

function startStream() {
    selectedStream = 0;
    var ch_nr = $("#video_player").attr('value');
    if($('#stream_1').prop("checked")==true){
        selectedStream = ch_nr*100+1
      }
    if ($('#stream_2').prop('checked')==true){
        selectedStream = ch_nr*100+2
    }
    if (streaming != null){
        Janus.log("Selected video id #" + selectedStream);
        var body = { "request": "watch", id: parseInt(selectedStream) };
        streaming.send({ "message": body });
    }
}

function stopStream() {
    selectedStream = 0;
    if (streaming != null){
        var body = { "request": "stop" };
        streaming.send({ "message": body });
        streaming.hangup();
        if (bitrateTimer !== null && bitrateTimer !== undefined)
            clearInterval(bitrateTimer);
        bitrateTimer = null;
    }
}



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
    $.ajax({
        type: 'GET',
        url: url,
        async:true,
        dataType: 'json',
        contentType: 'application/json',
        success:function(data){onSucces(data);},
        error:function(data){onError(data);}
    });
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
    stopStream();
    getName();
    getStream1();
    getStream2();
    getEnableStream1();
    getEnableStream2();
    getStateStream1();
    getStateStream2();
    setVideoId();
    $('#video_player').get(0).pause();
    $("#video_player").get(0).load();
    $('#pause_icon').fadeIn();
}

function simulateClickOnFirstCh(){
    var first_ch = $('#ch_selector button').get(0);
    $(first_ch).trigger( "click" );
}
function setVideoId(){
    var ch_id = $('#ch_selector button.active');
    var video_player = $('#video_player');
    var url = ch_id.attr('href')+ch_id.attr('value')+video_player.attr('href');//request video stream number
    getData(url,function(data){
                    if (data.succes == true){
                        console.log(data.value);
                        $('#video_player').attr('value',data.value);
                    }else{
                        $('#video_player').attr('value',0);
                    }
                },function(data){
                    console.log(data);
                    $('#video_player').attr('value',0);
                });
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
        var url = sel_ch.attr('href')+sel_ch.attr('value')+'/streams/0/inputUrl';
        var data = JSON.stringify({'value':$(this).val()});
        console.log(url);
        sendData(url,data,
            function(data){
                if (data.succes == true){
                    var span = $('#stream_1_span').removeClass('badge-danger').addClass('badge-success');
                    span.text('ok');
                    setTimeout(function(){clearSpan(span);},3000);
                }else{
                    var span = $('#stream_1_span').removeClass('badge-success').addClass('badge-danger');
                    span.text('error');
                    setTimeout(function(){clearSpan(span);},3000);
                }
            },function(data){
                var span = $('#stream_1_span').removeClass('badge-success').addClass('badge-danger');
                span.text('error');
                setTimeout(function(){clearSpan(span);},3000);
            });
});

$('#input_stream_2').on('change',function(){
            console.log($(this).val());
            var sel_ch = $('#ch_selector button.active');
            var url = sel_ch.attr('href')+sel_ch.attr('value')+'/streams/1/inputUrl';
            var data = JSON.stringify({'value':$(this).val()});
            console.log(url);
            sendData(url,data,
                function(data){
                    if (data.succes == true){
                        var span = $('#stream_2_span').removeClass('badge-danger').addClass('badge-success');
                        span.text('ok');
                        setTimeout(function(){clearSpan(span);},3000);
                    }else{
                        var span = $('#stream_2_span').removeClass('badge-success').addClass('badge-danger');
                        span.text('error');
                        setTimeout(function(){clearSpan(span);},3000);
                    }
                },function(data){
                    var span = $('#stream_2_span').removeClass('badge-success').addClass('badge-danger');
                    span.text('error');
                    setTimeout(function(){clearSpan(span);},3000);
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

$('#video_div').on('click',function () {video_div
    if($('#video_player').get(0).paused){
        $('#video_player').get(0).play();
        startStream();
        $('#pause_icon').fadeOut();
    }else{
       $('#video_player').get(0).pause();
       stopStream();
       $('#pause_icon').fadeIn();
    }
});

$('#stream_1').on('click',function() {
    stopStream();
    $('#video_player').get(0).pause();
    $('#pause_icon').fadeIn();
});
$('#stream_2').on('click',function() {
  stopStream();
  $('#video_player').get(0).pause();
  $('#pause_icon').fadeIn();
});

$(document).ready(function(){
    simulateClickOnFirstCh();
    startJanus();
});