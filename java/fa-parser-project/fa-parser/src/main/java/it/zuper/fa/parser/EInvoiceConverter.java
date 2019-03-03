package it.zuper.fa.parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.function.Supplier;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vavr.control.Try;
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

public class EInvoiceConverter {

	private static final Logger LOGGER = LoggerFactory.getLogger(EInvoiceConverter.class);

	private ObjectFactory factory = new ObjectFactory();
	private Invoice invoice;
	private int itemSequence = 1;
	private Supplier<Integer> nextVal = () -> itemSequence++;
	private FatturaElettronicaType faType  = new FatturaElettronicaType();

	private EInvoiceConverter(Invoice invoice) {
		this.invoice = invoice;
	}

	public static EInvoiceConverter valueOf(Invoice invoice) {
		return new EInvoiceConverter(invoice);
	}

	public void convert() {
		faType.setVersione(FormatoTrasmissioneType.FPR_12);
		faType.setFatturaElettronicaHeader(head());
		faType.getFatturaElettronicaBody().add(body() );
	}

	public void writeTo(Path output) throws JAXBException, FileNotFoundException {

		File xmlOut = new File(output.toFile(), "IT" + invoice.from().cf() +"_"+ invoice.number()+".xml");
		JAXBElement<FatturaElettronicaType> result = factory.createFatturaElettronica(faType);
		JAXBContext context = JAXBContext.newInstance(FatturaElettronicaType.class);
		Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.marshal(result, new FileOutputStream(xmlOut));
	}

	private FatturaElettronicaBodyType body() {
		FatturaElettronicaBodyType faBody = factory.createFatturaElettronicaBodyType();
		faBody.setDatiGenerali(generalData());
		faBody.setDatiBeniServizi(goods());
		return faBody;
	}

	private DatiBeniServiziType goods() {
		DatiBeniServiziType faDBS = factory.createDatiBeniServiziType();
		List<DettaglioLineeType> faDBSDL = faDBS.getDettaglioLinee();
		invoice.items().stream()
		.forEach(item -> faDBSDL.add(lineDetail(item)));

		faDBS.getDatiRiepilogo().add(recapData(invoice));

		return faDBS;
	}

	private DatiRiepilogoType recapData(Invoice invoice) {

		Double amountNoTax = invoice.totalAmount();
		Double amount = amountNoTax*1.22;

		DatiRiepilogoType faDRi = factory.createDatiRiepilogoType();
		faDRi.setAliquotaIVA(getBigDecimalScaled(22));
		faDRi.setImponibileImporto(getBigDecimalScaled(amountNoTax));
		faDRi.setImposta(getBigDecimalScaled(amount-amountNoTax));
		faDRi.setEsigibilitaIVA(EsigibilitaIVAType.D);
		return faDRi;
	}

	private DatiGeneraliType generalData() {
		DatiGeneraliType faDG = factory.createDatiGeneraliType();
		DatiGeneraliDocumentoType faDGD = factory.createDatiGeneraliDocumentoType();
		faDGD.setTipoDocumento(TipoDocumentoType.TD_01);
		faDGD.setDivisa("EUR");
		faDGD.setData(invoiceDate(invoice.date()));
		faDGD.setNumero(invoice.number()+"");
		faDG.setDatiGeneraliDocumento(faDGD);
		return faDG;
	}

	private XMLGregorianCalendar invoiceDate(LocalDate date) {
		GregorianCalendar gcal = GregorianCalendar.from(date.atStartOfDay(ZoneId.systemDefault()));
		return Try.of(() -> DatatypeFactory.newInstance())
				.map(dtf -> dtf.newXMLGregorianCalendar(gcal))
				.getOrElseThrow(e -> new RuntimeException(e));
	}

	private FatturaElettronicaHeaderType head() {
		FatturaElettronicaHeaderType faHead = factory.createFatturaElettronicaHeaderType();
		faHead.setDatiTrasmissione(transmissionData());
		faHead.setCedentePrestatore(cedentePrestatore(invoice.from()));
		faHead.setCessionarioCommittente(cessionario(invoice.to()));
		return faHead;
	}

	private CessionarioCommittenteType cessionario(Destination destination) {
		CessionarioCommittenteType faCC = factory.createCessionarioCommittenteType();
		DatiAnagraficiCessionarioType faCCDA = factory.createDatiAnagraficiCessionarioType();
		AnagraficaType faCCA = factory.createAnagraficaType();
		faCCA.setDenominazione(destination.name());
		faCCDA.setAnagrafica(faCCA);

		destination.pIVA()
		.ifPresent(pIVA -> {
			IdFiscaleType faCCDAIdF = factory.createIdFiscaleType();
			faCCDAIdF.setIdPaese("IT");
			faCCDAIdF.setIdCodice(pIVA.replace("IT", ""));			
			faCCDA.setIdFiscaleIVA(faCCDAIdF );
		});
		destination.cf()
		.ifPresent(cf -> {
			faCCDA.setCodiceFiscale(cf);
		});

		faCC.setDatiAnagrafici(faCCDA);
		IndirizzoType faCCS = factory.createIndirizzoType();
		faCCS.setIndirizzo(destination.address());
		faCCS.setCAP(destination.cap());
		faCCS.setComune(destination.comune());
		faCCS.setProvincia(destination.province().toUpperCase());
		faCCS.setNazione("IT");
		faCC.setSede(faCCS);
		return faCC;
	}

	private CedentePrestatoreType cedentePrestatore(From from) {
		CedentePrestatoreType faCP = factory.createCedentePrestatoreType();
		DatiAnagraficiCedenteType faDA=factory.createDatiAnagraficiCedenteType();
		IdFiscaleType faIdFIVA = factory.createIdFiscaleType();
		faIdFIVA.setIdPaese("IT");
		faIdFIVA.setIdCodice(from.pIVA().replaceAll("IT", ""));
		faDA.setIdFiscaleIVA(faIdFIVA );
		faDA.setCodiceFiscale(from.cf());
		AnagraficaType faAna = factory.createAnagraficaType();
		faAna.setDenominazione(from.name());
		faDA.setAnagrafica(faAna);
		faDA.setRegimeFiscale(RegimeFiscaleType.RF_01);
		faCP.setDatiAnagrafici(faDA);
		IndirizzoType faS = factory.createIndirizzoType();
		faS.setIndirizzo(from.address());
		faS.setCAP("30036");
		faS.setComune(from.comune());
		faS.setProvincia(from.province().toUpperCase());
		faS.setNazione("IT");
		faCP.setSede(faS );
		return faCP;
	}

	private DatiTrasmissioneType transmissionData() {


		DatiTrasmissioneType faDT = factory.createDatiTrasmissioneType();
		IdFiscaleType faIdT = factory.createIdFiscaleType();
		faIdT.setIdPaese("IT");
		faIdT.setIdCodice(invoice.from().cf());
		faDT.setIdTrasmittente(faIdT );
		faDT.setProgressivoInvio((int)(Math.random()*1000000)+"");
		faDT.setFormatoTrasmissione(FormatoTrasmissioneType.FPR_12);

		destination(faDT);

		return faDT;
	}


	private void destination(DatiTrasmissioneType faDT) {

		LOGGER.info("Search from PF {}", invoice.to().cf());
		String code = invoice.to().cf()
				.flatMap(CodesMap.getInstance()::get)
				.orElse(fromCF());

		LOGGER.info("Retrieved code: {}", code);
		if(code.contains("@")) {
			LOGGER.info("PEC Address");			
			faDT.setCodiceDestinatario("000000");
			faDT.setPECDestinatario(code);
		} else {
			faDT.setCodiceDestinatario(code);
		}
	}

	private String fromCF() {
		LOGGER.info("Search from P.IVA {}", invoice.to().pIVA());
		return invoice.to().pIVA()
				.flatMap(CodesMap.getInstance()::get)
				.orElse("XXXXXXX");
	}

	private DettaglioLineeType lineDetail(Item item) {
		DettaglioLineeType faDTL = factory.createDettaglioLineeType();
		faDTL.setNumeroLinea(nextVal.get());
		faDTL.setDescrizione(item.name());
		faDTL.setQuantita(getBigDecimalScaled(item.quantity()));
		faDTL.setUnitaMisura("PZ");
		faDTL.setPrezzoUnitario(getBigDecimalScaled(item.price()));
		faDTL.setAliquotaIVA(getBigDecimalScaled(22));
		faDTL.setPrezzoTotale(getBigDecimalScaled(item.price()*item.quantity()));
		return faDTL;

	}

	public static BigDecimal getBigDecimalScaled(double value) {
		BigDecimal bd = new BigDecimal(value);
		return bd.setScale(2, BigDecimal.ROUND_HALF_UP);
	}

}
