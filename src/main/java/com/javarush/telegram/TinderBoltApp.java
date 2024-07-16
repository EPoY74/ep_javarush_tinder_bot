//t.me/ep_java_tinder_bot.
//Учебный бот от javaRush
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

    //TODO: добавь токен бота в кавычках
    public static final String TELEGRAM_BOT_TOKEN = "7431471262:AAEb-1mt5_xPxnDk5MZ4IhwyvqJ09E_b3ww";

    //TODO: добавь токен ChatGPT в кавычках
    public static final String OPEN_AI_TOKEN = "gpt:4dws6NYyD0BDK2ufp71ZJFkblB3TCC3tppbmX6OYmhSFydbM";

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
            //20240715 Устанавливаем режим работы
            currentMode = DialogMode.MAIN; // Основной режим работы
            // 20240714Высылаем фотосообщение
            sendPhotoMessage("main");
            // 20240714Читаем файл с командами и выводим его пользователю
            String greetingText = loadMessage("main");
            sendTextMessage(greetingText);
            // Выводим меню слева от поля ввода текста.
            // ToDo: А как быть, если зашли не через /start?
            showMainMenu(
                    "Начало работы", "/start",
                    "Генерация Tinder-профля \uD83D\uDE0E ", "/profile",
                    "Cообщение для знакомства \uD83E\uDD70", "/opener",
                    "Переписка от вашего имени \uD83D\uDE08", "/message",
                    "Переписка со звездами \uD83D\uDD25", "/date",
                    "Общение с ChatGPT \uD83E\uDDE0", "/gpt");
            return; //20240714 После этой команды ничего не выполняется дальше, так как идет
            // выход? к началу класса? onUpdateEventReceived
        }


        //20240715 Обрабатывваю вызов команды /gpt
        if(inputMessage.equals("/gpt")){
            //20240715 Меняю режим на ражим работвы с чатом GPT
            currentMode = DialogMode.GPT; //20240715 Режим работы с Чатом
            //20240715 Высылаем фотосообщение
            sendPhotoMessage("gpt");

            //20240715 Лучше текст в отдельном файле, так как уод менять не нужно, код не сломается.
            //На самом деле - у этом есть что-то ... Что бы лишний раз не лезть в файлы
            //Можно в файл вставлять не только просто текст, но и смайлики и выделения текста
            //Текстовые данные рекомендуется выносить отдельно, что бы не мешать читать Код
            String messageForGPT = loadMessage("gpt");
            sendTextMessage(messageForGPT);
            return;
            //20240715 выход? к началу класса? onUpdateEventReceived
        }

        //20240715 Проверяем, если мы вошли в режим Чата, то посылаю последующие сообщения в Чат
        if (currentMode == DialogMode.GPT){
            //20240715 Загружаем сообщение для Чата, что бы он отвечал корректнее
            //Сообщение хранится в отдельноем файле
            // ToDo: Зачем разные функции(?) Зачем вообщение в файлах, а не в коде?
            String questionPromtForGPT = loadPrompt("gpt");
            // 20240715 Посылаем запрос в Чат и присваеваем его ответ в переменную answerGPT
            // Почему-то пахнет аинхронщиной...
            // Вставили из файла, что бы не сильно загружать код сообщениями
            // В отдельном файле мможно улучшить, удлиннить и тд
            String answerGPT = chatGPT.sendMessage(questionPromtForGPT, inputMessage);
            sendTextMessage(answerGPT);
            return; // 20240715 Не забывать return!!! Иначе будут вызываться все последующие строки (команды)!!!

        }


        // Если введена команда /date то начинаем работать
        if (inputMessage.equals("/date")){
            // устанавливаем режим /date
            currentMode = DialogMode.DATE;
            // высылаем сообщение для date
            sendPhotoMessage("date");
            // Читаю из файла сообщение для date
            String messageForDate = loadMessage("date");
            // Вывожу приветственный текст в консоль
            //sendTextMessage(messageForDate); // Это пока не надо

            //sendTextMessage("Выберите девушку для общения"); // Это поменяю на кнопки.
             sendTextButtonsMessage(
                    "Выберите девушку, которую хотите пригласить на свидание",
                    "Ариана Гранде", "date_grande",
                     "Марго Робби", "date_robbie",
                     "Зендея", "date_zendaya",
                     "Райан Гослинг", "date_gosling",
                     "Том Харди", "date_hardy"
            );
            return;
        }


        if (currentMode == DialogMode.DATE){
            // Обработчик для нажатых кнопок
            // Считываю какая кнопка нажата
            String buttonQuery = getCallbackQueryButtonKey();
            //Обрабатываем сами нажатия.

//            if (buttonQuery.equals("date_grande")){ // Название date_grande повторяется 2 раза.
//                                                    // Заменю его на buttonQuery в слудующих обработчиках, тут
//                                                    //  оставлю для примера как есть
//                // вывожу в=фотографию в чат
//                sendPhotoMessage("date_grande");
//                return; // возврат
//            }
//            if (buttonQuery.equals("date_robbie")){
//                // вывожу в=фотографию в чат
//                sendPhotoMessage(buttonQuery);
//                return; // возврат
//            }
//            if (buttonQuery.equals("date_zendaya")){
//                // вывожу в=фотографию в чат
//                sendPhotoMessage(buttonQuery);
//                return; // возврат
//            }
//            if (buttonQuery.equals("date_gosling")){
//                // вывожу в=фотографию в чат
//                sendPhotoMessage(buttonQuery);
//                return; // возврат
//            }
//            if (buttonQuery.equals("date_hardy")){
//                // вывожу в=фотографию в чат
//                sendPhotoMessage(buttonQuery);
//                return; // возврат
//            }

            // Делаем чуть иначе без кучи пустого кода.
            // Отличный пример оптимизации. Мой код - как я думал, чуть выше.

            // Проверяю, действительно ли нажата кнопка, так как текст довольно индивидуальный у кнопок
            if (buttonQuery.startsWith("date_")){
                // вывожу фото, соответсвующее нажатой кнопке (текст в переменной)
                sendPhotoMessage(buttonQuery);
                // И высылаю приветственный текст
                sendTextMessage("Отличный выбор! \nТвоя задача пригласить партнера на свидание ❤\uFE0F за 5 сообщений! ");
                // Убрали промт для Чата сюда (см. чуть ниже комменты).
                // Вызывается один раз только.
                // Все дальнейшее общение происходит ниже
                //chatGPT.setPrompt("Диалог с девушкой"); // Буду загружать отдельные подготовленные тексты

                //ВНИМАНИЕ ОПТИМИЗАЦИЯ! Опять имена  совпадают, и из=егаем лишнего кода, написав только одну переменную
                // Имя фала текста, имя фото и наименование кнопки - совпадает. т.е. еспользуем только
                // одно наименование переменной buttonQuery
                String datePromt = loadPrompt(buttonQuery);
                chatGPT.setPrompt(datePromt); //Скармливаю подготовленную текстовку Чату
                return;
            }


            // разбиваю сообщение Чату на 2 части. Делаем метод addMessage, что бы диалог был связанным,
            // т.е. каждое последующее сообщение дополняло предыдущее и устанавличаем отдельно promt для Чата,
            // т.е. "тему" общения
            //chatGPT.setPrompt("Диалог с девушкой"); // Промт убираем чуть выше, что бы он срабатывал 1 раз во время
            // нажатия кнопки
            String answerGpt = chatGPT.addMessage(inputMessage);
            // Ответ Чата присылаю в Телеграм пользователя
            sendTextMessage(answerGpt);
            // Не забываем писать return, что бы не выполнялись дальнейшие действия
            return;
        }

        String buttonPressed = getCallbackQueryButtonKey();

        if (buttonPressed.equals("noButton")){
            sendTextMessage("Вы нажали кнопку Нет");
            return;
        }
        sendTextMessage("*Привет*"); // Делаю текст жирным в телеграмме
        sendTextMessage("_Привет_");  // Делаю текст наклонным в телеграмме
        // И выводим его на экран
        // sendTextMessage("Это вы написали такое: " + inputMessage + "?"); //Пока не нужно
        //Тоже вывели еще одно сообщение, для тренировки
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
