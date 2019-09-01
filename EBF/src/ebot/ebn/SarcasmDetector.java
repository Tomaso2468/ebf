package ebot.ebn;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.Scanner;

import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.BackPropagation;

import ebot.io.DataSource;
import ebot.io.FileDataSource;

public class SarcasmDetector implements TextFeatureDetector, Serializable {
	private static final long serialVersionUID = -470137477983242231L;
	private final NeuralNetwork<BackPropagation> nn;
	private final int inNodeCount;
	private final int trainingQuality;
	private final DataSource trainingDataPositive;
	private final DataSource trainingDataNegative;
	private final double threshold;
	private final int sampleLimit;

	public SarcasmDetector(int slength, int trainingQuality, DataSource trainingDataPositive,
			DataSource trainingDataNegative, double threshold, int sampleLimit) {
		inNodeCount = TextFeatureDetector.genInputCountForTextSize(slength);
		this.trainingQuality = trainingQuality;
		this.trainingDataPositive = trainingDataPositive;
		this.trainingDataNegative = trainingDataNegative;
		this.threshold = threshold;
		this.sampleLimit = sampleLimit;

		nn = new MultiLayerPerceptron(inNodeCount, slength, 1);
	}

	@Override
	public synchronized boolean hasFeature(String in) {
		if (in.contains("\\s")) {
			return true;
		}
		nn.setInput(TextFeatureDetector.encodeText(in, inNodeCount));
		nn.calculate();
		double out = nn.getOutput()[0];
		return out >= threshold;
	}

	@Override
	public synchronized void train() throws IOException {
		DataSet ds = new DataSet(inNodeCount, 1);

		loadTrainingData(trainingDataPositive, ds, 1, sampleLimit);
		loadTrainingData(trainingDataNegative, ds, 0, sampleLimit);

		System.out.println("Learning");
		BackPropagation backPropagation = new BackPropagation();
		backPropagation.setMaxIterations(trainingQuality);
		backPropagation.setMaxError(0.05);
		nn.learn(ds, backPropagation);
		
		save();
	}
	
	@Override
	public void quickTrain() throws IOException {
		DataSet ds = new DataSet(inNodeCount, 1);

		loadTrainingData(trainingDataPositive, ds, 1, sampleLimit / 5);
		loadTrainingData(trainingDataNegative, ds, 0, sampleLimit / 5);

		System.out.println("Learning");
		BackPropagation backPropagation = new BackPropagation();
		backPropagation.setMaxIterations(trainingQuality / 2);
		backPropagation.setMaxError(0.1);
		nn.learn(ds, backPropagation);
		
		save();
	}

	protected void loadTrainingData(DataSource data, DataSet ds, double weight, int limit) throws IOException {
		System.out.println("Loading training data.");
		Scanner s = new Scanner(data.openStream());

		int index = 0;
		while (s.hasNextLine() && index < limit) {
			double[] in = TextFeatureDetector.encodeText(s.nextLine().trim(), inNodeCount);

			ds.add(in, new double[] { weight });
			
			index += 1;
		}

		s.close();
	}
	
	public synchronized void save() throws FileNotFoundException, IOException {
//		File f = new File("sarcasm.ebn");
//		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f));
//		oos.writeObject(this);
//		oos.flush();
//		oos.close();
	}
	
	public static SarcasmDetector load(int slength, int trainingQuality, DataSource trainingDataPositive,
			DataSource trainingDataNegative, double threshold, int sampleLimit) throws IOException {
		File f = new File("sarcasm.ebn").getAbsoluteFile();
		
		if	(!f.exists()) {
			SarcasmDetector sc = new SarcasmDetector(slength, trainingQuality, trainingDataPositive, trainingDataNegative, threshold, sampleLimit);
			
			f.getParentFile().mkdirs();
			f.createNewFile();
			
			sc.save();
			
			return sc;
		} else {
			f.delete();
			return load(slength, trainingQuality, trainingDataPositive, trainingDataNegative, threshold, sampleLimit);
//			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
//			Object o;
//			try {
//				o = ois.readObject();
//			} catch (ClassNotFoundException e) {
//				ois.close();
//				throw new IOException();
//			}
//			ois.close();
//			
//			return (SarcasmDetector) o;
		}
		
	}

	public static void main(String[] args) throws IOException {
		SarcasmDetector d = load(32, 5, new FileDataSource("sarcasmP.txt"),
				new FileDataSource("sarcasmN.txt"), 0.75, 100);

		d.train();

		Scanner s = new Scanner(System.in);

		while (true) {
			System.out.print("> ");

			while (!s.hasNextLine()) {
				Thread.yield();
			}

			String line = s.nextLine();

			if (line.trim().toLowerCase().equals("exit")) {
				s.close();
				return;
			}

			System.out.println(d.hasFeature(line));
		}
	}

}
