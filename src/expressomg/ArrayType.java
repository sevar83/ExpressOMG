package expressomg;

public final class ArrayType extends AggregationType
{
	protected ArrayType(Schema schema, TypeRef baseType)
	{
		super(schema, "ARRAY", baseType);
	}
}
