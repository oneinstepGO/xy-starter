package com.oneinstep.starter.business.api.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.oneinstep.starter.business.api.bean.domain.User;
import com.oneinstep.starter.business.api.bean.dto.req.UserRegisterReqDTO;

public interface UserService extends IService<User> {
    /**
     * 注册
     *
     * @param registerReqDTO 注册信息
     * @return 注册结果
     */
    boolean register(UserRegisterReqDTO registerReqDTO);
}
