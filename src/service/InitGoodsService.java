package service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import exception.AddGoodsInCarInfoException;
import exception.DiscountInfoException;
import exception.GoodsInfoException;
import exception.ShopCarInfoException;
import model.Discount;
import model.Goods;
import model.ShoppingCar;
import model.ShoppingCar.GoodsInCar;

/**
 * 收银机 - G1 - 单品满100减10块
 * 
 * @author kl
 *
 */
public class InitGoodsService {
	// 商店内所有商品列表
	private Map<String, Goods> mapGd = new HashMap<>();
	// 商店内所有促销商品列表<条形码,折扣>
	private Map<String, Double> mapDisc = new HashMap<>();

	// 购物车商品列表
	private ShoppingCar mapShopCar = new ShoppingCar();

	// 初始化商店内部现有商品信息
	public void initGoods(String infoGoods) throws Exception {
//		if (infoGoods==null||infoGoods.trim().equals("")) {
//			return;
//		}
		Gson gson = new Gson();
		//
		List<Goods> listGd = new ArrayList<Goods>();
		try {
			// 根据json字符串，解析得到商店内所有现存的商品信息
			listGd = gson.fromJson(infoGoods, new TypeToken<List<Goods>>() {
			}.getType());

		} catch (Exception e) {
			throw new GoodsInfoException();
		}
		if (listGd != null) {
			for (int i = 0; i < listGd.size(); i++) {
				mapGd.put(listGd.get(i).getBarcode(), listGd.get(i));
			}
		}
	}

	// 初始化优惠信息
	public void initDiscount(String infoDiscount) throws Exception {
//		if (infoDiscount==null||infoDiscount.trim().equals("")) {
//			return;
//		}
		Gson gson = new Gson();
		List<Discount> discount = null;
		try {
			// 根据json字符串，解析得到商店内所有促销信息
			discount = gson.fromJson(infoDiscount, new TypeToken<List<Discount>>(){}.getType());

		} catch (Exception e) {
			throw new DiscountInfoException();
		}
		if (discount!=null&&discount.size()>=0) {
			for (int i = 0; i < discount.size(); i++) {
				//单独遍历促销信息为SINGLE_ITEM_BUY_HUNDRED_DISCOUNT_TEN：满100减10的商品
				if(discount.get(i).getType().equals("SINGLE_ITEM_BUY_HUNDRED_DISCOUNT_TEN")){
					// 促销信息 满100减10
					String[] barcodes = discount.get(i).getBarcodes();
					if (barcodes!=null) {
						for (int j = 0; j < barcodes.length; j++) {
							mapDisc.put(barcodes[j], 10.0);
						}
					}
				}
			}
		}
		//一种促销信息的使用方式
//		if (discount != null && discount.getBarcodes() != null) {
//			for (int i = 0; i < discount.getBarcodes().length; i++) {
//				// 促销信息 满100减10
//				mapDisc.put(discount.getBarcodes()[i], 10.0);
//			}
//		}
	}

	// 初始化购物车信息
	public void initShopCar(String infoShopCar) throws Exception {
//		if (infoShopCar==null||infoShopCar.trim().equals("")) {
//			return;
//		}
		// 购物车内商品信息
		ShoppingCar sc = new ShoppingCar();
		try {
			/*
			 * 输入格式（样例）： // javascript [ 'ITEM000001', 'ITEM000001',
			 * 'ITEM000001', 'ITEM000001', 'ITEM000001', 'ITEM000003-2',
			 * 'ITEM000005', 'ITEM000005', 'ITEM000005' ]
			 */
			// 根据输入字符串，拆分去掉首尾[] , 得到购物车的商品信息
			String newShopCar = infoShopCar.trim();
			Integer index = newShopCar.indexOf('[');
			if (index<0) {
				throw new ShopCarInfoException();
			}
			index = newShopCar.indexOf(']');
			if (index<0) {
				throw new ShopCarInfoException();
			}
			newShopCar = newShopCar.substring(1, newShopCar.length() - 1).trim();
			String[] barcodeArr = newShopCar.split(",");
			for (int i = 0; i < barcodeArr.length; i++) {
				// 去掉字符串中的首尾单引号
				String barcode = barcodeArr[i].trim();
				barcode=barcode.substring(1, barcode.length() - 1);
				int count = 1;
				//拆分barcode类型为'ITEM000003-2'的情况
				String[] countSpli = barcode.split("-");
				if (countSpli.length<=1) {
					count=1;
				}else {
					count=Integer.parseInt(countSpli[1]);
				}
				//判断购买商品的条形码是否属于商店商品条形码数据
				if (mapGd.get(countSpli[0])==null) {
					throw new AddGoodsInCarInfoException(countSpli[0]);
				}else {
					// 将购买商品添加到购物车
					mapShopCar.addGoods(mapGd.get(countSpli[0]),count);
				}
				
			}
		} catch (Exception e) {
			if(e instanceof AddGoodsInCarInfoException){
				throw e;
			}else {
				throw new ShopCarInfoException();
				}
		}
	}

	// 结算购物车
	public String billCheck() throws Exception {
		//价格保留两位小数
		DecimalFormat df = new DecimalFormat("#.00");
		
		//商品的总价格（不使用优惠）
		Double totalPrice = 0.0;
		//商品的总价格（优惠后的）
		Double discountTotalPrice = 0.0;
				
		//拼接商品结算列表的barcode字符串
		StringBuffer sb = new StringBuffer();
		//获取购物车中的商品信息
		Map<String, GoodsInCar> mapInCar = mapShopCar.getShopCar();
		//定义购物车中的优惠商品信息
		Map<String, GoodsInCar> discountInCar = new HashMap<String,GoodsInCar>();
				
		Set setInCar = mapInCar.entrySet();
		Iterator it = setInCar.iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Entry) it.next();
			String key = (String) entry.getKey();
			//获取商品的数量、价格等信息
			GoodsInCar value = (GoodsInCar) entry.getValue();
			sb.append("名称：");
			sb.append(value.goods.getName());
			sb.append(",数量：");
			sb.append(value.count);
			sb.append(value.goods.getUnit());
			sb.append("，单价：");
			sb.append(value.goods.getPrice());
			sb.append("(元)，小计：");
			
			//根据折扣优惠信息，判断是否重新计算优惠商品的subprice
			if (mapDisc.containsKey(key)) {
				
				//折扣后的价格=原价格-优惠价(满100-10，可累计)
				value.discountPrice =value.subPrice- (value.subPrice.intValue()/100)*mapDisc.get(key);
				//标记优惠的商品信息
				discountInCar.put(key, value);
				totalPrice+=value.subPrice;
				discountTotalPrice +=value.discountPrice;
				/*名称：篮球，数量：2个，单价：98.00(元)，
				 * 小计：176.00(元)，优惠：10.00（元) 
				 * 名称：可口可乐，数量：3瓶，单价：3.00(元)，小计：9.00(元) 
				 * 名称：羽毛球，数量：5个，单价：1.00(元)，小计：5.00(元) 
				 * 名称：苹果，数量：2斤，单价：5.50(元)，小计：11.00(元)")
				 */
				
				sb.append(df.format(value.discountPrice));
				sb.append("(元)，优惠：");
				sb.append(value.subPrice-value.discountPrice);
				
			}else {
				totalPrice+=value.subPrice;
				/*	名称：可口可乐，数量：3瓶，单价：3.00(元)，小计：9.00(元) 
				 * 	名称：羽毛球，数量：5个，单价：1.00(元)，小计：5.00(元) 
				 * 	名称：苹果，数量：2斤，单价：5.50(元)，小计：11.00(元)
				 */
				//表示未优惠，折扣后价格==原总价
				discountTotalPrice +=value.subPrice;
				

				sb.append(df.format(value.subPrice));
				sb.append("(元)");
			}
			sb.append("\n");
		}
//		sb.append("<div style='border-top:1px;border-color:black;'></div>");
		/*
		 * 总计：25.00(元)
		 */
		if (discountInCar==null||discountInCar.size()<=0) {
			sb.append("总计：");
			sb.append(df.format(totalPrice));
			sb.append("元");
		}else {

//			sb.append("<div style='border-top:1px;border-color:black;'></div>");
			sb.append("单品满100减10块商品： ");
			//遍历有优惠的商品信息
			Set setInDicount = discountInCar.entrySet();
			Iterator itDis = setInDicount.iterator();
			while (itDis.hasNext()) {
				Map.Entry entry = (Entry) itDis.next();
				String key = (String) entry.getKey();
				//获取优惠商品的原总价、折扣价等信息
				GoodsInCar goodsInCar = (GoodsInCar) entry.getValue();
				/*
				 * 单品满100减10块商品： 篮球，原价：186.00(元)，优惠： 10.00(元)
				 * 总计：201.00(元) 节省：10.00(元)
				 */
				sb.append("\n");
				sb.append(goodsInCar.goods.getName());
				sb.append(",原价");
				sb.append(df.format(goodsInCar.subPrice));
				sb.append("(元),优惠");
				sb.append(goodsInCar.subPrice-goodsInCar.discountPrice);
				sb.append("元");
			}
			sb.append("\n");
			sb.append("总计：");
			sb.append(df.format(discountTotalPrice));
			sb.append("元 节省：");
			sb.append(totalPrice-discountTotalPrice);
			sb.append("元");
		}
		return sb.toString();
	}

	public Map<String, Goods> getMapGd() {
		return mapGd;
	}

	public void setMapGd(Map<String, Goods> mapGd) {
		this.mapGd = mapGd;
	}

	public Map<String, Double> getMapDisc() {
		return mapDisc;
	}

	public void setMapDisc(Map<String, Double> mapDisc) {
		this.mapDisc = mapDisc;
	}
}
