package com.ts.invoice.model;


import static com.ts.invoice.utils.Const.*;

public class ConvertResult {

	private char[] sharedBuffer;
	private boolean isValid = true;

	public ConvertResult(){
		//we dont need thread safe array since each thread works on different segment !
		sharedBuffer = new char[N_DIGITS];
	}

	public void PutSegment(int value , int position) {
		char ch ;
		if(value == NA) {
			isValid  = false;
			ch = '?';
		}
		else
			ch = (char)(value + '0');

		sharedBuffer[position] = ch;

	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(new String(sharedBuffer));
		if(!isValid)
			sb.append(INVALID_STR);
		return sb.toString();

	}

}
