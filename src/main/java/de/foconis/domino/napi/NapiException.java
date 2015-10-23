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

import com.ibm.domino.napi.NException;


/**
 * Exception, die im Zusammenhang mit der C-API geworfen wird
 * 
 * @author Roland Praml, FOCONIS AG
 *
 */
public class NapiException extends Exception {
	private static ThreadLocal<Throwable> currentException = new ThreadLocal<Throwable>();
	private int errorCode;

	private static final long serialVersionUID = 1L;

	/**
	 * static Method für Constructor
	 */
	private static Throwable getCause(final Throwable cause) {
		if (cause != null)
			return cause;
		return currentException.get();
	}

	/**
	 * Static Method um die CurrentException (bei C-Callbacks) durchzuschleusen
	 */
	public static void setCurrentException(final Throwable t) {
		currentException.set(t);
	}

	/**
	 * Konstruktor
	 */
	public NapiException() {
		super();
	}

	/**
	 * Konstruktor
	 */
	public NapiException(final short errorCode) {
		this(errorCode, null);
	}

	/**
	 * Konstruktor
	 */
	public NapiException(final short errorCode, final Throwable cause) {
		super(getMessage(errorCode, cause), getCause(cause));
		this.errorCode = errorCode;
		currentException.set(null);
	}

	/**
	 * Konstruktor
	 */
	public NapiException(final NException e) {
		this((short) e.getErrorCode(), e);
	}

	/**
	 * Liefert die zum Errorcode passende Meldung
	 */
	private static String getMessage(final short errorCode, final Throwable cause) {
		if ((errorCode & Napi.ERR_MASK) == Napi.JAVA_ERROR) {
			Throwable t = getCause(cause);
			if (t != null) {
				return t.getMessage();
			}
		}
		return Napi.INSTANCE.OSLoadString(errorCode & Napi.ERR_MASK);
	}

	/**
	 * Liefert den Errorcode
	 */
	public int getErrorCode() {
		return errorCode;
	}
}
