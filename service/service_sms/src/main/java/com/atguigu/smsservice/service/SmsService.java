package com.atguigu.smsservice.service;

import java.util.Map;

public interface SmsService {
    boolean sendCode(String phone, Map<String, String> map);
}
