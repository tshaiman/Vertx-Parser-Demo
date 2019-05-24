package com.ts.invoice.interfaces;

public interface IParser {
	byte[][] Transform(String[] lines);
	int ConvertDigit(byte[][] data, int digitPosition);
}
