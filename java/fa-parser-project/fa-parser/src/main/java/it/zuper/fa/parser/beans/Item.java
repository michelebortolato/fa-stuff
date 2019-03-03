package it.zuper.fa.parser.beans;

import org.immutables.value.Value;

@Value.Immutable
public interface Item {

	String name();
	Integer quantity();
	Double price();
}
