package com.qiqilm.server.admin.controller;

import com.qiqilm.server.admin.service.ILiveVideoService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequestMapping ( "/test" )
public class TestController {
    @Autowired
    private ILiveVideoService liveVideoService;

    @PostMapping ( "/countHostGift" )
    public void test() {
        liveVideoService.countHostGift();
    }
}
