package ebot.ebs.cmd;

import ebot.Context;
import ebot.EBot;
import ebot.ebs.EBSScript;
import ebot.ebs.WorldInterface;

public class CDiv implements CMD {
	protected final String var1;
	protected final String var2;
	
	public CDiv(String var1, String var2) {
		super();
		this.var1 = var1;
		this.var2 = var2;
	}

	@Override
	public void run(EBot bot, WorldInterface wi, String in, Context c, EBSScript s) {
		bot.set(var1, Double.parseDouble(bot.get(var1, c)) / Double.parseDouble(bot.get(var2, c)) + "");
	}
}
