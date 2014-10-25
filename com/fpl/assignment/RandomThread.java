package com.fpl.assignment;


public class RandomThread extends Thread {

	CDLListFine<String> cdl;
	CDLListFine<String>.Cursor cursor;
    public RandomThread(CDLListFine<String> cdl) {
        this.cdl = cdl;
    }
    
    public void run() {
        cursor = cdl.reader(cdl.head());
        for(int i = 0;i < 109;i++) {
            double temp = java.lang.Math.random();
            int rand = (int)(temp*10)%4;


            switch(rand) {
            case 0:
                cursor.next();// Go to the next 
                break;
            case 1:
                cursor.previous();
                break;    
            case 2:
                cursor.writer().insertBefore("Random-Before");
                break;
            case 3:
                cursor.writer().insertBefore("Random-After");
                break;
            default:
                break;
            }
            yield();
        }
    }
}
