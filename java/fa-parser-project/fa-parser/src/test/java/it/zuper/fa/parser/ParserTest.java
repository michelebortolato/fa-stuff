package it.zuper.fa.parser;

import static it.zuper.fa.parser.DataExtractor.ADDRESS_PATTERN;
import static it.zuper.fa.parser.DataExtractor.FROM_PATTERN;
import static it.zuper.fa.parser.DataExtractor.GET_ADDRESS_QUERY;
import static it.zuper.fa.parser.DataExtractor.GET_ADDRESS_ROAD_QUERY;
import static it.zuper.fa.parser.DataExtractor.GET_CLIENT_QUERY;
import static it.zuper.fa.parser.DataExtractor.GET_DATE_QUERY;
import static it.zuper.fa.parser.DataExtractor.GET_FROM_QUERY;
import static it.zuper.fa.parser.DataExtractor.document;
import static it.zuper.fa.parser.DataExtractor.getFirst;
import static it.zuper.fa.parser.DataExtractor.getItems;
import static it.zuper.fa.parser.DataExtractor.getPIva;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.net.URI;
import java.nio.file.Paths;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.log4j.BasicConfigurator;
import org.jsoup.nodes.Document;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import it.zuper.fa.parser.beans.AnagraficaType;
import it.zuper.fa.parser.beans.CedentePrestatoreType;
import it.zuper.fa.parser.beans.CessionarioCommittenteType;
import it.zuper.fa.parser.beans.DatiAnagraficiCedenteType;
import it.zuper.fa.parser.beans.DatiAnagraficiCessionarioType;
import it.zuper.fa.parser.beans.DatiBeniServiziType;
import it.zuper.fa.parser.beans.DatiGeneraliDocumentoType;
import it.zuper.fa.parser.beans.DatiGeneraliType;
import it.zuper.fa.parser.beans.DatiRiepilogoType;
import it.zuper.fa.parser.beans.DatiTrasmissioneType;
import it.zuper.fa.parser.beans.Destination;
import it.zuper.fa.parser.beans.DettaglioLineeType;
import it.zuper.fa.parser.beans.EsigibilitaIVAType;
import it.zuper.fa.parser.beans.FatturaElettronicaBodyType;
import it.zuper.fa.parser.beans.FatturaElettronicaHeaderType;
import it.zuper.fa.parser.beans.FatturaElettronicaType;
import it.zuper.fa.parser.beans.FormatoTrasmissioneType;
import it.zuper.fa.parser.beans.From;
import it.zuper.fa.parser.beans.IdFiscaleType;
import it.zuper.fa.parser.beans.IndirizzoType;
import it.zuper.fa.parser.beans.Invoice;
import it.zuper.fa.parser.beans.Item;
import it.zuper.fa.parser.beans.ObjectFactory;
import it.zuper.fa.parser.beans.RegimeFiscaleType;
import it.zuper.fa.parser.beans.TipoDocumentoType;

@Ignore("Too long")
public class ParserTest {

	private FatturaElettronicaType fa;
	private Class clazz = ParserTest.class;

	static {
		BasicConfigurator.configure();
	}

	@Before
	public void before() {
		this.fa = new FatturaElettronicaType();
	}

	@Test
	public void marshallTest() throws Exception {

		URI uri = getClass().getClassLoader().getResource("xsd/Schema_del_file_xml_FatturaPA_versione_1.2.xsd").toURI();
		JAXBContext context = JAXBContext.newInstance(FatturaElettronicaType.class);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		ObjectFactory factory = new ObjectFactory();
		FatturaElettronicaType faType = new FatturaElettronicaType();
		FormatoTrasmissioneType version = FormatoTrasmissioneType.FPR_12;
		faType.setVersione(version);
		FatturaElettronicaHeaderType faHead = factory.createFatturaElettronicaHeaderType();

		DatiTrasmissioneType faDT = factory.createDatiTrasmissioneType();
		IdFiscaleType faIdT = factory.createIdFiscaleType();
		faIdT.setIdPaese("IT");
		faIdT.setIdCodice("BRTMHL85E31F241R");
		faDT.setIdTrasmittente(faIdT );
		faDT.setProgressivoInvio((int)(Math.random()*1000000)+"");
		faDT.setFormatoTrasmissione(FormatoTrasmissioneType.FPR_12);
		faDT.setCodiceDestinatario("000000");

		faHead.setDatiTrasmissione(faDT);

		CedentePrestatoreType faCP = factory.createCedentePrestatoreType();
		DatiAnagraficiCedenteType faDA=factory.createDatiAnagraficiCedenteType();
		IdFiscaleType faIdFIVA = factory.createIdFiscaleType();
		faIdFIVA.setIdPaese("IT");
		faIdFIVA.setIdCodice("01234567890");
		faDA.setIdFiscaleIVA(faIdFIVA );
		faDA.setCodiceFiscale("BRTDNL43E34L123X");
		AnagraficaType faAna = factory.createAnagraficaType();
		faAna.setDenominazione("XXX YYGB SPA");
		faDA.setAnagrafica(faAna );
		faDA.setRegimeFiscale(RegimeFiscaleType.RF_01);
		faCP.setDatiAnagrafici(faDA);
		IndirizzoType faS = factory.createIndirizzoType();
		faS.setIndirizzo("VIA RIO 17");
		faS.setCAP("30036");
		faS.setComune("S.M. di SALA");
		faS.setProvincia("VE");
		faS.setNazione("IT");
		faCP.setSede(faS );
		faHead.setCedentePrestatore(faCP );

		CessionarioCommittenteType faCC = factory.createCessionarioCommittenteType();
		DatiAnagraficiCessionarioType faCCDA = factory.createDatiAnagraficiCessionarioType();
		AnagraficaType faCCA = factory.createAnagraficaType();
		faCCA.setDenominazione("SELLER SPA");
		faCCDA.setAnagrafica(faCCA );
		IdFiscaleType faCCDAIdF = factory.createIdFiscaleType();
		faCCDAIdF.setIdPaese("IT");
		faCCDAIdF.setIdCodice("01234567890");
		faCCDA.setIdFiscaleIVA(faCCDAIdF );
		faCC.setDatiAnagrafici(faCCDA);

		IndirizzoType faCCS = factory.createIndirizzoType();
		faCCS.setIndirizzo("VIA XXX 17");
		faCCS.setCAP("30030");
		faCCS.setComune("S.M. di SALA");
		faCCS.setProvincia("PD");
		faCCS.setNazione("IT");
		faCC.setSede(faCCS );
		faHead.setCessionarioCommittente(faCC );
		faType.setFatturaElettronicaHeader(faHead);
		/*BODY*/
		FatturaElettronicaBodyType faBody = factory.createFatturaElettronicaBodyType();

		DatiGeneraliType faDG = factory.createDatiGeneraliType();
		DatiGeneraliDocumentoType faDGD = factory.createDatiGeneraliDocumentoType();
		faDGD.setTipoDocumento(TipoDocumentoType.TD_01);
		faDGD.setDivisa("EUR");
		GregorianCalendar c = new GregorianCalendar();
		c.setTime(new Date());
		XMLGregorianCalendar faData = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
		faDGD.setData(faData);
		faDGD.setNumero("123");
		faDG.setDatiGeneraliDocumento(faDGD );
		faBody.setDatiGenerali(faDG );


		DatiBeniServiziType faDBS = factory.createDatiBeniServiziType();
		List<DettaglioLineeType> faDBSDL = faDBS.getDettaglioLinee();
		DettaglioLineeType faDTL = factory.createDettaglioLineeType();
		faDTL.setNumeroLinea(1);
		faDTL.setDescrizione("ART 1");
		faDTL.setQuantita(getBigDecimalScaled(20));
		faDTL.setUnitaMisura("PZ");
		faDTL.setPrezzoUnitario(getBigDecimalScaled(30));
		faDTL.setAliquotaIVA(getBigDecimalScaled(22));
		faDTL.setPrezzoTotale(getBigDecimalScaled(100));
		faDBSDL.add(faDTL);

		DettaglioLineeType faDTL2 = factory.createDettaglioLineeType();
		faDTL2.setNumeroLinea(2);
		faDTL2.setDescrizione("ART 2");
		faDTL2.setQuantita(getBigDecimalScaled(1));
		faDTL2.setUnitaMisura("PZ");
		faDTL2.setPrezzoUnitario(getBigDecimalScaled(200));
		faDTL2.setAliquotaIVA(getBigDecimalScaled(42));
		faDTL2.setPrezzoTotale(getBigDecimalScaled(700));
		faDBSDL.add(faDTL2);

		
		List<DatiRiepilogoType> faDR = faDBS.getDatiRiepilogo();
		DatiRiepilogoType faDRi = factory.createDatiRiepilogoType();
		faDRi.setAliquotaIVA(getBigDecimalScaled(22));
		faDRi.setImponibileImporto(getBigDecimalScaled(200));
		faDRi.setImposta(getBigDecimalScaled(10));
		faDRi.setEsigibilitaIVA(EsigibilitaIVAType.D);
		faDR.add(faDRi);
		faBody.setDatiBeniServizi(faDBS);
		faType.getFatturaElettronicaBody().add(faBody );

		JAXBElement<FatturaElettronicaType> result = factory.createFatturaElettronica(faType);
		Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.marshal(result, baos);

		byte[] resultByte = baos.toByteArray();
		System.out.println(new String(resultByte));
		SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema schema = sf.newSchema(new File(uri));
		Unmarshaller unmarshaller = context.createUnmarshaller();
		unmarshaller.setSchema(schema);
		try {
			unmarshaller.unmarshal(new ByteArrayInputStream(resultByte));
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}

	}

	@Test
	public void marshallFileTest() throws Exception {

		URI uri = getClass().getClassLoader().getResource("xsd/Schema_del_file_xml_FatturaPA_versione_1.2.xsd").toURI();
		URI xmlUri = getClass().getClassLoader().getResource("IT01234567890_FPR02.xml").toURI();
		JAXBContext context = JAXBContext.newInstance(FatturaElettronicaType.class);

		SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema schema = sf.newSchema(new File(uri));
		Unmarshaller unmarshaller = context.createUnmarshaller();
		unmarshaller.setSchema(schema);
		try {
			unmarshaller.unmarshal(new FileInputStream(new File(xmlUri)));
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}

	}

	@Test
	public void parseInvoice() throws Exception {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectFactory factory = new ObjectFactory();
		
		URI uri = getClass().getClassLoader().getResource("invoice.html").toURI();
		Document document = document(Paths.get(uri));
		Invoice invoice = Parser.parse(document);
		System.out.println("Invoice : " + invoice);
		EInvoiceConverter converter = EInvoiceConverter.valueOf(invoice);
		
		converter.convert();
		
		

		byte[] resultByte = baos.toByteArray();
		System.out.println("Result: " + new String(resultByte));
	}

	@Test
	public void parseFolder() throws Exception {

		URI uri = getClass().getClassLoader().getResource("examples").toURI();
		Parser.parseFolder(Paths.get(uri));
	}

	@Test
	public void parseHtml() throws Exception {

		URI uri = getClass().getClassLoader().getResource("invoice.html").toURI();

		Document document = document(Paths.get(uri));

		List<Item> items = getItems(document)		
				.stream()
				.map(BeanConverter::convert)
				.collect(Collectors.toList());

		assertThat(items).isNotEmpty();
		assertThat(items.size()).isEqualTo(4);

		String pIva = getPIva(document)
				.orElse("");

		assertThat(pIva).isEqualTo("04394960274");

		String address = getFirst(GET_ADDRESS_QUERY, document)
				.orElse("");

		assertThat(address).isEqualTo("30030 - DOLO (ve)");
		Matcher matcher = ADDRESS_PATTERN.matcher(address);
		assertThat(matcher.matches()).isTrue();
		assertThat(matcher.group(1)).isEqualTo("30030");
		assertThat(matcher.group(2)).isEqualTo("DOLO");
		assertThat(matcher.group(3)).isEqualTo("ve");


		String addressRoad = getFirst(GET_ADDRESS_ROAD_QUERY, document)
				.orElse("");

		assertThat(addressRoad).isEqualTo("VIA GARIBALDI 59");

		String client = getFirst(GET_CLIENT_QUERY, document)
				.orElse("");

		assertThat(client).isEqualTo("AEDO SAS");

		String date = getFirst(GET_DATE_QUERY, document)
				.orElse("");

		assertThat(date).isEqualTo("07/04/2018");

		Destination destination = BeanConverter.buildDestination(document);

		System.out.println("Destination: " + destination);

		String from = getFirst(GET_FROM_QUERY, document)
				.orElse("");

		matcher = FROM_PATTERN.matcher(from);
		assertThat(matcher.matches()).isTrue();
		assertThat(matcher.group(1)).isEqualTo("BORTOLATO DANIELE");
		assertThat(matcher.group(2)).isEqualTo("Santa Maria di Sala");
		assertThat(matcher.group(3)).isEqualTo("VE");
		assertThat(matcher.group(4)).isEqualTo("Via Rio 17/a");
		assertThat(matcher.group(5)).isEqualTo("00809380272");
		assertThat(matcher.group(6)).isEqualTo("BRTDNL57S10I242R");

		From fromBean = BeanConverter.buildFrom(document);

		System.out.println("From: " + fromBean);

		System.out.println("Finish");
	}
	
	public static BigDecimal getBigDecimalScaled(int value) {
		BigDecimal bd = new BigDecimal(value);
		return bd.setScale(2, BigDecimal.ROUND_HALF_UP);
	}

}
