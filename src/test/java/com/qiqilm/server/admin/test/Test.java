package com.qiqilm.server.admin.test;

import com.qiqilm.server.admin.PlatformAdminApplication;
import com.qiqilm.server.admin.config.LiveCenterConfig;
import lombok.extern.log4j.Log4j2;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@Log4j2
@SpringBootTest( classes = { PlatformAdminApplication.class } )// 指定启动类
@ActiveProfiles( "7700" )
public class Test {

    @Autowired
    private StringEncryptor stringEncryptor;

    @org.junit.jupiter.api.Test
    public void test1() {
        String agent = "77ql";
        // String host  = "livedata.cluster-c2mgsz0wk6v3.ap-east-1.rds.amazonaws.com"; // 新平台
        String host  = "heimdallasg-live-database-6871ee299fd9b97f.elb.ap-east-1.amazonaws.com"; // 新平台 读写分离
        //String host  = "back01.cluster-c2mgsz0wk6v3.ap-east-1.rds.amazonaws.com"; // 迁移平台
        String jdbc = stringEncryptor.encrypt( "jdbc:mysql://" + host + ":3306/" + agent
                + "_main?useUnicode=true&characterEncoding=utf-8&useSSL=false&autoReconnect=true&failOverReadOnly=false" );
        System.out.println( agent + "main: " + jdbc );

        // jdbc:mysql://livedata.cluster-c2mgsz0wk6v3.ap-east-1.rds.amazonaws.com:3306/cx_live?useUnicode=true&characterEncoding=utf-8&useSSL=false&autoReconnect=true&failOverReadOnly=false

        String jdbc2 = stringEncryptor.encrypt( "jdbc:mysql://" + host + ":3306/" + agent
                + "_live?useUnicode=true&characterEncoding=utf-8&useSSL=false&autoReconnect=true&failOverReadOnly=false" );
        System.out.println( agent + "live: " + jdbc2 );

        String jdbc3 = stringEncryptor.encrypt( "jdbc:mysql://" + host + ":3306/" + agent
                + "_lottery?useUnicode=true&characterEncoding=utf-8&useSSL=false&autoReconnect=true&failOverReadOnly=false" );
        System.out.println( agent + "lottery: " + jdbc3 );


        String username = stringEncryptor.encrypt( "servercp.zczoyz.ng.0001.ape1.cache.amazonaws.com" );
        System.out.println( "redisHost: " + username );

        //String password = stringEncryptor.encrypt( "X*5GXuIIMo5gY1ua" );
        //System.out.println( "databasePass: " + password );
    }

    @org.junit.jupiter.api.Test
    public void test2() {
        String jdbc = stringEncryptor.decrypt( "uPJFxgjBH4319Jv9gOHIhyHmu1gg21ACb2FeqtgF1TI2F7vf52pFzMeOmXBMdEfmbmsmPDpFFbHwSgf2ixVb9A==" );
        System.out.println( jdbc );

        String username = stringEncryptor.decrypt( "Eo5Gd7+EhU21sjUMLD7UlgNUhoJ8sxfhPJR0wMauFg1Bhrh3ltleHVANxmf6ed+gAtgMbVkFf62nP/b9m3r8eQ==" );
        System.out.println( username );
    }

    @org.junit.jupiter.api.Test
    public void test3() {
        System.out.println( LiveCenterConfig.me.getLiveSubAgents() );
    }
}
