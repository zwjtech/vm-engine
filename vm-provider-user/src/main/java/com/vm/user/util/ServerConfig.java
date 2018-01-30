package com.vm.user.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * Created by ZhangKe on 2018/1/17.
 */
@PropertySource(value = {"classpath:server.properties"}, encoding = "utf-8")
@Component
public class ServerConfig {
    public static Long VM_USER_SESSION_TIMEOUT;

    @Value("${vm.user.session.timeout}")
    public void setVM_USER_SESSION_TIMEOUT(Long VM_USER_SESSION_TIMEOUT) {
        this.VM_USER_SESSION_TIMEOUT = VM_USER_SESSION_TIMEOUT;
    }
}