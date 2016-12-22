# ExpressOMG (EXPRESS Object Model Generator)
Library for generating a complete Java class hierarchy from a schema written in the EXPRESS language (.exp file).

Based on ANTLR and http://osexpress.sourceforge.net/exparser.html

## Usage

1. Build it as a jar and add it as a dependency to your project
2. Generate the Object Model like this:

```
FileInputStream in = new FileInputStream(new File("schema.exp"));
ExpressObjectModel objectModel = new ExpressOMG(inputStream).generate();		
// Now the schema is fully parsed and objectModel is ready for interrogation. For example:
objectModel.getSchemas().get("schema1").getTypes().get("MyType1")...
```
