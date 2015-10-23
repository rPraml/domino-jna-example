package de.foconis.domino.napi.types;

/**
 * Atuell werden für DHANDLES immer 32 Bit verwendet
 */
public class DHANDLE {
	public final int value;

	public DHANDLE(final int v) {
		value = v;
	}

	public static int nativeValue(final DHANDLE handle) {
		return handle == null ? 0 : handle.value;
	}

}
