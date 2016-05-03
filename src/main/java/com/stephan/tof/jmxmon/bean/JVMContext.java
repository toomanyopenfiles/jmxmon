package com.stephan.tof.jmxmon.bean;

import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 用来缓存通过JMX获取到的JVM数据，每次程序退出时需要将此对象序列化到文件中，以便下次启动时能够再次加载到内存中使用
 * 
 * @author Stephan gao
 * @since 2016年4月26日
 *
 */
public class JVMContext {

	/**
	 * port -> jvm data
	 */
	@JsonProperty
	private Map<Integer, JVMData> jvmDatas = new LinkedHashMap<Integer, JVMData>();

	public JVMData getJvmData(Integer jmxPort) {
		if(jvmDatas.containsKey(jmxPort)) {
			return jvmDatas.get(jmxPort);
		} else {
			JVMData jvmData = new JVMData();
			jvmDatas.put(jmxPort, jvmData);
			return jvmData;
		}
	}
	
}
