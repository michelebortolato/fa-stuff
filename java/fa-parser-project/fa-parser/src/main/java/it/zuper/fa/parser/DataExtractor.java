package it.zuper.fa.parser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vavr.control.Try;

public class DataExtractor {

	public static final String GET_P_IVA_QUERY = "html body table tr td table[height=29] tr td";
	public static final String GET_ADDRESS_QUERY = "html body table tr td table[width=343] tr td";
	public static final String GET_ADDRESS_ROAD_QUERY = "html body table tr td table[width=255] tr td";
	public static final String GET_CLIENT_QUERY = "html body table tr td table[width=351] tr td";
	public static final String GET_DATE_QUERY = "html body table tr td[colspan=2] table[width=112] tr td";
	public static final String GET_INVOICE_NUMBER_QUERY = "html body table tr td[colspan=6] table[width=112] tr td";

	public static final String GET_FROM_QUERY = "html body table tr td[colspan=2] table[width=288] tr td";
	public static final String GET_ITEMS_QUERY = "html body table tr td tr[height=24]";

	public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yy");

	public static Pattern ADDRESS_PATTERN = Pattern.compile("(\\d+) - (.+) \\(([A-Za-z]{2})\\)"); 
	public static Pattern FROM_PATTERN = Pattern.compile("([A-Z]+ [A-Z]+) (.+) \\(([A-Za-z]{2})\\) (.+) P.IVA (.+) Reg.Imp.C.F. (.+)");

	private static final Logger LOGGER = LoggerFactory.getLogger(DataExtractor.class);
	
	public static Elements getItems(Document document) {
		return get(GET_ITEMS_QUERY, document);
	}

	public static Optional<String> getPIva(Document document) {
		return getFirst(GET_P_IVA_QUERY, document);
	}

	public static Elements get(String query, Document document) {
		return document.select(query);
	}

	public static Optional<String> getFirst(String query, Document document) {
		return document.select(query).stream()
				.map(Element::text)
				.findFirst();
	}

	public static Document document(Path path) throws IOException {
		String content = new String(Files.readAllBytes(path));
		return Jsoup.parse(content);
	}

	public static Optional<Document> getDocumentSafely(Path path){
		return Try.of(() -> document(path))
				.recover(e -> {
					LOGGER.warn("Unable to parse path {}", path, e);
					return null;})
				.toJavaOptional();
	}

}
