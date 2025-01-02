package ai.aitia.demo.car_provider.database;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import ai.aitia.demo.car_provider.entity.Car;
import eu.arrowhead.common.exception.InvalidParameterException;

@Component
public class InMemoryCarDB extends ConcurrentHashMap<Integer, Car> {

	//=================================================================================================
	// members
	
	private static final long serialVersionUID = -2462387539362748691L;
	
	private int idCounter = 0;
	
	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public Car create(final String brand, final int status) {
		if (brand == null || brand.isBlank()) {
			throw new InvalidParameterException("brand is null or empty");
		}		
		if (status>1 || status<0) {
			throw new InvalidParameterException("statusis null or empty");
		}
		
		idCounter++;
		this.put(idCounter, new Car(idCounter, brand.toLowerCase().trim(), status));
		return this.get(idCounter);
	}
	
	//-------------------------------------------------------------------------------------------------
	public List<Car> getAll() {
		return List.copyOf(this.values());
	}
	
	//-------------------------------------------------------------------------------------------------
	public Car getById(final int id) {
		if (this.containsKey(id)) {
			return this.get(id);
		} else {
			throw new InvalidParameterException("id '" + id + "' not exists");
		}
	}
	
	//-------------------------------------------------------------------------------------------------
	public Car updateById(final int id, final String brand, final Integer status) {
		if (this.containsKey(id)) {
			final Car car = this.get(id);
			if (brand!= null && !brand.isBlank()) {
				car.setBrand(brand);
			}
			if (status< 1 && status> 0 && status!=null) {
				car.setStatus(status);
			}
			this.put(id, car);
			return car;
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
