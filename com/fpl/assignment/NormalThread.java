package com.fpl.assignment;

public class NormalThread extends Thread {
    
	CDLListFine<String> cdl;
    int id;
    CDLListFine<String>.Cursor cursor;
    public NormalThread(CDLListFine<String> cdl, int id) {
        this.id = id;
        this.cdl = cdl;
        cursor = cdl.reader(cdl.head());
    }

    @Override
    public void run() {

        int offset = id * 2;
        for(int i = 0; i < offset; i++) {
            cursor.next();
        }
        System.out.println("Trying to insert before");
        cursor.writer().insertBefore("IB - " + id);
        
        System.out.println("Trying to insert after");
        cursor.writer().insertAfter("IA - " + id);
    }

}
