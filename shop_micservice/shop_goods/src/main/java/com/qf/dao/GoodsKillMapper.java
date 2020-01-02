package com.qf.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qf.entity.GoodsSecondkill;
import org.apache.ibatis.annotations.Param;

public interface GoodsKillMapper extends BaseMapper<GoodsSecondkill> {

    int updateKillSave(@Param("gid") Integer gid, @Param("gnumber") Integer gnumber);
}
