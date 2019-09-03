# Server-Monitor
基于telnet的轻量化定时检测公司各服务是否正常运行的java程序，异常/恢复时 系统利用邮件通知

## 使用方法
1. 使用maven package打包后，会生成一个 `server-monitor-xxx-jar-with-dependencies.jar` 文件，重命名成简单一点的文件名（或者从release中直接下载已经打包好的jar包）
2. 在jar包相同目录下，添加配置文件：`config.conf`，配置说明见下方
3. 命令：`java -jar xxxx.jar`


## 配置文件 `config.conf` 说明
1. 配置应用名`application.name`，此名称将体现在邮件提醒的标题中
2. 配置发件账户 `mail.xxx`
3. 配置多个服务，每个服务名必须写在 `[ ]` 中
4. 每个服务必须包含`ip`和`port`
5. 每个服务中的affects配置表示当前服务如果停止，将影响的产品或其他服务    

### 样例  
```
# 应用名将会体现在邮件通知的标题前缀中
application.name=汇智监控

# mail.to_users可以是多个，用逗号隔开
mail.to_users=

# 配置邮件服务器
mail.host=
mail.port=
mail.sslEnable=
mail.from=
mail.auth=
mail.user=
mail.pass=


[服务名1]
ip=
port=
affects=

[服务名2]
ip=
port=
affects=
```
