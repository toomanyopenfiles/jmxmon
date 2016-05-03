package com.stephan.tof.jmxmon.bean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.stephan.tof.jmxmon.Constants.CounterType;

public class FalconPostDataTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testJsonTransfer() throws IOException {
		FalconItem item1 = new FalconItem("m1", "ep1",
				System.currentTimeMillis() / 1000, 60, 123,
				CounterType.COUNTER.toString(), "tag1=a,tag2=b");
		FalconItem item2 = new FalconItem("m2", "ep2",
				System.currentTimeMillis() / 1000, 60, 156789.123456F,
				CounterType.GAUGE.toString(), "tag3=a,tag4=b");
		List<FalconItem> writeItems = new ArrayList<FalconItem>();
		writeItems.add(item1);
		writeItems.add(item2);

		File f = new File("test.falconPostData.json");
		JacksonUtil.writeBeanToFile(f, writeItems, true);
		System.out.println("json file path=" + f.getAbsolutePath());

		@SuppressWarnings("unchecked")
		List<FalconItem> readItems = JacksonUtil
				.readBeanFromFile(f, List.class);
		assertThat(readItems).hasSize(2);
		assertThat(readItems).extracting(
				"metric", "endpoint", "step", "value", "counterType", "tags").containsExactly(
						tuple("m1", "ep1", 60, 123D, CounterType.COUNTER.toString(), "tag1=a,tag2=b"),
						tuple("m2", "ep2", 60, 156789.12D, CounterType.GAUGE.toString(), "tag3=a,tag4=b"));
	}
}
