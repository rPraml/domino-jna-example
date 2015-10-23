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
package de.foconis.domino.napi.c;

import com.ibm.domino.napi.c.C;
import com.ibm.domino.napi.c.NotesUtil;

import de.foconis.domino.napi.MemBuffer;
import de.foconis.domino.napi.types.CStruct;
import de.foconis.domino.util.LMBCSUtils;

/**
 * Implementierung eines MemBuffers. Der MemBuffer macht aktuell noch intensiven Gebrauch von com.ibm.domino.napi.c.C - könnte aber später
 * auch mal einen DirectByteBuffer verwenden
 * 
 * @author Roland Praml, FOCONIS AG
 *
 */
public class MemBufferImpl implements MemBuffer {
	private long address;
	private int size;
	private int offset;
	private boolean ownsMemory;

	public static MemBuffer wrapAddress(final long ptr) {
		MemBufferImpl ret = new MemBufferImpl();
		ret.address = ptr;
		ret.size = 0;
		ret.ownsMemory = false;
		return ret;
	}

	public static MemBuffer wrapStrings(final String... what) {
		int bedarf = 0;
		for (int i = 0; i < what.length; i++) {
			bedarf += LMBCSUtils.getPayload(what[i]) + 1;
		}
		MemBufferImpl ret = new MemBufferImpl(bedarf);
		for (int i = 0; i < what.length; i++) {
			// direkt schreiben spart einen weiteren getPayload call
			int len = LMBCSUtils.getPayload(what[i]);
			NotesUtil.toLMBCSBuffer(what[i], ret.address, ret.offset, len);
			ret.offset += len;
			ret.writeBYTE((byte) 0); // nullterminator
		}
		return ret;
	}

	public MemBufferImpl(final int size) {
		this.address = C.malloc(size);
		this.size = size;
		this.ownsMemory = true;
	}

	private MemBufferImpl() {
	}

	public MemBufferImpl(final CStruct struct) {
		this(struct.size());
		struct.writeBuffer(this);
	}

	@Override
	public void recycle() {
		if (ownsMemory && address != 0) {
			C.free(address);
		}
		address = 0;
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		if (address != 0 && ownsMemory) {
			System.out.println("Found a non-recycled CBuffer");
		}
	}

	/**
	 * Gibt einen neuen Buffer zurück, mit eigenem Offset
	 */
	@Override
	public MemBuffer duplicate(final int delta) {
		MemBufferImpl ret = new MemBufferImpl();
		ret.address = address;
		ret.offset = this.offset + delta;
		ret.size = size;
		return ret;
	}

	/**
	 * Read unsigned byte. There is no unsigned byte in java, so we use int here.
	 */
	@Override
	public int readBYTE() {
		int ret = C.getBYTE(address, offset);
		offset += C.sizeOfBYTE;
		return (short) (ret & 0xFF);
	}

	/**
	 * Write unsigned byte. There is no unsigned byte in java, so we use int here.
	 */
	@Override
	public void writeBYTE(final int value) {
		C.setBYTE(address, offset, (byte) value);
		offset += C.sizeOfBYTE;
	}

	/**
	 * read unsigned 16bit value (there is no unsigned short in java, so we must use int here)
	 */
	@Override
	public int readWORD() {
		int ret = C.getWORD(address, offset);
		offset += C.sizeOfWORD;
		return ret & 0xFFFF;
	}

	/**
	 * write unsigned 16bit value (there is no unsigned short in java, so we must use int here)
	 */
	@Override
	public void writeWORD(final int value) {
		C.setWORD(address, offset, (short) value);
		offset += C.sizeOfWORD;
	}

	/**
	 * read signed 16bit word (=short)
	 */
	@Override
	public short readSWORD() {
		short ret = C.getSWORD(address, offset);
		offset += C.sizeOfSWORD;
		return ret;
	}

	/**
	 * write signed 16bit word (=short)
	 */
	@Override
	public void writeSWORD(final short value) {
		C.setSWORD(address, offset, value);
		offset += C.sizeOfSWORD;
	}

	/**
	 * read unsigned 32bit value (there is no unsigned int in java, but we do not use long here)
	 */
	@Override
	public int readDWORD() {
		int ret = C.getDWORD(address, offset);
		offset += C.sizeOfDWORD;
		return ret;
	}

	/**
	 * write unsigned 32bit value (there is no unsigned int in java, but we do not use long here)
	 */
	@Override
	public void writeDWORD(final int value) {
		C.setDWORD(address, offset, value);
		offset += C.sizeOfDWORD;
	}

	/**
	 * read signed 32bit value;
	 */
	@Override
	public int readLONG() {
		int ret = C.getLONG(address, offset);
		offset += C.sizeOfLONG;
		return ret;
	}

	/**
	 * write signed 32bit value;
	 */
	@Override
	public void writeLONG(final int value) {
		C.setLONG(address, offset, value);
		offset += C.sizeOfLONG;
	}

	@Override
	public int readUSHORT() {
		int ret = C.getUSHORT(address, offset);
		offset += C.sizeOfUSHORT;
		return ret & 0xFFFF;
	}

	@Override
	public void writeUSHORT(final int value) {
		C.setUSHORT(address, offset, (short) value);
		offset += C.sizeOfUSHORT;
	}

	@Override
	public boolean readBOOL() {
		boolean ret = C.getBOOL(address, offset);
		offset += C.sizeOfBOOL;
		return ret;
	}

	@Override
	public void writeBOOL(final boolean value) {
		C.setBOOL(address, offset, value);
		offset += C.sizeOfBOOL;
	}

	@Override
	public short readShort() {
		short ret = C.getShort(address, offset);
		offset += C.sizeOfShort;
		return ret;
	}

	@Override
	public void writeShort(final short value) {
		C.setShort(address, offset, value);
		offset += C.sizeOfShort;
	}

	@Override
	public int readInt() {
		int ret = C.getInt(address, offset);
		offset += C.sizeOfInt;
		return ret;
	}

	@Override
	public void writeInt(final int value) {
		C.setInt(address, offset, value);
		offset += C.sizeOfInt;
	}

	@Override
	public long readLong() {
		long ret = C.getLong(address, offset);
		offset += C.sizeOfLong;
		return ret;
	}

	@Override
	public void writeLong(final long value) {
		C.setLong(address, offset, value);
		offset += C.sizeOfLong;
	}

	@Override
	public long readHANDLE() {
		long ret = C.getHandle(address, offset);
		offset += C.sizeOfHANDLE;
		return ret;
	}

	@Override
	public void writeHANDLE(final long value) {
		C.setHandle(address, offset, value);
		offset += C.sizeOfHANDLE;
	}

	@Override
	public long readPOINTER() {
		long ret = C.getPointer(address, offset);
		offset += C.sizeOfPOINTER;
		return ret;
	}

	@Override
	public void writePOINTER(final long value) {
		C.setPointer(address, offset, value);
		offset += C.sizeOfPOINTER;
	}

	@Override
	public double readNUMBER() {
		double ret = C.getNUMBER(address, offset);
		offset += C.sizeOfNUMBER;
		return ret;
	}

	@Override
	public void writeNUMBER(final double value) {
		C.setNUMBER(address, offset, value);
		offset += C.sizeOfNUMBER;
	}

	@Override
	public void writeLMBCS(final String javaString, final int len) {
		int payload = LMBCSUtils.getPayload(javaString);
		if (payload < len) {
			NotesUtil.toLMBCSBuffer(javaString, address, offset, payload);
			C.setByte(address, offset + payload, (byte) 0); // Null byte to terminate string
		} else {
			NotesUtil.toLMBCSBuffer(javaString, address, offset, len);
		}
		offset += len;
	}

	@Override
	public String readLMBCS(final int len) {
		if (len == 0)
			return "";
		String ret = NotesUtil.fromLMBCSLen(address, offset, len);
		offset += len;
		return ret;
	}

	@Override
	public String readLMBCS() {
		String ret = NotesUtil.fromLMBCS(address, offset);
		offset += LMBCSUtils.getPayload(ret);
		return ret;
	}

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

//	@Override
//	public void writeDATETIME(final DateTime idt) {
//		writeDWORD(DateTimeUtil.getTimeInnard(idt));
//		writeDWORD(DateTimeUtil.getDateInnard(idt));
//	}
//
//	@Override
//	public DateTime readDATETIME() {
//		int timeInnard = readInt();
//		int dateInnard = readInt();
//		return DateTimeUtil.getDateTime(dateInnard, timeInnard);
//	}

	@Override
	public void rewind(final int i) {
		offset -= i;
	}

	@Override
	public void forward(final int i) {
		offset += i;
	}

	@Override
	public long getAddress() {
		return address;
	}

	@Override
	public String toString() {
		StringBuilder hex = new StringBuilder();
		StringBuilder ascii = new StringBuilder();
		StringBuilder out = new StringBuilder();
		for (int i = 0; i < size; i++) {
			int b = C.getByte(address, i) & 0xFF;
			if (b < 16) {
				hex.append('0');
				hex.append(Integer.toHexString(b));
			} else {
				hex.append(Integer.toHexString(b));
			}
			if (i % 8 == 7) {
				hex.append(' ');
			}
			hex.append(' ');
			if (32 <= b && b < 128) {
				ascii.append((char) b);
			} else {
				ascii.append('.');
			}

			if (i % 16 == 15) {
				out.append(hex.toString());
				out.append(':');
				out.append(ascii.toString());
				out.append('\n');
				hex.setLength(0);
				ascii.setLength(0);
			}
		}
		out.append(hex.toString());
		out.append(':');
		out.append(ascii.toString());
		out.append("\nSize=");
		out.append(size);
		return out.toString();

	}

	@Override
	public void setAddress(final long newAddress) {
		this.address = newAddress;
		this.offset = 0;
		this.size = 0;

	}

	@Override
	public int getOffset() {
		return offset;
	}

	@Override
	public void setOffset(final int newOffset) {
		offset = newOffset;
	}

}
