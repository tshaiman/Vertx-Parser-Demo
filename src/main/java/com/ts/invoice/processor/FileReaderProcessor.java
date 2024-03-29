package com.ts.invoice.processor;


import io.vertx.core.file.OpenOptions;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.eventbus.Message;
import io.vertx.reactivex.core.file.AsyncFile;
import io.vertx.reactivex.core.file.FileSystem;
import io.vertx.reactivex.core.parsetools.RecordParser;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import static com.ts.invoice.utils.Const.*;

public class FileReaderProcessor extends AbstractVerticle {
	Logger logger = LoggerFactory.getLogger(FileReaderProcessor.class);

	@Override
	public void start() throws Exception {
		logger.info("starting source Processor");
		vertx.eventBus().consumer(INPUT_CHANNEL,this::onProcess);
		super.start();
	}

	private void onProcess(Message<String> tMessage) {
		String path = tMessage.body();
		logger.info("Starting file stream processor on source {}",path);
		startFileSource(path);
	}

	private void startFileSource(String path) {
		FileSystem fs = vertx.fileSystem();
		if (!Files.exists(Paths.get(path))) {
			logger.error("The file input " + path + " does not exists. Exiting");
			System.exit(1);
			return;
		}
		fs.open(path, new OpenOptions().setCreate(false), result -> {
			if (result.succeeded()) {
				AsyncFile asyncFile = result.result();
				asyncFile.handler(RecordParser.newDelimited("\n", buf->{
					String line = normalize(buf.toString("UTF-8"));
					vertx.eventBus().send(BULK_CHANNEL,line);
				}))
				.endHandler(v -> {
					logger.info("File Reader completed. sending EOF");
					vertx.eventBus().send(BULK_CHANNEL,EOF);
					asyncFile.close();
				});
			}
		});
	}

	private String normalize(String line){
		if (line.length() == LINE_LENGTH || line.isEmpty())
			return line;
		return line + StringUtils.repeat(" ", LINE_LENGTH - line.length());
	}




}
