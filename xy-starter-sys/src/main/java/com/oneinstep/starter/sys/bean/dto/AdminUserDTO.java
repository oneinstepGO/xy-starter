package com.oneinstep.starter.sys.bean.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 管理员信息
 */
@Getter
@Setter
@ToString
@Schema(description = "管理员信息")
public class AdminUserDTO extends AccountOwnerDTO {

}
