package ebot.ebf;

import java.util.List;

import ebot.Context;
import ebot.EBot;

public abstract class EBFExpand extends EBFText {
	protected final List<EBFText> texts;
	public EBFExpand(List<EBFText> inputs) {
		super(EBFExpand.class.toString());
		this.texts = inputs;
	}

	public abstract boolean evaluate(EBot bot, Context c);
	
	public List<EBFText> getTexts() {
		return texts;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("EBFExpand{");
		sb.append("\n");
		
		sb.append("data=");
		sb.append(texts.toString());
		
		sb.append("\n");
		sb.append("}");
		
		return sb.toString();
	}
}
