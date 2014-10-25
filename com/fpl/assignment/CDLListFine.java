package com.fpl.assignment;

public class CDLListFine<T> extends CDLList<T>{
	
Element head;
Element magicElement;
	
	/**
	 * This is the constructor for the class CDLList
	 * @param v of type T
	 */
	
	public CDLListFine()
	{
		System.out.println("Default Constructor");
	}
	
	public CDLListFine(T v) 
	{
		head = new Element(v);
		System.out.println("In here CDLList");
		magicElement = new Element(v);
		head.next = magicElement;
		head.previous = magicElement;
		magicElement.previous = head;
		magicElement.next = head;
		
		
	}
		
	public class Element extends CDLList<T>.Element
	{
		private T value;
		private Element next;
		private Element previous;
		private Lock lock;
		
		public Element()
		{
			System.out.println("Default Constructor");
		}
		
		
		public Element (T v)
		{
			
			this.value = v;
			this.next = null;
			this.previous = null;
			this.lock = new Lock();
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
	
	public Cursor reader(Element from) 
	{
		synchronized(this)
		{
			if(from == magicElement)
			{
				System.out.println("Reader cannot be created on magicElement");
				return (null);
			}
			else
			{
				Cursor reader = new Cursor();	
				reader.current = from;
				return reader;
			}
			
		}
	}
		
	public class Cursor extends CDLList<T>.Cursor
	{
		private Element current;
		
		public Element current() 
		{
			return this.current;
			
		}
		public void previous() 
		{
				this.current.lock.acquire();
				Element temp;
				temp = this.current.previous;
				if(temp== magicElement)
				temp = temp.previous;
				this.current = temp;
				this.current.lock.release();
			
		}
		
		public void next() 
		{
			this.current.lock.acquire();
			Element temp;
			temp = this.current.next;
			if(temp== magicElement)
			temp = temp.next;
				this.current = temp;
			this.current.lock.release();
			
		}
		
		public Writer writer() 
		{
			Writer writer = new Writer();
			if (writer.current == magicElement)
			{
				System.out.println("Writer cannot be created on magicElement");
				return null;
			}
			else
			{
				writer.current = this.current;
				return (writer);
			}
			
		} 
	}
		 
	public class Writer extends CDLList<T>.Writer
	{
		Element current;
		
		public boolean insertBefore(T val) 
		{
			
			if(this.current == head)
				this.current = this.current.previous;
			Element oldPrevious = this.current.previous;	
			synchronized(oldPrevious.lock)
			{
				oldPrevious.lock.acquire();
				synchronized(this.current)
				{
					this.current.lock.acquire();
					if(oldPrevious.lock.isLocked && this.current.lock.isLocked)
					{
						if(oldPrevious == current.previous)
						{
							Element newNode = new Element(val);
							newNode.next = this.current;
							this.current.previous = newNode;
							oldPrevious.next = newNode;
							newNode.previous = oldPrevious;
						}
					}
					this.current.lock.release();
				}
				oldPrevious.lock.release();
				
			}
			return true;
		}
			
		
		public boolean insertAfter(T val) 
		{
			synchronized(this.current.lock)
			{
				this.current.lock.acquire();
				Element oldNext = this.current.next;
				synchronized(oldNext.lock)
				{
					this.current.next.lock.acquire();
					if(this.current.lock.isLocked && this.current.next.lock.isLocked)
					{
						if(oldNext == this.current.next )
						{	
							Element newNode = new Element(val);
							newNode.previous = this.current;
							this.current.next = newNode;
							oldNext.previous = newNode;
							newNode.next = oldNext;
						}
					}
					oldNext.lock.release();
				}
				current.lock.release();
				
			}
			return true;
		}
	}
	
	class Lock
	{
		boolean isLocked = false;		
		public synchronized  void acquire()
		{
			synchronized(this)
			{
				if(isLocked == false)
				{
					isLocked = true;
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
	
	public void printList()
	{
		//To display the CDL
		
		Cursor printer = new Cursor();
		printer.current = this.head;
		int i =1;
		while (printer.current().next!=head)
		{
			
			System.out.println("Value: " +i+ " "+printer.current.value+ "->");
			printer.current = printer.current().next;
			i++;
			
		}
		System.out.println("Value: "+i+" "+printer.current.value);
	}
}
