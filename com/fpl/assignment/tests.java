package com.fpl.assignment;

import java.util.List;
import java.util.ArrayList;

import com.fpl.assignment.CDLListFine.Cursor;
import com.fpl.assignment.CDLListFine.Element;




public class tests {


    public void test1(){
//       USE THIS FOR COARSELIST AND FINELIST WITH MODIFICATIONS
    	CDLListFine<String> list = new CDLListFine<String>("hi");
        Element head = list.head();
        Cursor c = list.reader(list.head());
     for(int i = 74; i >= 65; i--) {
       	char val = (char) i;
      c.writer().insertAfter("" + val);
    c.writer().insertBefore("" + val);
        }
       c.writer().insertBefore("" + "abc");
       c.writer().insertAfter("" + "xyz");

     System.out.println("*************************Trying to add Normal Thread************************");
        List<Thread> threadList = new ArrayList<Thread>();
     for (int i = 0; i < 109; i++) {
          NormalThread nt = new NormalThread(list, i);
            threadList.add(nt);
       }
     
     System.out.println("*************************Trying to add Random Thread************************");
RandomThread rt = new RandomThread(list);
threadList.add(rt);
	
        try {
            for(Thread t : threadList){
            	t.start();
            }
            for (Thread t : threadList) {
            	t.join();
            }
        } catch(InterruptedException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
      }
//    YOU MAY WANT TO INCLUDE A PRINT METHOD TO VIEW ALL THE ELEMENTS
        list.printList();
        
  }
    
    
    
}



