package it.zuper.fa.parser;

import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map.Entry;
import java.util.function.LongSupplier;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;

import org.apache.commons.csv.CSVRecord;
import org.jsoup.helper.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vavr.control.Option;
import io.vavr.control.Try;
import it.zuper.fa.dao.DataConnector;
import it.zuper.fa.parser.beans.FatturaElettronicaType;
import it.zuper.fa.parser.beans.ImmutableInvoiceToProcess;
import it.zuper.fa.parser.beans.InvoiceToProcess;
import it.zuper.fa.parser.beans.ObjectFactory;
import it.zuper.fa.parser.beans.Template;
import it.zuper.fa.parser.beans.ImmutableInvoiceToProcess.Builder;

public class InvoiceScanner {

	private static final Logger LOGGER = LoggerFactory.getLogger(InvoiceScanner.class);

	private Path inputFile;
	private Path outputFolder;
	private long firstInvoiceNumber;
	private LongSupplier NEXT_VALUE_SUPPLIER = () -> firstInvoiceNumber++;

	DataConnector connector = DataConnector.getConnector();

	public InvoiceScanner(long firstNumber, Path templateFolder, Path inputFile) {
		this(firstNumber, inputFile);
		TemplateCache.getInstance().withTemplateFolder(templateFolder.toString());
	}

	public InvoiceScanner(long firstNumber, Path inputFile) {
		this.inputFile = inputFile;
		this.firstInvoiceNumber = firstNumber;
	}

	public void parse() {
		parse(Paths.get(System.getProperty("java.io.tmpdir")));
	}

	public void parse(Path outputFolder) {
		this.outputFolder = outputFolder;

		Try.of(() -> CsvParser.getCsvRecords(inputFile.toString()))
		.onFailure(fail -> LOGGER.warn("Error reading CSV", fail))
		.onSuccess(records -> scanRecords(records));
	}

	private void scanRecords(Iterable<CSVRecord> records) {
		for(CSVRecord record : records) {
			parseRecordtoProcess(record)
			.map(toProcess -> BeanConverter.createInvoice(toProcess, NEXT_VALUE_SUPPLIER.getAsLong()))
			.forEach(this::toFile);
		}
	}

	private void toFile(FatturaElettronicaType invoice) {

		String name = (invoice.getFatturaElettronicaHeader()
				.getCessionarioCommittente()
				.getDatiAnagrafici()
				.getAnagrafica()
				.getDenominazione() + "-" +
				invoice.getFatturaElettronicaBody()
				.get(0).getDatiGenerali().getDatiGeneraliDocumento()
				.getNumero() + ".xml").replaceAll("/", "-");

		convert(invoice)
		.forEach(content -> toFile(outputFolder.resolve(name), content));
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

	private static void toFile(Path output, String content) {
		Try.run(() -> Files.write(output, content.getBytes()))
		.onFailure(fail -> LOGGER.error("", fail))
		.onSuccess(v -> LOGGER.info("Written: {}", output));
	}

	public Option<InvoiceToProcess> parseRecordtoProcess(CSVRecord record) {

		//Option<Template> template = TemplateCache.getInstance().getTemplate(record.get("TEMPLATE"));
		Option<Template> template =  connector.getTemplate(record.get("TEMPLATE"));

		Builder builder = ImmutableInvoiceToProcess.builder()
				.dataString(record.get("DATA"));
		if(template.isEmpty()) {
			LOGGER.warn("Template {} not found", record.get("TEMPLATE"));
			return Option.none();
		}

		return template
				.peek(builder::destination)
				.map(tmpl-> buildFromTemplate(record, builder, tmpl));
	}

	private static InvoiceToProcess buildFromTemplate(CSVRecord record, Builder builder, Template tmpl) {

		tmpl.pricedItems().entrySet()
		.forEach(entry -> processItem(entry, builder, record));

		return builder.build();
	}

	private static void processItem(Entry<String, Double> entry, Builder builder, CSVRecord recordpri) {
		if (recordpri.isMapped(entry.getKey())) {			
			String qtyStr = recordpri.get(entry.getKey());
			if(!StringUtil.isBlank(qtyStr)) {
				Integer qty = Integer.valueOf(qtyStr);
				if(qty>0) {
					builder.putItemQuantities(entry.getKey(), qty);
				}
			}
		} else {
			LOGGER.debug("{} Not mapped into csv", entry.getKey());
		}

	}
}
