import com.ts.invoice.logic.Converter;
import com.ts.invoice.processor.BulkProcessor;
import com.ts.invoice.processor.FileReaderProcessor;
import com.ts.invoice.logic.Parser;
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

		String sourceInput = "./input_Q1b.txt";
		if(args.length >0 )
			sourceInput = args[0];

		deployProcessors(sourceInput);
	}

	private static void deployProcessors(String input) {
		Vertx vertx = Vertx.vertx();

		Parser p = new Parser();
		Converter c = new Converter(p);
		BulkProcessor bulkProcessor = new BulkProcessor(c);

		vertx.rxDeployVerticle(FileReaderProcessor.class.getTypeName())
				.flatMap(r -> vertx.rxDeployVerticle(bulkProcessor))
				//.flatMap(r -> vertx.rxDeployVerticle(SourceProcessor.class.getTypeName(),new DeploymentOptions().setInstances(instanceCounts.getSourceReader())))
				.subscribe(r -> vertx.eventBus().send(INPUT_CHANNEL, input));


	}




}
