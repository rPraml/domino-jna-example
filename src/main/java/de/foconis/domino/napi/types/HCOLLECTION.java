package de.foconis.domino.napi.types;

/**
 * typedef WORD HCOLLECTION; Handle to NIF collection
 */
public class HCOLLECTION {
	public final short value;

	public HCOLLECTION(final short v) {
		value = v;
	}
}
