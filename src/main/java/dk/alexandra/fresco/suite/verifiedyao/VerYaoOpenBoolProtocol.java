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
		
		//VerYaoConfiguration.output.add(this.out);
		
		System.out.println("contructor = " + out.hashCode());
	}
	
	public VerYaoOpenBoolProtocol(SBool in, OBool out, int target) {
		super(in, out);
		this.in = (VerYaoSBool) in;
		this.out = (VerYaoOBool) out;
	}	
	
	@Override
	public EvaluationStatus evaluate(int round, ResourcePool resourcePool,
			SCENetwork network) {
		
		VerYaoSBool newIn = null;
		
		if (resourcePool.getMyId() == 1) {
			VerYaoConfiguration.output1.add(this.out);
		}
		if (resourcePool.getMyId() == 2) {
			
			if (VerYaoConfiguration.assoc.containsKey(this.in.getId())) {
				//VerYaoConfiguration.A.add(VerYaoConfiguration.assoc.get(this.inLeft.getId()));
				newIn = new VerYaoSBool(VerYaoConfiguration.assoc.get(this.in.getId()));
			}
			else {
				//VerYaoConfiguration.assoc.put(this.inLeft.getId(), Integer.max(Collections.max(VerYaoConfiguration.A), Integer.max(Collections.max(VerYaoConfiguration.B), Collections.max(VerYaoConfiguration.O))));
				//VerYaoConfiguration.A.add(-1);
				newIn = new VerYaoSBool(this.in.getId());
				if (VerYaoConfiguration.assoc_not_used.containsKey(this.in)) {
					VerYaoConfiguration.assoc_not_used.put(newIn, VerYaoConfiguration.index++);
				}
			}
			
			VerYaoConfiguration.m = VerYaoConfiguration.m + 1;
			VerYaoConfiguration.q = VerYaoConfiguration.q + 1;
			this.setGate("OUT");
			this.setIn_w(new VerYaoSBool[] {newIn});
			VerYaoConfiguration.gates.add(this);
			VerYaoConfiguration.outGates.add(this);
			System.out.println("metodo = " + this.out.hashCode());
			System.out.println("metodo = " + this.in.hashCode());
			VerYaoConfiguration.oBools.add(this.out);
			
			VerYaoConfiguration.output2.add(this.out);
		}
				
		return EvaluationStatus.IS_DONE;
	}	
	
	@Override
	public String toString() {
		return "VerYaoOpenBoolGate(" + this.getIn_w()[0] + "," + this.getOut_w()[0] + ")";
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
