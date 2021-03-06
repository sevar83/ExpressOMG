package net.sourceforge.osexpress.parser;

import java.io.*;
import antlr.TokenStreamHiddenTokenFilter;
import antlr.CommonAST;
import antlr.RecognitionException; 
import antlr.TokenStreamException;
import java.util.Vector;

import net.sourceforge.osexpress.parser.ExpressLexer;
import net.sourceforge.osexpress.parser.ExpressParser;

public class EasyParser 
{

 /* An easy way to parse Express by Stephane Lardet (stephane.lardet@nist.gov)
	*
	*   Development of this software was funded by the United States Government,
	*   and is not subject to copyright.
	*
	*   DISCLAIMER: Recipients of this software assume all responsibility
	*   associated with its operation, modification, maintenance, and subsequent
	*   re-distribution.
	*/
	
	private InputStream expin;
	
	public EasyParser(String filename) throws IOException 
	{
		File file;
		FileInputStream in = null;
		ByteArrayInputStream buff = null;
		byte[] fileContent;
		int length;
	
		file = new File(filename);
		length = (int)file.length();
		fileContent = new byte[length];
	
		in = new FileInputStream(file);
		in.read(fileContent);
		in.close();
		
		buff = new ByteArrayInputStream(fileContent);
		buff.mark(length);
		expin = buff;
	}
	
	public EasyParser(BufferedInputStream bis) throws IOException 
	{
		bis.mark(Integer.MAX_VALUE);
		expin = bis;
	}

  public EasyParser(Vector filenames) throws IOException 
  {
		/* put all files in a single buffer and parse them as a file with
		   multiple schemas */
		File[] fs;
		int[] flength;
		FileInputStream in = null;
		ByteArrayInputStream buff = null;
		byte[] filesContent;
		int wlength, tlength, length;
		int sz, i;
	
		wlength=0;
		sz = filenames.size();
		fs = new File[sz];
		flength = new int[sz];
		for (i = 0; i < sz; i++) 
		{
	    fs[i] = new File((String)filenames.elementAt(i));
	    flength[i] = (int)fs[i].length();
	    wlength += flength[i];
		}
		
		filesContent = new byte[wlength];
		tlength = 0;
		for (i = 0; i < sz; i++) 
		{
	    length = flength[i];
	    in = new FileInputStream(fs[i]);
	    in.read(filesContent, tlength, length);
	    in.close();
	    tlength += length;
		}
		
		buff = new ByteArrayInputStream(filesContent);
		buff.mark(wlength);
		expin = buff;
	}
	
	public CommonAST parse() throws IOException, RecognitionException, TokenStreamException 
	{
		ExpressLexer lexer;
		ExpressParser parser;
		TokenStreamHiddenTokenFilter filter;
		Scope rootScope;
	
		/* First pass initialization */
		lexer = new ExpressLexer(expin);
		parser = new ExpressParser(lexer);
		lexer.setParser(parser);
	
		/* First pass */
		parser.syntax();
	
		/* manage reference and use clauses */
		parser.processExternals();
		    
		rootScope = parser.rootScope;
	
		/* second pass initialization */
		expin.reset();
		lexer = new ExpressLexer(expin);
		lexer.setTokenObjectClass("antlr.CommonHiddenStreamToken");
		filter = new TokenStreamHiddenTokenFilter(lexer);
		filter.discard(ExpressParser.COMMENT);
		filter.discard(ExpressParser.LINECOMMENT);
		parser = new ExpressParser(filter);
		lexer.setParser(parser);
		parser.setRootScope(rootScope);
	
		/* second pass */
		parser.syntax();
		expin.close();
		
		/* AST returning */
		return (CommonAST) parser.getAST();
	}
}

