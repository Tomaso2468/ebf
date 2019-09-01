package ebot.ebs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import ebot.Context;
import ebot.EBot;
import ebot.IfMode;
import ebot.ebf.EBFTree;
import ebot.ebs.cmd.CAdd;
import ebot.ebs.cmd.CAddA;
import ebot.ebs.cmd.CCall;
import ebot.ebs.cmd.CContext;
import ebot.ebs.cmd.CDiv;
import ebot.ebs.cmd.CDivA;
import ebot.ebs.cmd.CIf;
import ebot.ebs.cmd.CIfA;
import ebot.ebs.cmd.CLn;
import ebot.ebs.cmd.CLog;
import ebot.ebs.cmd.CMul;
import ebot.ebs.cmd.CMulA;
import ebot.ebs.cmd.CNative;
import ebot.ebs.cmd.COut;
import ebot.ebs.cmd.CRepeat;
import ebot.ebs.cmd.CReturn;
import ebot.ebs.cmd.CSet;
import ebot.ebs.cmd.CSetA;
import ebot.ebs.cmd.CSub;
import ebot.ebs.cmd.CSubA;
import ebot.ebs.cmd.CTrain;
import ebot.ebs.cmd.CTrainQuick;
import ebot.ebs.cmd.CWhile;
import ebot.ebs.cmd.CWhileA;

public class EBSScript {
	protected List<String> lines;
	public final Map<String, CMDList> functions = new HashMap<String, CMDList>();
	protected CMDList cmds = new CMDList();
	
	public EBSScript(List<String> lines) {
		super();
		this.lines = lines;
	}

	public static void expandEBS(List<String> data, Scanner s) {
		int level = 0;
		while(s.hasNextLine()) {
			String line = s.nextLine().trim();
			
			if(line.startsWith("#") || line.isEmpty()) {
				continue;
			}
			
			data.add(line);
			
			if (line.endsWith("{")) {
				level += 1;
			}
			if (line.startsWith("}")) {
				level -= 1;
			}
			
			if (level == -1) {
				return;
			}
		}
	}

	public static void expandEBSFile(List<String> data, Scanner s) {
		while(s.hasNextLine()) {
			String line = s.nextLine().trim();
			
			if(line.startsWith("#") || line.isEmpty()) {
				continue;
			}
			
			data.add(line);
		}
	}

	public static EBSCompile compileScript(List<String> data, int index, EBFTree tree) {
		List<String> code = new ArrayList<>();
		
		int level = 0;
		while(index < data.size()) {
			String line = data.get(index);
			
			code.add(line);
			
			if (line.endsWith("{")) {
				level += 1;
			}
			if (line.startsWith("}")) {
				level -= 1;
			}
			
			if (level == -1) {
				code.remove(code.size() - 1);
				break;
			}
			
			index += 1;
		}
		
		return new EBSCompile(new EBSScript(code), index);
	}
	
	public static final class EBSCompile {
		public final EBSScript script;
		public final int index;
		
		public EBSCompile(EBSScript script, int index) {
			super();
			this.script = script;
			this.index = index;
		}
	}

	public void run(EBot eBot, WorldInterface wi, String in, Context c) {
		try {
			cmds.run(eBot, wi, in, c, this);
		} catch (EBSReturn r) {
			return;
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
	}
	
	protected int compile(CMDList cmds, int index) {
		while (index < lines.size()) {
			String line = lines.get(index);
			index += 1;
			
			if (line.endsWith("}")) {
				break;
			}
			if (line.startsWith("out ")) {
				String s = line.substring(line.indexOf(' ') + 1);
				cmds.add(new COut(s));
			}
			if (line.startsWith("log ")) {
				String s = line.substring(line.indexOf(' ') + 1);
				cmds.add(new CLog(s));
			}
			if (line.equals("ln")) {
				cmds.add(new CLn());
			}
			if (line.startsWith("if ")) {
				String[] iftext = line.split(" ");

				String var1 = iftext[1];
				String mode = iftext[2];
				String var2 = iftext[3];

				IfMode m;

				switch (mode) {
				case "=":
					m = IfMode.EQUAL;
					break;
				case ">":
					m = IfMode.GREATER;
					break;
				case "<":
					m = IfMode.LESS;
					break;
				case ">=":
					m = IfMode.GREATER_EQUAL;
					break;
				case "<=":
					m = IfMode.LESS_EQUAL;
					break;
				case "==":
					m = IfMode.EQUAL_TEXT_CASE;
					break;
				case "===":
					m = IfMode.EQUAL_TEXT;
					break;
				default:
					m = null;
					break;
				}

				CIf cif;
				if (var2.contains("%")) {
					cif = new CIf(m, var1.replace("%", ""), var2.replace("%", ""));
				} else {
					cif = new CIfA(m, var1.replace("%", ""), var2);
				}

				index = compile(cif.ifCode, index);

				if (index < lines.size()) {
					String line2 = lines.get(index);

					if (line2.equals("} else {")) {
						index += 1;

						index = compile(cif.elseCode, index);
					}
				}
				
				cmds.add(cif);
			}
			if (line.startsWith("while ")) {
				String[] iftext = line.split(" ");

				String var1 = iftext[1];
				String mode = iftext[2];
				String var2 = iftext[3];

				IfMode m;

				switch (mode) {
				case "=":
					m = IfMode.EQUAL;
					break;
				case ">":
					m = IfMode.GREATER;
					break;
				case "<":
					m = IfMode.LESS;
					break;
				case ">=":
					m = IfMode.GREATER_EQUAL;
					break;
				case "<=":
					m = IfMode.LESS_EQUAL;
					break;
				case "==":
					m = IfMode.EQUAL_TEXT_CASE;
					break;
				case "===":
					m = IfMode.EQUAL_TEXT;
					break;
				default:
					m = null;
					break;
				}

				CWhile cif;
				if (var2.contains("%")) {
					cif = new CWhile(m, var1.replace("%", ""), var2.replace("%", ""));
				} else {
					cif = new CWhileA(m, var1.replace("%", ""), var2);
				}

				index = compile(cif.code, index);
				
				cmds.add(cif);
			}
			if (line.startsWith("def ")) {
				String fName = line.split(" ")[1];
				
				CMDList defCode = new CMDList();
				
				index = compile(defCode, index);
				
				functions.put(fName, defCode);
			}
			if (line.startsWith("repeat ")) {
				CRepeat c = new CRepeat(Integer.parseInt(line.split(" ")[1]));
				
				index = compile(c.code, index);
				
				cmds.add(c);
			}
			if (line.startsWith("call ")) {
				CCall c = new CCall(line.split(" ")[1]);
				
				cmds.add(c);
			}
			if (line.startsWith("native ")) {
				CNative c = new CNative(line.split(" ")[1]);
				
				cmds.add(c);
			}
			if (line.equals("return")) {
				cmds.add(new CReturn());
			}
			if (line.startsWith("add ")) {
				String var1 = line.split(" ")[1];
				String var2 = line.split(" ")[2];
				
				if (var2.contains("%")) {
					cmds.add(new CAdd(var1.replace("%", ""), var2.replace("%", "")));
				} else {
					cmds.add(new CAddA(var1.replace("%", ""), var2.replace("%", "")));
				}
			}
			if (line.startsWith("sub ")) {
				String var1 = line.split(" ")[1];
				String var2 = line.split(" ")[2];
				
				if (var2.contains("%")) {
					cmds.add(new CSub(var1.replace("%", ""), var2.replace("%", "")));
				} else {
					cmds.add(new CSubA(var1.replace("%", ""), var2.replace("%", "")));
				}
			}
			if (line.startsWith("mul ")) {
				String var1 = line.split(" ")[1];
				String var2 = line.split(" ")[2];
				
				if (var2.contains("%")) {
					cmds.add(new CMul(var1.replace("%", ""), var2.replace("%", "")));
				} else {
					cmds.add(new CMulA(var1.replace("%", ""), var2.replace("%", "")));
				}
			}
			if (line.startsWith("div ")) {
				String var1 = line.split(" ")[1];
				String var2 = line.split(" ")[2];
				
				if (var2.contains("%")) {
					cmds.add(new CDiv(var1.replace("%", ""), var2.replace("%", "")));
				} else {
					cmds.add(new CDivA(var1.replace("%", ""), var2.replace("%", "")));
				}
			}
			if (line.startsWith("set ")) {
				String var1 = line.split(" ")[1];
				String var2 = line.split(" ")[2];
				
				if (var2.contains("%")) {
					cmds.add(new CSet(var1.replace("%", ""), var2.replace("%", "")));
				} else {
					cmds.add(new CSetA(var1.replace("%", ""), var2.replace("%", "")));
				}
			}
			if (line.startsWith("context ")) {
				String s = line.substring(line.indexOf(' ') + 1);
				cmds.add(new CContext(s));
			}
			if (line.startsWith("train ")) {
				String s = line.substring(line.indexOf(' ') + 1);
				cmds.add(new CTrain(s));
			}
			if (line.startsWith("trainquick ")) {
				String s = line.substring(line.indexOf(' ') + 1);
				cmds.add(new CTrainQuick(s));
			}
			//TODO Networking
		}
		
		return index;
	}

	public void compile() {
		compile(cmds, 0);
	}
}
