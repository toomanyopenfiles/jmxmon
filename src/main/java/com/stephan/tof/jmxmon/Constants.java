package com.stephan.tof.jmxmon;

public class Constants {

	public static enum CounterType { COUNTER, GAUGE }
	
	public static final String gcAvgTime 					= "gc.avg.time";
	public static final String gcCount	 					= "gc.count";
	public static final String gcThroughput 				= "gc.throughput";
	public static final String newGenPromotion              = "new.gen.promotion";
	public static final String newGenAvgPromotion           = "new.gen.avg.promotion";
	public static final String oldGenMemUsed 				= "old.gen.mem.used";
	public static final String oldGenMemRatio 				= "old.gen.mem.ratio";
	public static final String threadActiveCount			= "thread.active.count";
	public static final String threadPeakCount 				= "thread.peak.count";
	
	public static final String tagSeparator 			= ",";
	public static final String metricSeparator 			= ".";
	public static final int defaultStep 				= 60; // 单位秒
}
