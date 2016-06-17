package dk.alexandra.fresco.lib.newcompare.subprotocols;

import dk.alexandra.fresco.framework.value.OInt;
import dk.alexandra.fresco.framework.value.SInt;
import dk.alexandra.fresco.lib.compare.MiscOIntGenerators;
import dk.alexandra.fresco.lib.compare.RandomAdditiveMaskFactory;
import dk.alexandra.fresco.lib.compare.RandomAdditiveMaskFactoryImpl;
import dk.alexandra.fresco.lib.compare.RandomAdditiveMaskProtocol;
import dk.alexandra.fresco.lib.compare.zerotest.ZeroTestBruteforceProtocol;
import dk.alexandra.fresco.lib.compare.zerotest.ZeroTestReducerProtocol;
import dk.alexandra.fresco.lib.field.integer.BasicNumericFactory;
import dk.alexandra.fresco.lib.math.integer.HammingDistanceProtocol;
import dk.alexandra.fresco.lib.math.integer.PreprocessedNumericBitFactory;
import dk.alexandra.fresco.lib.math.integer.exp.ExpFactory;
import dk.alexandra.fresco.lib.math.integer.exp.ExpFromOIntFactory;

public class ComparisonSubProtocolFactoryImpl implements ComparisonSubProtocolFactory {
	
	private MiscOIntGenerators miscGen;
	private BasicNumericFactory bnFac;
	private ExpFactory expFac;
	private PreprocessedNumericBitFactory pnbFac;
	
	/**
	 * Constructs an factory for the subprotocols used in comparisons.
	 * @param bitLength an assumed upperbound
	 * @param securityParameter
	 * @param bnFac
	 * @param expFac
	 * @param pnbFac
	 */
	public ComparisonSubProtocolFactoryImpl(BasicNumericFactory bnFac,
			ExpFactory expFac, PreprocessedNumericBitFactory pnbFac) {
		this.bnFac = bnFac;
		this.expFac = expFac;
		this.pnbFac = pnbFac;
		this.miscGen = new MiscOIntGenerators(bnFac);
	}

	@Override
	public ZeroTestBruteforceProtocol getZTBruteForceProtocol(SInt x, SInt result, int max) {
		ZeroTestBruteforceProtocol zbp = new PolynomialZeroTest(x, result, max, miscGen, expFac, bnFac);
		return zbp;
	}

	@Override
	public ZeroTestReducerProtocol getZTReducerProtocol(SInt x, SInt result, int bitLength, int securityParameter) {
		ZeroTestReducerProtocol zrp = new ZTHammingReducer(x, result, bitLength, securityParameter, this, bnFac);
		return zrp;
	}

	@Override
	public HammingDistanceProtocol getZTHammingDistanceProtocol(SInt[] xs, OInt y, SInt result) {
		HammingDistanceProtocol hdp = new ZTHammingDistance(xs, y, result, bnFac);
		return hdp;
	}

	@Override
	public RandomAdditiveMaskProtocol getZTMaskProtocol(SInt[] bits, SInt mask, int securityParameter) {
		RandomAdditiveMaskFactory ramf = new RandomAdditiveMaskFactoryImpl(bnFac, pnbFac);
		RandomAdditiveMaskProtocol ramp = ramf.getRandomAdditiveMaskProtocol(securityParameter, bits, mask);
		return ramp;
	}

}
