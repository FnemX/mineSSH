package com.example.mineSSH;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import com.example.mineSSH.ssh.SSHServer;

import java.util.logging.Logger;

public class MineSSH extends JavaPlugin {
    
    private static final Logger logger = Logger.getLogger("MineSSH");
    private SSHServer sshServer;
    private FileConfiguration config;
    
    @Override
    public void onEnable() {
        // 保存默认配置文件
        saveDefaultConfig();
        config = getConfig();
        
        // 初始化SSH服务器
        sshServer = new SSHServer(this);
        
        // 启动SSH服务器
        if (config.getBoolean("ssh.enabled", true)) {
            int port = config.getInt("ssh.port", 2222);
            if (sshServer.start(port)) {
                logger.info("SSH服务器已启动在端口 " + port);
            } else {
                logger.severe("无法启动SSH服务器");
            }
        }
        
        logger.info("mineSSH插件已启用");
    }
    
    @Override
    public void onDisable() {
        if (sshServer != null) {
            sshServer.stop();
            logger.info("SSH服务器已停止");
        }
        logger.info("mineSSH插件已禁用");
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("minessh")) {
            return false;
        }
        
        if (!sender.hasPermission("minessh.admin")) {
            sender.sendMessage("§c你没有权限使用此命令");
            return true;
        }
        
        if (args.length == 0) {
            sender.sendMessage("§6mineSSH 命令用法:");
            sender.sendMessage("§e/minessh start §7- 启动SSH服务器");
            sender.sendMessage("§e/minessh stop §7- 停止SSH服务器");
            sender.sendMessage("§e/minessh status §7- 查看服务器状态");
            return true;
        }
        
        switch (args[0].toLowerCase()) {
            case "start":
                if (sshServer.isRunning()) {
                    sender.sendMessage("§cSSH服务器已经在运行中");
                } else {
                    int port = config.getInt("ssh.port", 2222);
                    if (sshServer.start(port)) {
                        sender.sendMessage("§aSSH服务器已启动在端口 " + port);
                    } else {
                        sender.sendMessage("§c无法启动SSH服务器");
                    }
                }
                break;
                
            case "stop":
                if (!sshServer.isRunning()) {
                    sender.sendMessage("§cSSH服务器未运行");
                } else {
                    sshServer.stop();
                    sender.sendMessage("§aSSH服务器已停止");
                }
                break;
                
            case "status":
                if (sshServer.isRunning()) {
                    sender.sendMessage("§aSSH服务器正在运行，端口: " + sshServer.getPort());
                } else {
                    sender.sendMessage("§cSSH服务器未运行");
                }
                break;
                
            default:
                sender.sendMessage("§c未知命令: " + args[0]);
                break;
        }
        
        return true;
    }
    
    public SSHServer getSSHServer() {
        return sshServer;
    }
}