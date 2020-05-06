app.controller('baseController',function ($scope) {
    //重新加载数据列表
    $scope.reloadList = function(){
        //切换页面数据跟新
        //$scope.findPage($scope.paginationConf.currentPage,$scope.paginationConf.itemsPerPage);
        $scope.search($scope.paginationConf.currentPage,$scope.paginationConf.itemsPerPage);
    }

    //分页组件的显示配置
    $scope.paginationConf = {
        currentPage:1,
        totalItems:10,
        itemsPerPage:10,
        perPageOptions:[10,20,30,40,50],
        onChange:function () {
            //每次更换页面都重新查询
            $scope.reloadList();
        }
    }

    //获取到要删除的品牌对象id的数组
    $scope.selectIds = [];
    //当选择后 数组要更新
    $scope.updateSelection = function ($event,id) {
        //如果是复选框选中就添加到数组中
        if ($event.target.checked){
            $scope.selectIds.push(id);
        }else {
            //如果复选框取消选中，就要从数组中删除
            var idx = $scope.selectIds.indexOf(id);
            $scope.selectIds.splice(idx,1);
        }
    }

    //查询集合中key是否存在
    $scope.searchObjectByKey = function (list,key,value) {
        for (var i=0;i<list.length;i++){
            if (list[i][key] == value){
                return list[i];
            }
        }
        return null;
    }
});