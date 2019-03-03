package it.zuper.fa.parser.beans;

import java.util.Map;

import org.immutables.value.Value;

@Value.Immutable
@Value.Style(allParameters=true)
public interface InvoiceToProcess {
	Template destination();
	String dataString();
	
	Map<String, Integer> itemQuantities();
}
