/**
 * IP - 城市 转换
 * @return var remote_ip_info = ...
 * @constructor
 * @see http://int.dpool.sina.com.cn/iplookup/iplookup.php?format=json&ip=202.196.35.35
 */
function IPUtil() {
}

IPUtil.getCityByIp = function (ip) {
    var r = new Object();
    var api = Config.contextPath + "/ip/info.json?ip=";
    $.ajax({
        url: api + ip,
        async: false,
        dataType: "json",
        success: function (data) {
            if (data.ret == 1) {
                r.province = data.province;
            }
        },
        error: function (a, b, c) {
            console.log(a);
            console.log(b);
            console.log(c);
        }
    });

    return r;
}

/**
 * 异步请求IP info
 * @param ip
 * @returns {___anonymous1023_1023}
 */
IPUtil.getCityByIpAsync = function (ip, callback) {
    var api = Config.contextPath + "/ip/info.json?ip=";
    $.ajax({
        url: api + ip,
        async: true,
        dataType: "json",
        success: function (data) {
            if (data.ret == 1) {
                var r = new Object();
                r.province = data.province;
                callback(r);
            }
        },
        error: function (a, b, c) {
            console.log(a);
            console.log(b);
            console.log(c);
        }
    });
}
