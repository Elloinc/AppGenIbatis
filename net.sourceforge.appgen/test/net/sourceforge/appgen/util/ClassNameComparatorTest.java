package net.sourceforge.appgen.util;

import static org.junit.Assert.*;

import java.util.Comparator;

import net.sourceforge.appgen.util.ClassNameComparator;

import org.junit.Test;

/**
 * @author Byeongkil Woo
 */
public class ClassNameComparatorTest {

	@Test
	public void testCompare() {
		Comparator<String> comparator = new ClassNameComparator();
		
		String class0 = "net.sourceforge.appgen.Class0";
		String class1 = "net.sourceforge.appgen.Class1";
		String class2 = "net.sourceforge.appgen.Class1";
		String subpack0 = "net.sourceforge.appgen.subpackage.Class0";
		String subpack1 = "net.sourceforge.appgen.subpackage.Class1";
		String subpack2 = "net.sourceforge.appgen.subpackage.Class1";
		
		// same package
		assertTrue(comparator.compare(class0, class1) < 0);
		assertTrue(comparator.compare(class1, class0) > 0);
		assertTrue(comparator.compare(class1, class2) == 0);
		assertTrue(comparator.compare(class2, class1) == 0);
		assertTrue(comparator.compare(subpack0, subpack1) < 0);
		assertTrue(comparator.compare(subpack1, subpack0) > 0);
		
		// different package
		assertTrue(comparator.compare(class0, subpack0) < 0);
		assertTrue(comparator.compare(subpack0, class0) > 0);
		assertTrue(comparator.compare(class1, subpack1) < 0);
		assertTrue(comparator.compare(subpack1, class1) > 0);
		assertTrue(comparator.compare(class2, subpack2) < 0);
		assertTrue(comparator.compare(subpack2, class2) > 0);
	}

}
