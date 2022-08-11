package com.qiqilm.server.admin.controller;

import com.qiqilm.server.admin.service.ILiveVideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping( "/test" )
public class ATestController {
    @Autowired
    private ILiveVideoService liveVideoService;

    @GetMapping()
    public void list() {
        liveVideoService.countHostGift();
    }
}
