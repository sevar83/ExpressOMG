package expressomg;

import java.util.ArrayList;
import java.util.List;

public abstract class Type extends ExpressRoot
{
	protected String id;
	protected final TypeRef superTypeRef;
	protected List<String> implementedInterfaces;
	
	protected Type(Schema schema, String id, TypeRef superType)
	{
		super(schema);
		this.id = id;
		this.superTypeRef = superType;
	}
	
	public String getId()
	{
		return this.id;
	}
	
	public void setId(String id)
	{
		this.id = id;		
	}
	
	public List<String> getImplementedInterfaces()
	{
		return this.implementedInterfaces;
	}
	
	public boolean isAbstract()
	{
		return false;
	}
	
	void addImplementedInterface(String implInterface)
	{
		if (this.implementedInterfaces == null)
			this.implementedInterfaces = new ArrayList<String>();
		this.implementedInterfaces.add(implInterface);
	}
	
	public TypeRef getSuperType()
	{
		return this.superTypeRef;
	}
	
	public boolean isSubTypeOf(String id)
	{
		TypeRef superTypeRef = this.superTypeRef;
		while (superTypeRef != null)
		{
			if (superTypeRef.getId().equals(id))
				return true;
			superTypeRef = superTypeRef.getType().getSuperType();
		}
		return false;
	}
	
	public boolean isAggregation()
	{
		return false;
	}
	
	@Override
	public String toString()
	{
		return getId();
	}
}