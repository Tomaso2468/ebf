package ebot.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileDataSource implements DataSource {
	private static final long serialVersionUID = -4169019306642285472L;
	private final File f;
	public FileDataSource(File f) {
		this.f = f.getAbsoluteFile();
	}
	public FileDataSource(String f) {
		this(new File(f));
	}
	@Override
	public InputStream openStream() throws IOException {
		return new FileInputStream(f);
	}

	@Override
	public DataSource getNearby(String name) {
		return new FileDataSource(new File(f.getParentFile(), name));
	}

}
