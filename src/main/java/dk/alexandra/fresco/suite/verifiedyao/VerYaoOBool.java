package dk.alexandra.fresco.suite.verifiedyao;

import dk.alexandra.fresco.framework.value.OBool;

/**
 * A public (open) boolean value.
 */

public class VerYaoOBool implements OBool {

	private static final long serialVersionUID = -4762843635114299987L;
	
	private int id;
	
	private Boolean value;

	/**
	 * Builds an open boolean, with the given
	 * ID and boolean value.
	 * 
	 * @param id
	 * 		wire ID
	 * @param b
	 * 		wire boolean value
	 * */
	public VerYaoOBool(int id, boolean b) {
		this.id = id;
		this.value = b;
	}

	/**
	 * Builds an open boolean, with the given
	 * ID but no boolean value.
	 * 
	 * @param id
	 * 		wire ID
	 * */
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
	
	@Override
	public String toString() {
		return "VerYaoOBool(" + this.id + "," + this.value + ")";
	}
	
}
