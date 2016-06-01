package dk.alexandra.fresco.lib.newcompare;

import dk.alexandra.fresco.framework.ProtocolFactory;
import dk.alexandra.fresco.framework.value.SInt;
import dk.alexandra.fresco.lib.compare.eq.EqualityProtocol;
import dk.alexandra.fresco.lib.compare.gt.GreaterThanProtocol;
import dk.alexandra.fresco.lib.compare.zerotest.ZeroTestProtocol;

public interface NewCompareFactory extends ProtocolFactory {
	
	public ZeroTestProtocol getZeroTestProtocol(SInt x, SInt result, int bitLength, int secParam);
	
	public GreaterThanProtocol getGreaterThanProtocol(SInt x, SInt y, SInt result, int bitLength, int secParam);
	
	public EqualityProtocol getEqualityProtocol(SInt x, SInt y, SInt result, int bitLength, int secParam);

}
