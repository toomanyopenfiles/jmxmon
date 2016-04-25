# jmxmon 简介
jmxmon是一个基于open-falcon的jmx监控插件，通过这个插件，结合open-falcon agent，可以采集任何开启了JMX服务端口的java进程的服务状态，并将采集信息自动上报给open-falcon服务端

## 主要功能
采集java进程的内存、gc、线程等信息

## jmxmon部署


## 采集指标
| Counters | Type | Notes|
|-----|------|------|
| parnew.gc.avg.time  | GAUGE  | 一分钟内，每次YoungGC(parnew)的平均耗时  |
| concurrentmarksweep.gc.avg.time  | GAUGE  | 一分钟内，每次CMSGC的平均耗时  |
| parnew.gc.count  | GAUGE  | 一分钟内，YoungGC(parnew)的总次数  |
| concurrentmarksweep.gc.count  | GAUGE  | 一分钟内，每次CMSGC的总次数  |
| gc.throughput  | GAUGE  | GC的总吞吐率（不包含gc的进程运行时间/进程总运行时间）  |
| new.gen.promotion  | GAUGE  | 一分钟内，新生代的内存晋升总大小  |
| new.gen.avg.promotion  | GAUGE  | 一分钟内，平均每次YoungGC的新生代内存晋升大小  |
| old.gen.mem.ratio  | GAUGE  | 老年代的内存使用率  |
| thread.active.count  | GAUGE  | 活跃线程数  |
| thread.peak.count  | GAUGE  | 峰值线程数  |

## 建议设置监控告警项

