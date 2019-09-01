package ebot.ebs.cmd;

import ebot.Context;
import ebot.EBot;
import ebot.ebs.EBSReturn;
import ebot.ebs.EBSScript;
import ebot.ebs.WorldInterface;

public class CReturn implements CMD {

	@Override
	public void run(EBot bot, WorldInterface wi, String in, Context c, EBSScript s) {
		throw new EBSReturn();
	}

}
