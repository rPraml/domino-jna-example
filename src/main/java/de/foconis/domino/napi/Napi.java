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

import lotus.domino.Database;
import lotus.domino.Document;

import de.foconis.domino.napi.c.jna.JNANapi;
import de.foconis.domino.napi.types.DBHANDLE;
import de.foconis.domino.napi.types.DHANDLE;
import de.foconis.domino.napi.types.NOTEHANDLE;

/**
 * Direkter Zugriff auf die API - hier kommen nur Methoden rein, die auf allen Platformen funktionieren unter Ausnutzung aller in der
 * IBM-NAPI zur Verfügung stehenden Funktionen.
 * 
 * Mehtoden die direkten Zugriff auf die NNotes.dll benötigen kommen in die {@link NapiEx}.
 * 
 * Hintergrund: Für all diese Mehtoden muss früher oder später ein Workaround entwickelt werden, wenn die JNA nicht zur Verfügung steht
 * (z.B. AS400)
 * 
 * @author Roland Praml, FOCONIS AG
 *
 */
public interface Napi {
	public static final Napi INSTANCE = new JNANapi();

	/** open only summary info */
	public static final int OPEN_SUMMARY = 0x0001;
	/** don't bother verifying default bit */
	public static final int OPEN_NOVERIFYDEFAULT = 0x0002;
	/** expand data while opening */
	public static final int OPEN_EXPAND = 0x0004;
	/** don't include any objects */
	public static final int OPEN_NOOBJECTS = 0x0008;
	/** open in a "shared" memory mode */
	public static final int OPEN_SHARE = 0x0020;
	/** Return ALL item values in canonical form */
	public static final int OPEN_CANONICAL = 0x0040;
	/** Mark unread if unread list is currently associated */
	public static final int OPEN_MARK_READ = 0x0100;
	/** Only open an abstract of large documents */
	public static final int OPEN_ABSTRACT = 0x0200;
	/** Return response ID table */
	public static final int OPEN_RESPONSE_ID_TABLE = 0x1000;
	/** Include folder objects - default is not to */
	public static final int OPEN_WITH_FOLDERS = 0x00020000;
	/** If set, leave TYPE_RFC822_TEXT items in native format. Otherwise, convert to TYPE_TEXT/TYPE_TIME. */
	public static final int OPEN_RAW_RFC822_TEXT = 0x01000000;
	/** If set, leave TYPE_MIME_PART items in native format. Otherwise, convert to TYPE_COMPOSITE. */
	public static final int OPEN_RAW_MIME_PART = 0x02000000;

	public static final int OPEN_RAW_MIME = (OPEN_RAW_RFC822_TEXT | OPEN_RAW_MIME_PART);

	/*      NIFOpenCollection "open" flags */

	/**
	 * Throw away existing index and rebuild it from scratch
	 */
	public static final short OPEN_REBUILD_INDEX = 0x0001;
	/**
	 * Do not update index or unread list as part of open (usually set by server when it does it incrementally instead).
	 */
	public static final short OPEN_NOUPDATE = 0x0002;
	/**
	 * If collection object has not yet been created, do NOT create it automatically, but instead return a special internal error called
	 * ERR_COLLECTION_NOT_CREATED
	 */
	public static final short OPEN_DO_NOT_CREATE = 0x0004;
	/**
	 * Tells NIF to "own" the view note (which gets read while opening the collection) in memory, rather than the caller "owning" the view
	 * note by default. If this flag is specified on subsequent opens, and NIF currently owns a copy of the view note, it will just pass
	 * back the view note handle rather than re-reading it from disk/network. If specified, the the caller does NOT have to close the
	 * handle. If not specified, the caller gets a separate copy, and has to NSFNoteClose the handle when its done with it.
	 */
	public static final short OPEN_SHARED_VIEW_NOTE = 0x0010;
	/**
	 * Force re-open of collection and thus, re-read of view note. Also implicitly prevents sharing of collection handle, and thus prevents
	 * any sharing of associated structures such as unread lists, etc
	 */
	public static final short OPEN_REOPEN_COLLECTION = 0x0020;

	// =================
	/** Preserve order of notes in NoteID list */
	public static final int GETNOTES_PRESERVE_ORDER = 0x00000001;
	/** Send (copiable) objects along with note */
	public static final int GETNOTES_SEND_OBJECTS = 0x00000002;
	/** Order returned notes by (approximate) ascending size */
	public static final int GETNOTES_ORDER_BY_SIZE = 0x00000004;
	/** Continue to next on list if error encountered */
	public static final int GETNOTES_CONTINUE_ON_ERROR = 0x00000008;
	/** Enable folder-add callback function after the note-level callback */
	public static final int GETNOTES_GET_FOLDER_ADDS = 0x00000010;
	/** Apply folder ops directly - don't bother using callback */
	public static final int GETNOTES_APPLY_FOLDER_ADDS = 0x00000020;
	/** Don't stream - used primarily for testing purposes */
	public static final int GETNOTES_NO_STREAMING = 0x00000040;
	/** Force streaming, WARNING: only to be used for testing purposes */
	public static final int GETNOTES_FORCE_STREAMING = 0x00000080;

	/** Object may be used by multiple processes */
	public static final short MEM_SHARE = (short) 0x8000;
	/** Object may be OSMemRealloc'ed LARGER */
	public static final short MEM_GROWABLE = 0x4000;

	// ========= ITEM TYPES ===============
	public static final short TYPE_ERROR = 256;
	public static final short TYPE_UNAVAILABLE = 512;
	public static final short TYPE_TEXT = 1280;
	public static final short TYPE_TEXT_LIST = 1281;
	public static final short TYPE_NUMBER = 768;
	public static final short TYPE_NUMBER_RANGE = 769;
	public static final short TYPE_TIME = 1024;
	public static final short TYPE_TIME_RANGE = 1025;
	public static final short TYPE_FORMULA = 1536;
	public static final short TYPE_USERID = 1792;
	public static final short TYPE_INVALID_OR_UNKNOWN = 0;
	public static final short TYPE_COMPOSITE = 1;
	public static final short TYPE_COLLATION = 2;
	public static final short TYPE_OBJECT = 3;
	public static final short TYPE_NOTEREF_LIST = 4;
	public static final short TYPE_VIEW_FORMAT = 5;
	public static final short TYPE_ICON = 6;
	public static final short TYPE_NOTELINK_LIST = 7;
	public static final short TYPE_SIGNATURE = 8;
	public static final short TYPE_SEAL = 9;
	public static final short TYPE_SEALDATA = 10;
	public static final short TYPE_SEAL_LIST = 11;
	public static final short TYPE_HIGHLIGHTS = 12;
	public static final short TYPE_WORKSHEET_DATA = 13;
	public static final short TYPE_USERDATA = 14;
	public static final short TYPE_QUERY = 15;
	public static final short TYPE_ACTION = 16;
	public static final short TYPE_ASSISTANT_INFO = 17;
	public static final short TYPE_VIEWMAP_DATASET = 18;
	public static final short TYPE_VIEWMAP_LAYOUT = 19;
	public static final short TYPE_LSOBJECT = 20;
	public static final short TYPE_HTML = 21;
	public static final short TYPE_SCHED_LIST = 22;
	public static final short TYPE_CALENDAR_FORMAT = 24;
	public static final short TYPE_MIME_PART = 25;
	public static final short TYPE_RFC822_TEXT = 1282;
	//===================READ MASKS =================
	public static final int READ_MASK_NOTEID = 1;
	public static final int READ_MASK_NOTEUNID = 2;
	public static final int READ_MASK_NOTECLASS = 4;
	public static final int READ_MASK_INDEXSIBLINGS = 8;
	public static final int READ_MASK_INDEXCHILDREN = 16;
	public static final int READ_MASK_INDEXDESCENDANTS = 32;
	public static final int READ_MASK_INDEXANYUNREAD = 64;
	public static final int READ_MASK_INDENTLEVELS = 128;
	public static final int READ_MASK_SCORE = 512;
	public static final int READ_MASK_INDEXUNREAD = 1024;
	public static final int READ_MASK_SERETFLAGS = 2048;
	public static final int READ_MASK_COLLECTIONSTATS = 256;
	public static final int READ_MASK_RETURN_DWORD = 4096;
	public static final int READ_MASK_SHORT_COLPOS = 65536;
	public static final int READ_MASK_INIT_POS_NOTEID = 131072;
	public static final int READ_MASK_INDEXPOSITION = 16384;
	public static final int READ_MASK_SUMMARYVALUES = 8192;
	public static final int READ_MASK_SUMMARY = 32768;
	public static final int READ_MASK_SUMMARY_PERMUTED = 262144;
	public static final int READ_MASK_NO_SUBTOTALS = 524288;
	public static final int READ_MASK_NO_EMPTY_VALUES = 1048576;
	public static final int READ_MASK_CATS_ONLY_FOR_COLUMN = 2097152;
	public static final int READ_MASK_INDEXCHILDREN_NOCATS = 4194304;
	public static final int READ_MASK_INDEXDESCENDANTS_NOCATS = 8388608;
	public static final int READ_MASK_RETURN_READERSLIST = 16777216;
	public static final int READ_MASK_PRIVATE_ONLY = 33554432;
	public static final int READ_MASK_ALL_TO_COLUMN = 67108864;
	public static final int READ_MASK_EXCLUDE_LEADING_PROGRAMMATIC_COLUMNS = 134217728;
	public static final int READ_MASK_NO_INTERNAL_ENTRIES = 268435456;
	public static final int READ_MASK_COMPUTE_SUBTOTALS = 536870912;
	public static final int READ_MASK_IS_GHOST_ENTRY = 1073741824;
	public static final int READ_MASK_ALL = 59391;
	/* Max. levels in hierarchy tree */
	public static final int MAXTUMBLERLEVELS = 32;
	// ================ SIGNALS ================
	public static final short SIGNAL_DEFN_ITEM_MODIFIED = 1;
	public static final short SIGNAL_VIEW_ITEM_MODIFIED = 2;
	public static final short SIGNAL_INDEX_MODIFIED = 4;
	public static final short SIGNAL_UNREADLIST_MODIFIED = 8;
	public static final short SIGNAL_DATABASE_MODIFIED = 16;
	public static final short SIGNAL_MORE_TO_DO = 32;
	public static final short SIGNAL_VIEW_TIME_RELATIVE = 64;
	public static final short SIGNAL_NOT_SUPPORTED = 128;
	public static final short SIGNAL_ANY_CONFLICT = 31;
	public static final short SIGNAL_ANY_NONDATA_CONFLICT = 15;

	public static final short NOERROR = 0;
	public static final short JAVA_ERROR = 0x3e27;
	public static final short ERR_MASK = 0x3FFF;
	public static final short ERR_NOT_FOUND = 0x0404;
	public static final short ERR_NO_SUCH_ITEM = 0x0310;
	public static final short ERR_NO_MORE_MEMBERS = 0x0311;
	public static final short ERR_UNSUPPORTED_TYPE = 0x312;
	public static final short ERR_FORMULA_COMPILATION = 0x501;

	public static final short NOTE_CLASS_DOCUMENT = 0x0001;
	

	/**
	 * Returns the DHANDLE of the underlying {@link lotus.domino.Database}
	 */
	public DBHANDLE getDatabaseHandle(final Database db);

	/**
	 * Returns the {@link NOTEHANDLE} of the underlying {@link lotus.domino.Document}
	 */
	public NOTEHANDLE getNoteHandle(final Document doc);

	/**
	 * Liefert den Fehlertext zu StringCode
	 */
	public abstract String OSLoadString(final int StringCode);

	/**
	 * <p>
	 * OSLockObject locks a memory object (preventing its physical relocation) and returns a pointer to its locked address in memory. This
	 * routine must be called before any memory object is referenced. Once locked, the application can freely use the pointer to access its
	 * contents.
	 * </p>
	 * <p>
	 * It is recommended that once an application is temporarily done using a memory object, it unlock the object freeing up the memory for
	 * swapping to disk. OSLockObject can then be used for subsequent access, and the new pointer value must then be used for access.
	 * </p>
	 * <p>
	 * An application can lock the same object more than once -- a lock reference count is maintained. It is not actually moveable until all
	 * locks are released (reference count = 0).
	 * </p>
	 * <p>
	 * Calling this routine with a HANDLE that is invalid or out of range will result in a Notes PANIC halt .
	 * </p>
	 * 
	 * @param handle
	 *            Handle for the object to be locked.
	 * @return CBuffer to the locked object.
	 */
	public MemBuffer OSLockObject(final DHANDLE handle);

	// =================== OS ======================
	// =================== OS ======================
	// =================== OS ======================

	/**
	 * <p>
	 * OSMemAlloc allocates a block of memory and returns a handle to the caller. The handle can be converted to a memory pointer by calling
	 * OSLockObject.
	 * <p/>
	 * <p>
	 * Under normal circumstances, memory allocated using OSMemAlloc must be released using the function OSMemFree. (For the exceptions,
	 * please see the discussion under OSMemFree.)
	 * </p>
	 * <p>
	 * This function is only necessary when allocating memory for which you need a handle to pass to C API functions. If you need to
	 * allocate memory that does not require a handle, you can use the standard C function malloc() instead.
	 * <p/>
	 * 
	 * @param bklType
	 *            Type of memory block to be allocated.<br/>
	 *            Use 0 to allocate a block of memory for the application's use.<br/>
	 *            To allocate memory that can be shared between processes, use MEM_SHARE. <br/>
	 *            For allocated memory that may be reallocated to a different size, use MEM_GROWABLE. See Symbolic Value, MEM_xxx for more
	 *            information.
	 * 
	 * @param size
	 *            Requested size of memory block in bytes. The limit is MAXDWORD. If the requested size is 0 bytes, NULLHANDLE will be
	 *            returned rather than a new handle.
	 * 
	 * @return The address of a HANDLE in which the handle to the allocated memory object is returned.
	 * @throws NapiException
	 *             possible error codes:
	 *             <ul>
	 *             <li>ERR_MEMORY - Not enough global memory available for allocation.</li>
	 * 
	 *             <li>ERR_SEGMENT_TOO_BIG - Requested size is greater than the maximum supported.</li>
	 *             </ul>
	 */
	public DHANDLE OSMemAlloc(final short bklType, final int size) throws NapiException;

	/**
	 * <p>
	 * OSMemFree is used to deallocate a block of Domino memory that was created via OSMemAlloc. In order for the deallocation to take
	 * place, the lock reference count for the memory block must be zero. In other words, all OSLockObject operations made to the memory
	 * block must be matched with an OSUnlockObject operation before memory can be freed.
	 * </p>
	 * <p>
	 * Calling this routine with a HANDLE that is invalid or out of range, or with a HANDLE for a memory object that is still locked, will
	 * result in a Notes PANIC halt .<br/>
	 * <b>NULLHANDLE is allowed.</b>
	 * </p>
	 * <p>
	 * Note: this routine must be called to free the memory objects specifically created by you with OSMemAlloc or returned by one of
	 * several functions. Each function description will mention any specific objects you must 'free'. The following discussion will
	 * organize C API functions into several categories, based on the way they handle the memory they create, manage, and destroy.
	 * </p>
	 * <p>
	 * Generally, functions that create database objects come in pairs, which could be regarded as symmetrical functions. A pair such as
	 * NSFNoteOpen and NSFNoteClose are a case in point; any memory objects created by NSFNoteOpen will be freed by NSFNoteClose. Also,
	 * since notes are composed of a header structure and a variable number of memory objects called 'items' (fields), NSFNoteClose will
	 * take care of freeing the memory associated with any items in a note. You must NOT free memory that is still associated with a note's
	 * "items" with OSMemFree, as a subsequent NSFNoteClose will try to free the memory objects that no longer exist and a Notes PANIC halt
	 * will be the result.
	 * </p>
	 * <p>
	 * Another category of functions transfers responsibility for existing memory objects to symmetrical functions. A function such as
	 * NSFItemAppendByBLOCKID actually creates an association between an object in memory and a note. You must NOT free the memory
	 * associated with note "items" by NSFItemAppendByBLOCKID with OSMemFree, as NSFNoteClose will try to do the same when it is called and
	 * a Notes PANIC halt will be the result.
	 * </p>
	 * <p>
	 * Another category of functions absolves symmetrical functions of their responsibility for existing memory objects. Functions such as
	 * NSFItemDeleteByBLOCKID or NSFItemDelete remove the association between an object in memory (item) and a note, and then perform an
	 * OSMemFree themselves.
	 * </p>
	 * <p>
	 * Another category of functions create memory objects for you, in order to return information to the caller. Generally, the size of
	 * these memory objects cannot be forseen or efficiently limited. These include, but are not limited to: NIFReadEntries,
	 * NSFConvertItemToComposite, and NSFConvertItemToText. Again, the function descriptions will flag any arguments that create memory your
	 * program is ultimately responsible for cleaning up.
	 * </p>
	 * <p>
	 * The final category of functions copy existing memory objects, transferring responsibility for management of the object to a
	 * symmetrical function. NSFItemAppend actually copies a memory object and appends it as a note item (field). NSFNoteClose takes care of
	 * the copied data, and you are left with the original object, which your program may either re-use or free.
	 * </p>
	 * 
	 * @param handle
	 *            Handle for the memory object to be freed.
	 */
	public void OSMemFree(final DHANDLE handle);

	/**
	 * <p>
	 * OSUnlockObject is called to indicate that an application no longer needs physical access to the specified object. The lock count on
	 * the object is then decremented, and the calling application must then assume that the physical address for that object is no longer
	 * valid. An object must be fully unlocked (reference count = 0) before it can be deallocated with OSMemFree.
	 * </p>
	 * <p>
	 * It is recommended that once an application is temporarily done using a memory object, it unlock the object freeing up the memory for
	 * swapping to disk. OSLockObject can then be used for subsequent access, and the new pointer value must then be used for access.
	 * </p>
	 * <p>
	 * An application can lock the same object more than once -- a lock reference count is maintained. It is not actually moveable until all
	 * locks are released (reference count = 0).
	 * </p>
	 * <p>
	 * Calling this routine with a HANDLE that is invalid or out of range will result in a Notes PANIC halt.
	 * </p>
	 * 
	 * @param handle
	 *            Handle for the object to be unlocked.
	 */
	public void OSUnlockObject(final DHANDLE handle);

	/**
	 * Findet den nächsten Namen. Diese Methode verändert die interne Adresse von pName! Keine Funktion in der NAPI vorhanden
	 * <p>
	 * This function is an enhanced 32-bit version of NAMELocateNextName.
	 * </p>
	 * <p>
	 * This routine finds the next record in a look up buffer. A look up buffer is created by a call to NAMELookup2. A look up buffer
	 * contains a series of records. Each record contains information about one name from one view in an Address book or Domino Directory
	 * (Server's Address book).
	 * </p>
	 * <p>
	 * The number of records in a look up buffer is equal to the number of views searched times the number of names looked up. Each record
	 * may contain multiple matches for the given name if multiple documents in the view match the name. NAMELocateNextName2 finds the next
	 * record (or the first record) in the buffer and returns the name itself and the number of matches in the record.
	 * </p>
	 * <p>
	 * Use this function to find the next record in a buffer created by NAMELookup2, and to determine how many matches NAMELookup2 found for
	 * that name in that view.
	 * </p>
	 * <p>
	 * Call NAMELocateNextName2 in a loop to process each record in the buffer created by NAMELookup2. Specifying pName = NULL on the first
	 * iteration to get the record for the first match. and the value returned from NAMELocateNextName2 on each subsequent iteration.
	 * Execute the loop for each record in the buffer.
	 * </p>
	 * <p>
	 * 
	 * @param pLookup
	 *            Address of a look up buffer containing information returned from NAMELookup2. NAMELookup2 returns a handle. Call
	 *            OSLockObject on the handle to obtain this address.
	 * @param pName
	 *            Input: Previous record in the buffer. Specify a NULL {@link MemBuffer}to locate the first record in the buffer.<br/>
	 * 
	 *            Output : The next record in the look up buffer. If pName is NULL on input, then this returns the first record in the
	 *            buffer. If pName specifies the last record in the buffer, then this returns NULL.
	 * 
	 * 
	 * 
	 * @return the number of matches in the next record. If NAMELookup2 finds more than one match for a given name in a given view, then the
	 *         record corresponding to that name in that view contains more than one match.
	 */
	public int NAMELocateNextName2(final MemBuffer pLookup, final MemBuffer pName);

	/**
	 * This function is an enhanced 32-bit version of NAMELocateNextMatch.
	 * <p>
	 * Note: Address books refer to both Notes Client Address books, and Domino Server Address books know as Domino Directories.
	 * </p>
	 * <p>
	 * This routine finds the next match in a record in a look up buffer. One record in a look up buffer contains all the matches in one
	 * view for a specified name. Use this function to iterate over multiple matches when a call to NAMELookup2 yields more than one match
	 * for a given name.
	 * </p>
	 * <p>
	 * NAMELookup2 creates and initializes a look up buffer containing information from the Address book(s). The look up buffer consists of
	 * a header (type LOOKUP_HEADER) followed by a series of records. The number of records is equal to the number of views searched times
	 * the number of names looked up. A record will contain multiple matches to a given name if the name matches more than one document in a
	 * single view. First use NAMELocateNextName2 to locate the next record in the buffer. Then use NAMELocateNextMatch2 to iterate over the
	 * matches within one record.
	 * </p>
	 * <p>
	 * Call NAMELocateNextMatch2 in a loop to locate all the matches in a record. Specify pMatch = NULL on the first iteration to locate the
	 * first match. Specify pMatch = the location of the last match on subsequent iterations. Locate individual items within a match by
	 * passing the location of the match as input to NAMELocateItem2.
	 * </p>
	 * <p>
	 * Note: this routine depends on the caller knowing when to terminate the loop. Use NumMatches as returned by NAMELocateNextName2 to
	 * control looping. The value returned by this funciton is undefined if pMatch, on input, specifies the last match in the record.
	 * </p>
	 * 
	 * @param pLookup
	 *            Address of a look up buffer containing information returned from NAMELookup2. NAMELookup2 returns a handle. Call
	 *            OSLockObject on the handle to obtain this address.
	 * @param pName
	 *            Location of a record corresponding to one name in the look up buffer. Use NAMELocateNextName2 to obtain this location.
	 * @param pMatch
	 *            Input: Location of previous match in the record. Specify a NULL {@link MemBuffer} to locate the first match in the record.
	 *            NAMELocateNextMatch returns a pointer to the next record in the buffer that matches the specified name. NULL is NOT
	 *            returned to indicate termination.
	 * 
	 *            Output : Address of the next match in the record in a look up buffer.
	 */
	public void NAMELocateNextMatch2(final MemBuffer pLookup, final MemBuffer pName, final MemBuffer pMatch);

	/**
	 * 
	 * This function is an enhanced 32-bit version of NAMELookup and supports returning data greater than 64 KB. Server Administrators can
	 * define how much data can be returned in a single call by setting the NAMELOOKUP_MAX_MB variable in notes.ini. For example,
	 * NAMELOOKUP_MAX_MB=3 will allow NAMELookup2 to return 3 MB of data. If this variable is not set, the default of 1 MB is used.
	 * Increasing the default will result in a performance hit on the server returning the data. The maximum size for the amount of returned
	 * data is MAXDWORD (approximately 4,000 MB).
	 * <p>
	 * Note: Address books refer to both Notes Client Address books, and Domino Server Address books known as Domino Directories.
	 * </p>
	 * <p>
	 * This routine looks up names in the Address Books. It retrieves item values from each document it finds that matches a name. It
	 * returns a handle to a buffer that contains the values of the items requested. Free the buffer after processing the data in it.
	 * </p>
	 * <p>
	 * Use this routine to efficiently find information stored in the Address books. NAMELookup2 is particularly useful for looking up
	 * information about a given name when you don't know, for example, whether the name corresponds to a person or a group, or when you
	 * don't know which Address book the name resides in.
	 * </p>
	 * <p>
	 * NAMELookup2 handles multiple views, multiple names, and multiple items. It takes, as input, a list of view names, a list of names,
	 * and a list of items. It searches each view for each name in all the Address books in use. For each document that matches a name,
	 * NAMELookup2 reads the requested item values. It stores the item values in a buffer, and returns the buffer handle.
	 * </p>
	 * <p>
	 * NAMELookup2 can be used with categorized (hierarchical) views, but this will require some knowledge of the view's hierarchy, since
	 * searches will be based on the categories in the view. For example, in the $People view in the Domino Directory, the first column is
	 * categorized by the first letter of the LastName item. Consequently, performing a lookup on the name "A" in the $People view will find
	 * all people whose last name begins with the letter "A". If a view has multiple levels of categorization, then NAMELookup2 will find
	 * only the documents immediately under the category specified in the Names parameter. For example, the $PeopleGroupsHier view is
	 * categorized based on an organization's hierarchy. Suppose a company "ABCorp" has users in the "Sales" and "Marketing" organizations.
	 * If you search the $PeopleGroupsHier, and specify "ABCorp" in the Names parameter, NAMELookup will find all people under the top-level
	 * of the organizational hierarchy "ABCorp", but not those in the organizational units. Likewise, specifying "ABCorp\Sales" in the Names
	 * parameter will return all members of the /Sales/ABCorp organizational unit only.
	 * </p>
	 * <p>
	 * The list of Address books in use is derived from the Directory Assistance database if the server is configured to have a Directory
	 * Assistance database. Otherwise this list is derived from the NAMES variable in the notes.ini file which defines the list of Address
	 * books in use. If notes.ini does not specify a NAMES variable, the default is "names.nsf".
	 * </p>
	 * <p>
	 * NAMELookup2 searches multiple Address books in the order specified by the Directory Assistance database or by the NAMES variable.
	 * NAMELookup2 stops searching for a given name as soon as it finds a match in one of the Address books. Therefore, if a given name
	 * appears in multiple Address books, NAMELookup2 only returns information for that name from the first database searched that contains
	 * a match.
	 * </p>
	 * <p>
	 * NAMELookup2 reads item values from the document's summary buffer. Therefore, NAMELookup2 can only retrieve summary item values.
	 * </p>
	 * <p>
	 * NAMELookup2 allocates a memory buffer, fills the buffer with the requested item values, and stores a handle to this buffer in
	 * rethBuffer. Lock the buffer before retrieving information from it. Locate information in this buffer using NAMELocateNextMatch2,
	 * NAMELocateItem2, and NAMELocateMatchAndItem2. Retrieve information from this buffer using NAMEGetTextItem2. Unlock and free the
	 * buffer after processing the information.
	 * </p>
	 * <p>
	 * If the ServerName parameter is non-NULL, then the look up process takes place on the specified server. In this case, if the server is
	 * not configured to have a Directory Assistance database, the NAMES variable in the server's notes.ini file defines the list of Domino
	 * Directories searched. NAMELookup2 searches Domino Directory databases resident on the specified server.
	 * </p>
	 * <p>
	 * It is important to note that if NameLookup2 is performed and the specified server is not available then NameLookup2 automatically
	 * fails over to another server in the cluster. The failover continues until an available server is found or there is no available
	 * server in the cluster. Therefore, it is possible that the information could be retrieved from any server in the cluster.
	 * </p>
	 * <p>
	 * The first time a process calls NAMELookup2, it creates a list of open collections from the Address books identified by either the
	 * Directory Assistance database or the NAMES variable. It saves this list in memory to improve the efficiency of repeated look ups.
	 * </p>
	 * 
	 * @param serverName
	 *            Server where look up is performed. Look in the Domino Directories that reside on, and in use by, this server. Specify NULL
	 *            to perform the lookup on, and search Address books in use by, the local system.
	 * @param flags
	 *            - Flags to control lookup (see NAME_LOOKUP_xxx) for a list of the legal values. Normally 0.
	 * @param views
	 *            Array of view names to be searched.
	 * @param names
	 *            Array of series of names to look up.
	 * @param items
	 *            Array of series of item names to return with each match.
	 * @return a handle to a buffer containing name information. Buffer consists of a header followed by a series of data records. The
	 *         number of data records is equal to the number of views searched times the number of names. Use NAMELocateItem2,
	 *         NAMEGetTextItem2, etc., to read information in the buffer. Free this buffer after processing the information it contains.
	 * 
	 */
	public DHANDLE NAMELookup2(final String serverName, final int flags, final String[] views, final String[] names, final String[] items)
			throws NapiException;

	/**
	 * This function is an enhanced 32-bit version of NAMEGetTextItem.
	 * <p>
	 * Note: Address books refer to both Notes Client Address books, and Domino Server Address books know as Domino Directories.
	 * </p>
	 * <p>
	 * This routine copies a text item from a NAMELookup2 buffer into a buffer provided by the caller. Use this routine to get a TYPE_TEXT
	 * item or one member of a TYPE_TEXT_LIST item from a look up buffer. This routine copies the text to a buffer provided by the caller,
	 * truncates the text if it would exceed the specified buffer length, and NULL-terminates the text.
	 * </p>
	 * <p>
	 * NAMELookup2 creates a look up buffer containing information about a series of names in the Address books. The look up buffer consists
	 * of a series of records, one record per name. Use NAMELocateNextName2 to locate the next record in the buffer. A record may contain
	 * multiple matches for a given name. Use NAMELocateNextMatch2 to locate one match in a record. Specify this match location in the
	 * pMatch argument to NAMEGetTextItem2.
	 * </p>
	 * <p>
	 * To retrieve one element of a text list, specify the text list member number in the "Member" argument. Member numbers are zero based:
	 * Member = 0 specifies the first member. To loop for all members in a text list, loop until this routine returns ERR_NO_MORE_MEMBERS.
	 * </p>
	 * <p>
	 * Use NAMEGetTextItem2 to retrieve text items from look up buffers. Use NAMELocateItem2 to obtain the data type of an item if you do
	 * not know the data type in advance, or to retrieve items of data types other than text or text list.
	 * </p>
	 * Parameters :
	 * 
	 * Input : pMatch -
	 * 
	 * Item -
	 * 
	 * Member -
	 * 
	 * 
	 * BufLen -
	 * 
	 * Output : (routine) - Return status from this call -- indicates either success or what the error is. The return codes include:
	 * 
	 * NOERROR - Successfully copied Item from buffer.
	 * 
	 * ERR_NO_SUCH_ITEM - Item was not returned for this match
	 * 
	 * ERR_NO_MORE_MEMBERS - Member number too large
	 * 
	 * ERR_UNSUPPORTED_DATATYPE - Item data type neither TYPE_TEXT nor TYPE_TEXT_LIST
	 * 
	 * ERR_xxx - Errors returned by lower level functions. Call OSLoadString to obtain the error string.
	 * 
	 * 
	 * Buffer - receives the zero-terminated text string
	 * 
	 * @param pMatch
	 *            Location of a match in a look up buffer. Use NAMELocateMatch2 to obtain this location.
	 * @param itemNo
	 *            Item number = index of the item name in the set specified by the "Items" argument to NAMELookup2. Zero specifies the first
	 *            item. Note: this item number must not be greater than the number of items specified by the "NumItems" argument to
	 *            NAMELookup2.
	 * @param itemEntry
	 *            Text list member number. Specify 0 if the item is of TYPE_TEXT. Member numbers are zero based: Member = 0 specifies the
	 *            first member.
	 * @param textBuffer
	 *            Buffer - pointer to a buffer to copy result into
	 * @param textBufferLen
	 *            Length, in bytes, of the buffer. Text is truncated to this length.
	 * @return true if Item could be read
	 */
	public boolean NAMEGetTextItem2(MemBuffer pMatch, int itemNo, int itemEntry, MemBuffer textBuffer, int textBufferLen)
			throws NapiException;

}
