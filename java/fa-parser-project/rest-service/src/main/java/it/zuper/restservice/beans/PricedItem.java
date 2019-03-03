package it.zuper.restservice.beans;

import java.io.Serializable;

public class PricedItem implements Serializable {

	private static final long serialVersionUID = 1L;

	private String name;
	private Double price;
	
	public String getName() {
		return name;
	}
	public Double getPrice() {
		return price;
	}

	public PricedItem(String name, Double price) {
		this.name = name;
		this.price = price;
	}
	
}
