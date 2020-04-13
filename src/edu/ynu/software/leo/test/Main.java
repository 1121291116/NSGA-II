package edu.ynu.software.leo.test;
import edu.ynu.software.leo.algorithm.Individual;
import edu.ynu.software.leo.algorithm.NSGA_II;
import edu.ynu.software.leo.algorithm.Population;
import edu.ynu.software.leo.dataSet.Iris;
import edu.ynu.software.leo.dataSet.Iris;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by maxleo on 17-11-2.
 * 这个NSGA2 用于聚类，类似于k-means方法
 */
public class Main {
	public static List<Iris> dataSet;							// 数据集声明
	public static Double disMatrix[][];							// 距离矩阵声明
	public static void main(String[] args) {
		Integer iterationNum = 50;								// 迭代数
		/**
		 * Java 输出文件地址设置
		 */
		String outFilePath = "data/output/irisEliteOut.data";	// 输出数据文件地址赋值
		String filePath = "data/Iris set/Iris.data";
		//        String filePath = "data/Wine set/Wine.data"; //exchange with the upper line! geneSize in Individual to go!
		//        String filePath = "data/test/DBscanTest.data";
		String elitePath = "data/Elite gene/elite.data";		// 输出精英路径地址赋值
		System.out.println("Initial done!");

		/**
		 * 数据读取
		 */
		dataSet = readIrisData(filePath); //read data from file 从文件读数据
		System.out.println("Read data, successful.");
		Integer dataSetSize = dataSet.size();					// 数据规模
		System.out.println("Data set size is:"+dataSetSize);

		/**
		 * 计算距离矩阵
		 */
		disMatrix = new Double[dataSetSize][dataSetSize];		// 构建距离矩阵，分配内存
		for (int i = 0; i < dataSetSize; i++) {					// 初始化距离矩阵
			disMatrix[i][i] = 0d;
		}
		for (int i = 0; i < dataSetSize; i++) {					// 构建下三角距离矩阵
			for (int j = 0; j < i; j++) {
				Double dis = dataSet.get(i).distance(dataSet.get(j));		// 获得距离值
				disMatrix[i][j] = dis;							// 分配值
				disMatrix[j][i] = dis;							// 对称分配，构建上三角
			}
		}
		System.out.println("Distance calculation complete!");

		/**
		 * 种群对象生成及合并
		 */
		Population population = new Population(true);			// 生成种群对象
		System.out.println("Initialize, successful");
		population.eliteInjection(elitePath); //inject elite individual		种群重组，再插入精英个体
		System.out.println("new population complete!");

		/**
		 * 算法对象生成及算法应用
		 */
		NSGA_II nsga_ii = new NSGA_II();						// 生成NSGA2算法对象
		for (int i = 0; i < iterationNum; i++) {
			population = nsga_ii.evolution(population);			// 调用算法evolution函数
			System.out.println("Iteration "+i+" : Done.");
			System.out.println();
		}
		System.out.println("Evolution complete!");

		/**
		 * 计算结果输出
		 */
		fileOutput(population,outFilePath,iterationNum);
		System.out.println("Output complete!");
	}

	/**
	 * 部分函数注释，1.Iris数据读取
	 * @param fileName
	 * @return 一个列表
	 */
	public static List<Iris> readIrisData(String fileName) {
		File file = new File(fileName);								// 生成一个文件对象
		BufferedReader reader = null;
		List<Iris> irisData = new ArrayList<>();					// 列表返回对象构建
		try {
			reader = new BufferedReader(new FileReader(file));		// 文件读取对象构建
			String tempString = null;
			int line = 1;
			while ((tempString = reader.readLine()) != null) {		// 只要读取行不为空，则一直循环
				// 显示行号
				String[] temp = tempString.split(",");				// 将行内容按","分割，并存储相关数据
				Iris iris = new Iris();								// 构建Iris对象
				iris.sepalL = Double.parseDouble(temp[0]);			// 把字符串对象转换为Double类型
				iris.sepalW = Double.parseDouble(temp[1]);
				iris.petalL = Double.parseDouble(temp[2]);
				iris.petalW = Double.parseDouble(temp[3]);
				iris.type = temp[4];
				irisData.add(iris);
			}
			reader.close();											// 文件读取关闭防止资源泄露
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
					e1.printStackTrace();

				}
			}
		}
		return irisData;
	}

	/**
	 * 文件输出函数
	 * @param population
	 * @param filePath
	 * @param iterationNum
	 */
	public static void fileOutput(Population population, String filePath, Integer iterationNum) {
		try {
			PrintWriter pw = new PrintWriter(new FileWriter(filePath));			// 打印对象构建
			pw.println("Population size: "+population.size()+"; Iteration times:"+iterationNum+";");
			pw.println();
			for (int i = 0; i < population.size(); i++) {						// 打印种群的每一代
				pw.println("-----------solution "+i+"-----------");
				Individual individual = population.individualList.get(i);		// 获得种群染色体个体
				pw.println("Rank:"+individual.rank+"; Cluster number:"+individual.clusterCount+"; Adapt values:"+ -individual.adaptiveValues.get(0)+" "+individual.adaptiveValues.get(1)+" "+individual.adaptiveValues.get(2));
				for (int j = 0; j < individual.gene.size(); j++) {
					pw.println(j+"\t"+individual.gene.get(j));
				}
			}
			pw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
