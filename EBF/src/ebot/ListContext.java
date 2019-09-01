package ebot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import ebot.ebf.EBFIO;

public class ListContext implements Context {
	private final List<EBFIO> ios = new ArrayList<>();
	private String c = "none";
	private final String name;
	private final Map<String, Boolean> properties = new HashMap<>();
	
	public ListContext(String name) {
		super();
		this.name = name;
	}

	@Override
	public int getCount(EBFIO io) {
		int count = 0;
		
		for (EBFIO io2 : ios) {
			if (io2 == io) {
				count += 1;
			}
		}
		
		return count;
	}

	@Override
	public void addCount(EBFIO io) {
		ios.add(io);
	}

	@Override
	public String getContext() {
		return c;
	}

	@Override
	public void setContext(String c) {
		this.c = c;
	}

	@Override
	public String getUser() {
		return name;
	}

	@Override
	public Set<Entry<String, Boolean>> getInputProperties() {
		return properties.entrySet();
	}

	@Override
	public void setInputProperty(String id, boolean v) {
		properties.put(id, v);
	}

	@Override
	public void clearInputProperties() {
		properties.clear();
	}

}
