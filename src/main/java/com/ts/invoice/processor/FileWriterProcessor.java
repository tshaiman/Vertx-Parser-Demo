package com.ts.invoice.processor;

import io.vertx.core.file.OpenOptions;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.buffer.Buffer;
import io.vertx.reactivex.core.eventbus.Message;
import io.vertx.reactivex.core.file.AsyncFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.ts.invoice.utils.Const.INPUT_CHANNEL;
import static com.ts.invoice.utils.Const.OUTPUT_CHANNEL;

public class FileWriterProcessor extends AbstractVerticle {
	Logger logger = LoggerFactory.getLogger(FileReaderProcessor.class);
	private String output;
	private AsyncFile file;

	public FileWriterProcessor(String outputName) {
		output = outputName;
	}

	@Override
	public void start() throws Exception {
		logger.info("starting file writer Processor on output file: {}",output);
		vertx.eventBus().consumer(OUTPUT_CHANNEL, this::onProcess);

		//create the file
		vertx.fileSystem().open(output, new OpenOptions().setAppend(true), ar -> {
			if (ar.succeeded()) {
				file = ar.result();
			} else {
				System.err.println("Could not open file");
			}
		});

		super.start();
	}

	private void onProcess(Message<String> tMessage) {
		String line = tMessage.body();
		if(line.isEmpty()) return;
		line = line + "\n";
		Buffer chunk = Buffer.buffer(line);
		file.write(chunk);
		file.flush(); // we dont need to do that for every line
	}
}