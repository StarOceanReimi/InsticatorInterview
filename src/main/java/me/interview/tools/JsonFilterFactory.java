package me.interview.tools;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

public abstract class JsonFilterFactory {

	public static FilterProvider excludeFilter(String...fieldNames) {
		return new SimpleFilterProvider()
				.addFilter("nameExcludeFilter", 
						SimpleBeanPropertyFilter.serializeAllExcept(fieldNames));
	}
}
