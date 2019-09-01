package ebot.ebs;

import java.util.Locale;

public interface WorldInterface {
	public Locale getLocale();

	public void couldNotFind();

	public void out(String s);
}
