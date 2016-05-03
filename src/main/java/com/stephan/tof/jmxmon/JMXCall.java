package com.stephan.tof.jmxmon;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.stephan.tof.jmxmon.bean.FalconItem;
import com.stephan.tof.jmxmon.jmxutil.ProxyClient;

public abstract class JMXCall<T> {

	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	private final ProxyClient proxyClient;
	private final int jmxPort;
	
	public JMXCall(final ProxyClient proxyClient, int jmxPort) {
		this.proxyClient = proxyClient;
		
		if (jmxPort <= 0) {
			throw new IllegalStateException("jmxPort is 0, client=" + proxyClient.getUrl());
		}
		this.jmxPort = jmxPort;
	}
	
	/**
	 * 调用jmx接口获取数据
	 * 
	 * @return
	 * @throws Exception
	 */
	public abstract T call() throws Exception;
	
	/**
	 * 将jmx获取到的数据组装成Openfalcon数据返回
	 * 
	 * @param jmxResultData
	 * @return
	 * @throws Exception
	 */
	public abstract List<FalconItem> build(T jmxResultData) throws Exception;

	/**
	 * @return the proxyClient
	 */
	public ProxyClient getProxyClient() {
		return proxyClient;
	}

	/**
	 * @return the jmxPort
	 */
	public int getJmxPort() {
		return jmxPort;
	}
}
