package com.ts.invoice.utils;

public  class ArrayUtils {

	public static byte[] toPrimitive(Byte[] all){
		byte[] arr = new byte[all.length];
		for(int i=0 ; i< all.length;++i)
			arr[i] = all[i];
		return arr;
	}


}
