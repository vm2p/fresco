package dk.alexandra.fresco.suite.verifiedyao;

import dk.alexandra.fresco.framework.network.SCENetwork;
import dk.alexandra.fresco.framework.sce.resources.ResourcePool;
import dk.alexandra.fresco.framework.value.SBool;
import dk.alexandra.fresco.framework.value.Value;
import dk.alexandra.fresco.lib.field.bool.NotProtocol;

public class VerYaoNotProtocol extends VerYaoProtocol implements NotProtocol {
	
	VerYaoSBool in, out;
	
	public VerYaoNotProtocol(SBool in, SBool out) {
		super(in, out);
		this.in = (VerYaoSBool)in;
		this.out = (VerYaoSBool)out;
	}

	@Override
	public String toString() {
		return "VerYaoNotProtocol(" + this.getIn_w()[0] + "," + this.getOut_w()[0] + ")";
	}

	@Override
	public Value[] getInputValues() {
		return new Value[]{this.getIn_w()[0]};
	}

	@Override
	public Value[] getOutputValues() {
		return new Value[]{this.getOut_w()[0]};
	}

	@Override
	public EvaluationStatus evaluate(int round, ResourcePool resourcePool,
			SCENetwork network) {
				
		if (resourcePool.getMyId() == 2) {
			
			
			if (VerYaoConfiguration.q >= 0) VerYaoConfiguration.alreadyInputs = true;
			if (VerYaoConfiguration.q >= 0 && !VerYaoConfiguration.alreadyInputs) VerYaoConfiguration.alreadyInputs2 = false;
			
			if (VerYaoConfiguration.assoc.containsKey(this.in.getId())) {
				if (VerYaoConfiguration.outWires.contains(this.in)) this.in.setId(this.in.getId() + VerYaoConfiguration.alreadyInputsI);
				else this.in.setId(VerYaoConfiguration.assoc.get(this.in.getId()));
			}
								
			this.out.setId(VerYaoConfiguration.li1 + VerYaoConfiguration.li2 + VerYaoConfiguration.q);
			if (!VerYaoConfiguration.alreadyInputs2) VerYaoConfiguration.outWires.add(this.out);
			
			this.setGate("INV");
			this.setQ(VerYaoConfiguration.q);
			VerYaoConfiguration.q = VerYaoConfiguration.q + 1;
			VerYaoConfiguration.gates.add(this);
		}

		return EvaluationStatus.IS_DONE;
	}
}
