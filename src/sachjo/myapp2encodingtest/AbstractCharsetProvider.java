/*
 * Copyright 2000-2004 Sun Microsystems, Inc.  All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * CA 95054 USA or visit www.sun.com if you need additional information or
 * have any questions.
 */

package sachjo.myapp2encodingtest;
 
import java.lang.ref.SoftReference;
import java.nio.charset.Charset;
import java.nio.charset.spi.CharsetProvider;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
//import sun.misc.ASCIICaseInsensitiveComparator;


/**
 * Abstract base class for charset providers.
 *
 * @author Mark Reinhold
 */

public class AbstractCharsetProvider
extends CharsetProvider
{

    /* Maps canonical names to class names
     */
    private Map classMap
	= new TreeMap(ASCIICaseInsensitiveComparator.CASE_INSENSITIVE_ORDER);

    /* Maps alias names to canonical names
     */
    private Map aliasMap
	= new TreeMap(ASCIICaseInsensitiveComparator.CASE_INSENSITIVE_ORDER);

    /* Maps canonical names to alias-name arrays
     */
    private Map aliasNameMap
	= new TreeMap(ASCIICaseInsensitiveComparator.CASE_INSENSITIVE_ORDER);

    /* Maps canonical names to soft references that hold cached instances
     */
    private Map cache
	= new TreeMap(ASCIICaseInsensitiveComparator.CASE_INSENSITIVE_ORDER);

    private String packagePrefix;

    protected AbstractCharsetProvider() {
        packagePrefix = "sun.nio.cs";
    }

    protected AbstractCharsetProvider(String pkgPrefixName) {
        packagePrefix = pkgPrefixName;
    }

    /* Add an entry to the given map, but only if no mapping yet exists
     * for the given name.
     */
    private static void put(Map m, String name, Object value) {
        if (!m.containsKey(name))
            m.put(name, value);
    }

    private static void remove(Map m, String name) {
        Object x  = m.remove(name);
        assert (x != null);
    }

    /* Declare support for the given charset
     */
    protected void charset(String name, String className, String[] aliases) {
        synchronized (this) {
            put(classMap, name, className);
            for (int i = 0; i < aliases.length; i++)
                put(aliasMap, aliases[i], name);
            put(aliasNameMap, name, aliases);
            cache.clear();
        }
    }

    protected void deleteCharset(String name, String[] aliases) {
        synchronized (this) {
            remove(classMap, name);
            for (int i = 0; i < aliases.length; i++)
                remove(aliasMap, aliases[i]);
            remove(aliasNameMap, name);
            cache.clear();
        }
    }

    /* Late initialization hook, needed by some providers
     */
    protected void init() { }

    private String canonicalize(String charsetName) {
        String acn = (String)aliasMap.get(charsetName);
        return (acn != null) ? acn : charsetName;
    }

    private Charset lookup(String csn) {

        // Check cache first
        SoftReference sr = (SoftReference)cache.get(csn);
        if (sr != null) {
            Charset cs = (Charset)sr.get();
            if (cs != null)
                return cs;
        }

        // Do we even support this charset?
        String cln = (String)classMap.get(csn);

        if (cln == null)
            return null;

        // Instantiate the charset and cache it
        try {

            Class c = Class.forName(packagePrefix + "." + cln,
                                    true,
                                    this.getClass().getClassLoader());

            Charset cs = (Charset)c.newInstance();
            cache.put(csn, new SoftReference(cs));
            return cs;
        } catch (ClassNotFoundException x) {
            return null;
        } catch (IllegalAccessException x) {
            return null;
        } catch (InstantiationException x) {
            return null;
        }
    }

    public final Charset charsetForName(String charsetName) {
        synchronized (this) {
            init();
            return lookup(canonicalize(charsetName));
        }
    }

    public final Iterator<Charset> charsets() {

        final ArrayList ks;
        synchronized (this) {
            init();
            ks = new ArrayList(classMap.keySet());
        }

        return new Iterator<Charset>() {
			Iterator i = ks.iterator();

			public boolean hasNext() {
				return i.hasNext();
			}

			public Charset next() {
				String csn = (String)i.next();
				return lookup(csn);
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
    }

    public final String[] aliases(String charsetName) {
        synchronized (this) {
            init();
            return (String[])aliasNameMap.get(charsetName);
        }
    }

}
