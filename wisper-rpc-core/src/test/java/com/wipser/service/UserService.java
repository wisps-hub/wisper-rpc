package com.wipser.service;

import com.wipser.service.dto.UserDto;

public interface UserService {
    UserDto getByUserId(Long userid);
    Long createUser(UserDto userDto);
}
