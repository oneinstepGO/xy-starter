package com.oneinstep.starter.sys.bean.bo;

import com.oneinstep.starter.sys.bean.domain.SysUser;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class SysUserBO extends SysUser implements Serializable {

    private List<Long> roleIds;
}
