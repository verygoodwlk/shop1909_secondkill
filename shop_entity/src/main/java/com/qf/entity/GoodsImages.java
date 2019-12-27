package com.qf.entity;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class GoodsImages extends BaseEntity {

    private Integer gid;
    private String info;
    private String url;
    private Integer isfengmian;
}
