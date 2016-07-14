package dk.alexandra.fresco.demo;

import dk.alexandra.fresco.framework.sce.SCE;
import dk.alexandra.fresco.framework.sce.SCEFactory;

public class DemoWithMain {

	public static void main(String[] args) {				
		DemoApp application = new DemoApp();
		
		SCE sce = SCEFactory.getSCEFromProperties();
		sce.runApplication(application);
		
		System.out.println("Output of demo application was: " + application.getOutput());
		sce.shutdownSCE();		
	}	
}
