package ai.aitia.demo.sensor_provider_with_publishing.entity;

public class Sensor {

	//=================================================================================================
	// members

	private final int id;
	private String name;
	private String value;

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public Sensor(final int id, final String name, final String value) {
		this.id = id;
		this.name = name;
		this.value = value;
	}

	//-------------------------------------------------------------------------------------------------
	public int getId() { return id; }
	public String getName() { return name; }
	public String getValue() { return value; }

	//-------------------------------------------------------------------------------------------------
	public void setName(final String name) { this.name = name; }
	public void setValue(final String value) { this.value = value; }	
}
