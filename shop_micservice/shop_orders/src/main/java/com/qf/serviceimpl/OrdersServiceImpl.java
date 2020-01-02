package com.qf.serviceimpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qf.dao.OrderDetilsMapper;
import com.qf.dao.OrdersMapper;
import com.qf.entity.Goods;
import com.qf.entity.OrderDetils;
import com.qf.entity.Orders;
import com.qf.feign.GoodsFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class OrdersServiceImpl implements IOrderService {

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private OrderDetilsMapper orderDetilsMapper;

    @Autowired
    private GoodsFeign goodsFeign;

//    @Override
//    @Transactional
//    public Orders insertOrder(Integer[] cids, Integer aid, User user) {
//
//        //根据aid获得收货地址信息
//        Address address = addressService.queryById(aid);
//
//        //获得购物清单
//        List<ShopCart> shopCarts = cartService.queryCartsByCid(cids);
//
//        double allprice = PriceUtil.allPrice(shopCarts);
//
//        //创建订单
//        Orders orders = new Orders()
//                .setOrderid(UUID.randomUUID().toString())
//                .setUid(user.getId())
//                .setAllprice(BigDecimal.valueOf(allprice))
//                .setPhone(address.getPhone())
//                .setCode(address.getCode())
//                .setAddress(address.getAddress())
//                .setPerson(address.getPerson());
//
//        //保存订单
//        ordersMapper.insert(orders);
//
//        //保存订单详情
//        for (ShopCart shopCart : shopCarts) {
//
//            OrderDetils orderDetils = new OrderDetils()
//                    .setGid(shopCart.getGid())
//                    .setDetilsPrice(shopCart.getCartPrice())
//                    .setNumber(shopCart.getNumber())
//                    .setPrice(shopCart.getGoods().getPrice())
//                    .setSubject(shopCart.getGoods().getSubject())
//                    .setOid(orders.getId())
//                    .setFmurl(shopCart.getGoods().getFmurl());
//
//            orderDetilsMapper.insert(orderDetils);
//        }
//
//        //删除下单的购物清单
//        cartService.deleteByCids(cids);
//
//        return orders;
//    }

    @Override
    public List<Orders> queryByUid(Integer uid) {

        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("uid", uid);
        queryWrapper.orderByDesc("create_time");
        List<Orders> ordersList = ordersMapper.selectList(queryWrapper);

        for (Orders orders : ordersList) {
            //查询订单详情
            QueryWrapper queryWrapper2 = new QueryWrapper();
            queryWrapper2.eq("oid", orders.getId());
            List<OrderDetils> list = orderDetilsMapper.selectList(queryWrapper2);
            orders.setOrderDetils(list);
        }

        return ordersList;
    }

    @Override
    public Orders queryById(Integer id) {
        Orders orders = ordersMapper.selectById(id);

        //查询订单详情
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("oid", orders.getId());
        List<OrderDetils> list = orderDetilsMapper.selectList(queryWrapper);
        orders.setOrderDetils(list);

        return orders;
    }

    @Override
    public Orders QueryByOid(String oid) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("orderid", oid);
        Orders orders = ordersMapper.selectOne(queryWrapper);

        //查询订单详情
        QueryWrapper queryWrapper2 = new QueryWrapper();
        queryWrapper2.eq("oid", orders.getId());
        List<OrderDetils> list = orderDetilsMapper.selectList(queryWrapper2);
        orders.setOrderDetils(list);

        return orders;
    }

    @Override
    public int updateOrderStatus(String orderid, Integer status) {

        Orders orders = this.QueryByOid(orderid);
        orders.setStatus(status);

        return ordersMapper.updateById(orders);
    }

    @Override
    @Transactional
    public int insertOrders(Integer gid, Integer uid, Integer gnumber) {

        Goods goods = goodsFeign.queryById(gid);

        //创建订单
        Orders orders = new Orders()
                .setOrderid(UUID.randomUUID().toString())
                .setUid(uid)
                .setAllprice(goods.getGoodsKill().getKillPrice().multiply(BigDecimal.valueOf(gnumber)));

        //保存订单
        ordersMapper.insert(orders);

        //保存订单详情
        OrderDetils orderDetils = new OrderDetils()
                .setGid(goods.getId())
                .setDetilsPrice(goods.getGoodsKill().getKillPrice().multiply(BigDecimal.valueOf(gnumber)))
                .setNumber(gnumber)
                .setPrice(goods.getGoodsKill().getKillPrice())
                .setSubject(goods.getSubject())
                .setOid(orders.getId())
                .setFmurl(goods.getFmUrl());

        orderDetilsMapper.insert(orderDetils);

        return 1;
    }
}
