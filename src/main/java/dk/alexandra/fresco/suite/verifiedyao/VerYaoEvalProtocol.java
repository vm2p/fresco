package dk.alexandra.fresco.suite.verifiedyao;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import dk.alexandra.fresco.framework.MPCException;
import dk.alexandra.fresco.framework.network.SCENetwork;
import dk.alexandra.fresco.framework.sce.resources.ResourcePool;
import dk.alexandra.fresco.framework.value.Value;

public class VerYaoEvalProtocol extends VerYaoProtocol {

	private boolean done = false;
	
	/**
	 * Adjust the index of the wires. Test purposes only.
	 * */
	private List<VerYaoProtocol> adjust(List<VerYaoProtocol> gates, HashMap<Integer,Integer> assoc) {
		List<VerYaoProtocol> ret = new ArrayList<VerYaoProtocol>();
		
		Iterator<VerYaoProtocol> gatesIterator = gates.iterator();
		while (gatesIterator.hasNext()) {
			
			VerYaoProtocol gate = gatesIterator.next();
			
			VerYaoSBool newin0 = new VerYaoSBool(assoc.getOrDefault(gate.getIn_w()[0].getId(), gate.getIn_w()[0].getId()));
			VerYaoSBool newin1 = new VerYaoSBool(assoc.getOrDefault(gate.getIn_w()[1].getId(), gate.getIn_w()[1].getId()));
			VerYaoSBool newout = new VerYaoSBool(assoc.getOrDefault(gate.getOut_w()[0].getId(), gate.getOut_w()[0].getId()));
			
			/*if (assoc.containsKey(gate.getIn_w()[0].getId()) && assoc.containsKey(gate.getIn_w()[1].getId())) {
				gate.setIn_w(new VerYaoSBool[] { new VerYaoSBool(assoc.get(gate.getIn_w()[0].getId())), new VerYaoSBool(assoc.get(gate.getIn_w()[1].getId())) });
			}
			else {
				if (assoc.containsKey(gate.getIn_w()[0])) {
					gate.setIn_w(new VerYaoSBool[] { new VerYaoSBool(assoc.get(gate.getIn_w()[0].getId())), new VerYaoSBool(gate.getIn_w()[1].getId()) });
				}
				else if (assoc.containsKey(gate.getIn_w()[1])) {
					gate.setIn_w(new VerYaoSBool[] { new VerYaoSBool(gate.getIn_w()[0].getId()), new VerYaoSBool(assoc.get(gate.getIn_w()[1].getId())) });
				}
				else {
					gate.setIn_w(new VerYaoSBool[] { new VerYaoSBool(gate.getIn_w()[0].getId()), new VerYaoSBool(gate.getIn_w()[1].getId()) });
				}
			}
			
			if (assoc.containsKey(gate.getOut_w()[0].getId())) {
				gate.setOut_w(new VerYaoSBool[] { new VerYaoSBool(assoc.getgate.getOut_w()[0].getId()) });
			}*/
			
			gate.setIn_w( new VerYaoSBool[] { newin0, newin1 } );
			gate.setOut_w( new VerYaoSBool[] { newout } );
			
			ret.add(gate);
		}
		
		return ret;
	}
	
	private ArrayList<Integer> notUsed(List<Integer> a, List<Integer> b, int max) {
		ArrayList<Integer> ret = new ArrayList<Integer>();

		for (int i = 0; i < max; i++) {
			if (!(a.contains(i) || b.contains(i))) {
				ret.add(i);
			}
		}
		
		return ret;
	}
	
	private void fixWireArrays(ArrayList<Integer> notUsed, int max) {
		int c = 0;
		
		for (int i = 0; i < VerYaoConfiguration.A.size(); i++) {
			if (VerYaoConfiguration.A.get(i) == - 1) {
				VerYaoConfiguration.A.set(i, notUsed.get(c++));
			}
			if (VerYaoConfiguration.B.get(i) == - 1) {
				VerYaoConfiguration.B.set(i, notUsed.get(c++));
			}
		}
	}
	
	private List<VerYaoProtocol> fixOutputIndex(List<VerYaoProtocol> gates, int outstart, int outs, int ins) {
		List<VerYaoProtocol> ret = new ArrayList<VerYaoProtocol>();
		Hashtable<Integer,Integer> aux = new Hashtable<Integer,Integer>();
		List<VerYaoProtocol> visited = new ArrayList<VerYaoProtocol>();
		
		for (int i = ins; i < outs+ins; i ++) {
			aux.put(i, outstart);
			outstart++;
		}
		
		Iterator<VerYaoProtocol> gatesIterator = gates.iterator();
		while (gatesIterator.hasNext()) {
			
			VerYaoProtocol gate = gatesIterator.next();
			if (aux.containsKey(gate.getOut_w()[0].getId())) {
				gate.setOut_w(new VerYaoSBool[] { new VerYaoSBool(aux.get(gate.getOut_w()[0].getId())) });
				ret.add(gate);
				visited.add(gate);
			}
			else {
				ret.add(gate);
			}
		}
		
		Iterator<VerYaoProtocol> retIterator = ret.iterator();
		while (retIterator.hasNext()) {
			
			VerYaoProtocol gate = retIterator.next();
			if (aux.contains(gate.getOut_w()[0].getId()) && !visited.contains(gate)) {
				gate.setOut_w(new VerYaoSBool[] { new VerYaoSBool(-1) });
			}
		}
		
		return ret;
	}
	
	
	/**
	 * 
	 * */
	private List<Integer> outputWires (List<VerYaoProtocol> gates, int outstart, int outs, int ins) {
		List<Integer> ret = new ArrayList<Integer>();
		
		ArrayList<VerYaoProtocol> gatescopy = new ArrayList<VerYaoProtocol>(gates);
		ArrayList<VerYaoProtocol> visited = new ArrayList<VerYaoProtocol>();
				
		for (int i = outstart; i < outstart+outs; i++) {
			try {
				final int ii = i;
				gatescopy.removeIf((ga) -> ga.getOut_w()[0].getId() != ii);
				
				visited.add(gatescopy.get(0));
				
				int outw = gatescopy.get(0).getQ();
				gatescopy = new ArrayList<VerYaoProtocol>(gates);
				ret.add(outw+ins);
			} catch (Exception e) {
				//e.printStackTrace();
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
		//System.out.println(l.size() + " comparing to " + VerYaoConfiguration.q);
		for (int i = 0; i < l.size(); i++) {
			//System.out.println(l.get(i));
			ret = ret + l.get(i) + ",";
		}
		
		return ret.substring(0, ret.length() - 1);
		
		/*Iterator<String> lIterator = l.iterator();
		while (lIterator.hasNext()) {
			
			String toAdd = lIterator.next();
			
			if (lIterator.hasNext() == true) {
				ret = ret + toAdd + ",";
			}
			else {
				ret = ret + toAdd;
			}
		}
		
		return ret;*/
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
	 * Retrieves the first incoming wire of some gate, according to the
	 * validity definitions of Foundations of Garbled Circuits
	 * 
	 * @param g
	 * 		gate
	 * @param ins
	 * 		number of input wires
	 * @param gates
	 * 		complete list of all the gates of the circuit
	 * */
	private int wireA(VerYaoProtocol g, int ins, List<VerYaoProtocol> gates) {
	
		//ArrayList<VerYaoProtocol> gatescopy = new ArrayList<VerYaoProtocol>(gates);
		
		//System.out.println("Good " + g.toString());
		
		VerYaoSBool [] in_w = g.getIn_w();
		int ret = 0;
	
		int inp = in_w[0].getId();
	
		if (inp < ins) {
			ret = inp;
		}
		else {
			try {
				gates.removeIf(ga -> ga.getOut_w()[0].getId() != inp);
				
				if (gates.isEmpty()) {
					ret = g.getIn_w()[0].getId();
				}
				else {
					int feedw = gates.get(0).getQ();
					ret = feedw + ins;
				}
			} catch (Exception e) {
				System.out.println(g.toString());
			}
		}

		return ret;
	}
	
	/**
	 * Retrieves the second incoming wire of some gate, according to the
	 * validity definitions of Foundations of Garbled Circuits
	 * 
	 * @param g
	 * 		gate
	 * @param ins
	 * 		number of input wires
	 * @param gates
	 * 		complete list of all the gates of the circuit
	 * */
	private int wireB(VerYaoProtocol g, int ins, List<VerYaoProtocol> gates) {
				
		//ArrayList<VerYaoProtocol> gatescopy = new ArrayList<VerYaoProtocol>(gates);
		int inarity = g.getInarity();
		VerYaoSBool [] in_w = g.getIn_w();
		int ret = 0;
		int inp;
		
		//System.out.println("Good = " + g.toString());
		
		if (g.getIn_w()[1].getId() == 640) {
			System.out.println("bota");
		}
		
		if (inarity == 1) {
			inp = in_w[0].getId();
		}
		else {
			inp = in_w[1].getId();
		}
		
		gates.removeIf((ga) -> ga.getOut_w()[0].getId() != inp);
		
		if (inp < ins && gates.isEmpty()) {
			ret = inp;
		}
		else {
			try {
				if (gates.isEmpty()) {
					ret = g.getIn_w()[1].getId();
				}
				else {
					int feedw = gates.get(0).getQ();
					ret = feedw + ins;
				}
			} catch (Exception e) {
				System.out.println("Bad = " + g.toString());
			}
		}
		
		return ret;
	}
	
	private int maxList (List<Integer> l) {
		int ret = -1;
		
		for (int i = 0; i < l.size(); i++) {
			if (l.get(i) > ret) ret = l.get(i);
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
		}
		
		return ret;
	}
	
	@Override
	public EvaluationStatus evaluate(int round, ResourcePool resourcePool, SCENetwork network) {
		
		System.out.println("Round = " + round + " && ID = " + resourcePool.getMyId());
		
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
				try {
					p = Runtime.getRuntime().exec(command);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				BufferedReader bri = new BufferedReader (new InputStreamReader(p.getInputStream()));
				
				String line = null;
				try {
					line = bri.readLine();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					bri.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					p.waitFor();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				/*
				 * OCaml outputs a series of values separated by spaces " ".
				 * This divides the output into 'state' and 'message'.
				 * */
				String[] msg = line.split(" ");
				String outmsg = msg[msg.length-1];
				
				/*FILE TEST HERE*/
				FileWriter fw = null;
				try {
					fw = new FileWriter("state1.dat");
					fw.write(msg[0] + "\n");
					fw.close();
				} catch (IOException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				/*END WRITING STATE TO FILE*/
								
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
				
				/*FILE TEST HERE*/
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
				/*END WRITING STATE TO FILE*/
							
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
				try {
					p = Runtime.getRuntime().exec(command);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				bri = new BufferedReader (new InputStreamReader(p.getInputStream()));
				
				
				/*
				 * Captures the output of the OCaml program
				 * */
				line = null;
				
				try {
					line = bri.readLine();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					bri.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					p.waitFor();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				/*
				 * The final output of the protocol will be stored in the variable 'line'
				 * */
				System.out.println("Final: " + line);
				VerYaoConfiguration.output = line;
				done = true;
				break;
			default:
				throw new MPCException("No further rounds.");
			}
			break;
		case 2 :
			switch (round) {
			case 0:
				/*VerYaoConfiguration.li1 = 128;
				VerYaoConfiguration.li2 = 128;
				VerYaoConfiguration.i1 = "00000000000100010010001000110011010001000101010101100110011101111000100010011001101010101011101111001100110111011110111011111111";
				VerYaoConfiguration.i2 = "00000000000000010000001000000011000001000000010100000110000001110000100000001001000010100000101100001100000011010000111000001111";
				VerYaoConfiguration.n = VerYaoConfiguration.li1 + VerYaoConfiguration.li2;*/
				VerYaoConfiguration.n = VerYaoConfiguration.li1 + VerYaoConfiguration.li2;
				
				System.out.println(VerYaoConfiguration.assoc.get(4736));
				
				System.out.println(VerYaoConfiguration.assoc.values().contains(384) + " && " + VerYaoConfiguration.assoc.values().contains(257));
				
				ArrayList<Integer> notUsed = notUsed(VerYaoConfiguration.A, VerYaoConfiguration.B, Integer.max(Collections.max(VerYaoConfiguration.A), Collections.max(VerYaoConfiguration.B)));
				fixWireArrays(notUsed, Integer.max(Collections.max(VerYaoConfiguration.A), Collections.max(VerYaoConfiguration.B)));
				
				//VerYaoConfiguration.gates = adjust(new ArrayList<VerYaoProtocol>(VerYaoConfiguration.gates), VerYaoConfiguration.assoc);
				
				//List<VerYaoProtocol> gatescopy = new ArrayList<VerYaoProtocol>(VerYaoConfiguration.gates);
				
				/*
				 * Constructs the 'B' list, that contains the second incoming wires of every gate
				 * */
				//gatescopy = new ArrayList<VerYaoProtocol>(VerYaoConfiguration.gates);
				//List<Integer> rwires = VerYaoConfiguration.gates.stream().map(g -> wireB(g, VerYaoConfiguration.n, new ArrayList<VerYaoProtocol>(VerYaoConfiguration.gates))).collect(Collectors.toList());
				//System.out.println(gatescopy.equals(VerYaoConfiguration.gates));
				
				/*
				 * Constructs the 'A' list, that contains the first incoming wires of every gate
				 * */
				
				//List<Integer> lwires = VerYaoConfiguration.gates.stream().map(g -> wireA(g, VerYaoConfiguration.n, new ArrayList<VerYaoProtocol>(VerYaoConfiguration.gates))).collect(Collectors.toList());
				//System.out.println(gatescopy.equals(VerYaoConfiguration.gates));
				
				
				
				
				
				/*
				 * We need to fix some indexes of the output wires.
				 * FRESCO changes the index of the output wires. We need to put them back in
				 * place.
				 * */
				//System.out.println(Integer.max(maxList(lwires), maxLisx 256t(rwires)));
				VerYaoConfiguration.gates = fixOutputIndex(new ArrayList<VerYaoProtocol>(VerYaoConfiguration.gates), Integer.max(maxList(VerYaoConfiguration.A), maxList(VerYaoConfiguration.B)) + 2 - VerYaoConfiguration.m, VerYaoConfiguration.m, VerYaoConfiguration.n);
				System.out.println(VerYaoConfiguration.gates.size());
				/*
				 * Constructs extra wires, that will work as 'output' wires
				 * */
				List<Integer> extraw = outputWires(new ArrayList<VerYaoProtocol>(VerYaoConfiguration.gates), Integer.max(maxList(VerYaoConfiguration.A), maxList(VerYaoConfiguration.B)) + 2 - VerYaoConfiguration.m, VerYaoConfiguration.m, VerYaoConfiguration.n);
				//System.out.println(VerYaoConfiguration.gates.size());
				/*
				 * Constructs extra gates, that will work as 'output' gates.
				 * 
				 * Both 'extraw' and 'extrag' are needed by our OCaml formalisation.
				 * */
				List<String> extrag = new ArrayList<String>(Collections.nCopies(VerYaoConfiguration.m, "0001"));
			
				/*
				 * Constructs the list of truth tables in a format readable by OCaml. Namely, this format implies that
				 * all gates are in the 'format' of its 'output column'. 
				 * 
				 * For example, XOR will be represented as 0110.
				 * */
				List<String> gates = VerYaoConfiguration.gates.stream().map(g -> truthT(g)).collect(Collectors.toList());
				
				/*
				 * Constructs the final list of the first incoming wires of every gate. 
				 * This is the combination of 'lwires' with 'extraw'.
				 * */
				//VerYaoConfiguration.A = Stream.concat(lwires.stream(), extraw.stream()).collect(Collectors.toList());
				VerYaoConfiguration.A.addAll(extraw);
				/*
				 * Constructs the final list of the second incoming wires of every gate. 
				 * This is the combination of 'rwires' with 'extraw'
				 * */
				//VerYaoConfiguration.B = Stream.concat(rwires.stream(), extraw.stream()).collect(Collectors.toList());
				VerYaoConfiguration.B.addAll(extraw);
				/*
				 * Constructs the final list of gates.
				 * This is the combination of 'gates' with 'extrag'
				 * */
				VerYaoConfiguration.G = Stream.concat(gates.stream(), extrag.stream()).collect(Collectors.toList());
				System.out.println(VerYaoConfiguration.G.get(VerYaoConfiguration.G.size() - 1));
				/*
				 * Builds the command the will call the OCaml program that executes the first step of the protocol.
				 * 
				 * The command is given:
				 	* Input of party 1
				 	* The configuration of the circuit (Bellare format)
				 * 
				 * It will return:
				 	* The state of party 1
				 	* The protocol message to be sent to party 2 
				 * */
				FileWriter p2stage1 = null;
				//FileWriter debug = null;
				try {
					p2stage1 = new FileWriter("p2stage1.dat");
					//debug = new FileWriter("debug13.txt");
					System.out.println("LI1 = " + VerYaoConfiguration.li1 + "\n");
					p2stage1.write(VerYaoConfiguration.li1 + "\n");
					System.out.println("I2 = " + parseInputs(VerYaoConfiguration.i2) + "\n");
					p2stage1.write(parseInputs(VerYaoConfiguration.i2) + "\n");
					System.out.println("N = " + VerYaoConfiguration.n + "\n");
					p2stage1.write(VerYaoConfiguration.n + "\n");
					System.out.println("M = " + VerYaoConfiguration.m + "\n");
					p2stage1.write(VerYaoConfiguration.m + "\n");
					System.out.println("Q = " + VerYaoConfiguration.q + "\n");
					p2stage1.write(VerYaoConfiguration.q + "\n");
					String aa = list2string(VerYaoConfiguration.A);
					String bb = list2string(VerYaoConfiguration.B);
					System.out.println("Size of A = " + list2string(VerYaoConfiguration.A));
					System.out.println("Size of B = " + VerYaoConfiguration.B.size());
					//debug.write("A = " + list2string(VerYaoConfiguration.A) + "\n");
					p2stage1.write(aa + "\n");
					//debug.write("B =" + list2string(VerYaoConfiguration.B) + "\n");
					p2stage1.write(bb + "\n");
					//debug.write("G = " + gates2string(VerYaoConfiguration.G) + "\n");
					String gg = gates2string(VerYaoConfiguration.G);
					System.out.println("Size of G = " + VerYaoConfiguration.G.size());
					p2stage1.write(gg + "\n");
					p2stage1.close();
				} catch (IOException e3) {
					// TODO Auto-generated catch block
					e3.printStackTrace();
				}
				
				String command = "./p2_stage1.native";
				
				Process p = null;
				try {
					p = Runtime.getRuntime().exec(command);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				BufferedReader bri = new BufferedReader (new InputStreamReader(p.getInputStream()));
				
				String line = null;
				try {
					line = bri.readLine();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					bri.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					p.waitFor();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				String[] output = line.split(" ");
				
				FileWriter fw = null;
				try {
					fw = new FileWriter("state2.dat");
					//toks
					fw.write(output[0] + "\n");
					//gps
					fw.write(output[1] + "\n");
					
					fw.write(VerYaoConfiguration.n + "\n");
					fw.write(VerYaoConfiguration.m + "\n");
					fw.write(VerYaoConfiguration.q + "\n");
										
					fw.write(list2string(VerYaoConfiguration.A) + "\n");					
					
					fw.write(list2string(VerYaoConfiguration.B) + "\n");
					//fung
					fw.write(output[2] + "\n");
					
					//x2g
					fw.write(output[3] + "\n");
					//rand2
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
				try {
					p = Runtime.getRuntime().exec(command);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				bri = new BufferedReader (new InputStreamReader(p.getInputStream()));
				
				/*
				 * Captures the output of the OCaml program
				 * */
				line = null;
				try {
					line = bri.readLine();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
					bri.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
					p.waitFor();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
								
				/*
				 * OCaml outputs a series of values separated by spaces " ".
				 * In this case, no division is needed because everything needs to be
				 * sent to party 2.
				 * */
				output = line.split(" ");
								
				/*
				 * Sends the third message of the protocol to party 2, throughout 
				 * channel 0.
				 * */				
				//System.out.println("CY = " + output[0]);
				network.send(1, output[0]);
				//System.out.println("E = " + output[1]);
				network.send(1, output[1]);
				//System.out.println("N = " + output[2]);
				network.send(1, output[2]);
				//System.out.println("M = " + output[3]);
				network.send(1, output[3]);
				//System.out.println("Q = " + output[4]);
				network.send(1, output[4]);
				//System.out.println("AA = " + output[5]);
				network.send(1, output[5]);
				//System.out.println("BB = " + output[6]);
				network.send(1, output[6]);
				//System.out.println("FG = " + output[7]);
				network.send(1, output[7]);
				//System.out.println("X2G = " + output[8]);
				if (output.length == 9) network.send(1, output[8]);
				else network.send(1, "");
				
				break;
			case 3:
				network.expectInputFromPlayer(1);
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
