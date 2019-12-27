package com.qf.controller;

import com.qf.entity.Goods;
import com.qf.entity.GoodsSecondkill;
import com.qf.entity.ResultData;
import com.qf.feign.GoodsFeign;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/goods")
public class GoodsController {

    //图片上传路径
    private String uploadPath = "C:/worker/imgs";

    @Autowired
    private GoodsFeign goodsFeign;

    @RequestMapping("/list")
    public String list(Model model){
        //调用商品服务，查询所有商品
        List<Goods> goods = goodsFeign.goodsList();
        model.addAttribute("goodsList" , goods);
        return "goodslist";
    }


    @RequestMapping("/uploader")
    @ResponseBody
    public ResultData<String> uploader(MultipartFile file){

        String fileName = UUID.randomUUID().toString();
        String path = uploadPath + "/" + fileName;

        try(
                InputStream in = file.getInputStream();
                OutputStream out = new FileOutputStream(path);
        ) {
            IOUtils.copy(in, out);
            //上传成功
            return new ResultData<String>().setCode(ResultData.ResultCodeList.OK).setData(path);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ResultData<String>().setCode(ResultData.ResultCodeList.ERROR);
    }

    /**
     * 图片回显
     */
    @RequestMapping("/showimg")
    @ResponseBody
    public void showImage(String imgPath, HttpServletResponse response){
        try (
                InputStream in = new FileInputStream(imgPath);
                ServletOutputStream out = response.getOutputStream();
        ){
            IOUtils.copy(in, out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     * 添加商品
     * @return
     */
    @RequestMapping("/insert")
    public String insert(Goods goods, GoodsSecondkill goodsSecondkill){
        goods.setGoodsKill(goodsSecondkill);
        //调用商品服务，保存图片
        goodsFeign.insertGoods(goods);
        return "redirect:list";
    }


}
