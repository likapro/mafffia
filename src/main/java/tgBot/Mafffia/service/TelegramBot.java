package tgBot.Mafffia.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import tgBot.Mafffia.Entity.Roles;
import tgBot.Mafffia.config.BotConfig;

import java.util.*;

@Slf4j
@Component
//@Data
public class TelegramBot extends TelegramLongPollingBot {

    @Autowired
    BotConfig botConfig;
    @Autowired
    Message message;

    @Override
    public void onUpdateReceived(Update update) {
        handlerUpdate(update);

    }

    public void handlerUpdate(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            hasMessage(update);
        } else if (update.getCallbackQuery().getData().equals("1")) {
            hasCallback(update);
        } else {
            votingInChat(update);
        }
    }

    private void votingInChat(Update update) {
        long chatId = update.getCallbackQuery().getMessage().getChat().getId();
        if (Callback.votes.containsKey(chatId)) {

            Map<String, Integer> votes = Callback.votes.get(chatId);
            User user = update.getCallbackQuery().getFrom();
            String data = update.getCallbackQuery().getData();
            log.info(user.getFirstName() + " проголосовал за " + data);
            if(votes.containsKey(data)) {
                Integer votesForPerson= votes.get(data);
                votes.put(data, ++votesForPerson);
            } else {
                votes.put(data, 1);
            }

        } else {

        }

    }

    public void hasMessage(Update update) {
        String message1 = update.getMessage().getText().toLowerCase();
        long chatId = update.getMessage().getChatId();
//        User user = update.getMessage().getFrom();
        String name = update.getMessage().getChat().getFirstName();
//        String chatType = update.getMessage().getChat().getType();

        log.info(chatId + ", " + name + ", " + message1);
        switch (message1) {
            case "/start":
//                if(chatType.equals("private")) {
//                    Message.userChatId.put(chatId, user);
//                    sendMessage(chatId, "Привет! Теперь можешь играть!");
//                } else {
                    executeMessage(message.inGroup(chatId));
//                }
                break;
            case "/run":
                try {
                    gameRun(chatId);
                } catch (InterruptedException e) {

                }
                break;
            case "/voting":
                votingResults(chatId);
                break;
        }
    }

    public void hasCallback(Update update) {
        User user = update.getCallbackQuery().getFrom();
        long idChat = update.getCallbackQuery().getMessage().getChat().getId();
//        Map<Long, User> userChatId = Message.userChatId;
//        if(!userChatId.containsValue(user)) {
//            sendMessage(idChat, String.format("@%s, надо начать личный чат с ботом", user.getUserName()));
//            return;
//        } else
        if(Callback.teams.containsKey(idChat)) {
            Set<User> users = Callback.teams.get(idChat);
            if(!users.contains(user)) users.add(user);
        } else {
            Set<User> users = new HashSet<>();
            users.add(user);
            Callback.teams.put(idChat, users);
        }

        log.info("Игрок " + user.getFirstName() + " (" + user.getUserName() + ") добавлен в игру в чате " + update.getCallbackQuery().getMessage().getChat().getTitle());
        sendMessage(idChat, String.format("+ %s", user.getFirstName()));
    }

    public void sendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(textToSend);

        executeMessage(message);
    }

    public void executeMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void gameRun(long chatId) throws InterruptedException {

        Map<Long, Set<User>> teams = Callback.teams;
        Callback.votes.put(chatId, new HashMap<>());
        int size = teams.get(chatId).size();
        if(teams.containsKey(chatId) && size > 0) {

            Set<User> users = teams.get(chatId);
            Iterator<User> iter = users.iterator();
            Map<Roles, List<User>> personAndRole = new HashMap();
            List<User> usersMafia = new ArrayList<>();
            List<User> usersDoctor = new ArrayList<>();
            List<User> usersPeople = new ArrayList<>();
            personAndRole.put(Roles.MAFIA, usersMafia);
            personAndRole.put(Roles.DOCTOR, usersDoctor);
            personAndRole.put(Roles.PERSON, usersPeople);
            // Раздаем роли
            for(int i = 0; iter.hasNext(); i++) {
                User user = iter.next();
                if (i == 0) {
                    //String roleGet = Roles.MAFIA.getName();
                    //Role role = new Role(user, roleGet);
                    usersMafia.add(user);
                    log.info(user.getFirstName() + " " + Roles.MAFIA.getName());
                }
                else if (i == 1) {
                    usersDoctor.add(user);
                    log.info(user.getFirstName() + " " + Roles.DOCTOR.getName());
                }
                else {
                    usersPeople.add(user);
                    log.info(user.getFirstName() + " " + Roles.PERSON.getName());
                }
            }

            // Рассылаем роли в личные сообщения
            for(Map.Entry<Roles, List<User>> rolesUserEntry : personAndRole.entrySet()) {
                for(User user : rolesUserEntry.getValue()) {
                    sendMessage(user.getId(), "Ты " + rolesUserEntry.getKey().getName());
                }
            }

            // оповещаем в чате, что игра началась
            sendMessage(chatId, String.format("Пока не начинаем. Но команда собрана. Участников: %d", size));
            Thread.sleep(2000);

            voting(chatId, personAndRole);

            //Thread.sleep(15000);

        } else {
            sendMessage(chatId, String.format("Команда не набрана. Участников: %d", size));
        }


    }

    private void votingResults(long chatId) {
        Map<String, Integer> votes = Callback.votes.get(chatId);
        String sv = "Итоги голосования:\n";
        List<String> killPerson = new ArrayList<>();
        int maxVote = 0;
        int[] countList = new int[votes.size()];
        int i = 0;
        for (Map.Entry<String, Integer> stringIntegerEntry : votes.entrySet()) {
            countList[i] = stringIntegerEntry.getValue();
            i++;
        }
        Arrays.sort(countList);
        if (countList.length == 0) {
            sendMessage(chatId, "Голосование не состоялось");
            sendMessage(chatId, currentTeam(chatId));
        } else if (countList.length == 1) {
            for (Map.Entry<String, Integer> stringIntegerEntry : votes.entrySet()) {
                sendMessage(chatId, "Убит " + stringIntegerEntry.getKey());
                deletePersonFromTeam(chatId, stringIntegerEntry.getKey());
                sendMessage(chatId, currentTeam(chatId));
            }
        } else {

            for(int j = countList.length-2; j >= 0; j-- ) {
                if(countList[j] == countList[j+1]) {
                    sendMessage(chatId, "Мнения разделились");
                    sendMessage(chatId, currentTeam(chatId));
                    break;
                } else {
                    maxVote = Math.max(maxVote, countList[j+1]);
                    if(maxVote > 0) {
                        for (Map.Entry<String, Integer> stringIntegerEntry : votes.entrySet()) {
                            if (stringIntegerEntry.getValue() == maxVote) {
                                sendMessage(chatId, "Убит " + stringIntegerEntry.getKey());
                            }
                        }
                    }

                    sendMessage(chatId, currentTeam(chatId));
                }
            }
        }

        for (Map.Entry<String, Integer> stringIntegerEntry : votes.entrySet()) {
            sv += stringIntegerEntry.getKey() + ": " + stringIntegerEntry.getValue() + "\n";
        }
        sendMessage(chatId, sv);
    }

    private void deletePersonFromTeam(long chatId, String userName) {
        Set<User> users = Callback.teams.get(chatId);
        Iterator<User> iterator = users.iterator();
        while (iterator.hasNext()) {
            User user = iterator.next();
            if(user.getFirstName().equals(userName)) {
                iterator.remove();
            }
        }
    }

    private String currentTeam(long chatId) {
        String result = "Выжившие: \n";
        Set<User> users = Callback.teams.get(chatId);
        Iterator<User> iterator = users.iterator();
        while (iterator.hasNext()) {
             result+= iterator.next().getFirstName() + "\n";
        }

        return result;
    }

    private void voting(long chatId, Map<Roles, List<User>> roles) {
        SendMessage message1 = message.voting(roles);
        message1.setChatId(chatId);
        executeMessage(message1);
    }

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }
}
