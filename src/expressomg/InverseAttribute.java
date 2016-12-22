package expressomg;

public class InverseAttribute extends Attribute
{
	private final TypeRef baseType;
	private final AttributeRef forwardAttribute;
	
	/**
	 * 
	 * @param schema
	 * @param id
	 * @param owner
	 * @param type type reference of this inverse attribute. Can refer to SetType or BagType
	 * @param baseType type reference to the element type
	 * @param forwardAttribute
	 */
	public InverseAttribute(Schema schema, String id, TypeRef type, TypeRef baseType, AttributeRef forwardAttribute)
	{
		super(schema, id, type);		
		this.baseType = baseType;
		this.forwardAttribute = forwardAttribute;
	}
	
	public TypeRef getBaseType()
	{
		return this.baseType;
	}
	
	public ExplicitAttribute getForwardAttribute()
	{
		return (ExplicitAttribute) this.forwardAttribute.getAttribute();
	}
}