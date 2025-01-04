package ai.aitia.demo.sensor_provider_with_publishing.entity;

public class Sensor {

	//=================================================================================================
	// members

	private final int id;
	private String brand;
	private String color;

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public Sensor(final int id, final String brand, final String color) {
		this.id = id;
		this.brand = brand;
		this.color = color;
	}

	//-------------------------------------------------------------------------------------------------
	public int getId() { return id; }
	public String getBrand() { return brand; }
	public String getColor() { return color; }

	//-------------------------------------------------------------------------------------------------
	public void setBrand(final String brand) { this.brand = brand; }
	public void setColor(final String color) { this.color = color; }	
}
