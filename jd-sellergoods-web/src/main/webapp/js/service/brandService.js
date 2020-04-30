//自定义服务(M)
app.service('brandService',function ($http) {
    //读取列表所有数据
    this.findAll = function () {
        return $http.get('../brand/show');
    }

    //分页查询
    this.findPage = function (page,rows) {
        return $http.get('../brand/findPage?page='+page+'&rows='+rows);
    }

    //添加
    this.add= function (entity) {
        return $http.post('../brand/add',entity);
    }
    //修改
    this.update = function (entity) {
        return $http.post('../brand/update',entity);
    }

    //查询一个实体
    this.findOne = function (id) {
        return $http.get('../brand/findOne?id='+id);
    }

    //删除
    this.del = function (ids) {
        return $http.get('../brand/delete?ids='+ids);
    }

    //条件查询和分页
    this.search = function (page,rows,searchEntity) {
        return $http.post('../brand/search?page='+page+'&rows='+rows,searchEntity);
    }
});
