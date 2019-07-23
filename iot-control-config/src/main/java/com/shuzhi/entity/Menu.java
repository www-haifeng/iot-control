package com.shuzhi.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Date;
import java.util.List;


/**
 * @author shuzhi
 * @date 2019-07-05 10:40:26
 */

@Table(name = "t_menu")
@Data
public class Menu implements Serializable {
    private static final long serialVersionUID = 1L;


    @Id
    private Integer urlId;

    /**
     * 资源路径 这里可以使用通配符 比如 /user/role/**
     */
    @Column(name = "url")
    private String url;

    /**
     * 资源名称
     */
    @Column(name = "url_name")
    private String urlName;

    /**
     * 父菜单的id  自关联
     */
    @Column(name = "parent_menu")
    private Integer parentMenu;

    /**
     * 子菜单的id  自关联
     */
    @Column(name = "sub_menu")
    private Integer subMenu;

    /**
     * 是否是一个按钮  按钮不允许有子菜单 0否 1是
     */
    @Column(name = "is_button")
    private Integer isButton;

    /**
     * 创建时间
     */
    @Column(name = "creation_time")
    private Date creationTime;

    /**
     * 更新时间
     */
    @Column(name = "update_time")
    private Date updateTime;

    /**
     * 创建人
     */
    @Column(name = "create_user")
    private Integer createUser;

    /**
     * 子目录
     */
    @Transient
    private List<Menu> menuList;

}
