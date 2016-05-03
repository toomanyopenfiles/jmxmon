package com.stephan.tof.jmxmon;

import java.io.IOException;
import java.lang.management.GarbageCollectorMXBean;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.stephan.tof.jmxmon.Constants.CounterType;
import com.stephan.tof.jmxmon.JVMGCGenInfoExtractor.GCGenInfo;
import com.stephan.tof.jmxmon.bean.FalconItem;
import com.stephan.tof.jmxmon.bean.GCData;
import com.stephan.tof.jmxmon.bean.JVMContext;
import com.stephan.tof.jmxmon.jmxutil.ProxyClient;

public class JVMGCGenInfoExtractor extends JVMDataExtractor<Map<String, GCGenInfo>> {

	public JVMGCGenInfoExtractor(ProxyClient proxyClient, int jmxPort) throws IOException {
		super(proxyClient, jmxPort);
	}

	/**
	 * 获取时间窗口内，每代GC的平均耗时 </br>
	 * 返回值：gcMXBean name -> avgGCTime
	 */
	@Override
	public Map<String, GCGenInfo> call() throws Exception {
		Map<String, GCGenInfo> result = new HashMap<String, GCGenInfo>();
		
		JVMContext c = Config.I.getJvmContext();
		for (GarbageCollectorMXBean gcMXBean : getGcMXBeanList()) {
			long gcTotalTime = gcMXBean.getCollectionTime();
			long gcTotalCount = gcMXBean.getCollectionCount();
			
			GCData gcData = c.getJvmData(getJmxPort()).getGcData(gcMXBean.getName());
			long lastGCTotalTime = gcData.getCollectionTime();
			long lastGCTotalCount = gcData.getCollectionCount();
			
			long tmpGCTime = gcTotalTime - lastGCTotalTime;
			long gcCount = gcTotalCount - lastGCTotalCount;
			if (lastGCTotalCount <= 0 || gcCount < 0) {
				gcCount = -1;
			}
			
			double avgGCTime = gcCount > 0 ? tmpGCTime / gcCount : 0;
			
			GCGenInfo gcGenInfo = new GCGenInfo(avgGCTime, gcCount);
			result.put(gcMXBean.getName(), gcGenInfo);
			
			logger.debug("mxbean=" + gcMXBean.getName() + 
					", gcTotalTime=" + gcTotalTime + ", gcTotalCount=" + gcTotalCount + 
					", lastGCTotalTime=" + lastGCTotalTime + ", lastGCTotalCount=" + lastGCTotalCount + 
					", avgGCTime=" + avgGCTime + ", gcCount=" + gcCount);
			
			// update last data
			gcData.setCollectionTime(gcTotalTime);
			gcData.setCollectionCount(gcTotalCount);
			gcData.setUnitTimeCollectionCount(gcCount);
		}
		
		return result;
	}

	@Override
	public List<FalconItem> build(Map<String, GCGenInfo> jmxResultData)
			throws Exception {
		List<FalconItem> items = new ArrayList<FalconItem>();
		
		// 将jvm信息封装成openfalcon格式数据
		for (String gcMXBeanName : jmxResultData.keySet()) {
			FalconItem avgTimeItem = new FalconItem();
			avgTimeItem.setCounterType(CounterType.GAUGE.toString());
			avgTimeItem.setEndpoint(Config.I.getHostname());
			avgTimeItem.setMetric(StringUtils.lowerCase(gcMXBeanName + Constants.metricSeparator + Constants.gcAvgTime));
			avgTimeItem.setStep(Constants.defaultStep);
			avgTimeItem.setTags(StringUtils.lowerCase("jmxport=" + getJmxPort()));	
			avgTimeItem.setTimestamp(System.currentTimeMillis() / 1000);
			avgTimeItem.setValue(jmxResultData.get(gcMXBeanName).getGcAvgTime());
			items.add(avgTimeItem);
			
			FalconItem countItem = new FalconItem();
			countItem.setCounterType(CounterType.GAUGE.toString());
			countItem.setEndpoint(Config.I.getHostname());
			countItem.setMetric(StringUtils.lowerCase(gcMXBeanName + Constants.metricSeparator + Constants.gcCount));
			countItem.setStep(Constants.defaultStep);
			countItem.setTags(StringUtils.lowerCase("jmxport=" + getJmxPort()));	
			countItem.setTimestamp(System.currentTimeMillis() / 1000);
			countItem.setValue(jmxResultData.get(gcMXBeanName).getGcCount());
			items.add(countItem);
		}
		
		return items;
	}
	
	class GCGenInfo {
		private final double gcAvgTime;
		private final long gcCount;
		
		public GCGenInfo(double gcAvgTime, long gcCount) {
			this.gcAvgTime = gcAvgTime;
			this.gcCount = gcCount;
		}

		/**
		 * @return the gcAvgTime
		 */
		public double getGcAvgTime() {
			return gcAvgTime;
		}

		/**
		 * @return the gcCount
		 */
		public long getGcCount() {
			return gcCount;
		}
	}

}
