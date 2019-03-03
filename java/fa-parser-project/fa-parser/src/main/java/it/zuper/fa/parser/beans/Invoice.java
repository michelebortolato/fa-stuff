package it.zuper.fa.parser.beans;

import java.time.LocalDate;
import java.util.List;

import org.immutables.value.Value;

@Value.Immutable
@Value.Style(allParameters=true)
public abstract class Invoice {

	public abstract From from();
	public abstract Destination to();
	public abstract LocalDate date();
	public abstract Integer number();
	
	public abstract List<Item> items();
	
	public Double totalAmount() {
		return items().stream()
		.mapToDouble(item -> item.quantity() * item.price())
		.sum();
	}
}
