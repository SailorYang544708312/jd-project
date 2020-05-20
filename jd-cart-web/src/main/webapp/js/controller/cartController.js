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
})