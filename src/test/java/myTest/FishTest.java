package myTest;

import Util.ImageUtil;
import fish.Fish;
import fish.ImagePool;
import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;


public class FishTest {
	@Test
	public void FishTest_1() {
		Fish afish = new Fish();
		afish.setDirection(1);
		ImagePool image = new ImagePool();
		int grade = afish.getGrade();
		switch (grade) {
			case 1:
				assertTrue(ImageUtil.imagesAreEqual(image.getImage(0), afish.getImage()));
				assertEquals(8, afish.getScore());
				assertEquals(220, afish.getWidth());
				assertEquals(120, afish.getHeight());
				break;
			case 2:
				assertTrue(ImageUtil.imagesAreEqual(image.getImage(2), afish.getImage()));
				assertEquals(4, afish.getScore());
				assertEquals(120, afish.getWidth());
				assertEquals(84, afish.getHeight());
				break;
			case 3:
				assertTrue(ImageUtil.imagesAreEqual(image.getImage(4), afish.getImage()));
				assertEquals(2, afish.getScore());
				assertEquals(90, afish.getWidth());
				assertEquals(50, afish.getHeight());
				break;
			case 4:
				assertTrue(ImageUtil.imagesAreEqual(image.getImage(6), afish.getImage()));
				assertEquals(1, afish.getScore());
				assertEquals(55, afish.getWidth());
				assertEquals(30, afish.getHeight());
				break;

		}
	}

	@Test
	public void FishTest_3() {
		for (int i = 0; i < 10; i++) {
			Fish afish = new Fish();
			if (afish.getDirection() == 0) {
				assertEquals(-100, afish.getX(), 1e-6);
			} else {
				assertEquals(2000, afish.getX(), 1e-6);
			}
		}

	}

	@Test
	public void moveTest1() {
		Fish afish = new Fish(500, 1, 9, 1);
		afish.setWidth(120);
		afish.setLeft(false);
		afish.setRight(true);
		afish.move();
		assertEquals(509, afish.getX(), 1e-6);
	}

	@Test
	public void moveTest2() {
		Fish afish = new Fish(2000, 1, 9, 1);
		afish.setWidth(120);
		afish.setLeft(true);
		afish.setRight(true);
		afish.move();
		assertEquals(1991, afish.getX(), 1e-6);
	}

	@Test
	public void moveTest3() {
		Fish afish = new Fish(2000, 1, 9, 1);
		afish.setWidth(120);
		afish.setLeft(false);
		afish.setRight(true);
		afish.move();
		assertEquals(2000, afish.getX(), 1e-6);
	}

	@Test
	public void moveTest4() {
		Fish afish = new Fish(500, 1, 7, 1);
		afish.setHeight(100);
		afish.setY(500);
		afish.setDown(true);
		afish.setUp(false);
		afish.setY_speed(7);
		afish.move();
		assertEquals(507, afish.getY(), 1e-6);
	}

	@Test
	public void moveTest5() {
		Fish afish = new Fish(500, 1, 7, 1);
		afish.setHeight(500);
		afish.setY(500);
		afish.setDown(true);
		afish.setUp(true);
		afish.setY_speed(7);
		afish.move();
		assertEquals(493, afish.getY(), 1e-6);
	}

	@Test
	public void moveTest6() {
		Fish afish = new Fish(500, 1, 7, 1);
		afish.setHeight(500);
		afish.setY(500);
		afish.setDown(true);
		afish.setUp(false);
		afish.setY_speed(7);
		afish.move();
		assertEquals(500, afish.getY(), 1e-6);
	}

	@Test
	public void moveTest7() {
		Fish afish = new Fish();
		afish.setDirection(0);
		afish.move();
		ImagePool image = new ImagePool();
		assertTrue(ImageUtil.imagesAreEqual(image.getImage(9), afish.getImage()));
	}

	@Test
	public void moveTest8() {
		Fish afish = new Fish();
		afish.setDirection(1);
		afish.move();
		ImagePool image = new ImagePool();
		assertTrue(ImageUtil.imagesAreEqual(image.getImage(8), afish.getImage()));
	}

	@Test
	public void rondowDirectionTest() {
		Fish afish = new Fish(500, 1, 7, 1);
		int randomDirection = afish.rondowDirection();
		assertThat(randomDirection, greaterThanOrEqualTo(0));
		assertThat(randomDirection, lessThan(2));
	}

	@Test
	public void rondowSpeedTest() {
		Fish afish = new Fish(500, 1, 7, 1);
		int randomSpeed = afish.rondowSpeed();
		assertThat(randomSpeed, greaterThanOrEqualTo(4));
		assertThat(randomSpeed, lessThan(9));
	}

	@Test
	public void rondowGradeTest() {
		Fish afish = new Fish(500, 1, 7, 1);
		int randomGrade = afish.rondowGrade();
		assertThat(randomGrade, greaterThanOrEqualTo(1));
		assertThat(randomGrade, lessThan(5));
	}

	@Test
	public void rondowYTest() {
		Fish afish = new Fish(500, 1, 7, 1);
		int randomY = afish.rondowY();
		assertThat(randomY, greaterThanOrEqualTo(0));
		assertThat(randomY, lessThan(800));
	}

}


