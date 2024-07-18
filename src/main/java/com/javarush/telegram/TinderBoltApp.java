//t.me/ep_java_tinder_bot.
//Учебный бот от javaRush
package com.javarush.telegram;

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
    private DialogMode currentMode = null; //20240716 просмотрел, что надо null указать
    private ArrayList<String> list= new ArrayList<>(); //объявляю переменную list тип ArrayList - чТО такое - узнать!!!
    private UserInfo meProfile;
    private UserInfo partnerOpener;
    private int questionCount;


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

        //20230716 Обрабатываю Чат
        //20240715 Обрабатываю вызов команды /gpt
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
        if (currentMode == DialogMode.GPT && !isMessageCommand()){ //UPD 20240718: Обязательно делаем проверку на то,
            // что введен именно текст, а не команда. Проверяем текущей режим работы и то что ввели текст, а не команду

        if (currentMode == DialogMode.GPT && !isMessageCommand()){
            //20240715 Загружаем сообщение для Чата, что бы он отвечал корректнее
            //Сообщение хранится в отдельноем файле
            // ToDo: Зачем разные функции(?) Зачем вообщение в файлах, а не в коде?
            String questionPromtForGPT = loadPrompt("gpt");

            Message msg = sendTextMessage("Подождите, пожалуйста, ChatGPT думает");  // служебное сообщение,
//                         которое потом заменим на ответ Чата

            // 20240715 Посылаем запрос в Чат и присваеваем его ответ в переменную answerGPT
            // Почему-то пахнет аинхронщиной...
            // Вставили из файла, что бы не сильно загружать код сообщениями
            // В отдельном файле мможно улучшить, удлиннить и тд
            String answerGPT = chatGPT.sendMessage(questionPromtForGPT, inputMessage);
            updateTextMessage(msg, answerGPT);
            return; // 20240715 Не забывать return!!! Иначе будут вызываться все последующие строки (команды)!!!

        }

        // Обрабатываем /date
        // Если введена команда /date, то начинаем работать
        if (inputMessage.equals("/date")){
            // устанавливаем режим /date
            currentMode = DialogMode.DATE;
            // высылаем сообщение для date
            sendPhotoMessage("date");

            // Читаю из файла сообщение для date
            String messageForDate = loadMessage("date");
            // Вывожу приветственный текст в телеграмм
            //sendTextMessage(messageForDate); // Пока все же не надо

            //sendTextMessage("Выберите девушку для общения"); // Это поменяю на кнопки.
             sendTextButtonsMessage(
                    messageForDate,
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
            if (buttonQuery.startsWith("date_") && !isMessageCommand()){
                // вывожу фото, соответсвующее нажатой кнопке (текст в переменной)
                sendPhotoMessage(buttonQuery);
                // И высылаю приветственный текст
                sendTextMessage("Отличный выбор! \nТвоя задача пригласить партнера на свидание ❤\uFE0F за 5 сообщений! ");
                // Убрали промт для Чата сюда (см. чуть ниже комменты).
                // Вызывается один раз только.
                // Все дальнейшее общение происходит ниже
                //chatGPT.setPrompt("Диалог с девушкой"); // Буду загружать отдельные подготовленные тексты

                //ВНИМАНИЕ ОПТИМИЗАЦИЯ! Опять имена  совпадают, и из=егаем лишнего кода, написав только одну переменную
                // Имя фала текста, имя фото и наименование кнопки - совпадает. т.е. используем только
                // одно наименование переменной buttonQuery
                String datePromt = loadPrompt(buttonQuery);
                chatGPT.setPrompt(datePromt); //Скармливаю подготовленную текстовку Чату
                return;
            }


            Message msg = sendTextMessage("Подождите, пожалуйста, собеседник набирает текст");  // служебное сообщение,
//                         которое потом заменим на ответ Чата

            // разбиваю сообщение Чату на 2 части. Делаем метод addMessage, что бы диалог был связанным,
            // т.е. каждое последующее сообщение дополняло предыдущее и устанавливаем отдельно promt для Чата,
            // т.е. "тему" общения
            //chatGPT.setPrompt("Диалог с девушкой"); // Промт убираем чуть выше, что бы он срабатывал 1 раз во время
            // нажатия кнопки
            String answerGpt = chatGPT.addMessage(inputMessage);
            // Ответ Чата присылаю в Телеграм пользователя
            updateTextMessage(msg, answerGpt);
            // Не забываем писать return, что бы не выполнялись дальнейшие действия
            return;
        }

        // Обрабатываем /message
        if (inputMessage.equals("/message")){
            currentMode = DialogMode.MESSAGE;
            sendPhotoMessage("message");
            //sendTextMessage("Пришлите в чат вашу переписку!"); //меняю сообщение на кнопки
            sendTextButtonsMessage("Пришлите в чат вашу переписку!",
                    "Следующее сообщение","message_next",
                    "Пригласить на свидание","message_date");
            return; // когда забываешь писать return - сразу куча мусора последующего вылазит
        }

        if (currentMode == DialogMode.MESSAGE && !isMessageCommand()){
            // делаем обработку кнопок
            String queryMessage = getCallbackQueryButtonKey();
            if (queryMessage.startsWith("message_")){ // фильтруем, что это именно кнопки ПРОВЕРЯТЬ КОРРЕКТНОСТЬ
                                                      // МЕТОДА НАЗВАНИЯ!
//                sendTextMessage(queryMessage);
                String promtMessage = loadPrompt(queryMessage); //Загружаю промт соответствующей кнопки  для Чата,
                // передам сразу имя кнопки - названия одинаковые.
                // Архитектура, однако!

                // Склеиваем все сообщения пользователя из array
                //"\n\n" разделитель
                //и список вторым параметром. Склеиваем методом join в Классе(?) String
                // TODO: Узнать, что есть String
                String userChatHistory = String.join("\n\n", list);

                Message msg = sendTextMessage("Подождите, пожалуйста, ChatGPT думает");  // служебное сообщение,
//                         которое потом заменим на ответ Чата
                // sendTextMessage(userChatHistory);
//                sendTextMessage(promtMessage);

                // Если кнопка нажата - то отправляем текстом все в Чат, иначе - складируем все в Array
                String answerMessage = chatGPT.sendMessage(promtMessage, userChatHistory);
                //sendTextMessage(answerMessage);
                updateTextMessage(msg, answerMessage); // Подменяю служебное сообщение ответом от Чата.
                // Очень удобная штука!
                return;
            }


            // Принимаем на вход переписку и отправляем её Чату. Ответ потом отправляем Юзверю

//            String answerMessage = chatGPT.sendMessage("Переписка", inputMessage);
//            sendTextMessage(answerMessage); // пока удрали эти две строчки. Будет работать с Array

            list.add(inputMessage); // Добавляю сообщение user в array
            return; //НЕ ЗАБЫВАТЬ
        }

        // обрабатываем /profile
        if (inputMessage.equals("/profile")){ // обработка команды /profile
            currentMode = DialogMode.PROFILE; // Устанавливаю текужий режим диалога для вылова второй строки
            sendPhotoMessage("profile"); // выводим фотографию

            meProfile = new UserInfo();  // обновляю переменную, что бы не сохранились старые данные
            questionCount = 1; // Обнуляю счетчик // Gjghfdbkf? htibkb c.lf gbcfnm yjvth djghjcf

            //задаю 1 вопрос
            sendTextMessage("Сколько вам лет? "); // Выводим строку с информацией. Стоит рассказать больше.
            return;
        }

        if (currentMode == DialogMode.PROFILE){  // Вылавливаю и обрабатываю второе сообщение

            switch (questionCount){ // переделываем все на switch ToDO: Зачем интересно?
                case 1:
                    //Ответ на 1й вопрос
                    meProfile.age = inputMessage;
                    questionCount = 2;
                    // задаю 2 вопрос
                    sendTextMessage("Кем вы работаете?");
                    return;
                case 2: // Цифра - номер вопроса, на который я отвеечаю
                    // Ответ на 2й вопрос и тд и тп
                    meProfile.occupation = inputMessage;
                    questionCount = 3;
                    sendTextMessage("У вас есть хобби?");
                    return;
                case 3:
                    meProfile.hobby = inputMessage;
                    questionCount = 4;
                    sendTextMessage("Что вам не нравится в людях?");
                    return;

                case 4:
                    meProfile.annoys = inputMessage;
                    questionCount = 5;
                    sendTextMessage("Что вы ждете от свидания, цель знакомства?");
                    return;
                case 5:
                    meProfile.goals = inputMessage;

                    //После того, как человек ответил на 5 вопросов - скармливаем все Чату
                    String aboutMySelfProfile = meProfile.toString(); // Преобразуем весь объект me в строку
                    // отправляю Чату  и  вывожу результат пользователю
                    // Сейчас буду писать promt из файла. Это была просто проба пера.
                    //String answerProfileGPT = chatGPT.sendMessage("Сгенерируй мне профиль от Tinder. Информация обо мне ниже: ", aboutMySelfProfile);

                    String profilePromt = loadPrompt("profile");
                    Message msg = sendTextMessage("Подождите пару секунд - ChatGPT думает..."); // Выводим сообщение,
                    // пока Чат думает. Потом его заменим на ответ. Это сделано для того, чтобы пользователь не скучал

                    String answerProfileGPT = chatGPT.sendMessage(profilePromt, aboutMySelfProfile);// Отправляю Чату
                    updateTextMessage(msg, answerProfileGPT); // Вывожу сообщение пользователю
                    // и меняю временное сообщение
                    return;
            }
            return; // Не забывать return!!!! Иначе будет выполняться все подряд.
        }


        // Обрабатываю последнюю команду Opener
        // Комментариев писать не буду, так как все будет аналогично предыдущим блокам
        // Что-то интересное - буду записывать

        if (inputMessage.equals("/opener")){
            currentMode = DialogMode.OPENER;
            sendPhotoMessage("opener");

            partnerOpener = new UserInfo();
            questionCount = 1;
            sendTextMessage("Имя девушки:");
            return;
        }

        if (currentMode == DialogMode.OPENER  && !isMessageCommand()){
            // String aboutFriendOpener = inputMessage; // Пока уберу

            switch (questionCount) { // переделываем все на switch ToDO: Зачем интересно?
                case 1:
                    //Ответ на 1й вопрос
                    partnerOpener.name = inputMessage;
                    questionCount = 2;
                    // задаю 2 вопрос
                    sendTextMessage("Сколько ей/ему  лет");
                    return;
                case 2: // Цифра - номер вопроса, на который я отвеечаю
                    // Ответ на 2й вопрос и тд и тп
                    partnerOpener.age = inputMessage;
                    questionCount = 3;
                    sendTextMessage("У него/неё есть хобби?");
                    return;
                case 3:
                    partnerOpener.hobby = inputMessage;
                    questionCount = 4;
                    sendTextMessage("Кем работает?");
                    return;

                case 4:
                    partnerOpener.occupation = inputMessage;
                    questionCount = 5;
                    sendTextMessage("Что вы ждете от свидания, цель знакомства?");
                    return;
                case 5:
                    partnerOpener.goals = inputMessage;

                    String aboutPartnerOpener = partnerOpener.toString(); // Преобразуем весь объект me в строку
                    // отправляю Чату  и  вывожу результат пользователю
                    // Сейчас буду писать promt из файла. Это была просто проба пера.
                    //String answerProfileGPT = chatGPT.sendMessage("Сгенерируй мне профиль от Tinder. Информация обо мне ниже: ", aboutMySelfProfile);

                    //TODO: Этот код повторяется много раз. Нужно его вынести в отдельный модуль.String profileOpener = loadPrompt("opener");
                    Message msg = sendTextMessage("Подождите пару секунд - ChatGPT думает..."); // Выводим сообщение,
                    // пока Чат думает. Потом его заменим на ответ. Это сделано для того, чтобы пользователь не скучал

                    String profileOpener = loadPrompt("opener");
                    String answerProfileGPT = chatGPT.sendMessage(profileOpener, aboutPartnerOpener);// Отправляю Чату
                    updateTextMessage(msg, answerProfileGPT); // Вывожу сообщение пользователю
                    // и меняю временное сообщение
                    return;
            }
        }

        sendTextMessage("*Привет*"); // Делаю текст жирным в телеграмме
        sendTextMessage("Если ты видишь этот спам - то значит ты забыл написать return в одном из обработчиков!!!");  // Делаю текст наклонным в телеграмме
    }

    public static void main(String[] args) throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(new TinderBoltApp());
    }
}
