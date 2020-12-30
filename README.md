# Server-Monitor
基于telnet的轻量化定时检测公司各服务是否正常运行的java程序，异常/恢复时 系统利用邮件通知

## 判定规则十分简单
* **服务正常：** telnet能连通
* **服务异常：** telnet无法连通

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

# 配置邮件服务器
mail.host=smtp.exmail.qq.com
mail.port=465
mail.sslEnable=true
mail.from=robot@jianghai56.com
mail.auth=true
mail.user=robot@jianghai56.com
mail.pass=xingyuanD908

# 定义webhook通知：
# web回调参数支持：
#    {{user|}} 用户列表，“user|”代表多个用户之间用|分开，如果用逗号分开可以改成{{user,}}，其中user代表每个用户，不能修改
#    {{msg}} 消息内容
#    {{msg}} 消息内容
#    {{desc}} 系统拼接的描述信息（包含了下面几个字段，通常情况没有特殊要求时，用此参数就够用了）
#    {{ip}} 宕机的ip
#    {{port}} 宕机的端口
#    {{affects}} 受影响的服务
#    {{down_time}} 宕机时间
#    {{status}} 状态
#    {{current_time}} 当前时间
webhooks.qiyeweixin=http://127.0.0.1:9001/send?touser={{user|}}&content={{msg}}\n\n{{desc}}

# 配置1个用户，mail代表此用户支持邮件通知、webhoot代表此用户支持网页回调通知，可以两个都配置（代表宕机后，系统会通过2个途径发出通知）
users.sunzsh.mail=cn.xiaoshan@gmail.com
users.sunzsh.webhook=qiyeweixin.sunzsh

# 再配置1个用户
users.zhangsan.mail=s.zs@qq.com
users.zhangsan.webhook=qiyeweixin.zhangsan

# 配置分组（用于按分组通知）
groups.admin=zhangsan

[springboot订单服务]
ip=127.0.0.1
port=8080
affects=移动端商场（受影响服务自己根据实际情况随便写，仅用作系统通知时候的展示）
users=sunzsh
groups=admin



```
