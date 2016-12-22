package expressomg;

import static net.sourceforge.osexpress.parser.ExpressParserTokenTypes.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Extractor
{
	public static Schema extractSchema(ASTNode schemaNode)
	{
		if (schemaNode.getType() != SCHEMA_DECL)
			throw new IllegalArgumentException();
				
		String id = schemaNode.getChild(SCHEMA_ID).getFirstChild().getText();
		Map<String, Type> types = new HashMap<String, Type>();
		Schema schema = new Schema(id);
		
		ASTNode schemaBody = schemaNode.getChild(SCHEMA_BODY);
		if (schemaBody != null)
		{
			// First pass
			for (ASTNode child : schemaBody.getChildren())
			{
				if (child.getType() == DECLARATION)
				{					
					ASTNode subChild = child.getFirstChild();
					if (subChild.getType() == TYPE_DECL)
					{
						// TYPE DECLARATION
						DeclaredType type = Extractor.extractDeclaredType(schema, subChild);
						types.put(type.getId(), type);
					}
					else if (subChild.getType() == ENTITY_DECL)
					{
						// ENTITY
						EntityType type = Extractor.extractEntityType(schema, subChild);
						types.put(type.getId(), type);
					}
					else if (subChild.getType() == FUNCTION_DECL)
					{
						// FUNCTION
					}
					else if (subChild.getType() == PROCEDURE_DECL)
					{
						// PROCEDURE
					}
				}
				else if (child.getType() == RULE_DECL)
				{
					// RULE
				}
			}
			
			schema.setTypes(types);			
			// We've just added all types to the schema.
			// After this moment all referenced types (via TypeRef.getType() etc.) should be accessible.
			// From now on all references (TypeRef, AttributeRef, etc.) should work.
			
			// Second pass
			
			// Cycle through all Selects and add them as implemented interfaces
			for (Type type : types.values())
			{
				try 
				{
					if (type.getSuperType() != null)
					{
						Type superType = type.getSuperType().getType();
						if (superType instanceof SelectType)
						{
							SelectType select = (SelectType) superType;
							for (String possibleTypeId : select.getPossibleTypes())
							{
								Type possibleType = types.get(possibleTypeId);
								possibleType.addImplementedInterface(type.getId());
							}
						}
					}
				}
				catch (Exception ex)
				{
					System.out.println("Error in resolving selects for " + type.getId());
				}
			}
		}
		
		
		return schema;
	}
		
	public static DeclaredType extractDeclaredType(Schema schema, ASTNode node)
	{
		ASTNode typeIdNode = node.getFirstChild();
		if (typeIdNode.getType() != TYPE_ID)
			throw new IllegalArgumentException();		
		String id = typeIdNode.getFirstChild().getText();
		TypeRef underlyingType;
		
		ASTNode underlyingTypeNode = typeIdNode.getNextSibling();
		if (underlyingTypeNode.getType() != UNDERLYING_TYPE)
			throw new IllegalArgumentException();		
		
		ASTNode subNode = underlyingTypeNode.getFirstChild();
		if (subNode.getType() == CONSTRUCTED_TYPES)
		{
			ASTNode enumOrSelectNode = subNode.getFirstChild();
			if (enumOrSelectNode.getType() == ENUMERATION_TYPE)
			{
				// ENUMERATION
				EnumerationType enumeration = Extractor.extractEnumerationType(schema, enumOrSelectNode);
				underlyingType = new TypeRef(schema, enumeration);
			}
			else if (enumOrSelectNode.getType() == SELECT_TYPE)
			{
				// SELECT
				SelectType select = Extractor.extractSelectType(schema, subNode);
				underlyingType = new TypeRef(schema, select);
			}
			else
			{				
				throw new IllegalStateException();
			}
		}
		else if (subNode.getType() == AGGREGATION_TYPES)
		{
			AggregationType aggType = Extractor.extractAggregationType(schema, underlyingTypeNode);
			underlyingType = new TypeRef(schema, aggType);			
		}
		else if (subNode.getType() == SIMPLE_TYPES)
		{
			SimpleType simpleType = Extractor.extractSimpleType(schema, underlyingTypeNode);
			underlyingType = new TypeRef(schema, simpleType);			
		}
		else if (subNode.getType() == TYPE_REF)
		{
			String typeId = Extractor.lookupTypeRef(schema, underlyingTypeNode).getId();		// TODO: Type constraints? Like STRING(255)
			underlyingType = new TypeRef(schema, typeId);
		}
		else
		{			
			throw new IllegalStateException();
		}
		
		return new DeclaredType(schema, id, underlyingType);
	}
	
	public static EntityType extractEntityType(Schema schema, ASTNode node)
	{
		if (node.getType() != ENTITY_DECL)
			throw new IllegalArgumentException();
		
		ASTNode headNode = node.getChild(ENTITY_HEAD);
		ASTNode idNode = headNode.getChild(ENTITY_ID).getFirstChild();
		String id = idNode.getText();
		boolean isAbstract = false;
		TypeRef superTypeRef = null;
		
		ASTNode subsuper = headNode.getChild(SUBSUPER);
		if (subsuper != null)
		{
			ASTNode superTypeConstraint = subsuper.getChild(SUPERTYPE_CONSTRAINT);
			if (superTypeConstraint != null)
			{
				ASTNode abstractSuperTypeDeclaration = superTypeConstraint.getChild(ABSTRACT_SUPERTYPE_DECLARATION);
				if (abstractSuperTypeDeclaration != null)
				{
					isAbstract = true;
					// TODO: Branch not checked
				}
				/*else 
				{							
					ASTNode superTypeRule = superTypeConstraint.getChild(SUPERTYPE_RULE);
					if (superTypeRule != null)
					{
						ASTNode subtypeConstraint = superTypeRule.getChild(SUBTYPE_CONSTRAINT);
						List<ASTNode> shit = subtypeConstraint.getChildren();
					}
				}*/
			}
			
			ASTNode subTypeDecl = subsuper.getChild(SUBTYPE_DECLARATION);
			if (subTypeDecl != null)
			{
				for (ASTNode entityRef : subTypeDecl.getChildren())
				{
					superTypeRef = new TypeRef(schema, entityRef.getFirstChild().getText());
					break;
				}
			}
		}
		
		ASTNode body = headNode.getNextSibling();
		List<ExplicitAttribute> explicitAttributes = Extractor.extractExplicitAttributes(schema, body);
		List<InverseAttribute> inverseAttributes = Extractor.extractInverseAttributes(schema, body);
		
		return new EntityType(schema, id, superTypeRef, isAbstract, explicitAttributes, inverseAttributes);
	}
	
	public static TypeRef extractBaseType(Schema schema, ASTNode node)
	{
		TypeRef baseTypeRef;
		ASTNode baseTypeNode = node.getChild(BASE_TYPE);
		ASTNode concreteTypeNode = baseTypeNode.getChild(CONCRETE_TYPES);		
		if (concreteTypeNode != null)
		{
			Type aggType = extractAggregationType(schema, concreteTypeNode);
			if (aggType != null)
			{
				baseTypeRef = new TypeRef(schema, aggType);
			}
			else
			{
				Type simpleType = extractSimpleType(schema, concreteTypeNode);
				if (simpleType != null)
				{
					baseTypeRef = new TypeRef(schema, simpleType);
				}
				else
				{					
					baseTypeRef = lookupNamedTypeRef(schema, concreteTypeNode);					
				}
			}
		}
		else
		{
			// TODO: Generalized types
			throw new IllegalStateException("Aggregations of generalized types are not supported in this version");
		}
		
		return baseTypeRef;
	}
	
	public static EnumerationType extractEnumerationType(Schema schema, ASTNode node)
	{
		if (node.getType() != ENUMERATION_TYPE)
			throw new IllegalArgumentException();
		
		ASTNode enumItemsNode = node.getFirstChild();
		if (enumItemsNode.getType() != ENUMERATION_ITEMS)
			throw new IllegalArgumentException();

		List<String> ids = new ArrayList<String>();
		for (ASTNode enumIdNode : enumItemsNode.getChildren())
		{
			if (enumIdNode.getType() != ENUMERATION_ID)
				throw new IllegalArgumentException();
			
			ids.add(enumIdNode.getFirstChild().getText());
		}
		
		return new EnumerationType(schema, ids);
	}
	
	public static SelectType extractSelectType(Schema schema, ASTNode node)
	{
		ASTNode select = node.getChild(SELECT_TYPE);
		if (select == null)
			return null;
		
		ASTNode selectList = select.getFirstChild();		
		List<String> possibleTypes = new ArrayList<String>();
		
		for (ASTNode child : selectList.getChildren())
		{
			if (child.getType() != NAMED_TYPES)
				throw new IllegalArgumentException();
			
			ASTNode ref = child.getFirstChild();			
			if (ref.getType() != ENTITY_REF && ref.getType() != TYPE_REF)
				throw new IllegalArgumentException();
			
			ASTNode refType = ref.getFirstChild();
			possibleTypes.add(refType.getText());
		}
		
		return new SelectType(schema, possibleTypes);
	}
	
	public static AggregationType extractAggregationType(Schema schema, ASTNode node)
	{
		ASTNode aggregationTypesNode = node.getFirstChild();
		
		if (aggregationTypesNode.getType() != AGGREGATION_TYPES)
			return null;
				
		ASTNode subNode = aggregationTypesNode.getFirstChild();
		ASTNode boundSpec = subNode.getChild(BOUND_SPEC);
		if (boundSpec != null)
		{
			// TODO				
		}
		
		AggregationType type = null;
		TypeRef baseTypeRef = extractBaseType(schema, subNode);
		
		if (subNode.getType() == SET_TYPE)
		{
			// SET
			type = new SetType(schema, baseTypeRef);
		}
		else if (subNode.getType() == LIST_TYPE)
		{
			// LIST
			type = new ListType(schema, baseTypeRef);
		}
		else if (subNode.getType() == ARRAY_TYPE)
		{
			// ARRAY
			type = new ArrayType(schema, baseTypeRef);
		}
		else if (subNode.getType() == BAG_TYPE)
		{
			// BAG
			type = new BagType(schema, baseTypeRef);
		}
		
		return type;
	}
	
	public static SimpleType extractSimpleType(Schema schema, ASTNode node)
	{
		// SIMPLE TYPE
		ASTNode simpleTypeNode = node.getChild(SIMPLE_TYPES);
		if (simpleTypeNode == null)
			return null;
		
		ASTNode subNode = simpleTypeNode.getFirstChild();
		if (subNode.getType() == BINARY_TYPE)
		{
			// BINARY		
			return BinaryType.BINARY;
		}
		else if (subNode.getType() == BOOLEAN_TYPE)
		{
			// BOOLEAN
			return BooleanType.BOOLEAN;
		}
		else if (subNode.getType() == INTEGER_TYPE)
		{
			// INTEGER
			return IntegerType.INTEGER;
		}
		else if (subNode.getType() == LOGICAL_TYPE)
		{
			// LOGICAL
			return LogicalType.LOGICAL;
		}
		else if (subNode.getType() == NUMBER_TYPE)
		{
			// NUMBER
			return NumberType.REAL;
		}
		else if (subNode.getType() == REAL_TYPE)
		{
			// REAL
			return RealType.REAL;
		}
		else if (subNode.getType() == STRING_TYPE)
		{
			// STRING
			return expressomg.StringType.STRING;
		}
		else
		{
			return null;
		}
	}
	
	public static TypeRef lookupNamedTypeRef(Schema schema, ASTNode node)
	{
		ASTNode namedTypeNode = node.getChild(NAMED_TYPES);
		if (namedTypeNode != null)
		{
			TypeRef typeRef = lookupEntityRef(schema, namedTypeNode);
			if (typeRef != null)
				return typeRef;
			else
				return lookupTypeRef(schema, namedTypeNode);
		}
		return null;
	}
	
	public static TypeRef lookupEntityRef(Schema schema, ASTNode node)
	{
		ASTNode entityRefNode = node.getChild(ENTITY_REF);
		if (entityRefNode != null)
			return new TypeRef(schema, entityRefNode.getFirstChild().getText());
		return null;
	}
	
	public static TypeRef lookupTypeRef(Schema schema, ASTNode node)
	{
		ASTNode typeRefNode = node.getChild(TYPE_REF);
		if (typeRefNode != null)
			return new TypeRef(schema, typeRefNode.getFirstChild().getText());
		return null;
	}
	
	public static AttributeRef lookupAttributeRef(Schema schema, TypeRef entityRef, ASTNode node)
	{
		ASTNode attributeRefNode = node.getChild(ATTRIBUTE_REF);
		if (attributeRefNode != null)
			return new AttributeRef(schema, entityRef, attributeRefNode.getFirstChild().getText());
		return null;
	}
	
	public static List<ExplicitAttribute> extractExplicitAttributes(Schema schema, ASTNode node)
	{
		List<ExplicitAttribute> explicitAttributes = new ArrayList<ExplicitAttribute>();
		for (ASTNode explicitAttr : node.getChildren(EXPLICIT_ATTR))
		{
			ASTNode attrDecl = explicitAttr.getChild(ATTRIBUTE_DECL);
			String id = attrDecl.getChild(ATTRIBUTE_ID).getFirstChild().getText();
			boolean isOptional = explicitAttr.getChild(LITERAL_optional) != null;
			TypeRef baseTypeRef = extractBaseType(schema, explicitAttr);
			
			explicitAttributes.add(new ExplicitAttribute(schema, id, baseTypeRef, isOptional));
		}
		return explicitAttributes;		
	}
	
	public static List<InverseAttribute> extractInverseAttributes(Schema schema, ASTNode node)
	{
		ASTNode inverse = node.getChild(INVERSE_CLAUSE);
		List<InverseAttribute> inverseAttributes = new ArrayList<InverseAttribute>();
		if (inverse != null)
		{
			for (ASTNode inverseAttr : inverse.getChildren(INVERSE_ATTR))
			{
				ASTNode attrDecl = inverseAttr.getChild(ATTRIBUTE_DECL);
				String id = attrDecl.getChild(ATTRIBUTE_ID).getFirstChild().getText();
				
				TypeRef baseTypeRef = lookupEntityRef(schema, inverseAttr);
				AttributeRef directAttributeRef = lookupAttributeRef(schema, baseTypeRef, inverseAttr);
				
				ASTNode setNode = inverseAttr.getChild(LITERAL_set);
				ASTNode bagNode = inverseAttr.getChild(LITERAL_bag);
				TypeRef typeRef = null;
				if (setNode != null)
					typeRef = new TypeRef(schema, new SetType(schema, baseTypeRef));
				else if (bagNode != null)
					typeRef = new TypeRef(schema, new BagType(schema, baseTypeRef));
				else
					typeRef = baseTypeRef;
				// TODO: Bounds
				
				InverseAttribute invAttribute = new InverseAttribute(schema, id, typeRef, baseTypeRef, directAttributeRef);
				inverseAttributes.add(invAttribute);
			}
		}
		return inverseAttributes;		
	}
}
