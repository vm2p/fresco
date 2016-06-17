package dk.alexandra.fresco.lib.newcompare;

import dk.alexandra.fresco.framework.ProtocolProducer;
import dk.alexandra.fresco.framework.value.SInt;
import dk.alexandra.fresco.lib.compare.zerotest.ZeroTestProtocol;
import dk.alexandra.fresco.lib.field.integer.BasicNumericFactory;
import dk.alexandra.fresco.lib.helper.AbstractRoundBasedProtocol;
import dk.alexandra.fresco.lib.helper.sequential.SequentialProtocolProducer;
import dk.alexandra.fresco.lib.lp.OpenAndPrintProtocol;
import dk.alexandra.fresco.lib.newcompare.subprotocols.ComparisonSubProtocolFactory;

/**
 * A protocol testing a value <i>x</i> of assumed maximum bit length <i>l</i> for zero.
 * <p>
 * We use the following method:
 * <ol>
 * <li> First we reduce the problem by computing a value <i>r</i> so that <i> -1 {@literal <} r {@literal <} l</i> and <i> r = 0</i> iff <i>x = 0</i>
 * <li> We then test if <i>r = 0</i> using a method that works well for small non-negative values
 * <ol>
 */
public class NewZeroTestProtocol extends AbstractRoundBasedProtocol implements ZeroTestProtocol {

	private enum State {
		REDUCE, SOLVE_REDUCED, DONE
	}
	private SInt x, result;
	private ComparisonSubProtocolFactory subFactory;
	private State state = State.REDUCE;
	private int securityParameter, bitLength;
	private BasicNumericFactory bnFac;
	
	/**
	 * Constructs a protocol testing a value for zero
	 * @param x input - the value to test
	 * @param result output - where to store the result
	 * @param bitLength an assumed max bit length of <code>x</code>
	 * @param securityParameter a statistical security parameter 
	 * @param subFactory a factory for the involved subprotocols
	 */
	public NewZeroTestProtocol(SInt x, SInt result, int bitLength, int securityParameter,
			ComparisonSubProtocolFactory subFactory, BasicNumericFactory bnFac) {
		super();
		this.x = x;
		this.result = result;
		this.bitLength = bitLength;
		this.securityParameter = securityParameter;
		this.subFactory = subFactory;
		this.bnFac = bnFac;
	}

	@Override
	public ProtocolProducer nextProtocolProducer() {
		ProtocolProducer pp = null;
		switch (state) {
		case REDUCE:
			pp = subFactory.getZTReducerProtocol(x, result, bitLength, securityParameter);
			state = State.SOLVE_REDUCED;
			break;
		case SOLVE_REDUCED:
			pp = subFactory.getZTBruteForceProtocol(result, result, bitLength);
			state = State.DONE;
			break;
		case DONE:
			return null;
		default:
			break;
		}
		return pp;
	}

}
