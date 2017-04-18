package dk.alexandra.fresco.suite.verifiedyao;

import dk.alexandra.fresco.framework.network.SCENetwork;
import dk.alexandra.fresco.framework.sce.resources.ResourcePool;
import dk.alexandra.fresco.framework.value.SBool;
import dk.alexandra.fresco.framework.value.Value;
import dk.alexandra.fresco.lib.field.bool.XorProtocol;

/**
 * Implementation of a boolean XOR gate.
 * */

public class VerYaoXorProtocol extends VerYaoProtocol implements XorProtocol {
	
	VerYaoSBool in_left, in_right, out;
	
	/**
	 * Returns a (generic) gate with two closed input wires and
	 * one closed output wire.
	 * 
	 * NOTE: this is still not a "XOR" gate, as we only define its
	 * 'func' parameter in the evaluation step.
	 * 
	 * @param inLeft
	 * 		left input wire
	 * @param inRight
	 * 		right input wire
	 * @param out
	 * 		output wire
	 * */
	public VerYaoXorProtocol(SBool in_left, SBool in_right, SBool out) {
		super(in_left, in_right, out);
		this.in_left = (VerYaoSBool)in_left;
		this.in_right = (VerYaoSBool)in_right;
		this.out = (VerYaoSBool)out;
	}

	@Override
	public String toString() {
		return "VerYaoXorProtocol(" +  this.get_in_wires()[0] + "," + this.get_in_wires()[1] + "," + this.get_out_wires()[0] + ")";
	}

	@Override
	public Value[] getInputValues() {
		return new Value[]{this.get_in_wires()[0], this.get_in_wires()[1]};
	}

	@Override
	public Value[] getOutputValues() {
		return new Value[]{this.get_out_wires()[0]};
	}

	@Override
	/**
	 * Evaluation of a XOR gate.
	 * 
	 * No real evaluation is made and only party 2 performs some computation.
	 * The evaluation function starts by re-defining new input wires with
	 * the correct IDs and by defining a new ID to the output wire. After,
	 * the gate definition is completed by its functionality and new wires.
	 * Finally, the gate is attached to the other already evaluated gates.
	 * */
	public EvaluationStatus evaluate(int round, ResourcePool resourcePool,
			SCENetwork network) {
		
		if (resourcePool.getMyId() == 2) {
			
			if (VerYaoConfiguration.q >= 0) VerYaoConfiguration.already_inputs = true;
			
			if (VerYaoConfiguration.assoc.containsKey(this.in_left.getId())) {
				if (VerYaoConfiguration.out_wires.contains(this.in_left)) this.in_left.setId(this.in_left.getId() + VerYaoConfiguration.in_counter2);
				else this.in_left.setId(VerYaoConfiguration.assoc.get(this.in_left.getId()));
			}
			
			if (VerYaoConfiguration.assoc.containsKey(this.in_right.getId())) {
				if (VerYaoConfiguration.out_wires.contains(this.in_right)) this.in_right.setId(this.in_right.getId() + VerYaoConfiguration.in_counter2);
				else this.in_right.setId(VerYaoConfiguration.assoc.get(this.in_right.getId()));
			}
				
			this.out.setId(VerYaoConfiguration.n_wires1 + VerYaoConfiguration.n_wires2 + VerYaoConfiguration.q);
			VerYaoConfiguration.out_wires.add(this.out);
			
			this.setFunc("XOR");
			this.setQ(VerYaoConfiguration.q);
			VerYaoConfiguration.q = VerYaoConfiguration.q + 1;
			VerYaoConfiguration.gates.add(this);
		}
		return EvaluationStatus.IS_DONE;
	}
}
