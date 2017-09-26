package de.maxvollmer.n26_coding_challenge.data.transactions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import javax.inject.Inject;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import de.maxvollmer.n26_coding_challenge.data.transactions.exception.OutdatedTransactionException;
import de.maxvollmer.n26_coding_challenge.time.FakeTime;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:spring/junitApplicationContext.xml"})
@TestExecutionListeners(listeners=DependencyInjectionTestExecutionListener.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestTransactionManager
{
	@Inject
	TransactionManager transactionManager;

	@Inject
	FakeTime time;

	private static final int NUM_OF_TRANSACTIONS_FOR_CORRECT_STATISTICS_TEST = 10000;

	private static final double EPSILON = 0.001;

	@Test
	public void testTransactionManagerReturnsEmptyStatisticsWhenNothingWasPosted() {
		time.jumpTwoMinutes();
		final TransactionStatistics transactionStatistics = transactionManager.getTransactionStatistics();
		assertFalse(transactionStatistics.hasData());
	}

	@Test(expected = OutdatedTransactionException.class)
	public void testTransactionManagerThrowsOutdatedTransactionExceptionForInvalidTransaction() throws OutdatedTransactionException {
		transactionManager.addTransaction(new Transaction(Math.random(), time.getRandomInvalidTimestamp()));
	}

	@Test
	public void testTransactionManagerReturnsSameDataWhenOnlyOneTransactionWasPosted() throws OutdatedTransactionException {
		time.jumpTwoMinutes();
		final Transaction transaction = new Transaction(Math.random(), time.getCurrentTimestamp());
		transactionManager.addTransaction(transaction);
		final TransactionStatistics transactionStatistics = transactionManager.getTransactionStatistics();
		assertEquals(transactionStatistics.getSum(), transaction.getAmount(), 0);
		assertEquals(transactionStatistics.getAvg(), transaction.getAmount(), 0);
		assertEquals(transactionStatistics.getMin(), transaction.getAmount(), 0);
		assertEquals(transactionStatistics.getMax(), transaction.getAmount(), 0);
		assertEquals(transactionStatistics.getCount(), 1L);
	}

	@Test
	public void testTransactionManagerReturnsCorrectStatisticsForMultipleValidTransactions() throws OutdatedTransactionException {
		time.jumpTwoMinutes();
		time.freezeTime();

		double sum = 0;
		double max = Double.MIN_VALUE;
		double min = Double.MAX_VALUE;
		
		for (int i = 0; i < NUM_OF_TRANSACTIONS_FOR_CORRECT_STATISTICS_TEST; i++) {
			final double amount = Math.random() * 1000;
			sum += amount;
			max = Math.max(max, amount);
			min = Math.min(min, amount);
			transactionManager.addTransaction(new Transaction(amount, time.getRandomValidTimestamp()));
		}
		
		final double average = sum / NUM_OF_TRANSACTIONS_FOR_CORRECT_STATISTICS_TEST;
		
		final TransactionStatistics transactionStatistics = transactionManager.getTransactionStatistics();
		
		assertEquals(transactionStatistics.getSum(), sum, EPSILON);
		assertEquals(transactionStatistics.getAvg(), average, EPSILON);
		assertEquals(transactionStatistics.getMin(), min, EPSILON);
		assertEquals(transactionStatistics.getMax(), max, EPSILON);
		assertEquals(transactionStatistics.getCount(), NUM_OF_TRANSACTIONS_FOR_CORRECT_STATISTICS_TEST);
		
		time.unfreezeTime();
	}

	@Test
	public void testTransactionManagerReturnsCorrectStatisticsForMultipleTransactionsIncludingInvalidOnes() throws OutdatedTransactionException {
		time.freezeTime();
		time.jumpTwoMinutes();
		
		double sum = 0;
		double max = Double.MIN_VALUE;
		double min = Double.MAX_VALUE;
		long count = 0;
		
		for (int i = 0; i < NUM_OF_TRANSACTIONS_FOR_CORRECT_STATISTICS_TEST; i++) {
			final double amount = Math.random() * 1000;
			if (Math.random() < 0.5D) {
				try {
					transactionManager.addTransaction(new Transaction(amount, time.getRandomInvalidTimestamp()));
				}
				catch (final OutdatedTransactionException e) {/*expected*/}
			}
			else {
				count++;
				sum += amount;
				max = Math.max(max, amount);
				min = Math.min(min, amount);
				transactionManager.addTransaction(new Transaction(amount, time.getRandomValidTimestamp()));
			}
		}
		
		final double average = sum / count;
		
		final TransactionStatistics transactionStatistics = transactionManager.getTransactionStatistics();
		
		assertEquals(transactionStatistics.getSum(), sum, EPSILON);
		assertEquals(transactionStatistics.getAvg(), average, EPSILON);
		assertEquals(transactionStatistics.getMin(), min, EPSILON);
		assertEquals(transactionStatistics.getMax(), max, EPSILON);
		assertEquals(transactionStatistics.getCount(), count);
		
		time.unfreezeTime();
	}

	@Test
	public void testTransactionManagerReturnsEmptyStatisticsAfterTwoMinutesPassedAfterLastValidTransactionWasPosted() throws OutdatedTransactionException {
		time.freezeTime();
		time.jumpTwoMinutes();
		
		for (int i = 0; i < NUM_OF_TRANSACTIONS_FOR_CORRECT_STATISTICS_TEST; i++) {
			transactionManager.addTransaction(new Transaction(Math.random(), time.getRandomValidTimestamp()));
		}
		
		time.jumpTwoMinutes();
		
		final TransactionStatistics transactionStatistics = transactionManager.getTransactionStatistics();
		assertFalse(transactionStatistics.hasData());
		
		time.unfreezeTime();
	}
}
