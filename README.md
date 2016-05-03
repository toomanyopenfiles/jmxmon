# jmxmon 简介
jmxmon是一个基于open-falcon的jmx监控插件，通过这个插件，结合open-falcon agent，可以采集任何开启了JMX服务端口的java进程的服务状态，并将采集信息自动上报给open-falcon服务端

## 主要功能
通过jmx采集java进程的内存、gc、线程等信息。对应用程序无侵入，几乎不占用系统资源。

## jmxmon部署

## 配置说明
配置文件路径为传递给jmxmon程序的唯一一个参数，配置文件内容说明如下：

    # 工作目录用来存放jmxmon的临时缓存文件，注意不要修改此目录下的文件
    workDir=./
    
    # 需要监听的本地jmx端口，支持监听多个端口，多端口用逗号分隔
    jmx.ports=10000,10001,10002,10003
    
    # 本地agent的上报url，如果使用open-falcon的默认配置，则这里不需要改变
    agent.posturl=http://localhost:1988/v1/push
    
    # 可选项：上报给open-falcon的endpoint，默认值为本机hostname。不建议修改
    #hostname=
    
    # 可选项：上报给open-falcon的上报间隔，默认值60，单位秒。不建议修改
    #step=

## 采集指标
| Counters | Type | Notes|
|-----|------|------|
| parnew.gc.avg.time  | GAUGE  | 一分钟内，每次YoungGC(parnew)的平均耗时  |
| concurrentmarksweep.gc.avg.time  | GAUGE  | 一分钟内，每次CMSGC的平均耗时  |
| parnew.gc.count  | GAUGE  | 一分钟内，YoungGC(parnew)的总次数  |
| concurrentmarksweep.gc.count  | GAUGE  | 一分钟内，CMSGC的总次数  |
| gc.throughput  | GAUGE  | GC的总吞吐率（应用运行时间/进程总运行时间）  |
| new.gen.promotion  | GAUGE  | 一分钟内，新生代的内存晋升总大小  |
| new.gen.avg.promotion  | GAUGE  | 一分钟内，平均每次YoungGC的新生代内存晋升大小  |
| old.gen.mem.used  | GAUGE  | 老年代的内存使用量  |
| old.gen.mem.ratio  | GAUGE  | 老年代的内存使用率  |
| thread.active.count  | GAUGE  | 当前活跃线程数  |
| thread.peak.count  | GAUGE  | 峰值线程数  |

## 建议设置监控告警项
-----------------------------
不同应用根据其特点，可以灵活调整触发条件及触发阈值
-----------------------------
| 告警项 | 触发条件 | 备注|
|-----|------|------|
| gc.throughput  | all(#3)<98  | gc吞吐率低于98%，影响性能  |
| old.gen.mem.ratio  | all(#3)>90  | 老年代内存使用率高于90%，需要调优  |
| thread.active.count  | all(#3)>500  | 线程数过多，影响性能  |
