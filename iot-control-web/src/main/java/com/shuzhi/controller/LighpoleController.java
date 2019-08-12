package com.shuzhi.controller;

import com.shuzhi.entity.Lighpole;
import com.shuzhi.service.LighpoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * @author shuzhi
 * @date 2019-08-12 10:42:37
 */

@RestController
@RequestMapping(value = "/lighpole")
public class LighpoleController  {

    @Autowired
    private LighpoleService lighpoleService;


    @RequestMapping(value = "/lighpole/list", method = RequestMethod.GET)
    public List<Lighpole> findAll() {
        // com.shuzhi.lightpole.entities.
       return lighpoleService.findAlls();
    }


}