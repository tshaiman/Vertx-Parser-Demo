import com.ts.invoice.logic.Converter;
import com.ts.invoice.processor.BulkProcessor;
import com.ts.invoice.processor.FileReaderProcessor;
import com.ts.invoice.logic.Parser;
import com.ts.invoice.processor.FileWriterProcessor;
import com.ts.invoice.utils.ArtLoader;
import io.vertx.reactivex.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;

import static com.ts.invoice.utils.Const.INPUT_CHANNEL;


public class NumericParserApp {
	static Logger logger = LoggerFactory.getLogger(NumericParserApp.class);



	public static void main(String[] args) throws IOException {
		logger.info("Invoice Parser V 1.0");
		ArtLoader.print();

		String sourceInput = "./input_Q1a.txt";
		String sourceOutput ;
		if(args.length >0 )
			sourceInput = args[0];
		if(args.length > 1)
			sourceOutput = args[1];
		else
			sourceOutput = sourceInput.replace("input","output");

		deployProcessors(sourceInput,sourceOutput);
	}

	private static void deployProcessors(String input,String output) {
		Vertx vertx = Vertx.vertx();

		//TODO : Use DI Container.
		Parser p = new Parser();
		Converter c = new Converter(p);

		BulkProcessor bulkProcessor = new BulkProcessor(c);
		FileWriterProcessor fileWriterProcessor = new FileWriterProcessor(output);

		vertx.rxDeployVerticle(fileWriterProcessor)
				.flatMap(r -> vertx.rxDeployVerticle(bulkProcessor))
				.flatMap(r -> vertx.rxDeployVerticle(FileReaderProcessor.class.getTypeName()))
				.subscribe(r -> vertx.eventBus().send(INPUT_CHANNEL, input));


	}




}
