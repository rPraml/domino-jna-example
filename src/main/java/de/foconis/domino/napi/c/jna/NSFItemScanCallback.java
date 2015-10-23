package de.foconis.domino.napi.c.jna;

import com.sun.jna.Callback;
import com.sun.jna.Pointer;

public interface NSFItemScanCallback extends Callback {
	public short itemScan(final short spare, final short itemFlags, final Pointer name, final short nameLength, final Pointer value,
			final int valueLength, final Pointer parameter);
}
