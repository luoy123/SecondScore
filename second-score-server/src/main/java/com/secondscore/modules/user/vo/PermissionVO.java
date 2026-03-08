package com.secondscore.modules.user.vo;

import lombok.Data;

@Data
public class PermissionVO {
    private Long id;
    private String permCode;
    private String permName;
    private String permType;
    private Integer status;
}
