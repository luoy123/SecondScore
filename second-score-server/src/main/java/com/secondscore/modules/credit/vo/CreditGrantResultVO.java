package com.secondscore.modules.credit.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CreditGrantResultVO {
    private Integer grantedCount = 0;
    private Integer skippedCount = 0;
    private List<String> messages = new ArrayList<>();
}

