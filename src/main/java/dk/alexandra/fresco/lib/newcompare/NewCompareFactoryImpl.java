package dk.alexandra.fresco.lib.newcompare;

import dk.alexandra.fresco.framework.value.SInt;
import dk.alexandra.fresco.lib.compare.eq.EqualityProtocol;
import dk.alexandra.fresco.lib.compare.gt.GreaterThanProtocol;
import dk.alexandra.fresco.lib.compare.zerotest.ZeroTestProtocol;

public class NewCompareFactoryImpl implements NewCompareFactory {

	@Override
	public ZeroTestProtocol getZeroTestProtocol(SInt x, SInt result, int bitLength, int secParam) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GreaterThanProtocol getGreaterThanProtocol(SInt x, SInt y, SInt result, int bitLength, int secParam) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EqualityProtocol getEqualityProtocol(SInt x, SInt y, SInt result, int bitLength, int secParam) {
		// TODO Auto-generated method stub
		return null;
	}

}
