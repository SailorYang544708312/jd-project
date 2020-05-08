app.controller('contentController',function ($scope,contentService) {
    //初始化广告列表的集合
    $scope.contentList =[];  //将所有的广告都放在这个集合中
    //根据广告分类id 查询广告列表
    $scope.findByCategoryId = function (categoryId) {
        contentService.findByCategoryId(categoryId).success(function (response) {
            $scope.contentList[categoryId] = response;
        })
    }
})