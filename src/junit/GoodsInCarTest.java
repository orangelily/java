package junit;

import org.junit.Test;

import model.Goods;
import model.ShoppingCar;
/**
 * 测试类
 * @author kl
 *
 */
public class GoodsInCarTest {

	@Test
	public void addGoods() {
		ShoppingCar sc = new ShoppingCar();
		Goods gd1=new Goods("ITM-000", "篮球", "个","体育器材", "球类", 98.00);
		Goods gd2=new Goods("ITM-000", "篮球", "个","体育器材", "球类", 98.00);
		Goods gd3=new Goods("ITM-000", "篮球", "个","体育器材", "球类", 98.00);
		Goods gd4=new Goods("ITM-001", "排球", "个","体育器材", "球类", 58.00);
		sc.addGoods(gd1,1);
		sc.addGoods(gd2,1);
		sc.addGoods(gd3,1);
		sc.addGoods(gd4,1);
		System.out.println(sc.getTotalPrice());
	}
}
