package ebot.text;

public class ComplexTextComparer implements TextComparer {

	@Override
	public double compare(String a, String b) {
		double d = 0;

		if (a.equals(b)) {
			return Double.MAX_VALUE;
		}
		if (a.equalsIgnoreCase(b)) {
			return Double.MAX_VALUE / 2;
		}

		String a2 = a.toLowerCase().replace("?", "").replace("!", " ").replace(".", "").replace(",", "")
				.replace("\"", "").trim();
		String b2 = b.toLowerCase().replace("?", "").replace("!", " ").replace(".", "").replace(",", "")
				.replace("\"", "").trim();

		if (a2.equals(b2)) {
			return Double.MAX_VALUE / 3;
		}
		if (a2.equalsIgnoreCase(b2)) {
			return Double.MAX_VALUE / 4;
		}

		int c = 0;
		for (String s : a2.split(" ")) {
			for (String s2 : b2.split(" ")) {
				if(s.equals(s2) && s.length() == 1 && (s.charAt(0) == '/' || s.charAt(0) == '*' || s.charAt(0) == '+' || s.charAt(0) == '-')) {
					d += 10;
				}
				d += equalEachChar(s, s2);
				c += 1;
			}
		}
		
		boolean num = false;
		for(char ch : a.toCharArray()) {
			if (Character.isDigit(ch)) {
				num = true;
			}
		}
		
		if (num) {
			for (String s : a2.split(" ")) {
				for (String s2 : b2.split(" ")) {
					try {
						if (Double.parseDouble(s) == Double.parseDouble(s2)) {
							d += 1;
						}
					} catch (NumberFormatException e) {
					}
				}
			}
		}

		d /= Math.sqrt(calculate(a2, b2) + 1);
		d /= Math.sqrt(calculate(a, b) + 1);

		return d / c;
	}

	protected static int calculate(String x, String y) {
		int[][] dp = new int[x.length() + 1][y.length() + 1];

		for (int i = 0; i <= x.length(); i++) {
			for (int j = 0; j <= y.length(); j++) {
				if (i == 0) {
					dp[i][j] = j;
				} else if (j == 0) {
					dp[i][j] = i;
				} else {
					dp[i][j] = min(dp[i - 1][j - 1] + costOfSubstitution(x.charAt(i - 1), y.charAt(j - 1)),
							dp[i - 1][j] + 1, dp[i][j - 1] + 1);
				}
			}
		}

		return dp[x.length()][y.length()];
	}

	private static int min(int... ints) {
		int min = ints[0];

		for (int i : ints) {
			if (i < min) {
				min = i;
			}
		}

		return min;
	}

	protected static int costOfSubstitution(char c1, char c2) {
		return 1;
	}

	protected int equalEachChar(String s1, String s2) {
		if (s1 == null) {
			s1 = "null";
		}
		if (s2 == null) {
			s2 = "null";
		}
		if (s1.length() == 0 || s2.length() == 0) {
			return 0;
		}
		int c = 0;
		for (int i = 0; i < Math.min(s1.length(), s2.length()); i++) {
			if (s1.charAt(i) == s2.charAt(i)) {
				c += 1;
			}
		}
		return c / Math.min(s1.length(), s2.length());
	}

	public static void main(String[] args) {
		System.out.println(new ComplexTextComparer().compare("Hello!", "Hello"));
	}

}
