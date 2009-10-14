package net.sourceforge.appgen.model;

import static org.junit.Assert.*;

import java.util.Comparator;

import net.sourceforge.appgen.model.Entity;
import net.sourceforge.appgen.model.Field;
import net.sourceforge.appgen.model.PrimaryKeyFieldComparator;

import org.junit.Test;

/**
 * @author Byeongkil Woo
 */
public class PrimaryKeyFieldComparatorTest {

	@Test
	public void testCompare() {
		Comparator<Field> comparator = new PrimaryKeyFieldComparator();
		
		Entity entity = new Entity();
		
		Field f0 = null;
		Field f1 = null;
		Field f2 = null;
		
		f0 = new Field(entity);
		f1 = f0;
		
		assertTrue(comparator.compare(f0, f1) == 0);
		
		f0 = new Field(entity);
		f1 = new Field(entity);
		f2 = new Field(entity);
		
		f0.setPkPosition(0);
		f1.setPkPosition(1);
		f2.setPkPosition(2);
		
		assertTrue(comparator.compare(f0, f1) < 0);
		assertTrue(comparator.compare(f1, f0) > 0);
		assertTrue(comparator.compare(f1, f2) < 0);
		assertTrue(comparator.compare(f2, f1) > 0);
		assertTrue(comparator.compare(f0, f2) < 0);
		assertTrue(comparator.compare(f2, f0) > 0);
	}

}
