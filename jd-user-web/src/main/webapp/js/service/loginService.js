app.service('loginService',function ($http) {
    //获取到当前登录的用户信息
    this.showName = function () {
        return $http.get('../login/name')
    }
})