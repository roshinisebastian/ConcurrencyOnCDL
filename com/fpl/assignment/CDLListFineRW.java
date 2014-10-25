package com.fpl.assignment;

import java.util.concurrent.atomic.AtomicInteger;



public class CDLListFineRW<T> extends CDLList<T>{
	
Element head;
Element magicElement;
	
	/**
	 * This is the constructor for the class CDLList
	 * @param v of type T
	 */
	
	public CDLListFineRW()
	{
		System.out.println("CDLListFineRW Default Constructor");
	}
	
	public CDLListFineRW(T v) 
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
		private ReadWriteLockFine lock;
		
		public Element()
		{
			System.out.println("Default Constructor");
		}
		
		
		public Element (T v)
		{
			System.out.println("In here Element 2");
			this.value = v;
			System.out.println("In here value of v: "+ this.value);
			this.next = null;
			this.previous = null;
			this.lock = new ReadWriteLockFine();
		}
			
		public T value() 
		{ 
				return (this.value);
		}
		
		public void readersWriters()
		{
			System.out.println("Readers: "+this.lock.readerCount);
			System.out.println("Writers: "+this.lock.writerCount);
		}
	}
	
	public Element head() 
	{
		return (head);
		
	}
	
	public Cursor reader(Element from) 
	{
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
			Element temp = null;
			if(this.current.lock.lockRead("current"))
			{
				temp = this.current;
				this.current.lock.unlockRead("current");
			}
			return temp;
		}
		
		public void previous() 
		{
			Element temp= this.current;
			if(temp.lock.lockRead("previous"))
			{
				Element tempPrevious;
				tempPrevious = temp.previous;
				if(tempPrevious== magicElement)
					tempPrevious = tempPrevious.previous;
				this.current = tempPrevious;
				temp.lock.unlockRead("previous");
			}
		}
		
		public synchronized void next() 
		{
			Element temp = this.current;
			if(temp.lock.lockRead("next"));
			{
				Element tempNext;
				tempNext = temp.next;
				if(tempNext== magicElement)
					tempNext = tempNext.next;
				this.current = tempNext;
				temp.lock.unlockRead("next");
			}

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
			Element oldPrevious;
			//This if block checks if the current element is head. If it is head it will assign the current to magicElement
			if(this.current.lock.lockWrite("In InsertBefore -checking if current can be locked-"))	
			{
				if(this.current == head)
				{
					this.current = magicElement;
					head.lock.unlockWrite("In InsertBefore unlocking head");
				}
				else
					this.current.lock.unlockWrite("In InsertBefore - unlocking current after checking if it is head -");	
			}
			
			oldPrevious = this.current.previous;
			if(oldPrevious.lock.lockWrite("Insert Before oldPrevious"))
			{
				oldPrevious.readersWriters();
				
				if(this.current.lock.lockWrite("Insert Before current"))
				{
					this.current.readersWriters();
					if(oldPrevious == this.current.previous)
					{
							System.out.println("In insertBefore safety condition met");
							Element newNode = new Element(val);	
							oldPrevious.next = newNode;
							newNode.next = this.current;
							newNode.previous = oldPrevious;
							this.current.previous = newNode;
							
							System.out.println("\n"+newNode.value+" has been added to the list");
					}
					else 
							return false;
					
					//This if block checks if the current element is magicElement. If it is magicElement it will assign the 
					//current to head as we had moved it to magicElement before
					if(this.current == magicElement)
					{
							this.current = this.current.next;
							magicElement.lock.unlockWrite("In InsertBefore -magicElement resetting current to head-");
							magicElement.readersWriters();
					}
						else
					{
							this.current.lock.unlockWrite("In InsertBefore - unlocking current after checking if it is head -");
							this.current.readersWriters();
					}
				}
				
				oldPrevious.lock.unlockWrite("Insert Before oldPrevious");
				oldPrevious.readersWriters();
			}	
			return true;
		}
			
		
		public boolean insertAfter(T val) 
		{
			
			if(this.current.lock.lockWrite("insertAfter current"))
			{
				
				Element oldNext = this.current.next;
				if(oldNext.lock.lockWrite("insertAfter oldNext"))
				{
					
					if(oldNext == this.current.next )
					{	
						
						Element newNode = new Element(val);
						
						oldNext.previous = newNode;
						newNode.previous = this.current;
						newNode.next = oldNext;
						this.current.next = newNode;
						System.out.println("\n"+newNode.value+" has been added to the list");
					}
					oldNext.lock.unlockWrite("insertAfter oldNext");
					
				}
				this.current.lock.unlockWrite("insertAfter current");
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
		while (printer.current().next!=head)
		{
			
			System.out.println("Value: " +i+ " "+printer.current.value+ "->");
			printer.current = printer.current().next;
			i++;
			
		}
		System.out.println("Value: "+i+" "+printer.current.value);
	}
}

class ReadWriteLockFine
{
	AtomicInteger isWriterWaiting;
	AtomicInteger readerCount;
	AtomicInteger writerCount;
	final Object lock;
	String name;
	
	public ReadWriteLockFine()
	{
		isWriterWaiting = new AtomicInteger(0);
		readerCount = new AtomicInteger(0);
		writerCount = new AtomicInteger(0);
		lock = new Object();
	}
	
	public boolean lockRead(String name)
	{
		synchronized(this)
		{
			while( isWriterWaiting.get() > 0 || writerCount.get() > 0)
			{
				System.out.println("writer waiting!!" +isWriterWaiting);
				try {
					wait();
				} catch (InterruptedException e) {
				e.printStackTrace();
				}
			}
			readerCount.getAndIncrement();
		}
		return true;
	}
	
	public boolean lockWrite(String name)
	{
		synchronized(this)
		{
			isWriterWaiting.set(1);
			while (readerCount.get() > 0 || writerCount.get() >0)
			{
				try
				{
					wait();
				} 
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
			isWriterWaiting.set(0);
			writerCount.incrementAndGet();
			System.out.println("Writer "+name+" has acquired lock!!");
		}
		return true;
	}

	
	public void unlockRead(String name)
	{
		synchronized(this)
		{
			System.out.println("Reader " +name+" has released lock!!");
			readerCount.decrementAndGet();
			notifyAll();
		}
			
	}
	
	public void unlockWrite(String name)
	{
		synchronized(this)
		{
			writerCount.decrementAndGet();
			notifyAll();
			System.out.println("Writer Thread "+name+" has released lock!!");
		}
	}
	
		
}

