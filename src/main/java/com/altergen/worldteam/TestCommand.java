package com.altergen.worldteam;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Team;

/**
 * Test command for debugging WorldTeam plugin
 * 
 * @author altergen
 */
public class TestCommand implements CommandExecutor {
    
    private final WorldTeam plugin;
    
    /**
     * Constructor for TestCommand
     * @param plugin The plugin instance
     */
    public TestCommand(WorldTeam plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            sendHelp(sender);
            return true;
        }
        
        switch (args[0].toLowerCase()) {
            case "status":
                return handleStatusCommand(sender, args);
            case "reload":
                return handleReloadCommand(sender);
            case "test":
                return handleTestCommand(sender);
            default:
                sender.sendMessage(ChatColor.RED + "Unknown command. Use /worldteam help for help.");
                return true;
        }
    }
    
    /**
     * Send help message
     * @param sender Command sender
     */
    private void sendHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "=== WorldTeam Commands ===");
        sender.sendMessage(ChatColor.YELLOW + "/worldteam help - Show this help");
        sender.sendMessage(ChatColor.YELLOW + "/worldteam status [player] - Show team status");
        sender.sendMessage(ChatColor.YELLOW + "/worldteam reload - Reload team configuration");
        sender.sendMessage(ChatColor.YELLOW + "/worldteam test - Test plugin functionality");
        
        if (sender instanceof ConsoleCommandSender || sender.hasPermission("worldteam.admin")) {
            sender.sendMessage(ChatColor.YELLOW + "/worldteam debug - Toggle debug mode");
        }
    }
    
    /**
     * Handle status command
     * @param sender Command sender
     * @param args Command arguments
     * @return true if command was handled
     */
    private boolean handleStatusCommand(CommandSender sender, String[] args) {
        if (args.length > 1 && sender.hasPermission("worldteam.admin")) {
            // Check specific player
            Player target = plugin.getServer().getPlayer(args[1]);
            if (target != null) {
                sendPlayerStatus(sender, target);
            } else {
                sender.sendMessage(ChatColor.RED + "Player not found: " + args[1]);
            }
        } else if (sender instanceof Player) {
            // Check sender's own status
            sendPlayerStatus(sender, (Player) sender);
        } else {
            sender.sendMessage(ChatColor.RED + "Usage: /worldteam status [player]");
        }
        return true;
    }
    
    /**
     * Send player status information
     * @param sender Command sender
     * @param player The player to check
     */
    private void sendPlayerStatus(CommandSender sender, Player player) {
        TeamManager teamManager = plugin.getTeamManager();
        if (teamManager == null) {
            sender.sendMessage(ChatColor.RED + "Team manager not initialized!");
            return;
        }
        
        Team team = teamManager.getTeamForPlayer(player);
        if (team != null) {
            sender.sendMessage(ChatColor.GREEN + "Player: " + ChatColor.WHITE + player.getName());
            sender.sendMessage(ChatColor.GREEN + "World: " + ChatColor.WHITE + player.getWorld().getName());
            sender.sendMessage(ChatColor.GREEN + "Environment: " + ChatColor.WHITE + player.getWorld().getEnvironment());
            sender.sendMessage(ChatColor.GREEN + "Team: " + ChatColor.WHITE + team.getName());
            sender.sendMessage(ChatColor.GREEN + "Team Prefix: " + team.getPrefix());
        } else {
            sender.sendMessage(ChatColor.RED + "Player is not in any team!");
        }
    }
    
    /**
     * Handle reload command
     * @param sender Command sender
     * @return true if command was handled
     */
    private boolean handleReloadCommand(CommandSender sender) {
        if (!sender.hasPermission("worldteam.reload")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
            return true;
        }
        
        TeamManager teamManager = plugin.getTeamManager();
        if (teamManager != null) {
            // Clean up and reinitialize
            teamManager.cleanup();
            
            // Reinitialize teams
            teamManager.initializeForOnlinePlayers();
            
            sender.sendMessage(ChatColor.GREEN + "WorldTeam teams have been reloaded!");
        } else {
            sender.sendMessage(ChatColor.RED + "Team manager not initialized!");
        }
        return true;
    }
    
    /**
     * Handle test command
     * @param sender Command sender
     * @return true if command was handled
     */
    private boolean handleTestCommand(CommandSender sender) {
        if (!sender.hasPermission("worldteam.test")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
            return true;
        }
        
        sender.sendMessage(ChatColor.GOLD + "=== WorldTeam Test ===");
        sender.sendMessage(ChatColor.GREEN + "Plugin Version: " + ChatColor.WHITE + plugin.getDescription().getVersion());
        sender.sendMessage(ChatColor.GREEN + "Author: " + ChatColor.WHITE + plugin.getDescription().getAuthors());
        sender.sendMessage(ChatColor.GREEN + "Java Version: " + ChatColor.WHITE + System.getProperty("java.version"));
        
        TeamManager teamManager = plugin.getTeamManager();
        if (teamManager != null) {
            sender.sendMessage(ChatColor.GREEN + "Teams Initialized: " + ChatColor.WHITE + "Yes");
            sender.sendMessage(ChatColor.GREEN + "Online Players: " + ChatColor.WHITE + plugin.getServer().getOnlinePlayers().size());
        } else {
            sender.sendMessage(ChatColor.RED + "Team manager not initialized!");
        }
        
        sender.sendMessage(ChatColor.GOLD + "=====================");
        return true;
    }
}