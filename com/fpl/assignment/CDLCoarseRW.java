package com.fpl.assignment;

import java.util.concurrent.atomic.AtomicInteger;


public class CDLCoarseRW<T> extends CDLList<T> {

	
public Element head;
public static readWriteLock coarseLock;
	
	/**
	 * This is the constructor for the class CDLList
	 * @param v of type T
	 */
	public CDLCoarseRW(T v) 
	{
		super();
		head = new Element(v);
		System.out.println("In the constructor of CDLCoarse");
		head.next = head;
		head.previous = head;
		coarseLock = new readWriteLock();
		
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
			Cursor reader = new Cursor();
			reader.current = from;
			return reader;
		
	}
		
	public class Cursor extends CDLList<T>.Cursor
	{
		private Element current;
		
		public synchronized Element current() 
		{
			Element temp;
				CDLCoarseRW.coarseLock.lockRead();
				temp = this.current;
				CDLCoarseRW.coarseLock.unlockRead();
			return temp;
		}
		
		public synchronized void previous() 
		{
			//synchronized(coarseLock)
			{
				CDLCoarseRW.coarseLock.lockRead();
				this.current = this.current.previous;
				CDLCoarseRW.coarseLock.unlockRead();
			}
			
		}
		
		public synchronized void next() 
		{
			//synchronized(coarseLock)
			{
				CDLCoarseRW.coarseLock.lockRead();
				this.current = this.current.next;
				CDLCoarseRW.coarseLock.unlockRead();
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
	//		synchronized(coarseLock)
			{
				CDLCoarseRW.coarseLock.lockWrite();
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
				CDLCoarseRW.coarseLock.unLockWrite();
				return true;
			}
		}
			
		public synchronized boolean insertAfter(T val) 
		{
		//	synchronized(coarseLock)
			{
				CDLCoarseRW.coarseLock.lockWrite();
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
				CDLCoarseRW.coarseLock.unLockWrite();
				return true;
			}
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

class readWriteLock
{
	static AtomicInteger isWriterWaiting = new AtomicInteger(0);
	static AtomicInteger readerCount = new AtomicInteger(0);
	static AtomicInteger writerCount = new AtomicInteger(0);
	
	public synchronized  void lockRead()
	{
		synchronized(this)
		{
			while( isWriterWaiting.compareAndSet(1, 1) || writerCount.get() > 0)
			{
				System.out.println("writer waiting!!" +isWriterWaiting);
				//System.out.println("writer writing!!" +isLockedWrite);

				try {
					wait();
				} catch (InterruptedException e) {
				//do nothing
				}
			}
			//isLockedRead = true;
			readerCount.getAndIncrement();
			System.out.println("No writer waiting!!");
			System.out.println("Reader "+readerCount+" has acquired lock!!");
		}
	}
	
	public synchronized  void lockWrite()
	{
	synchronized(this)
		{
			while (readerCount.get() > 0 || writerCount.get() >0)
			{
				if(readerCount.get() > 0)
					isWriterWaiting.set(1);;
				try
				{
					wait();
				} 
				catch (InterruptedException e)
				{
					System.out.println("The Writer Thread has woken up");
				}
			}
			writerCount.getAndIncrement();
			isWriterWaiting.set(0);;
			System.out.println("Writer "+writerCount+" has acquired lock!!");
			}
	}

	
	public synchronized void unlockRead()
	{
		synchronized(this)
		{
			System.out.println("Reader " +readerCount+" has released lock!!");
			synchronized(this)
			{
				readerCount.getAndDecrement();
				if(readerCount.get() == 0)
				{
					notifyAll();
				}
			}
		}
	}
	
	public synchronized void unLockWrite()
	{
		synchronized(this)
		{
			isWriterWaiting.set(0);;
			writerCount.getAndDecrement();
			notifyAll();
			System.out.println("Writer Thread "+writerCount+" has released lock!!");
		}
	}
	
		
}

