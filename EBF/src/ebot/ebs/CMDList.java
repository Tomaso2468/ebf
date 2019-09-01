package ebot.ebs;

import java.util.ArrayList;

import ebot.Context;
import ebot.EBot;
import ebot.ebs.cmd.CMD;

public class CMDList extends ArrayList<CMD> {
	private static final long serialVersionUID = -3710846228569806025L;

	public void run(EBot bot, WorldInterface wi, String in, Context c, EBSScript s) {
		for (CMD cmd : this) {
			cmd.run(bot, wi, in, c, s);
		}
	}
}
