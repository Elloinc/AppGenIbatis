package net.sourceforge.appgen;

import net.sourceforge.appgen.converter.CamelCaseConverterTest;
import net.sourceforge.appgen.converter.FirstCharacterLowerCaseConverterTest;
import net.sourceforge.appgen.converter.FirstCharacterUpperCaseConverterTest;
import net.sourceforge.appgen.model.FieldTest;
import net.sourceforge.appgen.model.PrimaryKeyFieldComparatorTest;
import net.sourceforge.appgen.util.ClassNameComparatorTest;
import net.sourceforge.appgen.util.ConventionUtilsTest;
import net.sourceforge.appgen.util.FileUtilsTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * @author Byeongkil Woo
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
	
	// converter package
	CamelCaseConverterTest.class,
	FirstCharacterLowerCaseConverterTest.class,
	FirstCharacterUpperCaseConverterTest.class,
	
	// model package
	FieldTest.class,
	PrimaryKeyFieldComparatorTest.class,
	
	// util package
	ClassNameComparatorTest.class,
	ConventionUtilsTest.class,
	FileUtilsTest.class
	
})
public class AllTests {
}
