package it.zuper.fa.parser;

import org.apache.log4j.BasicConfigurator;

import it.zuper.fa.dao.PostgresDataConnector;

public class PostgreSQLConnector {

	static {
		BasicConfigurator.configure();
	}
	
	public static void main(String[] args) {
		new PostgresDataConnector().getTemplate("BALDAN");
	}

}
