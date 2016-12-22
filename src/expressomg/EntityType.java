package expressomg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class EntityType extends Type implements NamedType
{
	private boolean isAbstract;
	private final List<ExplicitAttribute> explicitAttributes;	
	private final List<InverseAttribute> inverseAttributes;
	private List<ExplicitAttribute> allExplicitAttributes;
	private List<ExplicitAttribute> explicitAttributesOfSuper;
	
	public EntityType(Schema schema, String id, TypeRef superType, boolean isAbstract, 
			List<ExplicitAttribute> explicitAttributes, List<InverseAttribute> inverseAttributes)
	{
		super(schema, id, superType);
		this.explicitAttributes = Collections.unmodifiableList(explicitAttributes);
		this.inverseAttributes = Collections.unmodifiableList(inverseAttributes);
		
		for (Attribute attr : this.explicitAttributes)
		{
			attr.setOwner(new TypeRef(schema, this));
		}		
		for (Attribute attr : this.inverseAttributes)
		{
			attr.setOwner(new TypeRef(schema, this));
		}
	}
	
	@Override
	public String getId()
	{
		return this.id;
	}
	
	public boolean isAbstract()
	{
		return this.isAbstract;
	}
	
	/**
	 * Explicit attributes of this entity class + plus the explicit attributes of all inherited entity classes
	 * @return
	 */
	public List<ExplicitAttribute> getAllExplicitAttributes()
	{
		if (this.allExplicitAttributes == null)
		{
			this.allExplicitAttributes = new ArrayList<ExplicitAttribute>();
							
			Type currentType = this;
			do
			{
				if (currentType instanceof EntityType)
				{
					List<ExplicitAttribute> classAttribs = ((EntityType) currentType).getExplicitAttributes();
					if (classAttribs != null)
						this.allExplicitAttributes.addAll(0, classAttribs);
					currentType = currentType.getSuperType() != null ? currentType.getSuperType().getType() : null;
				}
			}
			while (currentType != null);
		}
		
		return this.allExplicitAttributes;
	}
	
	public List<ExplicitAttribute> getExplicitAttributes()
	{
		return this.explicitAttributes;
	}
	
	public List<ExplicitAttribute> getExplicitAttributesOfSuper()
	{
		if (this.explicitAttributesOfSuper == null)
		{
			List<ExplicitAttribute> list = new ArrayList<ExplicitAttribute>();
			list.addAll(getAllExplicitAttributes());
			list.removeAll(getExplicitAttributes());
			this.explicitAttributesOfSuper = Collections.unmodifiableList(list);
		}
		
		return this.explicitAttributesOfSuper;
	}
	
	public List<InverseAttribute> getInverseAttributes()
	{
		return this.inverseAttributes;
	}
	
	// Scope search method
	public Attribute getAttribute(String id)
	{
		for (Attribute attr : getExplicitAttributes())
			if (attr.getId().equals(id))
				return attr;
				
		for (Attribute attr : getInverseAttributes())
			if (attr.getId().equals(id))
				return attr;
		
		// TODO
		/*for (Attribute attr : getDerivedAttributes())
		  if (attr.getId().equals(id))
				return attr;*/
		
		return null;
	}
	
	@Override
	public String toString()
	{
		return "ENTITY " + this.id;
	}
}