package ebot.ebs.cmd;

import ebot.Context;
import ebot.EBot;
import ebot.ebs.EBSScript;
import ebot.ebs.WorldInterface;

public class COut implements CMD {
	private final String text;
	
	public COut(String text) {
		super();
		this.text = text;
	}

	@Override
	public void run(EBot bot, WorldInterface wi, String in, Context c, EBSScript s) {
		wi.out(bot.replaceVars(text, in, c));
	}
}
