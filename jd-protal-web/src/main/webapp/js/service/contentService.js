app.service('contentService',function ($http) {
    //根据广告分类id 查询广告列表
    this.findByCategoryId = function (categoryId) {
        return $http.get('content/findByCategoryId?categoryId='+categoryId);
    }

    //获取到当前登录的用户名
    this.showName = function () {
        return $http.jsonp('http://localhost:31001/login/name?callback=JSON_CALLBACK');
        //这里如果不加JSON_CALLBACK 在controller.js控制层里 就不会去走 success 而是走 error();
    }
})