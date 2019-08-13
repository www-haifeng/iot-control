package com.shuzhi.controller;

import com.shuzhi.entity.Lighpole;
import com.shuzhi.entity.Lighpoles;
import com.shuzhi.service.LighpoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
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
public class LighpoleController {

    @Autowired
    private LighpoleService lighpoleService;


    @RequestMapping(value = "/lighpole/list", method = RequestMethod.GET)
    public List<Lighpoles> findAll() {
        // com.shuzhi.lightpole.entities.
        return lighpoleService.findAlls();
    }

    /**
     * 新增
     *
     * @param lighpole
     * @return
     */
    @RequestMapping(value = "/lighpole/add", method = RequestMethod.POST)
    public int addLighpole(Lighpole lighpole) {
        return lighpoleService.insert(lighpole);
    }

    /**
     * 根据Id查询
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/lighpole/get/{id}", method = RequestMethod.GET)
    public Lighpole findById(@PathVariable("id") Integer id) {
        Lighpole lighpole = new Lighpole();
        lighpole.setId(id.longValue());
        return lighpoleService.selectOne(lighpole);
    }

    /**
     * 根据Id删除
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/lighpole/deleteById/{id}", method = RequestMethod.DELETE)
    public int deleteById(@PathVariable("id") Integer id) {
        Lighpole lighpole = new Lighpole();
        lighpole.setId(id.longValue());
        return lighpoleService.delete(lighpole);
    }

    /**
     * 修改
     *
     * @param lighpole
     * @return
     */
    @RequestMapping(value = "/lighpole/update", method = RequestMethod.PUT)
    public int updateTLed(Lighpole lighpole) {
        return lighpoleService.updateByPrimaryKey(lighpole);
    }

}