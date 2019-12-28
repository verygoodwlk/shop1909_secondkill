package com.qf.service;

import com.qf.entity.Goods;

import java.util.Date;
import java.util.List;

public interface IGoodsService {

    int insertGoods(Goods goods);

    List<Goods> goodsList();

    List<Goods> queryKillList(Date date);
}
