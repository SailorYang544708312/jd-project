app.service('uploadService',function ($http) {
    //文件上传
    this.uploadFile = function () {
        //创建表单数据对象
        //对于angularjs的get或者post请求。默认Content-Type是application/json,通过设置{'Content-Type':undefined'}
        //这样浏览器会帮组我们把Content-Type设置为一个multipart/form-data (一个附件上传)
        //transformRequest:angular.identity 将表单提交的数据 进行 二进制 序列化（如果是图片，音乐，电影等等文件必须采用）
        var formData = new FormData();
        formData.append("file",file.files[0]);
        return $http({
            method:'POST',
            url:'../upload',
            data:formData,
            headers:{'Content-Type':undefined},
            transformRequest:angular.identity
        });
    }
})