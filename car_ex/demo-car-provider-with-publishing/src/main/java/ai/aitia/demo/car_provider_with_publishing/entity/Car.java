package ai.aitia.demo.car_provider_with_publishing.entity;

public class Car {

	//=================================================================================================
	// members

	private final int id;
	private String status;

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public Car(final int id, final String status) {
		this.id = id;
		this.status = status;
	}

	//-------------------------------------------------------------------------------------------------
	public int getId() { return id; }
	public String getStatus() { return status; }

	//-------------------------------------------------------------------------------------------------
	public void setStatus(final String status) { this.status = status; }
}
