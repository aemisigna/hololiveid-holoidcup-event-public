package com.covercorp.holosports.game.minigame.tug.arena.listener;

import com.covercorp.holosports.game.minigame.tug.TugMiniGame;
import com.covercorp.holosports.game.minigame.tug.arena.TugArena;
import com.covercorp.holosports.game.minigame.tug.arena.state.TugMatchState;
import com.covercorp.holosports.game.minigame.tug.player.player.ITugPlayer;
import com.covercorp.holosports.game.minigame.tug.team.team.ITugTeam;
import com.covercorp.holosports.game.minigame.tug.util.MatchUtil;
import com.covercorp.holosports.game.util.StringUtils;

import org.bukkit.*;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.util.BoundingBox;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

public final class MatchGameListener implements Listener {
    private final TugMiniGame tugMiniGame;
    private final TugArena arena;

    public MatchGameListener(final TugMiniGame tugMiniGame, final TugArena arena) {
        this.tugMiniGame = tugMiniGame;
        this.arena = arena;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInteract(final PlayerInteractEvent event) {
        final Player sender = event.getPlayer();

        if (arena.getState() != TugMatchState.GAME) return;

        if (event.getClickedBlock() == null) return;

        if (event.getClickedBlock().getType() == Material.SPRUCE_TRAPDOOR) {
            event.setCancelled(true);
            return;
        }

        if (event.getClickedBlock().getType() != Material.LEVER) return;

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        final Optional<ITugPlayer> tugPlayerOptional = tugMiniGame.getPlayerHelper().getPlayer(sender.getUniqueId());
        if (tugPlayerOptional.isEmpty()) return;

        final ITugPlayer tugPlayer = tugPlayerOptional.get();
        final ITugTeam tugTeam = tugPlayer.getTeam();

        if (tugTeam == null) return;

        if (tugPlayer.isSpectating()) return;

        tugTeam.setPoints(tugTeam.getPoints() + 1);

        sender.playSound(sender, Sound.BLOCK_NOTE_BLOCK_BASS, 2.0F, 2.0F);

        // Get a random player of the other team to pull to the center a bit.
        final ITugTeam rivalTeam = arena.getTeamHelper().getOppositeTeam(tugTeam);
        if (rivalTeam == null) return;

        if (rivalTeam.getPlayers().size() == 0) {
            arena.runLoser();
            return;
        }

        final Random random = new Random();
        final ITugPlayer rivalTugPlayer = rivalTeam.getPlayers().stream().toList().get(random.nextInt(rivalTeam.getPlayers().size()));
        final Player rivalPlayer = Bukkit.getPlayer(rivalTugPlayer.getUniqueId());
        if (rivalPlayer == null) return;

        if (rivalPlayer.getGameMode() != GameMode.SPECTATOR) {
            if (rivalPlayer.isOnGround()) {
                MatchUtil.moveToward(rivalPlayer, arena.getCenterLocation().clone().add(0, -4, 0), 0.06);
            }
            /*
            if (rivalPlayer.getLocation().clone().add(0, -1, 0).getBlock().getType() != Material.AIR) {
                MatchUtil.moveToward(rivalPlayer, arena.getCenterLocation().clone().add(0, -4, 0), 0.06);
            }*/
        }

        // If the point difference between the teams is 100 or more, end the game.
        final ITugTeam team1 = arena.getTeamHelper().getTeamList().get(0);
        final ITugTeam team2 = arena.getTeamHelper().getTeamList().get(1);

        final int diff = Math.abs(team1.getPoints() - team2.getPoints());
        if (diff >= 100) {
            arena.setState(TugMatchState.ENDING);
            arena.runLoser();
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDeath(final EntityDamageEvent event) {
        if (!(event.getEntity() instanceof final Player player)) return;

        if (arena.getState() == TugMatchState.ROPE_STARTING) {
            // Check if die
            if (player.getHealth() - event.getFinalDamage() > 0) return;

            event.setCancelled(true);

            final Optional<ITugPlayer> tugPlayerOptional = arena.getPlayerHelper().getPlayer(player.getUniqueId());
            if (tugPlayerOptional.isEmpty()) return;

            final ITugPlayer tugPlayer = tugPlayerOptional.get();
            final ITugTeam tugTeam = tugPlayer.getTeam();
            if (tugTeam == null) return;

            player.teleport(tugTeam.getSpawn());
        }
        if (arena.getState() == TugMatchState.GAME || arena.getState() == TugMatchState.ENDING) {
            // Check if the player died
            if (player.getHealth() - event.getFinalDamage() > 0) return;

            event.setCancelled(true);

            final Optional<ITugPlayer> tugPlayerOptional = arena.getPlayerHelper().getPlayer(player.getUniqueId());
            if (tugPlayerOptional.isEmpty()) return;

            final ITugPlayer tugPlayer = tugPlayerOptional.get();
            final ITugTeam tugTeam = tugPlayer.getTeam();
            if (tugTeam == null) return;

            final Firework firework = player.getWorld().spawn(player.getLocation(), Firework.class);
            final FireworkMeta fireworkMeta = firework.getFireworkMeta();

            fireworkMeta.setPower(1);
            fireworkMeta.addEffect(FireworkEffect.builder().with(FireworkEffect.Type.BALL).withColor(Color.RED).withFade(Color.ORANGE).build());

            firework.setFireworkMeta(fireworkMeta);
            firework.detonate();

            player.setGameMode(GameMode.SPECTATOR);
            player.playSound(player.getLocation(), Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, 1.0F, 0.6F);
            player.sendTitle(StringUtils.translate("&c&lYou died!"), StringUtils.translate("You fell off the bridge!"), 5, 40, 10);

            arena.getArenaAnnouncer().sendGlobalMessage(StringUtils.translate("#ff6370" + tugPlayer.getName() + " fell off the platform!"));

            if (arena.getState() != TugMatchState.ENDING) {
                // Check if there's no more players alive in the team.
                if (tugTeam.getPlayers().stream().filter(playingPlayer -> !playingPlayer.isSpectating()).toList().isEmpty()) {
                    arena.setState(TugMatchState.ENDING);
                    arena.runLoser();

                    return;
                }

                // If there's no more players alive in general, end the game.
                if (arena.getPlayerHelper().getPlayerList().stream().filter(playingPlayer -> !playingPlayer.isSpectating()).toList().isEmpty()) {
                    arena.setState(TugMatchState.ENDING);
                    arena.runLoser();
                }
            }
        }
    }
}
