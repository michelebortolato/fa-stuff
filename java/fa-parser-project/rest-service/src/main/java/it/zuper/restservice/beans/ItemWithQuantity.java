package it.zuper.restservice.beans;

import java.io.Serializable;

public class ItemWithQuantity implements Serializable{

	private static final long serialVersionUID = 1L;

	private String name;
	private Integer quantity;
	
	public String getName() {
		return name;
	}
	public Integer getQuantity() {
		return quantity;
	}

	public ItemWithQuantity(String name, Integer quantity) {
		this.name = name;
		this.quantity = quantity;
	}
	
}
