package com.shuzhi.websocket.socketvo;

import lombok.Data;

import java.util.List;

/**
 * @author zgk
 * @description
 * @date 2019-07-30 13:36
 */
@Data
public class SumsMsg {

    private List<SumsVo> sums;

    public SumsMsg(List<SumsVo> sumsVos) {
        this.sums = sumsVos;

    }
}
