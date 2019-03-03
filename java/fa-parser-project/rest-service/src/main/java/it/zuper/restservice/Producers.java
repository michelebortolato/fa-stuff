package it.zuper.restservice;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.zuper.fa.dao.DataConnector;
import it.zuper.fa.dao.PostgresDataConnector;

@ApplicationScoped
public class Producers {

	private static final Logger LOGGER = LoggerFactory.getLogger(Producers.class);
	private PostgresDataConnector connector;
	
	@PostConstruct
	private void init() {
		LOGGER.info("Init");
		LOGGER.info("Building data connector");
		this.connector = new PostgresDataConnector();
	}
	
	@Produces
	public DataConnector getConnector() {
		return this.connector;
	}
	
}
