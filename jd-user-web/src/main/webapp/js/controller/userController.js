 //控制层 
app.controller('userController' ,function($scope,$controller,userService){
	
	$controller('baseController',{$scope:$scope});//继承

	//注册
	$scope.reg = function () {
		//判断两次输入的密码是否正确
		if ($scope.entity.password != $scope.password){
			alert("两次密码不一致");
			return;
		}
		if($scope.smscode == ""){
			alert("请输入验证码");
			return;
		}

		userService.add($scope.entity,$scope.smscode).success(function (response) {
			alert(response.message);
		})
	}

	$scope.entity={'phone':''}
	//发送验证码
	$scope.sendCode = function(){
		//前端也做一下手机号的验证(建议大家用正则表达式)
		if($scope.entity.phone == null){
			alert("请输入手机号");
			return;
		}
		if($scope.entity.length == 11){
			alert("手机号格式不正确");
			return;
		}
		userService.sendCode($scope.entity.phone).success(
			function(response){
				alert(response.message);
			}
		);
	}

});	
