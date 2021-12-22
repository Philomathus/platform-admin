package com.qiqilm.server.admin.utils;


import com.qiqilm.server.admin.cache.SysConfigCacheUtil;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
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
		return "yanQin";
	}

	@Override
	public String getBotToken() {
		String robot_message_token = sysConfigCacheUtil.getConf("robot_message_token");
		// 填写token
		return robot_message_token;
	}

	@Override
	public void onUpdateReceived( Update update ) {
		if ( update.hasMessage() && update.getMessage().hasText() ) {
			SendMessage message = new SendMessage()
					.setChatId( update.getMessage().getChatId() )
					.setText( "Robot回复的内容" );
			System.out.println("Robot回复的内容");
			try {
				execute( message );
			} catch ( TelegramApiException e ) {
				e.printStackTrace();
			}
		}
	}

	public static void main( String[] args ) throws TelegramApiException {
		// 初始化Api上下文
		ApiContextInitializer.init();
		// 实例化Telegram Bots API
		TelegramBotsApi botsApi = new TelegramBotsApi();
		try {
			// 注册我们的机器人.
			botsApi.registerBot(new RobotMessage());
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}

	}

	public void send(String tex){
		String withdraw_log_telegram = sysConfigCacheUtil.getConf( "withdraw_log_telegram" );
		if (Strings.isBlank(withdraw_log_telegram)){
			return;
		}
		log.info("彩票纸飞机id:"+withdraw_log_telegram,"纸飞机Token:"+sysConfigCacheUtil.getConf("robot_message_token"));

		SendMessage message = new SendMessage()
				.setChatId( withdraw_log_telegram )
				.setText( tex);
		try {
			execute( message );
		} catch ( TelegramApiException e ) {
			e.printStackTrace();
		}
	}

	public void sendByChatId(String tex,String chatId){

		SendMessage message = new SendMessage()
				.setChatId( chatId )
				.setText( tex);
		try {
			execute( message );
		} catch ( TelegramApiException e ) {
			e.printStackTrace();
		}
	}
}