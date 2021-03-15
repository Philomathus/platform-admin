package com.qiqilm.server.admin.task;

import com.qiqilm.server.admin.domain.ConfigDomain;
import com.qiqilm.server.admin.service.IConfigDomainService;
import com.qiqilm.server.admin.utils.DateUtils;
import com.qiqilm.server.admin.utils.RobotMessage;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.util.List;
import java.util.Objects;

@Component
@Log4j2
public class DomainTask implements Serializable {
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private IConfigDomainService configDomainService;
    @Autowired
    private RobotMessage robotMessage;

    private static final long serialVersionUID = 1L;

    @Scheduled(cron = "0/360 * * * * ?")
    public void checkDomain() {
        log.info("轮询检测域名" + DateUtils.getTime());
        ConfigDomain configDomain = new ConfigDomain();
        List<ConfigDomain> list = configDomainService.selectConfigDomainList(configDomain);
        for (ConfigDomain li : list) {
            String url = li.getDomain() + "/verif/ping";
            boolean a = doGet(url, 1);
              if(!a){
                  String warnText="这个域名挂了:"+li.getDomain();
                  robotMessage.send( warnText );
              }
        }
    }


    public boolean doGet(String url, int retryNum) {
        if (retryNum > 3) {
            log.error("url:{}访问三次失败，退出重试", url);
            return false;
        }
        try {
            ResponseEntity<Object> resultEntity = restTemplate.getForEntity(url, Object.class);
            if (resultEntity.getStatusCode() == HttpStatus.OK){
                return true;
            }
        } catch (Exception e) {
            log.warn(e.getMessage(),e);
            retryNum++;
            return doGet(url, retryNum);
        }
        return false;
    }
}
