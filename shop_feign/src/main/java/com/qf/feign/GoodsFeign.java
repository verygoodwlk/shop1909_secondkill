package com.qf.feign;

import com.qf.entity.Goods;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;
import java.util.List;

@FeignClient("MICSERVICE-GOODS")
public interface GoodsFeign {

    /**
     * 添加商品
     * @return
     */
    @RequestMapping("/goods/insert")
    int insertGoods(@RequestBody Goods goods);

    /**
     * 商品列表
     * @return
     */
    @RequestMapping("/goods/list")
    List<Goods> goodsList();

    @RequestMapping("/goods/queryKillList")
    List<Goods> queryKillList(@RequestBody Date date);
}
