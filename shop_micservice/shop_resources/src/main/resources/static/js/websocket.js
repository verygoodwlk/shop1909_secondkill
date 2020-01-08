/**
 * 初始化WebSocket
 */
var ws;
var heartTime = 2000;
var closeTime = 5000;
var reconnTime = 5000;
function initWs(callBack){

    if(callBack.heartTime){
        heartTime = callBack.heartTime;
    }

    if(callBack.closeTime){
        closeTime = callBack.closeTime;
    }

    if(callBack.reconnTime){
        reconnTime = callBack.reconnTime;
    }

    //判断浏览器是否支持WebSocket
    if(window.WebSocket){

        ws = new WebSocket(callBack.url);
        //设置回调
        ws.onopen = function(){
            console.log("服务器连接成功！");

            //开始发送心跳
            heart();

            //定时关闭服务器
            closeConn();

            //调用自定义的open方法
            if(callBack.myopen){
                callBack.myopen();
            }
        };

        ws.onclose = function(){
            console.log("服务器连接断开！");

            //开始重连
            setTimeout(function(){
                reconn(callBack);
            }, reconnTime);

            //关闭心跳
            if(heartTimeout){
                clearTimeout(heartTimeout);
                heartTimeout = null;
            }

            if(callBack.myclose){
                callBack.myclose();
            }
        };

        ws.onerror = function(){
            console.log("服务器连接异常！");

            if(callBack.myerror){
                callBack.myerror();
            }
        };

        ws.onmessage = function(msg){
            console.log("接收到服务器的消息：" + msg.data);
            var msgObj = JSON.parse(msg.data);

            if(msgObj.type == 2){
                //心跳回复消息
                clearTimeout(closeTimeout);
                closeConn();

                //非心跳回复，其他的消息
                if(callBack.mymessage){
                    callBack.mymessage(msgObj);
                }
            } else {
                //非心跳回复，其他的消息
                if(callBack.mymessage){
                    callBack.mymessage(msgObj);
                }
            }


        };

    } else {
        alert("浏览器太垃圾了，请换个浏览器！");
    }
}

/**
 * 重连服务器
 */
function reconn(callBack){
    console.log("开始重连服务器.....");
    initWs(callBack);
}

/**
 * 心跳
 */
var heartTimeout = null;
function heart(){
    console.log("发送一次心跳!");
    //构造心跳消息
    var heartMsg = {"type":2};
    //发送消息
    sendMsgObj(heartMsg);

    //发送消息
    heartTimeout = setTimeout(function(){
        heart();
    }, heartTime);
}

/**
 * 关闭连接
 */
var closeTimeout = null;
function closeConn(){
    closeTimeout = setTimeout(function(){
        if(ws){
            //关闭和服务器的连接
            ws.close();
        }
    }, closeTime);
}

/**
 * 发送字符串
 * @param msg
 */
function sendMsg(msg){
    if(ws){
        //发送给websocket服务器
        ws.send(msg);
    } else {
        alert("服务器连接异常，发送失败！");
    }
}

/**
 * 发送对象
 * @param msg
 */
function sendMsgObj(msg){
    var msgStr = JSON.stringify(msg);
    sendMsg(msgStr);
}

// var heartMsg = new Object();
// heartMsg.type = 2;
//
// var heartMsg = new Msg(2);
// function Msg(type){
//     this.type = type;
// }