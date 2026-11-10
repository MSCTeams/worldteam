package com.altergen.worldteam;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Listener for player events
 * 
 * @author altergen
 */
public class PlayerListener implements Listener {
    
    private final WorldTeam plugin;
    private final TeamManager teamManager;
    
    /**
     * Constructor for PlayerListener
     * @param plugin The plugin instance
     * @param teamManager The team manager
     */
    public PlayerListener(WorldTeam plugin, TeamManager teamManager) {
        this.plugin = plugin;
        this.teamManager = teamManager;
    }
    
    /**
     * Handle player join event
     * @param event PlayerJoinEvent
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        // Update player's team based on their current world
        teamManager.updatePlayerTeam(player);
        
        plugin.getLogger().fine("Player " + player.getName() + " joined and was assigned to team");
    }
    
    /**
     * Handle player world change event
     * @param event PlayerChangedWorldEvent
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        
        // Update player's team based on their new world
        teamManager.updatePlayerTeam(player);
        
        plugin.getLogger().fine("Player " + player.getName() + " changed world and was reassigned to team");
    }
    
    /**
     * Handle player quit event
     * @param event PlayerQuitEvent
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        
        // Remove player from all teams
        TeamManager tm = teamManager;
        if (tm != null) {
            // Remove player from all teams
            for (org.bukkit.scoreboard.Team team : tm.getMainScoreboard().getTeams()) {
                if (team.hasEntry(player.getName())) {
                    team.removeEntry(player.getName());
                    
                    plugin.getLogger().fine("Player " + player.getName() + " removed from team: " + team.getName());
                }
            }
        }
    }
}