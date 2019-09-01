package ebot.ebf;

import java.util.List;

import ebot.Context;
import ebot.EBot;
import ebot.IfMode;

public class EBFIfAbsolute extends EBFIf {
	public EBFIfAbsolute(List<EBFText> inputs, IfMode m, boolean invert, String var, String var2) {
		super(inputs, m, invert, var, var2);
	}

	@Override
	public boolean evaluate(EBot bot, Context c) {
		boolean b;
		switch(m) {
		case EQUAL:
			b = Double.parseDouble(bot.get(var, c)) == Double.parseDouble(var2);
			break;
		case EQUAL_TEXT:
			b = bot.get(var, c).equals(var2);
			break;
		case EQUAL_TEXT_CASE:
			b = bot.get(var, c).equalsIgnoreCase(var2);
			break;
		case GREATER:
			b = Double.parseDouble(bot.get(var, c)) > Double.parseDouble(var2);
			break;
		case GREATER_EQUAL:
			b = Double.parseDouble(bot.get(var, c)) >= Double.parseDouble(var2);
			break;
		case LESS:
			b = Double.parseDouble(bot.get(var, c)) < Double.parseDouble(var2);
			break;
		case LESS_EQUAL:
			b = Double.parseDouble(bot.get(var, c)) <= Double.parseDouble(var2);
			break;
		default:
			throw new IllegalArgumentException("Unknown if(a) mode: " + m);
		}
		
		return invert ? !b : b;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("EBFIf{");
		sb.append("\n");
		
		sb.append("data=");
		sb.append(texts.toString());
		sb.append(", \n");
		
		sb.append("mode=");
		sb.append(m + " (Absolute)");
		sb.append(", \n");
		
		sb.append("invert=");
		sb.append(invert);
		sb.append(", \n");
		
		sb.append("var1=");
		sb.append(var);
		sb.append(", \n");
		
		sb.append("var2=");
		sb.append(var2);
		
		sb.append("\n");
		sb.append("}");
		
		return sb.toString();
	}
}
