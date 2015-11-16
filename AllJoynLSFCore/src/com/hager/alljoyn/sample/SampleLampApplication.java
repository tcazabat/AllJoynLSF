package com.hager.alljoyn.sample;

import java.io.IOException;

public class SampleLampApplication {

    static {
        System.out.println("System.loadLibrary(alljoyn)");
        System.loadLibrary("alljoyn_java");
        System.out.println("Done.");
    }
	
	public static void main(String[] args) throws IOException {		
		System.out.println("Create of a sample lamp");
		SampleLamp sampleLamp = new SampleLamp();
		sampleLamp.registerDevice();
		
		System.out.println("Press enter to leave...");
		System.in.read();
		
		System.out.println("Unregister the sample lamp");
		sampleLamp.unregisterDevice();
	}

}
