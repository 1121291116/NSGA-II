# 基于NSGAII的多目标聚类算法

## 基本概念
- **NSGA-II**: Non-dominated Sorting Genetic Algorithm-II
- **多目标聚类算法**：通过多种不同的聚类评价指标作为目标进行优化

## NSGA-II

#### 1. 快速非支配排序算子<br>
![fast non-dominated sort](img/fast-non-dominated-sort.png)<br>
对种群中的所有个体循环求 **[pareto front(非支配解集)](https://baike.baidu.com/item/%E9%9D%9E%E6%94%AF%E9%85%8D%E8%A7%A3/6911808?fr=aladdin)**，第一组pareto front的非支配序为1，然后从种群中去掉，剩下的个体再求pareto front，这一组的非支配序为2，以此类推。

#### 2. 个体拥挤距离算子
![crowding distance assignment](img/crowding-distance-assignment.png)<br>
首先，初始化每个个体的拥挤距离为0，对于每个目标函数，按照目标函数的适应值对个体排序，第一个个体和最后一个个体的拥挤距离等于无穷大，剩下中间的个体用公式
$$I[i]_{distance} = I[i]_{distance}+\frac{I[i+1].m-I[i-1].m}{f_{m}^{max}-f_{m}^{min}}$$
来计算。

#### 3. 主循环
![NSGA-II flow chart](img/NSGA-II_flow_chart.png)<br>
父代种群 $P_t$ 进行crossover和mutation生成新的子代种群 $Q_t$，新种群 $R_t = P_t \bigcup Q_t$ ，对 $R_t$ 进行非支配排序和拥挤距离排序，筛选出新的父代 $P_{t+1}$ ，筛选规则遵循： **个体 $i$ 优于个体 $j$ ，当且仅当， $i_{rank} < j_{rank}$ ，或者， $i_{rank} = j_{rank}$ 且 $i_{distance} > j_{distance}$**
## Pareto Front
> 多目标规划中，由于存在目标之间的冲突和无法比较的现象，一个解在某个目标上是最好的，在其他的目标上可能比较差。Pareto 在1986 年提出多目标的解不受支配解(Non-dominated set)的概念。其定义为：假设任何二解S1 及S2 对所有目标而言，S1均优于S2，则我们称S1 支配S2，若S1 的解没有被其他解所支配，则S1 称为非支配解（不受支配解），也称Pareto解。这d些非支配解的集合即所谓的Pareto Front。所有坐落在Pareto front 中的所有解皆不受Pareto Front 之外的解（以及Pareto Front 曲线以内的其它解）所支配，因此这些非支配解较其他解而言拥有最少的目标冲突，可提供决策者一个较佳的选择空间。在某个非支配解的基础上改进任何目标函数的同时，必然会削弱至少一个其他目标函数。

## Pareto Dominance Relation
![Pareto Dominance Relation](img/pareto_domainance_relation.png)

**翻译过来就是**:在多目标问题中，有两个解S1，S2，其中一个解S1的所有目标适应值都好过S2的，那么S1支配S2

## 算法思想
首先，要写NSGA-II需要求出非支配序，而非支配序需要求出Pareto Front，可知，Pareto Front要判断支配关系，所以需要一个 **支配关系算子**，支配关系算子的有两个输入，分别是两个解S1、S2，返回结果为布尔型，表示S1是否支配S2，算法思想就是对两个解的所有目标适应值进行比较，从而得出关系 *可运用逻辑关系设计成非支配关系算子，效率会提高*。有了支配关系算子，就可以完成 **非支配排序算子**，即从一个种群中选出非支配解集并且编上号，感觉并不需要写成独立算子，*（其实，并不需要全部求出，而是求一层放入精英池一层，放不下的话，后面的pareto front就不用求了）* 具体实现再看。其次，NSGA-II需要一个 **个体拥挤距离算子**，这个算子直接根据伪代码写出即可，不会有子算子，最后需要一个 **算法主循环**，它包括 **交叉算子** 和 **变异算子**，最后还需要一个能够把基因型转化成表现型的 **目标函数算子**。所以综上所述，一共有：
* **算法主循环**
* **非支配排序算子**
* **个体拥挤距离算子**
* **支配关系算子**
* **交叉算子**
* **变异算子**
* **目标函数算子 * M**

## 个体基因编码
以记录个数为基因链，基因型为其被分到的类标号。*（聚类的类簇数量？）*

## 聚类的有效评价
根据维基百科所说，评价分为两类，外部评价(External evaluation,AKA *Given Label* )和内部评价(Internal evaluation, AKA *Not Given Label* )，聚类的评价指标(Not Given Label)基本可以分为类内和类间两方面来评价，类内紧致性(Compactness)表示类内各个点的聚拢效果，类间间隔性(Separation)表示不同类之间的分割程度。根据阅读的各种材料，各种评价指标基本遵循一种评价效果越好，其评价指标的计算就越复杂的准则。<br>
已知的评价指标：
* [Davies–Bouldin index](https://en.wikipedia.org/wiki/Davies%E2%80%93Bouldin_index) (戴维森堡丁指数)
* [Dunn index](https://en.wikipedia.org/wiki/Dunn_index) (邓恩指数)
* [Silhouette coefficient](https://en.wikipedia.org/wiki/Silhouette_(clustering))([轮廓系数](https://baike.baidu.com/item/%E8%BD%AE%E5%BB%93%E7%B3%BB%E6%95%B0/17361607?fr=aladdin))
* [new-index](file/a_clustering_validity_evaluation_index_based_on_connectivity.pdf)(一种基于连通性的聚类有效性评价指标)

#### Davies–Bouldin index
根据维基百科的定义直接来写，类内紧致性定义为$S_i$，类间隔离性表示为$M_{i,j}$。
$$S_i = \left(\frac {1}{T_i}\sum_{j=1}^{T_i}\left|X_j-A_i\right|^{p}\right)^{1/p}$$
其中 $T_{i}$ 是第i类的大小， $A_{i}$ 是第i类的重心， $X_{i}$ 是第 $i$ 类中的一个元素。
$$M_{i,j} = \left\|A_{i}-A_{j}\right\|_{p} = \left( \sum_{k=1}^{n}\left|a_{k,i}-a_{k,j}\right|^{p}\right)^{1/p}$$
可以理解为 $M_{i,j}$ 是第类簇i的重心和类簇j的重心的距离<br>
以此为基础定义 $R_{i,j}$ ， $D_{i}$ ，$DB$ 。
$$R_{i,j}=\frac{S_{i}+S_{j}}{M_{i,j}}$$
$$D_{i}=\max_{j \neq i}R_{i,j}$$
$$DB=\frac{1}{N}\sum_{i=1}^{N}D_{i}$$
$N$ 是类簇数量。<br>
根据公式可以看出，**DB越小意味着类内距离越小，同时类间距离越大，因此DBI越小证明聚类效果越好**。

#### Dunn index
根据维基百科资料，邓恩指数定义非常简单：
$$DI_{m}=\frac{\min \limits_{1 \leq i <j \leq m}\delta \left(C_{i},C_{j}\right)}{\max\limits_{1 \leq k \leq m}\Delta_k}$$
其中 $\delta$ 是指类间距离，$\Delta$ 是指类内距离。<br>
至于类间距离和类内距离的计算，并没有具体的给出，有多种计算方式，这些计算方式被称作类邓恩指数，比如可以取平均值，最大距离，最小距离，中心点距离等。<br>
所以根据公式可以看出，**DI越大意味着类内距离越小，类间距离越大，聚类效果越好**。

#### Silhouette coefficient
根据维基百科的定义，他的运算是针对每一个样本的，所以计算量非常大。<br>
公式如下：
$$Sc \left(i\right)=\frac{b \left(i\right)-a\left(i\right)}{\max \{a \left(i\right),b \left(i\right)\}}$$
其中$a \left(i\right)$ 是样本 $i$ 与同簇内其他样本的平均距离，$b\left(i\right)$ 是样本 $i$ 与其他某簇中所有样本的平均距离的最小值。<br>
所以 $Sc(i)$ 只是针对其中一个样本 $i$ 的评价，对所有的 $Sc(i)$ 求平均才能得到所有的。由此可见，**其计算量也是不小** 。<br>
可见轮廓系数的值是介于 $[-1,1]$ ，越趋近于1代表内聚度和分离度都相对较优。<br>
<br>
*ref from wikipedia*
> Assume the data have been clustered via any technique, such as k-means, into $k$ clusters. For each datum $i$, let $a(i)$ be the average distance between $i$ and all other data within the same cluster. We can interpret $a(i)$ as a measure of how well $i$ is assigned to its cluster (the smaller the value, the better the assignment). We then define the average dissimilarity of point $i$ to a cluster $c$ as the average of the distance from $i$ to all points in $c$.
>
> Let $b(i)$ be the lowest average distance of $i$ to all points in any other cluster, of which $i$ is not a member. The cluster with this lowest average dissimilarity is said to be the "neighbouring cluster" of $i$ because it is the next best fit cluster for point $i$.
<br>

#### new-index

根据论文[一种基于连通性的聚类有效评价指标.pdf](file/a_clustering_validity_evaluation_index_based_on_connectivity.pdf)，现存的各种评价指标都具有一定的缺陷，并提出一种较为有效的评价指标 **new-index**。emmm... <br>
new-index虽然有优点，但是我觉得其时间复杂度太高，尤其是在计算两点联通距离的时候，时间复杂度非常大。
我们假设，每一个类所包含的点的平均数量为m那么。每个类需要计算点之间的距离$2m$次，每两个点之间的联通距离计算就需要选取最大值 $\sum_{i=0}^{m-2}A_{m-2}^{i}$次，选取最小值1次。一个类的类内紧致性就需要 $C_{m}^{2} \sum_{i=0}^{m-2}A_{m-2}^{i}$ 次最大值计算，$C_{m}^{2}$ 次最小值计算。假设我们聚类结果有$k$类，那么，就需要
$$2km$$
次点距离计算。
$$kC_{m}^{2}\sum_{i=0}^{m-2}A_{m-2}^{i}$$
次最大值计算。
$$kC_{m}^{2}$$
次最小值计算。 <br>
<br>
**然鹅，new-index 是一种对基于密度的数据集聚类评价有很好的效果的指标** <br>

在无向图 $G(V,E,W)$ 中，顶点集为 $V=\{x_1,x_2,...,x_n\}$ ，边的集合为$E=\{e_{ij}\}$，$E$ 的权重集合为$W=\{w_{ij}|e_{ij}\in E\}$，设$G$ 上的两个定点$x_i$和$x_j$之间的路经集合为$path(x_i,x_j)=\{path_1,path_2,...,path_k,...,path_p\}$，$p$为$x_i$和$x_j$间的路径数量，其中一条路径$path_k$上的边标记为$e_1^k,e_2^k,...,e_{n_k}^k$，而将对应的权值记为$w_1^k,w_2^k,...,w_{n_k}^k$，则$x_i$和$x_j$间的连通距离定义如下：
$$d_{connect}(x_i,x_j)=\min_{k=1}^p\max_{m=1}^{n_k}w_m^k$$
其中$n_k$表示$x_i$和$x_j$之间的路径$path_k$所包含的边数。<br>
定义类簇$c$的类内紧致性：
$$compact(c)=\frac{1}{\max \limits_{x,y\in c}\{d_{connect}(x,y)\}}$$
定义类间距离：
$$dist(c_i,c_j)=\min \limits_{x\in c_i,y\in c_j}d(x,y)$$
单个类$c_i$的评价指标定义如下：
$$index(c)=\min \left(dist(c_i,c_j)\times \left(\frac{|c_i|\times compact(c_i)+|c_j|\times compact(c_j)}{|c_i|+|c_j|}\right) \right)$$
其中 $|c|$ 表示类$c$中数据点的个数。<br>

对整个聚类结果$C=\{c_1,c_2,...,c_k\}$ 的有效性指标定义：
$$new-index(C)=\min_{i=1}^{k}index(c_i)$$

#果然，时间复杂度太高。！！放弃！

###自己发明基于密度的评价方式起名字：最小覆盖距离。
就是利用flood fill思想，从一个点出发，以覆盖距离去覆盖类内的点，利用二分法，看看最小的覆盖距离是多少才能把所有点都覆盖。越小越好。基于DBscan思想来搞的。

## Reference
* [从NSGA到 NSGA II](http://www.cnblogs.com/bnuvincent/p/52s68786.html)
* [知乎-谁能通俗的讲解一下NSGA-II多目标遗传算法？](https://www.zhihu.com/question/26990498)
* [NSGA-II 中文翻译](file/NSGA-II_Chinese_Translation.pdf)
* [一种基于连通性的聚类有效性评价指标](file/a_clustering_validity_evaluation_index_based_on_connectivity.pdf)
* [Wikipedia-Pareto efficiency](https://en.wikipedia.org/wiki/Pareto_efficiency)
* [聚类算法评价指标](http://blog.csdn.net/sinat_33363493/article/details/52496011)
* [Wikipedia-Cluster analysis](https://en.wikipedia.org/wiki/Cluster_analysis#Internal_evaluation)
* [浅说Davies-Bouldin指数（DBI）](http://blog.sina.com.cn/s/blog_65c8baf901016flh.html)
* [Github Repository](https://github.com/MaxLeojh/NSGA-II)
<br>
new
<br>
* [常见的六大聚类算法](https://blog.csdn.net/Katherine_hsr/article/details/79382249)
* [UC Irvine Machine Learning Repository](http://archive.ics.uci.edu/ml/index.php)
* [用于数据挖掘的聚类算法有哪些，各有何优势？](https://www.zhihu.com/question/34554321)
* [基于密度聚类的DBSCAN和kmeans算法比较](https://www.cnblogs.com/hdu-2010/p/4621258.html)
* [求数据挖掘算法中聚类算法的常用合成数据集](http://muchong.com/html/201104/3074725.html)
