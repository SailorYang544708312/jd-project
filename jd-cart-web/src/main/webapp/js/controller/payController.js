app.controller('payController',function ($scope,$location,payService) {
    //本地生成二维码
    $scope.createNative = function () {
        payService.createNative().success(function (response) {
            //取出金额(这里钱的单位是分, 转换成元，并保留2位小数)
            $scope.money = (response.total_fee / 100).toFixed(2);
            $scope.out_trade_no = response.out_trade_no; //取出订单号

            //展示二维码
            var qr = new QRious({
                element: document.getElementById('QRcode'),
                size:250,
                value:response.code_url,
                level:'H',
                background:'white',
                foreground:'black'
            });

            //查询支付状态
            queryPayStatus($scope.out_trade_no);
        });
    }

    //查询支付状态
    queryPayStatus = function (out_trade_no) {
        payService.queryPayStatus(out_trade_no).success(function (response) {
            if (response.success){
                //已经支付成功
                location.href="paysuccess.html#?money="+$scope.money;
            }else {
                //支付失败
                //location.href="payfail.html";
                if (response.message == '二维码超时'){
                    $scope.createNative(); //重新生成二维码
                }else {
                    location.href="payfail.html";
                }
            }
        });
    }

    //取出金额
    $scope.getMoney = function () {
        return $location.search()['money'];
    }

});