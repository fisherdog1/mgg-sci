package com.mgg;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SortedLinkedList<T>
{
	public class SLLNode<T>
	{
		private T value;
		private SLLNode<T> next;
		
		public SLLNode(T value) {
			this.value = value;
		}
		
		public SLLNode<T> getNext() {
			return next;
		}
		
		public void setNext(SLLNode<T> next) {
			this.next = next;
		}
		
		public T getValue() {
			return value;
		}
	}
	
	Comparator<T> comp;
	SLLNode<T> root;
	
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
		
		
		SLLNode<T> currentNode = root;
		int count = 0;
		
		do {
			count++;
		} while ((currentNode = currentNode.getNext()) != null);
		
		return count;
	}
	
	public void add(T item) {
		SLLNode<T> newNode = new SLLNode<T>(item);
		//Special case if root is null, can only insert at root
		if (isEmpty()) {
			root = newNode;
			return;
		}
		
		SLLNode<T> parentNode = null;
		SLLNode<T> currentNode = root;
		
		//Go up the list until a higher value is found
		while (true) {
			//Parent node cannot be null here
			//Reached end of list, must insert here
			if (parentNode != null && currentNode == null) {
				parentNode.setNext(newNode);
				return;
			}
			
			int cmp = comp.compare(currentNode.getValue(), item);
			
			//Insert in the middle of the list here (between parent and current)
			if (cmp >= 0) {
				newNode.setNext(currentNode);
				
				//Special case where only the root is currently in the list
				if (parentNode == null)
					root = newNode;
				else
					parentNode.setNext(newNode);
				
				return;
			}
			
			//Advance to next node
			parentNode = currentNode;
			currentNode = currentNode.getNext();
		}
	}
	
	public void addAll(List<T> items) {
		for (T item : items)
			this.add(item);
	}
	
	public void remove(T item) {
		if (isEmpty())
			return;
		
		SLLNode<T> parentNode = null;
		SLLNode<T> currentNode = root;
		
		//Go up the list until the value is found
		while (true) {
			int cmp = comp.compare(currentNode.getValue(), item);
			
			//Insert in the middle of the list here (between parent and current)
			if (cmp == 0) {
				//Special case for removing the root
				if (parentNode == null)
					root = root.getNext();
				else
					parentNode.setNext(currentNode.getNext());
				
				return;
			}
			
			//Advance to next node
			parentNode = currentNode;
			currentNode = currentNode.getNext();
		}
	}
	
	/**
	 * Return the linked list as an array list
	 * @return
	 */
	public ArrayList<T> getList() {
		ArrayList<T> list = new ArrayList<T>();
		
		if (isEmpty())
			return list;
		
		SLLNode<T> currentNode = root;
		
		do {
			list.add(currentNode.getValue());
		} while ((currentNode = currentNode.getNext()) != null);
		
		return list;
	}
	
	/**
	 * Misc tests for SortedLinkedList
	 */
	public void SLLTest() {
		Comparator<Integer> cmp = new Comparator<Integer>() {
			@Override
			public int compare(Integer o1, Integer o2) { return o1-o2; }
		};
		
		SortedLinkedList<Integer> sll = new SortedLinkedList<Integer>(cmp);
		
		sll.add(5);
		sll.add(4);
		sll.add(3);
		sll.add(4);
		sll.add(5);
		
		for (Integer i : sll.getList())
			System.out.printf("%d\n", i);
		
		System.out.printf("Count: %d\n", sll.count());
		sll.remove(3);
		
		for (Integer i : sll.getList())
			System.out.printf("%d\n", i);
	}
}
