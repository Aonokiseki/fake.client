package fake.client.util;

import java.util.List;

public class MathUtil {
	private MathUtil() {}
	
	/**
     * 两个向量(终点)之间的闵可夫斯基距离. <br>元素类型必须为Number类的子类,两个向量的长度必须相等.
     * 
     * @param vector1 向量1
     * @param vector2 向量2
     * @param dimension 维度, dimension不小于1. <br>1为曼哈顿距离;2为欧几里得距离;数字越大越接近切比雪夫距离
     * @return Double 距离
     */
    public static <T extends Number> Double minkowskiDistance(List<T> vector1, List<T> vector2, int dimension){
    	if(vector1.size() != vector2.size()) {
    		throw new IllegalArgumentException("One vector's size don't equal the other's");
    	}
    	Double result = Double.parseDouble("0.0");
    	if(dimension < 1)
    		dimension = 1;
    	for(int i=0, vectorSize=vector1.size(); i<vectorSize; i++)
    		result+=Math.pow((vector1.get(i).doubleValue() - vector2.get(i).doubleValue()), dimension);
    	result = Math.pow(result, (1.0/((double)dimension)));
    	return result;
    }
    
    /**
     * 余弦相似度
     * @param <T>
     * @param vector1
     * @param vector2
     * @return
     */
    public static <T extends Number> double cosine(List<T> vector1, List<T> vector2) {
    	if(vector1.size() != vector2.size())
    		throw new IllegalArgumentException("One vector's size don't equal the other's");
    	double innerProduct = innerProduct(vector1, vector2);
    	double length1 = vectorLength(vector1);
		double length2 = vectorLength(vector2);
		return innerProduct / (length1 * length2);
    }
    private static <T extends Number> double innerProduct(List<T> vector1, List<T> vector2){
    	double innerProduct = 0.0;
    	for(int i = 0, size = vector1.size(); i < size; i++)
    		innerProduct += vector1.get(i).doubleValue() * vector2.get(i).doubleValue();
    	return innerProduct;
    }
    private static <T extends Number> double vectorLength(List<T> vector) {
    	double sum = 0.0;
		for(int i = 0, size = vector.size(); i < size; i++)
			sum += Math.pow(vector.get(i).doubleValue(), 2);
		return Math.pow(sum, 0.5);
    }
}
