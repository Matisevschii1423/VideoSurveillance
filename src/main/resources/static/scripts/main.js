
var server = 'http://192.168.0.48:8088/janus';

var janus = null;
var streaming = null;
var opaqueId = "raztot-" + Janus.randomString(12);

var bitrateTimer = null;
var spinner = null;
var selectedStream = 102;
var channel;
var stream;

var simulcastStarted = false, svcStarted = false;

function startStream() {
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

function startJanus(callback) {
    if (streaming != null) {
        Janus.log("Plugin attached! (" + streaming.getPlugin() + ", id=" + streaming.getId() + ")");
        startStream();

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
                            startStream();
                            $('#start-stream').click(function () {
                                $(this).attr('disabled', true);

                                clearInterval(bitrateTimer);
                                //janus.destroy();
                                stopStream();
                            });

                            typeof callback === 'function' && callback(true);
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
                            Janus.attachMediaStream($('#channel-1').get(0), stream);
                            $("channel-1").load();
                            var videoTracks = stream.getVideoTracks();
                            if(videoTracks && videoTracks.length &&(Janus.webRTCAdapter.browserDetails.browser === "chrome" ||
																	Janus.webRTCAdapter.browserDetails.browser === "firefox" ||
																	Janus.webRTCAdapter.browserDetails.browser === "safari")) {
										bitrateTimer = setInterval(function() {
											// Display updated bitrate, if supported
											var bitrate = streaming.getBitrate();
											$('#bitrate').text("Bitrate : "+bitrate);
											// Check if the resolution changed too
											var width = $("#channel-1").get(0).videoWidth;
											var height = $("#channel-1").get(0).videoHeight;
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




$(document).ready(function(){
    startJanus();
	startStream();
});
