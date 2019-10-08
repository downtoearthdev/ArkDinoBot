package com.scorchedcode.ArkDinoBot;

import com.scorchedcode.ArkDinoBot.exception.MemberRegistrationException;
import com.scorchedcode.ArkDinoBot.exception.ReasonableException;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.security.auth.login.LoginException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

public class ArkDinoBot {
    private static ArkDinoBot instance;
    private String TOKEN;
    private String requestChannelName;
    private String fulfilChannelName;
    private int warnMinutes;
    private TextChannel requestChannel;
    private TextChannel fulfilChannel;
    private JDA api;

    private ArkDinoBot() {

    }

    public static void main(String[] args) {
        ArkDinoBot bot = ArkDinoBot.getInstance();
        bot.initDiscordBot();
        bot.retrieveExistingRequests();
        bot.loadBreeders();
        bot.loadStats();
    }


    public void initDiscordBot() {
        handleConfig();
        try {
            api = new JDABuilder(AccountType.BOT).setToken(TOKEN).build().awaitReady();
        } catch (LoginException | IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        requestChannel = api.getTextChannelsByName(requestChannelName, true).get(0);
        fulfilChannel = api.getTextChannelsByName(fulfilChannelName, true).get(0);
        api.addEventListener(new DiscordListener());
    }

    private void handleConfig() {
        if (!new File("config.json").exists()) {
            try {
                InputStream is = ArkDinoBot.class.getResourceAsStream("/config.json");
                File config = new File("config.json");
                FileWriter os = new FileWriter(config);
                while (is.available() > 0)
                    os.write(is.read());
                is.close();
                os.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            String contents = new String(Files.readAllBytes(new File("config.json").toPath()));
            JSONObject obj = new JSONObject(contents);
            TOKEN = obj.getString("token");
            requestChannelName = obj.getString("request-channel");
            fulfilChannelName = obj.getString("fulfil-channel");
            warnMinutes = obj.getInt("minutes-to-warn");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (TOKEN == null || TOKEN.isEmpty() || requestChannelName == null || requestChannelName.isEmpty() || fulfilChannelName == null || fulfilChannelName.isEmpty())
            System.exit(0);
    }

    public static ArkDinoBot getInstance() {
        if (instance == null)
            instance = new ArkDinoBot();
        return instance;
    }

    public TextChannel getRequestChannel() {
        return requestChannel;
    }

    public TextChannel getFulfilChannel() {
        return fulfilChannel;
    }

    public int getWarnMinutes() {
        return warnMinutes;
    }

    public JDA getAPI() {
        return api;
    }

    private void retrieveExistingRequests() {
        List<Message> msgs = fulfilChannel.getHistoryBefore(fulfilChannel.getLatestMessageId(), 100).complete().getRetrievedHistory();
        int number = 0;
        for (Message msg : msgs) {
            if (msg.getAuthor().equals(api.getSelfUser()) && msg.getEmbeds().size() > 0 && msg.getEmbeds().get(0).getTitle().contains("Breeding Request")) {
                MessageEmbed embed = msg.getEmbeds().get(0);
                if (!embed.getTitle().contains("COMPLETED")) {
                    String id = fulfilChannel.getGuild().getMembersByName(embed.getDescription().split(" ")[0], true).get(0).getId();
                    String creature = Util.getDinoAndAmount(embed.getDescription()).split(" ")[1];
                    Integer amount = Integer.parseInt(Util.getDinoAndAmount(embed.getDescription()).split(" ")[0]);
                    BreedRequest req = new BreedRequest(id, creature, amount);
                    req.setBreeder(fulfilChannel.getGuild().getMembersByName(embed.getFooter().getText().split(" ")[2], true).get(0).getId());
                    req.setMsgId(msg.getId());
                    req.start();
                    req.getBreeder().getUser().openPrivateChannel().complete().sendMessage("<@" + req.getRequester().getId() + ">'s request for " + req.getAmount() + " " + req.getCreature() + " was just reloaded. If you had a timer set for this, please set it again for the appropriate time left!").queue();
                    number++;
                }
            }
        }
        Logger.getGlobal().info("Loaded " + number + " active requests.");
    }

    private void loadBreeders() {
        File savedFeeds = new File("breeders.json");
        if (savedFeeds.exists()) {
            try {
                JSONObject obj = new JSONObject(String.join("", Files.readAllLines(savedFeeds.toPath())));
                for (String breeder : obj.keySet()) {
                    if (!breeder.equals("empty")) {
                        JSONArray creatures = obj.getJSONArray(breeder);
                        List<Object> friendlyNames = creatures.toList();
                        BreederRegistrar.getInstance().addMember(breeder);
                        for (Object name : friendlyNames)
                            BreederRegistrar.getInstance().addCreature(breeder, CreatureTypes.valueOf((String) name).getFriendlyName());
                    }

                }
            } catch (IOException e) {

            } catch (ReasonableException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadStats() {
        File savedData = new File("stats.json");
        if(savedData.exists()) {
            try {
                JSONObject obj = new JSONObject(String.join("", Files.readAllLines(savedData.toPath())));
                for(String creatureString : obj.keySet()) {
                    JSONObject statPairs = obj.getJSONObject(creatureString);
                    try {
                        for (String stat : statPairs.keySet())
                            StatsRegistrar.getInstance().addStat(null, creatureString, stat, statPairs.getInt(stat));
                    }
                    catch (ReasonableException e) {

                    }
                }
            } catch (IOException e) {

            }
        }
    }

    protected void serialize(boolean isBreeders) {
        File savedFeeds = (isBreeders ? new File("breeders.json") : new File("stats.json"));
        JSONObject obj = null;
        try {
            FileUtils.touch(savedFeeds);
            obj = /*new JSONObject();*/new JSONObject(String.join("", Files.readAllLines(savedFeeds.toPath())));

        } catch (IOException e) {
            e.printStackTrace();
        }
        catch (JSONException e) {
            obj = new JSONObject();
        }
        if (isBreeders) {
            BreederRegistrar registrar = BreederRegistrar.getInstance();
            for (String id : registrar.getBreeders()) {
                try {
                    obj.put(id, new JSONArray(registrar.getCreatures(id)));
                } catch (MemberRegistrationException e) {
                    e.printStackTrace();
                }
            }
        } else {
            StatsRegistrar registrar = StatsRegistrar.getInstance();
            for (CreatureTypes creature : registrar.listAllStats().keySet()) {
                JSONObject statPairs = new JSONObject();
                for(DinoStats stat : registrar.listAllStats().get(creature).keySet())
                    statPairs.put(""+stat, registrar.listAllStats().get(creature).get(stat));
                obj.put(creature.name(), statPairs);
            }
        }
        try {
            Files.write(savedFeeds.toPath(), obj.toString().getBytes(), StandardOpenOption.WRITE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
