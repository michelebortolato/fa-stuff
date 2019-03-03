package it.zuper.fa.parser.beans;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

import com.google.gson.annotations.SerializedName;

@Value.Immutable
@Gson.TypeAdapters
@Value.Style(allParameters=true)
public interface Office {

	String indirizzo();
	@SerializedName("CAP")
	String cap();
	String comune();
	String provincia();
	String nazione();
	
}
