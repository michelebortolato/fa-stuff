package it.zuper.fa.parser;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

import io.vavr.control.Try;

public class CodesMap {

	private static CodesMap instance;
	private Map<String, String> map = new HashMap<>();
	
	private static final String CODES_FILE = "codes.properties";
	
	private CodesMap() {
		final Properties p = new Properties();
		
		Try.run(() -> p.load(getClass().getClassLoader().getResourceAsStream(CODES_FILE)))
		.getOrElseThrow(e -> new RuntimeException("Error on reading codes.properties", e));
				
		p.entrySet().stream()
		.forEach(entry -> map.put((String)entry.getKey(), (String)entry.getValue()));
	}
	
	public static CodesMap getInstance() {
		if(null==instance) {
			instance=new CodesMap();
		}
		return instance;
	}
	
	public Optional<String> get(String key){
		return Optional.ofNullable(map.get(key));
	}
	
}
