# java
收银机-G1-单品满100减10块系统说明书
访问地址：http://yimin.webmeteor.cn/CashRegister
1.	系统各模块介绍
收银机打印小票的小系统是基于Eclipse平台，使用JavaEE+Tomcat实现的Web系统，以CashRegister命名
其中src类文件下包含五个package包：action、exception、junit、model、service；WebContent文件目录下包含WEB-INF(lib资源包文件、web.xml配置文件)、jsp文件。
1.1操作action
action包中是控制模块，作为媒介控制前台页面中输入的数据与service模块的交互；
由于javaweb中的servlet不是线程安全的，所以对post方法进行了同步操作（ps:经过分析，由于GoodsInputActio未用到公共属性，此例在不使用同步代码时也能正常工作，但还是建议加上同步操作）。
GoodsInputAction是一个Servlet，通过request获取三个信息对象数据，调用执行service的商品、购物车、促销的初始化，以及结算方法，并返回给jsp，没有异常情况下打印购物清单的小票，否则打印error信息。

1.2数据模型model
1.2.1商品信息类Goods
商品信息，包含：条形码（伪），名称，数量单位，单价和类别等；
1.2.2促销信息类Discount
促销信息，包含：优惠类型，满足该优惠类型的所有商品条形码；
1.2.3购物车商品信息ShoppingCar
购物车商品信息，包含：购物车中的商品信息类GoodInCar，该类包含商品信息Goods，数量，累计原价，累计优惠后的价格；购物车添加商品信息方法addGoods(Goods goods,int count),表示添加数量为count的某类商品goods。

1.3服务类service：InitGoodsService
包含商品入库、优惠活动设置、购物车结算等功能，以及异常处理。
1.3.1 初始化商店内所有商品信息
initGoods(String infoGoods);
根据商品对象的结构，解析json数据，往商店仓库添加商品信息，以Map结构记录为mapGd,Map的格式<条形码，商品具体信息对象>

1.3.2 初始化商店内的优惠促销信息
initDiscount(String infoDiscount);
infoDiscount中包含优惠类别，参与该优惠的商品条形码，其数据结构的数据格式如下：
[{type:'SINGLE_ITEM_BUY_HUNDRED_DISCOUNT_TEN', barcodes: [ 'ITEM000000', 'ITEM000001' ] } ]
通过解析json数据，可以根据优惠类型,确定该商品选择哪种优惠方法执行后续工作，如满100减10，得到商店内所有满100减10促销的商品列表<条形码,折扣>存到结构为Map的mapDisc中。

1.3.3购物车中的商品信息
initShopCar (String infoShopCar);
根据购物车中商品对象的结构：[ 'ITEM000001', 'ITEM000001', 'ITEM000001', 'ITEM000001', 'ITEM000001', 'ITEM000003-2', 'ITEM000005', 'ITEM000005', 'ITEM000005' ]，获取购买商品的“条形码-数量”。根据条形码查商品仓库，当所有条形码属于商店的商品，即mapGd中包含该条形码的商品数据，作为待结算商品存到结构为Map的mapShopCar中，否则记录非法商品条形码。

1.3.4 购物车结算billCheck
根据购物车中待结算的商品信息mapShopCar，以及mapDisc中享受优惠的商品条形码信息，结算商品，记录结算清单的文本，打印小票。
1.4 exception
该package下包含四个异常处理类：商品入库信息异常、优惠促销信息异常、购物车添加商品条形码信息异常、非法商品加入购物车异常。
1.5	测试包junit
依次测试service中的方法是否可行，完善系统，测试运行效果如图1-2所示：
 
图1-2 junit测试

1.6	WebContent目录
包含信息输入页面goodsInput.jsp、lib资源包文件保存gson-2.2.4.jar、jstl-1.2.jar、WEB_INF存放系统配置文件web.xml用来，配置Servlet相关信息。

2.	系统使用介绍
2.1系统主界面
	启动Tomcat服务器，将CashRegister项目发布到服务器上，便可访问系统的主界面，主界面中包含三个输入区域供用户输入合理的数据，下方为“商品结算”按钮，如图2-1所示。
  
图2-1 系统主界面
2.2 数据输入规范
2.2.1商店商品信息
json输入格式：
[{ barcode: 'ITEM000001', name: '可口可乐1', unit: '瓶', category: '食品', subCategory: '碳酸饮料', price: 13.00 },
{ barcode: 'ITEM000002', name: '可口可乐2', unit: '瓶', category: '食品', subCategory: '碳酸饮料', price: 8.00 }]
当输入不正确的json数据，如缺少“}”，将提示错误信息，如图2-2所示：
 
图2-2 商店商品json数据错误
2.2.2购物车商品条形码
输入格式：
['ITEM000001', 'ITEM000001', 'ITEM000001', 'ITEM000001', 'ITEM000001', 'ITEM000003-2', 'ITEM000005', 'ITEM000005', 'ITEM000005' ]
其中对'ITEM000003-2'来说,"-"之前的是标准的条形码,"-"之后的是商品数量。当我们购买需要称量的物品的时候,由称量的机器生成此类条形码,收银机负责识别生成小票。
当输入非本商店条形码信息，进行结算时，将提示错误信息，如图2-3所示：
 
图2-3购物车条形码数据错误
2.2.3优惠促销信息
输入格式：
[{ type: 'SINGLE_ITEM_BUY_HUNDRED_DISCOUNT_TEN', barcodes: [ 'ITEM000001', 'ITEM000004' ] }   ]
当输入错误格式的json数据，添加一个多余的逗号，进行结算时，将提示错误信息，如图2-4所示：
 
图2-4促销信息数据格式错误
2.3 测试用例
2.3.1无促销信息
商店所有商品：[{ barcode: 'ITEM000001', name: '可口可乐1', unit: '瓶', category: '食品', subCategory: '碳酸饮料', price: 13.00} ]
购物车扫码：[ 'ITEM000001', 'ITEM000001', 'ITEM000001', 'ITEM000001', 'ITEM000001', 'ITEM000001-2', 'ITEM000001' ]
在主界面中对应输入以上合理的数据，点击商品结算，结果如图2-5所示：
 
 
图2-5 无促销打印小票
2.3.2 有商品享受促销信息
商店所有商品：[{ barcode: 'ITEM000001', name: '可口可乐1', unit: '瓶', category: '食品', subCategory: '碳酸饮料', price: 13.30 },
{ barcode: 'ITEM000002', name: '可口可乐2', unit: '瓶', category: '食品', subCategory: '碳酸饮料', price: 8.20 }]
购物车扫码：[ 'ITEM000001-120', 'ITEM000001', 'ITEM000001', 'ITEM000002-30'   ]
促销信息：[{ type: 'SINGLE_ITEM_BUY_HUNDRED_DISCOUNT_TEN', barcodes: [ 'ITEM000001', 'ITEM000002' ] }   ]
在主界面中对应输入以上合理的数据，点击商品结算，结果如图2-5所示：
 
图2-5享受促销打印小票


附录：
需求描述
商店里进行购物结算时会使用收银机系统，这台收银机会在结算时根据客户的购物车中的商品和商店正在进行的优惠活动进行结算和打印购物小票。
•	已知商品信息包含：名称，数量单位，单价，类别和条形码（伪）。以下是单个商品对象的结构：  javascript { barcode: 'ITEM000000', name: '可口可乐', unit: '瓶', category: '食品', subCategory: '碳酸饮料', price: 3.00 } 
•	已知我们可以对收银机进行设置，使之支持各种优惠。
我们需要实现一个名为打印小票的小模块，收银机会将输入的数据转换成一个JSON数据然后一次性传给我们这个小模块，我们将从控制台中输出结算清单的文本。
输入格式（样例）：
javascript [ 'ITEM000001', 'ITEM000001', 'ITEM000001', 'ITEM000001', 'ITEM000001', 'ITEM000003-2', 'ITEM000005', 'ITEM000005', 'ITEM000005' ]
其中对'ITEM000003-2'来说,"-"之前的是标准的条形码,"-"之后的是商品数量。当我们购买需要称量的物品的时候,由称量的机器生成此类条形码,收银机负责识别生成小票。
该商店正在对部分商品进行“单品满100减10块”的优惠活动。解释：
•	“单品满100减10块”是指，每当某一款商品算完小计之后，如果满100，则减去10元再收取，可累计优惠。
•	店员设置该优惠哪些条形码对应的商品可以享受此优惠。
要求写代码支持上述的功能，并根据输入和设置的不同，输出下列小票。
小票内容及格式（样例）：
•	当购买的商品符合“单品满100减10块”优惠条件的商品时：
` *<没钱赚商店>购物清单没钱赚商店* 名称：篮球，数量：2个，单价：98.00(元)，小计：176.00(元)，优惠：10.00（元) 名称：可口可乐，数量：3瓶，单价：3.00(元)，小计：9.00(元) 名称：羽毛球，数量：5个，单价：1.00(元)，小计：5.00(元) 名称：苹果，数量：2斤，单价：5.50(元)，小计：11.00(元)

单品满100减10块商品：篮球，原价：186.00(元)，优惠： 10.00(元)

总计：201.00(元) 节省：10.00(元)

`
•	当购买的商品没有符合“单品满100减10块”优惠条件的商品时：
` *<没钱赚商店>购物清单没钱赚商店* 名称：可口可乐，数量：3瓶，单价：3.00(元)，小计：9.00(元) 名称：羽毛球，数量：5个，单价：1.00(元)，小计：5.00(元) 名称：苹果，数量：2斤，单价：5.50(元)，小计：11.00(元)

总计：25.00(元)

`
数据格式
1	每一个商品对象的结构如下（样例）：  javascript { barcode: 'ITEM000000', name: '可口可乐', unit: '瓶', category: '食品', subCategory: '碳酸饮料', price: 3.00 } 
2	促销信息对象的数组格式（样例）：  javascript [ { type: 'SINGLE_ITEM_BUY_HUNDRED_DISCOUNT_TEN', barcodes: [ 'ITEM000000', 'ITEM000001' ] } ] 
注意: type和barcodes是促销信息对象的主体结构，也可以适当扩展其它属性，以适应程序的扩展性。
作业要求
1	请完成全部需求，并输出样例格示的小票;
2	程序实现语言（JavaScript、C#、Java、Scala等）和实现形式（Console、Web、Desktop、APP等）不限;
3	请在保证实现代码的同时，尽量保持代码的整洁性、可读性、易于扩展和维护性等；
加分项
满足以下的条件（但并不仅限于这些条件），将会成为你的加分项：
1	良好的抽象和设计；
2	完善的测试，TDD开发；
3	git小步提交；
良好的构建和编码规范；

