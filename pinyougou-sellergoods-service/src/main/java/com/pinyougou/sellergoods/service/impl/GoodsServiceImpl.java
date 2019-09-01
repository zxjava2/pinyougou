package com.pinyougou.sellergoods.service.impl;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;

import com.pinyougou.mapper.*;
import com.pinyougou.pojo.*;
import entity.Goods;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.sellergoods.service.GoodsService;
import entity.PageResult;
import com.pinyougou.pojo.TbGoodsExample.Criteria;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class GoodsServiceImpl implements GoodsService {

	@Autowired
	private TbGoodsMapper goodsMapper;
	@Autowired
	private TbGoodsDescMapper goodsDescMapper;

	@Autowired
    private TbItemMapper itemMapper;

	@Autowired
    private TbItemCatMapper itemCatMapper;

	//品牌
    @Autowired
    private TbBrandMapper brandMapper;

    //商家名称
    @Autowired
    private TbSellerMapper sellerMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbGoods> findAll() {
		return goodsMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbGoods> page=   (Page<TbGoods>) goodsMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(Goods goods) {
	/*	goodsMapper.insert(goods);*/
		goods.getGoods().setAuditStatus("0"); //状态：未审核
		goodsMapper.insert(goods.getGoods()); //插入商品基本信息
		//设置id 将商品基本表的Id给商品扩展表
		goods.getGoodsDesc().setGoodsId(goods.getGoods().getId());
		goodsDescMapper.insert(goods.getGoodsDesc()); //插入商品扩展数据
        savaItemList(goods); //插入sku数据

    }
         public void setItemValus(TbItem item, Goods goods){

            //商品分类
            item.setCategoryid(goods.getGoods().getCategory3Id());//三级分类
            item.setCreateTime(new Date()); //创建提起
            item.setUpdateTime(new Date()); //更新日期

            item.setGoodsId(goods.getGoods().getId()); //商品id
            item.setSellerId(goods.getGoods().getSellerId());// 商家编号id

            //分类名称
            TbItemCat itemCat = itemCatMapper.selectByPrimaryKey(goods.getGoods().getCategory3Id());
            item.setCategory(itemCat.getName());
            //品牌名称
            TbBrand brand = brandMapper.selectByPrimaryKey(goods.getGoods().getBrandId());
            item.setBrand(brand.getName());
            //商家名称(店铺名称)
            TbSeller seller = sellerMapper.selectByPrimaryKey(goods.getGoods().getSellerId());
            item.setSeller(seller.getNickName());

             //图片地址（取spu的第一个图片）
             List<Map>imageList = JSON.parseArray(goods.getGoodsDesc().getItemImages(), Map.class) ;
             if(imageList.size()>0){
                 item.setImage ( (String)imageList.get(0).get("url"));
             }
        }


/*
         for(TbItem item :goods.getItemList()){
            //标题
            String title= goods.getGoods().getGoodsName();
            Map<String,Object>specMap = JSON.parseObject(item.getSpec());
            for(String key:specMap.keySet()){
                title+=""+ specMap.get(key);
            }
            item.setTitle(title);
            item.setGoodsId(goods.getGoods().getId());//商品SPU编号
            item.setSellerId(goods.getGoods().getSellerId());//商家编号
            item.setCategoryid(goods.getGoods().getCategory3Id());//商品分类编号（3级）
            item.setCreateTime(new Date());//创建日期
            item.setUpdateTime(new Date());//修改日期
            //品牌名称
            TbBrand brand = brandMapper.selectByPrimaryKey(goods.getGoods().getBrandId());
            item.setBrand(brand.getName());
            //分类名称
            TbItemCat itemCat = itemCatMapper.selectByPrimaryKey(goods.getGoods().getCategory3Id());
            item.setCategory(itemCat.getName());
            //商家名称
            TbSeller seller = sellerMapper.selectByPrimaryKey(goods.getGoods().getSellerId());
            item.setSeller(seller.getNickName());
            //图片地址（取spu的第一个图片）
            List<Map>imageList = JSON.parseArray(goods.getGoodsDesc().getItemImages(), Map.class) ;
            if(imageList.size()>0){
                item.setImage ( (String)imageList.get(0).get("url"));
            }
            itemMapper.insert(item);
        }*/




	/**
	 * 修改
	 */
	@Override
	public void update(Goods goods){
        //更新基本表数据
	    goodsMapper.updateByPrimaryKey(goods.getGoods());
	    //更新扩展表数据
        goodsDescMapper.updateByPrimaryKey(goods.getGoodsDesc());
        //更新sku表
        //删除原有的sku数据
        TbItemExample example= new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andGoodsIdEqualTo(goods.getGoods().getId());
        itemMapper.deleteByExample(example);

        //插入新的SKU列表数据
        savaItemList(goods);//插入SKU商品数据

    }
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public Goods findOne( Long id ){
	    Goods goods=new Goods();
        //商品基本表
		TbGoods tbgoods=goodsMapper.selectByPrimaryKey(id);
		goods.setGoods(tbgoods);
		//商品扩展表
        TbGoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(id);
        goods.setGoodsDesc(goodsDesc);
        /* return   goods;*//*goodsMapper.selectByPrimaryKey(id);*/

		//读取sku表
        TbItemExample example=new TbItemExample();
        //创建条件
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andGoodsIdEqualTo(id);
        List<TbItem> itemList = itemMapper.selectByExample(example);
        goods.setItemList(itemList);
        return goods;
    }

    //插入sku列表数据
    public void savaItemList(Goods goods){
        if ("1".equals(	goods.getGoods().getIsEnableSpec())){
            for ( TbItem item: goods.getItemList()){
                //构建标题 SPU名称+规格选项值
                String title=goods.getGoods().getGoodsName();//SPU
                Map<String,Object> map = JSON.parseObject(item.getSpec());
                for (String key:map.keySet()){
                    title+=" "+map.get(key);
                }
                item.setTitle(title);
                setItemValus(item,goods );
                itemMapper.insert(item);

            }
        }else{
            TbItem item=new TbItem();
            item.setTitle(goods.getGoods().getGoodsName());//商品KPU+规格描述串作为SKU名称
            item.setPrice( goods.getGoods().getPrice() );//价格
            item.setStatus("1");//状态
            item.setIsDefault("1");//是否默认
            item.setNum(99999);//库存数量
            item.setSpec("{}");
            setItemValus(item,goods );
            itemMapper.insert(item);

        }

    }



	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
            TbGoods goods = goodsMapper.selectByPrimaryKey(id);
            goods.setIsDelete("1"); //设置字段值为1
            goodsMapper.updateByPrimaryKey(goods);//更新其值
        }
	}
	
	
		@Override
	public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbGoodsExample example=new TbGoodsExample();
			Criteria criteria = example.createCriteria();
		criteria.andIsDeleteIsNull();//非删除状态
		if(goods!=null){			
						if(goods.getSellerId()!=null && goods.getSellerId().length()>0){
				//criteria.andSellerIdLike("%"+goods.getSellerId()+"%");
                            criteria.andSellerIdEqualTo(goods.getSellerId());
						}
			if(goods.getGoodsName()!=null && goods.getGoodsName().length()>0){
				criteria.andGoodsNameLike("%"+goods.getGoodsName()+"%");
			}
			if(goods.getAuditStatus()!=null && goods.getAuditStatus().length()>0){
				criteria.andAuditStatusLike("%"+goods.getAuditStatus()+"%");
			}
			if(goods.getIsMarketable()!=null && goods.getIsMarketable().length()>0){
				criteria.andIsMarketableLike("%"+goods.getIsMarketable()+"%");
			}
			if(goods.getCaption()!=null && goods.getCaption().length()>0){
				((TbGoodsExample.Criteria) criteria).andCaptionLike("%"+goods.getCaption()+"%");
			}
			if(goods.getSmallPic()!=null && goods.getSmallPic().length()>0){
				criteria.andSmallPicLike("%"+goods.getSmallPic()+"%");
			}
			if(goods.getIsEnableSpec()!=null && goods.getIsEnableSpec().length()>0){
				criteria.andIsEnableSpecLike("%"+goods.getIsEnableSpec()+"%");
			}
			if(goods.getIsDelete()!=null && goods.getIsDelete().length()>0){
				criteria.andIsDeleteLike("%"+goods.getIsDelete()+"%");
			}
	
		}
		
		Page<TbGoods> page= (Page<TbGoods>)goodsMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Override
	public void updateStatus(Long[] ids, String status) {
		for (Long id:ids){
            TbGoods goods = goodsMapper.selectByPrimaryKey(id);
            goods.setAuditStatus(status);
            goodsMapper.updateByPrimaryKey(goods);
        }
	}
}
