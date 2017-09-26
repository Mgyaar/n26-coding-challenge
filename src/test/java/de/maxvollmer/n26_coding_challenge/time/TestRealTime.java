package de.maxvollmer.n26_coding_challenge.time;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.inject.Inject;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:spring/applicationContext.xml"})
@TestExecutionListeners(listeners=DependencyInjectionTestExecutionListener.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestRealTime
{
	@Inject
	Time time;

	@Inject
	RealTime realTime;

	@Test
	public void testTimeIsRealTime() {
		assertTrue(time instanceof RealTime);
	}

	@Test
	public void testTimeIsSingleton() {
		assertTrue(time == realTime);
		assertEquals(time.getCurrentTimestamp(), realTime.getCurrentTimestamp());
	}

	@Test
	public void testTimeValidatesCorrectTimestamp() {
		assertTrue(time.isValidTransactionTimestamp(time.getCurrentTimestamp()));
		assertTrue(time.isValidTransactionTimestamp(time.getEarliestValidTransactionTimestamp()));
	}

	@Test
	public void testTimeRejectsIncorrectTimestamp() {
		assertFalse(time.isValidTransactionTimestamp(time.getEarliestValidTransactionTimestamp() - 1));
	}

}
