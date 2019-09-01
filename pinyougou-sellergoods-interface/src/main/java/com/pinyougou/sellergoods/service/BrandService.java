package com.pinyougou.sellergoods.service;

import java.util.List;
import java.util.Map;

import com.pinyougou.pojo.TbBrand;
import entity.PageResult;

/**
 * 品牌接口
 * @author Administrator
 *
 */
public interface BrandService {

	public List<TbBrand> findAll();

	public PageResult findPage(int pageNum, int pageSize);

	//增加品牌
	public void add(TbBrand tbBrand);

	//根据id查询出品牌
	public TbBrand findOne(Long id);

	//修改
	public void update(TbBrand tbBrand);

	//删除
	public void delete(Long[] ids);


	//品牌分页查询
    public PageResult findPage(TbBrand brand,int pageNum,int pageSize);

	/**
	 * 品牌下拉框数据
	 */
	List<Map> selectOptionList();

}
