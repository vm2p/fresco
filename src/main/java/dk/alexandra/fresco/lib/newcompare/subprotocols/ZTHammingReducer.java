package dk.alexandra.fresco.lib.newcompare.subprotocols;

import dk.alexandra.fresco.framework.Protocol;
import dk.alexandra.fresco.framework.ProtocolProducer;
import dk.alexandra.fresco.framework.value.OInt;
import dk.alexandra.fresco.framework.value.SInt;
import dk.alexandra.fresco.lib.compare.zerotest.ZeroTestReducerProtocol;
import dk.alexandra.fresco.lib.debug.MarkerProtocolImpl;
import dk.alexandra.fresco.lib.field.integer.BasicNumericFactory;
import dk.alexandra.fresco.lib.helper.AbstractRoundBasedProtocol;
import dk.alexandra.fresco.lib.helper.builder.NumericProtocolBuilder;

/**
 * Protocol use to reduce the the problem of zero testing an input <i>x</i>.
 * <p>
 * We use the following method where <i>l</i> is an assumed bound on the
 * bitlenght of <i>x</i> and <i>s</i> is a security parameter:
 * <ol>
 * <li>Generate a random number <i>r</i> in <i><b>Z</b><sub>2<sup>l+s
 * <sup></sub></i> and a bit representation <i>r<sub>1</sub>, ..., r
 * <sub>l</sub></i> of the first <i>l</i> bits of <i>r</i>
 * <li>Compute and open <i>m = x + r</i>
 * <li>Compute the Hamming distance <i>h</i> between the first <i>l</i> bits of
 * <i>m</i> and <i>r</i>
 * <li>Let <i>h</i> be the result of the protocol
 * </ol>
 * Note that <i>h</i> will be 0 iff <i>x</i> is 0, and that <i>h</i> will be a
 * non-negative value less than or equal <i>l</i>. The complexity of the
 * protocol is determinded by the subprotocols used for step 1 and 3.
 */
public class ZTHammingReducer extends AbstractRoundBasedProtocol implements ZeroTestReducerProtocol {

	private enum State {
		LOAD_RAND, MASK, OPEN, HAMMING, DONE
	}

	private State state = State.LOAD_RAND;
	private ComparisonSubProtocolFactory subFac;
	private BasicNumericFactory bnFac;
	private SInt[] randBits;
	private SInt x, mClosed, reduced, rand;
	private OInt mOpened;
	private int securityParameter;
	private int bitLength;

	/**
	 * Constructs a protocol for reducing the problem of zero testing a value
	 * @param x input - the value to zero test
	 * @param reduced output - the reduced value
	 * @param bitLength an assumed bound on the bit length of <code>x</code>
	 * @param securityParameter a statistical security parameter
	 * @param maskFac a factory supplying and random masks
	 * @param hammFac a factory supplying hamming distance computation
	 * @param bnFac a factory supplying basic numeric functionalities
	 */
	public ZTHammingReducer(SInt x, SInt reduced, int bitLength, int securityParameter, ComparisonSubProtocolFactory subFac, BasicNumericFactory bnFac) {
		super();
		this.x = x;
		this.reduced = reduced;
		this.subFac = subFac;		
		this.bnFac = bnFac;
		this.securityParameter = securityParameter;
		this.bitLength = bitLength;
	}

	@Override
	public ProtocolProducer nextProtocolProducer() {
		NumericProtocolBuilder npb = new NumericProtocolBuilder(bnFac);
		switch (state) {
		case LOAD_RAND:
			rand = npb.getSInt();
			randBits = npb.getSIntArray(bitLength);
			Protocol maskPP = subFac.getZTMaskProtocol(randBits, rand, securityParameter);
			npb.addProtocolProducer(maskPP);
			state = State.MASK;
			break;
		case MASK:
			mClosed = npb.add(x, rand);
			state = State.OPEN;
			break;
		case OPEN:
			mOpened = bnFac.getOInt();
			npb.addProtocolProducer(bnFac.getOpenProtocol(mClosed, mOpened));
			state = State.HAMMING;
			break;
		case HAMMING:
			npb.addProtocolProducer(subFac.getZTHammingDistanceProtocol(randBits, mOpened, reduced));
			state = State.DONE;
			break;
		case DONE:			
			return null;
		default:
			break;
		}
		return npb.getProtocol();
	}

}
