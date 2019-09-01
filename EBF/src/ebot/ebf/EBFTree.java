package ebot.ebf;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import ebot.IfMode;
import ebot.ebs.EBSScript;
import ebot.ebs.EBSScript.EBSCompile;
import ebot.io.DataSource;
import ebot.io.FileDataSource;

public class EBFTree {
	public FinalEBFTree finals;

	public Properties variables = new Properties();

	public List<EBSScript> bgScripts = new ArrayList<EBSScript>();
	public List<EBSScript> loadScripts = new ArrayList<EBSScript>();
	public List<EBSScript> connectScripts = new ArrayList<EBSScript>();
	public List<EBSScript> postCall = new ArrayList<EBSScript>();
	public List<EBSScript> preCall = new ArrayList<EBSScript>();

	public EBFGroup root;

	public String get(String var) {
		return ((EBFTree) finals).variables.getProperty(var, variables.getProperty(var, "%" + var + "%"));
	}

	public void loadEBI(DataSource ds) throws IOException {
		loadEBI(ds.openStream());
	}

	public void loadEBI(InputStream in) throws IOException {
		variables.load(in);
		in.close();
	}

	public void saveEBI(OutputStream out) throws IOException {
		variables.store(out, "Auto-generated EBI file.");
		out.close();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append("EBFTree{");
		sb.append("\n");

		sb.append("finals=");
		sb.append(finals + "");
		sb.append(", \n");

		sb.append("vars=");
		sb.append(variables.toString());
		sb.append(", \n");

		sb.append("bgscripts=");
		sb.append(bgScripts.toString());
		sb.append(", \n");

		sb.append("loadscripts=");
		sb.append(loadScripts.toString());
		sb.append(", \n");
		
		sb.append("connectscripts=");
		sb.append(connectScripts.toString());
		sb.append(", \n");

		sb.append("postscripts=");
		sb.append(postCall.toString());
		sb.append(", \n");

		sb.append("prescripts=");
		sb.append(preCall.toString());
		sb.append(", \n");

		sb.append("root=");
		sb.append(root + "");

		sb.append("\n");
		sb.append("}");

		return sb.toString();
	}

	public static EBFTree parse(DataSource ds) throws IOException {
		List<String> data = expand(ds);

		int i = 0;
		for (String string : data) {
			System.out.println(i + ": " + string);
			
			i += 1;
		}

		EBFTree tree = new EBFTree();
		tree.finals = new FinalEBFTree();

		compile(data, 0, tree);

//		System.out.println(tree);

		return tree;
	}

	public static void main(String[] args) throws IOException {
		parse(new FileDataSource("ebf.ebf"));
	}

	public static void compile(List<String> data, int index, EBFTree tree) throws IOException {
//		System.out.println("Compiling at " + index);

		tree.root = new EBFGroup("root");

		if (data.get(index).equals("ebf {")) {
			index += 1;
			while (index < data.size()) {
				String line = data.get(index);
//				System.out.println(index + ": " + line);
				index += 1;

				if (line.equals("final {")) {
					index = compileFinal(data, index, tree);
				}
				if (line.equals("vars {")) {
					index = compileVars(data, index, tree);
				}
				if (line.equals("emotions {")) {
					index = compileVars(data, index, tree);
				}
				if (line.equals("text {")) {
					index = compileText(data, index, tree, tree.root);
				}
			}
		} else {
			throw new IOException("EBF data missing header.");
		}

//		System.out.println("Exiting compile");
	}

	public static int compileFinal(List<String> data, int index, EBFTree tree) throws IOException {
//		System.out.println("Compiling final area at " + index);

		while (index < data.size()) {
			String line = data.get(index);
//			System.out.println(index + ": " + line);

			index += 1;

			if (line.equals("info {")) {
				index = compileInfo(data, index, tree);
			} else if (line.equals("vars {")) {
				index = compileVars(data, index, tree.finals);
			} else if (line.equals("scripts {")) {
				index = compileBGScripts(data, index, tree);
			} else if (line.equals("onload {")) {
				index = compileLoadScripts(data, index, tree);
			} else if (line.equals("onconnect {")) {
				index = compileConnectScripts(data, index, tree);
			} else if (line.equals("post {")) {
				index = compilePost(data, index, tree);
			} else if (line.equals("pre {")) {
				index = compilePre(data, index, tree);
			} else if (line.equals("}")) {
				break;
			} else {
				System.err.println(line + " is invalid: " + (index - 1));
			}
		}

//		System.out.println("Exiting compileFinal");

		return index;
	}

	public static int compileInfo(List<String> data, int index, EBFTree tree) throws IOException {
//		System.out.println("Compiling info area at " + index);

		while (index < data.size()) {
			String line = data.get(index).replace("= ", "=").replace(" =", "=").replace(" = ", "=");
//			System.out.println(index + ": " + line);

			index += 1;

			if (line.equals("}")) {
				break;
			}

			String[] strs = line.split("=");

			tree.finals.info.setProperty(strs[0], strs[1]);
		}

//		System.out.println("Exiting compileInfo");

		return index;
	}

	public static int compileVars(List<String> data, int index, EBFTree tree) throws IOException {
//		System.out.println("Compiling data area at " + index);

		while (index < data.size()) {
			String line = data.get(index).replace("= ", "=").replace(" =", "=").replace(" = ", "=");
//			System.out.println(index + ": " + line);

			index += 1;

			if (line.equals("}")) {
				break;
			}

			String[] strs = line.split("=");

			if (strs[1].contains("/")) {
				String[] fraction = strs[1].split("/");

				strs[1] = (Double.parseDouble(fraction[0]) / Double.parseDouble(fraction[1])) + "";
			}

			tree.variables.setProperty(strs[0], strs[1]);
		}

//		System.out.println("Exiting compileVars");

		return index;
	}

	public static int compileBGScripts(List<String> data, int index, EBFTree tree) throws IOException {
//		System.out.println("Compiling background scripts at " + index);

		while (index < data.size()) {
			String line = data.get(index);
//			System.out.println(index + ": " + line);

			index += 1;

			if (line.equals("}")) {
				break;
			}

			if (line.equals("ebs {")) {
				EBSCompile c = EBSScript.compileScript(data, index, tree);
				index = c.index;
				tree.bgScripts.add(c.script);
				c.script.compile();
			}
		}

//		System.out.println("Exiting compileBG");

		return index;
	}

	public static int compileLoadScripts(List<String> data, int index, EBFTree tree) throws IOException {
//		System.out.println("Compiling loading scripts at " + index);

		while (index < data.size()) {
			String line = data.get(index);
//			System.out.println(index + ": " + line);

			index += 1;

			if (line.equals("}")) {
				break;
			}

			if (line.equals("ebs {")) {
				EBSCompile c = EBSScript.compileScript(data, index, tree);
				index = c.index;
				tree.loadScripts.add(c.script);
				c.script.compile();
			}
		}

//		System.out.println("Exiting compileLoad");

		return index;
	}
	
	public static int compileConnectScripts(List<String> data, int index, EBFTree tree) throws IOException {
//		System.out.println("Compiling loading scripts at " + index);

		while (index < data.size()) {
			String line = data.get(index);
//			System.out.println(index + ": " + line);

			index += 1;

			if (line.equals("}")) {
				break;
			}

			if (line.equals("ebs {")) {
				EBSCompile c = EBSScript.compileScript(data, index, tree);
				index = c.index;
				tree.connectScripts.add(c.script);
				c.script.compile();
			}
		}

//		System.out.println("Exiting compileLoad");

		return index;
	}

	public static int compilePost(List<String> data, int index, EBFTree tree) throws IOException {
//		System.out.println("Compiling post scripts at " + index);

		while (index < data.size()) {
			String line = data.get(index);

			index += 1;

			if (line.equals("}")) {
				break;
			}

			if (line.equals("ebs {")) {
				EBSCompile c = EBSScript.compileScript(data, index, tree);
				index = c.index;
				tree.postCall.add(c.script);
				c.script.compile();
			}
		}

//		System.out.println("Exiting compilePost");

		return index;
	}

	public static int compilePre(List<String> data, int index, EBFTree tree) throws IOException {
//		System.out.println("Compiling pre scripts at " + index);

		while (index < data.size()) {
			String line = data.get(index);

			index += 1;

			if (line.equals("}")) {
				break;
			}

			if (line.equals("ebs {")) {
				EBSCompile c = EBSScript.compileScript(data, index, tree);
				index = c.index;
				tree.preCall.add(c.script);
			}
		}

//		System.out.println("Exiting compilePre");

		return index;
	}

	public static int compileText(List<String> data, int index, EBFTree tree, EBFGroup g) throws IOException {
//		System.out.println("Compiling text area at " + index);

		while (index < data.size()) {
			String line = data.get(index);

			System.out.println(index + "@" + line);
			
			index += 1;

			if (line.equals("}")) {
				break;
			} else if (line.startsWith("lang:")) {
				String lang = line.split(" ")[0].split(":")[1];

				EBFGroup g2 = new EBFGroup("lang:" + lang);
				g2.override = true;
				g.subgroups.add(g2);

				index = compileText(data, index, tree, g2);
			} else if (line.startsWith("region:")) {
				String region = line.split(" ")[0].split(":")[1];

				EBFGroup g2 = new EBFGroup("region:" + region);
				g2.override = true;
				g.subgroups.add(g2);

				index = compileText(data, index, tree, g2);
			} else if (line.startsWith("g:")) {
				String group = line.split(" ")[0].split(":")[1];

				EBFGroup g2 = new EBFGroup("g:" + group);
				g2.override = true;
				g.subgroups.add(g2);

				index = compileText(data, index, tree, g2);
			} else if (line.equals("io {")) {
				index = compileIO(data, index, tree, g);
			} else if (line.equals("post {")) {
				index = compilePost(data, index, tree);
			} else if (line.equals("pre {")) {
				index = compilePre(data, index, tree);
			} else {
				System.err.println("Invalid syntax: " + line + " @ " + index);
			}
		}

//		System.out.println("Exiting compileText");

		return index;
	}

	public static int compileIO(List<String> data, int index, EBFTree tree, EBFGroup g) throws IOException {
//		System.out.println("Compiling input/output at " + index);

		List<EBFText> in = new ArrayList<>();
		List<EBFText> out = new ArrayList<>();
		List<EBFText> out2 = new ArrayList<>();
		List<EBFText> out3 = new ArrayList<>();
		List<EBSScript> scripts = new ArrayList<>();

		while (index < data.size()) {
			String line = data.get(index);
//			System.out.println(index + ": " + line);

			index += 1;

			if (line.equals("}")) {
				break;
			}

			if (line.equals("in {")) {
				index = compileIOInternal(data, index, tree, in);
			}

			if (line.equals("out {")) {
				index = compileIOInternal(data, index, tree, out);
			}

			if (line.equals("out2 {")) {
				index = compileIOInternal(data, index, tree, out2);
			}

			if (line.equals("out3 {")) {
				index = compileIOInternal(data, index, tree, out3);
			}

			if (line.equals("scripts {")) {
				while (index < data.size()) {
					String line2 = data.get(index);

					index += 1;

					if (line2.equals("}")) {
						break;
					}

					if (line2.equals("ebs {")) {
						EBSCompile c = EBSScript.compileScript(data, index, tree);
						index = c.index;
						scripts.add(c.script);
						c.script.compile();
					}
				}
			}
		}

		g.ios.add(new EBFIO(in.toArray(new EBFText[in.size()]), out.toArray(new EBFText[out.size()]),
				out2.toArray(new EBFText[out2.size()]), out3.toArray(new EBFText[out3.size()]),
				scripts.toArray(new EBSScript[scripts.size()])));

//		System.out.println("Exiting compileIO");

		return index;
	}

	public static int compileIOInternal(List<String> data, int index, EBFTree tree, List<EBFText> text) {
		System.out.println("Compiling io data at " + index);

		while (index < data.size()) {
			String line = data.get(index);
			System.out.println(index + ": " + line);

			index += 1;

			if (line.startsWith("}")) {
				if (line.equals("} else {")) {
					index -= 1;
				}
				break;
			}
			
			if (line.startsWith("context ")) {
				String[] ctext = line.split(" ");
				
				List<EBFText> code = new ArrayList<>();
				text.add(new EBFIfAbsolute(code, IfMode.EQUAL_TEXT, false, "context", ctext[1]));

				index = compileIOInternal(data, index, tree, code);
			} else if (line.startsWith("if ")) {
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

				List<EBFText> code = new ArrayList<>();
				if (var2.contains("%")) {
					text.add(new EBFIf(code, m, false, var1.replace("%", ""), var2.replace("%", "")));
				} else {
					text.add(new EBFIfAbsolute(code, m, false, var1.replace("%", ""), var2.replace("%", "")));
				}

				index = compileIOInternal(data, index, tree, code);

				String line2 = data.get(index);
//				System.out.println(index + ": " + line);

				if (line2.equals("} else {")) {
					index += 1;

					code = new ArrayList<>();
					if (var2.contains("%")) {
						text.add(new EBFIf(code, m, true, var1, var2));
					} else {
						text.add(new EBFIfAbsolute(code, m, true, var1, var2));
					}

					index = compileIOInternal(data, index, tree, code);
				}
			} else {
				text.add(new EBFText(line));
			}
		}

//		System.out.println("Exiting compileIOInternal");

		return index;
	}

	public static List<String> expand(DataSource ds) throws IOException {
		Scanner s = new Scanner(ds.openStream());

		String firstLine = s.nextLine();

		while (trim(firstLine).startsWith("#") || trim(firstLine).isEmpty()) {
			firstLine = s.nextLine();
		}

		List<String> data = new ArrayList<String>();

		if (trim(firstLine).equals("ebf {")) {
			data.add(trim(firstLine));

			expandBasic(data, s, ds);

			s.close();

			return data;
		} else {
			s.close();
			throw new IOException("EBF file missing header.");
		}
	}

	public static void expandBasic(List<String> data, Scanner s, DataSource ds) throws IOException {
		int level = 0;

		while (s.hasNextLine()) {
			String line = trim(s.nextLine());

			if (line.startsWith("#") || line.isEmpty()) {
				continue;
			}

			if (line.equals("}")) {
				data.add(line);
				if (level > 0) {
					level -= 1;
				} else {
					return;
				}
			}

			switch (line) {
			case "final {":
				data.add(line);
				level += 1;
				break;
			case "scripts {":
				data.add(line);
				expandScripts(data, s, ds);
				break;
			case "onload {":
				data.add(line);
				expandScripts(data, s, ds);
				break;
			case "onconnect {":
				data.add(line);
				expandScripts(data, s, ds);
				break;
			case "vars {":
				data.add(line);
				expandVars(data, s, ds);
				break;
			case "info {":
				data.add(line);
				expandVars(data, s, ds);
				break;
			case "emotions {":
				data.add(line);
				expandVars(data, s, ds);
				break;
			case "text {":
				data.add(line);
				level += 1;
				break;
			case "io {":
				data.add(line);
				level += 1;
				break;
			case "file {":
				String fileName = trim(s.nextLine());

				DataSource ds2 = ds.getNearby(fileName);
				Scanner s2 = new Scanner(ds2.openStream());

				expandBasic(data, s2, ds2);
				data.remove(data.size() - 1);

				s2.close();

				s.nextLine();
				break;
			case "in {":
				data.add(line);
				expandIn(data, s, ds);
				break;
			case "out {":
				data.add(line);
				expandIn(data, s, ds);
				break;
			case "out2 {":
				data.add(line);
				expandIn(data, s, ds);
				break;
			case "out3 {":
				data.add(line);
				expandIn(data, s, ds);
				break;
			case "post {":
				data.add(line);
				expandScripts(data, s, ds);
				break;
			case "pre {":
				data.add(line);
				expandScripts(data, s, ds);
				break;
			}

			if (line.startsWith("lang:")) {
				data.add(line);
				level += 1;
			}
			if (line.startsWith("region:")) {
				data.add(line);
				level += 1;
			}
			if (line.startsWith("g:")) {
				data.add(line);
				level += 1;
			}

//			System.out.println(line + " -> " + level);
		}
	}

	public static void expandScripts(List<String> data, Scanner s, DataSource ds) throws IOException {
		while (s.hasNextLine()) {
			String line = trim(s.nextLine());

			if (line.startsWith("#") || line.isEmpty()) {
				continue;
			}

			if (line.equals("}")) {
				data.add(line);
				return;
			}

			switch (line) {
			case "ebs {":
				data.add(line);
				EBSScript.expandEBS(data, s);
				break;
			case "file {":
				String fileName = trim(s.nextLine());

				DataSource ds2 = ds.getNearby(fileName);
				Scanner s2 = new Scanner(ds2.openStream());

				data.add("ebs {");
				EBSScript.expandEBSFile(data, s2);
				data.add("}");

				s2.close();

				s.nextLine();
				break;
			}
		}
	}

	public static void expandVars(List<String> data, Scanner s, DataSource ds) throws IOException {
		while (s.hasNextLine()) {
			String line = trim(s.nextLine());

			if (line.startsWith("#") || line.isEmpty()) {
				continue;
			}

			if (line.equals("}")) {
				data.add(line);
				return;
			}

			switch (line) {
			case "vars {":
				data.add(line);
				expandVars(data, s, ds);
				break;
			case "file {":
				String fileName = trim(s.nextLine());

				DataSource ds2 = ds.getNearby(fileName);
				Scanner s2 = new Scanner(ds2.openStream());

				expandVars(data, s2, ds2);

				s2.close();

				s.nextLine();
				break;
			default:
				data.add(line);
				break;
			}
		}
	}

	public static void expandIn(List<String> data, Scanner s, DataSource ds) throws IOException {
		int level = 0;
		while (s.hasNextLine()) {
			String line = trim(s.nextLine());

//			System.out.println(level + " -> " + line);

			if (line.startsWith("#") || line.isEmpty()) {
				continue;
			}

			if (line.equals("file {")) {
				String fileName = trim(s.nextLine());

				DataSource ds2 = ds.getNearby(fileName);
				Scanner s2 = new Scanner(ds2.openStream());

				expandIn(data, s2, ds2);

				s2.close();

				s.nextLine();
				break;
			} else {
				data.add(line);
				if (line.startsWith("}")) {
					level -= 1;

					if (level == -1) {
						return;
					}
				}

				if (line.endsWith("{")) {
					level += 1;
					continue;
				}
			}
		}
	}

	public static String trim(String input) {
		return input.trim().replaceAll("(\\s)+", " ");
	}
}
