package ebot.ebs;

import ebot.Context;
import ebot.EBot;

@FunctionalInterface
public interface NativeCall {
	public void run(EBot bot, WorldInterface wi, String in, Context c, EBSScript s);
}
