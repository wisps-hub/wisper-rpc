package com.wisper.common.enums;

public enum RegisterType{
    ZOOKEEPER;

    public static RegisterType getEnum(String type){
        for (RegisterType value : RegisterType.values()) {
            if (value.name().equals(type)) {
                return value;
            }
        }
        return RegisterType.ZOOKEEPER;
    }
}
