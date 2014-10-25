package com.fpl.assignment;


public class CDLCoarse<T> extends CDLList<T> {

	
public Element head;
public static Lock coarseLock;
	
	/**
	 * This is the constructor for the class CDLList
	 * @param v of type T
	 */
	public CDLCoarse(T v) 
	{
		super();
		head = new Element(v);
		System.out.println("In the constructor of CDLCoarse");
		head.next = head;
		head.previous = head;
		coarseLock = new Lock();
		
	}
		
	public class Element extends CDLList<T>.Element
	{
		private T value;
		private Element next;
		private Element previous;
		
		public Element()
		{
			super();
		}
		
		public Element (T v)
		{
			this.value = v;
			System.out.println("In here value of v: "+ this.value);
			this.next = null;
			this.previous = null;
		}
			
		public synchronized T value() 
		{ 
			synchronized(this)
			{
				return (this.value);
			}
		}
	}
	
	public Element head() 
	{
		return (head);
		
	}
	
	public synchronized Cursor reader(Element from) 
	{
		synchronized(this)
		{
			Cursor reader = new Cursor();
			reader.current = from;
			return reader;
		}
		
	}
		
	public class Cursor extends CDLList<T>.Cursor
	{
		private Element current;
		
		public synchronized Element current() 
		{
			synchronized(CDLCoarse.coarseLock)
			{
				return this.current;
			}
			
		}
		public void previous() 
		{
			synchronized(CDLCoarse.coarseLock)
			{
				this.current = this.current.previous;
			}
			
		}
		
		public void next() 
		{
			synchronized(CDLCoarse.coarseLock)
			{
				this.current = this.current.next;
			}
		}
		
		public Writer writer() 
		{
			Writer writer = new Writer();
			writer.current = this.current;
			return (writer);
		} 
	}
		
	public class Writer extends CDLList<T>.Writer
	{
		Element current;
		public synchronized boolean insertBefore(T val) 
		{
			synchronized(CDLCoarse.coarseLock)
			{
				CDLCoarse.coarseLock.acquire();
				System.out.println("Lock Acquired!!!");
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
				CDLCoarse.coarseLock.release();
				System.out.println("Lock Released!!!");
			}
			return true;
		}
			
		public synchronized boolean insertAfter(T val) 
		{
			synchronized(CDLCoarse.coarseLock)
			{
				CDLCoarse.coarseLock.acquire();
				System.out.println("Lock Acquired!!!");
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
				CDLCoarse.coarseLock.release();
					System.out.println("Lock Released!!");
			}
			return true;
		}
	}
	
	public void printList()
	{
		//To display the CDL
		
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

class Lock
{
	boolean isLocked = false;
	static int count = 0;
	
	public synchronized  void acquire()
	{
		synchronized(this)
		{
			if(isLocked == false)
			{
				isLocked = true;
				count++;
			}
			while(isLocked!=true)
			{
				try {
					wait();
				} catch (InterruptedException e) {
				}
			}
		
		}
	}
	
	public synchronized void release()
	{
		synchronized(this)
		{
			isLocked = false;
			notifyAll();
		}
	}
	
}

