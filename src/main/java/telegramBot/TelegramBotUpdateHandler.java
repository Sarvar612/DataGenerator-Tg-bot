package telegramBot;


import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.Keyboard;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.SendDocument;
import com.pengrad.telegrambot.request.SendMessage;
import seeder.AppForGeneratingDatas;
import seeder.FieldType;
import seeder.Pairs;
import seeder.Request;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class TelegramBotUpdateHandler {
    public static List<Pairs> pairs = new ArrayList<>();

    private static final ConcurrentHashMap<Long,State>  userState=new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Long, Request>  generateDataRequest=new ConcurrentHashMap<>();

    private static final TelegramBot bot=new TelegramBot("6087188203:AAHGhwrHxxhk5NF_xgo1tD4022sz6vpPiZw");
    public void handle(Update update) throws IOException {
        var message = update.message();
        var callbackQuery = update.callbackQuery();
        if(message!=null){
            String text = message.text();
            var chat=message.chat();
            var chatId=chat.id();
            if(text.equals("/start")){
                var sendMessage=new SendMessage(chatId,"Welcome to our Fake Data Generator!\n To generate fake datas please enter /generate command");
                bot.execute(sendMessage);
            } else if (text.equals("/generate")) {
                var sendMessage=new SendMessage(chatId,"Send File Name:");
                bot.execute(sendMessage);
                userState.put(chatId,State.FILE_NAME);
                generateDataRequest.put(chatId,new Request());
            } else if ( State.FILE_NAME.equals(userState.get(chatId)) ) {
                var sendMessage = new SendMessage(chatId, "SendType of file: \njson ; sql ; csv");
                bot.execute(sendMessage);
                userState.put(chatId, State.GENERATETYPE);
                generateDataRequest.get(chatId).setFileName(text.toLowerCase());
            }else if ( State.GENERATETYPE.equals(userState.get(chatId)) ) {
                var sendMessage = new SendMessage(chatId, "Send row count:");
                bot.execute(sendMessage);
                userState.put(chatId, State.ROW_COUNT);
                generateDataRequest.get(chatId).setType(text);
            }
            else if ( State.ROW_COUNT.equals(userState.get(chatId)) ) {
                var sendMessage = new SendMessage(chatId, "Choose Fields");
                sendMessage.replyMarkup(getInlineMarkupKeyboard());
                bot.execute(sendMessage);
                userState.put(chatId, State.FIELDS);
                generateDataRequest.get(chatId).setCount(Integer.valueOf(text));
            } else {
                var deleteMessage = new DeleteMessage(chatId, message.messageId());
                bot.execute(deleteMessage);
            }
        }else {
            FieldType[] fieldTypes = FieldType.values();
            String data = callbackQuery.data();
            Chat chat = callbackQuery.message().chat();
            Long chatId = chat.id();
            if(data.equals("g")){
                try {
                    AppForGeneratingDatas appForGeneratingDatas=new AppForGeneratingDatas();
                    generateDataRequest.get(chatId).setPairs(pairs);
                    appForGeneratingDatas.proccessRequest(generateDataRequest.get(chatId),chatId);
                    SendDocument sendDocument = new SendDocument(chatId, appForGeneratingDatas.getImportPath().toFile());
                    bot.execute(sendDocument);
                    Files.delete(appForGeneratingDatas.getImportPath());
                    pairs.clear();
                    userState.clear();
                    generateDataRequest.clear();
                }catch (IOException ignored){

                }
            }else{
                FieldType fieldType = fieldTypes[Integer.parseInt(data)];
                pairs.add(new Pairs(fieldType));
                generateDataRequest.get(chatId).getPairs().add(new Pairs(fieldType));
            }
        }
    }

    private Keyboard getInlineMarkupKeyboard() {
        FieldType[] fieldTypes = FieldType.values();
        InlineKeyboardButton[][] buttons = new InlineKeyboardButton[9][2];
        for ( int i = 0; i < fieldTypes.length / 2; i++ ) {
            InlineKeyboardButton button1 = new InlineKeyboardButton(fieldTypes[i * 2].name());
            InlineKeyboardButton button2 = new InlineKeyboardButton(fieldTypes[i * 2 + 1].name());
            button1.callbackData("" + i * 2);
            button2.callbackData("" + ( i * 2 + 1 ));
            buttons[i][0] = button1;
            buttons[i][1] = button2;
        }
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup(buttons);
        InlineKeyboardButton generate = new InlineKeyboardButton("✅ Generate");
        generate.callbackData("g");
        inlineKeyboardMarkup.addRow(generate);
        return inlineKeyboardMarkup;
    }
}

enum State {
    FILE_NAME,
    ROW_COUNT,
    FIELDS,
    GENERATETYPE
}
