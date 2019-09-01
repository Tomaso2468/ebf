package ebot.ebs.cmd;

import ebot.Context;
import ebot.EBot;
import ebot.ebs.EBSScript;
import ebot.ebs.WorldInterface;

public class CLog implements CMD {
	private final String text;
	
	public CLog(String text) {
		super();
		this.text = text;
	}

	@Override
	public void run(EBot bot, WorldInterface wi, String in, Context c, EBSScript s) {
		System.out.println(bot.replaceVars(text, in, c));
	}
}
