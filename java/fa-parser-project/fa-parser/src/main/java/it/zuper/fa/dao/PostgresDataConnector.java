package it.zuper.fa.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vavr.control.Option;
import it.zuper.fa.parser.beans.ImmutableOffice;
import it.zuper.fa.parser.beans.ImmutablePersonInfo;
import it.zuper.fa.parser.beans.ImmutableTemplate;
import it.zuper.fa.parser.beans.ImmutableTemplate.Builder;
import it.zuper.fa.parser.beans.Template;

public class PostgresDataConnector implements DataConnector {

	private static final Logger LOGGER = LoggerFactory.getLogger(PostgresDataConnector.class);

	private static final String PASSWORD = "55555555";
	private static final String USER = "postgres";
	private static final String JDBC = "jdbc:postgresql://localhost:5432/postgres";
	private Connection connection;

	public PostgresDataConnector() {
		try  {

			this.connection = DriverManager.getConnection(JDBC, USER, PASSWORD);
			Class.forName("org.postgresql.Driver"); 
			System.out.println("Connected to PostgreSQL database!");
		} catch (ClassNotFoundException e) {
			LOGGER.error("PostgreSQL JDBC driver not found.", e);
		} catch (SQLException e) {
			LOGGER.error("PostgreSQL Error.", e);
		}
	}

	@Override
	public Option<Double> getPrice(String template, String item) {
		PreparedStatement statement;
		try {
			statement = connection.prepareStatement("SELECT * FROM \"PRICED_ITEM\" WHERE \"TEMPLATE\" = ? AND \"ITEM\" = ?");
			statement.setString(1, template);
			statement.setString(2, item);
			ResultSet rs = statement.executeQuery();
			while (rs.next()) {
				return Option.of(rs.getDouble("PRICE"));
			}
		} catch (SQLException e) {
			LOGGER.error("SQL exception", e);
		}
		return Option.none();
	}

	@Override
	public Map<String, Double> getPricesForTemplate(String template) {
		PreparedStatement statement;
		try {
			statement = connection.prepareStatement("SELECT * FROM \"PRICED_ITEM\" WHERE \"TEMPLATE\" = ?");
			statement.setString(1, template);
			ResultSet rs = statement.executeQuery();
			Map<String, Double> result = new HashMap<>();
			while (rs.next()) {
				String item = rs.getString("ITEM");
				double price = rs.getDouble("PRICE");
				LOGGER.debug("Item {} with price {} for {}", item, price, template);
				result.put(item.trim(), price);
			}
			return result;
		} catch (SQLException e) {
			LOGGER.error("SQL exception", e);
			return Collections.emptyMap();
		}
	}

	@Override
	public Option<Template> getTemplate(String id) {
		try {
			Builder builder = ImmutableTemplate.builder();
			it.zuper.fa.parser.beans.ImmutablePersonInfo.Builder piBuilder = ImmutablePersonInfo.builder();
			it.zuper.fa.parser.beans.ImmutableOffice.Builder oBuilder = ImmutableOffice.builder();

			PreparedStatement statement = connection.prepareStatement("SELECT * FROM \"CLIENT\" WHERE \"TEMPLATE\" = ?");
			statement.setString(1, id);
			ResultSet rs = statement.executeQuery();

			while (rs.next()) {

				piBuilder.denominazione(rs.getString("NAME").trim())
				.idCodice(rs.getString("CODE").trim())
				.idPaese(rs.getString("COUNTRY").trim());

				oBuilder.cap(rs.getString("CAP").trim())
				.comune(rs.getString("LOCALITY").trim())
				.indirizzo(rs.getString("ADDRESS").trim())
				.nazione(rs.getString("COUNTRY").trim())
				.provincia(rs.getString("PROVINCE").trim());
				
				Template result = builder.personInfo(piBuilder.build())
						.sede(oBuilder.build())
						.pec(Optional.ofNullable(rs.getString("PEC")).map(String::trim))
						.sdi(Optional.ofNullable(rs.getString("SDI")).map(String::trim))
						.pricedItems(getPricesForTemplate(id))
						.build();
				
				LOGGER.info("Read template {}", result);
				return Option.of(result);
			}
			LOGGER.warn("No result");
			return Option.none();

		} catch (Exception e) {
			LOGGER.error("Generic sql exception", e);
			return Option.none();
		}
	}

	@Override
	public void delete(String template, String name) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Option<Double> add(String template, String name, Double price) {
		try {
			PreparedStatement st = connection.prepareStatement("INSERT INTO \"PRICED_ITEM\" (\"TEMPLATE\", \"ITEM\", \"PRICE\") VALUES (?, ?, ?)");
			st.setString(1, template);
			st.setString(2, name);
			st.setDouble(3, price);
			st.executeUpdate();
			st.close();		
			LOGGER.info("Record inserted {}", name);
			return Option.of(price);
		} catch (SQLException e) {
			LOGGER.error("Insertion error", e);
			return Option.none();
		}
	}

	@Override
	public void update(String template, String name, Double price) {
		// TODO Auto-generated method stub
		
	}

}
