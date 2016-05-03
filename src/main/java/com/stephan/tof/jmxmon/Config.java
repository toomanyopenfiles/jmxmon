package com.stephan.tof.jmxmon;

import java.io.File;
import java.io.IOException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.stephan.tof.jmxmon.bean.JVMContext;
import com.stephan.tof.jmxmon.bean.JacksonUtil;

public class Config {

	public static final Config I = new Config();
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private String workDir;
	private File jvmContextFile;	// 用来存放JVM数据文件（比如上一次的gc总耗时、gc总次数等），文件保存在workDir下
	private JVMContext jvmContext = new JVMContext();	// JVM数据文件对象
	
	private String hostname;
	private String agentPostUrl;
	private int step;
	
	private String jmxHost;
	private int[] jmxPorts;
	
	private Config(){}
	
	public void init(String configPath) throws ConfigurationException, IOException{
		logger.info("init config");
		
		PropertiesConfiguration config = new PropertiesConfiguration(configPath);
		config.setThrowExceptionOnMissing(true);
		
		this.workDir = config.getString("workDir");
		if (new File(workDir).isDirectory() == false) {
			throw new IllegalArgumentException("workDir is not a directory");
		}
		
		this.hostname = config.getString("hostname", Utils.getHostNameForLinux());
		
		this.jvmContextFile = new File(workDir, "jmxmon.jvm.context.json");
		
		if (jvmContextFile.exists() && jvmContextFile.isFile() && 
				jvmContextFile.length() > 0) {
			logger.info(jvmContextFile.getAbsolutePath() + " is exist, start loading...");
			this.jvmContext = JacksonUtil.readBeanFromFile(jvmContextFile, JVMContext.class);
		} else {
			logger.info(jvmContextFile.getAbsolutePath() + " is not exist");
		}
		
		this.agentPostUrl = config.getString("agent.posturl");
		this.step = config.getInt("step", Constants.defaultStep);
		
		// 默认的jmxHost为localhost，除非通过-D参数设置（线上不建议以远程方式采集，最好每台机器上部署agent，这样agent才能水平伸缩）
		this.jmxHost = System.getProperty("debug.jmx.host");
		if (this.jmxHost == null) {
			this.jmxHost = "localhost";
		}
		
		String[] jmxPortArray = config.getStringArray("jmx.ports");
		jmxPorts = new int[jmxPortArray.length];
		for (int i = 0; i < jmxPortArray.length; i++) {
			jmxPorts[i] = Integer.parseInt(jmxPortArray[i]);
		}
		
		logger.info("init config ok");
	}
	
	/**
	 * 保存数据文件
	 * @throws IOException 
	 */
	public void flush() throws IOException {
		JacksonUtil.writeBeanToFile(jvmContextFile, jvmContext, true);
	}

	/**
	 * @return the workDir
	 */
	public String getWorkDir() {
		return workDir;
	}

	/**
	 * @return the hostname
	 */
	public String getHostname() {
		return hostname;
	}

	/**
	 * @return the agentPostUrl
	 */
	public String getAgentPostUrl() {
		return agentPostUrl;
	}

	/**
	 * @return the step
	 */
	public int getStep() {
		return step;
	}

	/**
	 * @return the jmxHost
	 */
	public String getJmxHost() {
		return jmxHost;
	}

	/**
	 * @return the jmxPorts
	 */
	public int[] getJmxPorts() {
		return jmxPorts;
	}

	/**
	 * @return the jvmContext
	 */
	public JVMContext getJvmContext() {
		return jvmContext;
	}
}
