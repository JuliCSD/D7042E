package ai.aitia.demo.sensor_consumer_with_subscribing.entity;

public class Lamp {

	//=================================================================================================
	// members

	private final int id;
	private Integer status;
	private Integer lastRequestStatus;

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public Lamp(final int id, final Integer status) {
		this.id = id;
		this.status= status;
		this.lastRequestStatus = status;
	}

	//-------------------------------------------------------------------------------------------------
	public int getId() { return id; }
	public Integer getStatus() { return status; }
	public Integer getlastRequestStatus() { return lastRequestStatus; }

	//-------------------------------------------------------------------------------------------------
	public void setStatus(final Integer status) { this.status= status; }	
	public void setlastRequestStatus(final Integer lastRequestStatus) { this.lastRequestStatus = lastRequestStatus; }
}
