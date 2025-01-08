package ai.aitia.demo.lamp_consumer;

public class LampConsumerConstants {
	
	//=================================================================================================
	// members
	
	public static final String BASE_PACKAGE = "ai.aitia";
	
	public static final String INTERFACE_SECURE = "HTTP-SECURE-JSON";
	public static final String INTERFACE_INSECURE = "HTTP-INSECURE-JSON";
	public static final String HTTP_METHOD = "http-method";
	
	public static final String CREATE_LAMP_SERVICE_DEFINITION = "create-lamp";
	public static final String GET_LAMP_SERVICE_DEFINITION = "get-lamp";
	public static final String REQUEST_PARAM_KEY_STATUS = "request-param-status";

	public static final String REQUEST_PARAM_KEY_UPDATE = "request-param-update";
	public static final String REQUEST_LAMP_UPDATE = "update";
	
	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	private LampConsumerConstants() {
		throw new UnsupportedOperationException();
	}

}
