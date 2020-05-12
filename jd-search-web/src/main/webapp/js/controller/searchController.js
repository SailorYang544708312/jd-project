app.controller('searchController',function ($scope,searchService) {
    //搜索
    $scope.search = function () {
        searchService.search($scope.searchMap).success(function (response) {
            $scope.resultMap=response;//得到搜索的信息
        })
    }

    //定义searchMap的结构
    $scope.searchMap = {'keywords':'','category':'','brand':'','spec':{}};

    //添加搜索结构
    $scope.addSearchItem = function (key,value) {
        if (key == 'category' || key == 'brand'){
            $scope.searchMap[key] = value;//点击的是分类或者品牌
        }else {
            //规格
            $scope.searchMap.spec[key] = value;
        }
        $scope.search();//执行搜索
    }

    //撤销面包屑
    $scope.removeSearchItem = function (key) {
        if (key == 'category' || key == 'brand'){
            $scope.searchMap[key] = "";//点击的是分类或者品牌
        }else {
            //规格
            delete $scope.searchMap.spec[key];
        }
        $scope.search();//执行搜索
    }

})