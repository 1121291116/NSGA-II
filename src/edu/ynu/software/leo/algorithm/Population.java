package edu.ynu.software.leo.algorithm;

import edu.ynu.software.leo.dataSet.Iris;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Created by maxleo on 17-11-2.
 */
public class Population {
	public static final Integer populationSize = 400;			// 种群大小
	public static final Double crossRate = 0.5;					// 交叉概率
	public static final Double mutationRate = 0.001;			// 变异概率

	/**
	 * 染色体存储对象生成及排名设置
	 * 依据输入给每条染色体分个等级
	 */
	public List<Individual> individualList = new ArrayList<>();	// 种群染色体列表对象生成，存放整个种群的染色体
	public void setRanks(Integer rank) {
		for (int i = 0; i < individualList.size(); i++) {
			individualList.get(i).setRank(rank);
		}
	}

	/**
	 * 种群初始化
	 * @param isInitialize
	 */
	public Population(Boolean isInitialize) { //initial population
		if (isInitialize) {												// 判断是否初始化
			for (int i = 0; i < populationSize; i++) {
				Individual newIndividual = new Individual(true);		// 实例化一条染色体
				individualList.add(newIndividual);						// 添加到种群染色体列表中
			}
		}
	}

	/**
	 * 
	 */
	public void geneGuide(){
		for (int i = 0; i < populationSize; i++) {						// 对每一条染色体调用Individual类的geneGuide方法
			individualList.get(i).geneGuide();							// 函数嵌套
		}
	}

	/**
	 * 精英个体插入函数
	 * @param filename
	 */
	public void eliteInjection(String filename) {
		List<Individual> eliteIndiv = getEliteIndiv(filename);			// 获得数据文件中的精英个体
		for (int i = 0; i < eliteIndiv.size(); i++) {
			individualList.remove(0);
			individualList.add(eliteIndiv.get(i));						// 将他们全部添加到当前种群中
		}
	}

	/**
	 * 获得数据文件的精英染色体，可能不止一条
	 * @param fileName
	 * @return
	 */
	public List<Individual> getEliteIndiv(String fileName){
		File file = new File(fileName);									// 文件读取参见main函数
		BufferedReader reader = null;
		List<Individual> eliteIndivs = new ArrayList<>();
		try {
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			int line = 1;
			while ((tempString = reader.readLine()) != null) {
				String[] temp = tempString.split(",");
				Individual individual = new Individual();				// 保护数据，copy一个
				for (int i = 0; i < temp.length; i++) {
					individual.gene.add(Integer.parseInt(temp[i]));
				}
				individual.calcDerivedAttr();
				eliteIndivs.add(individual);
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
		return eliteIndivs;
	}

	/**
	 * 种群对象生成函数
	 */
	public Population() {
		new Population(false);
	}

	/**
	 * 种群大小计算函数
	 * @return
	 */
	public Integer size(){
		return individualList.size();
	}

	/**
	 * 种群帕累托前沿集生成函数
	 * @return
	 */
	public List<Individual> paretoFront () {
		List<Individual> result = new ArrayList<>();
		for (Individual ind :individualList) {
			if (isParetoOptimality(ind)) result.add(ind);		// 如果是帕累托最优则添加
		}
		return result;
	}

	/**
	 * 染色体是否为帕累托最优判断函数
	 * @param individual
	 * @return
	 */
	public boolean isParetoOptimality(Individual individual) {
		for(Individual ind: individualList) {
			if(individual == ind ) {
				continue;
			}	
			if(individual.isDominatedBy(ind)) return false;		// 只要individual被种群的一个染色体支配，其就不是帕累托最优解
		}
		return true;	
	}

	/**
	 * 交叉算子
	 * @return
	 */
	public Population crossover() {
		Random random = new Random();							// 随机数生成器
		Population newPop = new Population();					// 实例化一个新种群对象
		for (int i = 0; i < populationSize; i++) {
			Integer randomNum1 = random.nextInt(populationSize);	// 随机选择两个父代
			Integer randomNum2 = random.nextInt(populationSize);
			Individual newInd = corss(individualList.get(randomNum1),individualList.get(randomNum2));	// 让两个父代交叉
			newPop.individualList.add(newInd);					// 将新染色体添加到新种群中，种群规模不变
		}
		return newPop;
	}

	/**
	 * 变异算子
	 */
	public void mutation() {
		Random random = new Random();
		for (Individual ind:individualList) {
			if (random.nextDouble() < mutationRate) {
				Integer index = random.nextInt(Individual.geneSize);	// 随机挑选一个染色体位置
				Integer content = random.nextInt(ind.clusterCount);		// 为该位置随机生成一个数
				ind.gene.set(index,content);							// 替换，种群规模不变
			}
			ind.calcDerivedAttr();
		}
	}

	/**
	 * 交叉算子的交叉规则
	 * @param ind1
	 * @param ind2
	 * @return 由两个父代产生一个子代
	 */
	public Individual corss(Individual ind1, Individual ind2) {
		Random random = new Random();
		Individual newInd = new Individual();
		for (int i = 0; i < Individual.geneSize; i++) {
			if (random.nextDouble() < crossRate) {		// 如果小于，则对应基因位选用父代1的数值
				newInd.gene.add(ind1.gene.get(i));
			}
			else {
				newInd.gene.add(ind2.gene.get(i));		// 如果大于，则选用父代2对应基因位的数值
			}
		}
		return newInd;
	}

	/**
	 * 计算拥挤度，拥挤度是操作帕累托前沿集计算得来的，所以属于种群对象
	 */
	public void calcDistance(){
		for (Individual ind :individualList) {			// 初始化种群拥挤度
			ind.distance = 0.0;
		}
		for (int i = 0; i < Individual.objFunNum; i++) {									// 按目标函数个数分别求解
			individualList.sort(new adaptiveValuesComparator(i));							// ascending order 根据不同目标函数升序
			individualList.get(0).distance = Double.MAX_VALUE/(Individual.objFunNum*10);	// 设置第一个和最后一个拥挤度为最大值
			individualList.get(individualList.size()-1).distance = Double.MAX_VALUE/(Individual.objFunNum*10);
			for (int j = 1; j < size()-1; j++) {											// 对第2-倒数第二个染色体计算
				/**
				 * 第j个染色体目标函数i的拥挤度 = 第j个染色体目标函数i-1的拥挤度 + 
				 * 			（j+1染色体目标函数i适应度值 - j-1染色体目标函数i适应度值）/ 
				 * 			(最后一个染色体目标i的适应度值 - 第一个染色体目标i的适应度值)
				 * 注意：拥挤度和适应度值是两个概念，拥挤度是根据适应度值计算出来的；把第一个和最后一个染色体的拥挤度设为最大值，
				 * 		 并不代表他们的适应度值也被修改了。因为种群已经根据相应目标函数将其适应度值进行了升序排列，所以最后一个
				 * 		 染色体适应度值最大，第一个染色体适应度值最小。	 
				 */
				individualList.get(j).distance = individualList.get(j).distance +			
						(individualList.get(j+1).adaptiveValues.get(i) - individualList.get(j-1).adaptiveValues.get(i))
						/(individualList.get(individualList.size()-1).adaptiveValues.get(i) 
								- individualList.get(0).adaptiveValues.get(i));
			}
		}
	}
	
	/**
	 * 排序比较算子
	 */
	static class adaptiveValuesComparator implements Comparator {
		public Integer ojbFunIndex;

		public adaptiveValuesComparator(Integer ojbFunIndex) {
			this.ojbFunIndex = ojbFunIndex;
		}

		public int compare(Object object1, Object object2) {		// 实现接口中的方法
			Individual ind1 = (Individual) object1; 				// 强制转换
			Individual ind2 = (Individual) object2;
			return ind1.adaptiveValues.get(ojbFunIndex).compareTo(ind2.adaptiveValues.get(ojbFunIndex));
		}
	}
}
