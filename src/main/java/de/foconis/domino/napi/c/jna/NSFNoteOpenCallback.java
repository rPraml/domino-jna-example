package de.foconis.domino.napi.c.jna;

import com.sun.jna.Callback;
import com.sun.jna.Pointer;

public interface NSFNoteOpenCallback extends Callback {
	public short noteOpen(final Pointer param, final int hNote, final int noteId, final int status);
}
