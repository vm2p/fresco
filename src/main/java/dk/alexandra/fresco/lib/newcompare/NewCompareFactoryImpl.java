package dk.alexandra.fresco.lib.newcompare;

import dk.alexandra.fresco.framework.value.SInt;
import dk.alexandra.fresco.lib.compare.eq.EqualityProtocol;
import dk.alexandra.fresco.lib.compare.gt.GreaterThanProtocol;
import dk.alexandra.fresco.lib.compare.zerotest.ZeroTestProtocol;
import dk.alexandra.fresco.lib.field.integer.BasicNumericFactory;
import dk.alexandra.fresco.lib.math.integer.PreprocessedNumericBitFactory;
import dk.alexandra.fresco.lib.math.integer.exp.ExpFactory;
import dk.alexandra.fresco.lib.math.integer.exp.ExpFromOIntFactory;
import dk.alexandra.fresco.lib.newcompare.subprotocols.ComparisonSubProtocolFactory;
import dk.alexandra.fresco.lib.newcompare.subprotocols.ComparisonSubProtocolFactoryImpl;

public class NewCompareFactoryImpl implements NewCompareFactory {
	
	private ComparisonSubProtocolFactory subFac;
	private BasicNumericFactory bnFac;

	public NewCompareFactoryImpl(BasicNumericFactory bnFac, ExpFactory expFac, PreprocessedNumericBitFactory pnbFac) {
		this.subFac = new ComparisonSubProtocolFactoryImpl(bnFac, expFac, pnbFac);
		this.bnFac = bnFac;
	}

	@Override
	public ZeroTestProtocol getZeroTestProtocol(SInt x, SInt result, int bitLength, int secParam) {
		ZeroTestProtocol ztp = new NewZeroTestProtocol(x, result, bitLength, secParam, subFac, bnFac);
		return ztp;
	}

	@Override
	public GreaterThanProtocol getGreaterThanProtocol(SInt x, SInt y, SInt result, int bitLength, int secParam) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EqualityProtocol getEqualityProtocol(SInt x, SInt y, SInt result, int bitLength, int secParam) {
		EqualityProtocol eqp = new NewEquality(x, y, result, secParam, bitLength, bnFac, this);
		return eqp;
	}

}
