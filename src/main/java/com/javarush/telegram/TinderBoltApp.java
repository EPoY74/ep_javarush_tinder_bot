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
    public static final String OPEN_AI_TOKEN = "gpt:4dws6NYyD0BDK2ufp71ZJFkblB3TCC3tppbmX6OYmhSFydbM"; //TODO: добавь токен ChatGPT в кавычках

    // Создаю переменную для ощения с Чатом
    private  ChatGPTService chatGPT = new ChatGPTService(OPEN_AI_TOKEN);
    //Создаю переменную для Dialog mode. Она хранит текущий режим диалога. Тип enum.
    // Когда будет вызЫватьсЯ команда для смены режима, то вызаваем данную переменную
    // И меняем режимы
    private DialogMode currentMode;


    public TinderBoltApp() {
        super(TELEGRAM_BOT_NAME, TELEGRAM_BOT_TOKEN);
    }

    @Override
    public void onUpdateEventReceived(Update update) {
        //TODO: основной функционал бота будем писать здесь
        // Выводим сообщение от бота

        // 20240714Принимаем на вход сообщение, которое пишет пользователь
        // 20240715Любое сообщение считается. Любые символы, которые пользователь пишет в телеграм
        // и потом нажимает ввод
        String inputMessage = getMessageText();

        // 20240714Обрабатываю вызов команды /start
        if(inputMessage.equals("/start")) {
            //20240715 Устанавляваем режим работы
            currentMode = DialogMode.MAIN; // Основной режим работы
            // 20240714Высылаем фотосообщение
            sendPhotoMessage("main");
            // 20240714Читаем файл с командами и выводим его пользователю
            String greetingText = loadMessage("main");
            sendTextMessage(greetingText);
            return; //20240714 После этой команды ничего не  ъвыполняетс дальше, так как идет
            // выход? к началу класса? onUpdateEventReceived
        }


        //20240715 Обрабатывваю вызов команды /gpt
        if(inputMessage.equals("/gpt")){
            //20240715 Меняю режим на ражим работвы с чатом GPT
            currentMode = DialogMode.GPT; //20240715 Режим работы с Чатом
            //20240715 Высылаем фотосообщение
            sendPhotoMessage("gpt");
            sendTextMessage("Ваше сообщение для *ChatGPT*");
            return;
            //20240715 выход? к началу класса? onUpdateEventReceived
        }

        //20240715 Проверяем, если мы вошли в режим Чата, то посылаю последующие сообщения в Чат
        if (currentMode == DialogMode.GPT){
            // 20240715 Посылаем запрос в Чат и присваеваем его ответ в переменную answerGPT
            // Почему-то пахнет аинхронщиной...
            String answerGPT = chatGPT.sendMessage("Ответь на вопрос: ", inputMessage);
            sendTextMessage(answerGPT);
            return; // 20240715 Не забывать return!!! Иначе будут вызываться все последующие строки (команды)!!!

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
