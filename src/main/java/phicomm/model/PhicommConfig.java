package phicomm.model;

import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import logs.OtherUtils;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 斐讯配置
 */
public class PhicommConfig {
	private String name;
	private String explain;
	private Object ref;
	private String statusExplain;
	private String type;
	private String refClass;
	private String refClassField;

	public PhicommConfig(String statusExplain, String name, String explain, String refClass, String refClassField) {
		this.statusExplain = statusExplain;
		this.name = name;
		this.explain = explain;
		this.refClass = refClass;
		this.refClassField = refClassField;
		try {
			this.ref = Class.forName(refClass).getField(refClassField).get(Class.forName(refClass));
			type();
		} catch (Exception e) {
			OtherUtils.errorException(String.format("反射回写斐讯配置失败:%s",e.getMessage()), e);
		} 
	}

	public String getRefClass() {
		return refClass;
	}

	public void setRefClass(String refClass) {
		this.refClass = refClass;
	}

	public String getRefClassField() {
		return refClassField;
	}

	public void setRefClassField(String refClassField) {
		this.refClassField = refClassField;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getExplain() {
		return explain;
	}

	public void setExplain(String explain) {
		this.explain = explain;
	}

	public Object getRef() {
		return ref;
	}

	public void setRef(Object ref) {
		this.ref = ref;
		try {
			if(StringUtils.isNotBlank(type) && "Integer,Long,Double,BigDecimal".indexOf(type) > -1){
				 BigDecimal v = new BigDecimal(ref.toString().trim());
				 if("Integer".equals(type)){
					 Class.forName(refClass).getField(refClassField).set(Class.forName(refClass),v.intValue());
				 }else if("Long".equals(type)){
					 Class.forName(refClass).getField(refClassField).set(Class.forName(refClass),v.longValue());
				 }else if("Double".equals(type)){
					 Class.forName(refClass).getField(refClassField).set(Class.forName(refClass),v.doubleValue());
				 }else{
					 Class.forName(refClass).getField(refClassField).set(Class.forName(refClass),v);
				 }
			}else{
				Field field = Class.forName(refClass).getField(refClassField);
				Gson gson = new Gson();
				field.set(Class.forName(refClass),gson.fromJson(gson.toJson(ref) , field.getGenericType()));
			}
		} catch (Exception e) {
			OtherUtils.errorException(e.getMessage(), e);
		} 
	}

	public String getStatusExplain() {
		return statusExplain;
	}

	public void setStatusExplain(String statusExplain) {
		this.statusExplain = statusExplain;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	private void type() {
		if (null != this.ref) {
			if (this.ref instanceof String) {
				this.type = "String";
			} else if (this.ref instanceof Boolean) {
				this.type = "Boolean";
			} else if (this.ref instanceof Integer) {
				this.type = "Integer";
			} else if (this.ref instanceof Long) {
				this.type = "Long";
			} else if (this.ref instanceof Map) {
				this.type = "Map";
			} else if (this.ref instanceof List) {
				this.type = "List";
			}
		}
	}
}
