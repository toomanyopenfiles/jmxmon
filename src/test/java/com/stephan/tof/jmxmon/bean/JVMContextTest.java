package com.stephan.tof.jmxmon.bean;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class JVMContextTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testJsonTransfer() throws IOException {
		JVMContext context1 = new JVMContext();
		context1.getJvmData(1111).getGcData("1a").setCollectionCount(11);
		context1.getJvmData(1111).getGcData("1b").setCollectionCount(12);
		context1.getJvmData(2222).getGcData("2a").setCollectionCount(21);
		context1.getJvmData(2222).getGcData("2b").setCollectionCount(22);
		context1.getJvmData(2222).getGcData("2c").setCollectionCount(23);
		File f = new File("test.jvmcontext.json");
		JacksonUtil.writeBeanToFile(f, context1, true);
		System.out.println("json file path=" + f.getAbsolutePath());
		
		JVMContext context2 = JacksonUtil.readBeanFromFile(f, JVMContext.class);
		assertThat(context2.getJvmData(1111).getGcData("1b").getCollectionCount()).isEqualTo(12);
		assertThat(context2.getJvmData(2222).getGcData("2a").getCollectionCount()).isEqualTo(21);
		assertThat(context2.getJvmData(2222).getGcData("2c").getCollectionCount()).isEqualTo(23);
		assertThat(context2.getJvmData(2222).getGcData("2a").getCollectionTime()).isEqualTo(0);
		assertThat(context2.getJvmData(2222).getGcData("2b").getCollectionTime()).isEqualTo(0);
		assertThat(context2.getJvmData(2222).getGcData("2c").getCollectionTime()).isEqualTo(0);
	}

}
