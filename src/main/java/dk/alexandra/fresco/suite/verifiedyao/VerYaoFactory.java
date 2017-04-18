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

/**
 * Verified Yao boolean factory.
 * 
 * This factory builds boolean circuits. The allowed gates are:
 	* AND gates.
 	* OR gates.
 	* INV gates.
 	* XOR gates. 
 * We use a counter here to define the IDs of the wires. This ID will
 * after be fixed to match a valid circuit description to our OCaml
 * evaluator.
 * */

public class VerYaoFactory extends AbstractBinaryFactory implements BasicLogicFactory, OrProtocolFactory {
	
	private int counter;
	private int id;
	
	/**
	 * Builds a new boolean factory, with respect to a
	 * party.
	 * 
	 * @param id
	 * 	Party ID
	 * */
	public VerYaoFactory (int id) {
		this.counter = 0;
		this.id = id;
	}

	@Override
	public CloseBoolProtocol getCloseProtocol(int source, OBool open, SBool closed) {
		return new VerYaoCloseBoolProtocol(source, open,closed);
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
		return res;
	}

	@Override
	public SBool getKnownConstantSBool(boolean b) {	
		
		return new VerYaoSBool(this.counter++, b);
	}
	
	@Override
	public SBool[] getKnownConstantSBools(boolean[] bools) {
		
		VerYaoSBool[] res = new VerYaoSBool[bools.length];
		
		if (this.id == 2) {
			for (int i=0; i<bools.length; i++) {
				res[i] = new VerYaoSBool(this.counter++, bools[i]);
				Integer val = VerYaoConfiguration.assoc.putIfAbsent(this.counter-1, VerYaoConfiguration.in_counter);
				if (val == null) VerYaoConfiguration.in_counter = VerYaoConfiguration.in_counter + 1;
			}
		}
		else {
			for (int i=0; i<bools.length; i++) {
				res[i] = new VerYaoSBool(this.counter++, bools[i]);
			}
		}
		
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
