package dk.alexandra.fresco.suite.verifiedyao;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import dk.alexandra.fresco.framework.MPCException;
import dk.alexandra.fresco.framework.network.SCENetwork;
import dk.alexandra.fresco.framework.sce.resources.ResourcePool;
import dk.alexandra.fresco.framework.value.Value;

public class VerYaoEvalProtocol extends VerYaoProtocol {

	private boolean done = false;
	
	private List<Integer> getA(List<VerYaoProtocol> gates) {
		List<Integer> ret = new ArrayList<Integer>();
		
		Iterator<VerYaoProtocol> iter = gates.iterator();
		
		while (iter.hasNext()) ret.add(iter.next().getIn_w()[0].getId());
		
		return ret;
	}
	
	private List<Integer> getB(List<VerYaoProtocol> gates) {
		List<Integer> ret = new ArrayList<Integer>();
		
		Iterator<VerYaoProtocol> iter = gates.iterator();
		
		while (iter.hasNext()) {
			VerYaoProtocol gate = iter.next();
			
			if (gate.getInarity() == 1) {
				ret.add(gate.getIn_w()[0].getId());
			}
			else {
				ret.add(gate.getIn_w()[1].getId());
			}
		}
		
		return ret;
	}
	
	private String list2string(List<Integer> l) {
		String ret = "";
		
		for (int i = 0; i < l.size(); i ++) {
			ret = ret + l.get(i) + ",";
		}
		
		return ret.substring(0, ret.length() - 1);
	}
	
	private String gates2string(List<String> l) {
		String ret = "";
		for (int i = 0; i < l.size(); i++) {
			ret = ret + l.get(i) + ",";
		}
		
		return ret.substring(0, ret.length() - 1);
	}
	
	private String parseInputs (String input) {
		String ret = "";
		
		if (input.equals("")) {
			ret = "empty";
		}
		else {
			for (int i = 0; i < input.length() - 1; i++) {
				ret = ret + input.charAt(i) + ",";
			}
		
			ret = ret + input.charAt(input.length() - 1);
		}
		
		return ret;
	}
	
	/**
	 * Maps some description of the functionality of a gate into
	 * some truth table output column.
	 * 
	 * Ex: 'XOR' -> '0110'
	 * 
	 * @param g
	 * 		gate
	 * */
	private String truthT(VerYaoProtocol g) {
		String ret = "";
		
		switch (g.getGate()) {
		case "AND":
			ret = "0001";
			break;
		case "OR":
			ret = "0111";
			break;
		case "INV":
			ret = "1000";
			break;
		case "XOR":
			ret = "0110";
			break;
		case "OUT":
			ret = "0001";
			break;
		}
		
		return ret;
	}
	
	private void fixInputWires() {
		if (!VerYaoConfiguration.inW1.isEmpty()) {
			for (int i = 0 ; i < VerYaoConfiguration.li1 ; i++) {
				VerYaoConfiguration.inW1.get(i).setId(i);
			}
		}
		if (!VerYaoConfiguration.inW2.isEmpty()) {
			for (int i = 0 ; i < VerYaoConfiguration.li2 ; i++) {
				VerYaoConfiguration.inW2.get(i).setId(i + VerYaoConfiguration.li1);
			}
		}
	}
	
	@Override
	public EvaluationStatus evaluate(int round, ResourcePool resourcePool, SCENetwork network) {
				
		switch (resourcePool.getMyId()) {
		case 1 :
			switch (round) {
			case 0:
				network.expectInputFromPlayer(2);
				break;
			case 1:
				Serializable inmsg = "";
								
				inmsg = network.receive(2);
																
				FileWriter p1stage1 = null;
				try {
					p1stage1 = new FileWriter("p1stage1.dat");
					p1stage1.write(parseInputs(VerYaoConfiguration.i1) + "\n");
					p1stage1.write(inmsg + "\n");
					p1stage1.close();
				} catch (IOException e3) {
					// TODO Auto-generated catch block
					e3.printStackTrace();
				}
				
				String command = "./p1_stage1.native";
				
				Process p = null;
				BufferedReader bri = null;
				String line = null;
				try {
					p = Runtime.getRuntime().exec(command);
					bri = new BufferedReader (new InputStreamReader(p.getInputStream()));
					line = bri.readLine();
					bri.close();
					p.waitFor();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				/*
				 * OCaml outputs a series of values separated by spaces " ".
				 * This divides the output into 'state' and 'message'.
				 * */
				String[] msg = line.split(" ");
				String outmsg = msg[msg.length-1];
				
				FileWriter fw = null;
				try {
					fw = new FileWriter("state1.dat");
					fw.write(msg[0] + "\n");
					fw.close();
				} catch (IOException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
								
				//send					
				network.send(2, outmsg);
				
				break;
			case 2:
				network.expectInputFromPlayer(2);
				break;
			case 3:				
				String cy = null, eg = null, n = null, m = null, q = null, aa = null, bb = null, fg = null, x2g = null;

				cy = network.receive(2);
				eg = network.receive(2);
				n = network.receive(2);
				m = network.receive(2);
				q = network.receive(2);
				aa = network.receive(2);
				bb = network.receive(2);
				fg = network.receive(2);
				x2g = network.receive(2);
				
				fw = null;
				try {
					fw = new FileWriter("state2simpl.dat");
					fw.write(cy + "\n");
					fw.write(eg + "\n");
					fw.write(n + "\n");
					fw.write(m + "\n");
					fw.write(q + "\n");
					fw.write(aa + "\n");
					fw.write(bb + "\n");
					fw.write(fg + "\n");
					fw.write(x2g + "\n");
					fw.close();
				} catch (IOException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
							
				FileWriter p1stage2 = null;
				try {
					p1stage2 = new FileWriter("p1stage2.dat");
					p1stage2.write(parseInputs(VerYaoConfiguration.i1) + "\n");
					p1stage2.close();
				} catch (IOException e3) {
					// TODO Auto-generated catch block
					e3.printStackTrace();
				}
				
				command = "./p1_stage2.native";
				
				p = null;
				bri = null;
				line = null;
				try {
					p = Runtime.getRuntime().exec(command);
					bri = new BufferedReader (new InputStreamReader(p.getInputStream()));
					line = bri.readLine();
					bri.close();
					p.waitFor();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				/*
				 * The final output of the protocol will be stored in the variable 'line'
				 * */
				VerYaoConfiguration.output = line;
				done = true;
				network.send(2, "");
				break;
			default:
				throw new MPCException("No further rounds.");
			}
			break;
		case 2 :
			switch (round) {
			case 0:
				VerYaoConfiguration.n = VerYaoConfiguration.li1 + VerYaoConfiguration.li2;
								
				fixInputWires();

				VerYaoConfiguration.A = getA(VerYaoConfiguration.gates);
				VerYaoConfiguration.B = getB(VerYaoConfiguration.gates);
				
				VerYaoConfiguration.G = VerYaoConfiguration.gates.stream().map(g -> truthT(g)).collect(Collectors.toList()); 
				
					try {
						FileWriter fw = new FileWriter("test.sfc");
						fw.write(VerYaoConfiguration.circuitToString());
						fw.close();
					}
					catch (Exception e) {
						
					}
				
				String aa, bb, gg;
				
				aa = list2string(VerYaoConfiguration.A);
				bb = list2string(VerYaoConfiguration.B);
				gg = gates2string(VerYaoConfiguration.G);
				
				FileWriter p2stage1 = null;
				try {
					p2stage1 = new FileWriter("p2stage1.dat");
					p2stage1.write(VerYaoConfiguration.li1 + "\n");
					p2stage1.write(parseInputs(VerYaoConfiguration.i2) + "\n");
					p2stage1.write(VerYaoConfiguration.n + "\n");
					p2stage1.write(VerYaoConfiguration.m + "\n");
					p2stage1.write(VerYaoConfiguration.q + "\n");
					p2stage1.write(aa + "\n");
					p2stage1.write(bb + "\n");
					p2stage1.write(gg + "\n");
					p2stage1.close();
				} catch (IOException e3) {
					// TODO Auto-generated catch block
					e3.printStackTrace();
				}
				
				String command = "./p2_stage1.native";
				
				Process p = null;
				BufferedReader bri = null;
				String line = null;
				try {
					p = Runtime.getRuntime().exec(command);
					bri = new BufferedReader (new InputStreamReader(p.getInputStream()));
					line = bri.readLine();
					bri.close();
					p.waitFor();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				String[] output = line.split(" ");
				
				FileWriter fw = null;
				try {
					fw = new FileWriter("state2.dat");
					fw.write(output[0] + "\n");
					fw.write(output[1] + "\n");
					fw.write(VerYaoConfiguration.n + "\n");
					fw.write(VerYaoConfiguration.m + "\n");
					fw.write(VerYaoConfiguration.q + "\n");
					fw.write(aa + "\n");					
					fw.write(bb + "\n");
					fw.write(output[2] + "\n");
					fw.write(output[3] + "\n");
					fw.write(output[4] + "\n");
					fw.close();
				} catch (IOException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				
				String msg = output[output.length-1];
								
				network.send(1, msg);
				break;
			case 1:
				network.expectInputFromPlayer(1);
				break;
			case 2:
				
				Serializable inmsg = "";
				inmsg = network.receive(1);
								
				FileWriter p2stage2 = null;
				try {
					p2stage2 = new FileWriter("p2stage2.dat");
					p2stage2.write(inmsg + "\n");
					p2stage2.close();
				} catch (IOException e3) {
					// TODO Auto-generated catch block
					e3.printStackTrace();
				}
				
				command = "./p2_stage2.native";
								
				p = null;
				bri = null;
				line = null;
				try {
					p = Runtime.getRuntime().exec(command);
					bri = new BufferedReader (new InputStreamReader(p.getInputStream()));
					line = bri.readLine();
					bri.close();
					p.waitFor();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
								
				output = line.split(" ");
			
				network.send(1, output[0]);
				network.send(1, output[1]);
				network.send(1, output[2]);
				network.send(1, output[3]);
				network.send(1, output[4]);
				network.send(1, output[5]);
				network.send(1, output[6]);
				network.send(1, output[7]);
				if (output.length == 9) network.send(1, output[8]);
				else network.send(1, "");
				
				break;
			case 3:
				network.expectInputFromPlayer(1);
				break;
			case 4:
				inmsg = "";
				inmsg = network.receive(1);
				done = true;
				break;
			default:
				throw new MPCException("No further rounds.");
			}
			break;
		}
		EvaluationStatus status = (done) ? EvaluationStatus.IS_DONE
				: EvaluationStatus.HAS_MORE_ROUNDS;
		return status;
	}

	@Override
	public Value[] getInputValues() {
		return null;
	}

	@Override
	public Value[] getOutputValues() {
		return null;
	}

}
