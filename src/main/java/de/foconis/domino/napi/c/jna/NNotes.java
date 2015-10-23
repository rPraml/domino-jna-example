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

import lotus.domino.util.Platform;

import com.sun.jna.Callback;
import com.sun.jna.Native;
import com.sun.jna.Pointer;

/**
 * Diese Klasse ist Package-Private und enthält alle API-Calls
 * 
 * @author Roland Praml, FOCONIS AG
 *
 */
class NNotes {
	static {
		Native.register(Platform.getLibName("notes"));
	}

	/**
	 * This function will return a stream of notes to the caller through several callback functions.
	 * 
	 * @param hDB
	 *            DBHANDLE - Handle to an open database to retrieve notes from.
	 * @param NumNotes
	 *            DWORD - Number of notes to retrieve.
	 * @param NoteIDs
	 *            NOTEID * - Pointer to note ID(s) of retrieved note(s).
	 * @param NoteOpenFlags
	 *            DWORD * - Pointer to flags that control the manner in which the note is opened. This, in turn, controls what information
	 *            about the note is available to you and how it is structured. The flags are defined in OPEN_xxx (note) and may be or'ed
	 *            together to combine functionality.
	 * 
	 * @param SinceSeqNum
	 *            DWORD * - Pointer to the since sequence number.
	 * @param ControlFlags
	 *            DWORD - Flags that control the actions of the function during note retrieval. The flags are defined in GETNOTES_xxx.
	 * @param hObjectDB
	 *            DBHANDLE - Database handle. If objects are being retrieved (GETNOTES_SEND_OBJECTS) and the value of hObjectDB is not
	 *            NULLHANDLE, objects will be stored in this database and attached to the incoming notes prior to NoteOpenCallback being
	 *            called.
	 * @param CallbackParam
	 *            void * - Callback parameter for each of the callback functions.
	 * @param GetNotesCallback
	 *            NSFGETNOTESCALLBACK - The get notes callback function pointer. Called once before any others but only if going to a server
	 *            that is R6 or greater. If GETNOTES_ORDER_BY_SIZE is specified in options the two DWORD parameters, TotalSizeLow and
	 *            TotalSizeHigh, provide the approximate total size of the bytes to be returned in the notes and objects. These values are
	 *            intended to be used for progress indication. See NSFGETNOTESCALLBACK
	 * @param NoteOpenCallback
	 *            NSFNOTEOPENCALLBACK - The note open callback function pointer. This function is called for each note retrieved. If
	 *            non-NULL, this is called for each note after all objects have been retrieved (if GETNOTES_SEND_OBJECTS is specified). See
	 *            NSFNOTEOPENCALLBACK.
	 * 
	 * @param ObjectAllocCallback
	 *            NSFOBJECTALLOCCALLBACK - The object allocate callback function pointer. If GETNOTES_SEND_OBJECTS is specified and
	 *            hObjectDB is not NULLHANDLE, this function is called exactly once for each object to provide the caller with information
	 *            about the object's size and ObjectID. The intent is to allow for the physical allocation for the object if need be. It is
	 *            called before the NoteOpenCallback for the corresponding note. See NSFOBJECTALLOCCALLBACK.
	 * 
	 * @param ObjectWriteCallback
	 *            NSFOBJECTWRITECALLBACK - The object write callback function pointer. This function is called for each "chunk" of each
	 *            object if GETNOTES_SEND_OBJECTS is specified and hObjectDB is not NULLHANDLE. For each object this will be called one or
	 *            more times. See NSFOBJECTWRITECALLBACK.
	 * @param FolderSinceTime
	 *            TIMEDATE * - A pointer to a TIMEDATE structure containing a time/date value specifying the earliest time to retrieve notes
	 *            from the folder. If GETNOTES_GET_FOLDER_ADDS is specified this is the time folder operations should be retrieved from.
	 * 
	 * @param FolderAddCallback
	 *            NSFFOLDERADDCALLBACK - The folder add callback function pointer. If GETNOTES_GET_FOLDER_ADDS is specified but
	 *            GETNOTES_APPLY_FOLDER_ADDS is not, this function is called for each note after the NoteOpenCallback function is called.
	 *            See NSFFOLDERADDCALLBACK.
	 * 
	 * @return (routine) - Return status from this call -- indicates either success or what the error is. The return codes include:
	 * 
	 *         NOERROR - Successful.
	 * 
	 *         ERR_xxx - Errors returned by lower level functions. Call OSLoadString to interpret the error code for display.
	 */

	static native final short NSFDbGetNotes(//
			int hDB, //
			int NumNotes, //
			int[] NoteIDs, //
			int[] NoteOpenFlags, //
			int[] SinceSeqNum, //
			int ControlFlags, //
			int hObjectDB, //
			Pointer CallbackParam, //
			Callback GetNotesCallback, //
			NSFNoteOpenCallback NoteOpenCallback, //
			Callback ObjectAllocCallback, //
			Callback ObjectWriteCallback, //
			Pointer FolderSinceTime, // 
			Pointer FolderAddCallback);

	/**
	 * This function finds all the fields in a note. As it does so, NSFItemScan calls a user-supplied action routine for each field (item)
	 * in the note. The action routine can read the contents of the field, then returns control to NSFItemScan so the next field can be
	 * found.
	 * 
	 * This function, and your action routine, enables your program to open a note and determine which items (if any) are present. Then for
	 * each field, your program can find the field's data type, item flags, name, and contents -- all without knowing the form that was used
	 * to create the note.
	 * 
	 * The action routine can read the contents and properties of each field, but may not modify a field. This is because the action routine
	 * is passed the actual memory pointers of the field contents, and these should not be modified. If you want to modify a field, do so
	 * with the NSFItemSet* functions or with NSFItemDelete/NSFItemAppend. Modify fields only after NSFItemScan has completed -- not while
	 * it is still finding fields.
	 * 
	 * @param note_handle
	 *            NOTEHANDLE - handle to open note, whose items you wish to scan/enumerate in the action routine.
	 * 
	 * @param ActionRoutine
	 *            NSFITEMSCANPROC - Pointer to the routine, that you write, which is called for each item in a note. The pointer is of type
	 *            NSFITEMSCANPROC. NSFItemScan calls this routine for every item in the note before returning. . The routine has the
	 *            following prototype:
	 * 
	 *            STATUS LNCALLBACK ActionRoutine(WORD Spare, WORD ItemFlags, char far *Name, WORD NameLength, void far *Value, DWORD
	 *            ValueLength, void far *RoutineParameter);
	 * 
	 * @param func_param
	 *            void far * - An optional void pointer to additional data that can be used by the action routine.
	 * @return (routine) - Return status from the action routine -- indicates either success or what the error is. The action routine should
	 *         be coded to at least return the following codes:
	 * 
	 *         NOERROR - no error occurred in action routine.
	 * 
	 *         ERR_xxx - Errors returned by lower level functions, including the action routine itself.
	 */
	static native final short NSFItemScan( //
			int note_handle, //
			NSFItemScanCallback ActionRoutine, //
			Pointer func_param);

	/**
	 * This function searches through a collection for the first note whose sort column values match the given search keys. The search key
	 * consists of a buffer containing one or several values structured as an ITEM_TABLE. This function matches each value in the search key
	 * against the corresponding sorted column of the view or folder. Only sorted columns are used. The values in the search key item table
	 * must be specified in the same order as the sorted columns in the view or folder, from left to right. Other unsorted columns may lie
	 * between the sorted columns to be searched. For example, suppose view columns 1, 3, 4 and 5 are sorted. The key buffer may contain
	 * search keys for: just column 1; columns 1 and 3; or for columns 1, 3, and 4.
	 * 
	 * This function yields the COLLECTIONPOSITION of the first note in the collection that matches the keys. It also yields a count of the
	 * number of notes that match the keys. Since all notes that match the keys appear contiguously in the view or folder, you may pass the
	 * resulting COLLECTIONPOSITION and match count as inputs to NIFReadEntries to read all the entries in the collection that match the
	 * keys.
	 * 
	 * If multiple notes match the specified (partial) keys, and FIND_FIRST_EQUAL (the default flag) is specified, then the position of the
	 * first matching note is returned ("first" is defined by the note which collates before all the others in the view). The position of
	 * the last matching note is returned if FIND_LAST_EQUAL is specified. If FIND_LESS_THAN is specified, then the last note with a key
	 * value less than the specified key is returned. If FIND_GREATER_THAN is specified, then the first note with a key value greater than
	 * the specified key is returned.
	 * 
	 * This routine cannot be used to locate notes that are categorized under multiple categories (the resulting position is unpredictable),
	 * and also cannot be used to locate responses.
	 * 
	 * This routine is usually not appropriate for equality searches of key values of TYPE_TIME. A match will only be found if the key value
	 * is as precise as and is equal to the internally stored data. TYPE_TIME data is displayed with less precision than what is stored
	 * internally. Use inequality searches, such as FIND_GREATER_THAN or FIND_LESS_THAN, for TYPE_TIME key values to avoid having to find an
	 * exact match of the specified value. If the precise key value is known, however, equality searches of TYPE_TIME values are supported.
	 * 
	 * Returning the number of matches on an inequality search is not supported. In other words, if you specify any one of the following for
	 * the FindFlags argument: FIND_LESS_THAN FIND_LESS_THAN | FIND_EQUAL FIND_GREATER_THAN FIND_GREATER_THAN | FIND_EQUAL this function
	 * cannot determine the number of notes that match the search condition. In this case you may want to specify NULL for the retNumMatches
	 * argument. If you do not specify NULL for the retNumMatches argument, then *retNumMatches will be set to 1.
	 * 
	 * @param hCollection
	 *            HCOLLECTION - The handle of the collection you want to search.
	 * @param KeyBuffer
	 *            void far * - A pointer to a Domino memory object containing an ITEM_TABLE and its accompanying array of ITEMs. The ITEMs
	 *            must be specified in the same order as the sorted columns of the view or folder. The ITEM's '.NameLength' value may be set
	 *            to zero and the ITEM's name may be omitted from the information following the ITEM header. If the ITEM's name and
	 *            NameLength values are specified, they are ignored by this function. Each ITEM must match the data type of the information
	 *            in the corresponding sorted column.
	 * @param FindFlags
	 *            WORD - Flags that control the comparison rules used when a key is compared to the notes in the collection. The find flags
	 *            are described in FIND_xxx, and may be OR'ed together to combine functionailty.
	 * 
	 * @param retIndexPos
	 *            COLLECTIONPOSITION far * - The address of a COLLECTIONPOSITION in which the position within the collection where the match
	 *            is found is returned. The exact position that is returned depends on the value of the FIND_FLAGS that are specified. If
	 *            more than one matching note was found, the position of the first matching note will be returned. You can use retIndexPos
	 *            with NIFReadEntries to get the notes found by this search.
	 * 
	 * @param retNumMatches
	 *            - The address of a DWORD in which the number of matching notes is returned. This value is usually passed-on to a
	 *            subsequent call to NIFReadEntries(). It can also be used simply to determine if the name has multiple matches or not
	 *            (ambiguity).
	 * 
	 *            The number of matching entries is not supported if FIND_LESS_THAN, FIND_LESS_THAN | FIND_EQUAL, FIND_GREATER_THAN, or
	 *            FIND_GREATER_THAN | FIND_EQUAL is specified in the FindFlags argument. In this case, you may specify NULL for the
	 *            retNumMatches argument. If you do not specify NULL, then 1 is returned as the number of matching notes.
	 * @return (routine) - Return status from this call -- indicates either success or what the error is. The return codes include:
	 * 
	 *         NOERROR ERR_NOT_FOUND
	 */
	static native final short NIFFindByKey(short hCollection, //
			Pointer KeyBuffer, //
			short FindFlags, //
			Pointer retIndexPos, // 
			Pointer retNumMatches); //

	/**
	 * OSLoadString loads a string into a buffer, given the resource ID of the string and a handle to the module containing the string
	 * table.
	 * 
	 * Use OSLoadString to interpret STATUS error codes returned from C API functions. First use the ERR macro to mask off the high order
	 * bits of the STATUS code returned by the C API function. Then pass the result as the StringCode input to OSLoadString. Specify
	 * NULLHANDLE as the hModule parameter.
	 * 
	 * Under operating systems such as Unix that do not support string tables, specify NULLHANDLE as the hModule parameter. OSLoadString
	 * will search the string tables internal to Domino and Notes for a string matching the resource ID specified by StringCode.
	 * 
	 * Under Windows, you may use the hModule parameter to specify a non-Domino or non-Notes module, and the StringCode parameter to specify
	 * a non-Domino string. OSLoadString will search for the string specified by StringCode in the string table associated with the
	 * specified module first. If the string is not found, it goes on to search the string tables internal to Domino and Notes.
	 * 
	 * In order for DLLs created with the C API to find modules containing non-Domino or non-Notes error strings, the module handle of the
	 * string table and the non-Domino StringCode must be specified. Again, OSLoadString will search for the string specified by StringCode
	 * in the string table associated with the specified module first, then if it is not found, will go on to search the string tables
	 * internal to Domino and Notes. This functionality is not supported for shared objects under UNIX or for other platforms that do not
	 * support string tables.
	 * 
	 * In Domino and Notes, all input and output to the API is in Lotus Multi-Byte Character Set (LMBCS) optimized for Group 1 and all
	 * resource strings are also in LMBCS optimized for Group 1. OSLoadString does not translate resource strings after reading them from
	 * disk. If you need to convert strings from LMBCS to or from the native character set, use the C API function, OSTranslate.
	 * 
	 * @param hModule
	 *            HMODULE - Specify the module that has the string table OSLoadString should search first. Specify NULLHANDLE to cause
	 *            OSLoadString to search the string tables internal to Domino and Notes. You must specify NULLHANDLE under Unix or other
	 *            platforms that do not support string table resources. If you specify NULLHANDLE, the StringCode input argument should be
	 *            one of the STATUS error codes defined by Notes.
	 * 
	 * @param StringCode
	 *            STATUS - Resource ID corresponding to the desired string. This may be a STATUS code returned by a C API function.
	 * 
	 * @param retBuffer
	 *            char far * - The address of a text buffer in which the string associated with the STATUS code is returned. It is the
	 *            caller's responsibility to pre-allocate this buffer.
	 * 
	 * @param BufferLength
	 *            WORD - Maximum length of returned string, generally sizeof(retBuffer) -1.
	 * @return Length of the returned string, or ZERO if it is not found.
	 */
	static native final short OSLoadString(Pointer hModule, // nicht sicher
			int StringCode, //
			Pointer retBuffer, //
			short BufferLength);

	/**
	 * This function deallocates the memory associated with an open note. Closing a note does not write the contents of the note to disk.
	 * That function is performed by NSFNoteUpdate.
	 * 
	 * @param hNote
	 *            NOTEHANDLE - The handle of the note that you want to close.
	 * @return Always returns NOERROR
	 */
	static native final short NSFNoteClose(int hNote);

	/**
	 * <p>
	 * This function is an enhanced 32-bit version of NAMELookup and supports returning data greater than 64 KB. Server Administrators can
	 * define how much data can be returned in a single call by setting the NAMELOOKUP_MAX_MB variable in notes.ini. For example,
	 * NAMELOOKUP_MAX_MB=3 will allow NAMELookup2 to return 3 MB of data. If this variable is not set, the default of 1 MB is used.
	 * Increasing the default will result in a performance hit on the server returning the data. The maximum size for the amount of returned
	 * data is MAXDWORD (approximately 4,000 MB).
	 * </p>
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
	 * @param ServerName
	 *            Server where look up is performed. Look in the Domino Directories that reside on, and in use by, this server. Specify NULL
	 *            to perform the lookup on, and search Address books in use by, the local system.
	 * 
	 * @param Flags
	 *            Flags to control lookup (see NAME_LOOKUP_xxx) for a list of the legal values. Normally 0.
	 * @param NumNameSpaces
	 *            Number of views to search in each Address book.
	 * @param NameSpaces
	 *            Address of a series of view names to be searched. Each view name must be a null-terminated LMBCS string. There must be
	 *            NumNameSpaces view names in the series.
	 * @param NumNames
	 *            Number of names being looked up. This parameter must be greater than 0. If Flags specify NAME_LOOKUP_ALL and Names = "",
	 *            then specify NumNames = 1.
	 * @param Names
	 *            Address of series of names to look up. Each name must be a null-terminated LMBCS string. The series must contain NumNames
	 *            strings. If the view is categorized, you can look up the names in a specific category by specifying the category name in
	 *            this parameter (see details above). Names may = "" if Flags specifies NAME_LOOKUP_ALL and NumNames = 1. This will return
	 *            all documents, whether or not the view is categorized.
	 * @param NumItems
	 *            Number of items to be returned
	 * 
	 * @param Items
	 *            Address of series of item names to return with each match. Each item name must be a null-terminated LMBCS string. The
	 *            series must contain NumItems names.
	 * 
	 * @param retHandle
	 *            receives a handle to a buffer containing name information. Buffer consists of a header followed by a series of data
	 *            records. The number of data records is equal to the number of views searched times the number of names. Use
	 *            NAMELocateItem2, NAMEGetTextItem2, etc., to read information in the buffer. Free this buffer after processing the
	 *            information it contains.
	 * @return NOERROR - Successfully looked up specified names in Address books
	 * 
	 *         ERR_NO_SUCH_NAMES_BOOK - An Address book does not exist
	 * 
	 *         ERR_NO_SUCH_NAMESPACE - One of the specified views does not exist in any of the Address books.
	 * 
	 *         ERR_NO_NAMES_FILE - Unable to open one of the address book databases.
	 * 
	 *         ERR_xxx - Errors returned by lower level functions. Call OSLoadString to obtain the error string.
	 */
	static final native short NAMELookup2(final Pointer ServerName, // 
			final int Flags, // 
			final short NumNameSpaces, // 
			final Pointer NameSpaces, final short NumNames, //
			final Pointer Names, // 
			final short NumItems,  //
			final Pointer Items,  //
			final Pointer retHandle);

	/**
	 * <p>
	 * This function is an enhanced 32-bit version of NAMELocateNextMatch.
	 * </p>
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
	 *            Location of previous match in the record. Specify NULL to locate the first match in the record. NAMELocateNextMatch
	 *            returns a pointer to the next record in the buffer that matches the specified name. NULL is NOT returned to indicate
	 *            termination.
	 * @return Address of the next match in the record in a look up buffer.
	 */
	static final native Pointer NAMELocateNextMatch2(Pointer pLookup, //
			Pointer pName, //
			Pointer pMatch);

	/**
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
	 * 
	 * @param pLookup
	 *            Address of a look up buffer containing information returned from NAMELookup2. NAMELookup2 returns a handle. Call
	 *            OSLockObject on the handle to obtain this address.
	 * 
	 * @param pName
	 *            Previous record in the buffer. Specify NULL to locate the first record in the buffer.
	 * @param retNumMatches
	 *            Receives the number of matches in the next record. If NAMELookup2 finds more than one match for a given name in a given
	 *            view, then the record corresponding to that name in that view contains more than one match.
	 * @return The next record in the look up buffer. If pName is NULL on input, then this returns the first record in the buffer. If pName
	 *         specifies the last record in the buffer, then this returns NULL.
	 */
	static final native Pointer NAMELocateNextName2(Pointer pLookup, //
			Pointer pName, //
			Pointer retNumMatches);

	/**
	 * <p>
	 * This function is an enhanced 32-bit version of NAMEGetTextItem.
	 * </p>
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
	 * 
	 * @param pMatch
	 *            Location of a match in a look up buffer. Use NAMELocateMatch2 to obtain this location.
	 * @param Item
	 *            Item number = index of the item name in the set specified by the "Items" argument to NAMELookup2. Zero specifies the first
	 *            item. Note: this item number must not be greater than the number of items specified by the "NumItems" argument to
	 *            NAMELookup2.
	 * @param Member
	 *            Text list member number. Specify 0 if the item is of TYPE_TEXT. Member numbers are zero based: Member = 0 specifies the
	 *            first member.
	 * @param Buffer
	 *            pointer to a buffer to copy result into
	 * @param BufLen
	 *            Length, in bytes, of the buffer. Text is truncated to this length.
	 * @return Return status from this call -- indicates either success or what the error is. The return codes include:
	 * 
	 *         NOERROR - Successfully copied Item from buffer.
	 * 
	 *         ERR_NO_SUCH_ITEM - Item was not returned for this match
	 * 
	 *         ERR_NO_MORE_MEMBERS - Member number too large
	 * 
	 *         ERR_UNSUPPORTED_DATATYPE - Item data type neither TYPE_TEXT nor TYPE_TEXT_LIST
	 * 
	 *         ERR_xxx - Errors returned by lower level functions. Call OSLoadString to obtain the error string.
	 */
	static final native short NAMEGetTextItem2(Pointer pMatch, short Item, //
			short Member, //
			Pointer Buffer, //
			short BufLen);

	static final native void NSFNoteGetInfo(int handle, short meber, Pointer ret);

	static final native short NSFFormulaCompile(//
			Pointer FormulaName,//
			short FormulaNameLength,//
			Pointer FormulaText,//
			short FormulaTextLength,//
			Pointer /*FORMULAHANDLE*/rethFormula,//
			Pointer /*WORD*/retFormulaLength,//
			Pointer /*STATUS*/retCompileError,//
			Pointer /*WORD*/retCompileErrorLine,//
			Pointer /*WORD*/retCompileErrorColumn,//
			Pointer /*WORD*/retCompileErrorOffset,//
			Pointer /*WORD*/retCompileErrorLength);

	static final native short NSFSearch(//
			int hDB,//
			int hFormula,//
			Pointer /*String*/ViewTitle, //
			short SearchFlags, //
			short NoteClassMask, //
			Pointer /*TIMEDATE*/Since, //
			NSFSearchCallback EnumRoutine, //
			Pointer EnumRoutineParameter, //
			Pointer /*TIMEDATE*/retUntil);
}