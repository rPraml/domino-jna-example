package de.foconis.domino.napi.types;

import java.nio.ByteBuffer;

public enum CUtil {
	;
	private static java.lang.reflect.Method addressMethod;
	static {
		try {
			Class<?> dbClass = Class.forName("sun.nio.ch.DirectBuffer");
			addressMethod = dbClass.getDeclaredMethod("address", new Class[0]);
		} catch (Throwable error) {
			error.printStackTrace();
			throw new UnsupportedOperationException("Method Struct.address() not supported on this platform.");
		}
	}

	public static long address(final ByteBuffer buf) {
		try {
			return ((Long) addressMethod.invoke(buf, (Object[]) null)).longValue();
		} catch (Throwable error) {
			error.printStackTrace();
			throw new UnsupportedOperationException("Method Struct.address() not supported on this platform.");
		}
	}
}
