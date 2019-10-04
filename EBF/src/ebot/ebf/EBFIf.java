package ebot.ebf;

import java.util.List;

import ebot.Context;
import ebot.EBot;
import ebot.IfMode;

public class EBFIf extends EBFExpand {
	protected final IfMode m;
	protected final boolean invert;
	protected final String var;
	protected final String var2;

	public EBFIf(List<EBFText> inputs, IfMode m, boolean invert, String var, String var2) {
		super(inputs);
		this.m = m;
		this.invert = invert;
		this.var = var;
		this.var2 = var2;
	}

	@Override
	public boolean evaluate(EBot bot, Context c) {
		boolean b;
		if (m == null) {
			return false;
		}
		switch(m) {
		case EQUAL:
			b = Double.parseDouble(bot.get(var, c)) == Double.parseDouble(bot.get(var2, c));
			break;
		case EQUAL_TEXT:
			b = bot.get(var, c).equals(bot.get(var2, c));
			break;
		case EQUAL_TEXT_CASE:
			b = bot.get(var, c).equalsIgnoreCase(bot.get(var2, c));
			break;
		case GREATER:
			b = Double.parseDouble(bot.get(var, c)) > Double.parseDouble(bot.get(var2, c));
			break;
		case GREATER_EQUAL:
			b = Double.parseDouble(bot.get(var, c)) >= Double.parseDouble(bot.get(var2, c));
			break;
		case LESS:
			b = Double.parseDouble(bot.get(var, c)) < Double.parseDouble(bot.get(var2, c));
			break;
		case LESS_EQUAL:
			b = Double.parseDouble(bot.get(var, c)) <= Double.parseDouble(bot.get(var2, c));
			break;
		default:
			throw new IllegalArgumentException("Unknown if mode: " + m);
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
		sb.append(m);
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
