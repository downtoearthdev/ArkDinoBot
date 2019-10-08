package com.scorchedcode.ArkDinoBot;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Timer;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

public class BreedRequest implements Serializable {

    private String mob;
    private UUID id;
    private int amount;
    private String breeder;
    private String requester;
    private String msgId;
    private boolean cancelled = false;
    private long timeLeft = 0;
    protected static CopyOnWriteArrayList<BreedRequest> requests = new CopyOnWriteArrayList<>();

    public BreedRequest(String id, String creature, int amount) /*throws CreatureNotFoundException*/ {
        /*try {
            this.mob = Creature.valueOf(creature);
        }
        catch(IllegalArgumentException e) {
            throw new CreatureNotFoundException();
        }*/
        this.mob = creature;
        this.requester = id;
        this.amount = amount;
        this.id = UUID.randomUUID();
        requests.add(this);
    }

    public void setTimeLeft(long time) {
        this.timeLeft = time;
        Timer taskTimer = new Timer();
        taskTimer.scheduleAtFixedRate(new HatchTaskTimer(this), 1000L, 1000L);
    }

    public boolean setBreeder(String id) {
        if(breeder == null) {
            breeder = id;
            return true;
        }
        else
            return false;
    }

    public void setMsgId(String id) {
        this.msgId = id;
    }

    public String getMsgId() {
        return msgId;
    }

    public long getTimeLeft() {
        return timeLeft;
    }

    public Member getBreeder() {
        return (breeder != null ? ArkDinoBot.getInstance().getAPI().getGuilds().get(0).getMemberById(breeder) : null);
    }

    public String getCreature() {
        return mob;
    }

    public UUID getId() {
        return id;
    }

    public Member getRequester() {
        return ArkDinoBot.getInstance().getAPI().getGuilds().get(0).getMemberById(requester);
    }

    public int getAmount() {
        return amount;
    }

    public void complete() {
        Message msg = ArkDinoBot.getInstance().getFulfilChannel().getHistoryAround(msgId, 5).complete().getMessageById(msgId);
        //EmbedBuilder eb = new EmbedBuilder(msg.getEmbeds().get(0));
        //eb.setTitle(msg.getEmbeds().get(0).getTitle() + " COMPLETED");
        //msg.editMessage(eb.build()).queue();
        msg.delete().queue();
        ArkDinoBot.getInstance().getRequestChannel().sendMessage("Hey, <@"+requester+">, your " + getCreature() + " is ready!").queue();
        ArkDinoBot.getInstance().getFulfilChannel().sendMessage("<@"+breeder+"> has completed requisition " + id.toString()).queue();
        cancelled = true;
        requests.remove(this);
    }

    public void cancel() {
        Message msg = ArkDinoBot.getInstance().getFulfilChannel().getHistoryAround(msgId, 5).complete().getMessageById(msgId);
        //EmbedBuilder eb = new EmbedBuilder(msg.getEmbeds().get(0));
        //eb.setTitle(msg.getEmbeds().get(0).getTitle() + " CANCELED");
        //msg.editMessage(eb.build()).queue();
        msg.delete().queue();
        ArkDinoBot.getInstance().getRequestChannel().sendMessage("Requisition cancelled.").queue();
        ArkDinoBot.getInstance().getFulfilChannel().sendMessage("<@"+requester+"> has cancelled requisition " + id.toString()).queue();
        cancelled = true;
        requests.remove(this);
    }

    public boolean isCancelled() {
        return  cancelled;
    }

    public void start() {
        new Timer().schedule(new RemoveNotifyMsgTimerTask(ArkDinoBot.getInstance().getFulfilChannel().getHistoryAround(msgId, 5).complete().getMessageById(msgId)), 1440);
    }



}
