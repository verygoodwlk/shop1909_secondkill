package com.qf.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qf.dao.GoodsImagesMapper;
import com.qf.dao.GoodsKillMapper;
import com.qf.dao.GoodsMapper;
import com.qf.entity.Goods;
import com.qf.entity.GoodsImages;
import com.qf.entity.GoodsSecondkill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GoodsServiceImpl implements IGoodsService {

    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private GoodsImagesMapper goodsImagesMapper;

    @Autowired
    private GoodsKillMapper goodsKillMapper;

    @Override
    @Transactional
    public int insertGoods(Goods goods) {

        //添加商品
        goodsMapper.insert(goods);

        //添加封面
        GoodsImages fmgoodsImages = new GoodsImages()
                .setUrl(goods.getFmUrl())
                .setGid(goods.getId())
                .setIsfengmian(1);
        goodsImagesMapper.insert(fmgoodsImages);

        //添加其他图片
        for (String url : goods.getOtherUrl()) {
            GoodsImages goodsImages = new GoodsImages()
                    .setUrl(url)
                    .setGid(goods.getId())
                    .setIsfengmian(0);
            goodsImagesMapper.insert(goodsImages);
        }

        //TODO 添加秒杀信息
        if (goods.getType() == 2){
            //说明是秒杀商品 - 添加秒杀信息
            GoodsSecondkill goodsKill = goods.getGoodsKill();
            goodsKill.setGid(goods.getId());
            goodsKillMapper.insert(goodsKill);
        }

        //TODO 将商品信息放入rabbitmq， 同步到索引库中

        return 1;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Goods> goodsList() {

        //查询所有商品
        List<Goods> goods = goodsMapper.selectList(null);

        //查询商品图片
        for (Goods good : goods) {
            //查询相关图片
            QueryWrapper queryWrapper = new QueryWrapper();
            queryWrapper.eq("gid", good.getId());
            List<GoodsImages> images = goodsImagesMapper.selectList(queryWrapper);

            for (GoodsImages image : images) {
                if(image.getIsfengmian() == 1){
                    //是封面
                    good.setFmUrl(image.getUrl());
                } else {
                    //非封面
                    good.addOtherUrl(image.getUrl());
                }
            }

            //处理秒杀信息
            if (good.getType() == 2){
                GoodsSecondkill goodsSecondkill = goodsKillMapper.selectOne(queryWrapper);
                good.setGoodsKill(goodsSecondkill);
            }
        }

        return goods;
    }
}
