package dk.alexandra.fresco.suite.verifiedyao;

import dk.alexandra.fresco.framework.network.SCENetwork;
import dk.alexandra.fresco.framework.sce.resources.ResourcePool;
import dk.alexandra.fresco.framework.value.SBool;
import dk.alexandra.fresco.framework.value.Value;
import dk.alexandra.fresco.lib.field.bool.AndProtocol;

/**
 * Implementation of a boolean AND gate.
 * */

public class VerYaoAndProtocol extends VerYaoProtocol implements AndProtocol {
	
	public VerYaoAndProtocol(SBool inLeft, SBool inRight, SBool out) {
		super(inLeft, inRight, out);
	}

	@Override
	public String toString() {
		return "VerYaoAndProtocol(" +  this.getIn_w()[0] + "," + this.getIn_w()[1] + "," + this.getOut_w()[0] + ")";
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
	public EvaluationStatus evaluate(int round, ResourcePool resourcePool, SCENetwork network) {
		if (resourcePool.getMyId() == 2) {
			this.setGate("AND");
			this.setQ(VerYaoConfiguration.q);
			VerYaoConfiguration.q = VerYaoConfiguration.q + 1;
			VerYaoConfiguration.gates.add(this);
		}
		
		return EvaluationStatus.IS_DONE;
	}
}
