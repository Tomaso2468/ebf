package ebot.ebs.cmd;

import ebot.Context;
import ebot.EBot;
import ebot.ebs.EBSReturn;
import ebot.ebs.EBSScript;
import ebot.ebs.WorldInterface;

public class CCall implements CMD {
	private final String f;
	
	public CCall(String f) {
		super();
		this.f = f;
	}

	@Override
	public void run(EBot bot, WorldInterface wi, String in, Context c, EBSScript s) {
		try {
			s.functions.get(f).run(bot, wi, in, c, s);
		} catch (EBSReturn r) {
			// Returns end at this point.
			return;
		}
	}

}
