package com.ts.invoice.logic;

import com.ts.invoice.interfaces.IConverter;
import com.ts.invoice.interfaces.IParser;
import com.ts.invoice.model.ConvertResult;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.ts.invoice.utils.Const.*;

public class Converter implements IConverter {

	private IParser parser;
	ExecutorService executor = Executors.newFixedThreadPool(16);

	public Converter(IParser parser) {
		this.parser = parser;
	}

	public String Convert(String[] lines) {
		//phase 1 : validate
		if (!Validate(lines)) {
			return "NA";
		}
		ConvertResult convertResult = new ConvertResult();
		//phase 2 : convert to multi-dim array of bytes , representing a hashed value
		byte[][] hashedLine = parser.Transform(lines);

		//phase 3 : convert each Ascii digit to its numeric representation using Threads.
		CountDownLatch countDownLatch = new CountDownLatch(9);

		for (int i = 0; i < N_DIGITS; ++i) {
			int index = i;
			executor.submit(() -> {
				int localThreadResult = parser.ConvertDigit(hashedLine, index);
				convertResult.PutSegment(localThreadResult, index);
				countDownLatch.countDown();
			});
		}

		try {
			if (!countDownLatch.await(2, TimeUnit.SECONDS))
				return "Incomplete Task";
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return convertResult.toString();
	}

	/**
	 * Valudaion on the correctness of the input
	 *
	 * @param lines : 4 lines ,27 chars each except the last which is empty
	 * @return true if the validation succeeded, otherwise false
	 */
	private boolean Validate(String[] lines) {

		if (lines.length != LINES)
			return false;
		if (!lines[3].isEmpty())
			return false;

		return true;
	}


}
