package ebot.text;

public class BasicTextComparer implements TextComparer {

	@Override
	public double compare(String a, String b) {
		double score = 0;
		
		if (a.equals(b)) {
			score += 1;
		}
		if (a.equalsIgnoreCase(b)) {
			score += 0.5;
		}
		
		return score;
	}

}
