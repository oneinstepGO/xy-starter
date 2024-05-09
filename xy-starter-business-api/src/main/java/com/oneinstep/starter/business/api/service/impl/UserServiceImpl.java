package com.oneinstep.starter.business.api.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.oneinstep.starter.business.api.bean.domain.User;
import com.oneinstep.starter.business.api.mapper.UserMapper;
import com.oneinstep.starter.business.api.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

}
