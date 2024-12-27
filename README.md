# Stress-Testing-Agent
[![GitHub stars](https://img.shields.io/github/stars/caijianying/Stress-Testing-Agent.svg?style=badge&label=Stars&logo=github)](https://github.com/caijianying/Stress-Testing-Agent)
[![GitHub forks](https://img.shields.io/github/stars/caijianying/Stress-Testing-Agent.svg?style=badge&label=Fork&logo=github)](https://github.com/caijianying/Stress-Testing-Agent)
[![AUR](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg)](https://github.com/caijianying/Stress-Testing-Agent/blob/main/LICENSE)
[![](https://img.shields.io/badge/Author-小白菜-orange.svg)](https://caijianying.github.io)

# 介绍
基于SkyWalking搭建的全链路压测Agent，该项目请勿用于商业用途

# 主要特性
* 拦截网络请求，识别压测标
* 压测标透传
* 对Mysql可进行影子库、影子表的写入，适用于大部分简单场景
* 链路梳理，简单打印trace链路
* 熔断处理，请求时间过长会通知停止压测

# 实现原理及关键技术
* JavaAgent的Attach机制
* 字节码框架 ByteBuddy
* SkyWalking

# 运行环境
* JDK 17
* Gradle 8.5

## 最后
若觉得有方便到您，欢迎Star哦.若有建议，请提Issue.大家共同进步！

