package ai.aitia.demo.controller_with_subscribing.entity;

public class Lamp {

	//=================================================================================================
	// members

	private final int id;
	private Integer status;
	private Integer lastRequestStatus;
	private boolean sendToLamp;

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public Lamp(final int id, final Integer status) {
		this.id = id;
		this.status= status;
		this.lastRequestStatus = status;
		this.sendToLamp = true;
	}

	//-------------------------------------------------------------------------------------------------
	public int getId() { return id; }
	public Integer getStatus() { return status; }
	public Integer getlastRequestStatus() { return lastRequestStatus; }
	public boolean getSendToLamp() { return sendToLamp; }

	//-------------------------------------------------------------------------------------------------
	public void setStatus(final Integer status) { 
		this.status= status; 
		// System.out.println("status set to : " + status+ " and lastRequestStatus is : " + lastRequestStatus);
	}	

	public void setlastRequestStatus(final Integer lastRequestStatus) { 
		this.lastRequestStatus = lastRequestStatus; 
		// System.out.println("lastRequestStatus set to : " + lastRequestStatus+ " and status is : " + status);
	}

	public void setSendToLamp(final boolean sendToLamp) { this.sendToLamp = sendToLamp; }
}
