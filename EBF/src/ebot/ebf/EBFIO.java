package ebot.ebf;

import java.util.Arrays;

import ebot.ebs.EBSScript;

public class EBFIO {
	private final EBFText[] inputs;
	private final EBFText[] outputs;
	private final EBFText[] outputs2;
	private final EBFText[] outputs3;
	private final EBSScript[] scripts;
	private float value;
	
	public EBFIO(EBFText[] inputs, EBFText[] outputs, EBFText[] outputs2, EBFText[] outputs3, EBSScript[] scripts) {
		super();
		this.inputs = inputs;
		this.outputs = outputs;
		this.scripts = scripts;
		this.outputs2 = outputs2;
		this.outputs3 = outputs3;
	}

	public EBFText[] getInputs() {
		return inputs;
	}

	public EBFText[] getOutputs() {
		return outputs;
	}
	
	public EBFText[] getOutputs2() {
		return outputs2;
	}
	
	public EBFText[] getOutputs3() {
		return outputs3;
	}

	public EBSScript[] getScripts() {
		return scripts;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("EBFIO{");
		sb.append("\n");
		
		sb.append("in=");
		sb.append(Arrays.toString(inputs));
		sb.append(", \n");
		
		sb.append("out=");
		sb.append(Arrays.toString(outputs));
		sb.append(", \n");
		
		sb.append("scripts=");
		sb.append(Arrays.toString(scripts));
		
		sb.append("\n");
		sb.append("}");
		
		return sb.toString();
	}
	
	public void update(float delta) {
		value += delta;
	}
	
	public float getLastUpdate() {
		return value;
	}

	public void check() {
		value = 0;
	}
}
