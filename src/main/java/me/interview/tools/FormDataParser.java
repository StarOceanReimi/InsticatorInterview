package me.interview.tools;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

public abstract class FormDataParser {

	public static MultiValueMap<String, String> parse(String source) throws UnsupportedEncodingException {
		return parse(source, Charset.forName("UTF-8"));
	}
	
	public static MultiValueMap<String, String> parse(String source, Charset charset) throws UnsupportedEncodingException {
		String[] pairs = StringUtils.tokenizeToStringArray(source, "&");
		MultiValueMap<String, String> result = new LinkedMultiValueMap<>(pairs.length);
		for (String pair : pairs) {
			int idx = pair.indexOf('=');
			if (idx == -1) {
				result.add(URLDecoder.decode(pair, charset.name()), null);
			}
			else {
				String name = URLDecoder.decode(pair.substring(0, idx), charset.name());
				String value = URLDecoder.decode(pair.substring(idx + 1), charset.name());
				result.add(name, value);
			}
		}
		return result;
	}
}
