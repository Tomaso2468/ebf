package ebot.ebf;

import java.util.Properties;

public class FinalEBFTree extends EBFTree {
	protected Properties info = new Properties();
	
	@Override
	public String get(String var) {
		return variables.getProperty(var, "%" + var + "%");
	}
}
