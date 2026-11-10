package com.altergen.worldteam;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manages scoreboard teams for different worlds
 * 
 * @author altergen
 */
public class TeamManager {
    
    private final JavaPlugin plugin;
    private final ScoreboardManager scoreboardManager;
    private Scoreboard mainScoreboard;
    private final Map<String, Team> teams;
    
    // Team names
    public static final String TEAM_OVERWORLD = "overworld";
    public static final String TEAM_NETHER = "nether";
    public static final String TEAM_END = "end";
    
    /**
     * Constructor for TeamManager
     * @param plugin The plugin instance
     * @param scoreboardManager The scoreboard manager
     */
    public TeamManager(JavaPlugin plugin, ScoreboardManager scoreboardManager) {
        this.plugin = plugin;
        this.scoreboardManager = scoreboardManager;
        this.teams = new HashMap<>();
        
        // Get the main scoreboard
        this.mainScoreboard = scoreboardManager.getMainScoreboard();
        
        // Initialize teams
        initializeTeams();
    }
    
    /**
     * Initialize all teams
     */
    private void initializeTeams() {
        plugin.getLogger().info("Initializing scoreboard teams...");
        
        // Remove existing teams with the same names (cleanup from previous sessions)
        removeExistingTeams();
        
        // Create Overworld team
        Team overworldTeam = createTeam(TEAM_OVERWORLD, ChatColor.GREEN + "[Overworld]", ChatColor.GREEN);
        teams.put(TEAM_OVERWORLD, overworldTeam);
        
        // Create Nether team
        Team netherTeam = createTeam(TEAM_NETHER, ChatColor.RED + "[Nether]", ChatColor.RED);
        teams.put(TEAM_NETHER, netherTeam);
        
        // Create End team
        Team endTeam = createTeam(TEAM_END, ChatColor.DARK_PURPLE + "[End]", ChatColor.DARK_PURPLE);
        teams.put(TEAM_END, endTeam);
        
        plugin.getLogger().info("Successfully initialized " + teams.size() + " teams");
    }
    
    /**
     * Remove existing teams with our team names
     */
    private void removeExistingTeams() {
        for (String teamName : new String[]{TEAM_OVERWORLD, TEAM_NETHER, TEAM_END}) {
            Team existingTeam = mainScoreboard.getTeam(teamName);
            if (existingTeam != null) {
                existingTeam.unregister();
            }
        }
    }
    
    /**
     * Create a new team
     * @param name Team name
     * @param prefix Team prefix
     * @param color Team color
     * @return Created team
     */
    private Team createTeam(String name, String prefix, ChatColor color) {
        Team team = mainScoreboard.registerNewTeam(name);
        team.setPrefix(prefix);
        team.setColor(color);
        team.setDisplayName(name);
        team.setAllowFriendlyFire(false);
        team.setCanSeeFriendlyInvisibles(true);
        return team;
    }
    
    /**
     * Get the appropriate team for a world
     * @param world The world
     * @return The team for the world
     */
    public Team getTeamForWorld(World world) {
        if (world == null) {
            return teams.get(TEAM_OVERWORLD);
        }
        
        String worldName = world.getName().toLowerCase();
        
        // Check world name for nether/end keywords
        if (worldName.contains("nether")) {
            return teams.get(TEAM_NETHER);
        } else if (worldName.contains("end")) {
            return teams.get(TEAM_END);
        }
        
        // Check world environment
        Environment env = world.getEnvironment();
        switch (env) {
            case NETHER:
                return teams.get(TEAM_NETHER);
            case THE_END:
                return teams.get(TEAM_END);
            case NORMAL:
            default:
                return teams.get(TEAM_OVERWORLD);
        }
    }
    
    /**
     * Get the team for a player based on their current world
     * @param player The player
     * @return The team for the player's current world
     */
    public Team getTeamForPlayer(Player player) {
        if (player == null) {
            return teams.get(TEAM_OVERWORLD);
        }
        
        return getTeamForWorld(player.getWorld());
    }
    
    /**
     * Update a player's team based on their current world
     * @param player The player to update
     */
    public void updatePlayerTeam(Player player) {
        if (player == null) {
            return;
        }
        
        // Remove player from all teams first
        removePlayerFromAllTeams(player);
        
        // Add player to the appropriate team
        Team targetTeam = getTeamForPlayer(player);
        if (targetTeam != null) {
            targetTeam.addEntry(player.getName());
            player.setScoreboard(mainScoreboard);
            
            plugin.getLogger().fine("Player " + player.getName() + " added to team: " + targetTeam.getName());
        }
    }
    
    /**
     * Remove a player from all teams
     * @param player The player to remove
     */
    private void removePlayerFromAllTeams(Player player) {
        for (Team team : teams.values()) {
            if (team.hasEntry(player.getName())) {
                team.removeEntry(player.getName());
                
                plugin.getLogger().fine("Player " + player.getName() + " removed from team: " + team.getName());
            }
        }
    }
    
    /**
     * Initialize teams for all online players (useful for plugin reloads)
     */
    public void initializeForOnlinePlayers() {
        plugin.getLogger().info("Initializing teams for online players...");
        
        int count = 0;
        for (Player player : Bukkit.getOnlinePlayers()) {
            updatePlayerTeam(player);
            count++;
        }
        
        plugin.getLogger().info("Initialized teams for " + count + " online players");
    }
    
    /**
     * Clean up teams when plugin is disabled
     */
    public void cleanup() {
        plugin.getLogger().info("Cleaning up teams...");
        
        // Remove players from all teams
        for (Player player : Bukkit.getOnlinePlayers()) {
            removePlayerFromAllTeams(player);
        }
        
        // Unregister teams
        for (Team team : teams.values()) {
            if (team != null) {
                team.unregister();
            }
        }
        
        teams.clear();
        plugin.getLogger().info("Teams cleaned up successfully");
    }
    
    /**
     * Get the main scoreboard
     * @return The main scoreboard
     */
    public Scoreboard getMainScoreboard() {
        return mainScoreboard;
    }
}