/*
 * Copyright 2015 - FOCONIS AG
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at:
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express o 
 * implied. See the License for the specific language governing 
 * permissions and limitations under the License.
 * 
 */
package de.foconis.domino.napi.c.jna;


import com.ibm.domino.napi.NException;
import com.ibm.domino.napi.c.BackendBridge;
import com.ibm.domino.napi.c.NotesUtil;
import com.ibm.domino.napi.c.Os;
import com.sun.jna.Callback;
import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;

import de.foconis.domino.napi.MemBuffer;
import de.foconis.domino.napi.Napi;
import de.foconis.domino.napi.NapiException;
import de.foconis.domino.napi.c.MemBufferImpl;
import de.foconis.domino.napi.types.DBHANDLE;
import de.foconis.domino.napi.types.DHANDLE;
import de.foconis.domino.napi.types.NOTEHANDLE;
import lotus.domino.Database;
import lotus.domino.Document;

/**
 * Implementierung der {@link NapiEx} Funktionen unter Zuhilfename der JNA (https://github.com/java-native-access/jna)
 *
 * Konvention: Alle Aufrufe werden an die statischen Methoden in der {@link NNotes} weiterdelegiert. Eventuelle Rückgabefehlercodes wandelt
 * diese Klasse in eine Java-konforme {@link NapiException} um
 * 
 * @author Roland Praml, FOCONIS AG
 *
 */
public class JNANapi implements Napi{

	public static short _NOTE_OID = 2;


	/**
	 * Öffnet gleich mehrere Notes auf einmal. Dies ist im Wesentlichen nur ein Wrapper für {@link NNotes#NSFDbGetNotes} welcher den
	 * ErrorCode in eine Exception umwandelt
	 * 
	 * @throws NapiException
	 *             mit ErrorCode
	 */
	protected void NSFDbGetNotes(//
			final DBHANDLE hDB, //
			final int NumNotes, //
			final int[] NoteIDs, //
			final int[] NoteOpenFlags, //
			final int[] SinceSeqNum, //
			final int ControlFlags, //
			final DBHANDLE hObjectDB, //
			final Pointer CallbackParam, //
			final Callback GetNotesCallback, //
			final NSFNoteOpenCallback NoteOpenCallback, //
			final Callback ObjectAllocCallback, //
			final Callback ObjectWriteCallback, //
			final Pointer FolderSinceTime, // 
			final Pointer FolderAddCallback) throws NapiException {

		short status = NNotes.NSFDbGetNotes(DHANDLE.nativeValue(hDB),//
				NumNotes, //
				NoteIDs, //
				NoteOpenFlags, //
				SinceSeqNum, //
				ControlFlags, //
				DHANDLE.nativeValue(hObjectDB), //
				CallbackParam, //
				GetNotesCallback, //
				NoteOpenCallback, //
				ObjectAllocCallback, //
				ObjectWriteCallback, //
				FolderSinceTime, //
				FolderAddCallback);
		if (status != NOERROR) {
			throw new NapiException(status);
		}
	}

	/**
	 * Einfachere Variante, mit weniger Parametern
	 * 
	 * @param hDB
	 *            das DB-Handle
	 * @param maxEntries
	 *            maximale Anzahl von Entries
	 * @param NoteIDs
	 *            noteIDs
	 * @param NoteOpenFlags
	 *            Flags, gilt für alle NoteIDs
	 * @param ControlFlags
	 *            ControlFlags
	 * @param NoteOpenCallback
	 *            Callback
	 * @throws NapiException
	 *             im Fehlerfall
	 */
	protected void NSFDbGetNotes(//
			final DBHANDLE hDB, //
			final int maxEntries, //
			final int[] NoteIDs, //
			final int NoteOpenFlags, //
			final int ControlFlags, //
			final NSFNoteOpenCallback NoteOpenCallback) throws NapiException {
		int NumNotes = NoteIDs.length;
		if (NumNotes > maxEntries) {
			NumNotes = maxEntries;
		}

		int[] openFlags = new int[NumNotes];
		for (int i = 0; i < openFlags.length; i++) {
			openFlags[i] = NoteOpenFlags;
		}
		int[] sinceSeqNum = new int[NumNotes];
		NSFDbGetNotes(hDB, //
				NumNotes,// 
				NoteIDs, //
				openFlags,//
				sinceSeqNum, ControlFlags,//
				null, // hObjectDB
				null, // callbackParam
				null, //  GetNotesCallback,
				NoteOpenCallback, //
				null, // ObjectAllocCallback
				null, // ObjectWriteCallback
				null, // FolderSinceTime
				null); // FolderAddCallback
	}

	// =================== OS ======================
	// =================== OS ======================
	// =================== OS ======================

	/**
	 * Liefert den Fehlertext zu StringCode
	 */
	@Override
	public String OSLoadString(final int StringCode) {
		return OSLoadString(null, StringCode);
	}

	/**
	 * Liefert den Fehlertext zu Module + StringCode (nur der Vollständigkeit, wird i.d.R. nicht benötigt)
	 */
	public String OSLoadString(final Pointer hModule, final int StringCode) {
		int bufSize = 0x4000;
		Pointer retBuffer = new Memory(bufSize);
		int len = NNotes.OSLoadString(hModule, StringCode, retBuffer, (short) (bufSize - 1));
		if (len == 0) {
			return "Error#" + StringCode;
		}
		return NotesUtil.fromLMBCSLen(Pointer.nativeValue(retBuffer), 0, len);

	}

	/**
	 * Iteriert über alle Items in der Note. Siehe {@link NNotes#NSFItemScan(int, Callback, Pointer)} Wertet den Fehlercode aus
	 * 
	 * @throws NapiException
	 *             im Fehlerfall
	 */
	protected void NSFItemScan( //
			final NOTEHANDLE hNote, //
			final NSFItemScanCallback ActionRoutine) throws NapiException {
		short ret = NNotes.NSFItemScan(hNote.value, ActionRoutine, null);
		if (ret != Napi.NOERROR) {
			throw new NapiException(ret);
		}
	}

	protected Pointer toPointer(final MemBuffer buffer) {
		if (buffer == null)
			return null;
		return new Pointer(buffer.getAddress());
	}

	protected MemBuffer toBuffer(final Pointer ptr) {
		return MemBufferImpl.wrapAddress(Pointer.nativeValue(ptr));
	}

	

	@Override
	public DHANDLE NAMELookup2(final String serverName, final int flags, final String[] views, final String[] names, final String[] items)
			throws NapiException {
		MemBuffer serverBuf = serverName == null ? null : MemBufferImpl.wrapStrings(serverName);
		MemBuffer viewsBuf = MemBufferImpl.wrapStrings(views);
		MemBuffer namesBuf = MemBufferImpl.wrapStrings(names);
		MemBuffer itemsBuf = MemBufferImpl.wrapStrings(items);
		IntByReference retHandle = new IntByReference();
		try {
			short status = NNotes.NAMELookup2(toPointer(serverBuf), //
					flags, //
					(short) views.length, toPointer(viewsBuf), //
					(short) names.length, toPointer(namesBuf), //
					(short) items.length, toPointer(itemsBuf), //
					retHandle.getPointer());
			if (status != NOERROR) {
				throw new NapiException(status);
			}
			if (retHandle.getValue() != 0) {
				return new DHANDLE(retHandle.getValue());
			}
		} finally {
			if (serverBuf != null)
				serverBuf.recycle();
			viewsBuf.recycle();
			namesBuf.recycle();
			itemsBuf.recycle();
		}
		return null;
	}

	@Override
	public void NAMELocateNextMatch2(final MemBuffer pLookup, final MemBuffer pName, final MemBuffer pMatch) {
		Pointer newPMatchPtr = NNotes.NAMELocateNextMatch2(toPointer(pLookup), toPointer(pName), toPointer(pMatch));
		pMatch.setAddress(Pointer.nativeValue(newPMatchPtr));
	}

	@Override
	public boolean NAMEGetTextItem2(final MemBuffer pMatch, final int itemNo, final int itemEntry, final MemBuffer textBuffer,
			final int textBufferLen) throws NapiException {
		short status = NNotes.NAMEGetTextItem2(toPointer(pMatch), (short) itemNo, (short) itemEntry, toPointer(textBuffer),
				(short) textBufferLen);
		switch (status & ERR_MASK) {
		case NOERROR:
			return true;
		case ERR_NO_SUCH_ITEM:
		case ERR_NO_MORE_MEMBERS:
		case ERR_UNSUPPORTED_TYPE:
			return false;
		}
		throw new NapiException(status);
	}

	/**
	 * Findet den nächsten Match. Diese Methode verändert die interne Adresse von MemBuffer!
	 * 
	 * @return Anzahl der Einträge für diesen Namen
	 */
	@Override
	public int NAMELocateNextName2(final MemBuffer pLookup, final MemBuffer pName) {
		MemBuffer pNumMatches = new MemBufferImpl(MemBuffer.sizeOfDWORD);
		try {
			Pointer ptr = NNotes.NAMELocateNextName2(//
					new Pointer(pLookup.getAddress()), //
					new Pointer(pName.getAddress()), //
					new Pointer(pNumMatches.getAddress()));
			pName.setAddress(Pointer.nativeValue(ptr));
			return pNumMatches.readInt();
		} finally {
			pNumMatches.recycle();
		}
	}

	// =================== OS ======================
	// =================== OS ======================
	// =================== OS ======================
	
	/*
	 * Verwendet NAPI
	 * @see de.foconis.domino.api.API#OSMemAlloc(short, int)
	 */
	@Override
	public DHANDLE OSMemAlloc(final short bklType, final int size) throws NapiException {
		try {
			return new DHANDLE((int) Os.OSMemAlloc(bklType, size));
		} catch (NException e) {
			throw new NapiException(e);
		}
	}

	/*
	 * Verwendet NAPI
	 * @see de.foconis.domino.api.API#OSMemFree(de.foconis.domino.api.types.DHANDLE)
	 */
	@Override
	public void OSMemFree(final DHANDLE handle) {
		try {
			int ptr = DHANDLE.nativeValue(handle);
			if (ptr == 0)
				return;
			Os.OSMemFree(ptr);
		} catch (NException e) {
			e.printStackTrace(); // laut Doku sollte dieser Fall nie eintreten (eher crashed Notes)
		}
	}

	/*
	 * Verwendet NAPI
	 * @see de.foconis.domino.api.API#OSLockObject(de.foconis.domino.api.types.DHANDLE)
	 */
	@Override
	public MemBuffer OSLockObject(final DHANDLE handle) {
		int ptr = DHANDLE.nativeValue(handle);
		if (ptr == 0)
			return null;
		try {
			return MemBufferImpl.wrapAddress(Os.OSLockObject(ptr));
		} catch (NException e) {
			e.printStackTrace(); // laut Doku sollte dieser Fall nie eintreten (eher crashed Notes)
			return null;
		}
	}

	/*
	 * Verwendet NAPI
	 * @see de.foconis.domino.api.API#OSUnlockObject(de.foconis.domino.api.types.DHANDLE)
	 */
	@Override
	public void OSUnlockObject(final DHANDLE handle) {
		try {
			Os.OSUnlockObject(DHANDLE.nativeValue(handle));
		} catch (NException e) {
			e.printStackTrace(); // laut Doku sollte dieser Fall nie eintreten (eher crashed Notes)
		}
	}

	// ====================== NIF ==================
	// ====================== NIF ==================
	// ====================== NIF ==================
	
	/*
	 * Verwendet NAPI
	 * @see de.foconis.domino.api.API#getDatabaseHandle(lotus.domino.Database)
	 */
	@Override
	public DBHANDLE getDatabaseHandle(final Database db) {
		return new DBHANDLE((int) BackendBridge.getDatabaseHandleRO(db));
	}


	/*
	 * Verwendet NAPI
	 * @see de.foconis.domino.api.API#getNoteHandle(lotus.domino.Document)
	 */
	@Override
	public NOTEHANDLE getNoteHandle(final Document doc) {
		return new NOTEHANDLE((int) BackendBridge.getDocumentHandleRW(doc));
	}


}