package com.javarush.task.task30.task3008.client;

import com.javarush.task.task30.task3008.ConsoleHelper;
import com.javarush.task.task30.task3008.Message;
import com.javarush.task.task30.task3008.MessageType;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class BotClient extends Client {
    @Override
    protected String getUserName() {
        return "date_bot_" + (int)(Math.random()*100);
    }

    @Override
    protected boolean shouldSendTextFromConsole() {
        return false;
    }

    @Override
    protected SocketThread getSocketThread() {
        return new BotSocketThread();
    }

    public static void main(String[] args) {
        BotClient botClient = new BotClient();
        botClient.run();
    }

    public class BotSocketThread extends SocketThread {
        @Override
        protected void clientMainLoop() throws IOException, ClassNotFoundException {
            sendTextMessage("Привет чатику. Я бот. Понимаю команды: дата, день, месяц, год, время, час, минуты, секунды.");
            super.clientMainLoop();
        }

        @Override
        protected void processIncomingMessage(String message) {
            ConsoleHelper.writeMessage(message);
            if (message!=null&&!message.isEmpty()&&message.contains(":"))  {
                String[] data = message.split(": ");
                if (data.length == 2) {
                    Date date = Calendar.getInstance().getTime();
                    SimpleDateFormat simpleDateFormat;
                    switch (data[1]) {
                        case "дата": {
                            simpleDateFormat = new SimpleDateFormat("d.MM.YYYY");
                            break;
                        }
                        case "день": {
                            simpleDateFormat = new SimpleDateFormat("d");
                            break;
                        }
                        case "месяц": {
                            simpleDateFormat = new SimpleDateFormat("MMMM");
                            break;
                        }
                        case "год": {
                            simpleDateFormat = new SimpleDateFormat("YYYY");
                            break;
                        }
                        case "время": {
                            simpleDateFormat = new SimpleDateFormat("H:mm:ss");
                            break;
                        }
                        case "час": {
                            simpleDateFormat = new SimpleDateFormat("H");
                            break;
                        }
                        case "минуты": {
                            simpleDateFormat = new SimpleDateFormat("m");
                            break;
                        }
                        case "секунды": {
                            simpleDateFormat = new SimpleDateFormat("s");
                            break;
                        }
                        default: {
                            return;
                        }
                    }
                    sendTextMessage("Информация для " + data[0] + ": " + simpleDateFormat.format(date));
                } else return;
            }
        }
    }
}
