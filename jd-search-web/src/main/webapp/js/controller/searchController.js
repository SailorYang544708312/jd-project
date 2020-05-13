app.controller('searchController',function ($scope,$location,searchService) {
    //搜索
    $scope.search = function () {
        $scope.searchMap.pageNow = parseInt($scope.searchMap.pageNow);//转换成int类型,因为前端输入框拿到的都是string类型
        searchService.search($scope.searchMap).success(function (response) {
            $scope.resultMap=response;//得到搜索的信息
            //分页栏
            buildPageLabel();

        })
    }

    //构建分页栏
    buildPageLabel = function(){
        $scope.pageLabel = [];
        var maxPageNo = $scope.resultMap.totalPages;//得到最后一页的页码
        var firstPage = 1; //开始的页码
        var lastPage = maxPageNo;

        $scope.firstDot = true;//为true 表示前面需要有省略号
        $scope.lastDot = true;//为true 表示后面需要省略号

        //总页数大于5页
        if ($scope.resultMap.totalPages > 5){
            if ($scope.searchMap.pageNow <= 3){
                //如果当前页小于等于3,前面就是1.2.3... 方式输出
                lastPage = 5; //开始的5页
                $scope.firstDot = false; //前面没有省略号
                $scope.lastDot = true;//为true 表示后面需要省略号
            }else if ($scope.searchMap.pageNow >= lastPage -2){
                //如果当前页面大于等于最大页码-2
                firstPage = maxPageNo -4; //最后的5页
                $scope.lastDot = false; //最后5页没有省略号
                $scope.firstDot = true;//为true 表示前面需要有省略号
            }else {
                //中间的页码，就是当前页码为中心，前面-2  后面+2
                firstPage = $scope.searchMap.pageNow -2;
                lastPage = $scope.searchMap.pageNow +2;
                $scope.firstDot = true;//为true 表示前面需要有省略号
                $scope.lastDot = true;//为true 表示后面需要省略号
            }

        }

        //总的页数小于5页,我们直接遍历输出
        for (var i = firstPage; i <= lastPage; i++) {
            $scope.pageLabel.push(i);//将页面循环出来，添加到分页栏中
        }
    }

    //根据页码执行查询
    $scope.queryByPage = function(pageNow){
        if (pageNow < 1 || pageNow > $scope.resultMap.totalPages){
            return;
        }
        $scope.searchMap.pageNow = pageNow;//将pageNow放入到searchMap中
        //执行查询
        $scope.search();
    }

    //解决上一页有样式可不可以点的问题
    $scope.isTopPage = function(){
        if ($scope.searchMap.pageNow == 1){
            return true;
        }else {
            return false;
        }
    }
    //解决下一页有样式可不可以点的问题
    $scope.isEndPage = function(){
        if ($scope.searchMap.pageNow == $scope.resultMap.totalPages){
            return true;
        }else {
            return false;
        }
    }


    //定义searchMap的结构
    $scope.searchMap = {'keywords':'','category':'','brand':'','spec':{},'price':'','pageNow':1,'pageSize':10,'sortField':'','sort':''};

    //添加搜索结构
    $scope.addSearchItem = function (key,value) {
        if (key == 'category' || key == 'brand' || key == 'price'){
            $scope.searchMap[key] = value;//点击的是分类或者品牌
        }else {
            //规格
            $scope.searchMap.spec[key] = value;
        }
        $scope.search();//执行搜索
    }

    //撤销面包屑
    $scope.removeSearchItem = function (key) {
        if (key == 'category' || key == 'brand' || key == 'price'){
            $scope.searchMap[key] = "";//点击的是分类或者品牌
        }else {
            //规格
            delete $scope.searchMap.spec[key];
        }
        $scope.search();//执行搜索
    }

    //键盘回车事件
    $scope.enterEvent = function (e) {
        var keycode = window.event?e.keyCode:e.which;
        if (keycode == 13){
            $scope.search();//执行搜索
        }
    }

    //设置排序的规则
    $scope.sortSearch = function (sortField,sort) {
        $scope.searchMap.sortField = sortField;
        $scope.searchMap.sort = sort;
        $scope.search();//执行搜索
    }

    //判断搜索的关键字是否是品牌
    $scope.keywordsIsBrand = function(){
        for(var i = 0; i < $scope.resultMap.brandList.length; i++){
            //判断搜索的关键字是否在品牌列表中
            if($scope.searchMap.keywords.indexOf($scope.resultMap.brandList[i].text) >= 0){
                //关键字包含了品牌
                return true;
            }
        }
        return false;
    }


    //加载查询关键字
    $scope.loadkeywords = function(){
        //alert($location.search()['keywords']);
        //接收首页传递过来的参数
        $scope.searchMap.keywords = $location.search()['keywords'];
        //执行查询
        $scope.search();
    }
})