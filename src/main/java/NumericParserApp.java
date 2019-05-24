import com.ts.invoice.logic.Converter;
import com.ts.invoice.processor.BulkProcessor;
import com.ts.invoice.processor.FileReaderProcessor;
import com.ts.invoice.logic.Parser;
import io.vertx.reactivex.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.ts.invoice.utils.Const.INPUT_CHANNEL;


public class NumericParserApp {
	static Logger logger = LoggerFactory.getLogger(NumericParserApp.class);



	public static void main(String[] args) {
		logger.info("Invoice Parser V 1.0");
		logger.info("=====================");
		logger.info("Author : Tomer Shaiman");
		deployProcessors();

	}

	private static void deployProcessors() {
		Vertx vertx = Vertx.vertx();

		Parser p = new Parser();
		Converter c = new Converter(p);
		BulkProcessor bulkProcessor = new BulkProcessor(c);

		vertx.rxDeployVerticle(FileReaderProcessor.class.getTypeName())
				.flatMap(r -> vertx.rxDeployVerticle(bulkProcessor))
				//.flatMap(r -> vertx.rxDeployVerticle(SourceProcessor.class.getTypeName(),new DeploymentOptions().setInstances(instanceCounts.getSourceReader())))
				.subscribe(r -> vertx.eventBus().send(INPUT_CHANNEL, "./input_Q1b.txt"));


	}

}
