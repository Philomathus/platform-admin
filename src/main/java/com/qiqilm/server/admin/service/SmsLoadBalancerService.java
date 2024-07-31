package com.qiqilm.server.admin.service;

import com.qiqilm.server.admin.domain.ServerSms;
import com.qiqilm.server.admin.exception.BusinessException;
import com.qiqilm.server.admin.mapper.ServerSmsMapper;
import com.qiqilm.server.admin.proportion.SmsLoadBalancerConfiguration;
import com.qiqilm.server.admin.proportion.SmsProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SmsLoadBalancerService {

    private final SmsLoadBalancerConfiguration smsLoadBalancerConfiguration;
    private final ServerSmsMapper serverSmsMapper;

    public SmsProvider getProvider() {
        initializeProviders();
        return smsLoadBalancerConfiguration.chooseProvider();
    }

    private void initializeProviders() {
        if (!smsLoadBalancerConfiguration.hasProviderWeights()) {
            ServerSms queryServerSms = new ServerSms();
            queryServerSms.setIsEffect(1);
            List<ServerSms> serverSmsList = serverSmsMapper.selectServerSmsList(queryServerSms);
            log.info("Retrieved serverSms list size: {}", serverSmsList.size());
            if(serverSmsList.size() == 0) {
                throw new BusinessException("There are no SMS providers");
            }
            serverSmsList.forEach(serverSms -> smsLoadBalancerConfiguration.addProvider(
                    SmsProvider.getProviderByCode(serverSms.getProvider()), serverSms.getRatio() != null ? serverSms.getRatio() : 0));
            smsLoadBalancerConfiguration.initializeDistribution();
        }
    }
}
