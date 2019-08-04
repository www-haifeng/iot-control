package com.shuzhi.websocket.socketvo;

import com.shuzhi.frt.entities.OfflinesRingVo;
import lombok.Data;

import java.util.List;

/**
 * @author huliang
 * @date 2019/8/1 15:24
 */
@Data
public class OfflinesRing {

    List<OfflinesRingVo> offlines;

    public void OfflinesRing(List<OfflinesRingVo> offlines) {
        this.offlines = offlines;
    }
}
