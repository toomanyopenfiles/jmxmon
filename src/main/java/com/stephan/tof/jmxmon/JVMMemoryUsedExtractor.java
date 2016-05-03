package com.stephan.tof.jmxmon;

import java.io.IOException;
import java.lang.management.GarbageCollectorMXBean;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.stephan.tof.jmxmon.Constants.CounterType;
import com.stephan.tof.jmxmon.JVMMemoryUsedExtractor.MemoryUsedInfo;
import com.stephan.tof.jmxmon.bean.FalconItem;
import com.stephan.tof.jmxmon.bean.GCData;
import com.stephan.tof.jmxmon.bean.JVMContext;
import com.stephan.tof.jmxmon.jmxutil.MemoryPoolProxy;
import com.stephan.tof.jmxmon.jmxutil.ProxyClient;

public class JVMMemoryUsedExtractor extends JVMDataExtractor<MemoryUsedInfo> {

	public JVMMemoryUsedExtractor(ProxyClient proxyClient, int jmxPort)
			throws IOException {
		super(proxyClient, jmxPort);
	}

	@Override
	public MemoryUsedInfo call() throws Exception {
		long oldGenUsed = 0;
		long maxOldGenMemory = 0;
		Collection<MemoryPoolProxy> memoryPoolList = getMemoryPoolList();
		for (MemoryPoolProxy memoryPool : memoryPoolList) {
			String poolName = memoryPool.getStat().getPoolName();
			// see: http://stackoverflow.com/questions/16082004/how-to-identify-tenured-space/16083569#16083569
			if (poolName.contains("Old Gen") || poolName.contains("Tenured Gen")) {
				oldGenUsed = memoryPool.getStat().getUsage().getUsed();
				maxOldGenMemory = memoryPool.getStat().getUsage().getMax();
				break;
			}
		}
		double oldGenUsedRatio = maxOldGenMemory > 0 ? oldGenUsed * 100d / maxOldGenMemory : 0;
		
		// see: http://stackoverflow.com/questions/32002001/how-to-get-minor-and-major-garbage-collection-count-in-jdk-7-and-jdk-8
		JVMContext c = Config.I.getJvmContext();
		GarbageCollectorMXBean[] gcMXBeanArray = getGcMXBeanList().toArray(new GarbageCollectorMXBean[0]);

		// 在一个上报周期内，老年代的内存变化大小~=新生代晋升大小
		GarbageCollectorMXBean majorGCMXBean = gcMXBeanArray[1];
		GCData majorGcData = c.getJvmData(getJmxPort()).getGcData(majorGCMXBean.getName());
		long lastOldGenMemoryUsed = majorGcData.getMemoryUsed();
		long newGenPromotion = oldGenUsed - lastOldGenMemoryUsed;
		if (lastOldGenMemoryUsed <= 0 || newGenPromotion < 0) {
			newGenPromotion = -1;
		}
		
		// 在一个上报周期内，YGC次数
		GarbageCollectorMXBean minorGCMXBean = gcMXBeanArray[0];
		GCData minorGcData = c.getJvmData(getJmxPort()).getGcData(minorGCMXBean.getName());
		long gcCount = minorGcData.getUnitTimeCollectionCount();
		
		long newGenAvgPromotion = 0;
		if (gcCount > 0 && newGenPromotion > 0) {
			newGenAvgPromotion = (long) (newGenPromotion / gcCount);
		}
		
		MemoryUsedInfo memoryUsedInfo = new MemoryUsedInfo(oldGenUsed, oldGenUsedRatio, newGenPromotion, newGenAvgPromotion);
		
		// update last data
		majorGcData.setMemoryUsed(oldGenUsed);
		
		return memoryUsedInfo;
	}

	@Override
	public List<FalconItem> build(MemoryUsedInfo jmxResultData) throws Exception {
		List<FalconItem> items = new ArrayList<FalconItem>();
		
		// 将jvm信息封装成openfalcon格式数据
		FalconItem oldGenUsedItem = new FalconItem();
		oldGenUsedItem.setCounterType(CounterType.GAUGE.toString());
		oldGenUsedItem.setEndpoint(Config.I.getHostname());
		oldGenUsedItem.setMetric(StringUtils.lowerCase(Constants.oldGenMemUsed));
		oldGenUsedItem.setStep(Constants.defaultStep);
		oldGenUsedItem.setTags(StringUtils.lowerCase("jmxport=" + getJmxPort()));	
		oldGenUsedItem.setTimestamp(System.currentTimeMillis() / 1000);
		oldGenUsedItem.setValue(jmxResultData.getOldGenUsed());
		items.add(oldGenUsedItem);
		
		FalconItem oldGenUsedRatioItem = new FalconItem();
		oldGenUsedRatioItem.setCounterType(CounterType.GAUGE.toString());
		oldGenUsedRatioItem.setEndpoint(Config.I.getHostname());
		oldGenUsedRatioItem.setMetric(StringUtils.lowerCase(Constants.oldGenMemRatio));
		oldGenUsedRatioItem.setStep(Constants.defaultStep);
		oldGenUsedRatioItem.setTags(StringUtils.lowerCase("jmxport=" + getJmxPort()));	
		oldGenUsedRatioItem.setTimestamp(System.currentTimeMillis() / 1000);
		oldGenUsedRatioItem.setValue(jmxResultData.getOldGenUsedRatio());
		items.add(oldGenUsedRatioItem);
		
		FalconItem newGenPromotionItem = new FalconItem();
		newGenPromotionItem.setCounterType(CounterType.GAUGE.toString());
		newGenPromotionItem.setEndpoint(Config.I.getHostname());
		newGenPromotionItem.setMetric(StringUtils.lowerCase(Constants.newGenPromotion));
		newGenPromotionItem.setStep(Constants.defaultStep);
		newGenPromotionItem.setTags(StringUtils.lowerCase("jmxport=" + getJmxPort()));	
		newGenPromotionItem.setTimestamp(System.currentTimeMillis() / 1000);
		newGenPromotionItem.setValue(jmxResultData.getNewGenPromotion());
		items.add(newGenPromotionItem);
		
		FalconItem newGenAvgPromotionItem = new FalconItem();
		newGenAvgPromotionItem.setCounterType(CounterType.GAUGE.toString());
		newGenAvgPromotionItem.setEndpoint(Config.I.getHostname());
		newGenAvgPromotionItem.setMetric(StringUtils.lowerCase(Constants.newGenAvgPromotion));
		newGenAvgPromotionItem.setStep(Constants.defaultStep);
		newGenAvgPromotionItem.setTags(StringUtils.lowerCase("jmxport=" + getJmxPort()));	
		newGenAvgPromotionItem.setTimestamp(System.currentTimeMillis() / 1000);
		newGenAvgPromotionItem.setValue(jmxResultData.getNewGenAvgPromotion());
		items.add(newGenAvgPromotionItem);
		
		return items;
	}

	class MemoryUsedInfo {
		private final double oldGenUsedRatio;
		private final long oldGenUsed;
		private final long newGenPromotion;
		private final long newGenAvgPromotion;
		
		public MemoryUsedInfo(long oldGenUsed, double oldGenUsedRatio, long newGenPromotion, long newGenAvgPromotion) {
			this.oldGenUsed = oldGenUsed;
			this.oldGenUsedRatio = oldGenUsedRatio;
			this.newGenPromotion = newGenPromotion;
			this.newGenAvgPromotion = newGenAvgPromotion;
		}

		/**
		 * @return the oldGenUsedRatio
		 */
		public double getOldGenUsedRatio() {
			return oldGenUsedRatio;
		}

		/**
		 * @return the oldGenUsed
		 */
		public long getOldGenUsed() {
			return oldGenUsed;
		}

		/**
		 * @return the newGenPromotion
		 */
		public long getNewGenPromotion() {
			return newGenPromotion;
		}

		/**
		 * @return the newGenAvgPromotion
		 */
		public long getNewGenAvgPromotion() {
			return newGenAvgPromotion;
		}
		
		
	}
}
