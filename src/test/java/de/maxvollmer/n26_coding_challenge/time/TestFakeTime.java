package de.maxvollmer.n26_coding_challenge.time;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
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
@ContextConfiguration(locations={"classpath:spring/junitApplicationContext.xml"})
@TestExecutionListeners(listeners=DependencyInjectionTestExecutionListener.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestFakeTime
{
	@Inject
	Time time;

	@Inject
	FakeTime fakeTime;

	@Test
	public void testTimeIsFakeTime() {
		assertTrue(time instanceof FakeTime);
	}

	@Test
	public void testTimeIsSingleton() {
		assertTrue(time == fakeTime);
		assertEquals(time.getCurrentTimestamp(), fakeTime.getCurrentTimestamp());
		fakeTime.jumpTwoMinutes();
		assertEquals(time.getCurrentTimestamp(), fakeTime.getCurrentTimestamp());
	}

	@Test
	public void testFakeTimeJumpByTwoMinutes() {
		fakeTime.freezeTime();
		final long timestampBeforeJump = fakeTime.getCurrentTimestamp();
		fakeTime.jumpTwoMinutes();
		final long timestampAfterJump = fakeTime.getCurrentTimestamp();
		assertEquals(timestampAfterJump, timestampBeforeJump+120000);
		fakeTime.unfreezeTime();
	}

	@Test
	public void testFakeTimeFreeze() throws InterruptedException {
		fakeTime.freezeTime();
		final long timestampDuringFreezeAndBeforeFirstSleep = fakeTime.getCurrentTimestamp();
		Thread.sleep(10);
		final long timestampDuringFreezeAndAfterFirstSleep = fakeTime.getCurrentTimestamp();
		fakeTime.unfreezeTime();
		Thread.sleep(10);
		final long timestampAfterFreezeAndAfterSecondSleep = fakeTime.getCurrentTimestamp();
		assertEquals(timestampDuringFreezeAndBeforeFirstSleep, timestampDuringFreezeAndAfterFirstSleep);
		assertNotEquals(timestampDuringFreezeAndBeforeFirstSleep, timestampAfterFreezeAndAfterSecondSleep);
	}

}
