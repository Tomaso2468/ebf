package ebot.ebs.cmd;

import java.io.IOException;

import ebot.Context;
import ebot.EBot;
import ebot.ebs.EBSScript;
import ebot.ebs.WorldInterface;

public class CTrain implements CMD {
	private final String id;
	
	public CTrain(String id) {
		super();
		this.id = id;
	}

	@Override
	public void run(EBot bot, WorldInterface wi, String in, Context c, EBSScript s) {
		try {
			bot.getTextProcessor(id).train();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
