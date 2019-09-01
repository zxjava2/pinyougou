app.controller('baseController',function ($scope) {
    $scope.paginationConf = {
        currentPage: 1,
        totalItems: 10,
        itemsPerPage: 10,
        perPageOptions: [10, 20, 30, 40, 50],
        onChange: function () {
            $scope.reloadList();//重新加载
        }
    };

    //刷新列表
    $scope.reloadList=function(){
        $scope.search($scope.paginationConf.currentPage,$scope.paginationConf.itemsPerPage);
    }


    $scope.selectIds=[];//用户勾选的id集合
    //更新复选
    $scope.updateSelection=function($event,id){
        if ($event.target.checked){
            $scope.selectIds.push(id);//push向集合添加元素
        } else{
            var index=$scope.selectIds.indexOf(id);//查找值的位置
            //移除集合中的元素
            $scope.selectIds.splice(index,1) //移除的位置，移除的个数。
        }

    }

    //提取json字符串中的某个属性，返回拼接字符串，逗号分隔符
    $scope.jsonToString=function (jsonString,key) {
        var json=JSON.parse(jsonString);
        var value="";
        for (var i=0;i<json.length;i++){
            if (i>0){
                value+=","
            }
            value+=json[i][key];
        }
        return value;
    }

    //从集合中安装key查询对象
    $scope.searchObjectBykey=function (list,key,keyValue) {
        for (var i=0;i<list.length;i++){
            if (list[i][key]==keyValue){
                return list[i];
            }
        }
        return null;
    }


});