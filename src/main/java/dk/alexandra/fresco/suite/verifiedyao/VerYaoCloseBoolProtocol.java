package dk.alexandra.fresco.suite.verifiedyao;

import dk.alexandra.fresco.framework.network.SCENetwork;
import dk.alexandra.fresco.framework.sce.resources.ResourcePool;
import dk.alexandra.fresco.framework.value.OBool;
import dk.alexandra.fresco.framework.value.SBool;
import dk.alexandra.fresco.framework.value.Value;
import dk.alexandra.fresco.lib.field.bool.CloseBoolProtocol;

public class VerYaoCloseBoolProtocol extends VerYaoProtocol implements CloseBoolProtocol {

	public VerYaoCloseBoolProtocol(OBool in, SBool out) {
		super(in, out);
	}	
	
	@Override
	public EvaluationStatus evaluate(int round, ResourcePool resourcePool, SCENetwork network) {
		if (resourcePool.getMyId() == 1) { 
			VerYaoConfiguration.li1 = VerYaoConfiguration.li1 + 1;
		}
		else {
			VerYaoConfiguration.li2 = VerYaoConfiguration.li2 + 1;
		}
		
		return EvaluationStatus.IS_DONE;
	}
	
	@Override
	public String toString() {
		return "VerYaoCloseBoolGate(" + this.getIn_w()[0] + "," + this.getOut_w()[0] + ")";
	}

	@Override
	public Value[] getInputValues() {
		return new Value[] {this.getIn_w()[0]};
	}

	@Override
	public Value[] getOutputValues() {
		return new Value[] {this.getOut_w()[0]};
	}

	
}
