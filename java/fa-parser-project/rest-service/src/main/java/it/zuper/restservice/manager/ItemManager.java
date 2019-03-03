package it.zuper.restservice.manager;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vavr.control.Either;
import io.vavr.control.Option;
import it.zuper.fa.dao.DataConnector;
import it.zuper.fa.parser.BeanConverter;
import it.zuper.fa.parser.beans.FatturaElettronicaType;
import it.zuper.fa.parser.beans.ImmutableInvoiceToProcess;
import it.zuper.fa.parser.beans.ImmutableInvoiceToProcess.Builder;
import it.zuper.fa.parser.beans.InvoiceToProcess;
import it.zuper.fa.parser.beans.ObjectFactory;
import it.zuper.fa.parser.beans.Template;
import it.zuper.restservice.beans.ItemWithQuantity;
import it.zuper.restservice.beans.PricedItem;

public class ItemManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(ItemManager.class);

	@Inject
	DataConnector connector;

	public List<PricedItem> list(String template) {
		return connector.getPricesForTemplate(template).entrySet().stream()
				.map(entry -> new PricedItem(entry.getKey(), entry.getValue()))
				.collect(Collectors.toList());
	}

	public Either<String, PricedItem> get(String template, String name) {
		return connector.getPrice(template, name)
				.toEither("Not found")
				.map(price -> new PricedItem(name, price));
	}

	public Option<PricedItem> add(String template, String name, Double price) {
		return connector.add(template, name, price)
				.map(ign -> new PricedItem(name, price));
	}

	public void deleteItem(String template, String name) {
		connector.delete(template, name);
	}

	public PricedItem update(String template, String name, Double price) {
		connector.update(template, name, price);
		return new PricedItem(name, price);
	}

	public Either<String, String> createInvoice(String template, String date, Integer number, List<ItemWithQuantity> items) {
		FatturaElettronicaType invoice = BeanConverter.createInvoice(toProcessBuild(template, date, items).get(), number);

		return convert(invoice)
				.toEither("Some Error Occurred");
	}

	private Option<InvoiceToProcess> toProcessBuild(String templateId, String date, List<ItemWithQuantity> items) {

		Option<Template> template =  connector.getTemplate(templateId);

		Builder builder = ImmutableInvoiceToProcess.builder()
				.dataString(date);
		if(template.isEmpty()) {
			LOGGER.warn("Template {} not found", templateId);
			return Option.none();
		}

		return template
				.peek(builder::destination)
				.map(tmpl-> buildFromTemplate(items, builder, tmpl));
	}

	private static InvoiceToProcess buildFromTemplate(List<ItemWithQuantity> items, Builder builder, Template tmpl) {

		tmpl.pricedItems().entrySet()
		.forEach(entry -> processItem(entry, builder, items));

		return builder.build();
	}

	private static void processItem(Entry<String, Double> entry, Builder builder, List<ItemWithQuantity> items) {
		contains(items, entry.getKey())
		.map(ItemWithQuantity::getQuantity)
		.filter(qty -> qty > 0)
		.forEach(qty ->	builder.putItemQuantities(entry.getKey(), qty));
	}

	private static Option<ItemWithQuantity> contains(List<ItemWithQuantity> items, String key) {
		Optional<ItemWithQuantity> itemOpt = items.stream()
				.filter(item -> item.getName().equals(key))
				.findFirst();

		if(itemOpt.isPresent()) {
			return Option.ofOptional(itemOpt);
		}
		LOGGER.warn("Not Found " + key);
		return Option.none();

	}

	private static Option<String> convert(FatturaElettronicaType fa) {
		try {
			ObjectFactory factory = new ObjectFactory();
			JAXBContext context = JAXBContext.newInstance(FatturaElettronicaType.class);
			JAXBElement<FatturaElettronicaType> result = factory.createFatturaElettronica(fa);
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			marshaller.marshal(result, baos);
			return Option.of(baos.toByteArray()).map(String::new);
		} catch(Exception e) {
			LOGGER.warn("Error on parsing", e);
			return Option.none();
		}
	}

}
