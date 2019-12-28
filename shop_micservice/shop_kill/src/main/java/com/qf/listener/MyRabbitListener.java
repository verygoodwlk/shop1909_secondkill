package com.qf.listener;

import com.qf.entity.Goods;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

@Component
public class MyRabbitListener {

    @Autowired
    private Configuration configuration;

    @RabbitListener(queues = "kill_queue")
    public void msgHandler(Goods goods){
        //生成静态页面
        System.out.println("接收到消息生成静态页:" + goods);

        //获得classpath路径
        String path = MyRabbitListener.class.getResource("/").getPath() + "static/html";
        System.out.println("classpath:" + path);
        File file = new File(path);
        if(!file.exists()){
            file.mkdirs();
        }

        file = new File(file, goods.getId() + ".html");

        try(
                //准备一个静态页的输出路径
                Writer out = new FileWriter(file)
        ) {
            //获得对应的模板
            Template template = configuration.getTemplate("kill.ftlh");

            //准备数据
            Map<String, Object> map = new HashMap<>();
            map.put("goods", goods);

            //生成静态页
            template.process(map, out);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
