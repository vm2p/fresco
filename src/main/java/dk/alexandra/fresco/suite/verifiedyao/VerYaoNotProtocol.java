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
		
		VerYaoSBool newIn = null, newOut = null;
		
		if (resourcePool.getMyId() == 2) {
			
			if (VerYaoConfiguration.q == 0) VerYaoConfiguration.alreadyInputs = true;
			
			if (VerYaoConfiguration.assoc.containsKey(this.in.getId())) {
				//VerYaoConfiguration.A.add(VerYaoConfiguration.assoc.get(this.in.getId()));
				//VerYaoConfiguration.B.add(VerYaoConfiguration.assoc.get(this.in.getId()));
				newIn = new VerYaoSBool(VerYaoConfiguration.assoc.get(this.in.getId()));
			}
			else {
				//VerYaoConfiguration.assoc.put(this.inLeft.getId(), Integer.max(Collections.max(VerYaoConfiguration.A), Integer.max(Collections.max(VerYaoConfiguration.B), Collections.max(VerYaoConfiguration.O))));
				//VerYaoConfiguration.A.add(-1);
				//VerYaoConfiguration.B.add(-1);
				newIn = new VerYaoSBool(this.in.getId());
				if (VerYaoConfiguration.assoc_not_used.containsKey(this.in)) {
					VerYaoConfiguration.assoc_not_used.put(newIn, VerYaoConfiguration.index++);
				}
			}
			
			if (VerYaoConfiguration.assoc.containsKey(this.out.getId())) {
				VerYaoConfiguration.O.add(VerYaoConfiguration.assoc.get(this.out.getId())); //nunca pode bater aqui!!!!
			}
			else {
				VerYaoConfiguration.assoc.put(this.out.getId(), VerYaoConfiguration.li1 + VerYaoConfiguration.li2 + VerYaoConfiguration.q);
				//VerYaoConfiguration.O.add(VerYaoConfiguration.assoc.get(this.out.getId()));
				newOut = new VerYaoSBool(VerYaoConfiguration.li1 + VerYaoConfiguration.li2 + VerYaoConfiguration.q);
			}
			
			this.setGate("INV");
			this.setQ(VerYaoConfiguration.q);
			this.setIn_w(new VerYaoSBool[] {newIn});
			this.setOut_w(new VerYaoSBool[] {newOut});
			VerYaoConfiguration.q = VerYaoConfiguration.q + 1;
			VerYaoConfiguration.gates.add(this);
		}
		//this.out.setValue(this.in.getValue() & this.in.getValue());
		return EvaluationStatus.IS_DONE;
	}
}
