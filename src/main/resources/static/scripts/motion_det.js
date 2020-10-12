
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
        //startStream();

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
                            $('#start-stream').click(function () {
                                $(this).attr('disabled', true);

                                clearInterval(bitrateTimer);
                                //janus.destroy();
                                stopStream();
                            });
                        },
                        error: function (error) {
                            Janus.error("  -- Error attaching plugin... ", error);
                            alert("Error attaching plugin... " + error);
                        },
                        onmessage: function (msg, jsep) {
                            Janus.debug(" ::: Got a message :::");
                            Janus.debug(msg);
                            var result = msg["result"];
                            if (result !== null && result !== undefined) {
                                if (result["status"] !== undefined && result["status"] !== null) {
                                    var status = result["status"];
                                    if (status === 'starting')
                                        $('#status').removeClass('hide').text("Starting, please wait...").show();
                                    else if (status === 'started')
                                        $('#status').removeClass('hide').text("Started").show();
                                    else if (status === 'stopped')
                                        stopStream();
                                } else if (msg["streaming"] === "event") {
                                    // Is simulcast in place?
                                    var substream = result["substream"];
                                    var temporal = result["temporal"];
                                    if ((substream !== null && substream !== undefined) || (temporal !== null && temporal !== undefined)) {
                                        if (!simulcastStarted) {
                                            simulcastStarted = true;
                                            addSimulcastButtons(temporal !== null && temporal !== undefined);
                                        }
                                        // We just received notice that there's been a switch, update the buttons
                                        updateSimulcastButtons(substream, temporal);
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
                                            $('#watch').html("Stop").removeAttr('disabled').click(stopStream);
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
                            $("video_player").load();
                            var videoTracks = stream.getVideoTracks();
                            if(videoTracks && videoTracks.length &&(Janus.webRTCAdapter.browserDetails.browser === "chrome" ||
																	Janus.webRTCAdapter.browserDetails.browser === "firefox" ||
																	Janus.webRTCAdapter.browserDetails.browser === "safari")) {
										bitrateTimer = setInterval(function() {
											// Display updated bitrate, if supported
											var bitrate = streaming.getBitrate();
											$('#bitrate').text("Bitrate : "+bitrate);
											// Check if the resolution changed too
											var width = $("#video_player").get(0).videoWidth;
											var height = $("#video_player").get(0).videoHeight;
											if(width > 0 && height > 0)
												$('#resolution').text("Resolution : "+width+'x'+height).show();

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
                    alert(error);
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

function waitMotionEvent(){
    var md_zone_state = $('#md_zone_state')
    var ch_id = $('#ch_id');
    var url = ch_id.attr('href')+ch_id.attr('value')+"/mdetector/md_in_img";
    $.ajax({
        type: 'GET',
        url: url,
        async:true,
        dataType: 'json',
        contentType: 'application/json',
        timeout: 3000,
        success:
            function(data){
                if (data.succes==true){
                    if (eval(data.value)==true){
                        md_zone_state.show();
                        setTimeout(function(){waitMotionEvent();},1000);
                        //console.log(url+"<-GET<-"+data.value);
                    }else{
                        md_zone_state.hide();
                        setTimeout(function(){waitMotionEvent();},1000)
                        //console.log(url+"<-GET<-"+data.value);
                    }
                }else{
                    md_zone_state.hide();
                    setTimeout(function(){waitMotionEvent();},1000);
                    //console.log(url+"<-GET<-"+data.value);
                }
            },
        error:
            function(data){
                md_zone_state.hide();
                setTimeout(function(){waitMotionEvent();},1000);
                //console.log(url+"<-GET<-"+data.value);
            }
    });
}

function reqEnable(){
    var ch_id = $('#ch_id');
    var label = $('#md_enable');
    var span = $('#md_enable_span');
    var url = ch_id.attr('href')+ch_id.attr('value')+label.attr('href');
    getData(url,
        function(data){
            if (data.succes == true){
                if (eval(data.value) == true){
                    label.text('Enabled');
                    label.attr('value',true);
                }
                if (eval(data.value) == false){
                    label.text('Disabled');
                    label.attr('value',false);
                }
            }else{
                label.text('UNKNOWN');
                label.attr('value','');
            }
            span.removeClass('badge-danger').addClass('badge-success');
            span.text('ok');
            setTimeout(function(){clearSpan(span);},3000);
            console.log(url+"<-GET"+"<--"+data.value);
        },function(data){
            label.text('UNKNOWN');
            label.attr('value','');
            span.removeClass('badge-success').addClass('badge-danger');
            span.text('error');
            setTimeout(function(){clearSpan(span);},3000);
            console.log(url+"<-GET"+"<--"+data.value);
        }
    );
}
function reqStream(){
    var ch_id = $('#ch_id');
    var label = $('#md_stream');
    var span = $('#md_stream_span');
    var url = ch_id.attr('href')+ch_id.attr('value')+label.attr('href');
    getData(url,
        function(data){
            if (data.succes == true){
                label.text(eval(data.value)+1);
                label.attr('value',eval(data.value));
            }else{
                label.text('UNKNOWN');
                label.attr('value','');
            }
            span.removeClass('badge-danger').addClass('badge-success');
            span.text('ok');
            setTimeout(function(){clearSpan(span);},3000);
            console.log(url+"<-GET"+"<--"+data.value);
        },function(data){
            label.text('UNKNOWN');
            label.attr('value','');
            span.removeClass('badge-success').addClass('badge-danger');
            span.text('error');
            setTimeout(function(){clearSpan(span);},3000);
            console.log(url+"<-GET"+"<--"+data.value);
        }
    );
}

function reqThreshold(){
    var ch_id = $('#ch_id');
    var label = $('#md_threshold');
    var span = $('#md_threshold_span');
    var url = ch_id.attr('href')+ch_id.attr('value')+label.attr('href');
    getData(url,
        function(data){
            if (data.succes == true){
                label.text(data.value);
                label.attr('value',data.value);
            }else{
                label.text('UNKNOWN');
                label.attr('value','');
            }
            span.removeClass('badge-danger').addClass('badge-success');
            span.text('ok');
            setTimeout(function(){clearSpan(span);},3000);
            console.log(url+"<-GET"+"<--"+data.value);
        },function(data){
            label.text('UNKNOWN');
            label.attr('value','');
            span.removeClass('badge-success').addClass('badge-danger');
            span.text('error');
            setTimeout(function(){clearSpan(span);},3000);
            console.log(url+"<-GET"+"<--"+data.value);
        }
    );
}

function setVideoId(){
    var ch_id = $('#ch_id');
    var video_player = $('#video_player');
    var url = ch_id.attr('href')+ch_id.attr('value')+video_player.attr('href');//request video stream number
    getData(url,
        function(data){
            if (data.succes == true){
                video_player.attr('value',data.value);
            }else{
                video_player.attr('value',0);
            }
        },function(data){
            video_player.attr('value',0);
        }
    );
}

function initialParams(){
    var label = $('#ch_id');
    var first_ch = $('#ch_dropdown_menu a').get(0);
    label.attr('value',$(first_ch).attr('value'));
    label.text($(first_ch).text());
    setVideoId();
}

function reqAll(){
    //request actual settings from server
    reqStream();
    reqEnable();
    reqThreshold();
    setVideoId();
    stopStream();
    startStream();
}

function clearSpan(span){
    span.removeClass('badge-danger');
    span.removeClass('success');
    span.text('');
}

//Channel selector
$('#ch_dropdown_menu a').on('click',function(){
    var label = $('#ch_id');
    label.attr('value',$(this).attr('value'));
    label.text($(this).text());
    reqAll();

});

//enable disable selector
$('#md_enable_dropdown_menu a').on('click',function(){
    var ch_id = $('#ch_id');
    var label = $('#md_enable');
    var span = $('#md_enable_span');
    //label.attr('value',$(this).attr('value'));
    //label.text($(this).text());
    var url = ch_id.attr('href')+ch_id.attr('value')+label.attr('href');
    var dataOut = JSON.stringify({'value':$(this).attr('value')});
    sendData(url,dataOut,
        function(data){
            if (data.succes == true){
                if (eval(data.value)==true){
                    label.text('Enabled');
                    label.attr('value',true)
                }else{
                    label.text('Fisabled');
                    label.attr('value',false)
                }
            }else{
                label.text('UNKNOWN');
            }
            span.removeClass('badge-danger').addClass('badge-success');
            span.text('ok')
            setTimeout(function(){clearSpan(span);},3000);
            console.log(url+"-PUT->"+"<--"+data.value);
        },function(data){
            label.text('UNKNOWN');
            span.removeClass('badge-success').addClass('badge-danger');
            span.text('error')
            setTimeout(function(){clearSpan(span);},3000);
            console.log(url+"-PUT->"+"<--"+data.value);
        }
    );
});

//threshold selector
$('#md_threshold_dropdown_menu a').on('click',function(){
    var ch_id = $('#ch_id');
    var label = $('#md_threshold');
    var span = $('#md_threshold_span');
    //label.attr('value',$(this).attr('value'));
    //label.text($(this).text());
    var url = ch_id.attr('href')+ch_id.attr('value')+label.attr('href');
    var dataOut = JSON.stringify({'value':$(this).attr('value')});
    sendData(url,dataOut,
        function(data){
            if (data.succes == true){
                label.text(data.value);
                label.attr('value',data.value)
            }else{
                label.text('UNKNOWN');
            }
            span.removeClass('badge-danger').addClass('badge-success');
            span.text('ok')
            setTimeout(function(){clearSpan(span);},3000);
            console.log(url+"-PUT->"+"<--"+data.value);
        },function(data){
            label.text('UNKNOWN');
            span.removeClass('badge-success').addClass('badge-danger');
            span.text('error')
            setTimeout(function(){clearSpan(span);},3000);
            console.log(url+"-PUT->"+"<--"+data.value);
        }
    );
});

//stream selector
$('#md_stream_dropdown_menu a').on('click',function(){
    var ch_id = $('#ch_id');
    var label = $('#md_stream');
    var span = $('#md_stream_span');
    //label.attr('value',$(this).attr('value'));
    //label.text($(this).text());
    var url = ch_id.attr('href')+ch_id.attr('value')+label.attr('href');
    var dataOut = JSON.stringify({'value':eval($(this).attr('value'))});
    sendData(url,dataOut,
        function(data){
            if (data.succes == true){
                label.text(eval(data.value)+1);
                label.attr('value',eval(data.value))
            }else{
                label.text('UNKNOWN');
                label.attr('value','0')
            }
            span.removeClass('badge-danger').addClass('badge-success');
            span.text('ok')
            setTimeout(function(){clearSpan(span);},3000);
            console.log(url+"-PUT->"+"<--"+data.value);
        },function(data){
            label.text('UNKNOWN');
            label.attr('value','0')
            span.removeClass('badge-success').addClass('badge-danger');
            span.text('error')
            setTimeout(function(){clearSpan(span);},3000);
            console.log(url+"-PUT->"+"<--"+data.value);
        }
    );
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

$('#stream_1').click(function() {
    stopStream();
    $('#video_player').get(0).pause();
    $('#pause_icon').fadeIn();
});
$('#stream_2').click(function() {
  stopStream();
  $('#video_player').get(0).pause();
  $('#pause_icon').fadeIn();
});

$(document).ready(function(){
    initialParams();
    reqAll();
    startJanus();
    waitMotionEvent();
});