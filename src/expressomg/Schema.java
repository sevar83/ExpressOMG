package expressomg;

import java.util.Collections;
import java.util.Map;


public class Schema
{
	private final String id;	
	private Map<String, Type> types;
	
	protected Schema(String id)
	{
		this.id = id;
	}	
	
	public String getId()
	{
		return this.id;
	}

	public Map<String, Type> getTypes()
	{
		return this.types;
	}
	
	// This must be part of constructor. It is workaround becuase all types need its schema to use it as a root scope
	protected void setTypes(Map<String, Type> types)
	{
		this.types = Collections.unmodifiableMap(types);
	}
	
	@Override
	public String toString()
	{
		return this.id;
	}
}