package com.hager.alljoyn.sample.client;

import java.io.IOException;

public class SampleLampClientApplication {
	
    static {
        System.out.println("System.loadLibrary(alljoyn)");
        System.loadLibrary("alljoyn_java");
        System.out.println("Done.");
    }
	
	public static void main(String[] args) throws IOException {	
		System.out.println("Start about client.");
		AlljoynBusListener listener = new AlljoynBusListener();
		listener.initializeBus();
		
		System.in.read();
		
		listener.closeBus();
		
	}
}
