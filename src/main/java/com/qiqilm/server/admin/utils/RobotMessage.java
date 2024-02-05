package com.qiqilm.server.admin.utils;

import com.qiqilm.server.admin.cache.SysConfigCacheUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@Log4j2
public class RobotMessage extends TelegramLongPollingBot {
    @Autowired
    private SysConfigCacheUtil sysConfigCacheUtil;

    @Override
    public String getBotUsername() {
        // 填写username
        return sysConfigCacheUtil.getConf( "bot_username_telegram", "yanQin" );
    }

    @Override
    public String getBotToken() {
        // 填写token
        return sysConfigCacheUtil.getConf( "robot_message_token" );
    }

    @Override
    public void onUpdateReceived( Update update ) {
        if ( update.hasMessage() && update.getMessage().hasText() ) {
            SendMessage message = new SendMessage(); // Create a SendMessage object with mandatory fields
            message.setChatId( update.getMessage().getChatId().toString() );
            message.setText( "Robot回复的内容" );
            try {
                execute( message );
            } catch ( TelegramApiException e ) {
                log.error( e.getMessage(), e );
            }
        }
    }

    public void sendByChatId( String tex, String chatId ) {
        if ( StringUtils.isBlank( chatId ) ) {
            return;
        }

        log.info( "纸飞机3id" + chatId + "內容+" + tex );
        SendMessage message = new SendMessage();
        message.setChatId( chatId );
        message.setText( tex );
        try {
            execute( message );
        } catch ( TelegramApiException e ) {
            log.error( e.getMessage() );
        }
    }
}