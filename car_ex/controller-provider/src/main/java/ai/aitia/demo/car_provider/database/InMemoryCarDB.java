package ai.aitia.demo.car_provider.database;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import ai.aitia.demo.car_provider.entity.Lamp;
import eu.arrowhead.common.exception.InvalidParameterException;

@Component
public class InMemoryLampDB extends ConcurrentHashMap<Integer, Lamp> {

	//=================================================================================================
	// members
	
	private static final long serialVersionUID = -2462387539362748691L;
	
	private int idCounter = 0;
	
	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public Lamp create(final int status) {
		if (status == null ) {
			throw new InvalidParameterException("status is null or empty");
		}	 
		idCounter++;
		this.put(idCounter, new Lamp(idCounter, status));
		return this.get(idCounter);
	}
	
	//-------------------------------------------------------------------------------------------------
	public List<Lamp> getAll() {
		return List.copyOf(this.values());
	}
	
	//-------------------------------------------------------------------------------------------------
	public Lamp getById(final int id) {
		if (this.containsKey(id)) {
			return this.get(id);
		} else {
			throw new InvalidParameterException("id '" + id + "' not exists");
		}
	}
	
	//-------------------------------------------------------------------------------------------------
	public Lamp updateById(final int id, final int status) {
		if (this.containsKey(id)) {
			final Lamp lamp = this.get(id);
			if (status!= null) {
				lamp.setBrand(brand);
			}
			this.put(id, lamp);
			return lamp;
		} else {
			throw new InvalidParameterException("id '" + id + "' not exists");
		}
	}
	
	//-------------------------------------------------------------------------------------------------
	public void removeById(final int id) {
		if (this.containsKey(id)) {
			this.remove(id);
		}
	}
}
