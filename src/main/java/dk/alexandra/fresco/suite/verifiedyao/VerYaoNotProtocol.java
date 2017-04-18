package dk.alexandra.fresco.suite.verifiedyao;

import dk.alexandra.fresco.framework.network.SCENetwork;
import dk.alexandra.fresco.framework.sce.resources.ResourcePool;
import dk.alexandra.fresco.framework.value.SBool;
import dk.alexandra.fresco.framework.value.Value;
import dk.alexandra.fresco.lib.field.bool.NotProtocol;

/**
 * Implementation of a boolean INV gate.
 * */

public class VerYaoNotProtocol extends VerYaoProtocol implements NotProtocol {
	
	VerYaoSBool in, out;
	
	/**
	 * Builds a (generic) gate with one closed input wire and
	 * one closed output wire.
	 * 
	 * NOTE: this is still not an "INV" gate, as we only define its
	 * 'func' parameter in the evaluation step.
	 * 
	 * @param in
	 * 		input wire
	 * @param out
	 * 		output wire
	 * */
	public VerYaoNotProtocol(SBool in, SBool out) {
		super(in, out);
		this.in = (VerYaoSBool)in;
		this.out = (VerYaoSBool)out;
	}

	@Override
	public String toString() {
		return "VerYaoNotProtocol(" + this.get_in_wires()[0] + "," + this.get_out_wires()[0] + ")";
	}

	@Override
	public Value[] getInputValues() {
		return new Value[]{this.get_in_wires()[0]};
	}

	@Override
	public Value[] getOutputValues() {
		return new Value[]{this.get_out_wires()[0]};
	}

	@Override
	/**
	 * Evaluation of an INV gate.
	 * 
	 * No real evaluation is made and only party 2 performs some computation.
	 * The evaluation function starts by re-defining a new input wire with
	 * the correct IDs and by defining a new ID to the output wire. After,
	 * the gate definition is completed by its functionality and new wires.
	 * Finally, the gate is attached to the other already evaluated gates.
	 * */
	public EvaluationStatus evaluate(int round, ResourcePool resourcePool,
			SCENetwork network) {
				
		if (resourcePool.getMyId() == 2) {
			
			
			if (VerYaoConfiguration.q >= 0) VerYaoConfiguration.already_inputs = true;
			
			if (VerYaoConfiguration.assoc.containsKey(this.in.getId())) {
				if (VerYaoConfiguration.out_wires.contains(this.in)) this.in.setId(this.in.getId() + VerYaoConfiguration.in_counter2);
				else this.in.setId(VerYaoConfiguration.assoc.get(this.in.getId()));
			}
								
			this.out.setId(VerYaoConfiguration.n_wires1 + VerYaoConfiguration.n_wires2 + VerYaoConfiguration.q);
			VerYaoConfiguration.out_wires.add(this.out);
			
			this.setFunc("INV");
			this.setQ(VerYaoConfiguration.q);
			VerYaoConfiguration.q = VerYaoConfiguration.q + 1;
			VerYaoConfiguration.gates.add(this);
		}
		return EvaluationStatus.IS_DONE;
	}
}
