package dk.alexandra.fresco.suite.verifiedyao;

import dk.alexandra.fresco.framework.network.SCENetwork;
import dk.alexandra.fresco.framework.sce.resources.ResourcePool;
import dk.alexandra.fresco.framework.value.OBool;
import dk.alexandra.fresco.framework.value.SBool;
import dk.alexandra.fresco.framework.value.Value;
import dk.alexandra.fresco.lib.field.bool.CloseBoolProtocol;

/**
 * Implementation of an "input" gate.
 * */

public class VerYaoCloseBoolProtocol extends VerYaoProtocol implements CloseBoolProtocol {

	private VerYaoOBool in;
	private VerYaoSBool out;
	
	/**
	 * Builds a (generic) gate with one opened input wire and
	 * one closed output wire.
	 * 
	 * @param id
	 * 		party id
	 * @param in
	 * 		input wire
	 * @param out
	 * 		output wire
	 * */
	public VerYaoCloseBoolProtocol(int id, OBool in, SBool out) {
		super(in, out);
		this.in = (VerYaoOBool)in;
		this.out = (VerYaoSBool)out;
		
		if (id == 1) {
			if (this.in.isReady()) {
				if (this.in.getValue()) VerYaoConfiguration.input1.append(1);
				else VerYaoConfiguration.input1.append(0);
			}
		}
		
		else {
			if (this.in.isReady()) {
				if (this.in.getValue()) VerYaoConfiguration.input2.append(1);
				else VerYaoConfiguration.input2.append(0);
			}
		}
		
	}	
	
	@Override
	/**
	 * Evaluation of an "input" gate.
	 * 
	 * The evaluation function defines the number of inputs of each party and
	 * keeps track of the reference of each input wire. These information will
	 * then be used to build a valid circuit description for the OCaml evaluator.
	 * */
	public EvaluationStatus evaluate(int round, ResourcePool resourcePool, SCENetwork network) {
		
		if (resourcePool.getMyId() == 2) {
			
			if (this.in.isReady()) {
				VerYaoConfiguration.in_wires2.add(this.out);
				VerYaoConfiguration.n_wires2 = VerYaoConfiguration.n_wires2 + 1;
			}
			else {
				VerYaoConfiguration.in_wires1.add(this.out);
				VerYaoConfiguration.n_wires1 = VerYaoConfiguration.n_wires1 + 1;
			}
			
			if (!VerYaoConfiguration.already_inputs) {
				Integer val = VerYaoConfiguration.assoc.putIfAbsent(this.out.getId(), VerYaoConfiguration.in_counter);
				if (val == null) VerYaoConfiguration.in_counter = VerYaoConfiguration.in_counter + 1;
			}
			else {
				VerYaoConfiguration.in_counter2 = VerYaoConfiguration.in_counter2 + 1;
			}
		}
				
		return EvaluationStatus.IS_DONE;
	}
	
	@Override
	public String toString() {
		return "VerYaoCloseBoolGate(" + this.in + "," + this.out + ")";
	}

	@Override
	public Value[] getInputValues() {
		return new Value[] {this.get_in_wires()[0]};
	}

	@Override
	public Value[] getOutputValues() {
		return new Value[] {this.get_out_wires()[0]};
	}

	
}
