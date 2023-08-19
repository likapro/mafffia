package tgBot.Mafffia.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import tgBot.Mafffia.Entity.Roles;

import java.util.*;

@Component
@Slf4j
//@Data
public class Message {

    public static Map<Long, User> userChatId = new HashMap<>();

//    public SendMessage hasMessage(Update update) {
//        String message = update.getMessage().getText().toLowerCase();
//        long chatId = update.getMessage().getChatId();
//        User user = update.getMessage().getFrom();
//        String name = update.getMessage().getChat().getFirstName();
//        String chatType = update.getMessage().getChat().getType();
//
//        log.info(chatId + ", " + name + ", " + message);
//        switch (message) {
//            case "/start":
//                if(chatType.equals("private")) {
//                    userChatId.put(chatId, user);
//                    return sendMessage(chatId, "Привет! Теперь можешь играть!");
//                }
//
//                return group(chatId);
//
//            case "/game_run":
//                return gameRun(chatId);
//        }
//
//        return null;
//    }

    public SendMessage inGroup(long chatId){
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Набираем команду");

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        inlineKeyboardButton1.setText("+");
        inlineKeyboardButton1.setCallbackData("1");

        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline1 = new ArrayList<>();
        rowInline1.add(inlineKeyboardButton1);
        rowsInline.add(rowInline1);

        markupInline.setKeyboard(rowsInline);
        message.setReplyMarkup(markupInline);

        return message;
    }

    public SendMessage voting(Map<Roles, List<User>> roles){
        SendMessage message = new SendMessage();
        // message.setChatId(chatId);
        message.setText("Голосуем");

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        for (Map.Entry<Roles, List<User>> rolesListEntry : roles.entrySet()) {
            for (User user : rolesListEntry.getValue()) {

                InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
                inlineKeyboardButton.setText(user.getFirstName());
                inlineKeyboardButton.setCallbackData(user.getFirstName());

                List<InlineKeyboardButton> rowInline = new ArrayList<>();
                rowInline.add(inlineKeyboardButton);
                rowsInline.add(rowInline);
            }
        }

        markupInline.setKeyboard(rowsInline);

        message.setReplyMarkup(markupInline);

        return message;
    }



//    public SendMessage sendMessage(long chatId, String textToSend) {
//        SendMessage message = new SendMessage();
//        message.setChatId(chatId);
//        message.setText(textToSend);
//
//        return message;
//    }


//    public SendMessage gameRun(long chatId) {
//
//        Map<Long, Set<User>> teams = Callback.getTeams();
//        int size = teams.get(chatId).size();
//        if(teams.containsKey(chatId) && size > 0) {
//            // Раздаем роли
//
//            // Рассылаем роли в личные сообщения
//            // Set<User> users = teams.get(chatId);
//            for(Long userChatId : userChatId.keySet()) {
//                SendMessage message = new SendMessage(String.valueOf(userChatId), "Ты мафия");
//                bot.executeMessage(message);
//            }
//
//            // оповещаем в чате, что игра началась
//            return sendMessage(chatId, String.format("Пока не начинаем. Но команда собрана. Участников: %d", size));
//        }
//
//        return sendMessage(chatId, String.format("Команда не набрана. Участников: %d", size));
//
//    }
}