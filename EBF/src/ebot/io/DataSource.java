package ebot.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

public interface DataSource extends Serializable {
	public InputStream openStream() throws IOException;
	public DataSource getNearby(String name);
}
