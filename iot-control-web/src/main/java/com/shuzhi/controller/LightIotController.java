package com.shuzhi.controller;

import com.shuzhi.entity.TGatewayInfo;
import com.shuzhi.lightiotcomm.entities.TGateway;
import com.shuzhi.lightiotcomm.service.LightIotCommServiceApi;
import com.shuzhi.service.LighpoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
* @Program: LightIotController
* @Description:
* @Author: YuJQ
* @Create: 2019/8/13 13:13
**/
@RestController
@RequestMapping(value = "/lightIot")
public class LightIotController {

    @Autowired
    private LightIotCommServiceApi lightIotCommServiceApi;

    @RequestMapping(value = "/gatewayLocationInof", method = RequestMethod.GET)
    public List<TGatewayInfo> gatewayLocationInof(){
        List<TGateway> listTGateways= lightIotCommServiceApi.gatewayLocationInof();
        List<TGatewayInfo> TGatewayInfos= new ArrayList<TGatewayInfo>();
        if(!listTGateways.isEmpty()){
            for (TGateway listTGateway:
                    listTGateways) {
                TGatewayInfos.add(LightIotController.changeTGateway(listTGateway));
            }
        }

        return TGatewayInfos;
    }
    private static TGatewayInfo changeTGateway(TGateway tGateway){
        TGatewayInfo t = new TGatewayInfo();
        t.setId(tGateway.getId());
        t.setDid(tGateway.getDid());
        try{
            t.setLatitude(Double.parseDouble(tGateway.getLatitude()));
        }catch (Exception e){
            t.setLatitude(0.0);
        }
        try{
            t.setLongitude(Double.parseDouble(tGateway.getLongitude()));
        }catch (Exception e){
            t.setLongitude(0.0);
        }
        t.setName(tGateway.getName());
        return t;
    }
}
