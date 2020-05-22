app.service('payService',function ($http) {
    //扫描支付(Native)
    this.createNative = function () {
        return $http.get('pay/createNative');
    }

    //查看支付状态
    this.queryPayStatus = function (out_trade_no) {
        return $http.get('http://localhost:31002/pay/queryPayStatus?out_trade_no='+out_trade_no);
    }
});