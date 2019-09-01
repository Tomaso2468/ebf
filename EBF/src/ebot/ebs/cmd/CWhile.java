package ebot.ebs.cmd;

import ebot.Context;
import ebot.EBot;
import ebot.IfMode;
import ebot.ebs.CMDList;
import ebot.ebs.EBSScript;
import ebot.ebs.WorldInterface;

public class CWhile implements CMD {
	public final CMDList code = new CMDList();

	protected final IfMode m;
	protected final String var;
	protected final String var2;

	public CWhile(IfMode m, String var, String var2) {
		super();
		this.m = m;
		this.var = var;
		this.var2 = var2;
	}

	@Override
	public void run(EBot bot, WorldInterface wi, String in, Context c, EBSScript s) {
		while(true) {
			boolean b;
			switch (m) {
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
			
			if (!b) {
				return;
			}

			if (b) {
				code.run(bot, wi, in, c, s);
			}
		}
	}
}
