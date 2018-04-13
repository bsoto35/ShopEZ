package edu.ycp.cs320.ShopEZ.junit.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import edu.ycp.cs320.sqldemo.lab02.controller.ShopezeController;
import edu.ycp.cs320.sqldemo.lab02.model.ShopezeModel;

public class ShopezeControllerTest {
	private ShopezeModel model;
	private ShopezeController controller;
	
	@Before
	public void setUp() {
		model = new ShopezeModel();
		controller = new ShopezeController();
		
		model.setMin(1);
		model.setMax(100);
		
		controller.setModel(model);
	}
	
	@Test
	public void testNumberIsGreater() {
		int currentGuess = model.getGuess();
		controller.setNumberIsGreaterThanGuess();
		assertTrue(model.getGuess() > currentGuess);
	}
	@Test
	public void testNumberIsLess() {
		int currentGuess = model.getGuess();
		controller.setNumberIsLessThanGuess();
		assertTrue(model.getGuess() < currentGuess);
	}
	@Test
	public void testNumberIsGuess() {
		int currentGuess = model.getGuess();
		controller.setNumberIsGreaterThanGuess();
		controller.setNumberIsLessThanGuess();
		controller.setNumberFound();
		assertEquals(model.getGuess(), currentGuess);
	}
}
