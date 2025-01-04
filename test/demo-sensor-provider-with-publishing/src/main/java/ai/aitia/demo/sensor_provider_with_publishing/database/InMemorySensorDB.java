package ai.aitia.demo.sensor_provider_with_publishing.database;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import ai.aitia.demo.sensor_provider_with_publishing.entity.Sensor;
import eu.arrowhead.common.exception.InvalidParameterException;

@Component
public class InMemorySensorDB extends ConcurrentHashMap<Integer, Sensor> {

	//=================================================================================================
	// members
	
	private static final long serialVersionUID = -2462387539362748691L;
	
	private int idCounter = 0;
	
	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public Sensor create(final String brand, final String color) {
		if (brand == null || brand.isBlank()) {
			throw new InvalidParameterException("brand is null or empty");
		}		
		if (color == null || color.isBlank()) {
			throw new InvalidParameterException("color is null or empty");
		}
		
		idCounter++;
		this.put(idCounter, new Sensor(idCounter, brand.toLowerCase().trim(), color.toLowerCase().trim()));
		return this.get(idCounter);
	}
	
	//-------------------------------------------------------------------------------------------------
	public List<Sensor> getAll() {
		return List.copyOf(this.values());
	}
	
	//-------------------------------------------------------------------------------------------------
	public Sensor getById(final int id) {
		if (this.containsKey(id)) {
			return this.get(id);
		} else {
			throw new InvalidParameterException("id '" + id + "' not exists");
		}
	}
	
	//-------------------------------------------------------------------------------------------------
	public Sensor updateById(final int id, final String brand, final String color) {
		if (this.containsKey(id)) {
			final Sensor sensor = this.get(id);
			if (brand!= null && !brand.isBlank()) {
				sensor.setBrand(brand);
			}
			if (color != null && !color.isBlank()) {
				sensor.setColor(color);
			}
			this.put(id, sensor);
			return sensor;
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
