package ai.aitia.demo.light_sensor_provider_with_publishing.entity;

public class LightSensor {

	//=================================================================================================
	// members

	private final int id;
	private String value;

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public LightSensor(final int id, final String value) {
		this.id = id;
		this.value = value;
	}

	//-------------------------------------------------------------------------------------------------
	public int getId() { return id; }
	public String getValue() { return value; }

	//-------------------------------------------------------------------------------------------------
	public void setValue(final String value) { this.value = value; }	
}
