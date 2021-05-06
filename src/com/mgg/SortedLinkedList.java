package com.mgg;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class SortedLinkedList<T> implements Iterable<T>
{
	public class SLLNode
	{
		private SLLNode(T item) {
			this.value = item;
		}
		
		private T value;
		private SLLNode next;
	}
	
	public class SortedLinkedListIterator implements Iterator<T>
	{
		SLLNode currentNode;
		
		public SortedLinkedListIterator(SortedLinkedList<T> sortedLinkedList)
		{
			this.currentNode = sortedLinkedList.root;
		}

		@Override
		public boolean hasNext()
		{
			return currentNode != null;
		}

		@Override
		public T next()
		{
			if (currentNode != null) {
				T value = currentNode.value;
				currentNode = currentNode.next;
				return value;
			} else
				throw new NoSuchElementException("No more elements in SortedLinkedList");
		}
	}
	
	private Comparator<T> comp;
	private SLLNode root;
	
	public SortedLinkedList(Comparator<T> comparator) {
		this.comp = comparator;
	}
	
	public boolean isEmpty() {
		return root == null;
	}
	
	public void clear() {
		root = null;
	}
	
	public int count() {
		if (isEmpty())
			return 0;
		
		
		SLLNode currentNode = root;
		int count = 0;
		
		do {
			count++;
		} while ((currentNode = currentNode.next) != null);
		
		return count;
	}
	
	public void add(T item) {
		SLLNode newNode = new SLLNode(item);
		//Special case if root is null, can only insert at root
		if (isEmpty()) {
			root = newNode;
			return;
		}
		
		SLLNode parentNode = null;
		SLLNode currentNode = root;
		
		//Go up the list until a higher value is found
		while (true) {
			//Parent node cannot be null here
			//Reached end of list, must insert here
			if (parentNode != null && currentNode == null) {
				parentNode.next = newNode;
				return;
			}
			
			int cmp = comp.compare(currentNode.value, item);
			
			//Insert in the middle of the list here (between parent and current)
			if (cmp >= 0) {
				newNode.next = currentNode;
				
				//Special case where only the root is currently in the list
				if (parentNode == null)
					root = newNode;
				else
					parentNode.next = newNode;
				
				return;
			}
			
			//Advance to next node
			parentNode = currentNode;
			currentNode = currentNode.next;
		}
	}
	
	public void addAll(List<T> items) {
		for (T item : items)
			this.add(item);
	}
	
	public void remove(T item) {
		if (isEmpty())
			return;
		
		SLLNode parentNode = null;
		SLLNode currentNode = root;
		
		//Go up the list until the value is found
		while (true) {
			int cmp = comp.compare(currentNode.value, item);
			
			//Insert in the middle of the list here (between parent and current)
			if (cmp == 0) {
				//Special case for removing the root
				if (parentNode == null)
					root = root.next;
				else
					parentNode.next = currentNode.next;
				
				return;
			}
			
			//Advance to next node
			parentNode = currentNode;
			currentNode = currentNode.next;
		}
	}
	
	/**
	 * Return the linked list as an array list
	 * @return
	 */
	public ArrayList<T> getList() {
		ArrayList<T> list = new ArrayList<T>();
		
		for (T item : this)
			list.add(item);
		
		return list;
	}
	
	/**
	 * Change the comparator. The list is re-sorted immediately.
	 * @param cmp
	 */
	public void setComparator(Comparator<T> cmp) {
		ArrayList<T> tmp = getList();
		this.comp = cmp;
		clear();
		
		for (T item : tmp)
			this.add(item);
	}

	@Override
	public Iterator<T> iterator()
	{
		return new SortedLinkedListIterator(this);
	}
}
