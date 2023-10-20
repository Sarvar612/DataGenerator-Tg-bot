package telegramBot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TelegramBotRunner {
    private static final ExecutorService executorService= Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private static final ThreadLocal<TelegramBotUpdateHandler> tgUpdateHandlerThreadLocal=ThreadLocal.withInitial(TelegramBotUpdateHandler::new);
    public static void main(String[] args) {
        TelegramBot tgBot=new TelegramBot("6087188203:AAHGhwrHxxhk5NF_xgo1tD4022sz6vpPiZw");
        tgBot.setUpdatesListener((updates)->{
            for(Update update:updates){
                CompletableFuture.runAsync(()-> {
                    try {
                        tgUpdateHandlerThreadLocal.get().handle(update);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                },executorService);
        }   return UpdatesListener.CONFIRMED_UPDATES_ALL;
        }, Throwable::printStackTrace);
    }
}