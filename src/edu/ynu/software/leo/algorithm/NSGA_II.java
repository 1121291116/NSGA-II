package edu.ynu.software.leo.algorithm;

import java.util.Comparator;

/**
 * Created by maxleo on 17-11-2.
 * 这是NSGA的主运行过程，但是把初始种群的预处理过程没有放进来
 */
public class NSGA_II {

    public Population evolution (Population population) {
        Population result = new Population();				// 这里是原始种群的一个对象
        Population newPopulation = population.crossover();	// 选择交叉 变异 生成新种群
        newPopulation.mutation();
        newPopulation.individualList.addAll(population.individualList);//并集，这里规模变为2N
        newPopulation.calcDistance();						//计算个体拥挤距离
        Integer rank = 0;
        while (result.size() < Population.populationSize) {
            System.out.println("rank "+rank);
            Population tempPopulation = new Population();							// 临时帕累托前沿集种群
            tempPopulation.individualList = newPopulation.paretoFront();			// 获得当前种群的帕累托前沿集
            tempPopulation.setRanks(rank);											// 设置当前帕累托前沿级别
            int difference = Population.populationSize - result.size();				// 
            if (tempPopulation.size() < difference) {								// 当pareto front能放得下
                result.individualList.addAll(tempPopulation.individualList);		// 存放当前级的帕累托解
            }
            else {																	// 当pareto front放不下,只保留到最大尺寸
                tempPopulation.individualList.sort(new distanceComparator());		// 按拥挤度降序排列
                for (int i = 0; i < difference; i++) {
                    result.individualList.add(tempPopulation.individualList.get(i));
                }
            } 
            newPopulation.individualList.removeAll(tempPopulation.individualList);	// 去掉pareto front
            rank++;
        }
        return result;																// 返回所有的帕累托解
    }

    static class distanceComparator implements Comparator {
        public int compare(Object object1, Object object2) {// 实现接口中的方法
            Individual ind1 = (Individual) object1; // 强制转换
            Individual ind2 = (Individual) object2;
            return ind2.distance.compareTo(ind1.distance);
        }
    }
}
