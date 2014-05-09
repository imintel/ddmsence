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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Utility class for a list with dynamically added contents.
 * 
 * <p> LazyList wraps around a standard ArrayList and ensures that any get() activity which returns a <code>null</code>
 * value will return a freshly instantiated item instead.</p>
 * 
 * <p> This class should allow the Component Builder framework to function well within a form-based UI, where child
 * components are added / removed at the web page level. </p>
 * 
 * <p> This class is similar in intent to the Apache commons-collections version, but my implementation differs in the
 * following ways: </p>
 * <ul>
 * <li>Simplified layers of abstraction.</li>
 * <li>Eliminated overhead of Factory boilerplate to instantiate new items. Classes can just be instantiated through
 * the basic no-arg constructor.</li>
 * <li>Interim positions are also instantiated when an index beyond the size of the list is requested.</li>
 * </ul>
 * 
 * @author Brian Uri!
 * @since 1.9.0
 */
public class LazyList implements List, Serializable {
	private static final long serialVersionUID = 1241524905390180551L;
	private List<?> _list;
	private Class _class;

	/**
	 * Base constructor for a lazy list.
	 * 
	 * @param instantiatingClass the class that will be instantiated when get() returns null
	 * @throws IllegalArgumentException if either the list or class are null
	 */
	public LazyList(Class instantiatingClass) {
		this(new ArrayList(), instantiatingClass);
	}

	/**
	 * Complete constructor for a lazy list.
	 * 
	 * @param list the underlying list that will be wrapped
	 * @param instantiatingClass the class that will be instantiated when get() returns null
	 * @throws IllegalArgumentException if either the list or class are null
	 */
	public LazyList(List<?> list, Class instantiatingClass) {
		Util.requireValue("instantiatingClass", instantiatingClass);
		_list = (list == null) ? new ArrayList() : new ArrayList(list);
		_class = instantiatingClass;
	}

	/**
	 * Write the list out using a custom routine.
	 * 
	 * @param out the output stream
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.defaultWriteObject();
		out.writeObject(_list);
	}

	/**
	 * Read the list in using a custom routine.
	 * 
	 * @param in the input stream
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		_list = (List<?>) in.readObject();
	}

	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object object) {
		if (object == this) {
			return true;
		}
		return getList().equals(object);
	}

	/**
	 * @see Object#hashCode()
	 */
	public int hashCode() {
		return getList().hashCode();
	}

	/**
	 * @see Object#toString()
	 */
	public String toString() {
		return getList().toString();
	}

	/**
	 * Intercepts the get() method to create new items if get() might return null. The type of the item returned is
	 * determined by the instantiatingClass of the LazyList, and will be instantiated with the no-args constructor via
	 * newInstance().
	 * 
	 * <p> If an index is requested beyond the bounds of the list size, the list will extend to that position and
	 * interim positions will also be instantiated. </p>
	 * 
	 * @param index the index to retrieve
	 */
	public Object get(int index) {
		try {
			int size = getList().size();
			// Expand list size, if necessary
			for (int i = size; i <= index; i++) {
				add(getInstantiatingClass().newInstance());
			}
			return (getList().get(index));
		}
		catch (IllegalAccessException e) {
			throw new IllegalArgumentException("Cannot instantiate list item because instantiatingClass is invalid: "
				+ e.getMessage());
		}
		catch (InstantiationException e) {
			throw new IllegalArgumentException("Cannot instantiate list item because instantiatingClass is invalid: "
				+ e.getMessage());
		}
	}

	/**
	 * Intercepts the subList method to maintain a LazyList on the returned sublist.
	 * 
	 * @see java.util.List#subList(int, int)
	 */
	public List<?> subList(int fromIndex, int toIndex) {
		List<?> sub = getList().subList(fromIndex, toIndex);
		return new LazyList(sub, getInstantiatingClass());
	}

	/**
	 * Accessor for the wrapped list
	 */
	private List getList() {
		return _list;
	}

	/**
	 * Accessor for the class that will be instantiated lazily
	 */
	public Class getInstantiatingClass() {
		return _class;
	}

	/**
	 * @see java.util.List#size()
	 */
	public int size() {
		return (getList().size());
	}

	/**
	 * @see java.util.List#isEmpty()
	 */
	public boolean isEmpty() {
		return (getList().isEmpty());
	}

	/**
	 * @see java.util.List#contains(java.lang.Object)
	 */
	public boolean contains(Object o) {
		return (getList().contains(o));
	}

	/**
	 * @see java.util.List#iterator()
	 */
	public Iterator<?> iterator() {
		return (getList().iterator());
	}

	/**
	 * @see java.util.List#toArray()
	 */
	public Object[] toArray() {
		return (getList().toArray());
	}

	/**
	 * @see java.util.List#toArray(Object[])
	 */
	public Object[] toArray(Object[] a) {
		return (getList().toArray(a));
	}

	/**
	 * @see java.util.List#add(java.lang.Object)
	 */
	public boolean add(Object o) {
		return (getList().add(o));
	}

	/**
	 * @see java.util.List#remove(java.lang.Object)
	 */
	public boolean remove(Object o) {
		return (getList().remove(o));
	}

	/**
	 * @see java.util.List#containsAll(java.util.Collection)
	 */
	public boolean containsAll(Collection c) {
		return (getList().containsAll(c));
	}

	/**
	 * @see java.util.List#addAll(java.util.Collection)
	 */
	public boolean addAll(Collection c) {
		return (getList().addAll(c));
	}

	/**
	 * @see java.util.List#addAll(int, java.util.Collection)
	 */
	public boolean addAll(int index, Collection c) {
		return (getList().addAll(index, c));
	}

	/**
	 * @see java.util.List#removeAll(java.util.Collection)
	 */
	public boolean removeAll(Collection c) {
		return (getList().removeAll(c));
	}

	/**
	 * @see java.util.List#retainAll(java.util.Collection)
	 */
	public boolean retainAll(Collection c) {
		return (getList().retainAll(c));
	}

	/**
	 * @see java.util.List#clear()
	 */
	public void clear() {
		getList().clear();
	}

	/**
	 * @see java.util.List#set(int, java.lang.Object)
	 */
	public Object set(int index, Object element) {
		return (getList().set(index, element));
	}

	/**
	 * @see java.util.List#add(int, java.lang.Object)
	 */
	public void add(int index, Object element) {
		getList().add(index, element);
	}

	/**
	 * @see java.util.List#remove(int)
	 */
	public Object remove(int index) {
		return (getList().remove(index));
	}

	/**
	 * @see java.util.List#indexOf(java.lang.Object)
	 */
	public int indexOf(Object o) {
		return (getList().indexOf(o));
	}

	/**
	 * @see java.util.List#lastIndexOf(java.lang.Object)
	 */
	public int lastIndexOf(Object o) {
		return (getList().lastIndexOf(o));
	}

	/**
	 * @see java.util.List#listIterator()
	 */
	public ListIterator<?> listIterator() {
		return (getList().listIterator());
	}

	/**
	 * @see java.util.List#listIterator(int)
	 */
	public ListIterator<?> listIterator(int index) {
		return (getList().listIterator(index));
	}
}
