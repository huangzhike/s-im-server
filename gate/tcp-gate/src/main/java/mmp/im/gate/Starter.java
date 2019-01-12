package mmp.im.gate;


import mmp.im.gate.ack.ResendMessageThread;

public class Starter {

    public static void main(String[] args) {


        Thread t = new Thread(new ResendMessageThread(), "ack.timeout.scanner");
        t.setDaemon(true);
        t.start();


    }
}
