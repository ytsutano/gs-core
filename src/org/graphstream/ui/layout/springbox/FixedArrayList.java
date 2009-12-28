package org.graphstream.ui.layout.springbox;

import java.util.ArrayList;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.RandomAccess;

/**
 * Array list with immutable element indices.
 * 
 * <p>A fixed array list is like an array list, but it ensures the property that
 * each element will always stay at the same index, even if elements are
 * removed in between. The counterpart of this property is that the array
 * handles by itself the insertion of new elements (since when an element is
 * removed in the middle, this position can be reused), and therefore indices
 * cannot be chosen (i.e. only the {@link #add(Object)} and
 * {@link #addAll(Collection)} methods are usable to insert new elements in the
 * array).</p>
 * 
 * <p>This is the reason why this does not implement the List interface, because
 * the add(int,E) method cannot be implemented.</p>
 * 
 * <p>Furthermore, this array cannot contain null values, because it marks
 * unused positions within the array using the null value.</p>
 * 
 * @author Antoine Dutot
 * @since 20040912
 */
public class FixedArrayList<E>
	implements Collection<E>, RandomAccess
{
// Attributes

	/**
	 * List of elements.
	 */
	protected ArrayList<E> elements = new ArrayList<E>();

	/**
	 * List of free indices.
	 */
	protected ArrayList<Integer> freeIndices = new ArrayList<Integer>();

	/**
	 * Last inserted element index.
	 */
	protected int lastIndex = -1;

// Constructors

	public
	FixedArrayList()
	{
		elements = new ArrayList<E>();
		freeIndices = new ArrayList<Integer>( 16 );
	}

	public
	FixedArrayList( int capacity )
	{
		elements = new ArrayList<E>( capacity );
		freeIndices = new ArrayList<Integer>( 16 );
	}

// Accessors

	/**
	 * Number of elements in the array.
	 * @return The number of elements in the array.
	 */
	public int
	size()
	{
		return elements.size() - freeIndices.size();
	}

	/**
	 * Real size of the array, counting elements that have been erased.
	 * @see #unsafeGet(int)
	 */
	public int
	realSize()
	{
		return elements.size();
	}

	public boolean
	isEmpty()
	{
		return( size() == 0 );
	}

	/**
	 * I-th element.
	 * @param i The element index.
	 * @return The element at index <code>i</code>.
	 */
	public E
	get( int i )
	{
		E e = elements.get( i );

		if( e == null )
			throw new NoSuchElementException( "no element at index " + i );

		return e;
	}

	/**
	 * I-th element. Like the {@link #get(int)} method but it does not check
	 * the element does not exists at the given index.
	 * @param i The element index.
	 * @return The element at index <code>i</code>.
	 */
	public E
	unsafeGet( int i )
	{
		return elements.get( i );
	}

	public boolean
	contains( Object o )
	{
		int n = elements.size();

		for( int i=0; i<n; ++i )
		{
			E e = elements.get( i );
	
			if( e != null )
			{
				if( e == o )
					return true;

				if( elements.equals( o ) )
					return true;
			}
		}

		return false;
	}

	public boolean
	containsAll( Collection<?> c )
	{
		for( Object o: c )
		{
			if( ! contains( o ) )
				return false;
		}

		return true;
	}

	@SuppressWarnings("unchecked")
	public boolean
	equals( Object o )
	{
		if( o instanceof FixedArrayList )
		{
			FixedArrayList<? extends E> other = (FixedArrayList<? extends E>) o;

			int n = size();

			if( other.size() == n )
			{
				for( int i=0; i<n; ++i )
				{
					E e0 = elements.get( i );
					E e1 = other.elements.get( i );

					if( e0 != e1 )
					{
						if( e0 == null && e1 != null )
							return false;

						if( e0 != null && e1 == null )
							return false;

						if( ! e0.equals( e1 ) )
							return false;
					}
				}

				return true;
			}
		}

		return false;
	}

	public java.util.Iterator<E>
	iterator()
	{
		return new FixedArrayIterator();
	}

	/**
	 * Last index used by the {@link #add(E)} method.
	 * @return The last insertion index.
	 */
	public int
	getLastIndex()
	{
		return lastIndex;
	}
	
	/**
	 * The index that will be used in case of a next insertion in this array.
	 * @return
	 */
	public int
	getNextAddIndex()
	{
		int n = freeIndices.size();
		
		if( n > 0 )
		     return freeIndices.get( n - 1 );
		else return elements.size();
	}

	public Object[]
	toArray()
	{
		int n = size();
		int m = elements.size();
		int j = 0;
		Object a[] = new Object[n];

		for( int i=0; i<m; ++i )
		{
			E e = elements.get( i );

			if( e != null )
				a[j++] = e;
		}

		assert( j == n );
		return a;
	}

	public <T> T[]
	toArray( T[] a )
	{
		// TODO
		throw new RuntimeException( "not implemented yet" );
	}

// Commands

	/**
	 * Add one <code>element</code> in the array. The index used for inserting
	 * the element is then available using {@link #getLastIndex()}.
	 * @see #getLastIndex()
	 * @param element The element to add.
	 * @return Always true.
	 * @throws NullPointerException If a null value is inserted.
	 */
	public boolean
	add( E element )
		throws java.lang.NullPointerException
	{
		if( element == null )
			throw new java.lang.NullPointerException( "this array cannot contain null value" );

		int n = freeIndices.size();

		if( n > 0 )
		{
			int i = freeIndices.remove( n - 1 );
			elements.set( i, element );
			lastIndex = i;
		}
		else
		{
			elements.add( element );
			lastIndex = elements.size() - 1;
		}

		return true;
	}

	public boolean
	addAll( Collection<? extends E> c )
		throws UnsupportedOperationException
	{
		java.util.Iterator<? extends E> k = c.iterator();
		
		while( k.hasNext() )
		{
			add( k.next() );
		}

		return true;
	}

	/**
	 * Remove the element at index <code>i</code>.
	 * @param i Index of the element to remove.
	 * @return The removed element.
	 */
	public E
	remove( int i )
	{
		int n = elements.size();

		if( i < 0 || i >= n )
			throw new ArrayIndexOutOfBoundsException( "index "+i+" does not exist" );

		if( n > 0 )
		{
			if( elements.get( i ) == null )
				throw new NullPointerException( "no element stored at index " + i );

			if( i == ( n - 1 ) )
			{
				return elements.remove( i );
			}
			else
			{
				E e = elements.get( i );
				elements.set( i, null );
				freeIndices.add( i );
				return e;
			}
		}

		throw new ArrayIndexOutOfBoundsException( "index "+i+" does not exist" );
	}

	protected void
	removeIt( int i )
	{
		remove( i );
	}

	/**
	 * Remove the element <code>e</code>.
	 * @param e The element to remove.
	 * @return True if removed.
	 */
	public boolean
	remove( Object e )
	{
		int n = elements.size();

		for( int i=0; i<n; ++i )
		{
			if( elements.get( i ) == e )
			{
				elements.remove( i );
				return true;
			}
		}

		return false;
	}

	public boolean
	removeAll( Collection<?> c )
	{
		throw new UnsupportedOperationException( "not implemented yet" );
	}

	public boolean
	retainAll( Collection<?> c )
	{
		throw new UnsupportedOperationException( "not implemented yet" );
	}

	public void
	clear()
	{
		elements.clear();
		freeIndices.clear();
	}

// Nested classes

protected class FixedArrayIterator
	implements java.util.Iterator<E>
{
	int i;

	public
	FixedArrayIterator()
	{
		i = -1;
	}

	public boolean
	hasNext()
	{
		int n = elements.size();

		for( int j=i+1; j<n; ++j )
		{
			if( elements.get( j ) != null )
				return true;
		}

		return false;
	}

	public E
	next()
	{
		int n = elements.size();

		for( int j=i+1; j<n; ++j )
		{
			E e = elements.get( j );

			if( e != null )
			{
				i = j;
				return e;
			}
		}

		throw new NoSuchElementException( "no more elements in iterator" );
	}

	public void
	remove()
		throws UnsupportedOperationException
	{
//		throw new UnsupportedOperationException( "not implemented yet" );

		if( i >= 0 && i < elements.size() && elements.get( i ) != null )
		{
			removeIt( i );	// A parent class method cannot be called if it has
							// the same name as one in the inner class
							// (normal), but even if they have distinct
							// arguments types. Hence this strange removeIt()
							// method...
		}
		else
		{
			throw new IllegalStateException( "no such element" );
		}

	}
}

}