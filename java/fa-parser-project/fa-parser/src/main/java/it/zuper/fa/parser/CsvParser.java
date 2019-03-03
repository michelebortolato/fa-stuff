package it.zuper.fa.parser;

import java.io.FileReader;
import java.io.IOException;
import java.util.Map.Entry;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.jsoup.helper.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vavr.control.Option;
import it.zuper.fa.parser.beans.ImmutableInvoiceToProcess;
import it.zuper.fa.parser.beans.ImmutableInvoiceToProcess.Builder;
import it.zuper.fa.parser.beans.InvoiceToProcess;
import it.zuper.fa.parser.beans.Template;

public class CsvParser {

	private static final Logger LOGGER = LoggerFactory.getLogger(CsvParser.class);

	private static CSVFormat FORMATTER = CSVFormat.newFormat(';').withFirstRecordAsHeader();


	public static Iterable<CSVRecord> getCsvRecords(String path) throws IOException {
		return FORMATTER.parse(new FileReader(path));
	}

}
