app.controller('cartController',function ($scope,cartService) {
    //查询购物车列表
    $scope.findCartList = function () {
        cartService.findCartList().success(function (response) {
            $scope.cartList = response;
            //展示购物车的时候就顺便计算价格
            $scope.totalValue = cartService.sum($scope.cartList);
        })
    }

    //更新商品数量
    $scope.addGoodsToCartList = function (itemId,num) {
        cartService.addGoodsToCartList(itemId,num).success(function (response) {
            if (response.success){
                $scope.findCartList();//成功就刷新购物车列表
            }else {
                alert(response.message);
            }
        })
    }

    //获取到当前登录的用户名
    $scope.showName = function () {
        cartService.showName().success(function (response) {
            $scope.loginName = response.loginName;
            //alert($scope.loginName);
        })
    }

    //获取当前登录用户的地址列表
    $scope.findAddressList = function () {
        cartService.findAddressList().success(function (response) {
            $scope.addressList = response;
            //在查询结果中找到默认地址，找到之后，给$scope.address(相当于是选择了默认地址)
            for (var i = 0; i < $scope.addressList.length; i++) {
                if ($scope.addressList[i].isDefault == '1'){
                    $scope.address = $scope.addressList[i];
                    break;//找到后结束循环
                }
            }
        });
    }

    //选择地址 将当前选择的地址保存到全局变量 $scope.address中
    $scope.selectAddress = function (address) {
        $scope.address = address;
    }
    //判断是否选择是当前地址
    $scope.isSelectedAddress = function (address) {
        if (address == $scope.address){
            return true;
        }else {
            return false;
        }
    }

    //定义一个对象
    $scope.order={'paymentType':'1'};
    //选择支付方式
    $scope.selectPayType = function (type) {
        $scope.order.paymentType = type;
    }

    //提交订单
    $scope.submitOrder = function () {
        $scope.order.receiverAreaName = $scope.address.address; //地址
        $scope.order.receiverMobile = $scope.address.mobile; //电话
        $scope.order.receiver = $scope.address.contact; //联系人

        cartService.submitOrder($scope.order).success(function (response) {
            if ($scope.order.paymentType == '1'){
                //表示微信支付
                location.href = "http://localhost:9240/pay.html";
            }else {
                //货到付款
                location.href = "paysuccess.html";
            }
        });
    }

})