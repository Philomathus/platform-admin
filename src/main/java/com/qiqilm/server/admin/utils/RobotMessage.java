package com.qiqilm.server.admin.utils;


import org.springframework.stereotype.Component;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class RobotMessage extends TelegramLongPollingBot {

	@Override
	public String getBotUsername() {
		// 填写username
		return "chengziBot";
	}

	@Override
	public String getBotToken() {
		// 填写token
		return "1647949061:AAG0bkLgSZNTnL8KI3EQcMIwx79D-Jd6dVk";
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

		SendMessage message = new SendMessage()
				.setChatId( "-434671494" )
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