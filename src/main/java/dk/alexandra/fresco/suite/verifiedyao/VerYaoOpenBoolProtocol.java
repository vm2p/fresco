package dk.alexandra.fresco.suite.verifiedyao;

import dk.alexandra.fresco.framework.network.SCENetwork;
import dk.alexandra.fresco.framework.sce.resources.ResourcePool;
import dk.alexandra.fresco.framework.value.OBool;
import dk.alexandra.fresco.framework.value.SBool;
import dk.alexandra.fresco.framework.value.Value;
import dk.alexandra.fresco.lib.field.bool.OpenBoolProtocol;

/**
 * Implementation of an "output" gate.
 * */

public class VerYaoOpenBoolProtocol extends VerYaoProtocol implements OpenBoolProtocol {

	public VerYaoSBool in;
	public VerYaoOBool out;
		
	/**
	 * Builds a (generic) gate with one opened input wire and
	 * one closed output wire.
	 * 
	 * @param in
	 * 		input wire
	 * @param out
	 * 		output wire
	 * */
	public VerYaoOpenBoolProtocol(SBool in, OBool out) {
		super(in, out);
		this.in = (VerYaoSBool) in;
		this.out = (VerYaoOBool) out;		
	}
	
	@Override
	/**
	 * Evaluates an "output" gate.
	 * 
	 * Party 1 simply stores the output wire in the out_wires1 list.
	 * Party 2 starts by defining a new ID for the input wire of
	 * the gate and then will increase the number of outputs 'm',
	 * the number of gates 'q' and re-define the parameters of the 
	 * gate. It will finish by attaching the gate to the already 
	 * evaluated gates and by storing the output wire in out_wires2
	 * list.
	 * */
	public EvaluationStatus evaluate(int round, ResourcePool resourcePool,
			SCENetwork network) {
				
		if (resourcePool.getMyId() == 1) {
			VerYaoConfiguration.out_wires1.add(this.out);
		}
		if (resourcePool.getMyId() == 2) {
			
			VerYaoConfiguration.m = VerYaoConfiguration.m + 1;
			VerYaoConfiguration.q = VerYaoConfiguration.q + 1;
			this.setFunc("AND");
			this.set_in_wires(new VerYaoSBool[] {this.in});
			VerYaoConfiguration.gates.add(this);
			
			VerYaoConfiguration.out_wires2.add(this.out);
		}
		return EvaluationStatus.IS_DONE;
	}	
	
	@Override
	public String toString() {
		return "VerYaoOpenBoolGate(" + this.get_in_wires()[0] + "," + this.get_out_owires()[0] + ")";
	}

	@Override
	public Value[] getInputValues() {
		return new Value[] {this.get_in_wires()[0]};
	}

	@Override
	public Value[] getOutputValues() {
		return new Value[] {this.get_out_owires()[0]};
	}

	
}
