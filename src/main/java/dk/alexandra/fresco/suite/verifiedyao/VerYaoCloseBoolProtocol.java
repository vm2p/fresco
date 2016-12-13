package dk.alexandra.fresco.suite.verifiedyao;

import dk.alexandra.fresco.framework.network.SCENetwork;
import dk.alexandra.fresco.framework.sce.resources.ResourcePool;
import dk.alexandra.fresco.framework.value.OBool;
import dk.alexandra.fresco.framework.value.SBool;
import dk.alexandra.fresco.framework.value.Value;
import dk.alexandra.fresco.lib.field.bool.CloseBoolProtocol;

public class VerYaoCloseBoolProtocol extends VerYaoProtocol implements CloseBoolProtocol {

	private VerYaoOBool in;
	private VerYaoSBool out;
	
	public VerYaoCloseBoolProtocol(int source, OBool in, SBool out) {
		super(in, out);
		this.in = (VerYaoOBool)in;
		this.out = (VerYaoSBool)out;
		
		if (source == 1) {
			if (this.in.isReady()) {
				if (this.in.getValue()) VerYaoConfiguration.i1 = VerYaoConfiguration.i1 + "1";
				else VerYaoConfiguration.i1 = VerYaoConfiguration.i1 + "0";
			}
		}
		
		else {
			if (this.in.isReady()) {
				if (this.in.getValue()) VerYaoConfiguration.i2 = VerYaoConfiguration.i2 + "1";
				else VerYaoConfiguration.i2 = VerYaoConfiguration.i2 + "0";
			}
		}
		
	}	
	
	@Override
	public EvaluationStatus evaluate(int round, ResourcePool resourcePool, SCENetwork network) {
		
		if (resourcePool.getMyId() == 2) {
			
			if (this.in.isReady()) {
				VerYaoConfiguration.inW2.add(this.out);
				VerYaoConfiguration.li2 = VerYaoConfiguration.li2 + 1;
			}
			else {
				VerYaoConfiguration.inW1.add(this.out);
				VerYaoConfiguration.li1 = VerYaoConfiguration.li1 + 1;
			}
			
			if (!VerYaoConfiguration.alreadyInputs) {
				Integer val = VerYaoConfiguration.assoc.putIfAbsent(this.out.getId(), VerYaoConfiguration.veryaocounter);
				if (val == null) VerYaoConfiguration.veryaocounter = VerYaoConfiguration.veryaocounter + 1;
			}
			else {
				VerYaoConfiguration.alreadyInputs2 = true;
				VerYaoConfiguration.alreadyInputsI = VerYaoConfiguration.alreadyInputsI + 1;
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
		return new Value[] {this.getIn_w()[0]};
	}

	@Override
	public Value[] getOutputValues() {
		return new Value[] {this.getOut_w()[0]};
	}

	
}
