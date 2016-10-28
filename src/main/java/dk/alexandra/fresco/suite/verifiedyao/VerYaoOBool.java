package dk.alexandra.fresco.suite.verifiedyao;

import dk.alexandra.fresco.framework.value.OBool;

/**
 * A public boolean value.
 * 
 * TODO: The O-types should rather be common to all protocol suites,
 * i.e., not DummyOBool, ShamirOBool, etc.
 *
 */
public class VerYaoOBool implements OBool {

	private static final long serialVersionUID = -4762843635114299987L;
	
	private final int id;
	
	private Boolean value;

	public VerYaoOBool(int id, boolean b) {
		this.id = id;
		this.value = b;
	}

	public VerYaoOBool(int id) {
		this.id = id;
		this.value = null;
	}

	@Override
	public byte[] getSerializableContent() {
		byte s;
		if (this.value) { 
			s = 1;
		} else {
			s = 0;
		}
		return new byte[] {s};
	}

	@Override
	public void setSerializableContent(byte[] val) {
		this.value = val[0] == 1;
	}

	@Override
	public boolean isReady() {
		return this.value != null;
	}


	@Override
	public boolean getValue() {
		return this.value;
	}

	@Override
	public void setValue(boolean b) {
		this.value = b;
	}
	
	public int getId() {
		return this.id;
	}
	
	@Override
	public String toString() {
		return "VerYaoOBool(" + this.id + "; " + this.value + ")";
	}
	
}
