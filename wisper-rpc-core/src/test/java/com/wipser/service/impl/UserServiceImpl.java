package com.wipser.service.impl;

import com.google.common.collect.Lists;
import com.wipser.service.UserService;
import com.wipser.service.dto.UserDto;
import com.wipser.service.enums.Sex;

import java.util.HashMap;

public class UserServiceImpl implements UserService {
    @Override
    public UserDto getByUserId(Long userid) {
//        try {
//            //模拟重试机制
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
        UserDto userDto = UserDto.builder().id(userid).age(25)
                .realNameAuth(true)
                .name("lilia")
                .sex(Sex.WOMAN)
                .tag(Lists.newArrayList("java", "springBoot", "es"))
                .attMap(new HashMap<String, String>() {{
                    put("title", "java");
                    put("skill", "springboot");
                }})
                .build();
        return userDto;
    }

    @Override
    public Long createUser(UserDto userDto) {
        System.out.println("数据插入成功: " + userDto.toString());
        return userDto.getId();
    }
}
