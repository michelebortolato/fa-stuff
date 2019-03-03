package it.zuper.fa.parser.beans;

import java.util.Optional;

import org.immutables.value.Value;

@Value.Immutable
public interface Destination {

	String name();
	String address();
	String cap();
	String comune();
	String province();
	
	Optional<String> cf();
	Optional<String> pIVA();
}
