package dk.alexandra.fresco.suite.verifiedyao;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;

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
		
		VerYaoSBool newInLeft = null;
		VerYaoSBool newInRight = null;
		VerYaoSBool newOut = null;
		
		if (resourcePool.getMyId() == 2) {
			
			try {
				FileWriter fw = new FileWriter("circuit2.txt", true);
				fw.write(this + "\n");
				fw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if (this.inLeft.getId() == 112 || VerYaoConfiguration.q == 157 || this.inLeft.getId() == 2 || VerYaoConfiguration.q == 197) {
				System.out.println();
			}
			
			if (VerYaoConfiguration.q >= 0) VerYaoConfiguration.alreadyInputs = true;
			if (VerYaoConfiguration.q >= 0 && !VerYaoConfiguration.alreadyInputs) VerYaoConfiguration.alreadyInputs2 = false;
			
			if (VerYaoConfiguration.assoc.containsKey(this.inLeft.getId())) {
				//VerYaoConfiguration.A.add(VerYaoConfiguration.assoc.get(this.inLeft.getId()));
				if (VerYaoConfiguration.outWires.contains(this.inLeft)) this.inLeft.setId(this.inLeft.getId() + VerYaoConfiguration.alreadyInputsI);
				else this.inLeft.setId(VerYaoConfiguration.assoc.get(this.inLeft.getId()));
				//newInLeft = new VerYaoSBool(VerYaoConfiguration.assoc.get(this.inLeft.getId()));
			}
			else {
				//VerYaoConfiguration.assoc.put(this.inLeft.getId(), Integer.max(Collections.max(VerYaoConfiguration.A), Integer.max(Collections.max(VerYaoConfiguration.B), Collections.max(VerYaoConfiguration.O))));
				//VerYaoConfiguration.A.add(-1);
				//newInLeft = new VerYaoSBool(this.inLeft.getId());
				/*if (VerYaoConfiguration.assoc_not_used.containsKey(this.inLeft)) {
					VerYaoConfiguration.assoc_not_used.putIfAbsent(this.inLeft, VerYaoConfiguration.index++);
				}*/
			}
			
			if (VerYaoConfiguration.assoc.containsKey(this.inRight.getId())) {
				//VerYaoConfiguration.B.add(VerYaoConfiguration.assoc.get(this.inRight.getId()));
				this.inRight.setId(VerYaoConfiguration.assoc.get(this.inRight.getId()));
				//newInRight = new VerYaoSBool(VerYaoConfiguration.assoc.get(this.inRight.getId()));
			}
			else {
				//VerYaoConfiguration.assoc.put(this.inRight.getId(), Integer.max(Collections.max(VerYaoConfiguration.A), Integer.max(Collections.max(VerYaoConfiguration.B), Collections.max(VerYaoConfiguration.O))));
				//VerYaoConfiguration.B.add(-1);
				newInRight = this.inRight;
				if (!VerYaoConfiguration.assoc_not_used.containsKey(this.inRight)) {
					VerYaoConfiguration.assoc_not_used.putIfAbsent(this.inRight, VerYaoConfiguration.index++);
				}
			}
			
			if (VerYaoConfiguration.assoc.containsKey(this.out.getId())) {
				VerYaoConfiguration.O.add(VerYaoConfiguration.assoc.get(this.out.getId())); //nunca pode bater aqui!!!!
			}
			else {
				if (this.out.getId() == 194) {
					System.out.println();
				}
				//VerYaoConfiguration.assoc.put(this.out.getId(), VerYaoConfiguration.li1 + VerYaoConfiguration.li2 + VerYaoConfiguration.q);
				//VerYaoConfiguration.O.add(VerYaoConfiguration.assoc.get(this.out.getId()));
				
				this.out.setId(VerYaoConfiguration.li1 + VerYaoConfiguration.li2 + VerYaoConfiguration.q);
				//newOut = new VerYaoSBool(VerYaoConfiguration.li1 + VerYaoConfiguration.li2 + VerYaoConfiguration.q);
				if (!VerYaoConfiguration.alreadyInputs2) VerYaoConfiguration.outWires.add(this.out);
			}
			
			this.setGate("XOR");
			this.setQ(VerYaoConfiguration.q);
			//this.setIn_w(new VerYaoSBool[] {newInLeft,newInRight});
			//this.setOut_w(new VerYaoSBool[] {newOut});
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
