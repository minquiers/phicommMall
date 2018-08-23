package phicomm.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PhicommBuyInfo implements Cloneable {
	// 购买产品MD5码
	@Expose
	@SerializedName("cart_md5")
	private String cartMd5;

	// 购买地址ID
	@Expose
	@SerializedName("addr_id")
	private String addrId;

	// 快递类型
	@Expose
	@SerializedName("dlytype_id")
	private String dlyTypeId;

	// 付款方式
	@Expose
	@SerializedName("payapp_id")
	private String payAppId;

	// 是否需要发票
	@Expose
	@SerializedName("need_invoice")
	private boolean needInvoice;

	// 发票类型
	@Expose
	@SerializedName("invoice_type")
	private String invoiceType;

	// 发票抬头
	@Expose
	@SerializedName("invoice_title")
	private String invoiceTitle;

	// 发票备注
	@Expose
	@SerializedName("memo")
	private String memo;

	// 验证码
	@Expose
	@SerializedName("vcode")
	private String vcode;

	private String memberId;

	// 购买产品名称
	private String buyName;

	// 购买人手机
	private String phone;

	// 购买人密码
	private String password;

	// 返利号
	private String rebateVsid;
	// 购买产品ID
	private Integer buyId;

	// 立即购买页面
	private String fastBuyPage;

	// 优惠券
	private String coupon;

	// 优惠券List
	private List<Map<String, String>> couponList = null;

	// 购买类型:BUY-购买,COUPON-优惠券
	private String buyType;

	// 购买分数
	private int number;
	// id
	private String ip;
	// 创建时间
	private Date createTime = new Date();

	// 游戏分数
	private int gameScore;
	// 优惠券使用失败次数
	private int couponUseErrorCoupon = 0;

	// 天天牛token
	private String ddnToken = null;
	// 天天牛uuid
	private String ddnUuid = null;
	// 天天牛访问token
	private String ddnAccessToken = null;
	// 天天牛版本
	private String ddnVersion = null;
	// 待结算列表
	private List<String> preClearings;
	// 结算信息
	private List<ClearingInfo> clearingInfos;

	public void addClearingInfos(ClearingInfo clearingInfo) {
		if (null == clearingInfos) {
			this.clearingInfos = new ArrayList<ClearingInfo>();
		}
		this.clearingInfos.add(clearingInfo);
	}

	public void addPreClearings(String preClearing) {
		if (null == this.preClearings) {
			this.preClearings = new ArrayList<String>();
		}
		this.preClearings.add(preClearing);
	}

	public List<String> getPreClearings() {
		return preClearings;
	}

	public void setPreClearings(List<String> preClearings) {
		this.preClearings = preClearings;
	}

	public List<ClearingInfo> getClearingInfos() {
		return clearingInfos;
	}

	public void setClearingInfos(List<ClearingInfo> clearingInfos) {
		this.clearingInfos = clearingInfos;
	}

	public String getMemberId() {
		return memberId;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}

	public String getDdnUuid() {
		return ddnUuid;
	}

	public void setDdnUuid(String ddnUuid) {
		this.ddnUuid = ddnUuid;
	}

	public String getDdnVersion() {
		return ddnVersion;
	}

	public void setDdnVersion(String ddnVersion) {
		this.ddnVersion = ddnVersion;
	}

	public String getDdnToken() {
		return ddnToken;
	}

	public void setDdnToken(String ddnToken) {
		this.ddnToken = ddnToken;
	}

	public String getDdnAccessToken() {
		return ddnAccessToken;
	}

	public void setDdnAccessToken(String ddnAccessToken) {
		this.ddnAccessToken = ddnAccessToken;
	}

	public String getVcode() {
		return vcode;
	}

	public void setVcode(String vcode) {
		this.vcode = vcode;
	}

	public String getBuyName() {
		return buyName;
	}

	public void setBuyName(String buyName) {
		this.buyName = buyName;
	}

	public Integer getBuyId() {
		return buyId;
	}

	public void setBuyId(Integer buyId) {
		this.buyId = buyId;
	}

	public int getCouponUseErrorCoupon() {
		return couponUseErrorCoupon;
	}

	public void setCouponUseErrorCoupon(int couponUseErrorCoupon) {
		this.couponUseErrorCoupon = couponUseErrorCoupon;
	}

	public int getGameScore() {
		return gameScore;
	}

	public void setGameScore(int gameScore) {
		this.gameScore = gameScore;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public String getBuyType() {
		return buyType;
	}

	public void setBuyType(String buyType) {
		this.buyType = buyType;
	}

	public List<Map<String, String>> getCouponList() {
		return couponList;
	}

	public void setCouponList(List<Map<String, String>> couponList) {
		this.couponList = couponList;
	}

	public String getCoupon() {
		return coupon;
	}

	public void setCoupon(String coupon) {
		this.coupon = coupon;
	}

	public String getFastBuyPage() {
		return fastBuyPage;
	}

	public void setFastBuyPage(String fastBuyPage) {
		this.fastBuyPage = fastBuyPage;
	}

	public String getRebateVsid() {
		return rebateVsid;
	}

	public void setRebateVsid(String rebateVsid) {
		this.rebateVsid = rebateVsid;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getCartMd5() {
		return cartMd5;
	}

	public void setCartMd5(String cartMd5) {
		this.cartMd5 = cartMd5;
	}

	public String getAddrId() {
		return addrId;
	}

	public void setAddrId(String addrId) {
		this.addrId = addrId;
	}

	public String getDlyTypeId() {
		return dlyTypeId;
	}

	public void setDlyTypeId(String dlyTypeId) {
		this.dlyTypeId = dlyTypeId;
	}

	public String getPayAppId() {
		return payAppId;
	}

	public void setPayAppId(String payAppId) {
		this.payAppId = payAppId;
	}

	public boolean isNeedInvoice() {
		return needInvoice;
	}

	public void setNeedInvoice(boolean needInvoice) {
		this.needInvoice = needInvoice;
	}

	public String getInvoiceType() {
		return invoiceType;
	}

	public void setInvoiceType(String invoiceType) {
		this.invoiceType = invoiceType;
	}

	public String getInvoiceTitle() {
		return invoiceTitle;
	}

	public void setInvoiceTitle(String invoiceTitle) {
		this.invoiceTitle = invoiceTitle;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return (PhicommBuyInfo) super.clone();
	}
}
