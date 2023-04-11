package com.qiqilm.server.admin.proportion;

import com.qiqilm.server.admin.exception.SmsProviderNotFoundException;
import lombok.Getter;

import java.util.Arrays;
import java.util.StringJoiner;

@Getter
public enum SmsProvider {

    TENCENT(0),
    ALIYUN(1),
    BAIDU(2),
    HUAWEI(3);

    SmsProvider(int code) {
        this.code = code;
    }

    private final int code;

    public static SmsProvider getProviderByCode(int code) {
        return Arrays.stream(SmsProvider.values()).filter(p -> p.getCode() == code).findAny()
                .orElseThrow(() -> new SmsProviderNotFoundException(String.format("No provider found using code %d", code)));
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", SmsProvider.class.getSimpleName() + "[", "]")
                .add("code=" + code)
                .toString();
    }
}
