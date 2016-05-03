package com.stephan.tof.jmxmon.bean;

import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

public class JVMData {

	/**
	 * GCMXBean name -> GCData 
	 */
	@JsonProperty
	private Map<String, GCData> gcDatas = new LinkedHashMap<String, GCData>();

	public GCData getGcData(String beanName) {
		if (gcDatas.containsKey(beanName)) {
			return gcDatas.get(beanName);
		} else {
			GCData gcData = new GCData();
			gcDatas.put(beanName, gcData);
			return gcData;
		}
	}
	
}
