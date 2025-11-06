package com.example.mineSSH.ssh;

import com.example.mineSSH.MineSSH;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.shell.ProcessShellFactory;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.logging.Logger;

public class SSHServer {
    
    private final MineSSH plugin;
    private final Logger logger;
    private SshServer sshd;
    private int port;
    private boolean isRunning;
    
    public SSHServer(MineSSH plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.isRunning = false;
    }
    
    public boolean start(int port) {
        if (isRunning) {
            logger.warning("SSH服务器已经在运行中");
            return false;
        }
        
        try {
            sshd = SshServer.setUpDefaultServer();
            sshd.setPort(port);
            this.port = port;
            
            // 设置主机密钥
            sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider(Paths.get("hostkey.ser")));
            
            // 设置密码认证
            sshd.setPasswordAuthenticator((username, password, session) -> {
                // 简单的认证逻辑 - 在实际使用中应该更安全
                return "admin".equals(username) && "admin".equals(password);
            });
            
            // 设置命令工厂 - 使用Minecraft命令处理器
            sshd.setShellFactory(new MinecraftShellFactory(plugin));
            
            sshd.start();
            isRunning = true;
            logger.info("SSH服务器启动成功，端口: " + port);
            return true;
            
        } catch (IOException e) {
            logger.severe("启动SSH服务器失败: " + e.getMessage());
            return false;
        }
    }
    
    public void stop() {
        if (!isRunning || sshd == null) {
            return;
        }
        
        try {
            sshd.stop();
            isRunning = false;
            logger.info("SSH服务器已停止");
        } catch (IOException e) {
            logger.severe("停止SSH服务器失败: " + e.getMessage());
        }
    }
    
    public boolean isRunning() {
        return isRunning;
    }
    
    public int getPort() {
        return port;
    }
}