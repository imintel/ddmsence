/* Copyright 2010 - 2013 by Brian Uri!
   
   This file is part of DDMSence.
   
   This library is free software; you can redistribute it and/or modify
   it under the terms of version 3.0 of the GNU Lesser General Public 
   License as published by the Free Software Foundation.
   
   This library is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the 
   GNU Lesser General Public License for more details.
   
   You should have received a copy of the GNU Lesser General Public 
   License along with DDMSence. If not, see <http://www.gnu.org/licenses/>.

   You can contact the author at ddmsence@urizone.net. The DDMSence
   home page is located at http://ddmsence.urizone.net/
 */
package buri.ddmsence.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import buri.ddmsence.AbstractBaseComponent;
import buri.ddmsence.AbstractBaseTestCase;
import buri.ddmsence.ddms.IBuilder;
import buri.ddmsence.ddms.IDDMSComponent;
import buri.ddmsence.ddms.resource.Dates;
import buri.ddmsence.ddms.resource.Rights;

/**
 * A collection of tests related to the LazyList
 * 
 * @author Brian Uri!
 * @since 1.9.9
 */
public class LazyListTest extends AbstractBaseTestCase {

	public LazyListTest() {
		super(null);
	}

	public void testSerializableInterface() throws Exception {
		LazyList list = new LazyList(Dates.Builder.class);
		list.add(new Dates.Builder());
		((Dates.Builder) (list.get(0))).setCreated("05-24-2011");

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(out);
		oos.writeObject(list);
		oos.close();
		byte[] serialized = out.toByteArray();
		assertTrue(serialized.length > 0);

		ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(serialized));
		LazyList unserializedList = (LazyList) ois.readObject();
		assertEquals(list.size(), unserializedList.size());
		assertEquals(((Dates.Builder) (list.get(0))).getCreated(),
			((Dates.Builder) (unserializedList.get(0))).getCreated());
	}

	public void testUnmodifiableList() {
		List<String> list = new ArrayList<String>();
		list.add("Test");
		LazyList lazyList = new LazyList(Collections.unmodifiableList(list), String.class);
		try {
			lazyList.set(0, "Test2");
		}
		catch (UnsupportedOperationException e) {
			fail("Should have unwrapped the unmodifiable list.");
		}
	}

	public void testObjectMethods() {
		List<String> list = new ArrayList<String>();
		list.add("Test");
		LazyList lazyList = new LazyList(list, String.class);
		assertEquals(lazyList, lazyList);
		assertEquals(lazyList, list);
		assertEquals(lazyList.hashCode(), list.hashCode());
		assertEquals(lazyList.toString(), list.toString());
	}

	public void testListInterface() {
		// Because a Java ArrayList underlies this Object, I'm not too worried that these tests are necessary.

		LazyList list = new LazyList(Dates.Builder.class);
		assertTrue(list.isEmpty());
		Dates.Builder datesBuilder = new Dates.Builder();
		list.add(datesBuilder);
		assertEquals(1, list.size());
		assertFalse(list.isEmpty());
		list.add(datesBuilder);
		assertEquals(2, list.size());
		list.remove(1);
		assertEquals(1, list.size());
		Rights.Builder rightsBuilder = new Rights.Builder();
		list.add(rightsBuilder);
		assertTrue(list.contains(rightsBuilder));
		assertEquals(2, list.size());
		list.remove(rightsBuilder);
		assertFalse(list.contains(rightsBuilder));
		assertEquals(1, list.size());
		for (Iterator iterator = list.iterator(); iterator.hasNext();) {
			IBuilder builder = (IBuilder) iterator.next();
			assertNotNull(builder);
		}
		assertEquals(0, list.indexOf(datesBuilder));
		list.add(datesBuilder);
		assertEquals(1, list.lastIndexOf(datesBuilder));

		IBuilder[] array = new IBuilder[0];
		Object[] returnedArray = list.toArray();
		assertEquals(2, returnedArray.length);
		IBuilder[] returnedBuilderArray = (IBuilder[]) list.toArray(array);
		assertEquals(2, returnedBuilderArray.length);

		assertNotNull(list.listIterator());
		assertNotNull(list.listIterator(1));

		list.set(1, rightsBuilder);
		assertEquals(rightsBuilder, list.get(1));
		list.add(1, datesBuilder);
		assertEquals(datesBuilder, list.get(1));
		assertEquals(rightsBuilder, list.get(2));

		List newList = new ArrayList();
		newList.add(rightsBuilder);
		list.removeAll(newList);
		assertFalse(list.containsAll(newList));
		assertEquals(2, list.size());
		list.add(rightsBuilder);
		list.retainAll(newList);
		assertEquals(1, list.size());
		assertTrue(list.containsAll(newList));

		list.addAll(newList);
		assertEquals(2, list.size());
		list.add(datesBuilder);
		list.addAll(2, newList);
		assertEquals(rightsBuilder, list.get(2));
		assertEquals(datesBuilder, list.get(3));

		list.add(datesBuilder);
		list.clear();
		assertTrue(list.isEmpty());
	}

	public void testSublist() {
		LazyList list = new LazyList(Dates.Builder.class);
		Rights.Builder rightsBuilder = new Rights.Builder();
		Dates.Builder datesBuilder = new Dates.Builder();

		list.add(rightsBuilder);
		list.add(rightsBuilder);
		list.add(rightsBuilder);
		list.add(datesBuilder);

		List subList = list.subList(0, 1);
		assertFalse(subList.contains(datesBuilder));
		assertEquals(1, subList.size());

		assertNotNull(subList.get(10));
	}

	public void testGet() {
		LazyList list = new LazyList(Dates.Builder.class);
		assertTrue(list.isEmpty());
		Dates.Builder datesBuilder = new Dates.Builder();
		list.add(datesBuilder);
		assertEquals(datesBuilder, list.get(0));
		Dates.Builder builder = (Dates.Builder) list.get(3);
		assertNotNull(builder);
		assertEquals(4, list.size());
	}

	public void testNullList() {
		try {
			new LazyList(null, String.class);
		}
		catch (NullPointerException e) {
			fail("Should not have thrown NPE.");
		}
	}

	public void testListGetExceptions() {
		LazyList list = new LazyList(null, AbstractBaseComponent.class);
		try {
			list.get(0);
			fail("Allowed invalid class.");
		}
		catch (IllegalArgumentException e) {
			expectMessage(e, "Cannot instantiate list item");
		}

		list = new LazyList(null, IDDMSComponent.class);
		try {
			list.get(0);
			fail("Allowed invalid class.");
		}
		catch (IllegalArgumentException e) {
			expectMessage(e, "Cannot instantiate list item");
		}
	}
}
