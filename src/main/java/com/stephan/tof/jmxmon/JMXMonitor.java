package com.stephan.tof.jmxmon;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.stephan.tof.jmxmon.HttpClientUtils.HttpResult;
import com.stephan.tof.jmxmon.JVMGCGenInfoExtractor.GCGenInfo;
import com.stephan.tof.jmxmon.JVMMemoryUsedExtractor.MemoryUsedInfo;
import com.stephan.tof.jmxmon.JVMThreadExtractor.ThreadInfo;
import com.stephan.tof.jmxmon.bean.FalconItem;
import com.stephan.tof.jmxmon.bean.JacksonUtil;
import com.stephan.tof.jmxmon.jmxutil.ProxyClient;

public class JMXMonitor {

	private static Logger logger = LoggerFactory.getLogger(JMXMonitor.class);
	
	public static void main(String[] args) {
		if (args.length != 1) {
			throw new IllegalArgumentException("Usage: configFile");
		}
		
		try {
			Config.I.init(args[0]);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new IllegalStateException(e);	// 抛出异常便于外部脚本感知
		}
		
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
		executor.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				runTask();
			}
		}, 0, Config.I.getStep(), TimeUnit.SECONDS);
		
	}

	/**
	 * 
	 */
	private static void runTask() {
		try {
			List<FalconItem> items = new ArrayList<FalconItem>();
			
			for (int jmxPort : Config.I.getJmxPorts()) {
				// 从JMX中获取JVM信息
				ProxyClient proxyClient = null;
				try {
					proxyClient = ProxyClient.getProxyClient(Config.I.getJmxHost(), jmxPort, null, null);
					proxyClient.connect();
					
					JMXCall<Map<String, GCGenInfo>> gcGenInfoExtractor = new JVMGCGenInfoExtractor(proxyClient, jmxPort);
					Map<String, GCGenInfo> genInfoMap = gcGenInfoExtractor.call();
					items.addAll(gcGenInfoExtractor.build(genInfoMap));
					
					JMXCall<Double> gcThroughputExtractor = new JVMGCThroughputExtractor(proxyClient, jmxPort);
					Double gcThroughput = gcThroughputExtractor.call();
					items.addAll(gcThroughputExtractor.build(gcThroughput));
					
					JMXCall<MemoryUsedInfo> memoryUsedExtractor = new JVMMemoryUsedExtractor(proxyClient, jmxPort);
					MemoryUsedInfo memoryUsedInfo = memoryUsedExtractor.call();
					items.addAll(memoryUsedExtractor.build(memoryUsedInfo));
					
					JMXCall<ThreadInfo> threadExtractor = new JVMThreadExtractor(proxyClient, jmxPort);
					ThreadInfo threadInfo = threadExtractor.call();
					items.addAll(threadExtractor.build(threadInfo));
				} finally {
					if (proxyClient != null) {
						proxyClient.disconnect();
					}
				}
			}
			
			// 发送items给Openfalcon agent
			String content = JacksonUtil.writeBeanToString(items, false);
			HttpResult postResult = HttpClientUtils.getInstance().post(Config.I.getAgentPostUrl(), content);
			logger.info("post status=" + postResult.getStatusCode() + 
					", post url=" + Config.I.getAgentPostUrl() + ", content=" + content);
			if (postResult.getStatusCode() != HttpClientUtils.okStatusCode ||
					postResult.getT() != null) {
				throw postResult.getT(); 
			}
			
			// 将context数据回写文件
			Config.I.flush();
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
		}
	}

}
