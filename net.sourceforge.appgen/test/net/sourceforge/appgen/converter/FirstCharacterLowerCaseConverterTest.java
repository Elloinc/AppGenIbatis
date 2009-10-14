package net.sourceforge.appgen.converter;

import static org.junit.Assert.*;

import net.sourceforge.appgen.converter.FirstCharacterLowerCaseConverter;

import org.junit.Test;

/**
 * @author Byeongkil Woo
 */
public class FirstCharacterLowerCaseConverterTest {

	@Test
	public void testConvert() {
		FirstCharacterLowerCaseConverter converter = new FirstCharacterLowerCaseConverter();
		
		assertNull(converter.convert(null));
		assertEquals("", converter.convert(""));
		assertEquals("aBCDEF", converter.convert("ABCDEF"));
		assertEquals("abcdef", converter.convert("abcdef"));
		assertEquals("abc_def", converter.convert("abc_def"));
		assertEquals("_abc_def", converter.convert("_abc_def"));
	}

}
