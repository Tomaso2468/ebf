package ebot.ebs.cmd;

import ebot.Context;
import ebot.EBot;
import ebot.ebs.EBSScript;
import ebot.ebs.WorldInterface;

public interface CMD {
	public void run(EBot bot, WorldInterface wi, String in, Context c, EBSScript s);
}
