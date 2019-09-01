package ebot.ebs.cmd;

import java.io.IOException;

import ebot.Context;
import ebot.EBot;
import ebot.ebs.EBSScript;
import ebot.ebs.WorldInterface;

public class CTrainQuick implements CMD {
private final String id;
	
	public CTrainQuick(String id) {
		super();
		this.id = id;
	}

	@Override
	public void run(EBot bot, WorldInterface wi, String in, Context c, EBSScript s) {
		try {
			bot.getTextProcessor(id).quickTrain();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
