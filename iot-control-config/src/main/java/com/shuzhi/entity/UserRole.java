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

@Table(name = "t_user_role")
@Data
@EqualsAndHashCode(callSuper = true)
public class UserRole extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

    @Id
    private Integer id;
		
    /**
     * 角色id
     */
    @Column(name = "role_id")
    private Integer roleId;
		
    /**
     * 用户id
     */
    @Column(name = "user_id")
    private Integer userId;
		
    /**
     * 更新时间
     */
    @Column(name = "update_time")
    private Date updateTime;
		
    /**
     * 创建时间
     */
    @Column(name = "creation_time")
    private Date creationTime;
	
}
