package it.zuper.fa.parser.beans;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

import com.google.gson.annotations.SerializedName;

@Value.Immutable
@Gson.TypeAdapters
@Value.Style(allParameters=true)
public interface Template {

	@SerializedName("SDI")
	Optional<String> sdi();
	
	@SerializedName("PEC")
	Optional<String> pec();
	
	@SerializedName("dati_anagrafici")
	PersonInfo personInfo();
	
	Office sede();
	
	Map<String, Double> pricedItems();
}
