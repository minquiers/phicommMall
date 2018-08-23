package phicomm.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PhicommPreBuyInfo implements Cloneable {
    //购买产品MD5码
    @Expose
    @SerializedName("cart_md5")
    private String cartMd5;

    //购买地址ID
    @Expose
    @SerializedName("addr_id")
    private String addrId;

    //快递类型
    @Expose
    @SerializedName("dlytype_id")
    private String dlyTypeId;

    //付款方式
    @Expose
    @SerializedName("payapp_id")
    private String payAppId;


    //发票类型
    @Expose
    @SerializedName("invoice_type")
    private String invoiceType;

    //发票抬头
    @Expose
    @SerializedName("invoice_title")
    private String invoiceTitle;

    //发票备注
    @Expose
    @SerializedName("memo")
    private String memo;

    //数量
    @Expose
    @SerializedName("quantity")
    private String quantity;
    
    //产品ID
    @Expose
    @SerializedName("product_id")
    private String productId;
    
    //产品ID
    @Expose
    @SerializedName("activity_id")
    private String activityId;
    
    

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



	public String getQuantity() {
		return quantity;
	}



	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}



	public String getProductId() {
		return productId;
	}



	public void setProductId(String productId) {
		this.productId = productId;
	}



	public String getActivityId() {
		return activityId;
	}



	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}



	@Override
    protected Object clone() throws CloneNotSupportedException {
        return (PhicommPreBuyInfo) super.clone();
    }
}
