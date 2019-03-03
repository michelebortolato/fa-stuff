package it.zuper.fa.parser;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Predicates.is;
import static it.zuper.fa.parser.DataExtractor.ADDRESS_PATTERN;
import static it.zuper.fa.parser.DataExtractor.DATE_FORMATTER;
import static it.zuper.fa.parser.DataExtractor.FROM_PATTERN;
import static it.zuper.fa.parser.DataExtractor.GET_ADDRESS_QUERY;
import static it.zuper.fa.parser.DataExtractor.GET_ADDRESS_ROAD_QUERY;
import static it.zuper.fa.parser.DataExtractor.GET_CLIENT_QUERY;
import static it.zuper.fa.parser.DataExtractor.GET_DATE_QUERY;
import static it.zuper.fa.parser.DataExtractor.GET_FROM_QUERY;
import static it.zuper.fa.parser.DataExtractor.GET_INVOICE_NUMBER_QUERY;
import static it.zuper.fa.parser.DataExtractor.GET_P_IVA_QUERY;
import static it.zuper.fa.parser.DataExtractor.getFirst;
import static it.zuper.fa.parser.DataExtractor.getItems;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vavr.control.Option;
import io.vavr.control.Try;
import it.zuper.fa.parser.beans.AnagraficaType;
import it.zuper.fa.parser.beans.CedentePrestatoreType;
import it.zuper.fa.parser.beans.CessionarioCommittenteType;
import it.zuper.fa.parser.beans.CondizioniPagamentoType;
import it.zuper.fa.parser.beans.DatiAnagraficiCedenteType;
import it.zuper.fa.parser.beans.DatiAnagraficiCessionarioType;
import it.zuper.fa.parser.beans.DatiAnagraficiTerzoIntermediarioType;
import it.zuper.fa.parser.beans.DatiBeniServiziType;
import it.zuper.fa.parser.beans.DatiGeneraliDocumentoType;
import it.zuper.fa.parser.beans.DatiGeneraliType;
import it.zuper.fa.parser.beans.DatiPagamentoType;
import it.zuper.fa.parser.beans.DatiRiepilogoType;
import it.zuper.fa.parser.beans.DatiTrasmissioneType;
import it.zuper.fa.parser.beans.Destination;
import it.zuper.fa.parser.beans.DettaglioLineeType;
import it.zuper.fa.parser.beans.DettaglioPagamentoType;
import it.zuper.fa.parser.beans.EsigibilitaIVAType;
import it.zuper.fa.parser.beans.FatturaElettronicaBodyType;
import it.zuper.fa.parser.beans.FatturaElettronicaHeaderType;
import it.zuper.fa.parser.beans.FatturaElettronicaType;
import it.zuper.fa.parser.beans.FormatoTrasmissioneType;
import it.zuper.fa.parser.beans.From;
import it.zuper.fa.parser.beans.IdFiscaleType;
import it.zuper.fa.parser.beans.ImmutableDestination;
import it.zuper.fa.parser.beans.ImmutableFrom;
import it.zuper.fa.parser.beans.ImmutableItem;
import it.zuper.fa.parser.beans.ImmutableItem.Builder;
import it.zuper.fa.parser.beans.IndirizzoType;
import it.zuper.fa.parser.beans.InvoiceToProcess;
import it.zuper.fa.parser.beans.Item;
import it.zuper.fa.parser.beans.ModalitaPagamentoType;
import it.zuper.fa.parser.beans.ObjectFactory;
import it.zuper.fa.parser.beans.Office;
import it.zuper.fa.parser.beans.PersonInfo;
import it.zuper.fa.parser.beans.RegimeFiscaleType;
import it.zuper.fa.parser.beans.TerzoIntermediarioSoggettoEmittenteType;
import it.zuper.fa.parser.beans.TipoDocumentoType;

public class BeanConverter {

	private static final Logger LOGGER = LoggerFactory.getLogger(BeanConverter.class);
	
	private static final String SEAC_PIVA = "00865310221";

	public static FatturaElettronicaType createInvoice(InvoiceToProcess toProcess, long number) {

		ObjectFactory factory = new ObjectFactory();
		FatturaElettronicaType faType = new FatturaElettronicaType();
		FormatoTrasmissioneType version = FormatoTrasmissioneType.FPR_12;
		faType.setVersione(version);
		FatturaElettronicaHeaderType faHead = factory.createFatturaElettronicaHeaderType();

		DatiTrasmissioneType faDT = factory.createDatiTrasmissioneType();
		IdFiscaleType faIdT = factory.createIdFiscaleType();
		faIdT.setIdPaese("IT");
		faIdT.setIdCodice(SEAC_PIVA);
		faDT.setIdTrasmittente(faIdT );
		faDT.setProgressivoInvio("1");
		faDT.setFormatoTrasmissione(FormatoTrasmissioneType.FPR_12);

		String sdi = toProcess.destination().sdi()
				.orElse("0000000");
		toProcess.destination().pec()
		.ifPresent(faDT::setPECDestinatario);
		faDT.setCodiceDestinatario(sdi);
		faHead.setDatiTrasmissione(faDT);

		CedentePrestatoreType faCP = factory.createCedentePrestatoreType();
		DatiAnagraficiCedenteType faDA=factory.createDatiAnagraficiCedenteType();
		IdFiscaleType faIdFIVA = factory.createIdFiscaleType();
		faIdFIVA.setIdPaese("IT");
		faIdFIVA.setIdCodice("00809380272");
		faDA.setIdFiscaleIVA(faIdFIVA );
		faDA.setCodiceFiscale("BRTDNL57S10I242R");
		AnagraficaType faAna = factory.createAnagraficaType();
		//faAna.setDenominazione("YYGB SPA");
		faAna.setNome("DANIELE");
		faAna.setCognome("BORTOLATO");
		faDA.setAnagrafica(faAna );

		faDA.setRegimeFiscale(RegimeFiscaleType.RF_01);
		faCP.setDatiAnagrafici(faDA);
		IndirizzoType faS = factory.createIndirizzoType();
		faS.setIndirizzo("VIA RIO 17");
		faS.setCAP("30036");
		faS.setComune("SANTA MARIA DI SALA");
		faS.setProvincia("VE");
		faS.setNazione("IT");
		faCP.setSede(faS );
		faHead.setCedentePrestatore(faCP );

		Office address = toProcess.destination().sede();
		PersonInfo personInfo = toProcess.destination().personInfo();

		CessionarioCommittenteType faCC = factory.createCessionarioCommittenteType();
		DatiAnagraficiCessionarioType faCCDA = factory.createDatiAnagraficiCessionarioType();
		AnagraficaType faCCA = factory.createAnagraficaType();
		faCCA.setDenominazione(personInfo.denominazione());
		faCCDA.setAnagrafica(faCCA );
		IdFiscaleType faCCDAIdF = factory.createIdFiscaleType();
		faCCDAIdF.setIdPaese(personInfo.idPaese());
		faCCDAIdF.setIdCodice(personInfo.idCodice());
		faCCDA.setIdFiscaleIVA(faCCDAIdF );
		faCC.setDatiAnagrafici(faCCDA);

		TerzoIntermediarioSoggettoEmittenteType faTISE = factory.createTerzoIntermediarioSoggettoEmittenteType();
		DatiAnagraficiTerzoIntermediarioType faTISEDA = factory.createDatiAnagraficiTerzoIntermediarioType();
		AnagraficaType faTISEA = factory.createAnagraficaType();
		faTISEA.setDenominazione("SEAC SPA");
		faTISEDA.setAnagrafica(faTISEA );
		IdFiscaleType faTISEAF = factory.createIdFiscaleType();
		faTISEAF.setIdCodice("01530760220");
		faTISEAF.setIdPaese("IT");
		faTISEDA.setIdFiscaleIVA(faTISEAF );

		IndirizzoType faCCS = factory.createIndirizzoType();
		faCCS.setIndirizzo(address.indirizzo());
		faCCS.setCAP(address.cap());
		faCCS.setComune(address.comune());
		faCCS.setProvincia(address.provincia());
		faCCS.setNazione(address.nazione());
		faCC.setSede(faCCS );

		//faHead.setTerzoIntermediarioOSoggettoEmittente(faTISE);
		faHead.setCessionarioCommittente(faCC );
		faType.setFatturaElettronicaHeader(faHead);
		/*BODY*/
		FatturaElettronicaBodyType faBody = factory.createFatturaElettronicaBodyType();

		DatiGeneraliType faDG = factory.createDatiGeneraliType();
		DatiGeneraliDocumentoType faDGD = factory.createDatiGeneraliDocumentoType();
		faDGD.setTipoDocumento(TipoDocumentoType.TD_01);
		faDGD.setDivisa("EUR");
		faDGD.setData(xmlDate(toProcess.dataString()));
		//
		faDGD.setNumero(number+"/01");
		faDG.setDatiGeneraliDocumento(faDGD );
		faBody.setDatiGenerali(faDG );


		DatiBeniServiziType faDBS = factory.createDatiBeniServiziType();
		List<DettaglioLineeType> faDBSDL = faDBS.getDettaglioLinee();

		int index = 1;
		for(Entry<String, Integer> entry : toProcess.itemQuantities().entrySet()) {			
			DettaglioLineeType detail = addDetail(index, entry, toProcess, factory);
			faDBSDL.add(detail);
			index++;
		}

		Double totalPrice = faDBSDL.stream()
				.map(DettaglioLineeType::getPrezzoTotale)
				.collect(Collectors.summingDouble(BigDecimal::doubleValue));

		Double fullPrice = totalPrice*1.22;
		Double taxable = fullPrice - totalPrice;

		faDGD.setImportoTotaleDocumento(getBigDecimalScaled(fullPrice)); 
		List<DatiRiepilogoType> faDR = faDBS.getDatiRiepilogo();
		DatiRiepilogoType faDRi = factory.createDatiRiepilogoType();
		faDRi.setAliquotaIVA(getBigDecimalScaled(22));

		faDRi.setImponibileImporto(getBigDecimalScaled(totalPrice));
		faDRi.setImposta(getBigDecimalScaled(taxable));

		faDRi.setEsigibilitaIVA(EsigibilitaIVAType.D);
		faDR.add(faDRi);
		faBody.setDatiBeniServizi(faDBS);

		DatiPagamentoType faDP = factory.createDatiPagamentoType();

		faDP.setCondizioniPagamento(CondizioniPagamentoType.TP_02);
		DettaglioPagamentoType faDPDP = factory.createDettaglioPagamentoType();
		faDPDP.setModalitaPagamento(ModalitaPagamentoType.MP_01);
		faDPDP.setImportoPagamento(getBigDecimalScaled(fullPrice));
		faDP.getDettaglioPagamento().add(faDPDP );
		faBody.getDatiPagamento().add(faDP );
		faType.getFatturaElettronicaBody().add(faBody );
		LOGGER.info("Invoice created");
		return faType;
	}

	private static Date date(String date) {
		return Date.from(LocalDate.parse(date, DATE_FORMATTER).atStartOfDay(ZoneId.systemDefault()).toInstant());
	}

	public static XMLGregorianCalendar xmlDate(String input){
		GregorianCalendar c = new GregorianCalendar();
		c.setTime(date(input));
		return Try.of(() ->DatatypeFactory.newInstance().newXMLGregorianCalendar(c))
				.getOrElseThrow(() -> new RuntimeException());
	}

	private static DettaglioLineeType addDetail(int index, Entry<String, Integer> entry, InvoiceToProcess toProcess, ObjectFactory factory) {

		Integer qty = entry.getValue();
		Double price = toProcess.destination().pricedItems().get(entry.getKey());

		DettaglioLineeType faDTL = factory.createDettaglioLineeType();
		faDTL.setNumeroLinea(index);
		faDTL.setDescrizione(entry.getKey());
		faDTL.setQuantita(getBigDecimalScaled(qty));
		faDTL.setUnitaMisura("PZ");
		faDTL.setPrezzoUnitario(getBigDecimalScaled(price));
		faDTL.setAliquotaIVA(getBigDecimalScaled(22));
		faDTL.setPrezzoTotale(getBigDecimalScaled(price*qty));
		return faDTL;
	}

	public static Destination buildDestination(Document document) {
		ImmutableDestination.Builder destinationBuilder = ImmutableDestination.builder();

		Option.ofOptional(getFirst(GET_ADDRESS_QUERY, document))
		.map(address -> ADDRESS_PATTERN.matcher(address))
		.peek(Matcher::matches)
		.forEach(matcher -> {
			destinationBuilder.cap(matcher.group(1))
			.comune(matcher.group(2))
			.province(matcher.group(3));
		});

		getFirst(GET_P_IVA_QUERY, document)
		.map(destinationBuilder::pIVA);

		getFirst(GET_CLIENT_QUERY, document)
		.map(destinationBuilder::name);

		getFirst(GET_CLIENT_QUERY, document)
		.map(destinationBuilder::name);

		getFirst(GET_ADDRESS_ROAD_QUERY, document)
		.map(destinationBuilder::address);

		return destinationBuilder.build();
	}

	public static From buildFrom(Document document) {
		ImmutableFrom.Builder fromBuilder = ImmutableFrom.builder();

		Option.ofOptional(getFirst(GET_FROM_QUERY, document))
		.map(address -> FROM_PATTERN.matcher(address))
		.peek(Matcher::matches)
		.forEach(matcher -> {
			fromBuilder
			.name(matcher.group(1))
			.comune(matcher.group(2))
			.province(matcher.group(3))
			.address(matcher.group(4))
			.pIVA(matcher.group(5))
			.cf(matcher.group(6));
		});
		return fromBuilder.build();
	}

	public static List<Item> items(Document document){
		return getItems(document)		
				.stream()
				.map(BeanConverter::convert)
				.collect(Collectors.toList());
	}

	public static LocalDate date(Document document) {
		return DataExtractor.getFirst(GET_DATE_QUERY, document)
				.map(string -> LocalDate.parse(string, DATE_FORMATTER))
				.orElseThrow(RuntimeException::new);
	}

	public static Integer number(Document document) {
		return  getFirst(GET_INVOICE_NUMBER_QUERY, document)
				.map(Integer::valueOf)
				.orElseThrow(RuntimeException::new);
	}

	public static Item convert(Element element) {
		Builder itemBuilder = ImmutableItem.builder();

		Map<String, String> attributes = element.select("tr td").stream()
				.collect(Collectors.toMap(item -> item.attr("width"), Element::text));

		fillItem(itemBuilder, attributes);

		return itemBuilder.build();
	}

	private static void fillItem(Builder itemBuilder, Map<String, String> attributes) {

		attributes.entrySet().stream()
		.forEach(entry -> 
		Match(entry.getKey()).of(
				Case($(is("64")), key -> itemBuilder.quantity(toInteger(attributes.get(key)))),
				Case($(is("496")), key -> itemBuilder.name(attributes.get(key))),
				Case($(is("56")), key -> itemBuilder.price(toDouble(attributes.get(key)))),
				Case($(), itemBuilder)
				));
	}

	private static Double toDouble(String value) {
		return Double.valueOf(value.replaceAll(",", "."));
	}

	private static Integer toInteger(String value) {
		return Integer.valueOf(value);
	}

	public static BigDecimal getBigDecimalScaled(int value) {
		BigDecimal bd = new BigDecimal(value);
		return bd.setScale(2, BigDecimal.ROUND_HALF_UP);
	}

	public static BigDecimal getBigDecimalScaled(double value) {
		BigDecimal bd = new BigDecimal(value);
		return bd.setScale(2, BigDecimal.ROUND_HALF_UP);
	}

}
