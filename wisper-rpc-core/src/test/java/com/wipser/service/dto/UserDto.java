package com.wipser.service.dto;

import com.wipser.service.enums.Sex;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserDto implements Serializable {
    private Long id;
    private Integer age;
    private Sex sex;
    private String name;
    private Boolean realNameAuth;
    private List<String> tag;
    private Map<String, String> attMap;
}
