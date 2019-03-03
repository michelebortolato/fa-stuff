package it.zuper.fa.parser.beans;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Value.Immutable
@Gson.TypeAdapters
@Value.Style(allParameters=true)
public interface PersonInfo {

	String idPaese();
	
	String idCodice();
	
	String denominazione();
	
}
