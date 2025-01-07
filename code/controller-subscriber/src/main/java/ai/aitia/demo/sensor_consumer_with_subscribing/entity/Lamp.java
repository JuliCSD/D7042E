package ai.aitia.demo.sensor_consumer_with_subscribing.entity;

public class Lamp {

	//=================================================================================================
	// members

	private final int id;
	private Integer status;

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public Lamp(final int id, final Integer status) {
		this.id = id;
		this.status= status;
	}

	//-------------------------------------------------------------------------------------------------
	public int getId() { return id; }
	public Integer getStatus() { return status; }

	//-------------------------------------------------------------------------------------------------
	public void setStatus(final Integer status) { this.status= status; }	
}
