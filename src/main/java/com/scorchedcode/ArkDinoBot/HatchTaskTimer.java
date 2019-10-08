package com.scorchedcode.ArkDinoBot;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.TimerTask;

public class HatchTaskTimer extends TimerTask {

    private BreedRequest req;
    private boolean firstPass = false;
    protected HatchTaskTimer(BreedRequest req) {
        this.req = req;
    }

    @Override
    public void run() {
        if(req.isCancelled()) {
            cancel();
            return;
        }
        if(!firstPass && Duration.between(LocalTime.now(), LocalTime.ofNanoOfDay(req.getTimeLeft())).toMinutes() <= 5) {
            firstPass = true;
            ArkDinoBot.getInstance().getRequestChannel().sendMessage("Heads up, <@"+req.getRequester().getId()+">! Your " + req.getCreature() + " eggs are almost ready!").queue();
        }
        else if(firstPass && LocalTime.now().isAfter(LocalTime.ofNanoOfDay(req.getTimeLeft()))) {
            req.complete();
            cancel();
        }
    }
}
