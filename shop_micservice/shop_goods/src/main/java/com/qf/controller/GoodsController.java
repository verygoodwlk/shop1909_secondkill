package com.qf.controller;

import com.qf.entity.Goods;
import com.qf.service.IGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    private IGoodsService goodsService;

    /**
     * 添加商品
     * @return
     */
    @RequestMapping("/insert")
    public int insertGoods(@RequestBody Goods goods){
        System.out.println("商品服务：" + goods);
        return goodsService.insertGoods(goods);
    }

    /**
     * 商品列表
     * @return
     */
    @RequestMapping("/list")
    public List<Goods> goodsList(){
        return goodsService.goodsList();
    }

    /**
     * 查询对应场次的秒杀商品列表
     * @return
     */
    @RequestMapping("/queryKillList")
    public List<Goods> queryKillList(@RequestBody Date date){
        System.out.println("商品服务获得时间对象：" + date);
        List<Goods> goodsList = goodsService.queryKillList(date);
        return goodsList;
    }
}
