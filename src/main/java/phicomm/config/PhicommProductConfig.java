package phicomm.config;

import org.apache.commons.lang3.StringUtils;
import phicomm.model.PhicommProduct;

import java.util.ArrayList;
import java.util.List;

public class PhicommProductConfig {
	
	public static List<PhicommProduct> PHICOMM_PRODUCT_CONFIG = new ArrayList<PhicommProduct>();
	
	
	static{
		PhicommProduct K2_WHITE = new PhicommProduct();
		K2_WHITE.setId(1);
		K2_WHITE.setName("K2");
		K2_WHITE.setColor("白色");
		K2_WHITE.setSkuCode("901002388");
		K2_WHITE.setBuyPage(String.format("%s%s",PhicommConstants.PHICOMM_HOST, "/index.php/cart-fastbuy-5-%s.html"));
		K2_WHITE.setImgSrc("https://imgmall.phicomm.com/56/4a/496d4974498f.jpg");

		PhicommProduct K2_BLUE = new PhicommProduct();
		K2_BLUE.setId(2);
		K2_BLUE.setName("K2");
		K2_BLUE.setColor("蓝色");
		K2_BLUE.setSkuCode("901002449");
		K2_BLUE.setBuyPage(String.format("%s%s", PhicommConstants.PHICOMM_HOST,"/index.php/cart-fastbuy-6-%s.html"));
		K2_BLUE.setImgSrc("https://imgmall.phicomm.com/15/fc/f451d753e401.jpg");

		PhicommProduct K2P_WHITE = new PhicommProduct();
		K2P_WHITE.setId(3);
		K2P_WHITE.setName("K2P");
		K2P_WHITE.setColor("白色");
		K2P_WHITE.setSkuCode("901002451");
		K2P_WHITE.setBuyPage(String.format("%s%s", PhicommConstants.PHICOMM_HOST,"/index.php/cart-fastbuy-7-%s.html"));
		K2P_WHITE.setImgSrc("https://imgmall.phicomm.com/31/66/631795ff98ec.jpg");

		PhicommProduct K2P_GOLDEN = new PhicommProduct();
		K2P_GOLDEN.setId(4);
		K2P_GOLDEN.setName("K2P");
		K2P_GOLDEN.setColor("金色");
		K2P_GOLDEN.setSkuCode("901002489");
		K2P_GOLDEN.setBuyPage(String.format("%s%s", PhicommConstants.PHICOMM_HOST,"/index.php/cart-fastbuy-8-%s.html"));
		K2P_GOLDEN.setImgSrc("https://imgmall.phicomm.com/bb/7d/7ebed4862685.jpg");

		PhicommProduct K3_WHITE = new PhicommProduct();
		K3_WHITE.setId(5);
		K3_WHITE.setName("K3");
		K3_WHITE.setColor("白色");
		K3_WHITE.setSkuCode("901002439");
		K3_WHITE.setBuyPage(String.format("%s%s", PhicommConstants.PHICOMM_HOST,"/index.php/cart-fastbuy-9-%s.html"));
		K3_WHITE.setImgSrc("https://imgmall.phicomm.com/88/82/88820e3de1d5.jpg");

		PhicommProduct K3_GOLDEN = new PhicommProduct();
		K3_GOLDEN.setId(6);
		K3_GOLDEN.setName("K3");
		K3_GOLDEN.setColor("金色");
		K3_GOLDEN.setSkuCode("901002562");
		K3_GOLDEN.setBuyPage(String.format("%s%s", PhicommConstants.PHICOMM_HOST,"/index.php/cart-fastbuy-10-%s.html"));
		K3_GOLDEN.setImgSrc("https://imgmall.phicomm.com/30/53/5e049d54d2fe.jpg");

		PhicommProduct K3C_WHITE = new PhicommProduct();
		K3C_WHITE.setId(7);
		K3C_WHITE.setName("K3C");
		K3C_WHITE.setColor("白色");
		K3C_WHITE.setSkuCode("901002415");
		K3C_WHITE.setBuyPage(String.format("%s%s", PhicommConstants.PHICOMM_HOST,"/index.php/cart-fastbuy-11-%s.html"));
		K3C_WHITE.setImgSrc("https://imgmall.phicomm.com/28/9d/968d13807e29.jpg");

		PhicommProduct S7_WHITE = new PhicommProduct();
		S7_WHITE.setId(8);
		S7_WHITE.setName("S7");
		S7_WHITE.setColor("白色");
		S7_WHITE.setSkuCode("911000057");
		S7_WHITE.setBuyPage(String.format("%s%s", PhicommConstants.PHICOMM_HOST,"/index.php/cart-fastbuy-12-%s.html"));
		S7_WHITE.setImgSrc("https://imgmall.phicomm.com/d7/57/52740e7cf6fe.jpg");

		PhicommProduct T1_BLACK = new PhicommProduct();
		T1_BLACK.setId(9);
		T1_BLACK.setName("T1");
		T1_BLACK.setColor("黑色");
		T1_BLACK.setSkuCode("911000019");
		T1_BLACK.setBuyPage(String.format("%s%s", PhicommConstants.PHICOMM_HOST,"/index.php/cart-fastbuy-13-%s.html"));
		T1_BLACK.setImgSrc("https://imgmall.phicomm.com/dc/47/46cbda40bb68.jpg");

		PhicommProduct W1_BLACK = new PhicommProduct();
		W1_BLACK.setId(10);
		W1_BLACK.setName("W1");
		W1_BLACK.setColor("黑色");
		W1_BLACK.setSkuCode("911000092");
		W1_BLACK.setBuyPage(String.format("%s%s",PhicommConstants.PHICOMM_HOST, "/index.php/cart-fastbuy-14-%s.html"));
		W1_BLACK.setImgSrc("https://imgmall.phicomm.com/a0/b3/be064243b73c.jpg");

		PhicommProduct M1_SILVERY = new PhicommProduct();
		M1_SILVERY.setId(11);
		M1_SILVERY.setName("M1");
		M1_SILVERY.setColor("银色");
		M1_SILVERY.setSkuCode("912000030");
		M1_SILVERY.setBuyPage(String.format("%s%s", PhicommConstants.PHICOMM_HOST,"/index.php/cart-fastbuy-15-%s.html"));
		M1_SILVERY.setImgSrc("https://imgmall.phicomm.com/04/d0/d84844d8ed0c.jpg");

		PhicommProduct M1_ROSE_GOLD = new PhicommProduct();
		M1_ROSE_GOLD.setId(12);
		M1_ROSE_GOLD.setName("M1");
		M1_ROSE_GOLD.setColor("玫瑰色");
		M1_ROSE_GOLD.setSkuCode("912000025");
		M1_ROSE_GOLD.setBuyPage(String.format("%s%s",PhicommConstants.PHICOMM_HOST, "/index.php/cart-fastbuy-16-%s.html"));
		M1_ROSE_GOLD.setImgSrc("https://imgmall.phicomm.com/e6/39/36678f9a40ff.jpg");

		PhicommProduct X3_GOLDEN = new PhicommProduct();
		X3_GOLDEN.setId(13);
		X3_GOLDEN.setName("X3");
		X3_GOLDEN.setColor("金色");
		X3_GOLDEN.setSkuCode("912000039");
		X3_GOLDEN.setBuyPage(String.format("%s%s", PhicommConstants.PHICOMM_HOST,"/index.php/cart-fastbuy-17-%s.html"));
		X3_GOLDEN.setImgSrc("https://imgmall.phicomm.com/9a/83/8da1fce25075.jpg");

		PhicommProduct A1_WHITE = new PhicommProduct();
		A1_WHITE.setId(14);
		A1_WHITE.setName("A1");
		A1_WHITE.setColor("白色");
		A1_WHITE.setSkuCode("912000066");
		A1_WHITE.setBuyPage(String.format("%s%s",PhicommConstants.PHICOMM_HOST, "/index.php/cart-fastbuy-90-%s.html"));
		A1_WHITE.setImgSrc("https://imgmall.phicomm.com/49/c9/c19d95f19b90.jpg");

		PhicommProduct DC1_WHITE = new PhicommProduct();
		DC1_WHITE.setId(15);
		DC1_WHITE.setName("DC1");
		DC1_WHITE.setColor("白色");
		DC1_WHITE.setSkuCode("911000129");
		DC1_WHITE.setBuyPage(String.format("%s%s",PhicommConstants.PHICOMM_HOST, "/index.php/cart-fastbuy-24-%s.html"));
		DC1_WHITE.setImgSrc("https://imgmall.phicomm.com/20/1c/140df45b75f5.jpg");

		PhicommProduct N1_WHITE = new PhicommProduct();
		N1_WHITE.setId(16);
		N1_WHITE.setName("N1");
		N1_WHITE.setColor("白色");
		N1_WHITE.setSkuCode("911000104");
		N1_WHITE.setBuyPage(String.format("%s%s",PhicommConstants.PHICOMM_HOST, "/index.php/cart-fastbuy-29-%s.html"));
		N1_WHITE.setImgSrc("https://imgmall.phicomm.com/33/0f/0a3a27d6f56f.jpg");

		PhicommProduct R1_GRAY = new PhicommProduct();
		R1_GRAY.setId(17);
		R1_GRAY.setName("R1");
		R1_GRAY.setColor("星空灰");
		R1_GRAY.setSkuCode("901002497");
		R1_GRAY.setBuyPage(String.format("%s%s",PhicommConstants.PHICOMM_HOST, "/index.php/cart-fastbuy-143-%s.html"));
		R1_GRAY.setImgSrc("https://imgmall.phicomm.com/36/bc/b66c74adfc9a.jpg");

		PhicommProduct R1_BLACK = new PhicommProduct();
		R1_BLACK.setId(18);
		R1_BLACK.setName("R1");
		R1_BLACK.setColor("黑色");
		R1_BLACK.setSkuCode("901002712");
		R1_BLACK.setBuyPage(String.format("%s%s",PhicommConstants.PHICOMM_HOST, "/index.php/cart-fastbuy-147-%s.html"));
		R1_BLACK.setImgSrc("https://imgmall.phicomm.com/cb/8f/8db97506ad2c.jpg");
		
		PhicommProduct H1_WHITE = new PhicommProduct();
		H1_WHITE.setId(19);
		H1_WHITE.setName("H1");
		H1_WHITE.setColor("白色");
		H1_WHITE.setSkuCode("912000049");
		H1_WHITE.setBuyPage(String.format("%s%s",PhicommConstants.PHICOMM_HOST, "/index.php/cart-fastbuy-144-%s.html"));
		H1_WHITE.setImgSrc("https://imgmall.phicomm.com/bf/51/57f96358c468.jpg");

		PhicommProduct H1_N1_GROUP = new PhicommProduct();
		H1_N1_GROUP.setId(20);
		H1_N1_GROUP.setName("H1_N1");
		H1_N1_GROUP.setColor("套装");
		H1_N1_GROUP.setSkuCode("700000001");
		H1_N1_GROUP.setBuyPage(String.format("%s%s", PhicommConstants.PHICOMM_HOST,"/index.php/cart-fastbuy-97-%s.html"));
		H1_N1_GROUP.setImgSrc("https://imgmall.phicomm.com/1c/23/25c1f3be9bca.jpg");

		PhicommProduct K2T_WHITE = new PhicommProduct();
		K2T_WHITE.setId(21);
		K2T_WHITE.setName("K2T");
		K2T_WHITE.setColor("白色");
		K2T_WHITE.setSkuCode("901002611");
		K2T_WHITE.setBuyPage(String.format("%s%s", PhicommConstants.PHICOMM_HOST,"/index.php/cart-fastbuy-120-%s.html"));
		K2T_WHITE.setImgSrc("https://imgmall.phicomm.com/c7/4e/4b7528afca5e.jpg");
		
		PhicommProduct W2_BLACK = new PhicommProduct();
		W2_BLACK.setId(22);
		W2_BLACK.setName("W2");
		W2_BLACK.setColor("黑色");
		W2_BLACK.setSkuCode("911000005");
		W2_BLACK.setBuyPage(String.format("%s%s",PhicommConstants.PHICOMM_HOST, "/index.php/cart-fastbuy-119-%s.html"));
		W2_BLACK.setImgSrc("https://imgmall.phicomm.com/f4/d7/d5418c00f863.jpg");
		
		PhicommProduct S7PE_WHITE = new PhicommProduct();
		S7PE_WHITE.setId(23);
		S7PE_WHITE.setName("S7PE");
		S7PE_WHITE.setColor("白色");
		S7PE_WHITE.setSkuCode("911000132");
		S7PE_WHITE.setBuyPage(String.format("%s%s", PhicommConstants.PHICOMM_HOST,"/index.php/cart-fastbuy-153-%s.html"));
		S7PE_WHITE.setImgSrc("https://imgmall.phicomm.com/9e/b9/bce28c5770e3.jpg");

		PhicommProduct A1_M1 = new PhicommProduct();
		A1_M1.setId(23);
		A1_M1.setName("A1_M1");
		A1_M1.setColor("套装");
		A1_M1.setSkuCode("701009891");
		A1_M1.setBuyPage(String.format("%s%s", PhicommConstants.PHICOMM_HOST,"/index.php/cart-fastbuy-154-%s.html"));
		A1_M1.setImgSrc("https://imgmall.phicomm.com/2f/3c/33fd82d4e334.jpg");

		PhicommProduct TC1 = new PhicommProduct();
		TC1.setId(24);
		TC1.setName("TC1");
		TC1.setColor("白色");
		TC1.setSkuCode("911000053");
		TC1.setBuyPage(String.format("%s%s", PhicommConstants.PHICOMM_HOST,"/index.php/cart-fastbuy-155-%s.html"));
		TC1.setImgSrc("https://imgmall.phicomm.com/95/fd/fc53e87f0a89.jpg");

		PhicommProduct K2G = new PhicommProduct();
		K2G.setId(25);
		K2G.setName("K2G");
		K2G.setColor("白色");
		K2G.setSkuCode("901002735");
		K2G.setBuyPage(String.format("%s%s", PhicommConstants.PHICOMM_HOST,"/index.php/cart-fastbuy-156-%s.html"));
		K2G.setImgSrc("https://imgmall.phicomm.com/9b/bf/bbbc450e9947.jpg");

		PhicommProduct E_START_I = new PhicommProduct();
		E_START_I.setId(26);
		E_START_I.setName("E-STAR-I");
		E_START_I.setColor("以太星球云算力");
		E_START_I.setSkuCode("903002247");
		E_START_I.setBuyPage(String.format("%s%s", PhicommConstants.PHICOMM_HOST,"/index.php/cart-fastbuy-190-%s.html"));
		E_START_I.setImgSrc("https://imgmall.phicomm.com/56/2e/2a6c2850698c.jpg");

		PhicommProduct LIGHT = new PhicommProduct();
		LIGHT.setId(27);
		LIGHT.setName("LIGHT");
		LIGHT.setColor("母婴灯");
		LIGHT.setSkuCode("912000058");
		LIGHT.setBuyPage(String.format("%s%s", PhicommConstants.PHICOMM_HOST,"/index.php/cart-fastbuy-198-%s.html"));
		LIGHT.setImgSrc("https://imgmall.phicomm.com/0d/ce/cbde73e97a48.jpg");

		PhicommProduct K3N = new PhicommProduct();
		K3N.setId(28);
		K3N.setName("K3N");
		K3N.setColor("黑色");
		K3N.setSkuCode("901002695");
		K3N.setBuyPage(String.format("%s%s", PhicommConstants.PHICOMM_HOST,"/index.php/cart-fastbuy-199-%s.html"));
		K3N.setImgSrc("https://imgmall.phicomm.com/43/f2/fd3905849036.jpg");

		PhicommProduct W3 = new PhicommProduct();
		W3.setId(29);
		W3.setName("W3");
		W3.setColor("黑草绿");
		W3.setSkuCode("911000158");
		W3.setBuyPage(String.format("%s%s", PhicommConstants.PHICOMM_HOST,"/index.php/cart-fastbuy-197-%s.html"));
		W3.setImgSrc("https://imgmall.phicomm.com/c7/34/397c623863bf.jpg");

		PhicommProduct K3N_H1 = new PhicommProduct();
		K3N_H1.setId(30);
		K3N_H1.setName("K3N_H1");
		K3N_H1.setColor("套装");
		K3N_H1.setSkuCode("700110004");
		K3N_H1.setBuyPage(String.format("%s%s", PhicommConstants.PHICOMM_HOST,"/index.php/cart-fastbuy-194-%s.html"));
		K3N_H1.setImgSrc("https://imgmall.phicomm.com/11/5e/5f193f800d8e.jpg");

		PhicommProduct H1_BLACK = new PhicommProduct();
		H1_BLACK.setId(31);
		H1_BLACK.setName("H1");
		H1_BLACK.setColor("黑色");
		H1_BLACK.setSkuCode("912000068");
		H1_BLACK.setBuyPage(String.format("%s%s", PhicommConstants.PHICOMM_HOST,"/index.php/cart-fastbuy-216-%s.html"));
		H1_BLACK.setImgSrc("https://imgmall.phicomm.com/e0/e2/e70bbfa82b6b.jpg");

		PhicommProduct INFINITY_BETA = new PhicommProduct();
		INFINITY_BETA.setId(32);
		INFINITY_BETA.setName("Infinity");
		INFINITY_BETA.setColor("Beta 音乐震荡波");
		INFINITY_BETA.setSkuCode("912000082");
		INFINITY_BETA.setBuyPage(String.format("%s%s", PhicommConstants.PHICOMM_HOST,"/index.php/cart-fastbuy-226-%s.html"));
		INFINITY_BETA.setImgSrc("https://imgmall.phicomm.com/e8/d8/da8764f2e02d.jpg");

		PhicommProduct INFINITY_ALPHA = new PhicommProduct();
		INFINITY_ALPHA.setId(33);
		INFINITY_ALPHA.setName("Infinity");
		INFINITY_ALPHA.setColor("Alpha 音乐风火轮");
		INFINITY_ALPHA.setSkuCode("912000083");
		INFINITY_ALPHA.setBuyPage(String.format("%s%s", PhicommConstants.PHICOMM_HOST,"/index.php/cart-fastbuy-225-%s.html"));
		INFINITY_ALPHA.setImgSrc("https://imgmall.phicomm.com/46/30/346faea91293.jpg");



		PHICOMM_PRODUCT_CONFIG.add(K2_WHITE);
		PHICOMM_PRODUCT_CONFIG.add(K2_BLUE);
		PHICOMM_PRODUCT_CONFIG.add(K2P_WHITE);
		PHICOMM_PRODUCT_CONFIG.add(K2P_GOLDEN);
		PHICOMM_PRODUCT_CONFIG.add(K3_WHITE);
		PHICOMM_PRODUCT_CONFIG.add(K3_GOLDEN);
		PHICOMM_PRODUCT_CONFIG.add(K3C_WHITE);
		PHICOMM_PRODUCT_CONFIG.add(S7_WHITE);
		PHICOMM_PRODUCT_CONFIG.add(T1_BLACK);
		PHICOMM_PRODUCT_CONFIG.add(W1_BLACK);
		PHICOMM_PRODUCT_CONFIG.add(M1_SILVERY);
		PHICOMM_PRODUCT_CONFIG.add(M1_ROSE_GOLD);
		PHICOMM_PRODUCT_CONFIG.add(X3_GOLDEN);
		PHICOMM_PRODUCT_CONFIG.add(A1_WHITE);
		PHICOMM_PRODUCT_CONFIG.add(DC1_WHITE);
		PHICOMM_PRODUCT_CONFIG.add(N1_WHITE);
		PHICOMM_PRODUCT_CONFIG.add(R1_GRAY);
		PHICOMM_PRODUCT_CONFIG.add(R1_BLACK);
		PHICOMM_PRODUCT_CONFIG.add(H1_WHITE);
		PHICOMM_PRODUCT_CONFIG.add(H1_N1_GROUP);
		PHICOMM_PRODUCT_CONFIG.add(K2T_WHITE);
		PHICOMM_PRODUCT_CONFIG.add(W2_BLACK);
		PHICOMM_PRODUCT_CONFIG.add(S7PE_WHITE);
		PHICOMM_PRODUCT_CONFIG.add(A1_M1);
		PHICOMM_PRODUCT_CONFIG.add(TC1);
		PHICOMM_PRODUCT_CONFIG.add(K2G);
		PHICOMM_PRODUCT_CONFIG.add(E_START_I);
		PHICOMM_PRODUCT_CONFIG.add(LIGHT);
		PHICOMM_PRODUCT_CONFIG.add(K3N);
		PHICOMM_PRODUCT_CONFIG.add(W3);
		PHICOMM_PRODUCT_CONFIG.add(K3N_H1);
		PHICOMM_PRODUCT_CONFIG.add(H1_BLACK);
		PHICOMM_PRODUCT_CONFIG.add(INFINITY_BETA);
		PHICOMM_PRODUCT_CONFIG.add(INFINITY_ALPHA);
	}

	public static PhicommProduct getBySkuCode(String skuCode){
		if(StringUtils.isNotBlank(skuCode) && null != PHICOMM_PRODUCT_CONFIG && PHICOMM_PRODUCT_CONFIG.size() > 1){
			for(PhicommProduct phicommProduct : PHICOMM_PRODUCT_CONFIG){
				if(skuCode.equals(phicommProduct.getSkuCode())){
					return phicommProduct;
				}
			}
		}
		return null;
	}

	public static PhicommProduct getById(Integer id){
		if(null == id || null == PHICOMM_PRODUCT_CONFIG || PHICOMM_PRODUCT_CONFIG.size() < 1){
			return null;
		}
		for(PhicommProduct phicommProduct : PHICOMM_PRODUCT_CONFIG){
			if(null != phicommProduct.getId() && id.intValue() == phicommProduct.getId().intValue()){
				return phicommProduct;
			}
		}
		return null;
	}
}
