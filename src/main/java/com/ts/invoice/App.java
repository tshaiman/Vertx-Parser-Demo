package com.ts.invoice;

import com.ts.invoice.logic.Converter;
import com.ts.invoice.logic.Parser;
import com.ts.invoice.processor.BulkProcessor;
import com.ts.invoice.processor.FileReaderProcessor;
import com.ts.invoice.processor.FileWriterProcessor;
import com.ts.invoice.utils.ArtLoader;
import io.vertx.reactivex.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static com.ts.invoice.utils.Const.INPUT_CHANNEL;


public class App {
	static Logger logger = LoggerFactory.getLogger(App.class);



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
			sourceOutput = "./outputg_Q1a.txt";

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
