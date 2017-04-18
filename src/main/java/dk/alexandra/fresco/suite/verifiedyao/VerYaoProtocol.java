package dk.alexandra.fresco.suite.verifiedyao;

import dk.alexandra.fresco.framework.value.OBool;
import dk.alexandra.fresco.framework.value.SBool;
import dk.alexandra.fresco.lib.helper.HalfCookedNativeProtocol;

/**
 * Abstract class of all the gates readable by our verified Yao's SFE protocol implementation.
 * 
 * NOTE: we will not do any computation, evaluation or whatsoever in the 
 * gates of the circuit. This means that, for example, a boolean AND gate will
 * not do anything (i.e., it will not take two booleans A and B and produce A && B),
 * but will produce a circuit description.
 * 
 * This is due to our verified evaluator being written in OCaml and all the evaluation
 * work being done there.
 * 
 * A VerYaoProtocol description has:
 	* Input arity (number of input wires) 
 	* Output arity (number of output wires)
 	* Input wires (the identifiers of the input wires) 
 	* Output wires (the identifiers of the output wires)
 	* The functionality of the gate ('XOR', 'AND', 'INV' ...)
 	* The identifier of the gate
 *
 * Wires can be "open" (public) or "closed" (private).
 * */

public abstract class VerYaoProtocol extends HalfCookedNativeProtocol {
	private int in_arity;
	private int out_arity;
	private VerYaoSBool[] in_wires;
	private VerYaoOBool[] in_owires;
	private VerYaoSBool[] out_wires;
	private VerYaoOBool[] out_owires;
	private String func;
	private int q;
	
	/**
	 * Builds an totally empty gate.
	 * 
	 * Constructor to be used by the VerYaoEvalProtocol gate.
	 * */
	public VerYaoProtocol() {
		
	}
	
	/**
	 * Builds a gate with two secret input wires and one secrete output wire.
	 * 
	 * Constructor to be used by boolean gates with 2 input wires (XOR, AND, OR).
	 * 
	 * @param in_left
	 * 		left input wire
	 * @param in_right
	 * 		right input wire
	 * @param out
	 * 		output wire
	 * */
	public VerYaoProtocol(SBool in_left, SBool in_right, SBool out) {
		this.set_in_arity(2);
		this.set_out_arity(1);
		this.set_in_wires(new VerYaoSBool[] { (VerYaoSBool)in_left, (VerYaoSBool)in_right} );
		this.set_out_wires(new VerYaoSBool[] { (VerYaoSBool)out });
		this.setFunc("");
		this.setQ(0);
	}

	/**
	 * Builds a gate with one open input wire and one secret
	 * output wire.
	 * 
	 * Constructor to be used when building an input gate. This is a gate where 
	 * an "open" boolean becomes a "closed" one only visible to the circuit.
	 * 
	 * @param in
	 * 		input wire
	 * @param
	 * 		output wire
	 * */
	public VerYaoProtocol(OBool in, SBool out) {
		this.set_in_arity(1);
		this.set_out_arity(1);
		this.set_in_owires(new VerYaoOBool[] { (VerYaoOBool)in} );
		this.set_out_wires(new VerYaoSBool[] { (VerYaoSBool)out });
		this.setFunc("");
		this.setQ(0);
	}
	
	/**
	 * Builds a gate with one secret input wire and one open
	 * output wire.
	 * 
	 * Constructor to be used when building an output gate. This is a gate where 
	 * a "closed" boolean becomes an "open" one, visible to everyone.
	 * 
	 * @param in
	 * 		input wire
	 * @param
	 * 		output wire
	 * */
	public VerYaoProtocol(SBool in, OBool out) {
		this.set_in_arity(1);
		this.set_out_arity(1);
		this.set_in_wires(new VerYaoSBool[] { (VerYaoSBool)in} );
		this.set_out_owires(new VerYaoOBool[] { (VerYaoOBool)out });
		this.setFunc("");
		this.setQ(0);
	}
	
	/**
	 * Builds a gate with one secret input wires and one secrete output wire.
	 * 
	 * Constructor to be used by boolean gates with 1 input wire (INV)
	 * 
	 * @param in
	 * 		input wire
	 * @param out
	 * 		output wire
	 * */
	public VerYaoProtocol(SBool in, SBool out) {
		this.set_in_arity(1);
		this.set_out_arity(1);
		this.set_in_wires(new VerYaoSBool[] { (VerYaoSBool)in, (VerYaoSBool)in} );
		this.set_out_wires(new VerYaoSBool[] { (VerYaoSBool)out });
		this.setFunc("");
		this.setQ(0);
	}

	public String getFunc() {
		return func;
	}

	public void setFunc(String func) {
		this.func = func;
	}

	public int getQ() {
		return q;
	}

	public void setQ(int q) {
		this.q = q;
	}

	public int get_in_arity() {
		return in_arity;
	}

	public void set_in_arity(int in_arity) {
		this.in_arity = in_arity;
	}

	public int get_out_arity() {
		return out_arity;
	}

	public void set_out_arity(int out_arity) {
		this.out_arity = out_arity;
	}

	public VerYaoSBool[] get_in_wires() {
		return in_wires;
	}

	public void set_in_wires(VerYaoSBool[] in_wires) {
		this.in_wires = in_wires;
	}

	public VerYaoOBool[] get_in_owires() {
		return in_owires;
	}

	public void set_in_owires(VerYaoOBool[] in_owires) {
		this.in_owires = in_owires;
	}

	public VerYaoSBool[] get_out_wires() {
		return out_wires;
	}

	public void set_out_wires(VerYaoSBool[] out_wires) {
		this.out_wires = out_wires;
	}

	public VerYaoOBool[] get_out_owires() {
		return out_owires;
	}

	public void set_out_owires(VerYaoOBool[] out_owires) {
		this.out_owires = out_owires;
	}
}
