package dk.alexandra.fresco.suite.verifiedyao;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import dk.alexandra.fresco.framework.MPCException;
import dk.alexandra.fresco.framework.network.SCENetwork;
import dk.alexandra.fresco.framework.sce.resources.ResourcePool;
import dk.alexandra.fresco.framework.value.Value;

/**
 * VerYaoEvalProtocol gate.
 * 
 * This is the gate were all the evaluation will be made.
 * */

public class VerYaoEvalProtocol extends VerYaoProtocol {

	private boolean done = false;
	
	/**
	 * Returns a list containing the left incoming wires of
	 * each gate.
	 * 
	 * @param gates
	 * 		circuit gates
	 * @return
	 * 		left input wires of each gate
	 * */
	private List<Integer> getA(List<VerYaoProtocol> gates) {
		List<Integer> ret = new ArrayList<Integer>();
		
		Iterator<VerYaoProtocol> iter = gates.iterator();
		
		while (iter.hasNext()) ret.add(iter.next().get_in_wires()[0].getId());
		
		return ret;
	}
	
	/**
	 * Returns a list containing the right incoming wires of
	 * each gate.
	 * 
	 * @param gates
	 * 		circuit gates
	 * @return
	 * 		right input wires of each gate
	 * */
	private List<Integer> getB(List<VerYaoProtocol> gates) {
		List<Integer> ret = new ArrayList<Integer>();
		
		Iterator<VerYaoProtocol> iter = gates.iterator();
		
		while (iter.hasNext()) {
			VerYaoProtocol gate = iter.next();
			
			if (gate.get_in_arity() == 1) {
				ret.add(gate.get_in_wires()[0].getId());
			}
			else {
				ret.add(gate.get_in_wires()[1].getId());
			}
		}
		
		return ret;
	}
	
	/**
	 * Transforms a list of wires' ID's into a string with
	 * comma separated values.
	 * 
	 * Should be used to map the lists A and B into strings.
	 * 
	 * @param wires
	 * 	List of wires' ID's
	 * 
	 * @return
	 * 	String of the wires' ID's
	 * */
	private String wires_to_string(List<Integer> wires) {
		StringBuilder ret = new StringBuilder();
		Iterator<Integer> iter = wires.iterator();
		
		while (iter.hasNext()) {
			Integer next = iter.next();
			if (iter.hasNext()) ret.append(next).append(",");
			else ret.append(next);
		}
		
		return ret.toString();
	}
	
	/**
	 * Transforms a list of gates' functionalities into a string with
	 * comma separated values
	 * 
	 * Should be used to transform the G list into a string.
	 * 
	 * @param gates
	 * 	List of gates
	 * 
	 * @return
	 * 	String of the gates
	 * */
	private String gates_to_string(List<String> gates) {
		StringBuilder ret = new StringBuilder();
		Iterator<String> iter = gates.iterator();
		
		while (iter.hasNext()) {
			String next = iter.next();
			if (iter.hasNext()) ret.append(next).append(",");
			else ret.append(next);
		}
		
		return ret.toString();
	}
	
	/**
	 * Processes the inputs of each party.
	 * 
	 * If the input of some party is empty, then it returns the string
	 * "empty". Otherwise, it creates a string with comma separated
	 * boolean values (0 or 1).
	 * 
	 * @param input
	 * 		input string
	 * @return
	 * 		processed input string
	 * */
	private String parse_inputs (String input) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		StringBuilder ret = new StringBuilder();
		
		if (input.equals("")) {
			ret.append("empty");
		}
		else {
			
			Field field = String.class.getDeclaredField("value");
			field.setAccessible(true);
			
			char[] chars = (char[]) field.get(input);
			int len = chars.length;
			
			for (int i = 0; i < len-1; i++) {
				ret.append(chars[i]).append(",");
	       }
		
			ret.append(chars[len-1]);
		}
		
		return ret.toString();
	}
	
	/**
	 * Maps some description of the functionality of a gate into
	 * some truth table output column.
	 * 
	 * Ex: 'XOR' -> '0110'
	 * 
	 * @param g
	 * 		gate
	 * @return
	 * 		functionality description in truth table format
	 * */
	private String truthT(VerYaoProtocol g) {
		String ret = "";
		
		switch (g.getFunc()) {
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
	
	/**
	 * Fixes the ID's of the input wires of each party.
	 * */
	private void fix_input_wires() {
		if (!VerYaoConfiguration.in_wires1.isEmpty()) {
			for (int i = 0 ; i < VerYaoConfiguration.n_wires1 ; i++) {
				VerYaoConfiguration.in_wires1.get(i).setId(i);
			}
		}
		if (!VerYaoConfiguration.in_wires2.isEmpty()) {
			for (int i = 0 ; i < VerYaoConfiguration.n_wires2 ; i++) {
				VerYaoConfiguration.in_wires2.get(i).setId(i + VerYaoConfiguration.n_wires1);
			}
		}
	}
	
	@Override
	/**
	 * Assuming that the configuration of the circuit is correct, evaluation
	 * of a VerYaoEvalProtocol gate evaluates the circuit with the verified
	 * Yao evaluator.
	 * 
	 * According to the party and the round, it will work as follows:
	 	* Round 0: 
	 		* Party 1 -> waits for the input from party 2.
	 		* Party 2 -> sets up the circuit, calls the OCaml evaluator to
	 		obtain the result of the first step of the protocol and sends it
	 		to party 1.
 		* Round 1:
 			* Party 1 -> receives the round 0 input from party 2, calls the 
 			OCaml evaluator to obtain the result of the second step of the 
 			protocol and sends it to party 2.
	 		* Party 2 -> waits for the input from party 1.
 		* Round 2:
 			* Party 1 ->  waits for the input from party 2.
 			* Party 2 -> receives the round 1 input from party 1, calls the 
 			OCaml evaluator to obtain the result of the third step of the 
 			protocol and sends it to party 1.
		* Round 3:
			* Party 1 -> receives the round 2 input from party 2, calls the 
 			OCaml evaluator to obtain the final output of the protocol,
 			sends an empty message to party 2 and ends its evaluation process.
	 		* Party 2 -> waits for the input from party 1.
 		* Round 4:
 			* Party 1 -> already ended the evaluation process.
 			* Party 2 -> receives the empty message from party 1 and
 			ends its evaluation process.
	 * */
	public EvaluationStatus evaluate(int round, ResourcePool resourcePool, SCENetwork network) {
				
		switch (resourcePool.getMyId()) {
		case 1 :
			switch (round) {
			case 0:
				//Expecting message from party 2
				network.expectInputFromPlayer(2);
				break;
			case 1:
				Serializable inmsg = "";
							
				//Receive message from party 2
				inmsg = network.receive(2);
						
				//We write the inputs of the execution to a file.
				FileWriter p1stage1 = null;
				try {
					p1stage1 = new FileWriter("p1stage1.dat");
					p1stage1.write(parse_inputs(VerYaoConfiguration.input1.toString()) + "\n");
					p1stage1.write(inmsg + "\n");
					p1stage1.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
				
				//OCaml execution
				String command = "./p1_stage1.byte";
				
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
				
				//We write the state of the party to a file.
				FileWriter fw = null;
				try {
					fw = new FileWriter("state1.dat");
					fw.write(msg[0] + "\n");
					fw.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
								
				//Send message to party 2					
				network.send(2, outmsg);
				
				break;
			case 2:
				//Expecting message from party 2
				network.expectInputFromPlayer(2);
				break;
			case 3:				
				String cy = null, eg = null, n = null, m = null, q = null, aa = null, bb = null, fg = null, x2g = null;

				//Receives 9 messages from party 2
				cy = network.receive(2);
				eg = network.receive(2);
				n = network.receive(2);
				m = network.receive(2);
				q = network.receive(2);
				aa = network.receive(2);
				bb = network.receive(2);
				fg = network.receive(2);
				x2g = network.receive(2);
				
				//We write a "simplified" state of party 2 to a file.
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
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
						
				
				//We write the inputs of the execution to a file.
				FileWriter p1stage2 = null;
				try {
					p1stage2 = new FileWriter("p1stage2.dat");
					p1stage2.write(parse_inputs(VerYaoConfiguration.input1.toString()) + "\n");
					p1stage2.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				//OCaml execution
				command = "./p1_stage2.byte";
				
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
				
				//The final output of the protocol will be stored in the variable 'line'
				VerYaoConfiguration.output.append(line);
				
				//The protocol execution is done.
				done = true;
				
				//We send a dummy message to party 2 to signal the end of the protocol
				network.send(2, "");
				
				break;
			default:
				throw new MPCException("No further rounds.");
				}
			break;
		case 2 :
			switch (round) {
			case 0:
				//Definition of the circuit description
				VerYaoConfiguration.n = VerYaoConfiguration.n_wires1 + VerYaoConfiguration.n_wires2;
								
				fix_input_wires();

				VerYaoConfiguration.A = getA(VerYaoConfiguration.gates);
				VerYaoConfiguration.B = getB(VerYaoConfiguration.gates);
				
				VerYaoConfiguration.G = VerYaoConfiguration.gates.stream().map(g -> truthT(g)).collect(Collectors.toList()); 
				
				String aa, bb, gg;
				
				aa = wires_to_string(VerYaoConfiguration.A);
				bb = wires_to_string(VerYaoConfiguration.B);
				gg = gates_to_string(VerYaoConfiguration.G);
				
				//We write the inputs of the execution to a file.
				FileWriter p2stage1 = null;
				try {
					p2stage1 = new FileWriter("p2stage1.dat");
					p2stage1.write(VerYaoConfiguration.n_wires1 + "\n");
					p2stage1.write(parse_inputs(VerYaoConfiguration.input2.toString()) + "\n");
					p2stage1.write(VerYaoConfiguration.n + "\n");
					p2stage1.write(VerYaoConfiguration.m + "\n");
					p2stage1.write(VerYaoConfiguration.q + "\n");
					p2stage1.write(aa + "\n");
					p2stage1.write(bb + "\n");
					p2stage1.write(gg + "\n");
					p2stage1.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
				
				//OCaml execution
				String command = "./p2_stage1.byte";
				
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
				String[] output = line.split(" ");
				
				//We write the state of the party to a file.
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
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				//The message to be sent is the last value of the OCaml output
				String msg = output[output.length-1];
				
				//Sends the message to party 1
				network.send(1, msg);
				break;
			case 1:
				//Expecting message from party 1
				network.expectInputFromPlayer(1);
				break;
			case 2:
				
				Serializable inmsg = "";
				
				//Receives the message of party 1
				inmsg = network.receive(1);
								
				//Writes the input of the execution to a file
				FileWriter p2stage2 = null;
				try {
					p2stage2 = new FileWriter("p2stage2.dat");
					p2stage2.write(inmsg + "\n");
					p2stage2.close();
				} catch (IOException e3) {
					// TODO Auto-generated catch block
					e3.printStackTrace();
				}
				
				//OCaml execution
				command = "./p2_stage2.byte";
								
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
				 * OCaml outputs a series of values separated by spaces " ".
				 * This divides the output into 'state' and 'message'.
				 * */
				output = line.split(" ");
			
				//Sends a series of messages to party 1
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
				//Expects message from party 1
				network.expectInputFromPlayer(1);
				break;
			case 4:
				//Receives the dummy message from party 2
				inmsg = "";
				inmsg = network.receive(1);
				
				//Signals that the protocol is over
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
