/**
 * 
 */
package com.stephan.tof.jmxmon.bean;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Stephan gao
 * @since 2015年4月20日
 *
 */
public class JacksonUtil {

	private static ObjectMapper om = new ObjectMapper();
//	static {
//		// 设置输入时忽略JSON字符串中存在而Java对象实际没有的属性
//		om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//	}

	private JacksonUtil() {
	}
	
	public static void setObjectMapper(ObjectMapper om) {
		JacksonUtil.om = om;
	}

	public static <T> T readBeanFromFile(File srcFile, Class<T> clazz)
			throws IOException {
		return om.readValue(srcFile, clazz);
	}

	public static <T> T readBeanFromString(String jsonStr, Class<T> clazz)
			throws IOException {
		return om.readValue(jsonStr, clazz);
	}
	
	public static <T> T readBeanFromStream(InputStream input, Class<T> clazz) throws JsonParseException, JsonMappingException, IOException {
		return om.readValue(input, clazz);
	}

	public static <T> void writeBeanToFile(File dstFile, T t,
			boolean withPrettyPrinter) throws IOException {
		if (withPrettyPrinter) {
			om.writerWithDefaultPrettyPrinter().writeValue(dstFile, t);
		} else {
			om.writeValue(dstFile, t);
		}
	}

	public static <T> String writeBeanToString(T t, boolean withPrettyPrinter)
			throws JsonProcessingException {
		if (withPrettyPrinter) {
			return om.writerWithDefaultPrettyPrinter().writeValueAsString(t);
		} else {
			return om.writeValueAsString(t);
		}
	}
	
	public static Map<String, Object> readMapFromString(String jsonStr) 
			throws JsonParseException, JsonMappingException, IOException {
		return om.readValue(jsonStr, Map.class);
	}
}
