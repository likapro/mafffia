package tgBot.Mafffia.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import tgBot.Mafffia.config.BotConfig;

@Slf4j
//@Component
public class Handler {

    @Autowired
    BotConfig botConfig;
//    @Autowired
//    Message message;
//    @Autowired
//    Callback callback;

//    public SendMessage handlerUpdate(Update update) {
//        Message message = new Message();
//        Callback callback = new Callback();
//        if (update.hasMessage() && update.getMessage().hasText()) {
//            return message.hasMessage(update);
//        } else if (update.getCallbackQuery().getData().equals("1")) {
//            return callback.hasCallback(update);
//        }
//
//        return null;
//    }


//    private SendMessage sendMessage(long chatId, String textToSend) {
//        SendMessage message = new SendMessage();
//        message.setChatId(chatId);
//        message.setText(textToSend);
//
//        return message;
//    }
}
