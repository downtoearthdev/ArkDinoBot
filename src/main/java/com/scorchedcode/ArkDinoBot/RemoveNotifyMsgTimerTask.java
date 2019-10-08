package com.scorchedcode.ArkDinoBot;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.Timer;
import java.util.TimerTask;

public class RemoveNotifyMsgTimerTask extends TimerTask {

    private Message msg;

    public RemoveNotifyMsgTimerTask(Message msg) {
        this.msg = msg;
    }

    @Override
    public void run() {
        if(msg.getEmbeds().size() > 0 && msg.getEmbeds().get(0).getTitle() != null && msg.getEmbeds().get(0).getTitle().contains("Request")) {
            for(BreedRequest req : BreedRequest.requests) {
                if(req.getMsgId().equals(msg.getId()) && req.getBreeder() != null) {
                    return;
                }
            }
        }
        msg.delete().complete();
    }

    public static void sendNotice(String text, TextChannel channel, int expiration) {
        Message notifyMsg = channel.sendMessage(new EmbedBuilder().setTitle("Notice").setDescription(text).setFooter("Expires in " + String.valueOf(expiration) + " minutes.").build()).complete();
        new Timer().schedule(new RemoveNotifyMsgTimerTask(notifyMsg), expiration * 60 * 1000);
    }

}
