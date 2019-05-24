package com.ts.invoice.logic;

import com.ts.invoice.interfaces.IParser;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.ts.invoice.utils.ArrayUtils.toPrimitive;
import static com.ts.invoice.utils.Const.LINE_LENGTH;
import static com.ts.invoice.utils.Const.NA;

public class Parser implements IParser {

	static String train1 = "    _  _     _  _  _  _  _ ";
	static String train2 = "  | _| _||_||_ |_   ||_||_|";
	static String train3 = "  ||_  _|  | _||_|  ||_| _|";

	static String trainz1 = " _                         ";
	static String trainz2 = "| |                        ";
	static String trainz3 = "|_|                        ";

	private static final int SPACE = ' ';
	private static final int VERTICAL = '|';
	private static final int HORIZONTAL = '_';

	private Map<Integer,Byte> convertMap = new HashMap<>(); //used to convert SPACE/VERTICAL/HORIZONTAL to (0,1,2)
	private Map<Integer,Integer> numbers = new HashMap<>(); //holds the hased value for each digit.


	public Parser() {
		populateMaps();
	}
	/**
	 * prepare a convert table
	 */
	private void populateMaps() {
		//populate the conversion map.
		convertMap.put(SPACE,(byte)0);
		convertMap.put(VERTICAL,(byte)1);
		convertMap.put(HORIZONTAL,(byte)2);

		//build the hashed numbers based on the training input. 1-9
		String trainingBulk[] = new String[]{train1,train2,train3,""};
		byte[][] trainer = Transform(trainingBulk);
		for(int i=0; i < 9 ;++i) {
			int val = hashDigit(trainer, i);
			numbers.put(val,i+1);
		}
		//train Zero
		trainingBulk = new String[]{trainz1,trainz2,trainz3,""};
		int zeroVal = hashDigit(Transform(trainingBulk), 0);
		numbers.put(zeroVal,0);


	}

	/***
	 * Convert 3 lines of input into multi-dimension array of bytes representing a value to be hashed
	 * @param lines
	 * @return byte[3][27] that contains the hashed value of each cell
	 *
	 * Sample input the digit 6 :
	 * 				    _
	 * 				   |_
	 *                 |_|
     *
	 * Sample output [0] [2] [0]
	 *				 [1] [2] [0]
	 *				 [1] [2] [1]
	 *
	 */

	@Override
	public byte[][] Transform(String[] lines) {
		byte[] all = toPrimitive(
				Arrays.stream(lines)
						.filter(s->!s.isEmpty())
						.flatMap(l->l.chars().boxed())
						.map(ch->convertMap.getOrDefault(ch,(byte)NA))
						.toArray(Byte[]::new));


		//we want to work with the smallest primitive available to store 3 possible values.
		byte[][] dim = new byte[3][];
		int start = 0;

		for(int i=0 ; i < 3 ; ++i){
			dim[i] = Arrays.copyOfRange(all,start,start +LINE_LENGTH);
			start += LINE_LENGTH;
		}
		return dim;
	}

	/**
	 * Convert a single "AScii-Digit" representation into its numeric value
	 * @param data - a multi-dimension array representing the entire input
	 * @param digitPosition - the index of which the Ascii-Digit belongs (0-8 inclusive)
	 * @return int - the numeric representation of the digit.
	 *
	 *	For incorrect output the cell will be filled with -1 (NA)
	 */
	@Override
	public int ConvertDigit(byte[][] data, int digitPosition) {
		int hash = hashDigit(data,digitPosition);
		return numbers.getOrDefault(hash,NA);
	}

	private int hashDigit(byte[][] data, int digitPosition) {
		int start = digitPosition * 3;
		int end = start + 3;
		int hash = 0;
		int r = 0;
		for(int row = 0 ; row < 3 ; ++row) {
			for (int col = start ; col < end ; col ++) {
				byte cur = data[row][col];
				if(cur == NA) return NA;

				//the hash itself : value * 3^i where i is the index.
				hash += cur* Math.pow(3,r);
				r++;
			}
		}

		return hash;
	}





}
