package com.shuzhi.entity;

import com.shuzhi.common.basemapper.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;


/**
 * @author shuzhi
 * @date 2019-07-05 10:40:26
 */

@Table(name = "t_role")
@Data
@EqualsAndHashCode(callSuper = true)
public class Role extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

    @Id
    private Integer roleId;
		
    /**
     * 角色名
     */
    @Column(name = "role_name")
    private String roleName;
		
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
     * 角色编号
     */
    @Column(name = "role_code")
    private String roleCode;
	
}
