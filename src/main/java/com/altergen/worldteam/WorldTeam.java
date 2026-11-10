package com.altergen.worldteam;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

/**
 * Main plugin class for WorldTeam
 * Automatically assigns players to scoreboard teams based on world location
 * 
 * @author altergen
 */
public class WorldTeam extends JavaPlugin {
    
    private ScoreboardManager scoreboardManager;
    private TeamManager teamManager;
    
    @Override
    public void onEnable() {
        getLogger().info("WorldTeam v" + getDescription().getVersion() + " by altergen is enabling...");
        
        // Get scoreboard manager
        scoreboardManager = Bukkit.getScoreboardManager();
        if (scoreboardManager == null) {
            getLogger().severe("Failed to get scoreboard manager! Disabling plugin...");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        
        // Initialize team manager
        teamManager = new TeamManager(this, scoreboardManager);
        
        // Register event listeners
        getServer().getPluginManager().registerEvents(new PlayerListener(this, teamManager), this);
        
        // Register commands
        getCommand("worldteam").setExecutor(new TestCommand(this));
        
        // Initialize teams for online players (in case of reload)
        teamManager.initializeForOnlinePlayers();
        
        getLogger().info("WorldTeam has been enabled successfully!");
    }
    
    @Override
    public void onDisable() {
        getLogger().info("WorldTeam is disabling...");
        
        // Clean up teams
        if (teamManager != null) {
            teamManager.cleanup();
        }
        
        getLogger().info("WorldTeam has been disabled.");
    }
    
    /**
     * Get the team manager instance
     * @return TeamManager instance
     */
    public TeamManager getTeamManager() {
        return teamManager;
    }
}