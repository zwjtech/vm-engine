
function fail(code) {
    return code < 0;
}

function success(code) {
    return code > 0;
}

//用户未登录时受保护的页面，用于用户注销后或者被动离线后调用
var protectedUserPageLists = ["/user/[0-9/_-a-zA-Z]*"];

//保护用户页面
function protectUserPageWhenUserIsOffline(react_this) {
    for (var i = 0; i < protectedUserPageLists.length; i++) {
        var protectedPage = protectedUserPageLists[i];
        if (react_this.props.location.pathname.match(protectedPage)) {
            react_this.props.history.replace("/");
            break;
        }
    }

}

//用户在其他地方登录code
const WS_USER_STATUS_RESULT_CODE_LOGIN_OTHER_AREA = 5;
//在线用户超时code
const WS_USER_STATUS_RESULT_CODE_SESSION_TIMEOUT = 6;

//电影图片等待加载时使用的图片
const MOVIE_LOADING_IMG = "/image/movie_img_loading.gif";

//电影人图片等待加载时使用的图片
const FILMMAKER_LOADING_IMG = "/image/filmmaker_img_loading.gif";
//websocket前缀
const WS_URL_PREFIX = "ws://localhost:8888";

//开始懒加载，依赖jquery.lazyload.js
function lazyLoad() {
    // c($("img"));
    $("img").lazyload({effect: "fadeIn"});
}

//ajax封装
//common是所有的返回结果都会执行的方法
var ajax = {
    ajaxError: "访问服务器失败,请稍后重试",
    requestServerSuccess: function (args, result) {
        if (isUndefined(result)) {
            return;
        }
        if (!isEmpty(args.onResponseStart)) {
            args.onResponseStart();
        }
        if (fail(result.code) && !isEmpty(args.onResponseFailure)) {
            args.onResponseFailure(result);
        }
        if (success(result.code) && !isEmpty(args.onResponseSuccess)) {
            args.onResponseSuccess(result);
        }
        if (!isEmpty(args.onResponseEnd)) {
            args.onResponseEnd();
        }

    },
    requestServerError: function (args, XMLHttpRequest, textStatus, errorThrown) {
        console.error(XMLHttpRequest);
        console.error(textStatus);
        console.error(errorThrown);


        if (!isEmpty(args.onResponseStart)) {
            args.onResponseStart();
        }

        window.VmFrontendEventsDispatcher.showMsgDialog(this.ajaxError, function () {
            if (!isEmpty(args.onRequestError)) {
                args.onRequestError();
            }
            if (!isEmpty(args.onResponseEnd)) {
                args.onResponseEnd();
            }

        });


    },
    ajax: function (args) {
        //handler args.onBeforeRequest
        if (!isEmpty(args.onBeforeRequest) && args.onBeforeRequest() == false) {
            return;
        }
        //handler args.async
        if (isEmpty(args.async)) {
            args.async = true;
        }
        //handler args.data
        if (isUndefined(args.data) || isEmptyString(args.data)) {
            args.data = {};
        }
        args.data = JSON.stringify(args.data);
        $.ajax({
            url: args.url,
            //配合@requestBody
            data: args.data,
            async: args.async,
            type: args.type,
            contentType: "application/json",
            dataType: "json",
            success: function (result) {
                this.requestServerSuccess(args, result);
            }.bind(this),
            error: function (XMLHttpRequest, textStatus, errorThrown) {

                this.requestServerError(args, XMLHttpRequest, textStatus, errorThrown);
            }.bind(this)
        });
    },
    get: function (args) {
        args.type = "GET";
        this.ajax(args);
    },
    put: function (args) {
        args.type = "PUT";
        this.ajax(args);
    }
};





