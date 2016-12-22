package expressomg;

import java.io.BufferedInputStream;
import java.io.IOException;

import net.sourceforge.osexpress.parser.EasyParser;
import antlr.CommonAST;
import antlr.RecognitionException;
import antlr.TokenStreamException;

/** 
 * EXPRESS Object Model Generator
 * 
 * @author Svetlozar Kostadinov (sevarbg@gmail.com)
 * 
 */
public class ExpressOMG 
{
	private final BufferedInputStream inputStream;
	private EasyParser parser;
	private ExpressObjectModel objectModel;
	
	public ExpressOMG(BufferedInputStream inputStream)
	{
		this.inputStream = inputStream; 		  
	}
	
	public ExpressObjectModel generate()
	{
		try
		{
			parser = new EasyParser(inputStream);
			CommonAST rootAst = parser.parse();
			this.objectModel = new ExpressObjectModel(rootAst);
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}
		catch (RecognitionException ex)
		{
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
		catch (TokenStreamException ex)
		{
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}	 
		
		return this.objectModel;
	}
}
