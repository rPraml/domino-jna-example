package de.foconis.domino.napi.c.jna;

import com.sun.jna.Callback;
import com.sun.jna.Pointer;

public interface NSFSearchCallback extends Callback {
	public short noteFound(final Pointer param, Pointer match, Pointer summaryBuffer);
}
