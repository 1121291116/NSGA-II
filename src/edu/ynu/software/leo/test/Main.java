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

		//check file input
		//        for (int i = 0; i < dataSet.size(); i++) {
		//            System.out.println("["+i+"]"+dataSet.get(i).sepalL+","+dataSet.get(i).sepalW+","+dataSet.get(i).petalL+","+dataSet.get(i).petalW+","+dataSet.get(i).type);
		//        }

		Population population = new Population(true);			// 生成种群对象
		population.eliteInjection(elitePath); //inject elite individual		种群重组，再插入精英个体
		System.out.println("new population complete!");
		NSGA_II nsga_ii = new NSGA_II();						// 生成NSGA2算法对象
		System.out.println("Initialize, successful");

		for (int i = 0; i < iterationNum; i++) {
			population = nsga_ii.evolution(population);
			//-------------------Gene Guide----------------------------------------------------
			//            if (i < 10) {
			//                population.geneGuide();
			//            }
			//----------------------------------------------------------------------------------
			System.out.println("Iteration "+i+" : Done.");
			System.out.println();
		}
		System.out.println("Evolution complete!");

		fileOutput(population,outFilePath,iterationNum);
		System.out.println("Output complete!");


		//        Individual individual = population.individualList.get(0);
		//        System.out.println("genes are as follows:");
		//        for (int i = 0; i < individual.gene.size(); i++) {
		//            System.out.println(i+":"+individual.gene.get(i));
		//        }
		//
		//        System.out.println("DB:"+individual.adaptiveValues.get(0));
		//        System.out.println("DI:"+individual.adaptiveValues.get(1));
		//        System.out.println("SC:"+individual.adaptiveValues.get(2));

	}

	public static List<Iris> readIrisData(String fileName) {
		File file = new File(fileName);								// 生成一个文件对象
		BufferedReader reader = null;
		List<Iris> irisData = new ArrayList<>();
		try {
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			int line = 1;
			while ((tempString = reader.readLine()) != null) {
				// 显示行号
				String[] temp = tempString.split(",");
				Iris iris = new Iris();
				iris.sepalL = Double.parseDouble(temp[0]);
				iris.sepalW = Double.parseDouble(temp[1]);
				iris.petalL = Double.parseDouble(temp[2]);
				iris.petalW = Double.parseDouble(temp[3]);
				iris.type = temp[4];
				irisData.add(iris);
			}
			reader.close();
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

	//    public static List<Iris> readIrisData(String fileName) {
	//        File file = new File(fileName);
	//        BufferedReader reader = null;
	//        List<Iris> IrisData = new ArrayList<>();
	//        try {
	//            reader = new BufferedReader(new FileReader(file));
	//            String tempString = null;
	//            int line = 1;
	//            while ((tempString = reader.readLine()) != null) {
	//                // 显示行号
	//                String[] temp = tempString.split(",");
	//                Iris Iris = new Iris();
	//                Iris.type = temp[0];
	//                Iris.Alcohol = Double.parseDouble(temp[1]);
	//                Iris.Malic = Double.parseDouble(temp[2]);
	//                Iris.Ash = Double.parseDouble(temp[3]);
	//                Iris.Alcalinity = Double.parseDouble(temp[4]);
	//                Iris.Magnesium = Double.parseDouble(temp[5]);
	//                Iris.phenols = Double.parseDouble(temp[6]);
	//                Iris.Flavanoids = Double.parseDouble(temp[7]);
	//                Iris.Nonflavanoid = Double.parseDouble(temp[8]);
	//                Iris.Proanthocyanins = Double.parseDouble(temp[9]);
	//                Iris.Color = Double.parseDouble(temp[10]);
	//                Iris.Hue = Double.parseDouble(temp[11]);
	//                Iris.diluted = Double.parseDouble(temp[12]);
	//                Iris.Proline = Double.parseDouble(temp[13]);
	//
	//                IrisData.add(Iris);
	//            }
	//            reader.close();
	//        } catch (IOException e) {
	//            e.printStackTrace();
	//        } finally {
	//            if (reader != null) {
	//                try {
	//                    reader.close();
	//                } catch (IOException e1) {
	//                    e1.printStackTrace();
	//
	//                }
	//            }
	//        }
	//        return IrisData;
	//    }



	public static void fileOutput(Population population, String filePath, Integer iterationNum) {
		try {
			PrintWriter pw = new PrintWriter(new FileWriter(filePath));
			pw.println("Population size: "+population.size()+"; Iteration times:"+iterationNum+";");
			pw.println();
			for (int i = 0; i < population.size(); i++) {
				pw.println("-----------solution "+i+"-----------");
				Individual individual = population.individualList.get(i);
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
