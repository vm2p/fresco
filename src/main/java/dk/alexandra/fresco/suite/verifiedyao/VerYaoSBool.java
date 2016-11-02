package dk.alexandra.fresco.suite.verifiedyao;

import dk.alexandra.fresco.framework.value.SBool;

public class VerYaoSBool implements SBool {

private static final long serialVersionUID = -4614951451129474002L;
	
	private int id;
	private Boolean value;
	
	public VerYaoSBool() {
		
	}
	
	public VerYaoSBool(int id) {
		this.id = id;
		this.value = new Boolean(true);
	}
	
	public VerYaoSBool(int id, boolean b) {
		this.id = id;
		this.value = b;
	}

	@Override
	public byte[] getSerializableContent() {
		// nothing...
		return null;
	}

	@Override
	public void setSerializableContent(byte[] val) {
		// nothing...
	}

	@Override
	public boolean isReady() {
		return true;
	}
	
	public Boolean getValue() {
		return this.value;
	}
	
	public String getValueString() {
		String ret;
		
		if (this.value) {
			ret = "1";
		}
		else {
			ret = "0";
		}
		return ret;
	}
	
	public int getId() {
		return this.id;
	}
	
	public void setId(int id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return Integer.toString(this.id);
	}
	
}
