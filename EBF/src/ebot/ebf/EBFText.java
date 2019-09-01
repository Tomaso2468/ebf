package ebot.ebf;

public class EBFText {
	private final String text;
	
	public EBFText(String input) {
		super();
		this.text = input;
	}

	public String getText() {
		return text;
	}
	
	@Override
	public String toString() {
		return "\"" + text + "\"";
	}
}
