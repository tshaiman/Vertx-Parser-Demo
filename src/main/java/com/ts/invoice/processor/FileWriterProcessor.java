package com.ts.invoice.processor;

import io.vertx.core.file.OpenOptions;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.buffer.Buffer;
import io.vertx.reactivex.core.eventbus.Message;
import io.vertx.reactivex.core.file.AsyncFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Paths;

import static com.ts.invoice.utils.Const.EOF;
import static com.ts.invoice.utils.Const.OUTPUT_CHANNEL;

public class FileWriterProcessor extends AbstractVerticle {
	Logger logger = LoggerFactory.getLogger(FileReaderProcessor.class);

	private String outputPath;
	private AsyncFile file;

	public FileWriterProcessor(String outputName) {
		outputPath = outputName;
	}

	@Override
	public void start() throws Exception {
		logger.info("starting file writer Processor on outputPath file: {}", outputPath);
		vertx.eventBus().consumer(OUTPUT_CHANNEL, this::onProcess);
		Files.deleteIfExists(Paths.get(outputPath));

		OpenOptions options = new OpenOptions().setWrite(true).setCreateNew(true);
		//create the file
		vertx.fileSystem().open(outputPath, options, ar -> {
			if (ar.succeeded()) {
				file = ar.result();
			} else {
				logger.error("Could not open file " ,ar.cause());
			}
		});

		super.start();
	}



	private void onProcess(Message<String> tMessage) {
		String line = tMessage.body();
		if(line.isEmpty()) return;

		if(line.equals(EOF)){ //we got the EOF message
			logger.info("File Writter completed. closing file");
			file.flush();
			file.close();
			return;
		}

		line = line + "\n";
		Buffer chunk = Buffer.buffer(line);
		file.write(chunk);

	}
}