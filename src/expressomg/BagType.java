package expressomg;

public final class BagType extends AggregationType
{
	protected BagType(Schema schema, TypeRef baseType)
	{
		super(schema, "BAG", baseType);
	}
}
