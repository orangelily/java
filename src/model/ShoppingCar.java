package model;

import java.util.HashMap;
import java.util.Map;
/**
 * 购物单
 * @author kl
 *
 */
public class ShoppingCar {
	//
	public class GoodsInCar{
		public Goods goods;
		public Integer count=0;
		public Double subPrice=0.0;//同种商品未进行优惠的累计价格
		public Double discountPrice=0.0;//优惠后的价格
		public GoodsInCar(Goods goods){
			this.goods = goods;
		}
	}
	
	private Map<String, GoodsInCar> shopCar = new HashMap<String, GoodsInCar>();
	private Double totalPrice=0.0;
	
	public void addGoods(Goods goods,int count){
		if (goods==null||goods.getBarcode()==null||goods.getBarcode().equals("")) {
			return;
		}
		if (shopCar.containsKey(goods.getBarcode())) {
			GoodsInCar gc=shopCar.get(goods.getBarcode());
			gc.count+=count;
			gc.subPrice=gc.count*goods.getPrice();
			totalPrice+=goods.getPrice();
			shopCar.put(goods.getBarcode(), gc);
			
		}else {
			GoodsInCar gc = new GoodsInCar(goods);
			gc.count += count;
			gc.subPrice=gc.count*goods.getPrice();
			totalPrice+=goods.getPrice();
			
			shopCar.put(goods.getBarcode(), gc);
			
		}
		
	}

	public Map<String, GoodsInCar> getShopCar() {
		return shopCar;
	}

	public void setShopCar(Map<String, GoodsInCar> shopCar) {
		this.shopCar = shopCar;
	}

	public Double getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(Double totalPrice) {
		this.totalPrice = totalPrice;
	}
}
