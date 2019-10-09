package com.scorchedcode.ArkDinoBot;

import com.scorchedcode.ArkDinoBot.exception.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.annotation.Nonnull;
import java.awt.*;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class DiscordListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        if (event.getMessage().getContentRaw().split(" ")[0].equalsIgnoreCase(".request") && event.getChannel().equals(ArkDinoBot.getInstance().getRequestChannel())) {
            String[] args = event.getMessage().getContentRaw().split(" ");
            if (args.length >= 3) {
                //try {
                String creature = ((args.length == 3) ? args[1] : (args[1] + "_" + args[2]));
                BreedRequest req = new BreedRequest(event.getMember().getId(), creature, Integer.valueOf((args.length == 3) ? args[2] : args[3]));
                req.setMsgId(event.getMessageId());
                req.start();
                RemoveNotifyMsgTimerTask.sendNotice("Your request has been submitted! A breeder will be assigned shortly.", event.getTextChannel(), 5);
                EmbedBuilder eb = new EmbedBuilder();
                eb.setTitle(req.getCreature() + " Breeding Request", "http://ark.gamepedia.com/" + req.getCreature())
                        .setDescription(event.getMember().getEffectiveName() + " has submitted a request for " + req.getAmount() + " " + req.getCreature() + ". Type .accept " + req.getId().toString() + " to be assigned!")
                        .setColor(Color.MAGENTA);
                ArkDinoBot.getInstance().getFulfilChannel().sendMessage(eb.build()).queue();
                /*} catch (BreedRequest.CreatureNotFoundException e) {
                    event.getChannel().sendMessage(e.getReason()).queue();
                }*/
            } else {
                event.getChannel().sendMessage("Usage: .request <Creature Name> <amount>").queue();
            }

        }

        if (event.getMessage().getContentRaw().split(" ")[0].equalsIgnoreCase(".accept") && event.getChannel().equals(ArkDinoBot.getInstance().getFulfilChannel())) {
            String[] args = event.getMessage().getContentRaw().split(" ");
            if (args.length == 2) {
                try {
                    UUID id = UUID.fromString(args[1]);
                    for (BreedRequest req : BreedRequest.requests) {
                        if (req.getId().equals(id)) {
                            if (req.setBreeder(event.getMember().getId())) {
                                for (Message msg : event.getChannel().getIterableHistory().complete()) {
                                    if (msg.getEmbeds().size() == 1 && msg.getEmbeds().get(0).getDescription() != null && msg.getEmbeds().get(0).getDescription().contains(".accept " + req.getId().toString())) {
                                        req.setMsgId(msg.getId());
                                        EmbedBuilder eb = new EmbedBuilder(msg.getEmbeds().get(0));
                                        eb.setFooter("Assigned to " + event.getMember().getEffectiveName());
                                        msg.editMessage(eb.build()).queue();
                                        event.getMessage().delete().queue();
                                        event.getChannel().sendMessage("<@" + event.getMember().getId() + "> has been assigned!").queue();
                                        req.getRequester().getUser().openPrivateChannel().complete().sendMessage("Your request has been accepted by <@" + req.getBreeder().getId() + ">, contact this user with any questions about your dinos.").queue();
                                        break;
                                    }
                                }
                            } else
                                event.getChannel().sendMessage("This request has already been assigned.").queue();
                            return;
                        }
                    }
                    event.getChannel().sendMessage("No request with that ID found.").queue();
                } catch (IllegalArgumentException e) {
                    event.getChannel().sendMessage("This is not a valid ID.").queue();
                }
            }
        }

        if (event.getMessage().getContentRaw().split(" ")[0].equalsIgnoreCase(".list") && event.getChannel().equals(ArkDinoBot.getInstance().getFulfilChannel())) {
            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle(event.getMember().getEffectiveName() + "'s Assignments");
            for (BreedRequest req : BreedRequest.requests) {
                if (req.getBreeder() != null && req.getBreeder().equals(event.getMember()))
                    eb.addField(req.getAmount() + " " + req.getCreature() + " for " + req.getRequester().getEffectiveName(), "ID: " + req.getId(), false);
            }
            if (eb.getFields().size() == 0)
                eb.setDescription("You have no assignments.");
            event.getChannel().sendMessage(eb.build()).queue();
        }

        if (event.getMessage().getContentRaw().split(" ")[0].equalsIgnoreCase(".complete") && event.getChannel().equals(ArkDinoBot.getInstance().getFulfilChannel())) {
            String[] args = event.getMessage().getContentRaw().split(" ");
            if (args.length == 2) {
                for (BreedRequest req : BreedRequest.requests) {
                    UUID id = UUID.fromString(args[1]);
                    if (req.getId().equals(id)) {
                        if (req.getBreeder().equals(event.getMember()))
                            req.complete();
                        else
                            event.getChannel().sendMessage("Only the assigned breeder can complete a requisition.").queue();
                    }
                }
            }
        }

        if (event.getMessage().getContentRaw().split(" ")[0].equalsIgnoreCase(".listall") && event.getChannel().equals(ArkDinoBot.getInstance().getFulfilChannel())) {
            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("Requisitions");
            for (BreedRequest req : BreedRequest.requests)
                eb.addField(req.getAmount() + " " + req.getCreature() + " for " + req.getRequester().getEffectiveName(), "ID: " + req.getId() + "\nBreeder: " + (req.getBreeder() != null ? req.getBreeder().getEffectiveName() : "None"), false);
            if (eb.getFields().size() == 0)
                eb.setDescription("There are no requisitions");
            event.getChannel().sendMessage(eb.build()).queue();
        }

        if (event.getMessage().getContentRaw().split(" ")[0].equalsIgnoreCase(".settimer") && event.getChannel().equals(ArkDinoBot.getInstance().getFulfilChannel())) {
            String[] args = event.getMessage().getContentRaw().split(" ");
            if (args.length == 3) {
                try {
                    UUID id = UUID.fromString(args[1]);
                    for (BreedRequest req : BreedRequest.requests) {
                        if (req.getId().equals(id)) {
                            if (req.getBreeder() != null && req.getBreeder().equals(event.getMember())) {
                                try {
                                    Duration duration = Duration.parse("PT" + args[2].toUpperCase());
                                    //Duration duration = new Duration(new Instant(System.currentTimeMillis()), Instant.parse(args[2], DateTimeFormat.forPattern("HH:MM")).plus(DateTimeUtils.currentTimeMillis()));
                                    //Logger.getGlobal().info(System.currentTimeMillis() + " and " + duration.toStandardHours().getHours()+ " " + duration.toStandardMinutes().getMinutes());
                                    req.setTimeLeft(LocalTime.now().toNanoOfDay() + duration.toNanos());
                                    //event.getChannel().sendMessage("You entered " + duration.plus(DateTime.now().getMillis()).toString("HH:mm")).queue();
                                    event.getChannel().sendMessage("Assignment will complete at " + LocalTime.ofNanoOfDay(req.getTimeLeft()).toString()).queue();
                                } catch (DateTimeParseException e) {
                                    event.getChannel().sendMessage("This time period is invalid.").queue();
                                }
                            } else
                                event.getChannel().sendMessage("You are not assigned to this requisition.").queue();
                        }
                    }
                } catch (IllegalArgumentException e) {
                    event.getChannel().sendMessage("This is not a valid ID.").queue();
                }
            }
        }
        if (event.getMessage().getContentRaw().split(" ")[0].equalsIgnoreCase(".cancel") && event.getChannel().equals(ArkDinoBot.getInstance().getFulfilChannel())) {
            String[] args = event.getMessage().getContentRaw().split(" ");
            if (args.length == 2) {
                for (BreedRequest req : BreedRequest.requests) {
                    UUID id = UUID.fromString(args[1]);
                    if (req.getId().equals(id)) {
                        if (req.getRequester().equals(event.getMember()))
                            req.cancel();
                        else
                            event.getChannel().sendMessage("Only the original requester can cancel a requisition.").queue();
                    }
                }
            }
        }
        if (event.getMessage().getContentRaw().split(" ")[0].equalsIgnoreCase(".help")) {
            String[] args = event.getMessage().getContentRaw().split(" ");
            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("ArkDinoBot Usage:")
                    .addField(".request", "Request for a breeder to breed a dinosaur for you.\nUsage: .request Dinoname #", false)
                    .addField(".cancel", "Cancel a requisition. Can only be done by the original requester.\nUsage: .cancel id", false)
                    .addField(".listbreeders", "Lists registered breeders and the creatures they breed.\nUsage: .listbreeders", false)
                    .addField(".listdinos", "Lists creatures that a user breeds.\nUsage: .listdinos DiscordName", false)
                    .addField(".liststats", "Lists highest stats this creature is breeding with\nUsage: .liststats dino", false)
                    .addField(".taming", "Retrieves useful information on taming a creature.\nUsage: .taming dinoname level tamingmultiplier# consumemultiplier#\n(the multipliers default to 1 if omitted)", false);
            event.getChannel().sendMessage(eb.build()).queue();
        }
        if (event.getMessage().getContentRaw().split(" ")[0].equalsIgnoreCase(".breederhelp")) {
            String[] args = event.getMessage().getContentRaw().split(" ");
            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("ArkDinoBot Breeder Usage:")
                    .addField(".accept", "Accept a requisition.\nUsage: .request id", false)
                    .addField(".settimer", "Sets the amount of time until eggs are ready.\nUsage: .settimer id #h#m (ie. 6h10m for 6 hours and 10 minutes from now.", false)
                    .addField(".list", "Lists requisitions assigned to you.", false)
                    .addField(".listall", "Lists all requisitions, assigned or unassigned.", false)
                    .addField(".complete", "Manually completes an assigned requisition\nUsage: .complete id", false)
                    .addField(".register", "Registers yourself as a breeder with the creatures specified\nUsage: .register dino1,dino2,dino3,etc", false)
                    .addField(".unregister", "Removes yourself as a breeder (or the specified breeder if the command user is an owner.\nUsage: .unregister <username>", false)
                    .addField(".registerdinos", "Registers additional creatures you breed\nUsage: .registerdinos dino1,dino2,dino3,etc", false)
                    .addField(".setstat", "Sets a high stat for a creature, you must be registered to breed the creature.\nUsage: .setstat Dino_Name (first letter of stat ie. health is h, food is f, damage is d, movement is m)#\nExample: .setstat Spinosaur h500 s200 f1000", false)
                    .addField(".breeding", "Use Crumplecorn's calculator", false)
                    .addField(".statcalc", "Use the DoDoDex stat calculator", false);
            event.getChannel().sendMessage(eb.build()).queue();
        }
        if (event.getMessage().getContentRaw().split(" ")[0].equalsIgnoreCase(".liststats")) {
            String[] args = event.getMessage().getContentRaw().split(" ");
            if (args.length == 2) {
                StatsRegistrar sregistrar = StatsRegistrar.getInstance();
                try {
                    EmbedBuilder eb = new EmbedBuilder();
                    eb.setTitle("Highest Stat Set");
                    for (DinoStats stat : sregistrar.listStats(args[1]).keySet())
                        eb.addField(stat.getFriendlyName(), "" + sregistrar.listStats(args[1]).get(stat), true);
                    event.getTextChannel().sendMessage(eb.build()).queue();
                } catch (ReasonableException e) {
                    event.getTextChannel().sendMessage(e.getReason()).queue();
                    return;
                }
            }
        }
        if (event.getMessage().getContentRaw().split(" ")[0].equalsIgnoreCase(".setstat") && event.getTextChannel().equals(ArkDinoBot.getInstance().getFulfilChannel())) {
            String[] args = event.getMessage().getContentRaw().split(" ");
            if(args.length >= 3) {
                String dino = args[1];
                for(int x = 2; x < args.length; x++) {
                    DinoStats stat;
                    int value = 0;
                    try {
                        switch (args[x].substring(0, 1)) {
                            case "h":
                                stat = DinoStats.HEALTH;
                                break;
                            case "s":
                                stat = DinoStats.STAMINA;
                                break;
                            case "o":
                                stat = DinoStats.OXYGEN;
                                break;
                            case "f":
                                stat = DinoStats.FOOD;
                                break;
                            case "w":
                                stat = DinoStats.WEIGHT;
                                break;
                            case "d":
                                stat = DinoStats.BASE_DAMAGE;
                                break;
                            case "m":
                                stat = DinoStats.MOVEMENT_SPEED;
                                break;
                            case "t":
                                stat = DinoStats.TORPOR;
                                break;
                            default:
                                throw new StatNotFoundException();
                        }
                        value = Integer.parseInt(args[x].substring(1));
                    }
                    catch(NumberFormatException e) {
                        event.getTextChannel().sendMessage("Must be a whole number value!").queue();
                        return;
                    }
                    catch (StatNotFoundException e) {
                        event.getTextChannel().sendMessage(e.getReason()).queue();
                        return;
                    }
                    try {
                        StatsRegistrar.getInstance().addStat(event.getMember().getId(), dino, stat.name(), value);
                        ArkDinoBot.getInstance().serialize(false);
                    } catch (ReasonableException e) {
                        event.getTextChannel().sendMessage(e.getReason()).queue();
                        return;
                    }
                }
                event.getTextChannel().sendMessage("Registered stats!").queue();
            }
            /*if (args.length == 4) {
                String dino = args[1];
                String stat = args[2];
                Integer value = 0;
                try {
                    value = Integer.parseInt(args[3]);
                } catch (NumberFormatException e) {
                    event.getTextChannel().sendMessage("Must be a whole number value!").queue();
                    return;
                }
                try {
                    StatsRegistrar.getInstance().addStat(event.getMember().getId(), dino, stat, value);
                    ArkDinoBot.getInstance().serialize(false);
                } catch (ReasonableException e) {
                    event.getTextChannel().sendMessage(e.getReason()).queue();
                    return;
                }
                event.getTextChannel().sendMessage("Registered stat!").queue();
            }*/ else
                event.getTextChannel().sendMessage("Usage: .setstat Dino_Name stats...(see breederhelp)").queue();
        }

        if (event.getMessage().getContentRaw().split(" ")[0].equalsIgnoreCase(".register") && event.getTextChannel().equals(ArkDinoBot.getInstance().getFulfilChannel())) {
            String[] args = event.getMessage().getContentRaw().split(" ");
            if (args.length == 2) {
                String[] dinos = args[1].split(",");
                BreederRegistrar registrar = BreederRegistrar.getInstance();
                try {
                    registrar.addMember(event.getMember().getId());
                } catch (MemberRegistrationException e) {
                    event.getTextChannel().sendMessage(e.getReason()).queue();
                    return;
                }
                for (String dino : dinos) {
                    try {
                        registrar.addCreature(event.getMember().getId(), dino);
                        ArkDinoBot.getInstance().serialize(true);
                    } catch (ReasonableException ex) {
                        event.getTextChannel().sendMessage(ex.getReason() + " - " + dino).queue();
                    }
                }
                event.getTextChannel().sendMessage("Breeder registered!").queue();
            }

        }

        if (event.getMessage().getContentRaw().split(" ")[0].equalsIgnoreCase(".unregister") && event.getTextChannel().equals(ArkDinoBot.getInstance().getFulfilChannel())) {
            String[] args = event.getMessage().getContentRaw().split(" ");
            BreederRegistrar registrar = BreederRegistrar.getInstance();
            if (args.length == 2) {
                if(event.getMember().isOwner()) {
                    try {
                        registrar.removeMember(event.getGuild().getMembersByName(args[1], true).get(0).getId());
                        ArkDinoBot.getInstance().serialize(true);
                    } catch (MemberRegistrationException e) {
                        event.getTextChannel().sendMessage(e.getReason()).queue();
                        return;
                    }
                }
                else
                    event.getTextChannel().sendMessage("Only an owner can remove other breeders!");
            }
            else {
                try {
                    registrar.removeMember(event.getMember().getId());
                    ArkDinoBot.getInstance().serialize(true);
                    event.getTextChannel().sendMessage("Removed breeder!").queue();
                }
                catch (MemberRegistrationException e) {
                    event.getTextChannel().sendMessage(e.getReason()).queue();
                    return;
                }
            }
        }

        if (event.getMessage().getContentRaw().split(" ")[0].equalsIgnoreCase(".registerdinos") && event.getTextChannel().equals(ArkDinoBot.getInstance().getFulfilChannel())) {
            String[] args = event.getMessage().getContentRaw().split(" ");
            if (args.length == 2) {
                String[] dinos = args[1].split(",");
                BreederRegistrar registrar = BreederRegistrar.getInstance();
                for (String dino : dinos) {
                    try {
                        registrar.addCreature(event.getMember().getId(), dino);
                        ArkDinoBot.getInstance().serialize(true);
                    } catch (ReasonableException ex) {
                        event.getTextChannel().sendMessage(ex.getReason() + " - " + dino).queue();
                    }
                }
                event.getTextChannel().sendMessage("Registration completed!").queue();
            }
        }
        if (event.getMessage().getContentRaw().split(" ")[0].equalsIgnoreCase(".listbreeders")) {
            BreederRegistrar registrar = BreederRegistrar.getInstance();
            if (registrar.getBreeders().size() > 0) {
                EmbedBuilder eb = new EmbedBuilder();
                eb.setTitle("Registered Breeders");
                eb.setColor(Color.ORANGE);
                for (String id : registrar.getBreeders()) {
                    String creatureList = "";
                    try {
                        for (CreatureTypes type : registrar.getCreatures(id))
                            creatureList += type.getFriendlyName() + "\n";
                    } catch (MemberRegistrationException e) {
                        e.printStackTrace();
                    }
                    eb.addField(event.getTextChannel().getGuild().getMemberById(id).getEffectiveName(), creatureList, false);
                }
                event.getTextChannel().sendMessage(eb.build()).queue();
            } else
                event.getTextChannel().sendMessage("No breeders currently registered.").queue();
        }
        if (event.getMessage().getContentRaw().split(" ")[0].equalsIgnoreCase(".listdinos")) {
            String[] args = event.getMessage().getContentRaw().split(" ");
            if (args.length == 2) {
                BreederRegistrar registrar = BreederRegistrar.getInstance();
                try {
                    CreatureTypes[] creatures = registrar.getCreatures(event.getTextChannel().getGuild().getMembersByName(args[1], true).get(0).getId());
                    String friendlyNames = "";
                    for (CreatureTypes type : creatures)
                        friendlyNames += type.getFriendlyName() + "\n";
                    EmbedBuilder eb = new EmbedBuilder();
                    eb.setTitle("Breeder " + event.getTextChannel().getGuild().getMembersByName(args[1], true).get(0).getEffectiveName() + "'s Creatures");
                    eb.setDescription(friendlyNames);
                    event.getTextChannel().sendMessage(eb.build()).queue();
                } catch (MemberRegistrationException e) {
                    event.getTextChannel().sendMessage(e.getReason()).queue();
                }
            }
        }
        if(event.getMessage().getContentRaw().split(" ")[0].equalsIgnoreCase(".taming")) {
            String[] args = event.getMessage().getContentRaw().split(" ");
            if(args.length >= 3) {
                try {
                    CreatureTypes creature = Util.acceptableName(args[1]);
                    Integer lvl = Integer.parseInt(args[2]);
                    int mult = (args.length == 4) ? Integer.parseInt(args[3]) : 1;
                    int consump = (args.length == 5) ? Integer.parseInt(args[4]) : 1;
                    Document doc = null;
                    doc = Jsoup.connect("https://www.dododex.com/taming/"+creature.getFormattedHandle()+"/"+lvl+"?taming="+mult+"&consumption="+consump).userAgent("Mozilla/5.0 (jsoup)").timeout(5000).get();
                    String imgUrl = "https://www.dododex.com/"+doc.selectFirst("#mainImage").attr("src");
                    Element foodTable = doc.selectFirst("#content > div > div:nth-child(3) > div > table");
                    EmbedBuilder eb = new EmbedBuilder();
                    eb.setTitle(doc.selectFirst("#mainImage").attr("alt"));
                    eb.setThumbnail(imgUrl);
                    for(Element tr : foodTable.getElementsByTag("tr")) {
                        if(tr.text().contains("TIME") || tr.text().contains("Mobile/Switch"))
                            continue;
                        Elements tds = tr.getElementsByTag("td");
                        eb.addField(tds.get(0).text(), "Qty: " + tds.get(1).text() + "\nTime: " + tds.get(2).text() + "\nBio Toxin: " + tds.get(3).selectFirst("div").ownText() +"\nNarcotics: " + tds.get(3).selectFirst("div > div").ownText() + "\nNarcoberries: " + tds.get(3).selectFirst("div > div > div").ownText(), false);
                        //foodInfo+=tds.get(0).text() + " Qty:" + tds.get(1).text() + " Time: " + tds.get(2).text() + "\n";

                    }
                    //eb.addField("Food", foodInfo, false);
                    event.getTextChannel().sendMessage(eb.build()).queue();
                } catch (CreatureNotFoundException e) {
                    event.getTextChannel().sendMessage(e.getReason()).queue();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (NumberFormatException e) {
                    event.getTextChannel().sendMessage("Must be a whole number!").queue();
                }
            }
        }

        /*if(event.getMessage().getContentRaw().split(" ")[0].equalsIgnoreCase(".taming")) {
            if(event.getMessage().getContentRaw().split(" ").length == 2) {
                String userAgent = "Mozilla/5.0 (jsoup)";
                int timeout = 5 * 1000;
                Document doc = null;
                Element carnivoreTable = null;
                Element herbivoreTable = null;
                try {
                    doc = Jsoup.connect("https://ark.gamepedia.com/Taming").userAgent(userAgent).timeout(timeout).get();
                    //Creature column selector #mw-content-text > div > table:nth-child(86) > thead > tr > th:nth-child(1)
                    //Rideable column selector #mw-content-text > div > table:nth-child(86) > thead > tr > th:nth-child(2)
                    //First creature lvl30 "#mw-content-text > div > table:nth-child(86) > tbody > tr:nth-child(1) > td:nth-child(1)"
                    //Second creature lvl30 "#mw-content-text > div > table:nth-child(86) > tbody > tr:nth-child(2) > td:nth-child(1)
                    herbivoreTable = doc.selectFirst("#mw-content-text > div > table:nth-child(82)");
                    carnivoreTable = doc.selectFirst("#mw-content-text > div > table:nth-child(86)");
                    Element searchElement = (carnivoreTable.html().contains(event.getMessage().getContentRaw().split(" ")[1]) ? carnivoreTable.getElementsContainingOwnText(event.getMessage().getContentRaw().split(" ")[1]).get(0) : (herbivoreTable.html().contains(event.getMessage().getContentRaw().split(" ")[1]) ? herbivoreTable.getElementsContainingOwnText(event.getMessage().getContentRaw().split(" ")[1]).get(0) : null));
                    if(searchElement == null) {
                        event.getChannel().sendMessage("Species not found!").queue();
                        return;
                    }
                    Element tr = searchElement.parent().parent();
                    EmbedBuilder eb = new EmbedBuilder();
                    eb.setTitle(tr.getElementsByTag("td").get(0).text(), "http://ark.gamepedia.com/"+tr.getElementsByTag("td").get(0).text())
                            .setThumbnail(tr.getElementsByClass("itemlink").attr("src"))
                            .setColor(Color.ORANGE);
                    for(Element td : tr.getElementsByTag("td")) {
                        if(tr.getElementsByTag("td").indexOf(td) == 0)
                            continue;
                        eb.addField(tr.parent().parent().getElementsByTag("tr").get(0).selectFirst("th:nth-child(" + (tr.getElementsByTag("td").indexOf(td)+1) + ")").text(), td.text(), false);
                    }
                    event.getChannel().sendMessage(eb.build()).queue();
                    return;

                } catch (Exception e) {

                }
            }
        }*/
        if(event.getMessage().getContentRaw().split(" ")[0].equalsIgnoreCase(".statcalc")) {
            String[] args = event.getMessage().getContentRaw().split(" ");
            if (args.length == 1) {
                EmbedBuilder eb = new EmbedBuilder();
                eb.setTitle("Stat Calculator Help")
                        .addField("Syntax:", ".statcalc Dino Name level <options...>", false)
                        .addField("Options:", "", false)
                        .addField("w#", "Set the weight stat", true)
                        .addField("o#", "Set the oxygen stat", true)
                        .addField("f#", "Set the food stat", true)
                        .addField("s#", "Set the stamina stat", true)
                        .addField("m#", "Set the melee stat", true)
                        .addField("h#", "Set the health stat", true)
                        .setDescription("If any options are omitted, default values will be used.");
                event.getChannel().sendMessage(eb.build()).queue();
            }
            else if(args.length >= 3) {
                CopyOnWriteArrayList<String> flags = new CopyOnWriteArrayList<>();
                for(int x = 2; x < args.length; x++) {
                    switch (args[x].substring(0, 1)) {
                        case "w":
                            flags.add("weight="+args[x].substring(1));
                            break;
                        case "o":
                            flags.add("oxygen="+args[x].substring(1));
                            break;
                        case "f":
                            flags.add("food="+args[x].substring(1));
                            break;
                        case "s":
                            flags.add("stamina="+args[x].substring(1));
                            break;
                        case "m":
                            flags.add("melee="+args[x].substring(1));
                            break;
                        case "h":
                            flags.add("health="+args[x].substring(1));
                            break;
                        default:
                    }
                }
                /*for (String arg : args) {
                    if (arg.contains("="))
                        flags.add(arg);
                }*/
                /*String primordialName = "";
                for (String arg : args) {
                    if (!flags.contains(arg) && Arrays.asList(args).indexOf(arg) != 2)
                        primordialName += arg + " ";
                }
                String name = primordialName.replaceAll("^.+?\\s", "");*/
                //System.out.println(name);
                Message msg = event.getChannel().sendMessage("Calculating...").complete();
                try {
                    MessageEmbed embed = getStatCalculatorEmbed(args[1], args[2], flags.toArray(new String[flags.size()]));
                    event.getChannel().sendMessage(embed).queue();
                } catch (NumberFormatException e) {
                    event.getTextChannel().sendMessage("Number values must be whole numbers.").queue();
                } catch (CreatureNotFoundException e) {
                    event.getTextChannel().sendMessage(e.getReason()).queue();
                }
                msg.delete().complete();
            }
        }
        if (event.getMessage().getContentRaw().split(" ")[0].equalsIgnoreCase(".breeding")) {
            String[] args = event.getMessage().getContentRaw().split(" ");
            if (args.length == 1) {
                EmbedBuilder eb = new EmbedBuilder();
                eb.setTitle("Feeding Calculator Help")
                        .setAuthor("Based on Crumplecorn's Breeding Calculator", "http://ark.crumplecorn.com/breeding/")
                        .addField("Syntax:", ".breeding Dino Name <options...>", false)
                        .addField("Options:", "", false)
                        .addField("w#", "Set the weight", true)
                        .addField("h#", "Set the hatch multiplier", true)
                        .addField("m#", "Set the mature multiplier", true)
                        .addField("c#", "Set the consume multiplier", true)
                        .addField("r#", "Set the maturation percentage", true)
                        .addField("b#", "Set the number of minutes between feedings", true)
                        .addField("tNormal/Tek/Clicker", "Sets the type of trough", true)
                        .addField("fFood_Type", "Sets the food type, use _ between spaces (ie. Raw_Meat)", true)
                        .setDescription("If any options are omitted, default values will be used.");
                event.getChannel().sendMessage(eb.build()).queue();
            } else if (args.length >= 2) {
                CopyOnWriteArrayList<String> flags = new CopyOnWriteArrayList<>();
                for(int x = 2; x < args.length; x++) {
                        switch (args[x].substring(0, 1)) {
                            case "w":
                                flags.add("weight="+args[x].substring(1));
                                break;
                            case "h":
                                flags.add("hatch="+args[x].substring(1));
                                break;
                            case "m":
                                flags.add("mature="+args[x].substring(1));
                                break;
                            case "c":
                                flags.add("consume="+args[x].substring(1));
                                break;
                            case "r":
                                flags.add("maturation="+args[x].substring(1));
                                break;
                            case "b":
                                flags.add("buffer="+args[x].substring(1));
                                break;
                            case "t":
                                flags.add("trough="+args[x].substring(1));
                                break;
                            case "f":
                                flags.add("food="+args[x].substring(1));
                                break;
                            default:
                        }
                }
                /*for (String arg : args) {
                    if (arg.contains("="))
                        flags.add(arg);
                }
                String primordialName = "";
                for (String arg : args) {
                    if (!flags.contains(arg))
                        primordialName += arg + " ";
                }*/
                //String name = primordialName.replaceAll("^.+?\\s", "");
                //System.out.println(name);
                Message msg = event.getChannel().sendMessage("Calculating...").complete();
                try {
                    MessageEmbed embed = getBreedingEmbed(args[1], flags.toArray(new String[flags.size()]));
                    if (embed != null)
                        event.getChannel().sendMessage(embed).queue();
                    else
                        event.getChannel().sendMessage("No creature found with that name!").queue();
                } catch (NumberFormatException e) {
                    event.getTextChannel().sendMessage("Number values must be whole numbers.").queue();
                } catch (CreatureNotFoundException e) {
                    event.getTextChannel().sendMessage(e.getReason()).queue();
                }
                msg.delete().complete();
            }
        }
    }

    private String stripLinksAndImg(String input) {
        return input.replaceAll("(<a href=.+?>|</a>|<img.+?>)", "");
    }

    private String stripHtmlTags(String input) {
        return input.replaceAll("<.+?>", "");
        //Strips everything but <li></li> -
        //return input.replaceAll("((?!</?li>)<.+?>)", "");
    }

    private ArrayList<String> convertHtmlToList(Elements html) {
        ArrayList<String> convertedHTML = new ArrayList<>();
        //String converted = stripHtmlTags(html);
        for (int x = 0; x < html.get(0).getElementsByTag("li").size(); x++)
            convertedHTML.add(stripHtmlTags(html.get(0).getElementsByTag("li").get(x).html()));
        return convertedHTML;
    }

    private MessageEmbed getStatCalculatorEmbed(String creature, String level, String... flags) throws NumberFormatException,CreatureNotFoundException {
        Integer lvl = Integer.parseInt(level);
        List<String> iterableFlags = Arrays.asList(flags);
        Integer health = 0, stamina = 0, oxygen = 0, food = 0, melee = 0, weight = 0;
        for (String flag : flags) {
            if (flag.contains("health"))
                health = Integer.valueOf(flag.replaceAll("^\\D+=", ""));
            else if (flag.contains("stamina"))
                stamina = Integer.valueOf(flag.replaceAll("^\\D+=", ""));
            else if (flag.contains("oxgen"))
                oxygen = Integer.valueOf(flag.replaceAll("^\\D+=", ""));
            else if (flag.contains("food"))
                food = Integer.valueOf(flag.replaceAll("^\\D+=", ""));
            else if (flag.contains("melee"))
                melee = Integer.valueOf(flag.replaceAll("^\\D+=", ""));
            else if (flag.contains("weight"))
                weight = Integer.valueOf(flag.replaceAll("^\\D+=", ""));
        }
        CreatureTypes mob = Util.acceptableName(creature);
        System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); //So the fucking browser doesn't popup
        WebDriver driver = new ChromeDriver(options);
        driver.get("https://www.dododex.com/stat-calculator/" + mob.getFormattedHandle());
        WebElement creatureInput = driver.findElement(By.cssSelector("#level"));
        creatureInput.click();
        creatureInput.sendKeys(Keys.chord(Keys.LEFT_CONTROL, "a"), ""+lvl, Keys.TAB, (health > 0 ? ""+health : Keys.ARROW_RIGHT), Keys.TAB,
                (stamina > 0 ? ""+stamina : Keys.ARROW_RIGHT), Keys.TAB,
                (food > 0 ? ""+food : Keys.ARROW_RIGHT), Keys.TAB,
                (oxygen > 0 ? ""+oxygen : Keys.ARROW_RIGHT), Keys.TAB,
                (weight > 0 ? ""+weight : Keys.ARROW_RIGHT), Keys.TAB,
                (melee > 0 ? ""+melee : Keys.ARROW_RIGHT));
        //driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(mob.getFriendlyName() + " Stat Calculator");
        try {
            eb.addField(driver.findElement(By.cssSelector("#content > div > div.lightrows.statCalcTable > div:nth-child(2) > div.rowItem.white.flex2-5 > div > div.rowItem.white.flex2 > b")).getText(), driver.findElement(By.cssSelector("#content > div > div.lightrows.statCalcTable > div:nth-child(2) > div.rowItem.white.flex2-5 > div > div.rowItem.white.flex2 > div")).getText() + "\n" +
                    "Base: " + driver.findElement(By.cssSelector("#content > div > div.lightrows.statCalcTable > div:nth-child(2) > div:nth-child(3) > span")).getText() + "\n" +
                    "Included Per Level: " + driver.findElement(By.cssSelector("#content > div > div.lightrows.statCalcTable > div:nth-child(2) > div:nth-child(4) > span")).getText() + "\n" +
                    "Points: " + driver.findElement(By.cssSelector("#content > div > div.lightrows.statCalcTable > div:nth-child(2) > div.rowItem.flex3 > div > div.rowItemN.flex1 > div:nth-child(1) > b")).getText() + " - " + driver.findElement(By.cssSelector("#content > div > div.lightrows.statCalcTable > div:nth-child(2) > div.rowItem.flex3 > div > div.rowItemN.flex1 > div:nth-child(2) > span")).getText(), true);
        }
        catch (NoSuchElementException e) {

        }
        try {
            eb.addField(driver.findElement(By.cssSelector("#content > div > div.lightrows.statCalcTable > div:nth-child(3) > div.rowItem.white.flex2-5 > div > div.rowItem.white.flex2 > b")).getText(), driver.findElement(By.cssSelector("#content > div > div.lightrows.statCalcTable > div:nth-child(3) > div.rowItem.white.flex2-5 > div > div.rowItem.white.flex2 > div")).getText() + "\n" +
                    "Base: " + driver.findElement(By.cssSelector("#content > div > div.lightrows.statCalcTable > div:nth-child(3) > div:nth-child(3) > span")).getText() + "\n" +
                    "Included Per Level: " + driver.findElement(By.cssSelector("#content > div > div.lightrows.statCalcTable > div:nth-child(3) > div:nth-child(4) > span")).getText() + "\n" +
                    "Points: " + driver.findElement(By.cssSelector("#content > div > div.lightrows.statCalcTable > div:nth-child(3) > div.rowItem.flex3 > div > div.rowItemN.flex1 > div:nth-child(1) > b")).getText() + " - " + driver.findElement(By.cssSelector("#content > div > div.lightrows.statCalcTable > div:nth-child(3) > div.rowItem.flex3 > div > div.rowItemN.flex1 > div:nth-child(2) > span")).getText(), true);
        }
        catch (NoSuchElementException e) {

        }
        try {
            eb.addField(driver.findElement(By.cssSelector("#content > div > div.lightrows.statCalcTable > div:nth-child(4) > div.rowItem.white.flex2-5 > div > div.rowItem.white.flex2 > b")).getText(), driver.findElement(By.cssSelector("#content > div > div.lightrows.statCalcTable > div:nth-child(4) > div.rowItem.white.flex2-5 > div > div.rowItem.white.flex2 > div")).getText() + "\n" +
                    "Base: " + driver.findElement(By.cssSelector("#content > div > div.lightrows.statCalcTable > div:nth-child(4) > div:nth-child(3) > span")).getText() + "\n" +
                    "Included Per Level: " + driver.findElement(By.cssSelector("#content > div > div.lightrows.statCalcTable > div:nth-child(4) > div:nth-child(4) > span")).getText() + "\n" +
                    "Points: " + driver.findElement(By.cssSelector("#content > div > div.lightrows.statCalcTable > div:nth-child(4) > div.rowItem.flex3 > div > div.rowItemN.flex1 > div:nth-child(1) > b")).getText() + " - " + driver.findElement(By.cssSelector("#content > div > div.lightrows.statCalcTable > div:nth-child(4) > div.rowItem.flex3 > div > div.rowItemN.flex1 > div:nth-child(2) > span")).getText(), true);
        }
        catch (NoSuchElementException e) {

        }
        try {
            eb.addField(driver.findElement(By.cssSelector("#content > div > div.lightrows.statCalcTable > div:nth-child(5) > div.rowItem.white.flex2-5 > div > div.rowItem.white.flex2 > b")).getText(), driver.findElement(By.cssSelector("#content > div > div.lightrows.statCalcTable > div:nth-child(5) > div.rowItem.white.flex2-5 > div > div.rowItem.white.flex2 > div")).getText() + "\n" +
                    "Base: " + driver.findElement(By.cssSelector("#content > div > div.lightrows.statCalcTable > div:nth-child(5) > div:nth-child(3) > span")).getText() + "\n" +
                    "Included Per Level: " + driver.findElement(By.cssSelector("#content > div > div.lightrows.statCalcTable > div:nth-child(5) > div:nth-child(4) > span")).getText() + "\n" +
                    "Points: " + driver.findElement(By.cssSelector("#content > div > div.lightrows.statCalcTable > div:nth-child(5) > div.rowItem.flex3 > div > div.rowItemN.flex1 > div:nth-child(1) > b")).getText() + " - " + driver.findElement(By.cssSelector("#content > div > div.lightrows.statCalcTable > div:nth-child(5) > div.rowItem.flex3 > div > div.rowItemN.flex1 > div:nth-child(2) > span")).getText(), true);
        }
        catch (NoSuchElementException e) {

        }
        try {
            eb.addField(driver.findElement(By.cssSelector("#content > div > div.lightrows.statCalcTable > div:nth-child(6) > div.rowItem.white.flex2-5 > div > div.rowItem.white.flex2 > b")).getText(), driver.findElement(By.cssSelector("#content > div > div.lightrows.statCalcTable > div:nth-child(6) > div.rowItem.white.flex2-5 > div > div.rowItem.white.flex2 > div")).getText() + "\n" +
                    "Base: " + driver.findElement(By.cssSelector("#content > div > div.lightrows.statCalcTable > div:nth-child(6) > div:nth-child(3) > span")).getText() + "\n" +
                    "Included Per Level: " + driver.findElement(By.cssSelector("#content > div > div.lightrows.statCalcTable > div:nth-child(6) > div:nth-child(4) > span")).getText() + "\n" +
                    "Points: " + driver.findElement(By.cssSelector("#content > div > div.lightrows.statCalcTable > div:nth-child(6) > div.rowItem.flex3 > div > div.rowItemN.flex1 > div:nth-child(1) > b")).getText() + " - " + driver.findElement(By.cssSelector("#content > div > div.lightrows.statCalcTable > div:nth-child(6) > div.rowItem.flex3 > div > div.rowItemN.flex1 > div:nth-child(2) > span")).getText(), true);
        }
        catch (NoSuchElementException e) {

        }
        try {
            eb.addField(driver.findElement(By.cssSelector("#content > div > div.lightrows.statCalcTable > div:nth-child(7) > div.rowItem.white.flex2-5 > div > div.rowItem.white.flex2 > b")).getText(), driver.findElement(By.cssSelector("#content > div > div.lightrows.statCalcTable > div:nth-child(7) > div.rowItem.white.flex2-5 > div > div.rowItem.white.flex2 > div")).getText()+"\n"+
                    "Base: " + driver.findElement(By.cssSelector("#content > div > div.lightrows.statCalcTable > div:nth-child(7) > div:nth-child(3) > span")).getText() + "\n" +
                    "Included Per Level: " + driver.findElement(By.cssSelector("#content > div > div.lightrows.statCalcTable > div:nth-child(7) > div:nth-child(4) > span")).getText() + "\n" +
                    "Points: " + driver.findElement(By.cssSelector("#content > div > div.lightrows.statCalcTable > div:nth-child(7) > div.rowItem.flex3 > div > div.rowItemN.flex1 > div:nth-child(1) > b")).getText() + " - " + driver.findElement(By.cssSelector("#content > div > div.lightrows.statCalcTable > div:nth-child(7) > div.rowItem.flex3 > div > div.rowItemN.flex1 > div:nth-child(2) > span")).getText(), true);
        }
        catch (NoSuchElementException e) {

        }
        String imgUrl = driver.findElement(By.cssSelector("#mainImage")).getAttribute("src");
        eb.setThumbnail(imgUrl);
        eb.setColor(Color.CYAN);
        driver.quit();
        return eb.build();
    }

    private MessageEmbed getBreedingEmbed(String creature, String... flags) throws NumberFormatException, CreatureNotFoundException {
        List<String> iterableFlags = Arrays.asList(flags);
        Integer weight = 0, hatchMultiplier = 0, matureMultiplier = 0, consumeMultiplier = 0, maturationPercentage = 0, bufferMinutes = 0;
        FoodTypes foodType = FoodTypes.RAW_MEAT;
        TroughTypes troughTypes = TroughTypes.NORMAL;
        for (String flag : flags) {
            if (flag.contains("hatch"))
                hatchMultiplier = Integer.valueOf(flag.replaceAll("^\\D+=", ""));
            else if (flag.contains("weight"))
                weight = Integer.valueOf(flag.replaceAll("^\\D+=", ""));
            else if (flag.contains("mature"))
                matureMultiplier = Integer.valueOf(flag.replaceAll("^\\D+=", ""));
            else if (flag.contains("consume"))
                consumeMultiplier = Integer.valueOf(flag.replaceAll("^\\D+=", ""));
            else if (flag.contains("maturation"))
                maturationPercentage = Integer.valueOf(flag.replaceAll("^\\D+=", ""));
            else if (flag.contains("buffer"))
                bufferMinutes = Integer.valueOf(flag.replaceAll("^\\D+=", ""));
            else if (flag.contains("food"))
                foodType = FoodTypes.getFromFriendly(flag.replaceAll("^\\D+=", ""));
            else if (flag.contains("trough"))
                troughTypes = TroughTypes.getFromFriendly(flag.replaceAll("^\\D+=", ""));
        }
        CreatureTypes mob = Util.acceptableName(creature);
        System.out.println(creature);
        System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); //So the fucking browser doesn't popup
        WebDriver driver = new ChromeDriver(options);
        driver.get("http://ark.crumplecorn.com/breeding/");
        WebElement creaturesDropdown = driver.findElement(By.cssSelector("body > ng-view > div > div > form > div:nth-child(4) > table > tbody > tr:nth-child(1) > td:nth-child(2) > select"));
        boolean pass = false;
        for (WebElement elem : creaturesDropdown.findElements(By.tagName("option"))) {
            if (elem.getAttribute("label").toLowerCase().contains(creature))
                pass = true;
        }
        if (pass) {
            WebElement creatureInput = driver.findElement(By.cssSelector("body > ng-view > div > div > form > div:nth-child(4) > table > tbody > tr:nth-child(1) > td:nth-child(2) > input"));
            creatureInput.click();
            creatureInput.sendKeys(creature);
            creatureInput.sendKeys(Keys.TAB, "" + weight, Keys.TAB, foodType.getFriendlyName(), Keys.TAB, "" + hatchMultiplier, Keys.TAB, "" + matureMultiplier, Keys.TAB, "" + consumeMultiplier, Keys.TAB, "" + maturationPercentage, Keys.TAB, "" + bufferMinutes, Keys.TAB, Keys.TAB, troughTypes.getFriendlyName());
            try {
                (new WebDriverWait(driver, 10)).until((WebDriver d) -> creatureInput.getAttribute("value").toLowerCase().contains(creature));
            } catch (TimeoutException e) {
                return null;
            }
            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle(creature + " Feeding Stats");
            eb.addField("Weight", driver.findElement(By.cssSelector("body > ng-view > div > div > form > div:nth-child(4) > table > tbody > tr:nth-child(2) > td:nth-child(2) > input")).getAttribute("value"), true);
            eb.addField("Food Type", driver.findElement(By.cssSelector("body > ng-view > div > div > form > div:nth-child(4) > table > tbody > tr:nth-child(3) > td:nth-child(2) > select")).getAttribute("value").replace("string:", ""), true);
            eb.addBlankField(false);
            eb.addField("Incubation", driver.findElement(By.cssSelector("body > ng-view > div > div > form > div:nth-child(6) > table > tbody > tr:nth-child(1) > td:nth-child(2)")).getText(), true);
            eb.addField("Elapsed Time Since Birth", driver.findElement(By.cssSelector("body > ng-view > div > div > form > div:nth-child(6) > table > tbody > tr:nth-child(3) > td.ng-binding")).getText(), true);
            eb.addField("Time to Juvenile", driver.findElement(By.cssSelector("body > ng-view > div > div > form > div:nth-child(6) > table > tbody > tr:nth-child(4) > td.ng-binding")).getText(), true);
            eb.addField("Time to Adult", driver.findElement(By.cssSelector("body > ng-view > div > div > form > div:nth-child(6) > table > tbody > tr:nth-child(5) > td.ng-binding")).getText(), true);
            eb.addField("Food to Juvenile", driver.findElement(By.cssSelector("body > ng-view > div > div > form > div:nth-child(6) > table > tbody > tr:nth-child(6) > td.ng-binding")).getText(), true);
            eb.addField("Food to Adult", driver.findElement(By.cssSelector("body > ng-view > div > div > form > div:nth-child(6) > table > tbody > tr:nth-child(7) > td.ng-binding")).getText(), true);
            eb.addBlankField(false);
            eb.addField("Current Buffer", driver.findElement(By.cssSelector("body > ng-view > div > div > form > div:nth-child(8) > table > tbody > tr:nth-child(1) > td.ng-binding")).getText(), true);
            eb.addField("Food to Fill", driver.findElement(By.cssSelector("body > ng-view > div > div > form > div:nth-child(8) > table > tbody > tr:nth-child(2) > td.ng-binding")).getText(), true);
            eb.addField("Hand Feed For", driver.findElement(By.cssSelector("body > ng-view > div > div > form > div:nth-child(8) > table > tbody > tr:nth-child(3) > td.ng-binding")).getText(), true);
            eb.addField("Food to Finish", driver.findElement(By.cssSelector("body > ng-view > div > div > form > div:nth-child(8) > table > tbody > tr:nth-child(4) > td.ng-binding")).getText(), true);
            eb.addField("Time Until Buffer", driver.findElement(By.cssSelector("body > ng-view > div > div > form > div:nth-child(8) > table > tbody > tr:nth-child(6) > td.ng-binding")).getText(), true);
            eb.addBlankField(false);
            eb.addField("Trough Type", driver.findElement(By.cssSelector("body > ng-view > div > div > form > div:nth-child(12) > table > tbody > tr:nth-child(1) > td:nth-child(2) > select")).getAttribute("value").replaceAll("string:", ""), true);
            eb.addField("Loss", driver.findElement(By.cssSelector("body > ng-view > div > div > form > div:nth-child(12) > table > tbody > tr:nth-child(2) > td.ng-binding")).getText(), true);
            eb.setColor(Color.CYAN);
            driver.quit();
            return eb.build();
        }
        driver.quit();
        return null;
        /*WebElement seleniumForm = driver.findElement(By.cssSelector("body > ng-view > div > div > form"));
                String bigPaste = "";
                for(WebElement tr : seleniumForm.findElements(By.tagName("tr"))) {
                    if(tr.findElements(By.tagName("td")).size() == 4)
                        continue;
                    for(WebElement td : tr.findElements(By.cssSelector("td, input:not([type=\"button\"]):not(select)"))) {
                        if (td.getTagName().equalsIgnoreCase("td"))
                            bigPaste+=(td.getText().trim().isEmpty() ? "" : td.getText().trim()+ (td.getText().trim().lastIndexOf(":") == td.getText().trim().length()-1 ? " " : "\n"));
                        else if (td.getTagName().equalsIgnoreCase("input") || td.getTagName().equalsIgnoreCase("select"))
                            bigPaste+=(td.getAttribute("value").trim().isEmpty() ? "" : td.getAttribute("value").trim()+"\n");
                    }
                }
                event.getChannel().sendMessage("```"+bigPaste.trim()+"```").queue();*/
    }

}
