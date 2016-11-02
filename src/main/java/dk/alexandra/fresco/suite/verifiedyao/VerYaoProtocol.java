package dk.alexandra.fresco.suite.verifiedyao;

import dk.alexandra.fresco.framework.value.OBool;
import dk.alexandra.fresco.framework.value.SBool;
import dk.alexandra.fresco.lib.helper.HalfCookedNativeProtocol;

/**
 * Abstract class of all the gates readable by our verified Yao's SFE protocol implementation.
 * 
 * NOTE: we will not do any computation, evaluation or whatsoever in the 
 * gates of the circuit. This means that, for example, a boolean AND gate will
 * not do anything (i.e., it will not take two booleans A and B and produce A && B).
 * 
 * This is due to our verified evaluator being written in OCaml and all the evaluation
 * work being done there.
 * 
 * This is almost a Bristol gate. It has one additional parameter 'q' that works as an integer 
 * identifier for the gate.
 * 
 * A VerYaoProtocol description has:
 	* Input arity (number of input wires) 
 	* Output arity (number of output wires)
 	* Input wires (the identifiers of the input wires)
 	* Output wires (the identifiers of the output wires)
 	* The functionality of the gate ('XOR', 'AND', 'INV' ...)
 	* The identifier of the gate
 * */

public abstract class VerYaoProtocol extends HalfCookedNativeProtocol {
	private int inarity;
	private int outarity;
	private VerYaoSBool[] in_w;
	private VerYaoOBool[] in_ow;
	private VerYaoSBool[] out_w;
	private VerYaoOBool[] out_ow;
	private String gate;
	private int q;
	
	/**
	 * Constructor to be used by the VerYaoEvalProtocol protocol
	 * */
	public VerYaoProtocol() {
		
	}
	
	/**
	 * Construct a gate with two secret input wires and one output wire.
	 * 
	 * Useful to construct boolean gates (XOR, AND, OR)
	 * */
	public VerYaoProtocol(SBool inLeft, SBool inRight, SBool out) {
		this.setInarity(2);
		this.setOutarity(1);
		this.setIn_w(new VerYaoSBool[] { (VerYaoSBool)inLeft, (VerYaoSBool)inRight} );
		this.setOut_w(new VerYaoSBool[] { (VerYaoSBool)out });
		this.setGate("");
		this.setQ(0);
	}
	
	/**
	 * "Full" constructor, where the instance is built when everything is already defined.
	 * */
	public VerYaoProtocol(int inarity, int outarity, VerYaoSBool[] in_w, VerYaoSBool[] out_w, String gate, int q) {
		this.setInarity(inarity);
		this.setOutarity(outarity);
		this.setIn_w(in_w);
		this.setOut_w(out_w);
		this.setGate(gate);
		this.setQ(q);
	}

	/**
	 * Constructor to build an "input" gate.
	 * 
	 * This is a gate where an "open" value becomes a "closed" one
	 * only visible to the circuit
	 * */
	public VerYaoProtocol(OBool in, SBool out) {
		this.setInarity(1);
		this.setOutarity(1);
		this.setIn_ow(new VerYaoOBool[] { (VerYaoOBool)in} );
		this.setOut_w(new VerYaoSBool[] { (VerYaoSBool)out });
		this.setGate("");
		this.setQ(0);
	}
	
	/**
	 * Constructor to build an "output" gate.
	 * 
	 * This is a gate where an "open" value becomes a "closed" one
	 * only visible to the circuit
	 * */
	public VerYaoProtocol(SBool in, OBool out) {
		this.setInarity(1);
		this.setOutarity(1);
		this.setIn_w(new VerYaoSBool[] { (VerYaoSBool)in} );
		this.setOut_ow(new VerYaoOBool[] { (VerYaoOBool)out });
		this.setGate("");
		this.setQ(0);
	}
	
	/**
	 * Constructor to build an "INV" gate.
	 * 
	 * This simply transforms an INV gate into a gate with in-arity 2,
	 * such that the input wires are the same.
	 * */
	public VerYaoProtocol(SBool in, SBool out) {
		this.setInarity(1);
		this.setOutarity(1);
		this.setIn_w(new VerYaoSBool[] { (VerYaoSBool)in, (VerYaoSBool)in} );
		this.setOut_w(new VerYaoSBool[] { (VerYaoSBool)out });
		this.setGate("");
		this.setQ(0);
	}
	
	public int getInarity() {
		return inarity;
	}

	public void setInarity(int inarity) {
		this.inarity = inarity;
	}

	public int getOutarity() {
		return outarity;
	}

	public void setOutarity(int outarity) {
		this.outarity = outarity;
	}

	public VerYaoSBool[] getIn_w() {
		return in_w;
	}

	public void setIn_w(VerYaoSBool[] in_w) {
		this.in_w = in_w;
	}

	public VerYaoSBool[] getOut_w() {
		return out_w;
	}

	public void setOut_w(VerYaoSBool[] out_w) {
		this.out_w = out_w;
	}

	public String getGate() {
		return gate;
	}

	public void setGate(String gate) {
		this.gate = gate;
	}

	public int getQ() {
		return q;
	}

	public void setQ(int q) {
		this.q = q;
	}

	public VerYaoOBool[] getIn_ow() {
		return in_ow;
	}

	public void setIn_ow(VerYaoOBool[] in_ow) {
		this.in_ow = in_ow;
	}

	public VerYaoOBool[] getOut_ow() {
		return out_ow;
	}

	public void setOut_ow(VerYaoOBool[] out_ow) {
		this.out_ow = out_ow;
	}
}
