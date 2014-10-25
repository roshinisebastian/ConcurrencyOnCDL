/**
 * @Author Roshini Sebastian
 * UB Email ID - roshinis@buffalo.edu
 * File Name: CDLList.java
 * Contents: This file contains the API for building the Circular Doubly Linked List
 */
package com.fpl.assignment;


//Class for Circular Doubly Linked List
public class CDLList<T> 
{		
	//Head Element
	Element head; 
	
	/*
	 * This is the constructor for the class CDLList
	 * @param v of type T
	 */
	
	public CDLList()
	{
	}
	
	//Parameterized Constructor
	public CDLList(T v) 
	{
		head = new Element(v);
		System.out.println("In here CDLList");
		head.next = head;
		head.previous = head;
		
	}
		
	//Definition of class Element
	public class Element
	{
		private T value;
		private Element next;
		private Element previous;
		
		public Element()
		{
		}
		
		
		public Element (T v)
		{
			this.value = v;
			this.next = null;
			this.previous = null;
		}
			
		public T value() 
		{ 
			return (this.value);
		}
	}
	
	public Element head() 
	{
		return (head);
		
	}
	
	//This method will create a reader on the node from where it was invoked
	public Cursor reader(Element from) 
	{
		Cursor reader = new Cursor();
		reader.current = from;
		return reader;

	}
		
	//Cursor class definition
	public class Cursor 
	{
		private Element current;
		
		//return current element
		public Element current() 
		{
			return this.current;
			
		}
		
		//return previous element
		public void previous() 
		{
			this.current = this.current.previous;
			
		}
		
		//return next element
		public void next() 
		{
			this.current = this.current.next;
		}
		
		public Writer writer() 
		{
			Writer writer = new Writer();
			writer.current = this.current;
			return (writer);
		} 
	}
		
	public class Writer 
	{
		Element current;
		
		//method description for inserting before a node
		public boolean insertBefore(T val) 
		{
			Element newNode = new Element(val);
			newNode.next = current;
			current.previous = newNode;
			Element temp = current;
			while (temp.next!=current)
			{
				temp = temp.next;
			}
			temp.next = newNode;
			newNode.previous = temp;
			return true;
		}
			
		//method description for inserting after a node
		public boolean insertAfter(T val) 
		{
			Element newNode = new Element(val);
			newNode.previous = current;
			current.next = newNode;
			Element temp = current;
			while (temp.previous!=current)
			{
				temp = temp.previous;
			}
			temp.previous = newNode;
			newNode.next = temp;
			return true;
		}
	}
	
	public void printList()
	{
		//To display the CDLList
		
		Cursor printer = new Cursor();
		printer.current = this.head;
		int i =1;
		while (printer.current.next!=head)
		{
			
			System.out.println("Value: " +i+ " "+printer.current.value+ "->");
			printer.current = printer.current.next;
			i++;
			
		}
		System.out.println("Value: "+i+" "+printer.current.value);
	}
	
}


	
