package ebot.ebs.cmd;

import ebot.Context;
import ebot.EBot;
import ebot.ebs.EBSScript;
import ebot.ebs.WorldInterface;

public class CLn implements CMD {

	@Override
	public void run(EBot bot, WorldInterface wi, String in, Context c, EBSScript s) {
		wi.out("");
	}

}
