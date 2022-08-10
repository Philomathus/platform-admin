package com.qiqilm.server.admin.config;

import com.qiqilm.server.admin.utils.SpringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
public class LiveCenterConfig {
    public static LiveCenterConfig me;

    @PostConstruct
    void init() {
        me = this;
    }

    @Value("${liveCenter}")
    private String liveCenter;

    @Value("${spring.profiles.active}")
    private String profile;

    public boolean isLiveCenter() {
        return profile.equals(liveCenter);
    }

    public String getLiveCenterDbMain() {
        if (liveCenter.equals("7701")) {
            return "db_cx001";
        }
        return liveCenter + "_main";
    }

    public String getLiveCenterDbLive() {
        if (liveCenter.equals("7701")) {
            return "cx_live";
        }
        return liveCenter + "_live";
    }

    public String getLiveCenterDbLottery() {
        return liveCenter + "_lottery";
    }

    public String getProfileDbMain() {
        if (profile.equals("7701")) {
            return "db_cx001";
        }
        return profile + "_main";
    }

    public String getProfileDbLive() {
        if (profile.equals("7701")) {
            return "cx_live";
        }
        return profile + "_live";
    }

    public String getProfileDbLottery() {
        return profile + "_lottery";
    }

    public String[] getLiveSubAgents() {
        String propertiesValue = SpringUtils.getPropertiesValue("live.share." + this.liveCenter);
        return StringUtils.hasText(propertiesValue) ? propertiesValue.split(",") : null;
    }

    public String getLiveSubAgentDbLive(String agent) {
        return agent + "_live";
    }

    public String getLiveSubAgentDbMain(String agent) {
        return agent + "_main";
    }

    public String getLiveSubAgentDbLottery(String agent) {
        return agent + "_lottery";
    }
}
