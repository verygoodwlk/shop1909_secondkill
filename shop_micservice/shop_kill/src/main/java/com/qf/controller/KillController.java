package com.qf.controller;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.qf.aop.IsLogin;
import com.qf.aop.UserHolder;
import com.qf.entity.Goods;
import com.qf.entity.ResultData;
import com.qf.entity.User;
import com.qf.feign.GoodsFeign;
import com.qf.util.DateUtil;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/kill")
public class KillController {

    @Autowired
    private GoodsFeign goodsFeign;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private DefaultKaptcha defaultKaptcha;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private String lua = "--获得参数\n" +
            "local gid = KEYS[1]\n" +
            "local gnumber = tonumber(ARGV[1])\n" +
            "local uid = ARGV[2]\n" +
            "local now = tonumber(ARGV[3])\n" +
            "\n" +
            "--获得库存\n" +
            "local gsave = tonumber(redis.call('get', 'gsave_'..gid))\n" +
            "--判断库存\n" +
            "if gsave < gnumber then\n" +
            "\t--库存不足\n" +
            "\treturn -1\n" +
            "end\n" +
            "\n" +
            "--修改库存\n" +
            "local newSave = tonumber(redis.call('decrby', 'gsave_'..gid, gnumber))\n" +
            "--记录排队位置\n" +
            "redis.call('zadd', 'paidui_'..gid, now, uid)\n" +
            "\n" +
            "--抢购成功\n" +
            "return 1";

    /**
     * 查询当前的秒杀场次
     * @return
     */
    @RequestMapping("/queryKillTimes")
    @ResponseBody
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
    @ResponseBody
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

    /**
     * 获取当前时间
     * @return
     */
    @ResponseBody
    @RequestMapping("/queryNow")
    public ResultData<Date> queryNow(){
        return new ResultData<Date>().setCode(ResultData.ResultCodeList.OK).setData(new Date());
    }

    /**
     * 立刻抢购
     * @return
     */
    @IsLogin(mustLogin = true)
    @RequestMapping("/qiangGou")
    public String qiangGou(Integer gid, Integer gnumber, Model model){

        if(gnumber == null || gnumber == 0){
            gnumber = 1;
        }

        //获得登录的用户信息
        User user = UserHolder.getUser();
        System.out.println(user.getNickname() + "抢购了id为" + gid + "商品！");

        //判定
        long result = redisTemplate.execute(
                new DefaultRedisScript<>(lua, Long.class),
                Collections.singletonList(gid + ""),
                gnumber + "",
                user.getId() + "",
                System.currentTimeMillis() + ""
        );

        //是否抢购成功
        if(result != -1){

            Map<String, Object> map = new HashMap<>();
            map.put("gid", gid);
            map.put("gnumber", gnumber);
            map.put("uid", user.getId());

            //将商品信息放入rabbitmq中
            rabbitTemplate.convertAndSend("kill_exchange", "", map);

            model.addAttribute("gid", gid);
            return "paidui";
        }

        return "fail";
    }

    /**
     * 获得验证码
     */
    @RequestMapping("/code")
    public void getCode(HttpServletResponse response){
        //验证码的文本
        String text = defaultKaptcha.createText();
        //根据验证码的文本生成图片
        BufferedImage image = defaultKaptcha.createImage(text);

        //将验证码的值存入redis
        String token = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(token, text);
        redisTemplate.expire(token, 1, TimeUnit.MINUTES);

        //将uuid写入用户的cookid
        Cookie cookie = new Cookie("codeToken", token);
        cookie.setMaxAge(60);
        cookie.setPath("/");
        response.addCookie(cookie);

        //将二维码图片设置到浏览器端
        try {
            ImageIO.write(image, "jpg", response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获得当前排队位置
     * @return
     */
    @RequestMapping("/getRank")
    @IsLogin
    @ResponseBody
    public ResultData<String> getRank(Integer gid){
        //获得当前的用户信息
        User user = UserHolder.getUser();

        //获得排名
        Long rank = redisTemplate.opsForZSet().rank("paidui_" + gid, user.getId() + "");
        System.out.println(user.getId() + "当前排名：" + rank);
        if(rank == null){
            //没有排名
            return new ResultData<String>().setCode(ResultData.ResultCodeList.OK).setData("抢购成功！");
        }

        //当前正在排队
        return new ResultData<String>().setCode(ResultData.ResultCodeList.ERROR).setData((rank + 1) + "");
    }
}
