package ebot.ebs.cmd;

import ebot.Context;
import ebot.EBot;
import ebot.ebs.CMDList;
import ebot.ebs.EBSScript;
import ebot.ebs.WorldInterface;

public class CRepeat implements CMD {
	public final CMDList code = new CMDList();
	public final int count;

	public CRepeat(int count) {
		super();
		this.count = count;
	}

	@Override
	public void run(EBot bot, WorldInterface wi, String in, Context c, EBSScript s) {
		for (int i = 0; i < count; i++) {
			code.run(bot, wi, in, c, s);
		}
	}

}
