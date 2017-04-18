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
 * In order to be able to reconstruct a valid circuit description to
 * out OCaml evaluator, we need some auxiliary parameters:
 	*   n_wires1 -> number of input wires of party 1
 	*   n_wires2 -> number of input wires of party 2
 	*   input1 -> input of party 1
 	*   input2 -> input of party 2
 	*   state1 -> state of party 1
 	*   state2 -> state of party 2
 	*   out_wires1 -> list of VerYaoOBool's that will store the output of party 1
 	*   out_wires2 -> list of VerYaoOBool's that will store the output of party 2
 	*   output -> output of the protocol. Should be the same for both parties
 	*   assoc -> association list between wires' IDs. Helps in fixing the IDs of wires
 	*   in_counter -> counter that defines the ID of the input wires
 	*   in_counter2 -> counter that defines the ID of possible input wires in the middle of the circuit
 	*   alreadyInputs -> boolean that checks if the circuit already has the first "row" of inputs defined
 	*   out_wires -> list of the output wires of every gate in the circuit
 	*   input_wires1 -> list of the input wires of party 1
 	*   input_wires2 -> list of the input wires of party 2
 *
 * We include an additional list 'gates' that will store every gate of the circuit
 * in the format of VerYaoGate.
 * */

public class VerYaoConfiguration implements ProtocolSuiteConfiguration {
	static int n_wires1;
	static int n_wires2;
	
	static int n;
	static int m;
	static int q;
		
	static List<Integer> A;
	static List<Integer> B;
	static List<String> G;
		
	static List<VerYaoProtocol> gates;
	
	static StringBuilder input1;
	static StringBuilder input2;
	
	static String [] state1;
	static String [] state2;
		
	static List<VerYaoOBool> out_wires1;
	static List<VerYaoOBool> out_wires2;
	
	static StringBuilder output;
	
	static HashMap<Integer,Integer> assoc;
	
	static int in_counter;
	
	static boolean already_inputs;
	static int in_counter2;
	
	static List<VerYaoSBool> out_wires;
		
	static List<VerYaoSBool> in_wires1;
	static List<VerYaoSBool> in_wires2;
	
	/**
	 * Builds an "empty" circuit description for the OCaml evaluator.
	 * */
	public VerYaoConfiguration() {
		VerYaoConfiguration.n_wires1 = 0;
		VerYaoConfiguration.n_wires2 = 0;
		VerYaoConfiguration.n = 0;
		VerYaoConfiguration.m = 0;
		VerYaoConfiguration.q = 0;
		VerYaoConfiguration.A = new ArrayList<Integer>();
		VerYaoConfiguration.B = new ArrayList<Integer>();
		VerYaoConfiguration.G = new ArrayList<String>();
		VerYaoConfiguration.gates = new ArrayList<VerYaoProtocol>();
		VerYaoConfiguration.input1 = new StringBuilder();
		VerYaoConfiguration.input2 = new StringBuilder();
		VerYaoConfiguration.state1 = null;
		VerYaoConfiguration.state2 = null;
		
		in_wires1 = new ArrayList<VerYaoSBool>();
		in_wires2 = new ArrayList<VerYaoSBool>();
		out_wires = new ArrayList<VerYaoSBool>();
		
		VerYaoConfiguration.out_wires1 = new ArrayList<VerYaoOBool>();
		VerYaoConfiguration.out_wires2 = new ArrayList<VerYaoOBool>();
		
		VerYaoConfiguration.output = new StringBuilder();
		
		VerYaoConfiguration.assoc = new HashMap<Integer,Integer>();
		
		VerYaoConfiguration.in_counter = 0;
		
		VerYaoConfiguration.already_inputs = false;
		VerYaoConfiguration.in_counter2 = 0;
	}
	
	/**
	 * Transforms a list of wires' ID's into a string readable
	 * by the OCaml evaluator.
	 * 
	 * Should be used to map the lists A and B into strings.
	 * 
	 * @param wires
	 * 	List of wires' ID's
	 * 
	 * @return
	 * 	String of the wires' ID's
	 * */
	private static String wires_to_string(List<Integer> wires) {
		String ret = "";
		
		Iterator<Integer> wiresIterator = wires.iterator();
		while (wiresIterator.hasNext()) {
			Integer next = wiresIterator.next();
			if (wiresIterator.hasNext()) ret = ret + next + " ";
			else ret = ret + next;
		}
		
		return ret;
	}
	
	/**
	 * Transforms a list of gates' functionalities into a string readable
	 * by the OCaml evaluator.
	 * 
	 * Should be used to transform the G list into a string.
	 * 
	 * @param gates
	 * 	List of gates
	 * 
	 * @return
	 * 	String of the gates
	 * */
	private static String gates_to_string(List<String> gates) {
		String ret = "";
		
		Iterator<String> gatesIterator = gates.iterator();
		while (gatesIterator.hasNext()) {
			String next = gatesIterator.next();
			if (gatesIterator.hasNext()) ret = ret + next + " ";
			else ret = ret + next;
		}
		
		return ret;
	}
	
	/**
	 * Transforms the overall circuit description into a string in a format
	 * readable by the OCaml evaluator.
	 * 
	 * The output of this function can be given to a stand-alone version of
	 * the OCaml evaluator.
	 * 
	 * The format is the following:
	 	i1<li1>
	 	i2<li2>
	 	fn = n ; m ; q ; A ; B ; G
	 * 
	 * @return
	 * 	Circuit description readable by the OCaml evaluator
	 * */
	public static String circuitToString() {
		String ret;
		
		ret = "i1<"+VerYaoConfiguration.n_wires1+">\n"
				+ "i2<"+VerYaoConfiguration.n_wires2+">\n"
				+ "fn = " + VerYaoConfiguration.n + "; " + VerYaoConfiguration.m + "; " + VerYaoConfiguration.q + "; " 
				+ wires_to_string(VerYaoConfiguration.A) + "; "
				+ wires_to_string(VerYaoConfiguration.B) + "; "
				+ gates_to_string(VerYaoConfiguration.G);
		
		return ret;
	}
}
