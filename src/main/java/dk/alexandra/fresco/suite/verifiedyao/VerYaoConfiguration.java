package dk.alexandra.fresco.suite.verifiedyao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import dk.alexandra.fresco.framework.sce.configuration.ProtocolSuiteConfiguration;

/**
 * Configuration of a circuit readable by our OCaml evaluator.
 * 
 * A circuit must have the parameters:
 	* n -> the number of input wires (both parties together);
 	* m -> the number of output wires;
 	* q -> the number of gates;
 	* A -> a list containing all the first incoming wires of each gate;
 	* B -> a list containing all the second incoming wires of each gates;
 	* G -> a list containing the functionality of each gate. 
 *
 * We include an additional list 'gates' that will store every gate of the circuit
 * in the format of VerYaoGate.
 * */

public class VerYaoConfiguration implements ProtocolSuiteConfiguration {
	static int li1;
	static int li2;
	static int n;
	static int m;
	static int q;
		
	static List<Integer> A;
	static List<Integer> B;
	static List<String> G;
		
	static List<VerYaoProtocol> gates;
	
	static StringBuilder i1;
	static StringBuilder i2;
	
	static String [] state1;
	static String [] state2;
		
	static List<VerYaoOBool> output1;
	static List<VerYaoOBool> output2;
	
	static StringBuilder output;
	
	static HashMap<Integer,Integer> assoc;
	
	static int veryaocounter;
	
	static boolean alreadyInputs;
	static boolean alreadyInputs2;
	static int alreadyInputsI;
	
	static List<VerYaoSBool> outWires;
		
	static List<VerYaoSBool> inW1;
	static List<VerYaoSBool> inW2;
	
	public VerYaoConfiguration() {
		VerYaoConfiguration.li1 = 0;
		VerYaoConfiguration.li2 = 0;
		VerYaoConfiguration.n = 0;
		VerYaoConfiguration.m = 0;
		VerYaoConfiguration.q = 0;
		VerYaoConfiguration.A = new ArrayList<Integer>();
		VerYaoConfiguration.B = new ArrayList<Integer>();
		VerYaoConfiguration.G = new ArrayList<String>();
		VerYaoConfiguration.gates = new ArrayList<VerYaoProtocol>();
		VerYaoConfiguration.i1 = new StringBuilder();
		VerYaoConfiguration.i2 = new StringBuilder();
		VerYaoConfiguration.state1 = null;
		VerYaoConfiguration.state2 = null;
		
		inW1 = new ArrayList<VerYaoSBool>();
		inW2 = new ArrayList<VerYaoSBool>();
		outWires = new ArrayList<VerYaoSBool>();
		
		VerYaoConfiguration.output1 = new ArrayList<VerYaoOBool>();
		VerYaoConfiguration.output2 = new ArrayList<VerYaoOBool>();
		
		VerYaoConfiguration.output = new StringBuilder();
		
		VerYaoConfiguration.assoc = new HashMap<Integer,Integer>();
		
		VerYaoConfiguration.veryaocounter = 0;
		
		VerYaoConfiguration.alreadyInputs = false;
		VerYaoConfiguration.alreadyInputs2 = false;
		VerYaoConfiguration.alreadyInputsI = 0;
	}
	
	public static void setLi1 (int li1) {
		VerYaoConfiguration.li1 = li1;
	}
	
	private static String wiresToString(List<Integer> wires) {
		String ret = "";
		
		Iterator<Integer> wiresIterator = wires.iterator();
		while (wiresIterator.hasNext()) {
			Integer next = wiresIterator.next();
			if (wiresIterator.hasNext()) ret = ret + next + " ";
			else ret = ret + next;
		}
		
		return ret;
	}
	
	private static String gatesToString(List<String> gates) {
		String ret = "";
		
		Iterator<String> gatesIterator = gates.iterator();
		while (gatesIterator.hasNext()) {
			String next = gatesIterator.next();
			if (gatesIterator.hasNext()) ret = ret + next + " ";
			else ret = ret + next;
		}
		
		return ret;
	}
	
	public static String circuitToString() {
		String ret;
		
		ret = "i1<"+VerYaoConfiguration.li1+">\n"
				+ "i2<"+VerYaoConfiguration.li2+">\n"
				+ "fn = " + VerYaoConfiguration.n + "; " + VerYaoConfiguration.m + "; " + VerYaoConfiguration.q + "; " 
				+ wiresToString(VerYaoConfiguration.A) + "; "
				+ wiresToString(VerYaoConfiguration.B) + "; "
				+ gatesToString(VerYaoConfiguration.G);
		
		return ret;
	}
}
