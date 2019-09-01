app.controller('brandController',function ($scope,$controller,brandService) {

    $controller('baseController',{$scope:$scope});

    $scope.findALL=function () {
        brandService.findALL().success(
            function (response) {
                $scope.list=response;
            }
        );
    }
    //




    //请求后端的数据
    $scope.findPage=function(page,rows){
        brandService.findPage(page,rows).success(
            function(response){
                $scope.list=response.rows;//显示当前页数据
                $scope.paginationConf.totalItems=response.total; //更新总记录数

            }
        )
    }

    //增加和保存
    /*		$scope.add=function(){
                $http.post('../brand/add.do',$scope.entity).success(
                    function(response){
                        if (response.success){
                            $scope.reloadList();//刷新
                        } else{
                            alert(response.message); //失败弹出
                        }
                    }
                )
            }*/

    /*    $scope.add=function(){
            $http.post('../brand/add.do',$scope.entity).success(
                function(response){
                    if (response.success){
                        $scope.reloadList();//刷新
                    } else{
                        alert(response.message); //失败弹出
                    }
                }
            )
        }
*/
    //增加
    $scope.sava=function(){
        var object=null;
        if ($scope.entity.id!=null){
            object=brandService.update($scope.entity);
        } else{
            object=brandService.add($scope.entity);
        }
        /*     var methodName='add';*/
        /*  if ($scope.entity.id!=null){
              methodName='update';
          }*/ /*$http.post('../brand/'+methodName+'.do',$scope.entity)*/
        object.success(
            function(response){
                if (response.success){
                    $scope.reloadList();//刷新
                } else{
                    alert(response.message); //失败弹出
                }
            }
        );
    }

    //查询实体
    $scope.findOne=function (id) {
        brandService.findOne(id).success(
            function (response) {
                $scope.entity=response;
            }
        )
    }



    //删除
    $scope.dele=function () {
        brandService.dele($scope.selectIds).success(
            function (response) {
                if (response.success){
                    $scope.reloadList();//刷新
                }else{
                    alert(response.message);
                }
            }
        );
    }

    $scope.searchEntity={};
    //条件查询
    $scope.search=function (page,rows) {
        brandService.search(page,rows,$scope.searchEntity).success(
            function (response) {
                $scope.paginationConf.totalItems=response.total;//更新总记录数
                $scope.list=response.rows;//显示当前页数据

            }
        );
    }

});
