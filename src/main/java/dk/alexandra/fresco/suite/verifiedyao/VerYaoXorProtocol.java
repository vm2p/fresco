package dk.alexandra.fresco.suite.verifiedyao;

import dk.alexandra.fresco.framework.network.SCENetwork;
import dk.alexandra.fresco.framework.sce.resources.ResourcePool;
import dk.alexandra.fresco.framework.value.SBool;
import dk.alexandra.fresco.framework.value.Value;
import dk.alexandra.fresco.lib.field.bool.XorProtocol;

public class VerYaoXorProtocol extends VerYaoProtocol implements XorProtocol {
	
	VerYaoSBool inLeft, inRight, out;
	
	public VerYaoXorProtocol(SBool inLeft, SBool inRight, SBool out) {
		super(inLeft, inRight, out);
		this.inLeft = (VerYaoSBool)inLeft;
		this.inRight = (VerYaoSBool)inRight;
		this.out = (VerYaoSBool)out;
	}

	@Override
	public String toString() {
		return "VerYaoXorProtocol(" +  this.getIn_w()[0] + "," + this.getIn_w()[1] + "," + this.getOut_w()[0] + ")";
	}

	@Override
	public Value[] getInputValues() {
		return new Value[]{this.getIn_w()[0], this.getIn_w()[1]};
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
			
			if (VerYaoConfiguration.assoc.containsKey(this.inLeft.getId())) {
				if (VerYaoConfiguration.outWires.contains(this.inLeft)) this.inLeft.setId(this.inLeft.getId() + VerYaoConfiguration.alreadyInputsI);
				else this.inLeft.setId(VerYaoConfiguration.assoc.get(this.inLeft.getId()));
			}
			
			if (VerYaoConfiguration.assoc.containsKey(this.inRight.getId())) {
				if (VerYaoConfiguration.outWires.contains(this.inRight)) this.inRight.setId(this.inRight.getId() + VerYaoConfiguration.alreadyInputsI);
				else this.inRight.setId(VerYaoConfiguration.assoc.get(this.inRight.getId()));
			}
				
			this.out.setId(VerYaoConfiguration.li1 + VerYaoConfiguration.li2 + VerYaoConfiguration.q);
			if (!VerYaoConfiguration.alreadyInputs2) VerYaoConfiguration.outWires.add(this.out);
			
			this.setGate("XOR");
			this.setQ(VerYaoConfiguration.q);
			VerYaoConfiguration.q = VerYaoConfiguration.q + 1;
			VerYaoConfiguration.gates.add(this);
		}
		
		return EvaluationStatus.IS_DONE;
	}
}
