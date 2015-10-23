package de.foconis.examples;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ibm.domino.napi.c.C;

import de.foconis.domino.napi.MemBuffer;
import de.foconis.domino.napi.Napi;
import de.foconis.domino.napi.NapiException;
import de.foconis.domino.napi.c.MemBufferImpl;
import de.foconis.domino.napi.types.DHANDLE;
import lotus.domino.NotesThread;

/**
 * Demo app zur Verwendung der NameLookup2 Methode
 */
public class App 
{
	interface LookupCallback {

		void processEntry(Map<String, List<String>> ret);

	}
	private static final int VALUE_BUFFER_SIZE = 64000;
	private static String SERVER = "CN=srv-01-nprod/O=FOCONIS";
	public static void main( String[] args ) throws NapiException
	{
		C.initLibrary(null);
		NotesThread.sinitThread();
		try {
			List<String> ret = getGroupsOfUser("CN=Roland Praml/OU=01/OU=int/O=FOCONIS");
			System.out.println("Gruppen von Roland: " + ret);

			ret = resolveGroups("LocalDomainAdmins");
			System.out.println("LocalDomainAdmins: " + ret);
		} finally {
			NotesThread.stermThread();
		}
	}
	/**
	 * Liefert die Gruppen  für den User
	 * @param names
	 * @return
	 * @throws NapiException
	 */
	private static List<String> getGroupsOfUser(String... names) throws NapiException {
		String[] views = new String[] { "$ServerAccess" };
		String[] items = new String[] {"GroupType", "ListName" };
		final List<String> ret = new ArrayList<String>();

		final List<String> nextLookup = new ArrayList<String>();
		LookupCallback lc = new LookupCallback() {
			@Override
			public void processEntry(Map<String, List<String>> entry) {
				//System.out.println("ListName=" + entry.get("ListName"));
				// Hier werden alle gefundenen Einträge gesammelt und gleichzeitig in den "nextLookup"
				// eingefügt, so dass die Gruppen transitiv aufgelöst werden.
				String groupType = entry.get("GroupType").get(0);
				// Nur ACL + mehrere Zwecke berücksichtigen
				if ("0".equals(groupType)|| "2".equals(groupType)) {
					if (ret.add(entry.get("ListName").get(0))) {
						nextLookup.addAll(entry.get("ListName"));
					}
				}
			}
		};
		while(true) {
			lookup(SERVER, views, names, items, lc);
			if (nextLookup.isEmpty()) {
				break;
			}
			names = nextLookup.toArray(new String[nextLookup.size()]);
			nextLookup.clear();
		}
		return ret;
	}

	private static List<String> resolveGroups(String... names) throws NapiException {
		String[] views = new String[] { "$Users" };
		String[] items = new String[] {"Type", "GroupType", "Members" };
		final List<String> ret = new ArrayList<String>();

		final List<String> nextLookup = new ArrayList<String>();
		LookupCallback lc = new LookupCallback() {
			@Override
			public void processEntry(Map<String, List<String>> entry) {
				//System.out.println("ListName=" + entry.get("ListName"));
				// Hier werden alle gefundenen Einträge gesammelt und gleichzeitig in den "nextLookup"
				// eingefügt, so dass die Gruppen transitiv aufgelöst werden.
				String type = entry.get("Type").get(0);
				if ("Group".equals(type)) {
					String groupType = entry.get("GroupType").get(0);
					// Nur ACL + mehrere Zwecke berücksichtigen
					if ("0".equals(groupType)|| "2".equals(groupType)) {
						if (ret.addAll(entry.get("Members"))) {
							nextLookup.addAll(entry.get("Members"));
						}
					}
				}
			}
		};
		while(true) {
			lookup(SERVER, views, names, items, lc);
			if (nextLookup.isEmpty()) {
				break;
			}
			names = nextLookup.toArray(new String[nextLookup.size()]);
			nextLookup.clear();
		}
		return ret;
	}

	private static void lookup(String server, String[] views, String[] names, String[] items, LookupCallback callback) throws NapiException {
		//System.out.println("Resolve groups: " + Arrays.toString(names));
		DHANDLE hLookup = Napi.INSTANCE.NAMELookup2(server, 0, views, names, items);
		MemBuffer pLookup = Napi.INSTANCE.OSLockObject(hLookup);
		MemBuffer pName = MemBufferImpl.wrapAddress(0);
		MemBuffer textBuffer = new MemBufferImpl(VALUE_BUFFER_SIZE);
		// ValueList wird immer wieder verwendet

		try {
			int m = 0;
			for (int v = 0; v < views.length; v++) {// TODO: was kommt erster, Ansichten oder Namen?
				for (int i = 0; i < names.length; i++) {

					// diese Methode verändert PName und liefert die Anzahl der gefundenen Einträge zurück
					int numMatches = Napi.INSTANCE.NAMELocateNextName2(pLookup, pName);

					MemBuffer pMatch = MemBufferImpl.wrapAddress(0);

					for (int j = 0; j < numMatches; j++) { // über die gefundenen Einträge iterieren wir nun

						Map<String,	List<String>> ret = new HashMap<String,	List<String>>();
						Napi.INSTANCE.NAMELocateNextMatch2(pLookup, pName, pMatch);

						for (int k = 0; k < items.length; k++) {
							List<String> valueList = new ArrayList<String>();
							int l = 0;
							while (Napi.INSTANCE.NAMEGetTextItem2(pMatch, k, l++, textBuffer, VALUE_BUFFER_SIZE)) {
								textBuffer.setOffset(0); // read from begin - textbuffer is reused!
								valueList.add(textBuffer.readLMBCS());
							}

							ret.put(items[k], valueList);
						}
						callback.processEntry(ret);
					}
				}
			}
		} finally {
			textBuffer.recycle();
			Napi.INSTANCE.OSUnlockObject(hLookup);
			Napi.INSTANCE.OSMemFree(hLookup);
		}
	}
}
