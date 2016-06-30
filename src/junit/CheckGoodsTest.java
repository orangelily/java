package junit;

import org.junit.Test;

import model.Goods;
import model.ShoppingCar;
import service.InitGoodsService;
/**
 * 测试类
 * @author kl
 *
 */
public class CheckGoodsTest {

	@Test
	public void initGoods() {
		//
		InitGoodsService goodsService = new InitGoodsService();
		String jsonGoods = "[{ barcode: 'ITEM000001', name: '可口可乐', unit: '瓶', category: '食品', subCategory: '碳酸饮料', price: 3.00 },{ barcode: 'ITEM000000', name: '可口可乐', unit: '瓶', category: '食品', subCategory: '碳酸饮料', price: 3.00 },{ barcode: 'ITEM000001', name: '可口可乐', unit: '瓶', category: '食品', subCategory: '碳酸饮料', price: 3.00 }]";
		try {
			goodsService.initGoods(jsonGoods);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(goodsService.getMapGd());
	}
}
