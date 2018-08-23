package phicomm.model;

public class PhicommBuyHomeInfo {
	//同时抢购人数
	private long sameTimeBuyPerson;
	//最大容量
	private long buyMaxPerson;
	//公告
	private String notice;
	
	public String getNotice() {
		return notice;
	}
	public void setNotice(String notice) {
		this.notice = notice;
	}
	public long getSameTimeBuyPerson() {
		return sameTimeBuyPerson;
	}
	public void setSameTimeBuyPerson(long sameTimeBuyPerson) {
		this.sameTimeBuyPerson = sameTimeBuyPerson;
	}
	public long getBuyMaxPerson() {
		return buyMaxPerson;
	}
	public void setBuyMaxPerson(long buyMaxPerson) {
		this.buyMaxPerson = buyMaxPerson;
	}
	
}
