package net.sourceforge.appgen.converter;

import static org.junit.Assert.*;

import net.sourceforge.appgen.converter.CamelCaseConverter;

import org.junit.Test;

/**
 * @author Byeongkil Woo
 */
public class CamelCaseConverterTest {

	@Test
	public void testConvert() {
		CamelCaseConverter converter = new CamelCaseConverter();
		
		assertNull(converter.convert(null));
		assertEquals("", converter.convert(""));
		assertEquals("abcdef", converter.convert("ABCDEF"));
		assertEquals("abcdef", converter.convert("abcdef"));
		assertEquals("abcDef", converter.convert("abc_def"));
		assertEquals("abcDef", converter.convert("ABC_DEF"));
		assertEquals("abcDef", converter.convert("abc__def"));
		assertEquals("abcDef", converter.convert("abc_def_"));
		assertEquals("abcDef", converter.convert("abc_def_"));
		assertEquals("AbcDef", converter.convert("_abc_def"));
		assertEquals("AbcDef", converter.convert("__abc_def"));
		assertEquals("", converter.convert("____"));
	}

}
