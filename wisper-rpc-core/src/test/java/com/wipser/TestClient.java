package com.wipser;


import com.wisper.client.proxy.ClientProxy;
import com.wipser.service.UserService;
import com.wipser.service.dto.UserDto;
import com.wipser.service.enums.Sex;

public class TestClient {
    public static void main(String[] args) throws InterruptedException {
        ClientProxy proxy = new ClientProxy();
        UserService userService = proxy.getProxy(UserService.class);

//        for (int i = 0; i < 120; i++) {
//            final Long id = (long) i;
//            if (i % 30 == 0) {
//                Thread.sleep(10000);
//            }
        long id = 1L;
            new Thread(() -> {
                UserDto userDto = userService.getByUserId(id);
                System.out.println(String.format("[client_main]请求结果: %s", userDto));
                Long uid = userService.createUser(UserDto.builder().id(id).age(23)
                        .sex(Sex.WOMAN).name("User" + id).build());
                System.out.println(String.format("[client_main]请求结果: [id: %s]", uid));
            }).start();
//        }
    }
}
