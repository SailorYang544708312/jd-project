//不分页的自定义模块
var app = angular.module('jd',[]);

//$sce: 封装了angularjs的信任策略
app.filter('xinren',['$sce',function ($sce) {
    return function (data) {
        return $sce.trustAsHtml(data);
    }
}])