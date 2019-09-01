package ebot.ebf;

import java.util.ArrayList;
import java.util.List;

public class EBFGroup {
	final String name;
	List<EBFIO> ios = new ArrayList<>();
	List<EBFGroup> subgroups = new ArrayList<>();
	boolean override;
	
	public EBFGroup(String name) {
		this.name = name;
	}
	
	public List<EBFIO> getIOs() {
		return ios;
	}
	public List<EBFGroup> getSubGroups() {
		return subgroups;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("EBFGroup{");
		sb.append("\n");
		
		sb.append("name=");
		sb.append(name);
		sb.append(", \n");
		
		sb.append("ios=");
		sb.append(ios.toString());
		sb.append(", \n");
		
		sb.append("subgroups=");
		sb.append(subgroups.toString());
		sb.append(", \n");
		
		sb.append("override=");
		sb.append(override);
		
		sb.append("\n");
		sb.append("}");
		
		return sb.toString();
	}

	public String getName() {
		return name;
	}
}
