package phicomm.model;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang3.time.DateFormatUtils;

import com.fasterxml.jackson.annotation.JsonFormat;

import phicomm.config.PhicommConstants;

public class PhicommProduct implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
    //产品购买页面
    private String buyPage;
    //产品名称
    private String name;
    //颜色
    private String color;
    //库存
    private Long sku;
    //查询库存代码
    private String skuCode;
    //库存状态1:有库存,0:无库存,-1:已下架
    private Integer skuStatus;
    //库存改变提示信息
    private String skuChangeAlertInfo;
    //状态改变标志:0:未改变,1:已改变
    private Integer skuChangeFlag = 0;
    //库存查询时间
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date skuQueryDate;
    //库存通知时间
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date skuNotifyDate;
    //图片地址
    private String imgSrc;
    //优惠券开关
    private Boolean couponSwitch = true;
    //限制购买开关
    private Boolean limitBuySwitch = false;
    

	public Boolean getLimitBuySwitch() {
        return limitBuySwitch;
    }

    public void setLimitBuySwitch(Boolean limitBuySwitch) {
        this.limitBuySwitch = limitBuySwitch;
    }

    public Boolean getCouponSwitch() {
        return couponSwitch;
    }

    public void setCouponSwitch(Boolean couponSwitch) {
        this.couponSwitch = couponSwitch;
    }

    public String getImgSrc() {
        return imgSrc;
    }

    public void setImgSrc(String imgSrc) {
        this.imgSrc = imgSrc;
    }

    public Integer getSkuStatus() {
        return skuStatus;
    }

    public void setSkuStatus(Integer skuStatus) {
        this.skuStatus = skuStatus;
    }

    public String getSkuChangeAlertInfo() {
        return skuChangeAlertInfo;
    }

    public void setSkuChangeAlertInfo(String skuChangeAlertInfo) {
        this.skuChangeAlertInfo = skuChangeAlertInfo;
    }

    public Integer getId() {

        return id;
    }

    public Date getSkuQueryDate() {
        return skuQueryDate;
    }

    public void setSkuQueryDate(Date skuQueryDate) {
        this.skuQueryDate = skuQueryDate;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBuyPage() {
        return buyPage;
    }

    public void setBuyPage(String buyPage) {
        this.buyPage = buyPage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Long getSku() {
        return sku;
    }

    public void setSku(Long sku) {
        this.sku = sku;
    }

    public String getSkuCode() {
        return skuCode;
    }

    public Integer getSkuChangeFlag() {
        return skuChangeFlag;
    }

    public void setSkuChangeFlag(Integer skuChangeFlag) {
        this.skuChangeFlag = skuChangeFlag;
    }

    public Date getSkuNotifyDate() {
        return skuNotifyDate;
    }

    public void setSkuNotifyDate(Date skuNotifyDate) {
        this.skuNotifyDate = skuNotifyDate;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }

    public void skuChange(){
        if(null != this.sku){
            String tmp = null;
            if(this.sku.longValue() > 0l){
                this.skuStatus = 1;
                tmp = "有库存";
            }else if(this.sku.longValue() == 0l){
                this.skuStatus = 0;
                tmp = "无库存";
            }else{
                this.skuStatus = -1;
                tmp = "已下架";
            }
            this.skuChangeAlertInfo = String.format("【来自库存情报系统】\n 产品:%s%s\n 状态:%s\n 库存:%s\n 查询时间:%s\n 立即抢购地址:%s\n" , this.name , this.color , tmp , (this.sku < 0 ? 0 : this.sku) , DateFormatUtils.format(this.skuQueryDate , "yyyy-MM-dd HH:mm:ss") ,PhicommConstants.FRONT_PATH);
            this.skuChangeFlag = 1;
        }
    }
}
