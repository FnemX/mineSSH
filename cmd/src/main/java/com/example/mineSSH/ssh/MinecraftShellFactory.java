package com.example.mineSSH.ssh;

import com.example.mineSSH.MineSSH;
import org.apache.sshd.server.Environment;
import org.apache.sshd.server.ExitCallback;
import org.apache.sshd.server.channel.ChannelSession;
import org.apache.sshd.server.command.Command;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MinecraftShellFactory implements org.apache.sshd.server.shell.ShellFactory {
    
    private final MineSSH plugin;
    
    public MinecraftShellFactory(MineSSH plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public Command createShell(ChannelSession channel) {
        return new MinecraftCommand(plugin);
    }
    
    public static class MinecraftCommand implements Command, Runnable {
        
        private final MineSSH plugin;
        private InputStream in;
        private OutputStream out;
        private OutputStream err;
        private ExitCallback callback;
        private ExecutorService executor;
        private BufferedReader reader;
        private PrintWriter writer;
        
        public MinecraftCommand(MineSSH plugin) {
            this.plugin = plugin;
        }
        
        @Override
        public void setInputStream(InputStream in) {
            this.in = in;
        }
        
        @Override
        public void setOutputStream(OutputStream out) {
            this.out = out;
        }
        
        @Override
        public void setErrorStream(OutputStream err) {
            this.err = err;
        }
        
        @Override
        public void setExitCallback(ExitCallback callback) {
            this.callback = callback;
        }
        
        @Override
        public void start(ChannelSession channel, Environment env) throws IOException {
            reader = new BufferedReader(new InputStreamReader(in));
            writer = new PrintWriter(out, true);
            
            // 显示欢迎信息
            writer.println("=== Minecraft SSH Console ===");
            writer.println("连接到服务器: " + Bukkit.getServer().getName());
            writer.println("在线玩家: " + Bukkit.getOnlinePlayers().size());
            writer.println("输入 'help' 查看可用命令");
            writer.print("minecraft> ");
            
            executor = Executors.newSingleThreadExecutor();
            executor.execute(this);
        }
        
        @Override
        public void run() {
            try {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.trim().equalsIgnoreCase("exit") || line.trim().equalsIgnoreCase("quit")) {
                        writer.println("再见!");
                        break;
                    }
                    
                    // 在主线程中执行Minecraft命令
                    final String command = line.trim();
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        try {
                            if (command.equalsIgnoreCase("help")) {
                                writer.println("可用命令:");
                                writer.println("  list - 显示在线玩家");
                                writer.println("  time - 显示服务器时间");
                                writer.println("  say <消息> - 广播消息");
                                writer.println("  players - 显示玩家详情");
                                writer.println("  exit/quit - 退出SSH会话");
                                writer.println("  任何有效的Minecraft命令");
                            } else if (command.equalsIgnoreCase("list")) {
                                writer.println("在线玩家 (" + Bukkit.getOnlinePlayers().size() + "):");
                                for (Player player : Bukkit.getOnlinePlayers()) {
                                    writer.println("  " + player.getName() + " - " + 
                                        player.getLocation().getBlockX() + "," + 
                                        player.getLocation().getBlockY() + "," + 
                                        player.getLocation().getBlockZ());
                                }
                            } else if (command.equalsIgnoreCase("time")) {
                                long time = Bukkit.getWorlds().get(0).getTime();
                                writer.println("服务器时间: " + time + " (" + formatTime(time) + ")");
                            } else if (command.equalsIgnoreCase("players")) {
                                writer.println("=== 玩家详情 ===");
                                for (Player player : Bukkit.getOnlinePlayers()) {
                                    writer.println("名称: " + player.getName());
                                    writer.println("  UUID: " + player.getUniqueId());
                                    writer.println("  位置: " + formatLocation(player.getLocation()));
                                    writer.println("  生命值: " + player.getHealth() + "/" + player.getMaxHealth());
                                    writer.println("  等级: " + player.getLevel());
                                    writer.println("---");
                                }
                            } else if (command.toLowerCase().startsWith("say ")) {
                                String message = command.substring(4);
                                if (!message.isEmpty()) {
                                    Bukkit.broadcastMessage("[SSH] " + message);
                                    writer.println("消息已广播");
                                }
                            } else {
                                // 执行Minecraft命令
                                boolean success = Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                                if (success) {
                                    writer.println("命令执行成功");
                                } else {
                                    writer.println("命令执行失败或无效");
                                }
                            }
                        } catch (Exception e) {
                            writer.println("错误: " + e.getMessage());
                        }
                        
                        writer.print("minecraft> ");
                    });
                }
            } catch (IOException e) {
                plugin.getLogger().warning("SSH会话错误: " + e.getMessage());
            } finally {
                if (executor != null) {
                    executor.shutdown();
                }
                callback.onExit(0);
            }
        }
        
        @Override
        public void destroy(ChannelSession channel) throws Exception {
            if (executor != null) {
                executor.shutdownNow();
            }
        }
        
        private String formatTime(long time) {
            int hours = (int) ((time / 1000 + 6) % 24);
            int minutes = (int) ((time % 1000) * 60 / 1000);
            return String.format("%02d:%02d", hours, minutes);
        }
        
        private String formatLocation(org.bukkit.Location location) {
            return location.getWorld().getName() + " " + 
                   location.getBlockX() + "," + 
                   location.getBlockY() + "," + 
                   location.getBlockZ();
        }
    }
}