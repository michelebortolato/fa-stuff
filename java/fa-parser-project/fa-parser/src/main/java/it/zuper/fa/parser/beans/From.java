package it.zuper.fa.parser.beans;

import org.immutables.value.Value;

@Value.Immutable
public interface From {

	String name();
	String address();
	//String cap();
	String comune();
	String province();
	
	String cf();
	String pIVA();
}
