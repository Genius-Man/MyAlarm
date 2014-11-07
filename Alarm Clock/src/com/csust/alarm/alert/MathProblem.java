package com.csust.alarm.alert;

import java.util.ArrayList;
import java.util.Random;

public class MathProblem {

	enum Operator {
		ADD, SUBTRACT, MULTIPLY, DIVIDE;

		@Override
		public String toString() {
			String string = null;
			switch (ordinal()) {
			case 0:
				string = "+";
				break;
			case 1:
				string = "-";
				break;
			case 2:
				string = "*";
				break;
			case 3:
				string = "/";
				break;
			}
			return string;
		}
	}

	private ArrayList<Float> parts;  // 运算变量
	private ArrayList<Operator> operators; //  运算符
	private float answer = 0f; // 运算结果
	private int min = 0;
	private int max = 10;
	
	/**
	 * 默认只有三个数的加减乘除
	 */
	public MathProblem() {		
		this(3);
	}
	
	/**
	 * 
	 * @param numParts 运算变量的数量
	 */
	public MathProblem(int numParts) {
		super();
		Random random = new Random(System.currentTimeMillis());

		// 运算变量 总数为numParts
		parts = new ArrayList<Float>(numParts);
		
		
		for (int i = 0; i < numParts; i++)
			parts.add(i, (float) random.nextInt(max - min + 1) + min);
		
		// 运算符 总数为numParts-1
		operators = new ArrayList<MathProblem.Operator>(numParts - 1);
		
		// 随机生成各个运算符
		for (int i = 0; i < numParts - 1; i++)
			operators.add(i,Operator.values()[random.nextInt(2)+1]);
			//operators.add(i,Operator.values()[random.nextInt(3)]);
		ArrayList<Object> combinedParts = new ArrayList<Object>();
		
		// 分离各个因子与运算符
		for (int i = 0; i < numParts; i++){
			combinedParts.add(parts.get(i));
			if(i<numParts-1)
				combinedParts.add(operators.get(i));
		}
		
		// ****************计算除法*********************
		while(combinedParts.contains(Operator.DIVIDE)){	
			int i = combinedParts.indexOf(Operator.DIVIDE);
			answer = (Float)combinedParts.get(i-1) / (Float)combinedParts.get(i+1);
			
			/*
			 * 如 1.0+3.0*3.0/3.0/3.0 ===》 1+3.0*1.0/3.0 ===> 1+3.0*0.333333333
			 * 
			 * */
			// 移除参与此次运算的第一个因子与运算符 
			for (int r = 0; r < 2; r++)
				combinedParts.remove(i-1);
			
			// 将结果保留至第二个运算符的位置
			combinedParts.set(i-1, answer);
		}
		
		// ***************计算乘法**********************
		while(combinedParts.contains(Operator.MULTIPLY)){	
			int i = combinedParts.indexOf(Operator.MULTIPLY);
			answer = (Float)combinedParts.get(i-1) * (Float)combinedParts.get(i+1);
			for (int r = 0; r < 2; r++)
				combinedParts.remove(i-1);
			combinedParts.set(i-1, answer);			
		}
		
		while(combinedParts.contains(Operator.ADD)){	
			int i = combinedParts.indexOf(Operator.ADD);
			answer = (Float)combinedParts.get(i-1) + (Float)combinedParts.get(i+1);
			for (int r = 0; r < 2; r++)
				combinedParts.remove(i-1);
			combinedParts.set(i-1, answer);
		}
		
		while(combinedParts.contains(Operator.SUBTRACT)){	
			int i = combinedParts.indexOf(Operator.SUBTRACT);
			answer = (Float)combinedParts.get(i-1) - (Float)combinedParts.get(i+1);
			for (int r = 0; r < 2; r++)
				combinedParts.remove(i-1);
			combinedParts.set(i-1, answer);
		}
		

	}

	/**
	 * 生成数学算式 形如：1.0+3.0*3.0/3.0/3.0
	 */
	@Override
	public String toString() {
		StringBuilder problemBuilder = new StringBuilder();
		for (int i = 0; i < parts.size(); i++) {
			problemBuilder.append(parts.get(i));
			problemBuilder.append(" ");
			if (i < operators.size()){
				problemBuilder.append(operators.get(i).toString());
				problemBuilder.append(" ");
			}
		}
		return problemBuilder.toString();
	}

	/**
	 * 运算结果
	 * @return
	 */
	public float getAnswer() {
		return answer;
	}

}
