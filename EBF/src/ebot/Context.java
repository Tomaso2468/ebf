package ebot;

import java.util.Map.Entry;
import java.util.Set;

import ebot.ebf.EBFIO;

public interface Context {
	public int getCount(EBFIO io);
	public void addCount(EBFIO io);
	public String getContext();
	public void setContext(String c);
	public String getUser();
	public Set<Entry<String, Boolean>> getInputProperties();
	public void setInputProperty(String id, boolean v);
	public void clearInputProperties();
}
