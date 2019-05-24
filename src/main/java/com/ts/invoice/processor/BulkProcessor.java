package com.ts.invoice.processor;

import com.ts.invoice.interfaces.IConverter;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.buffer.Buffer;
import io.vertx.reactivex.core.eventbus.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static com.ts.invoice.utils.Const.*;

public class BulkProcessor extends AbstractVerticle {
	Logger logger = LoggerFactory.getLogger(BulkProcessor.class);
	private IConverter converter ;
	public BulkProcessor(IConverter converter) {
		this.converter = converter;
	}

	List<String> bulk = new ArrayList<>();

	@Override
	public void start() throws Exception {
		logger.info("starting source Processor");
		vertx.eventBus().consumer(BULK_CHANNEL,this::onProcess);
		super.start();
	}

	private void onProcess(Message<String> tMessage) {
		String line = tMessage.body();
		updateOrFlush(line);
	}

	private void updateOrFlush(String line) {
		bulk.add(line);
		//flush
		if(bulk.size() == LINES){
			String[] lines = bulk.toArray(new String[0]);
			//send the lines to Convert Processor
			String result = converter.Convert(lines);
			//send the result down the pipeline
			vertx.eventBus().send(OUTPUT_CHANNEL,result);
			bulk.clear();
		}
	}


}
