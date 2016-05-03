package com.stephan.tof.jmxmon.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GCData {

	@JsonProperty
	private long collectionTime;
	
	@JsonProperty
	private long collectionCount;
	
	@JsonProperty
	private long memoryUsed;
	
	@JsonProperty
	private long unitTimeCollectionCount;

	/**
	 * @return the collectionTime
	 */
	public long getCollectionTime() {
		return collectionTime;
	}

	/**
	 * @return the collectionCount
	 */
	public long getCollectionCount() {
		return collectionCount;
	}

	/**
	 * @param collectionTime the collectionTime to set
	 */
	public void setCollectionTime(long collectionTime) {
		this.collectionTime = collectionTime;
	}

	/**
	 * @param collectionCount the collectionCount to set
	 */
	public void setCollectionCount(long collectionCount) {
		this.collectionCount = collectionCount;
	}

	/**
	 * @return the memoryUsed
	 */
	public long getMemoryUsed() {
		return memoryUsed;
	}

	/**
	 * @param memoryUsed the memoryUsed to set
	 */
	public void setMemoryUsed(long memoryUsed) {
		this.memoryUsed = memoryUsed;
	}

	/**
	 * @return the unitTimeCollectionCount
	 */
	public long getUnitTimeCollectionCount() {
		return unitTimeCollectionCount;
	}

	/**
	 * @param unitTimeCollectionCount the unitTimeCollectionCount to set
	 */
	public void setUnitTimeCollectionCount(long unitTimeCollectionCount) {
		this.unitTimeCollectionCount = unitTimeCollectionCount;
	}
	
}
