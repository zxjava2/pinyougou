package com.pinyougou.manager.controller;

import java.util.List;
import java.util.Map;

import com.pinyougou.pojo.TbBrand;
import entity.PageResult;
import entity.Result;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.sellergoods.service.BrandService;

@RestController
@RequestMapping("/brand")
public class BrandController {

	@Reference
	private BrandService brandService;

	@RequestMapping("/findAll")
	public List<TbBrand> findAll(){
		return brandService.findAll();
	}

	@RequestMapping("/findPage")
	public PageResult findPage(int page,int size){
		return brandService.findPage(page,size);
	}

	@RequestMapping("/add")
	public Result add(@RequestBody TbBrand tbBrand){
		try {
			brandService.add(tbBrand);
			return  new Result(true,"增加成功！");
		} catch (Exception e){
			e.printStackTrace();
			return  new Result(false,"增加失败！");
		}
	}

	//根据id查找品牌信息
	@RequestMapping("/findOne")
	public TbBrand findOne(@RequestParam("id")Long id){
			return brandService.findOne(id);
	}

	//修改试题信息
	@RequestMapping("/update")
	public Result update(@RequestBody TbBrand tbBrand){
		try {
			brandService.update(tbBrand);
			return  new Result(true,"修改成功！");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false,"修改失败！");
		}
	}

	//批量删除
	@RequestMapping("/delete")
	public Result delete(Long[] ids){
		try {
			brandService.delete(ids);
			return  new Result(true,"删除成功！");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false,"删除失败！");
		}
	}

	//查询加分页
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbBrand brand,int page,int rows){
		return brandService.findPage(brand,page,rows);
	}

	@RequestMapping("/selectOptionList")
	public List<Map> selectOptionList(){
		return brandService.selectOptionList();
	}
}
