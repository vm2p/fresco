package dk.alexandra.fresco.suite.verifiedyao;

import dk.alexandra.fresco.framework.NativeProtocol.EvaluationStatus;
import dk.alexandra.fresco.framework.network.SCENetwork;
import dk.alexandra.fresco.framework.sce.resources.ResourcePool;
import dk.alexandra.fresco.framework.value.SBool;
import dk.alexandra.fresco.framework.value.Value;
import dk.alexandra.fresco.lib.field.bool.OrProtocol;

public class VerYaoOrProtocol extends VerYaoProtocol implements OrProtocol {
	
	VerYaoSBool inLeft, inRight, out;
	
	public VerYaoOrProtocol(SBool inLeft, SBool inRight, SBool out) {
		super(inLeft, inRight, out);
		this.inLeft = (VerYaoSBool)inLeft;
		this.inRight = (VerYaoSBool)inRight;
		this.out = (VerYaoSBool)out;
	}

	@Override
	public String toString() {
		return "VerYaoOrProtocol(" +  this.getIn_w()[0] + "," + this.getIn_w()[1] + "," + this.getOut_w()[0] + ")";
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
		
		VerYaoSBool newInLeft = null;
		VerYaoSBool newInRight = null;
		VerYaoSBool newOut = null;
		
		if (resourcePool.getMyId() == 2) {
			
			if (this.inLeft.getId() == 610) {
				System.out.println(this);
			}
			
			if (VerYaoConfiguration.q == 0) VerYaoConfiguration.alreadyInputs = true;
			
			if (VerYaoConfiguration.assoc.containsKey(this.inLeft.getId())) {
				//VerYaoConfiguration.A.add(VerYaoConfiguration.assoc.get(this.inLeft.getId()));
				newInLeft = new VerYaoSBool(VerYaoConfiguration.assoc.get(this.inLeft.getId()));
			}
			else {
				//VerYaoConfiguration.assoc.put(this.inLeft.getId(), Integer.max(Collections.max(VerYaoConfiguration.A), Integer.max(Collections.max(VerYaoConfiguration.B), Collections.max(VerYaoConfiguration.O))));
				//VerYaoConfiguration.A.add(-1);
				newInLeft = new VerYaoSBool(this.inLeft.getId());
				if (VerYaoConfiguration.assoc_not_used.containsKey(this.inLeft)) {
					VerYaoConfiguration.assoc_not_used.put(newInLeft, VerYaoConfiguration.index++);
				}
			}
			
			if (VerYaoConfiguration.assoc.containsKey(this.inRight.getId())) {
				//VerYaoConfiguration.B.add(VerYaoConfiguration.assoc.get(this.inRight.getId()));
				newInRight = new VerYaoSBool(VerYaoConfiguration.assoc.get(this.inRight.getId()));
			}
			else {
				//VerYaoConfiguration.assoc.put(this.inRight.getId(), Integer.max(Collections.max(VerYaoConfiguration.A), Integer.max(Collections.max(VerYaoConfiguration.B), Collections.max(VerYaoConfiguration.O))));
				//VerYaoConfiguration.B.add(-1);
				newInRight = new VerYaoSBool(this.inRight.getId());
				if (!VerYaoConfiguration.assoc_not_used.containsKey(this.inRight)) {
					VerYaoConfiguration.assoc_not_used.put(newInRight, VerYaoConfiguration.index++);
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
			
			this.setGate("OR");
			this.setQ(VerYaoConfiguration.q);
			this.setIn_w(new VerYaoSBool[] {newInLeft,newInRight});
			this.setOut_w(new VerYaoSBool[] {newOut});
			VerYaoConfiguration.q = VerYaoConfiguration.q + 1;
			VerYaoConfiguration.gates.add(this);
			
			/*val = VerYaoConfiguration.assoc.putIfAbsent(this.inRight.getId(), VerYaoConfiguration.veryaocounter);
			if (val == null) VerYaoConfiguration.veryaocounter = VerYaoConfiguration.veryaocounter + 1;
			
			val = VerYaoConfiguration.assoc.putIfAbsent(this.out.getId(), VerYaoConfiguration.li1 + VerYaoConfiguration.li2 + VerYaoConfiguration.q - 1);
			VerYaoConfiguration.veryaocounter = VerYaoConfiguration.veryaocounter + 1;
			//if (val == null) VerYaoConfiguration.veryaocounter = VerYaoConfiguration.veryaocounter + 1;*/
		}
		
		//this.out.setValue(this.inLeft.getValue() & this.inRight.getValue());
		return EvaluationStatus.IS_DONE;
	}
}
