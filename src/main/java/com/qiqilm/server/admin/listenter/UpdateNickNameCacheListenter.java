package com.qiqilm.server.admin.listenter;

import com.qiqilm.server.admin.task.UpdateNickNameTask;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Log4j2
//@Component
public class UpdateNickNameCacheListenter implements ApplicationListener<ApplicationReadyEvent> {
    @Autowired
    private UpdateNickNameTask updateNickNameTask;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        try {
            updateNickNameTask.updateNickNameCache();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
