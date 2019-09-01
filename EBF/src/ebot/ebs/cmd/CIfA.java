package ebot.ebs.cmd;

import ebot.Context;
import ebot.EBot;
import ebot.IfMode;
import ebot.ebs.EBSScript;
import ebot.ebs.WorldInterface;

public class CIfA extends CIf {
	public CIfA(IfMode m, String var, String var2) {
		super(m, var, var2);
	}

	@Override
	public void run(EBot bot, WorldInterface wi, String in, Context c, EBSScript s) {
		boolean b;
		switch (m) {
		case EQUAL:
			b = Double.parseDouble(bot.get(var, c)) == Double.parseDouble(var);
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
			throw new IllegalArgumentException("Unknown if mode: " + m);
		}

		if (b) {
			ifCode.run(bot, wi, in, c, s);
		} else {
			elseCode.run(bot, wi, in, c, s);
		}
	}
}
