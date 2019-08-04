package com.shuzhi.websocket.socketvo;

import com.shuzhi.frt.entities.TDataDto;
import lombok.Data;

import java.util.List;

/**
 * @author huliang
 * @date 2019/8/1 11:14
 */
@Data
public class TDataVo {

    List<TDataDto> frt;
    public TDataVo(List<TDataDto> frt) {
        this.frt = frt;
    }
}
