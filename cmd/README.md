# mineSSH - Minecraft SSH Plugin | Minecraft SSH插件
一个支持Minecraft Paper 1.21+版本的SSH插件，允许通过SSH远程连接管理Minecraft服务器。  
A SSH plugin for Minecraft Paper 1.21+ that allows remote server management via SSH.
---
## 功能特性 | Features
- 🔐 安全的SSH远程连接 | Secure SSH remote connection
- 🎮 执行Minecraft服务器命令 | Execute Minecraft server commands
- 👥 查看在线玩家信息 | View online player information
- ⏰ 获取服务器状态和时间 | Get server status and time
- 📡 支持自定义端口和认证 | Custom port and authentication support
- 🔒 可配置的命令权限控制 | Configurable command permission control
---
## 安装要求 | Requirements
- Minecraft Paper服务器 1.21+ | Minecraft Paper Server 1.21+
- Java 21+ | Java 21+
---
## 安装方法 | Installation
1. 下载最新的 `mineSSH.jar` 文件 | Download the latest `mineSSH.jar`
2. 将文件放入服务器的 `plugins` 文件夹 | Place it into the server's `plugins` folder
3. 重启服务器 | Restart the server
4. 插件会自动生成配置文件 | The plugin will auto-generate its config file
---
## 配置说明 | Configuration
编辑 `plugins/mineSSH/config.yml` 文件 | Edit `plugins/mineSSH/config.yml`:

---

## 配置说明 | Configuration

编辑 `plugins/mineSSH/config.yml` 文件 | Edit `plugins/mineSSH/config.yml`:

```yaml
# SSH服务器设置 | SSH Server Settings
ssh:
  enabled: true           # 是否启用SSH服务器 | Enable SSH server
  port: 2222             # SSH服务器端口 | SSH server port
  authentication:
    username: "admin"    # SSH登录用户名 | SSH login username
    password: "admin"    # SSH登录密码 | SSH login password

# 命令权限控制 | Command Permission Control
commands:
  allowed-commands: []    # 允许的命令列表（空表示允许所有） | Allowed commands (empty means all allowed)
  denied-commands:        # 禁止的命令列表 | Denied commands
    - "op"
    - "deop"
    - "stop"
    - "reload"
```

---

## 使用方法 | Usage

### 游戏内命令 | In-game Commands

```
/minessh start     # 启动SSH服务器 | Start SSH server
/minessh stop      # 停止SSH服务器 | Stop SSH server
/minessh status    # 查看SSH服务器状态 | Check SSH server status
```

### SSH连接 | SSH Connection

使用SSH客户端连接服务器 | Connect to server using SSH client:

```bash
ssh admin@服务器IP -p 2222
密码: admin
```

### SSH会话中的可用命令 | Available Commands in SSH Session

- `list` - 显示在线玩家列表 | Show online players list
- `time` - 显示服务器时间 | Show server time
- `players` - 显示玩家详细信息 | Show detailed player information
- `say <消息>` - 广播消息 | Broadcast message
- `help` - 显示帮助信息 | Show help information
- 任何有效的Minecraft命令 | Any valid Minecraft command
- `exit` 或 `quit` - 退出SSH会话 | Exit SSH session

---

## 安全建议 | Security Recommendations

1. **修改默认密码**：首次使用后立即修改配置文件中的密码 | **Change default password**: Modify the password in config file immediately after first use
2. **使用防火墙**：限制SSH端口的访问IP | **Use firewall**: Restrict access to SSH port by IP
3. **定期更新**：保持插件为最新版本 | **Regular updates**: Keep the plugin up to date
4. **监控日志**：定期检查SSH连接日志 | **Monitor logs**: Regularly check SSH connection logs

---

## 故障排除 | Troubleshooting

### SSH连接失败 | SSH Connection Failed
- 检查防火墙设置 | Check firewall settings
- 确认端口未被占用 | Confirm port is not occupied
- 验证用户名和密码 | Verify username and password

### 命令执行失败 | Command Execution Failed
- 检查命令权限配置 | Check command permission configuration
- 查看服务器日志获取详细信息 | Check server logs for detailed information

---

## 开发构建 | Development Build

使用Maven构建插件 | Build plugin using Maven:

```bash
mvn clean package
```

构建完成后，在 `target` 文件夹中找到 `mineSSH.jar` 文件。  
After building, find the `mineSSH.jar` file in the `target` folder.

---

## 许可证 | License

本项目采用MIT许可证。  
This project is licensed under the MIT License.

---

## 支持 | Support

如有问题或建议，请提交Issue或联系开发者。  
If you have questions or suggestions, please submit an Issue or contact the developer.