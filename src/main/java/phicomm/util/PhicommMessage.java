package phicomm.util;

import java.util.List;

public class PhicommMessage {

	private String status;
	private String message;
	private List resultList;
	private Object result;

	public PhicommMessage(String status, String message) {
		this.status = status;
		this.message = message;
	}

	public PhicommMessage(String status, String message, List resultList) {
		this(status, message);
		this.resultList = resultList;
	}
	
	public PhicommMessage(String status, String message, Object result) {
		this(status, message);
		this.result = result;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public List getResultList() {
		return resultList;
	}

	public void setResultList(List resultList) {
		this.resultList = resultList;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}
}
