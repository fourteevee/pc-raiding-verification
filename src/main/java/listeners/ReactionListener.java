package listeners;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import main.Config;
import main.Emote;
import main.Rank;
import main.StatsJson;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.priv.react.PrivateMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import raids.Raid;
import raids.RaidHub;
import utils.Utils;

import javax.annotation.Nonnull;
import java.util.concurrent.TimeUnit;


public class ReactionListener extends ListenerAdapter {

    private EventWaiter eventWaiter;

    public ReactionListener(EventWaiter eventWaiter) {
        this.eventWaiter = eventWaiter;
    }

    @Override
    public void onGuildMessageReactionAdd(@Nonnull GuildMessageReactionAddEvent event) {

        Member member = event.getMember();
        User user = event.getUser();
        String messageId = event.getMessageId();
        TextChannel textChannel = event.getChannel();
        MessageReaction.ReactionEmote reactionEmote = event.getReactionEmote();

        if (!user.isBot()) {
            Raid raid = RaidHub.getRaid(messageId);
            if (raid != null) {
                if (event.getReactionEmote().isEmote()) {
                    net.dv8tion.jda.api.entities.Emote emote = event.getReactionEmote().getEmote();
                    if (!raid.isRaidActive()) {
                        if (raid.getRaidRoom().getMembers().contains(member)) {
                            if (emote == Emote.NEST_KEY.getEmote()) {
                                if (!raid.getRaidType().equals(RaidHub.RaidType.WR_RAID)) {
                                    if (raid.getNestKeyReactions().size() < Long.parseLong((String) Config.get("MAX_LOCATION_REACTS"))) {
                                        Message privateMessage = Utils.sendPM(member.getUser(), "Did you intend to react with " + Emote.NEST_KEY.getEmote().getAsMention()
                                                + "? If you react to this without having a nest key you will be suspended");

                                        privateMessage.addReaction("\u2705").queue();
                                        privateMessage.addReaction("\u274C").queue();

                                        eventWaiter.waitForEvent(PrivateMessageReactionAddEvent.class, e -> {
                                            return e.getMessageId().equals(privateMessage.getId()) && !e.getUser().isBot()
                                                    && e.getReactionEmote().isEmoji() && (e.getReactionEmote().getEmoji().equals("\u2705") || e.getReactionEmote().getEmoji().equals("\u274C"));
                                        }, e -> {
                                            if (e.getReactionEmote().isEmoji() && (e.getReactionEmote().getEmoji().equals("\u2705"))) {
                                                if (raid.getNestKeyReactions().size() == 0) {
                                                    StatsJson.addKeysPopped(e.getUserId());
                                                }
                                                Utils.sendPM(member.getUser(), "**Location:** " + raid.getLocation());
                                            }
                                            raid.getNestKeyReactions().add(member);
                                        }, 1, TimeUnit.HOURS, () -> {
                                        });
                                    }
                                } else {
                                    Utils.sendPM(member.getUser(), "Please listen to the RL to call location");
                                }
                            } else if (!raid.getRaidType().equals(RaidHub.RaidType.WR_RAID)) {
                                if (emote == Emote.EVENT_KEY.getEmote()) {
                                    if (raid.getEventKeyReactions().size() < Long.parseLong((String) Config.get("MAX_LOCATION_REACTS"))) {
                                        Message privateMessage = Utils.sendPM(member.getUser(), "Did you intend to react with " + Emote.EVENT_KEY.getEmote().getAsMention()
                                                + "? If you react to this without having a nest key you will be suspended");

                                        privateMessage.addReaction("\u2705").queue();
                                        privateMessage.addReaction("\u274C").queue();

                                        eventWaiter.waitForEvent(PrivateMessageReactionAddEvent.class, e -> {
                                            return e.getMessageId().equals(privateMessage.getId()) && !e.getUser().isBot()
                                                    && e.getReactionEmote().isEmoji() && (e.getReactionEmote().getEmoji().equals("\u2705") || e.getReactionEmote().getEmoji().equals("\u274C"));
                                        }, e -> {
                                            if (e.getReactionEmote().isEmoji() && (e.getReactionEmote().getEmoji().equals("\u2705"))) {
                                                if (raid.getEventKeyReactions().size() == 0) {
                                                    StatsJson.addKeysPopped(e.getUserId());
                                                }
                                                Utils.sendPM(member.getUser(), "**Location:** " + raid.getLocation());
                                            }
                                            raid.getEventKeyReactions().add(member);
                                        }, 1, TimeUnit.HOURS, () -> {
                                        });
                                    }
                                } else if (emote == Emote.NITRO.getEmote()) {
                                    if (raid.getNitroReactions().size() < Long.parseLong((String) Config.get("MAX_LOCATION_REACTS"))) {
                                        if (member.getTimeBoosted() == null) {
                                            Utils.sendPM(member.getUser(), "This feature is unavailable. You are currently not boosting our server.");
                                            event.getReaction().removeReaction(member.getUser()).queue();
                                        } else {
                                            Utils.sendPM(member.getUser(), "**Location:** " + raid.getLocation());
                                            raid.getNitroReactions().add(member);
                                        }
                                    }
                                } else if (emote == Emote.ASSIST.getEmote()) {
                                        if (!Rank.getHighestRank(member).isAtLeast(Rank.ALMOST_RL)) {
                                            Utils.sendPM(member.getUser(), "This feature is unavailable. You are not a raid leader or security.");
                                            event.getReaction().removeReaction(member.getUser()).queue();
                                        } else if(member == raid.getLeader()) {
                                        Utils.sendPM(member.getUser(), "This feature is unavailable. You cant assist on your own run. \uD83E\uDD21");
                                    } else {
                                            Utils.sendPM(member.getUser(), "If you are still in the raid channel at the end of the run, you will recieve an assist.");
                                            raid.getAssistReactions().add(member);
                                        }
                                    }

                                    } else if ((raid.isFullSkipped() || raid.getRaidType().equals(RaidHub.RaidType.EXRL_RAID)) && !raid.getRaidType().equals(RaidHub.RaidType.EVENT_RAID)) {
                                        if (emote == Emote.KNIGHT.getEmote()) {
                                            if (raid.getKnightReactions().size() < Long.parseLong((String) Config.get("MAX_LOCATION_REACTS"))) {
                                                Message privateMessage = Utils.sendPM(member.getUser(), "Did you intend to react with " + Emote.KNIGHT.getEmote().getAsMention() + "? " +
                                                        "If you react to this without having a 85heal/85mheal pet you will be suspended");

                                                privateMessage.addReaction("\u2705").queue();
                                                privateMessage.addReaction("\u274C").queue();

                                                eventWaiter.waitForEvent(PrivateMessageReactionAddEvent.class, e -> {
                                                    return e.getMessageId().equals(privateMessage.getId()) && !e.getUser().isBot()
                                                            && e.getReactionEmote().isEmoji() && (e.getReactionEmote().getEmoji().equals("\u2705") || e.getReactionEmote().getEmoji().equals("\u274C"));
                                                }, e -> {
                                                    if (e.getReactionEmote().isEmoji() && (e.getReactionEmote().getEmoji().equals("\u2705"))) {
                                                        Utils.sendPM(member.getUser(), "**Location:** " + raid.getLocation());
                                                    }
                                                    raid.getKnightReactions().add(member);
                                                }, 1, TimeUnit.HOURS, () -> {
                                                });
                                            }
                                        } else if (emote == Emote.PURI.getEmote()) {
                                            if (raid.getPuriReactions().size() < Long.parseLong((String) Config.get("MAX_LOCATION_REACTS"))) {
                                                Message privateMessage = Utils.sendPM(member.getUser(), "Did you intend to react with " + Emote.PURI.getEmote().getAsMention() + "? " +
                                                        "If you react to this without having a 90mheal pet you will be suspended");

                                                privateMessage.addReaction("\u2705").queue();
                                                privateMessage.addReaction("\u274C").queue();

                                                eventWaiter.waitForEvent(PrivateMessageReactionAddEvent.class, e -> {
                                                    return e.getMessageId().equals(privateMessage.getId()) && !e.getUser().isBot()
                                                            && e.getReactionEmote().isEmoji() && (e.getReactionEmote().getEmoji().equals("\u2705") || e.getReactionEmote().getEmoji().equals("\u274C"));
                                                }, e -> {
                                                    if (e.getReactionEmote().isEmoji() && (e.getReactionEmote().getEmoji().equals("\u2705"))) {
                                                        Utils.sendPM(member.getUser(), "**Location:** " + raid.getLocation());
                                                    }
                                                    raid.getPuriReactions().add(member);
                                                }, 1, TimeUnit.HOURS, () -> {
                                                });
                                            }
                                        } else if (emote == Emote.MYSTIC.getEmote()) {
                                            if (raid.getMysticReactions().size() < Long.parseLong((String) Config.get("MAX_LOCATION_REACTS"))) {
                                                Message privateMessage = Utils.sendPM(member.getUser(), "Did you intend to react with " + Emote.MYSTIC.getEmote().getAsMention() + "? " +
                                                        "If you react to this without having a 85heal/85mheal pet you will be suspended");

                                                privateMessage.addReaction("\u2705").queue();
                                                privateMessage.addReaction("\u274C").queue();

                                                eventWaiter.waitForEvent(PrivateMessageReactionAddEvent.class, e -> {
                                                    return e.getMessageId().equals(privateMessage.getId()) && !e.getUser().isBot()
                                                            && e.getReactionEmote().isEmoji() && (e.getReactionEmote().getEmoji().equals("\u2705") || e.getReactionEmote().getEmoji().equals("\u274C"));
                                                }, e -> {
                                                    if (e.getReactionEmote().isEmoji() && (e.getReactionEmote().getEmoji().equals("\u2705"))) {
                                                        Utils.sendPM(member.getUser(), "**Location:** " + raid.getLocation());
                                                    }
                                                    raid.getMysticReactions().add(member);
                                                }, 1, TimeUnit.HOURS, () -> {
                                                });
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

        }

