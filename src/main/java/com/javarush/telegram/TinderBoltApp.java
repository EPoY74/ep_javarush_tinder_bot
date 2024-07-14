package com.javarush.telegram;

import com.javarush.telegram.ChatGPTService;
import com.javarush.telegram.DialogMode;
import com.javarush.telegram.MultiSessionTelegramBot;
import com.javarush.telegram.UserInfo;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.ArrayList;

public class TinderBoltApp extends MultiSessionTelegramBot {
    public static final String TELEGRAM_BOT_NAME = "ep_java_tinder_bot"; //TODO: добавь имя бота в кавычках
    public static final String TELEGRAM_BOT_TOKEN = "7431471262:AAEb-1mt5_xPxnDk5MZ4IhwyvqJ09E_b3ww"; //TODO: добавь токен бота в кавычках
    public static final String OPEN_AI_TOKEN = "chat-gpt-token"; //TODO: добавь токен ChatGPT в кавычках

    public TinderBoltApp() {
        super(TELEGRAM_BOT_NAME, TELEGRAM_BOT_TOKEN);
    }

    @Override
    public void onUpdateEventReceived(Update update) {
        //TODO: основной функционал бота будем писать здесь
        // Выводим сообщение от бота

        // Принимаем на вход сообщение, которое пишет пользователь
        String inputMessage = getMessageText();
        if(inputMessage.equals("/start")) {
            // Высылаем фотосообщение
            sendPhotoMessage("main");
            // Читаем файл с командами и выводим его пользователю
            String greetingText = loadMessage("main");
            sendTextMessage(greetingText);
            return; // После этой команды ничего не  ъвыполняетс дальше, так как идет
            // выход? к началу класса? onUpdateEventReceived
        }

        String buttonPressed = getCallbackQueryButtonKey();
        if (buttonPressed.equals("noButton")){
            sendTextMessage("Вы нажали кнопку Нет");
            return;
        }

        sendTextMessage("*Привет*"); // Делаю текст жирным в телеграмме
        sendTextMessage("_Привет_");  // Делаю текст наклонным в телеграмме



        // И выводим его на жкран
        // sendTextMessage("Это вы написали такое: " + inputMessage + "?"); //Пока не нужно
        //Тоже выывели еще одно сообщение, для тренировки
        sendTextMessage("Что нового?");

        // выводим сообщение с кнопками
        sendTextButtonsMessage(
                "У вас все хорошо?!",
                "Да!!!", "yesButton",
                "Нет!! :((" , "noButton"

        );




    }

    public static void main(String[] args) throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(new TinderBoltApp());
    }
}
