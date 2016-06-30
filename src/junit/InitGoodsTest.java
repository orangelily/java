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
public class InitGoodsTest {

	@Test
	public void initGoods() {
		//
		InitGoodsService goodsService = new InitGoodsService();
		String jsonGoods = "[{ barcode: 'ITEM000000', name: '可口可乐1', unit: '瓶', category: '食品', subCategory: '碳酸饮料', price: 300.1200 },{ barcode: 'ITEM000001', name: '可口可乐2', unit: '瓶', category: '食品', subCategory: '碳酸饮料', price: 3.30 },{ barcode: 'ITEM000002', name: '可口可乐3', unit: '瓶', category: '食品', subCategory: '碳酸饮料', price: 3.00 }]";
		String infoShopCar = " [ 'ITEM000000', 'ITEM000001', 'ITEM000001', 'ITEM000001', 'ITEM000001-122', 'ITEM000002', 'ITEM000001', 'ITEM000000', 'ITEM000001']";
		String infoDiscount="[{ type: 'SINGLE_ITEM_BUY_HUNDRED_DISCOUNT_TEN', barcodes: [ 'ITEM000000', 'ITEM000001' ] }]";
		try {
			goodsService.initGoods(jsonGoods);
			goodsService.initShopCar(infoShopCar);
			goodsService.initDiscount(infoDiscount);
			System.out.println("*<没钱赚商店>购物清单没钱赚商店*购物清单：\n"+goodsService.billCheck());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		System.out.println(goodsService.getMapGd());
	}
}
