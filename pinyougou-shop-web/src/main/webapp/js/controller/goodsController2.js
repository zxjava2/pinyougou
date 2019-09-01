//控制层
app.controller('goodsController2', function ($scope, $controller,$location, goodsService2, uploadService, itemCatService, typeTemplateService) {

    $controller('baseController', {$scope: $scope});//继承

    //读取列表数据绑定到表单中  
    $scope.findAll = function () {
        goodsService2.findAll().success(
            function (response) {
                $scope.list = response;
            }
        );
    }

    //分页
    $scope.findPage = function (page, rows) {
        goodsService2.findPage(page, rows).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    }

    //查询实体
    $scope.findOne = function () {
     var id=$location.search()['id'];
     if (id==null){
         return;
     }
       goodsService2.findOne(id).success(
            function (response) {
                $scope.entity = response;
                //向富文本编辑器添加商品介绍
                editor.html($scope.entity.goodsDesc.introduction);//商品介绍
                //商品图片
                $scope.entity.goodsDesc.itemImages=JSON.parse($scope.entity.goodsDesc.itemImages);
                //扩展属性
                $scope.entity.goodsDesc.customAttributeItems=JSON.parse( $scope.entity.goodsDesc.customAttributeItems);
                //规格选择
                $scope.entity.goodsDesc.specificationItems=JSON.parse($scope.entity.goodsDesc.specificationItems);
                //转化sku列表中的规格对象
                for (var i=0;i<$scope.entity.itemList.length;i++){
                    $scope.entity.itemList[i].spec=JSON.parse( $scope.entity.itemList[i].spec);
                }

            }
        );
    }

    //保存
    $scope.save=function(){
        $scope.entity.goodsDesc.introduction = editor.html();
        var serviceObject;//服务层对象
        if($scope.entity.goods.id!=null){//如果有ID
            serviceObject=goodsService2.update( $scope.entity ); //修改
        }else{
            serviceObject=goodsService2.add( $scope.entity  );//增加
        }
        serviceObject.success(
            function(response){
                if(response.success){
                    alert("新增成功！");
                    $scope.entity = {};
                    editor.html("")
                  /*  //重新查询
                    $scope.reloadList();//重新加载*/
                }else{
                    alert(response.message);
                }
            }
        );
    }


    //增加
    $scope.addgoods = function () {
        $scope.entity.goodsDesc.introduction = editor.html();

        goodsService2.addgoods($scope.entity).success(
            function (response) {
                alert("进去判断")
                if (response.success) {
                    alert("新增成功！");
                    $scope.entity = {};
                    editor.html("")
                } else {
                    alert("失败")
                    alert(response.message);
                }
            }
        );
    }


    //批量删除
    $scope.dele = function () {
        //获取选中的复选框
        goodsService2.dele($scope.selectIds).success(
            function (response) {
                if (response.success) {
                    $scope.reloadList();//刷新列表
                    $scope.selectIds = [];
                }
            }
        );
    }

    $scope.searchEntity = {};//定义搜索对象

    //搜索
    $scope.search = function (page, rows) {
        goodsService2.search(page, rows, $scope.searchEntity).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    }

    //上传图片
    $scope.uploadFile = function () {
        alert("进入上传方法")
        uploadService.uploadFile().success(
            function (response) {
                if (response.success) {
                    alert(response.message)
                    $scope.image_entity.url = response.message;
                } else {
                    alert(response.message);
                }
            }
        )
    }

    $scope.entity = {goodsDesc: {itemImages: [], specificationItems: []}};

    //将当前上传的图片实体存入图片列表
    $scope.add_image_entity = function () {
        $scope.entity.goodsDesc.itemImages.push($scope.image_entity);
    }

    //移除图片
    $scope.remove_image_entity = function (index) {
        $scope.entity.goodsDesc.itemImages.splice(index, 1);
    }

    //查询一级商品分类
    $scope.selectItemCat1List = function () {
       // alert("初始化..........");
        itemCatService.findByParentId(0).success(
            function (response) {
              //  alert("前端发起请求。。");
                $scope.itemCat1List = response;
            }
        )
    }

    //读取二级分类
    $scope.$watch('entity.goods.category1Id', function (newValue, oldValue) {
        //根据选择的值，查询二级分类
        itemCatService.findByParentId(newValue).success(
            function (response) {
                $scope.itemCat2List = response;
            }
        );
    })

    //读取三级分类
    $scope.$watch('entity.goods.category2Id', function (newValue, oldValue) {
        //根据选择的值，查询二级分类
        itemCatService.findByParentId(newValue).success(
            function (response) {
                $scope.itemCat3List = response;
            }
        );
    })

    //读取模板id
    //三级分类选择后，读取模板Id
    $scope.$watch('entity.goods.category3Id', function (newValue, oldValue) {
        itemCatService.findOne(newValue).success(
            function (response) {
                $scope.entity.goods.typeTemplateId = response.typeId;//跟新模板id
            }
        );
    });

    //根据模板ID号，读取品牌列表
    /*$scope.$watch('entity.goods.typeTemplateId',function (newValue,oldValue) {
        typeTemplateService.findOne(newValue).success(
            function (response) {
                $scope.typeTemplate=response;//模板对象
                /!*alert($scope.typeTemplate.brandIds);*!/
                //品牌列表类型转换
                $scope.typeTemplate.brandIds=JSON.parse($scope.typeTemplate.brandIds);

                $scope.entity.goodsDesc.customAttributeItems=JSON.parse ($scope.typeTemplate.customAttributeItems);
            }
        );

        //读取规格列表
        typeTemplateService.findSpecList(newValue).success(
            function (response) {
                alert(response.length)
                $scope.specList=response;
            }
        )

    })*/
    //模板ID选择后  更新模板对象
    $scope.$watch('entity.goods.typeTemplateId', function (newValue, oldValue) {
        typeTemplateService.findOne(newValue).success(
            function (response) {
                $scope.typeTemplate = response;//获取类型模板
                $scope.typeTemplate.brandIds = JSON.parse($scope.typeTemplate.brandIds);//品牌列表

                if ($location.search()['id']==null){
                 $scope.entity.goodsDesc.customAttributeItems = JSON.parse($scope.typeTemplate.customAttributeItems);//扩展属性
                }
                alert($scope.entity.goodsDesc.customAttributeItems)
            }
        );
        //查询规格列表
        typeTemplateService.findSpecList(newValue).success(
            function (response) {
                $scope.specList = response;
            }
        );

    });

    $scope.updateSpercAttribute = function ($event, name, value) {
        var object = $scope.searchObjectBykey(
            $scope.entity.goodsDesc.specificationItems, 'attributeName', name);
        if (object != null) {
            if ($event.target.checked) {
                object.attributeValue.push(value);
            } else {//取消勾选
                object.attributeValue.splice(object.attributeValue.indexOf(value), 1);//移除选项
                //如果选项都取消了，将词条记录移除
                if (object.attributeValue.length == 0) {
                         $scope.entity.goodsDesc.specificationItems.splice(
                        $scope.entity.goodsDesc.specificationItems.indexOf(object), 1
                    )
                }
            }
        }else{
            $scope.entity.goodsDesc.specificationItems.push(
                {"attributeName":name,"attributeValue":[value]});

        }

    }
    //创建SKU列表
    $scope.createItemList=function () {

        $scope.entity.itemList=[{spec:{},price:0,num:99999,status:'0',isDefault:'0' }];
      //列表初始化
        //alert($scope.entity.itemList);
        var items=  $scope.entity.goodsDesc.specificationItems;
        //alert("items"+JSON.stringify($scope.entity.goodsDesc.specificationItems));
        for (var i=0;i<items.length;i++){
            $scope.entity.itemList=addCoulum($scope.entity.itemList,items[i].attributeName,items[i].attributeValue);
        }
    }
    addCoulum=function (list,columnName,columnValue) {
            var newList=[];
        for (var i=0;i<list.length;i++){
            var oldRow=list[i];
            for (var j=0;j<columnValue.length;j++){
                var newRow=JSON.parse(JSON.stringify(oldRow));
                newRow.spec[columnName]=columnValue[j];
                newList.push(newRow);
            }
        }
        return newList;
    }
    $scope.status=['未审核','已审核','审核未通过','关闭'];//商品状态


    //定义数组封装返回数据
    $scope.itemCatList=[]; //商品分类列表

    //查询商品分类列表
    $scope.findItemCatList=function () {
        itemCatService.findAll().success(
            function (response) {
                for (var i=0;i<response.length;i++){
                    $scope.itemCatList[response[i].id]=response[i].name;
                }
            }
        );
    }

    //读取规格属性带勾选 //判断规格与规格选项是否应该被勾线
    $scope.checkAttributeValue=function (specName,optionName) {
        var items=$scope.entity.goodsDesc.specificationItems;
        var object= $scope.searchObjectBykey(items,'attributeName',specName);
        if (object!=null){
            if (object.attributeValue.indexOf(optionName)>=0){//如果能够查询到规格选项
                return true;
            }else{
                return false;
            }
        } else{
            return false;
        }



    }





});
