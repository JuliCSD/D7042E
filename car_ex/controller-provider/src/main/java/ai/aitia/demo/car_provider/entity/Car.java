package ai.aitia.demo.car_provider.entity;

public class Lamp {

	//=================================================================================================
	// members

	private final int id;
	private int status;

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public Lamp(final int id, final String status) {
		this.id = id;
		this.status = status;
	}

	//-------------------------------------------------------------------------------------------------
	public int getId() { return id; }
	public int getStatus() { return status; } 
	//-------------------------------------------------------------------------------------------------
	public void getStatus(final int status) { this.status = status; }
 }
