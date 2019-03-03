package it.zuper.fa.parser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vavr.control.Try;
import it.zuper.fa.parser.beans.Destination;
import it.zuper.fa.parser.beans.From;
import it.zuper.fa.parser.beans.ImmutableInvoice;
import it.zuper.fa.parser.beans.Invoice;
import it.zuper.fa.parser.beans.Item;

public class Parser {

	private static final Logger LOGGER = LoggerFactory.getLogger(Parser.class);
	
	public static Invoice parse(Document document) {
		From from = BeanConverter.buildFrom(document);
		Destination to = BeanConverter.buildDestination(document);
		List<Item> items = BeanConverter.items(document);
		
		return ImmutableInvoice.builder()
				.items(items)
				.from(from)
				.to(to)
				.date(BeanConverter.date(document))
				.number(BeanConverter.number(document))
				.build();
	}
	
	public static void parseFolder(Path folder) throws IOException {
		Files.list(folder)
		.filter(path -> path.toString().endsWith(".html"))
		.forEach(Parser::parseFile);

	}
	
	public static void parseFile(Path path) {
		DataExtractor.getDocumentSafely(path)
				.flatMap(Parser::parseSafely)
				.ifPresent(invoice -> Parser.write(path, invoice));
	}
	
	private static Optional<Invoice> parseSafely(Document document) {
		return Try.of(() -> Parser.parse(document))
				.onFailure(e -> LOGGER.warn("Error parsing document ", e))
				.toJavaOptional();
	}
	
	public static void write(Path path, Invoice invoice) {
		LOGGER.info("Writing.... {}", path);
		//Path filename = path.getFileName();
		Path folder = path.getParent();
		Path outputFolder = Paths.get(folder.toString(), "output");
		outputFolder.toFile().mkdirs();
		EInvoiceConverter converter = EInvoiceConverter.valueOf(invoice);
		converter.convert();
		Try.run(() -> converter.writeTo(outputFolder))
		.onSuccess(ign -> LOGGER.info("Successful"))
		.onFailure(f -> LOGGER.warn("Failure ",f));
	}
}
