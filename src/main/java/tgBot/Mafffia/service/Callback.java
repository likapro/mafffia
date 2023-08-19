package tgBot.Mafffia.service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.*;

@Slf4j
//@Component
@Data
public class Callback {

//    @Autowired
//    Message message;

    public static Map<Long, Set<User>> teams = new HashMap();
    public static Map<Long, Map<String, Integer>> votes = new HashMap<>();

//    public SendMessage hasCallback(Update update) {
//        Message message = new Message();
//        User user = update.getCallbackQuery().getFrom();
//        long idChat = update.getCallbackQuery().getMessage().getChat().getId();
//
//        Map<Long, User> userChatId = Message.userChatId;
//        if(!userChatId.containsValue(user)) return message.sendMessage(idChat, String.format("@%s, надо начать личный чат с ботом", user.getUserName()));
//
//        if(teams.containsKey(idChat)) {
//            Set<User> users = teams.get(idChat);
//            if(users.contains(user)) return null;
//            users.add(user);
//        } else {
//            Set<User> users = new HashSet<>();
//            users.add(user);
//            teams.put(idChat, users);
//        }
//
//        log.info("Игрок " + user.getFirstName() + " (" + user.getUserName() + ") добавлен в игру в чате " + update.getCallbackQuery().getMessage().getChat().getTitle());
//        return message.sendMessage(idChat, String.format("+ %s", user.getFirstName()));
//    }

//    public static int sizeTeam(long chatId) {
//        Map<Long, Set<User>> teams = getTeams();
//        return teams.get(chatId).size();
//    }

//    public static Map<Long, Set<User>> getTeams() {
//        return teams;
//    }
}
