package ebot;

import ebot.ebs.WorldInterface;

public interface InputFilter {
	public boolean check(String in);
	public String getGroup();
	public default void onFilter(WorldInterface wi) {
		
	}
}
