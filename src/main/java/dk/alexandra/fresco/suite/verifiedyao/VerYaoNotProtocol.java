package dk.alexandra.fresco.suite.verifiedyao;

import dk.alexandra.fresco.framework.network.SCENetwork;
import dk.alexandra.fresco.framework.sce.resources.ResourcePool;
import dk.alexandra.fresco.framework.value.SBool;
import dk.alexandra.fresco.framework.value.Value;
import dk.alexandra.fresco.lib.field.bool.NotProtocol;

public class VerYaoNotProtocol extends VerYaoProtocol implements NotProtocol {
	
	public VerYaoNotProtocol(SBool in, SBool out) {
		super(in, out);
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
			this.setGate("INV");
			this.setQ(VerYaoConfiguration.q);
			VerYaoConfiguration.q = VerYaoConfiguration.q + 1;
			VerYaoConfiguration.gates.add(this);
		}
		//this.out.setValue(this.in.getValue() & this.in.getValue());
		return EvaluationStatus.IS_DONE;
	}
}
