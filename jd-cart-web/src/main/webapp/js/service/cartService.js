app.service('cartService',function ($http) {
    //购物车列表展示
    this.findCartList = function () {
        return $http.get('cart/findCartList');
    }

    //添加商品到购物车
    this.addGoodsToCartList = function (itemId,num) {
        return $http.get('cart/addGoodsToCartList?itemId='+itemId+'&num='+num);
    }

    //求合计业务逻辑
    this.sum = function(cartList){
        var totalValue = {totalNum:0,totalMoney:0.00};//返回的实体
        for(var i = 0; i < cartList.length; i++){
            //挨个取出购物车
            var cart = cartList[i];
            //遍历购物车明细
            for(var j = 0; j < cart.orderItemList.length; j++){
                //挨个取出orderItem
                var orderItem = cart.orderItemList[j];
                //计算
                totalValue.totalNum += orderItem.num;
                totalValue.totalMoney += orderItem.totalFee;
            }
        }
        return totalValue;
    }

    //获取到当前登录的用户名
    this.showName = function () {
        return $http.jsonp('http://localhost:31002/login/name?callback=JSON_CALLBACK');
        //这里如果不加JSON_CALLBACK 在controller.js控制层里 就不会去走 success 而是走 error();
    }

    //根据当前登录用户获取地址列表
    this.findAddressList = function () {
        return $http.get('address/findListByLoginUser')
    }

    //提交订单
    this.submitOrder = function (order) {
        return $http.post('order/add',order);
    }
})