package de.foconis.domino.napi.types;

import de.foconis.domino.napi.MemBuffer;

public interface CStruct {

	/**
	 * Gibt die Größe der Struktur zurück
	 * 
	 * @return
	 */
	public int size();

	/**
	 * Schreibt die Bytes an der Stelle "ofs" in den Speicher, gibt die Anzahl der geschriebenen Bytes zurück.
	 */
	public void writeBuffer(MemBuffer buffer);

	/**
	 * Liest & parsed die Struktur aus den Bytebuffer ab "ofs", gibt die Anzahl der gelesenen Bytes zurück.
	 */
	public void readBuffer(MemBuffer buffer);

}
