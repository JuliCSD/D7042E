package ai.aitia.demo.lamp_provider.database;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import ai.aitia.demo.lamp_provider.entity.Lamp;
import eu.arrowhead.common.exception.InvalidParameterException;

@Component
public class InMemoryLampDB extends ConcurrentHashMap<Integer, Lamp> {

	//=================================================================================================
	// members
	
	private static final long serialVersionUID = -2462387539362748691L;
	
	private int idCounter = 0;

	private List<Lamp> lamps;

	
	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	private void initializeLamps() {

        this.lamps = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            lamps.add(new Lamp(i, "street", 0));
        }
    }



	public Lamp create(final String group, final int status) {
		if (group == null || group.isBlank()) {
			throw new InvalidParameterException("group is null or empty");
		}		
		if (status>1 || status<0) {
			throw new InvalidParameterException("statusis null or empty");
		}
		
		idCounter++;
		this.put(idCounter, new Lamp(idCounter, group.toLowerCase().trim(), status));
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
	public Lamp updateById(final int id, final String group, final Integer status) {
		if (this.containsKey(id)) {
			final Lamp lamp = this.get(id);
			if (group!= null && !group.isBlank()) {
				lamp.setGroup(group);
			}
			if (status< 1 && status> 0 && status!=null) {
				lamp.setStatus(status);
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