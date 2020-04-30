//自定义控制器(C)
app.controller('brandController',function ($scope,$controller,brandService) {
    //通过控制器继承引入baseController
    $controller('baseController',{$scope:$scope})
    //读取表单数据绑定到表单中
    $scope.findAll = function () {
        brandService.findAll().success(function (response) {
            $scope.list = response;
        })
    }

    $scope.findPage = function (page,rows) {
        brandService.findPage(page,rows).success(function (response) {
            $scope.list = response.rows; //一页信息
            $scope.paginationConf.totalItems = response.total; //总信息条数
        })
    }

    //保存
    $scope.save = function () {
        var object = null;
        if ($scope.entity.id != null) {
            object = brandService.update($scope.entity)
        }else{
            object = brandService.add($scope.entity);
        }

        object.success(function (response) {
            if (response.success){
                $scope.reloadList();
            }else {
                alert(response.message);
            }
        })
    }

    //修改 查询实体
    $scope.findOne = function (id) {
        brandService.findOne(id).success(function (response) {
            $scope.entity = response;
        })
    }

    //删除
    $scope.del = function () {
        brandService.del($scope.selectIds).success(function (response) {
            if (response.success){
                $scope.reloadList();
                //删除成功后 清空数组
                $scope.selectIds = [];
            }else {
                alert(response.message);
            }
        });
    }

    //条件查询
    $scope.searchEntity = {};
    $scope.search = function (page,rows) {
        brandService.search(page,rows,$scope.searchEntity).success(function (response) {
            $scope.list = response.rows; //一页信息
            $scope.paginationConf.totalItems = response.total; //总信息条数
        });
    }
});