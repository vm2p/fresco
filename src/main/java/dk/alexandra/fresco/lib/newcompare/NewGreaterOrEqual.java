package dk.alexandra.fresco.lib.newcompare;

import java.math.BigInteger;
import java.util.Arrays;

import dk.alexandra.fresco.framework.ProtocolProducer;
import dk.alexandra.fresco.framework.value.OInt;
import dk.alexandra.fresco.framework.value.SInt;
import dk.alexandra.fresco.lib.compare.MiscOIntGenerators;
import dk.alexandra.fresco.lib.compare.gt.GreaterThanProtocol;
import dk.alexandra.fresco.lib.field.integer.BasicNumericFactory;
import dk.alexandra.fresco.lib.helper.AbstractRoundBasedProtocol;
import dk.alexandra.fresco.lib.helper.builder.NumericProtocolBuilder;
import dk.alexandra.fresco.lib.math.integer.inv.LocalInversionFactory;
import dk.alexandra.fresco.lib.newcompare.subprotocols.ComparisonSubProtocolFactory;

/**
 * Computes <i>x &ge; y</i> for numbers of assumed bit length <i>l</i>. The high-level idea 
 * of this approach is basically to do binary search to find the first bit on which the 
 * two inputs are different.
 * <ol>
 * 
 * <li> Find a random number <i>[r]</i> of bit length <i>l + k</i> and <i>[r<sub>1</sub>], ..., [r<sub>l</sub>]</i>, the <i>l</i> first bits of <i>r</i>. 
 * 
 * <li> Compute <i>[t<sub>bot</sub>]</i> and 
 * <i>[t<sub>top</sub>]</i> to be the numbers represented by 
 * <i>[r<sub>1 </sub>], ..., [r<sub>l/2</sub>]</i> and <i>[r<sub>l/2 + 1 </sub>],
 *  ..., [r<sub>l </sub>]</i> respectively.
 * 
 * <li> Compute <i>[z] = [x] - [y] + 2<sup> l</sup></i>. Note that <i>z</i> will 
 * have the <i>l</i>-th bit set to <i>1</i> if and only if  <i>x &ge; y</i>.
 * 
 * <li> Compute and open <i>m = [z] + [r]</i>.  
 * 
 * <li> Compute <i>m' = m mod 2<sup> l<sup></i>. From <i>m'</i> compute <i>m<sub>bot</sub></i> and 
 * <i>m<sub>top</sub></i> as the numbers represented by the first and last 
 * <i>l/2</i> bits of <i>m'</i> respectively.
 * 
 * <li> Compute <i>[z<sub>top</sub>] = m<sub>top</sub> - [t<sub>top</sub>]</i>.
 *    
 * </ol>
 */
public class NewGreaterOrEqual extends AbstractRoundBasedProtocol implements GreaterThanProtocol{
	
	private enum State {
		LOAD_MASK, BUILD_TS, TEST_TOP, SELECT, RECURSE, RESULT, DONE
	}
	private State state = State.LOAD_MASK;
	private BasicNumericFactory bnFac;
	private ComparisonSubProtocolFactory subFac;
	private NewCompareFactory compFac;
	private LocalInversionFactory invFac;
	private SInt[] tBits;
	private int bitLength, secParam, topBits, botBits;
	private SInt x, y;
	private int securityParameter;
	private MiscOIntGenerators miscGen;
	/* Intermediate values */
	private SInt r;
	private SInt tTop;
	private SInt tBot;
	private SInt tPrime;
	private SInt mPrime;
	private SInt topEq;
	private SInt recurse;
	private SInt z;
	private SInt result;
	private OInt mO;
	private OInt mBar;
	private OInt mBot;
	private OInt mTop;
	
	private static OInt ONE;

	@Override
	public ProtocolProducer nextProtocolProducer() {
		NumericProtocolBuilder npb = new NumericProtocolBuilder(bnFac);
		switch (state) {
		case LOAD_MASK: {
			tBits = npb.getSIntArray(bitLength); 
			r = npb.getSInt();
			npb.addProtocolProducer(subFac.getZTMaskProtocol(tBits, r, securityParameter));
			state = State.BUILD_TS;
			break;
		}
		case BUILD_TS: {
			botBits = bitLength / 2;
			topBits = bitLength - botBits;
			SInt[] rBotBits = Arrays.copyOf(tBits, botBits);
			SInt[] rTopBits = Arrays.copyOfRange(tBits, botBits, bitLength);
			OInt[] topPows = miscGen.getTwoPowers(botBits); 
			OInt[] botPows = Arrays.copyOf(topPows, topBits);
			npb.beginParScope();
				tTop = npb.innerProduct(topPows, rTopBits);
				tBot = npb.innerProduct(botPows, rBotBits);
				SInt d = npb.sub(x, y);
			npb.endCurScope();	
			OInt tmp = bnFac.getOInt(BigInteger.ONE.shiftLeft(bitLength)); //2^(l)
			z = npb.add(d, tmp);
			SInt mS = npb.add(z, r);		
			mO = bnFac.getOInt();
			npb.addProtocolProducer(bnFac.getOpenProtocol(mS, mO));
			r = null;
			state = State.TEST_TOP;
			break;
		}
		case TEST_TOP: {
			BigInteger mMod = mO.getValue().mod(BigInteger.ONE.shiftLeft(bitLength));
			mBar = bnFac.getOInt(mMod);
			mBot = bnFac.getOInt(mMod.mod(BigInteger.ONE.shiftLeft(botBits)));
			mTop = bnFac.getOInt(mMod.shiftRight(botBits));
			SInt dTop = npb.sub(mTop, tTop);
			topEq = npb.getSInt();
			ProtocolProducer pp = compFac.getZeroTestProtocol(dTop, topEq, topBits, secParam);
			npb.addProtocolProducer(pp);
			state = State.SELECT;
			mO = null;
			break;
		}
		case SELECT: {
			npb.beginParScope();
				tPrime = npb.conditionalSelect(topEq, tBot, tTop);
				mPrime = npb.conditionalSelect(topEq, mBot, mTop);
			npb.endCurScope();
			topEq = null;
			state = State.RECURSE;
			break;
		}
		case RECURSE: {
			if (!ONE.isReady()) {
				ONE = bnFac.getOInt(BigInteger.ONE);
			}
			recurse = npb.getSInt();
			if (bitLength == 2) {	
				recurse = npb.mult(tPrime, mPrime);
				recurse = npb.sub(mPrime, recurse);
				recurse = npb.sub(ONE, recurse);				
			} else {
				int half = (bitLength+1)/2;
				ProtocolProducer gt = compFac.getGreaterThanProtocol(mPrime, tPrime, recurse, half, secParam);
				npb.addProtocolProducer(gt);
			}
			tPrime = mPrime = null;
			state = State.RESULT;
			break;
		}
		case RESULT: {
			OInt k = bnFac.getOInt(BigInteger.ONE.shiftLeft(botBits));  // 2^{l/2}
			OInt h = bnFac.getOInt(BigInteger.ONE.shiftLeft(bitLength)); // 2^l
			OInt invK = bnFac.getOInt();
			npb.beginParScope();
				npb.addProtocolProducer(invFac.getLocalInversionProtocol(k, invK));
				npb.beginSeqScope();
					SInt zPrime = npb.mult(k, tTop);
					zPrime = npb.add(zPrime, tBot);
					zPrime = npb.sub(mBar, zPrime);
				npb.endCurScope();
				npb.beginSeqScope();		
					SInt u = npb.sub(ONE, recurse);
					u = npb.mult(h, u);
				npb.endCurScope();
			npb.endCurScope();			
			zPrime = npb.add(zPrime, u);
			zPrime = npb.sub(z, zPrime);
			npb.mult(invK, zPrime, result);			
			state = State.DONE;
			recurse = tTop = tBot = z = null;
			mBar = mBot = mTop = null;
			break;
		}
		case DONE:
			return null;
		default:
			break;
		}
		return npb.getProtocol();
	}

}
