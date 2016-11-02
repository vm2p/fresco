package dk.alexandra.fresco.suite.verifiedyao;

import org.apache.commons.lang.NotImplementedException;

import dk.alexandra.fresco.framework.value.OBool;
import dk.alexandra.fresco.framework.value.SBool;
import dk.alexandra.fresco.lib.field.bool.AndProtocol;
import dk.alexandra.fresco.lib.field.bool.BasicLogicFactory;
import dk.alexandra.fresco.lib.field.bool.CloseBoolProtocol;
import dk.alexandra.fresco.lib.field.bool.NotProtocol;
import dk.alexandra.fresco.lib.field.bool.OpenBoolProtocol;
import dk.alexandra.fresco.lib.field.bool.OrProtocol;
import dk.alexandra.fresco.lib.field.bool.OrProtocolFactory;
import dk.alexandra.fresco.lib.field.bool.XorProtocol;
import dk.alexandra.fresco.lib.logic.AbstractBinaryFactory;

public class VerYaoFactory extends AbstractBinaryFactory implements BasicLogicFactory, OrProtocolFactory {
	
	private int counter;
	
	public VerYaoFactory () {
		this.counter = 0;
	}
	
	public int getCounter() {
		return this.counter;
	}
	
	public void setCounter(int counter) {
		this.counter = counter;
	}

	@Override
	public CloseBoolProtocol getCloseProtocol(int source, OBool open, SBool closed) {
		return new VerYaoCloseBoolProtocol(open,closed);
	}

	@Override
	public OpenBoolProtocol getOpenProtocol(SBool closed, OBool open) {
		return new VerYaoOpenBoolProtocol(closed,open);
	}

	@Override
	public OpenBoolProtocol getOpenProtocol(int target, SBool closed, OBool open) {
		return new VerYaoOpenBoolProtocol(closed,open);
	}

	@Override
	public SBool getSBool() {
		return new VerYaoSBool(this.counter++);
	}
	
	@Override
	public SBool[] getSBools(int amount) {
		SBool[] res = new VerYaoSBool[amount];
		for (int i=0; i<amount; i++) {
			res[i] = getSBool();
		}
		//VerYaoConfiguration.n = VerYaoConfiguration.n + amount;
		return res;
	}

	@Override
	public SBool getKnownConstantSBool(boolean b) {
		
		/*if (this.id == 1) {
			VerYaoConfiguration.i1 = VerYaoConfiguration.visited1 ? VerYaoConfiguration.i1 : VerYaoConfiguration.i1 + (b ? "1" : "0");
			VerYaoConfiguration.li1 = VerYaoConfiguration.visited1 ? VerYaoConfiguration.li1 : VerYaoConfiguration.li1 + 1;
		}
		else {
			VerYaoConfiguration.i2 = VerYaoConfiguration.visited2 ? VerYaoConfiguration.i2 : VerYaoConfiguration.i2 + (b ? "1" : "0");
			VerYaoConfiguration.li2 = VerYaoConfiguration.visited2 ? VerYaoConfiguration.li2 : VerYaoConfiguration.li2 + 1;
		}*/		
		
		return new VerYaoSBool(this.counter++, b);
	}
	
	@Override
	public SBool[] getKnownConstantSBools(boolean[] bools) {
		
		VerYaoSBool[] res = new VerYaoSBool[bools.length];
		/*System.out.println(this.id);
		
		if (this.id == 1) {
			for (int i=0; i<bools.length; i++) {
				res[i] = new VerYaoSBool(this.counter++, bools[i]);
				VerYaoConfiguration.i1 = VerYaoConfiguration.visited1 ? VerYaoConfiguration.i1 : VerYaoConfiguration.i1 + boolToString(bools[i]);
			}
			VerYaoConfiguration.li1 = VerYaoConfiguration.visited1 ? VerYaoConfiguration.li1 : VerYaoConfiguration.li1 + bools.length;
		}
		else {
			for (int i=0; i<bools.length; i++) {
				res[i] = new VerYaoSBool(this.counter++, bools[i]);
				VerYaoConfiguration.i2 = VerYaoConfiguration.visited2 ? VerYaoConfiguration.i2 : VerYaoConfiguration.i2 + boolToString(bools[i]);
			}
			VerYaoConfiguration.li2 = VerYaoConfiguration.visited2 ? VerYaoConfiguration.li2 : VerYaoConfiguration.li2 + bools.length;
		}*/
		
		for (int i=0; i<bools.length; i++) {
			res[i] = new VerYaoSBool(this.counter++, bools[i]);
			Integer val = VerYaoConfiguration.assoc.putIfAbsent(this.counter-1, VerYaoConfiguration.veryaocounter);
			if (val == null) VerYaoConfiguration.veryaocounter = VerYaoConfiguration.veryaocounter + 1;
		}
		
		/*if (this.id == 1) {
			for (int i=0; i<bools.length; i++) {
				res[i] = new VerYaoSBool(this.counter++, bools[i]);
				VerYaoConfiguration.i1 = VerYaoConfiguration.visited1 ? VerYaoConfiguration.i1 : VerYaoConfiguration.i1 + boolToString(bools[i]);
			}
			VerYaoConfiguration.li1 = VerYaoConfiguration.visited1 ? VerYaoConfiguration.li1 : VerYaoConfiguration.li1 + bools.length;
			VerYaoConfiguration.visited1 = true;
		}
		else {
			for (int i=0; i<bools.length; i++) {
				res[i] = new VerYaoSBool(this.counter++, bools[i]);
				VerYaoConfiguration.i2 = VerYaoConfiguration.visited2 ? VerYaoConfiguration.i2 : VerYaoConfiguration.i2 + boolToString(bools[i]);
			}
			VerYaoConfiguration.li2 = VerYaoConfiguration.visited2 ? VerYaoConfiguration.li2 : VerYaoConfiguration.li2 + bools.length;
			VerYaoConfiguration.visited2 = true;
		}*/
		
		/*if (this.id == 1) {
			for (int i=0; i<bools.length; i++) {
				res[i] = new VerYaoSBool(this.counter++, bools[i]);
				VerYaoConfiguration.i1 = VerYaoConfiguration.visited1 ? VerYaoConfiguration.i1 : VerYaoConfiguration.i1 + boolToString(bools[i]);
			}
			VerYaoConfiguration.li1 = VerYaoConfiguration.visited1 ? VerYaoConfiguration.li1 : VerYaoConfiguration.li1 + bools.length;
			VerYaoConfiguration.visited1 = true;
		}
		else {
			if (this.id == 2) {
				for (int i=0; i<bools.length; i++) {
					res[i] = new VerYaoSBool(this.counter++, bools[i]);
					VerYaoConfiguration.i2 = VerYaoConfiguration.visited2 ? VerYaoConfiguration.i2 : VerYaoConfiguration.i2 + boolToString(bools[i]);
				}
				VerYaoConfiguration.li2 = VerYaoConfiguration.visited2 ? VerYaoConfiguration.li2 : VerYaoConfiguration.li2 + bools.length;
				VerYaoConfiguration.visited2 = true;
			}
		}*/
		return res;
	}

	@Override
	public OBool getOBool() {
		return new VerYaoOBool(this.counter++);
	}

	@Override
	public OBool getKnownConstantOBool(boolean b) {
		return new VerYaoOBool(this.counter++, b);
	}

	@Override
	public AndProtocol getAndProtocol(SBool inLeft, SBool inRight, SBool out) {
		return new VerYaoAndProtocol(inLeft,inRight,out);
	}

	@Override
	public XorProtocol getXorProtocol(SBool inLeft, SBool inRight, SBool out) {
		return new VerYaoXorProtocol(inLeft,inRight,out);
	}

	@Override
	public XorProtocol getXorProtocol(SBool inLeft, OBool inRight, SBool out) {
		throw new NotImplementedException();
	}

	@Override
	public NotProtocol getNotProtocol(SBool input, SBool output) {
		return new VerYaoNotProtocol(input,output);
	}
	
	@Override
	public OrProtocol getOrProtocol(SBool inLeft, SBool inRight, SBool out) {
		return new VerYaoOrProtocol(inLeft,inRight,out);
	}
}
