package de.foconis.domino.napi.types;

import de.foconis.domino.napi.MemBuffer;
import de.foconis.domino.util.LMBCSUtils;

public class LMBCSString implements CStruct {

	private String javaString;
	private int size;
	private boolean nullTerminated;

	public LMBCSString(final String javaString, final boolean nt) {
		this.javaString = javaString;
		this.size = LMBCSUtils.getPayload(javaString) + (nt ? 1 : 0);
		this.nullTerminated = nt;
	}

	public LMBCSString(final int size) {
		this.size = size;
		this.nullTerminated = false;
	}

	public LMBCSString() {
		this.nullTerminated = true;
		this.javaString = "";
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public void writeBuffer(final MemBuffer buffer) {
		buffer.writeLMBCS(javaString, size);
	}

	@Override
	public void readBuffer(final MemBuffer buffer) {
		if (nullTerminated) {
			javaString = buffer.readLMBCS();
			this.size = LMBCSUtils.getPayload(javaString) + 1;
		} else {
			javaString = buffer.readLMBCS(size);
		}
	}

	@Override
	public String toString() {
		return javaString;
	}

}
