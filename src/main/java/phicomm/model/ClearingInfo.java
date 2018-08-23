package phicomm.model;

import java.math.BigDecimal;

/**
 * 结算信息
 * @author Administrator
 *
 */
public class ClearingInfo {

	private String orderNo;
	private BigDecimal amount;
	private String url;
	
	
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	
}
