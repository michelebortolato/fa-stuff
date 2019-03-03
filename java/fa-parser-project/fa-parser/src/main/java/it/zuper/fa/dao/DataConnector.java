package it.zuper.fa.dao;

import java.util.Map;

import io.vavr.control.Option;
import it.zuper.fa.parser.beans.Template;

public interface DataConnector {

	Option<Double> getPrice(String template, String item);
	Map<String, Double> getPricesForTemplate(String template);
	Option<Template> getTemplate(String id);
	
	public static DataConnector getConnector() {return new PostgresDataConnector();}
	void delete(String template, String name);
	Option<Double> add(String template, String name, Double price);
	void update(String template, String name, Double price);
	
}
