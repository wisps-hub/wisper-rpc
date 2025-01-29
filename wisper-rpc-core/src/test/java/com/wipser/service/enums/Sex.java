package com.wipser.service.enums;

public enum Sex {
    MAN(1, "男"),
    WOMAN(0, "女")
    ;

    private Integer code;
    private String desc;

    Sex(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Sex getByCode(Integer code){
        if (code == null){
            return null;
        }
        for (Sex sex : Sex.values()) {
            if (sex.code.equals(code)) {
                return sex;
            }
        }
        return null;
    }

}
