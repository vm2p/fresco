package dk.alexandra.fresco.lib.newcompare;

import dk.alexandra.fresco.framework.ProtocolProducer;
import dk.alexandra.fresco.framework.value.SInt;
import dk.alexandra.fresco.lib.compare.eq.EqualityProtocol;
import dk.alexandra.fresco.lib.field.integer.BasicNumericFactory;
import dk.alexandra.fresco.lib.helper.AbstractRoundBasedProtocol;

public class NewEquality extends AbstractRoundBasedProtocol implements EqualityProtocol {

	private enum State {
		SUB, ZERO_TEST, DONE
	}
	private SInt x, y, tmp, result;
	private BasicNumericFactory bnFac;
	private NewCompareFactory compFac;
	private State state = State.SUB;
	private int secParam, bitLength;
	
	@Override
	public ProtocolProducer nextProtocolProducer() {
		ProtocolProducer pp = null;
		switch (state) {
		case SUB:
			pp = bnFac.getSubtractProtocol(x, y, result);
			break;
		case ZERO_TEST:
			pp = compFac.getZeroTestProtocol(tmp, result, bitLength, secParam);
			break;
		case DONE:
			return null;
		default:
			break;
		}
		return pp;
	}

}
