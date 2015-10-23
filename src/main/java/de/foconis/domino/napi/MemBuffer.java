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
package de.foconis.domino.napi;

import com.ibm.domino.napi.c.C;


/**
 * Abstrakte Implementierung eines MemBuffer
 * 
 * @author Roland Praml, FOCONIS AG
 *
 */
public interface MemBuffer {

	public static int sizeOfBYTE = C.sizeOfBYTE;
	public static int sizeOfWORD = C.sizeOfWORD;
	public static int sizeOfSWORD = C.sizeOfSWORD;
	public static int sizeOfDWORD = C.sizeOfDWORD;
	public static int sizeOfLONG = C.sizeOfLONG;
	public static int sizeOfUSHORT = C.sizeOfUSHORT;
	public static int sizeOfBOOL = C.sizeOfBOOL;
	public static int sizeOfShort = C.sizeOfShort;
	public static int sizeOfInt = C.sizeOfInt;
	public static int sizeOfLong = C.sizeOfLong;
	public static int sizeOfHANDLE = C.sizeOfHANDLE;
	public static int sizeOfPOINTER = C.sizeOfPOINTER;
	public static int sizeOfNUMBER = C.sizeOfNUMBER;
	public static int sizeOfNOTEID = C.sizeOfNOTEID;
	public static int sizeOfDATETIME = C.sizeOfDWORD * 2;

	/**
	 * gibt den MemBuffer wieder frei
	 */
	public void recycle();

	/**
	 * Gibt einen neuen Buffer zurück, mit eigenem Offset
	 * 
	 * @param delta
	 *            der Offset, an der der neue MemBuffer liest
	 * @return neuer MemBuffer
	 */
	public MemBuffer duplicate(final int delta);

	/**
	 * Read unsigned byte. There is no unsigned byte in java, so we use int here.
	 */
	public int readBYTE();

	/**
	 * Write unsigned byte. There is no unsigned byte in java, so we use int here.
	 */
	public void writeBYTE(final int value);

	/**
	 * read unsigned 16bit value (there is no unsigned short in java, so we must use int here)
	 */
	public int readWORD();

	/**
	 * write unsigned 16bit value (there is no unsigned short in java, so we must use int here)
	 */
	public void writeWORD(final int value);

	/**
	 * read signed 16bit word (=short)
	 */
	public short readSWORD();

	/**
	 * write signed 16bit word (=short)
	 */
	public void writeSWORD(final short value);

	/**
	 * read unsigned 32bit value (there is no unsigned int in java, but we do not use long here)
	 */
	public int readDWORD();

	/**
	 * write unsigned 32bit value (there is no unsigned int in java, but we do not use long here)
	 */
	public void writeDWORD(final int value);

	/**
	 * read signed 32bit value;
	 */
	public int readLONG();

	/**
	 * write signed 32bit value;
	 */
	public void writeLONG(final int value);

	/**
	 * read USHORT 16bit value - there is no unsigned short in java
	 */
	public int readUSHORT();

	/**
	 * write USHORT 16bit value - there is no unsigned short in java
	 */
	public void writeUSHORT(final int value);

	public boolean readBOOL();

	public void writeBOOL(final boolean value);

	public short readShort();

	public void writeShort(final short value);

	public int readInt();

	public void writeInt(final int value);

	public long readLong();

	public void writeLong(final long value);

	public long readHANDLE();

	public void writeHANDLE(final long value);

	public long readPOINTER();

	public void writePOINTER(final long value);

	public double readNUMBER();

	public void writeNUMBER(final double value);

	/**
	 * Write a LMBCS String with given LMBCS length.
	 * 
	 * @param javaString
	 *            string to write
	 * @param len
	 *            LMBCS len of javaString. If javaString is longer than len, javaString will be truncated. IF it is shorter, written string
	 *            will be nullterminated. MemBuffer is forwarded to <code>len</code> bytes in every case
	 */
	public void writeLMBCS(final String javaString, final int len);

	/**
	 * Read a LMBCS String with given (LMBCS) length
	 */
	public String readLMBCS(final int len);

	/**
	 * Read a nullterminated LMBCS String
	 */
	public String readLMBCS();

	//	public void readNOTEID() {
	//		Object ret = C.getNOTEID(address, offset);
	//		offset += C.sizeOfNOTEID;
	//		return ret;
	//	}
	//
	//	public void writeNOTEID(final Object value) {
	//		C.setNOTEID(address, offset, value);
	//		offset += C.sizeOfNOTEID;
	//	}

//	/**
//	 * Write an {@link DateTime} value
//	 */
//	public void writeDATETIME(final DateTime idt);
//
//	public DateTime readDATETIME();

	/**
	 * Rewinds the buffer;
	 * 
	 * @param i
	 *            amount of bytes to rewind
	 */
	public void rewind(final int i);

	/**
	 * forwards the buffer
	 * 
	 * @param i
	 *            amount of bytes to forward
	 */
	public void forward(final int i);

	/**
	 * Returns the native address of the MemBuffer
	 */
	public long getAddress();

	/**
	 * Sets the native address of the MemBuffer
	 */
	public void setAddress(final long newAddress);

	/**
	 * Returns the offset of the membuffer
	 */
	public int getOffset();

	/**
	 * Sets the offset of the membuffer
	 */
	public void setOffset(final int newOffset);
}
