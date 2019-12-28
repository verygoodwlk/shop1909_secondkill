package com.qf.controller;

import com.qf.entity.Goods;
import com.qf.entity.ResultData;
import com.qf.feign.GoodsFeign;
import com.qf.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/kill")
public class KillController {

    @Autowired
    private GoodsFeign goodsFeign;

    /**
     * 查询当前的秒杀场次
     * @return
     */
    @RequestMapping("/queryKillTimes")
    public ResultData<List<Date>> queryKillTimes(){

        List<Date> dates = new ArrayList<>();

        //获得当前时间
        Date now = DateUtil.getNextNDate(0);
        //获得下一个小时的时间
        Date next1 = DateUtil.getNextNDate(1);
        //获得下下个小时的时间
        Date next2 = DateUtil.getNextNDate(2);
        dates.add(now);
        dates.add(next1);
        dates.add(next2);

        return new ResultData<List<Date>>().setCode(ResultData.ResultCodeList.OK).setData(dates);
    }

    /**
     * 查询对应场次的秒杀商品列表
     * @return
     */
    @RequestMapping("/queryKillList")
    public ResultData<List<Goods>> queryKillList(Integer n){

        //获得对应的时间
        Date time = DateUtil.getNextNDate(n);
        //根据时间查询对应的秒杀商品信息
        List<Goods> goodsList = goodsFeign.queryKillList(time);
        System.out.println(n + " 秒杀服务获得整点场次：" + goodsList);

        return new ResultData<List<Goods>>().setCode(ResultData.ResultCodeList.OK)
                .setData(goodsList);
    }
}
