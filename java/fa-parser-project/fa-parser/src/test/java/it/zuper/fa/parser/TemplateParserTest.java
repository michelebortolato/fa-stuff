package it.zuper.fa.parser;

import static org.junit.Assert.*;

import java.io.File;
import java.net.URI;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.ServiceLoader;
import java.util.TimeZone;

import org.apache.log4j.BasicConfigurator;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapterFactory;

import it.zuper.fa.parser.beans.GsonAdaptersOffice;
import it.zuper.fa.parser.beans.GsonAdaptersPersonInfo;
import it.zuper.fa.parser.beans.GsonAdaptersTemplate;
import it.zuper.fa.parser.beans.ImmutableTemplate;
import it.zuper.fa.parser.beans.Template;

public class TemplateParserTest {

	private static Gson GSON_BUILDER;

	static {
		GsonBuilder gsonBuilder = new GsonBuilder();
		for (TypeAdapterFactory factory : ServiceLoader.load(TypeAdapterFactory.class)) {
			gsonBuilder.registerTypeAdapterFactory(factory);
		}
		gsonBuilder.registerTypeAdapterFactory(new GsonAdaptersTemplate());
		gsonBuilder.registerTypeAdapterFactory(new GsonAdaptersPersonInfo());
		gsonBuilder.registerTypeAdapterFactory(new GsonAdaptersOffice());
		GSON_BUILDER = gsonBuilder.create();	
	}


	static {
		BasicConfigurator.configure();
	}

	@Test
	public void parse() throws Exception {
		URI uri = getClass().getClassLoader().getResource("templates/BALDAN.json").toURI();
		byte[] content = Files.readAllBytes(new File(uri).toPath());
		Template item = GSON_BUILDER.fromJson(new String(content), Template.class);

		System.out.println("Denominazione: " + item.personInfo().denominazione());
		System.out.println("Test: " + item.pricedItems());

	}

	@Test
	public void dateTest() {
		System.out.println(GregorianCalendar.getInstance().getTime());
		System.out.println(GregorianCalendar.getInstance().getTimeZone().getID());

		Calendar cal = GregorianCalendar.getInstance();
		Date date = cal .getTime();
		TimeZone tz = cal.getTimeZone();

		System.out.println("input calendar has date [" + date + "]");

		//Returns the number of milliseconds since January 1, 1970, 00:00:00 GMT 
		long msFromEpochGmt = date.getTime();

		//gives you the current offset in ms from GMT at the current date
		int offsetFromUTC = tz.getOffset(msFromEpochGmt);
		System.out.println("offset is " + offsetFromUTC);

		//create a new calendar in GMT timezone, set to this date and add the offset
		Calendar gmtCal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		gmtCal.setTime(date);
		gmtCal.add(Calendar.MILLISECOND, offsetFromUTC);

		System.out.println("Created GMT cal with date [" + gmtCal.getTime() + "]");

		Calendar sigTime = new GregorianCalendar();
		sigTime.setTimeZone(TimeZone.getTimeZone("WET"));
		sigTime.add(Calendar.HOUR, 1);
		
		System.out.println(sigTime.getTime());
		
		String format = new SimpleDateFormat("yyyyMMdd HH:mm").format(sigTime.getTime());
		System.out.println("Format: " + format);

	}

}
