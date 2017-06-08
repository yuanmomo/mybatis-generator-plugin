package net.yuanmomo.springboot.util;



public class AjaxResponseBean<T> {
	/**
	 * {"statusCode":"200", "message":"操作成功"}
	 * {"statusCode":"300", "message":"操作失败"}
	 * {"statusCode":"301", "message":"会话超时"}
	 *
	 */
	private int statusCode;
	private Object message;
	private String navTabId = "";
	private String rel = "";
	private String callbackType = "";
	private String forwardUrl = "";

	/**
	 * returnValue: 后台相应返回的json数据.
	 * @since JDK 1.7
	 */
	private T returnValue ;

	public static final int OK = 200;
	public static final String OK_MESSAGE = "操作成功";
	public static final String NO_DATA_MESSAGE = "没有数据结果";

	public static final int ERROR = 300;
	public static final String ERROR_MESSAGE = "操作失败";

	public static final String PARAMETER_INVALID_ERROR_MESSAGE = "提交参数格式不正确";

	public static final int TIMEOUT = 301;
	public static final String TIMEOUT_MESSAGE = "会话超时";

	public static final int PERMISSION_DENY = 302;
	public static final String PERMISSION_DENY_MESSAGE = "权限不足";

	public AjaxResponseBean() {}

	private AjaxResponseBean(int statusCode, Object message) {
		this.statusCode = statusCode;
		this.message = message;
	}

	private AjaxResponseBean(int statusCode, String message, T returnValue) {
		this.statusCode = statusCode;
		this.message = message;
		this.returnValue = returnValue;
	}
	public AjaxResponseBean(int statusCode, String message, String navTabId,
                            String rel, String callbackType, String forwardUrl) {
		this.statusCode = statusCode;
		this.message = message;
		this.navTabId = navTabId;
		this.rel = rel;
		this.callbackType = callbackType;
		this.forwardUrl = forwardUrl;
	}
	public static class Const{
		/************************************* 操作失败 **********************************************/
		public static AjaxResponseBean TIMEOUT_RESPONSE_BEAN =
				new AjaxResponseBean(TIMEOUT, TIMEOUT_MESSAGE);

		public static AjaxResponseBean NO_PERMISSION_RESPONSE_BEAN =
				new AjaxResponseBean(PERMISSION_DENY, PERMISSION_DENY_MESSAGE);

		public static AjaxResponseBean ERROR_RESPONSE_BEAN =
				new AjaxResponseBean(ERROR, ERROR_MESSAGE);

		public static AjaxResponseBean PARAMETER_INVALID_ERROR_RESPONSE_BEAN =
				new AjaxResponseBean(ERROR, PARAMETER_INVALID_ERROR_MESSAGE);

		/************************************* 操作成功 **********************************************/
		public static AjaxResponseBean NO_DATA_RESPONSE_BEAN =
				new AjaxResponseBean(OK, NO_DATA_MESSAGE);

		public static AjaxResponseBean SUCCESS_RESPONSE_BEAN =
				new AjaxResponseBean(OK, OK_MESSAGE);
	}

	public static <T> AjaxResponseBean getReturnValueResponseBean(T returnObj) {
		return new AjaxResponseBean(OK, OK_MESSAGE,returnObj);
	}

	public static <T> AjaxResponseBean getReturnValueResponseBean(int statusCode,
                                                              String message, T returnObj) throws Exception {
		return new AjaxResponseBean(statusCode,message, returnObj);
	}

	public static AjaxResponseBean getErrorResponseBean(String message)  {
		return new AjaxResponseBean(ERROR,message);
	}

	public static AjaxResponseBean getNoDataReturnValueResponseBean() throws Exception {
		return getNoDataReturnValueResponseBean(null);
	}
	public static AjaxResponseBean getNoDataReturnValueResponseBean(PaginationBean paginationBean) throws Exception {
		if(paginationBean == null){
			paginationBean  = new PaginationBean();
		}
		return new AjaxResponseBean(OK, NO_DATA_MESSAGE,paginationBean);
	}

	@Override
	public String toString() {
		return "AjaxResponseBean{" +
				"statusCode=" + statusCode +
				", message=" + message +
				", navTabId='" + navTabId + '\'' +
				", rel='" + rel + '\'' +
				", callbackType='" + callbackType + '\'' +
				", forwardUrl='" + forwardUrl + '\'' +
				", returnValue=" + returnValue +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		AjaxResponseBean that = (AjaxResponseBean) o;

		if (statusCode != that.statusCode) return false;
		if (message != null ? !message.equals(that.message) : that.message != null) return false;
		if (navTabId != null ? !navTabId.equals(that.navTabId) : that.navTabId != null) return false;
		if (rel != null ? !rel.equals(that.rel) : that.rel != null) return false;
		if (callbackType != null ? !callbackType.equals(that.callbackType) : that.callbackType != null) return false;
		return forwardUrl != null ? forwardUrl.equals(that.forwardUrl) : that.forwardUrl == null;
	}

	@Override
	public int hashCode() {
		int result = statusCode;
		result = 31 * result + (message != null ? message.hashCode() : 0);
		result = 31 * result + (navTabId != null ? navTabId.hashCode() : 0);
		result = 31 * result + (rel != null ? rel.hashCode() : 0);
		result = 31 * result + (callbackType != null ? callbackType.hashCode() : 0);
		result = 31 * result + (forwardUrl != null ? forwardUrl.hashCode() : 0);
		return result;
	}

	public int getStatusCode() {
		return statusCode;
	}
	public Object getMessage() {
		return message;
	}
	public String getNavTabId() {
		return navTabId;
	}
	public String getRel() {
		return rel;
	}
	public String getCallbackType() {
		return callbackType;
	}
	public String getForwardUrl() {
		return forwardUrl;
	}
	public Object getReturnValue() {
		return returnValue;
	}
}
