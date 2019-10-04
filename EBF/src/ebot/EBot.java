package ebot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import ebot.ebf.EBFExpand;
import ebot.ebf.EBFGroup;
import ebot.ebf.EBFIO;
import ebot.ebf.EBFText;
import ebot.ebf.EBFTree;
import ebot.ebn.TextFeatureDetector;
import ebot.ebs.EBSScript;
import ebot.ebs.NativeCall;
import ebot.ebs.WorldInterface;
import ebot.event.GetListener;
import ebot.text.TextComparer;

public class EBot {
	private final EBFTree tree;
	private final List<GetListener> getListeners = new ArrayList<>();
	private TextComparer compare;
	private final Map<String, TextFeatureDetector> detectors = new HashMap<>();
	private final Map<String, NativeCall> calls = new HashMap<>();
	private final List<InputFilter> filters = new ArrayList<>();
	private final List<Replacer> replacers = new ArrayList<>();

	public EBot(EBFTree tree, TextComparer compare) {
		super();
		this.tree = tree;
		this.compare = compare;
	}

	public void addListener(GetListener l) {
		getListeners.add(l);
	}

	public void removeListener(GetListener l) {
		getListeners.remove(l);
	}

	public String get(String var, Context c) {
		for (GetListener getListener : getListeners) {
			getListener.get(this, var);
		}
		return replaceVars("%" + var + "%", "null", c);
	}

	public String getSafe(String var, Context c) {
		for (GetListener getListener : getListeners) {
			getListener.get(this, var);
		}
		return tree.get(var);
	}

	public void load() {
		for (EBSScript s : tree.loadScripts) {
			s.run(this, new WorldInterface() {

				@Override
				public Locale getLocale() {
					return Locale.getDefault();
				}

				@Override
				public void couldNotFind() {

				}

				@Override
				public void out(String s) {
					System.out.println(s);
				}
			}, "null", new Context() {

				@Override
				public int getCount(EBFIO io) {
					return 0;
				}

				@Override
				public void addCount(EBFIO io) {

				}

				@Override
				public String getContext() {
					return "global";
				}

				@Override
				public void setContext(String c) {

				}

				@Override
				public String getUser() {
					return "none";
				}

				@Override
				public Set<Entry<String, Boolean>> getInputProperties() {
					return new HashSet<>();
				}

				@Override
				public void setInputProperty(String id, boolean v) {

				}

				@Override
				public void clearInputProperties() {

				}
			});
		}
	}

	public void background(long gap) {
		for (EBSScript s : tree.bgScripts) {
			s.run(this, new WorldInterface() {

				@Override
				public Locale getLocale() {
					return Locale.getDefault();
				}

				@Override
				public void couldNotFind() {

				}

				@Override
				public void out(String s) {
					System.out.println(s);
				}
			}, "null", new Context() {

				@Override
				public int getCount(EBFIO io) {
					return 0;
				}

				@Override
				public void addCount(EBFIO io) {

				}

				@Override
				public String getContext() {
					return "global";
				}

				@Override
				public void setContext(String c) {

				}

				@Override
				public String getUser() {
					return "none";
				}

				@Override
				public Set<Entry<String, Boolean>> getInputProperties() {
					return new HashSet<>();
				}

				@Override
				public void setInputProperty(String id, boolean v) {

				}

				@Override
				public void clearInputProperties() {

				}
			});

			try {
				Thread.sleep(gap);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void onConnect(WorldInterface wi, Context c) {
		for (EBSScript s : tree.connectScripts) {
			s.run(this, wi, "none", c);
		}
	}

	public void startBackground(long gap, long gap2) {
		while (true) {
			background(gap2);

			try {
				Thread.sleep(gap);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private class DoneCounter {
		private boolean done = false;
	}

	private class ThreadCounter {
		private long c = 0;

		public synchronized void add() {
			c += 1;
		}

		public synchronized void remove() {
			c -= 1;
		}

		public synchronized long get() {
			return c;
		}
	}

	public void doTask(String in, WorldInterface wi, Context c) {
		System.out.println("Running scripts");
		for (EBSScript s : tree.preCall) {
			s.run(this, wi, in, c);
		}

		in = in.trim();

		c.clearInputProperties();

		System.out.println("Detecting features");
		for (Entry<String, TextFeatureDetector> e : detectors.entrySet()) {
			c.setInputProperty(e.getKey(), e.getValue().hasFeature(in));
		}

		Map<EBFText, EBFIO> ios = Collections.synchronizedMap(new HashMap<>(1024 * 128));
		EBFIO max = null;
		double maxScore = Double.NEGATIVE_INFINITY;

		String filter = null;

		System.out.println("Loading input filters.");
		for (InputFilter inputFilter : filters) {
			if (inputFilter.check(in)) {
				filter = inputFilter.getGroup();
				inputFilter.onFilter(wi);
			}
		}

		String f_filter = filter;

		final ExecutorService executor2 = Executors.newFixedThreadPool(Math.min(2, Runtime.getRuntime().availableProcessors() / 2));

		ThreadCounter tc = new ThreadCounter();

		System.out.println("Expanding inputs.");
		tc.add();
		executor2.execute(new Runnable() {
			@Override
			public void run() {
				expandInputs(ios, tree.root, wi.getLocale(), c, f_filter, executor2, tc);
				if (f_filter != null && ios.size() == 0) {
					expandInputs(ios, tree.root, wi.getLocale(), c, null, executor2, tc);

					if (ios.size() == 0) {
						expandInputs(ios, tree.root, wi.getLocale(), c, "all", executor2, tc);
					}
				}
				tc.remove();
			}
		});

		while (tc.get() > 0) {
			Thread.yield();
		}
		System.out.println("Expanding inputs - done.");
		executor2.shutdown();
		try {
			executor2.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
		} catch (InterruptedException e2) {
			e2.printStackTrace();
		}

		List<Entry<EBFText, EBFIO>> entries = new ArrayList<>(ios.entrySet());
		List<Entry<EBFText, EBFIO>> lmax = Collections.synchronizedList(new ArrayList<>(entries.size() / 4999));

		final String fin = in;

		final ExecutorService executor = Executors.newWorkStealingPool(Math.min(2, Runtime.getRuntime().availableProcessors() / 2));

		DoneCounter done = new DoneCounter();

		System.out.println("Computing local minimums.");
		for (int i = 0; i < entries.size(); i += 5000) {
			int si = i;
			int ei = Math.min(i + 5000, entries.size());
			System.out.println("Starting: " + si + ".." + ei);
			executor.execute(new Runnable() {

				@Override
				public void run() {
					Entry<EBFText, EBFIO> max = null;
					double maxScore = Double.NEGATIVE_INFINITY;

					for (int li = si; li < ei; li++) {
						Entry<EBFText, EBFIO> e = entries.get(li);
						e.getValue().check();
						double score = compare.compare(replaceVars(e.getKey().getText(), fin, c), fin);

						if (score > maxScore && score > 0) {
							max = e;
							maxScore = score;
							// Exit if sufficiently high.
							if (score > Double.MAX_VALUE / 16) {
								done.done = true;
								break;
							}
						}
					}

					if (max != null) {
						lmax.add(max);
					}
				}
			});
		}

		executor.shutdown();

		long time = 1;
		while (!done.done) {
			try {
				if (executor.awaitTermination(1, TimeUnit.SECONDS)) {
					done.done = true;
				}
				System.out.println("Taken " + time + " seconds");
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			time += 1;
		}

		executor.shutdownNow();
		System.out.println("Done computing local max.");

		try {
			Thread.sleep(500);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		System.out.println("Checking generated data.");

		for (Entry<EBFText, EBFIO> e : lmax) {
			double score = compare.compare(replaceVars(e.getKey().getText(), fin, c), fin);

			System.out.println("Weighted " + e.getKey().getText() + " as " + score);

			if (score > maxScore && score > 0) {
				max = e.getValue();
				maxScore = score;
				// Exit if sufficiently high.
				if (score > Double.MAX_VALUE / 16) {
					break;
				}
			}
		}

		System.out.println("Doing output.");
		
		if (max == null) {
			wi.couldNotFind();
		} else {
			c.addCount(max);

			List<EBFText> outs = new ArrayList<>();

			expandOutputs(outs, max, c.getCount(max), c);

			int i = new Random().nextInt(outs.size());

			wi.out(replaceVars(outs.get(i).getText(), in, c));

			c.setContext("none");

			for (EBSScript s : max.getScripts()) {
				s.run(this, wi, in, c);
			}
		}
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		System.out.println("Final scripts.");
		for (EBSScript s : tree.postCall) {
			s.run(this, wi, in, c);
		}

		System.gc();
	}

	public String replaceVars(String text, String in, Context c) {
		return replaceVars(text, in, c, 3);
	}

	public String replaceVars(String text, String in, Context c, int iterations) {
		if (iterations == 0) {
			return text;
		}

		for (Replacer r : replacers) {
			text = r.replace(text);
		}

		text = text.replace("\\n", "\n");

		for (Entry<Object, Object> e : tree.variables.entrySet()) {
			text = text.replace("%" + e.getKey() + "%", e.getValue() + "");
		}
		for (Entry<Object, Object> e : tree.finals.variables.entrySet()) {
			text = text.replace("%" + e.getKey() + "%", e.getValue() + "");
		}
		text = text.replace("%context%", c.getContext());
		text = text.replace("%input%", in);
		text = text.replace("%user%", c.getUser());
		text = text.replace("%date%", new Date() + "");
		for (Entry<String, Boolean> e : c.getInputProperties()) {
			text = text.replace("%d_" + e.getKey() + "%", e.getValue() + "");
		}

		long age = System.currentTimeMillis() - Long.parseLong(getSafe("botBirth", c));
		String ageString = "";
		if (age < 1000) {
			ageString = age + " milliseconds old";
		} else if (age < 1000 * 60) {
			ageString = age / 1000 + " seconds old";
		} else if (age < 1000 * 60 * 60) {
			ageString = age / 1000 / 60 + " minutes old";
		} else if (age < 1000 * 60 * 60 * 24) {
			ageString = age / 1000 / 60 / 60 + " hours old";
		} else if (age < 1000 * 60 * 60 * 24 * 365) {
			ageString = age / 1000 / 60 / 60 / 24 + " days old";
		} else {
			ageString = age / 1000 / 60 / 60 / 24 / 365 + " years old";
		}
		text = text.replace("%age%", ageString);

		text = text.replace("%ramUsage%", Runtime.getRuntime().maxMemory() / 1024 / 1024 + "MB");

		return replaceVars(text, in, c, iterations - 1);
	}

	protected void expandOutputs(List<EBFText> outs, EBFIO io, int count, Context c) {
		if (count >= 3 && io.getOutputs3().length != 0) {
			for (EBFText t : io.getOutputs3()) {
				if (t instanceof EBFExpand) {
					if (((EBFExpand) t).evaluate(this, c)) {
						expandOut(outs, (EBFExpand) t, c);
					}
				} else {
					outs.add(t);
				}
			}

			return;
		}

		if (count >= 2 && io.getOutputs2().length != 0) {
			for (EBFText t : io.getOutputs2()) {
				if (t instanceof EBFExpand) {
					if (((EBFExpand) t).evaluate(this, c)) {
						expandOut(outs, (EBFExpand) t, c);
					}
				} else {
					outs.add(t);
				}
			}

			return;
		}

		for (EBFText t : io.getOutputs()) {
			if (t instanceof EBFExpand) {
				if (((EBFExpand) t).evaluate(this, c)) {
					expandOut(outs, (EBFExpand) t, c);
				}
			} else {
				outs.add(t);
			}
		}
	}

	protected void expandOut(List<EBFText> outs, EBFExpand e, Context c) {
		for (EBFText t : e.getTexts()) {
			if (t instanceof EBFExpand) {
				if (((EBFExpand) t).evaluate(this, c)) {
					expandOut(outs, (EBFExpand) t, c);
				}
			} else {
				outs.add(t);
			}
		}
	}

	protected void expandInputs(Map<EBFText, EBFIO> map, EBFGroup g, Locale l, Context c, String filter,
			Executor executor, ThreadCounter tc) {
		tc.add();
		if ((filter == null && !g.getName().startsWith("f:")) || (filter != null && filter.equals("all"))
				|| g.getName().equals("f:" + filter)) {
			for (EBFIO io : g.getIOs()) {
				for (EBFText t : io.getInputs()) {
					if (t instanceof EBFExpand) {
						if (((EBFExpand) t).evaluate(this, c)) {
							executor.execute(new Runnable() {
								@Override
								public void run() {
									tc.add();
									expandIn(map, io, (EBFExpand) t, l, c, executor);
									tc.remove();
								}
							});
						}
					} else {
						map.put(t, io);
					}
				}
			}
		}
		for (EBFGroup g2 : g.getSubGroups()) {
			if (g2.getName().startsWith("lang:")) {
				if (l.getLanguage().equals(g2.getName().split(":")[1])) {
					executor.execute(new Runnable() {
						@Override
						public void run() {
							expandInputs(map, g2, l, c, filter, executor, tc);
						}
					});
				}
			} else if (g2.getName().startsWith("region:")) {
				if (l.getCountry().equals(g2.getName().split(":")[1])) {
					executor.execute(new Runnable() {
						@Override
						public void run() {
							expandInputs(map, g2, l, c, filter, executor, tc);
						}
					});
				}
			} else {
				executor.execute(new Runnable() {
					@Override
					public void run() {
						expandInputs(map, g2, l, c, filter, executor, tc);
					}
				});
			}
		}
		tc.remove();
	}

	protected void expandIn(Map<EBFText, EBFIO> map, EBFIO io, EBFExpand e, Locale l, Context c, Executor executor) {
		for (EBFText t : e.getTexts()) {
			if (t instanceof EBFExpand) {
				executor.execute(new Runnable() {
					@Override
					public void run() {
						if (((EBFExpand) t).evaluate(EBot.this, c)) {
							expandIn(map, io, (EBFExpand) t, l, c, executor);
						}
					}
				});
			} else {
				map.put(t, io);
			}
		}
	}

	public void callNative(String name, EBot bot, WorldInterface wi, String in, Context c, EBSScript s) {
		calls.get(name).run(bot, wi, in, c, s);
	}

	public void set(String var1, String var2) {
		tree.variables.setProperty(var1, var2);
	}

	public TextFeatureDetector getTextProcessor(String id) {
		return detectors.get(id);
	}

	public void addTextProcessor(String id, TextFeatureDetector tfd) {
		detectors.put(id, tfd);
	}

	public void addNative(String id, NativeCall c) {
		calls.put(id, c);
	}

	public void addFilter(InputFilter f) {
		filters.add(f);
	}

	public void addReplacer(Replacer r) {
		replacers.add(r);
	}
}
