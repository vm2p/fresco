package dk.alexandra.fresco.suite.verifiedyao;

import dk.alexandra.fresco.framework.network.SCENetwork;
import dk.alexandra.fresco.framework.sce.resources.ResourcePool;
import dk.alexandra.fresco.framework.value.OBool;
import dk.alexandra.fresco.framework.value.SBool;
import dk.alexandra.fresco.framework.value.Value;
import dk.alexandra.fresco.lib.field.bool.OpenBoolProtocol;

public class VerYaoOpenBoolProtocol extends VerYaoProtocol implements OpenBoolProtocol {

	public VerYaoSBool in;
	public VerYaoOBool out;
		
	public VerYaoOpenBoolProtocol(SBool in, OBool out) {
		super(in, out);
		this.in = (VerYaoSBool) in;
		this.out = (VerYaoOBool) out;		
	}
	
	@Override
	public EvaluationStatus evaluate(int round, ResourcePool resourcePool,
			SCENetwork network) {
				
		if (resourcePool.getMyId() == 1) {
			VerYaoConfiguration.output1.add(this.out);
		}
		if (resourcePool.getMyId() == 2) {
			
			VerYaoConfiguration.m = VerYaoConfiguration.m + 1;
			VerYaoConfiguration.q = VerYaoConfiguration.q + 1;
			this.setGate("AND");
			this.setIn_w(new VerYaoSBool[] {this.in});
			VerYaoConfiguration.gates.add(this);
			
			VerYaoConfiguration.output2.add(this.out);
		}
				
		return EvaluationStatus.IS_DONE;
	}	
	
	@Override
	public String toString() {
		return "VerYaoOpenBoolGate(" + this.getIn_w()[0] + "," + this.getOut_ow()[0] + ")";
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
