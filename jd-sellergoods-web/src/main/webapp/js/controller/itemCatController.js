 //控制层 
app.controller('itemCatController' ,function($scope,$controller   ,itemCatService){	
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		itemCatService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		itemCatService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		itemCatService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id != null){//如果有ID
			serviceObject=itemCatService.update($scope.entity); //修改
		}else{
			$scope.entity.parentId = $scope.parentId; //赋予上级ID
			serviceObject=itemCatService.add($scope.entity);//增加
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
		        	//$scope.reloadList();
					$scope.findByParentId($scope.parentId); //重新加载
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		itemCatService.dele($scope.selectIds).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
					$scope.selectIds=[];
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		itemCatService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}


	//根据上级id显示下级列表
	$scope.parentId = 0;

	//根据上级id查询下级列表
	$scope.findByParentId = function (parentId) {
		$scope.parentId = parentId;
		itemCatService.findByParentId(parentId).success(function (response) {
			$scope.list = response;
		});
	}

	//面包屑
	$scope.grade = 1;//默认是1级分类
	//提供设置级别的方法
	$scope.setGrade = function (val) {
		$scope.grade = val;
	}

	//读取列表
	$scope.selectList = function (p_entity) {
		//如果是1级
		if ($scope.grade == 1){
			$scope.entity_1 = null;
			$scope.entity_2 = null;
		}

		//如果是2级
		if ($scope.grade === 2){
			$scope.entity_1 = p_entity;
			$scope.entity_2 = null;
		}

		//如果是3级
		if ($scope.grade == 3){
			$scope.entity_2 = p_entity;
		}
		//查询下一级
		$scope.findByParentId(p_entity.id);
	}
});	
