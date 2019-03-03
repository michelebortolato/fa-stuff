package it.zuper.fa.parser;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.ServiceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapterFactory;

import io.vavr.control.Option;
import io.vavr.control.Try;
import it.zuper.fa.parser.beans.GsonAdaptersOffice;
import it.zuper.fa.parser.beans.GsonAdaptersPersonInfo;
import it.zuper.fa.parser.beans.GsonAdaptersTemplate;
import it.zuper.fa.parser.beans.Template;

public class TemplateCache {

	private static final Logger LOGGER = LoggerFactory.getLogger(TemplateCache.class);

	private static Gson GSON_BUILDER;

	private Map<String, Template> cache = new HashMap<String, Template>();

	private String templateFolder = null;

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

	private static TemplateCache INSTANCE = new TemplateCache();

	public static TemplateCache getInstance() {
		return INSTANCE;
	}

	public void withTemplateFolder(String templateFolder) {
		this.templateFolder = templateFolder;
	}

	public Option<Template> getTemplate(String name) {
		return Option.of(cache.get(name))
				.orElse(() -> getTemplateFromResource(name));
	}

	public Option<Template> getTemplateFromResource(String name) {

		Option<String> resourceOpt = null;
		if(Objects.isNull(templateFolder)) {			
			resourceOpt = pickFileFromClassPath(name);
		} else {
			resourceOpt = pickFileFromFolder(name);
		}
		return resourceOpt.flatMap(this::parseTemplateFile)
				.peek(template -> cache.put(name, template));

	}

	private Option<String> pickFileFromClassPath(String name) {
		URL resource = getClass().getClassLoader().getResource("templates/" + name + ".json");
		return Option.of(resource)
				.map(URL::getFile);
	}

	private Option<String> pickFileFromFolder(String name) {
		return Option.of(Paths.get(templateFolder, name+".json"))
				.filter(path -> path.toFile().exists())
				.map(Path::toString	);
	}


	public Option<Template> parseTemplateFile(String path) {

		LOGGER.info("Reading template {}", path);
		return Try.of(() -> Files.readAllBytes(new File(path).toPath()))
				.map(String::new)
				.map(content -> GSON_BUILDER.fromJson(new String(content), Template.class))
				.onFailure(fail -> LOGGER.warn("Error reading template {}", fail))
				.toOption();
	}
}
