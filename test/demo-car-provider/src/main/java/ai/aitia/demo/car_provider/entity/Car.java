package ai.aitia.demo.car_provider.entity;

public class Car {

	//=================================================================================================
	// members

	private final int id;
	private String group;
	private Integer status;

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public Car(final int id, final String group, final Integer status) {
		this.id = id;
		this.group = group;
		this.status= status;
	}

	//-------------------------------------------------------------------------------------------------
	public int getId() { return id; }
	public String getGroup() { return group; }
	public Integer getStatus() { return status; }

	//-------------------------------------------------------------------------------------------------
	public void setGroup(final String group) { this.group = group; }
	public void setStatus(final Integer status) { this.status= status; }	
}
