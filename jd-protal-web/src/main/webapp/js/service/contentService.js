app.service('contentService',function ($http) {
    //根据广告分类id 查询广告列表
    this.findByCategoryId = function (categoryId) {
        return $http.get('content/findByCategoryId?categoryId='+categoryId);
    }
})