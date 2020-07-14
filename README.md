# scord
a spigot plugin to count miner's work

## logic

## command

- /scord on|off
- /scord player [player] config|score [exp]
- /scord board title|numberprefix|nameprefix|maxiumleaders set [exp]
- /scord blacklist add|remove|find|isin [player]

## permission

- scord.*
    - scord.basic: player
    - scord.set: op

## config

```yaml
# Scord 插件配置文件
# 可以使用&来代替minecraft样式代码中的§
title: &b挖掘榜 # 显示挖掘榜的名字
maxmiumleaders: 5 # 挖掘榜上最大显示人数
numberprefix: &6No.
nameprefix: " &4"
scoreprefix: " §e"
blacklist: # 你不希望在列表上显示的人
  - herobine
  - jeb_
  - norch
```