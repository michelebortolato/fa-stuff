package it.zuper.fa;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vavr.control.Try;
import it.zuper.fa.parser.InvoiceScanner;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "example", mixinStandardHelpOptions=true, version="1.0.0")
public class App implements Runnable{

	private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

	static {
		BasicConfigurator.configure();
	}

	@Option(names = { "-v", "--verbose" }, description = "Verbose mode. Helpful for troubleshooting. " +
			"Multiple -v options increase the verbosity.")
	private boolean[] verbose = new boolean[0];
	
	@Option(names = {"-o", "--output_folder"}, description = "Output folder")
	private File outputFolder;
	
	@Option(names = {"-t", "--template_folder"}, description = "Template folder", required=false)
	private File templateFolder;
	
	@Option(names = {"-n", "--number"}, description = "First invoice number", required=true)
	private Long firstInvoiceNumber;
	
	@Parameters(arity = "1", paramLabel = "Path", description = "File to process.")
    private File inputFile;

	public static void main(String[] args) {
		
		CommandLine.run(new App(),
				"-n", "14",
				"-o", "C:\\Users\\Michele\\eclipse-workspace\\fa-parser",
				"C:\\Users\\Michele\\eclipse-workspace\\fa-parser\\src\\test\\resources\\examples\\costantino.csv");
	}

	@Override
	public void run() {
		
		Path inputPath = Paths.get(inputFile.getAbsolutePath());
		LOGGER.info("Scanning {}", inputFile);
		
		Try.run( () -> {
			InvoiceScanner is = new InvoiceScanner(firstInvoiceNumber, inputPath);
			is.parse(outputFolder.toPath());
		})
		.onFailure(fail -> LOGGER.warn("Some error occurred: {}", fail))
		.onSuccess(v -> LOGGER.info("Done successfully."));
		
	}
}
