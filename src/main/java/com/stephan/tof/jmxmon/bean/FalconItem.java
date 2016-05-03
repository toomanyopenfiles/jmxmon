package com.stephan.tof.jmxmon.bean;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * { "metric": "metric.demo", "endpoint": "qd-open-falcon-judge01.hd",
 * "timestamp": $ts, "step": 60, "value": 9, "counterType": "GAUGE", "tags":
 * "project=falcon,module=judge" }
 * 
 * @author gaosong
 * @since 2016年4月28日
 *
 */
public class FalconItem {

	@JsonProperty
	private String metric;

	@JsonProperty
	private String endpoint;

	@JsonProperty
	private long timestamp;

	@JsonProperty
	private int step;

	@JsonProperty
	@JsonSerialize(using = CustomDoubleSerialize.class)
	private double value;

	@JsonProperty
	private String counterType;

	@JsonProperty
	private String tags;

	public FalconItem() {
	}

	public FalconItem(String metric, String endpoint, long timestamp, int step,
			double value, String counterType, String tags) {
		this.metric = metric;
		this.endpoint = endpoint;
		this.timestamp = timestamp;
		this.step = step;
		this.value = value;
		this.counterType = counterType;
		this.tags = tags;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	/**
	 * @return the metric
	 */
	public String getMetric() {
		return metric;
	}

	/**
	 * @return the endpoint
	 */
	public String getEndpoint() {
		return endpoint;
	}

	/**
	 * @return the timestamp
	 */
	public long getTimestamp() {
		return timestamp;
	}

	/**
	 * @return the step
	 */
	public int getStep() {
		return step;
	}

	/**
	 * @return the value
	 */
	public double getValue() {
		return value;
	}

	/**
	 * @return the counterType
	 */
	public String getCounterType() {
		return counterType;
	}

	/**
	 * @return the tags
	 */
	public String getTags() {
		return tags;
	}

	/**
	 * @param metric
	 *            the metric to set
	 */
	public void setMetric(String metric) {
		this.metric = metric;
	}

	/**
	 * @param endpoint
	 *            the endpoint to set
	 */
	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	/**
	 * @param timestamp
	 *            the timestamp to set
	 */
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * @param step
	 *            the step to set
	 */
	public void setStep(int step) {
		this.step = step;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(double value) {
		this.value = value;
	}

	/**
	 * @param counterType
	 *            the counterType to set
	 */
	public void setCounterType(String counterType) {
		this.counterType = counterType;
	}

	/**
	 * @param tags
	 *            the tags to set
	 */
	public void setTags(String tags) {
		this.tags = tags;
	}

}
