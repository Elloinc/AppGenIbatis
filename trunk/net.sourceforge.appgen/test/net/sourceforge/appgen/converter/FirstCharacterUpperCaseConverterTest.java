package net.sourceforge.appgen.converter;

import static org.junit.Assert.*;

import net.sourceforge.appgen.converter.FirstCharacterUpperCaseConverter;

import org.junit.Test;

/**
 * @author Byeongkil Woo
 */
public class FirstCharacterUpperCaseConverterTest {

	@Test
	public void testConvert() {
		FirstCharacterUpperCaseConverter converter = new FirstCharacterUpperCaseConverter();
		
		assertNull(converter.convert(null));
		assertEquals("", converter.convert(""));
		assertEquals("ABCDEF", converter.convert("ABCDEF"));
		assertEquals("Abcdef", converter.convert("abcdef"));
		assertEquals("Abc_def", converter.convert("abc_def"));
		assertEquals("_abc_def", converter.convert("_abc_def"));
	}

}
