package ebot.ebs.cmd;

import ebot.Context;
import ebot.EBot;
import ebot.ebs.EBSScript;
import ebot.ebs.WorldInterface;

public class CContext implements CMD {
	protected final String var2;
	
	public CContext(String var2) {
		super();
		this.var2 = var2;
	}

	@Override
	public void run(EBot bot, WorldInterface wi, String in, Context c, EBSScript s) {
		c.setContext(var2);
	}
}
