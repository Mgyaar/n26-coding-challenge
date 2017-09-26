package de.maxvollmer.n26_coding_challenge.data.transactions;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.maxvollmer.n26_coding_challenge.config.Config;
import de.maxvollmer.n26_coding_challenge.data.transactions.exception.OutdatedTransactionException;
import de.maxvollmer.n26_coding_challenge.time.Time;

@Singleton
public class TransactionManager
{
	private final Time time;

	private final TransactionStatistics[] transactionStatistics;
	private final Object[] locks;

	private final TransactionStatistics cachedTransactionStatistics;
	private final Object cachedTransactionStatisticsLock;

	@Inject
	public TransactionManager(final Time time) {
		this.time = time;
		transactionStatistics = new TransactionStatistics[Config.MILISECONDS_TO_KEEP_TRANSACTIONS / Config.MILISECONDS_TO_PACK_IN_ONE_LUMP];
		locks = new Object[transactionStatistics.length];
		cachedTransactionStatistics = new TransactionStatistics(time);
		cachedTransactionStatisticsLock = new Object();
		initializeLocks();
	}

	private void initializeLocks()
	{
		for (int i = 0; i < locks.length; i++) {
			locks[i] = new Object();
		}
	}

	public void addTransaction(final Transaction transaction) throws OutdatedTransactionException {
		assertValidTransaction(transaction);
		final int index = getTransactionStatisticsIndexForTimestamp(transaction.getTimestamp());
		synchronized (locks[index])
		{
			transactionStatistics[index] = new TransactionStatistics(time, transaction, transactionStatistics[index]);
		}
	}

	private int getTransactionStatisticsIndexForTimestamp(final long timestamp)
	{
		return (int) ((timestamp / Config.MILISECONDS_TO_PACK_IN_ONE_LUMP) % transactionStatistics.length);
	}

	private void assertValidTransaction(final Transaction transaction) throws OutdatedTransactionException
	{
		if (!time.isValidTransactionTimestamp(transaction.getTimestamp())) {
			throw new OutdatedTransactionException();
		}
	}

	public TransactionStatistics getTransactionStatistics() {
		final long timestamp = time.getCurrentTimestamp();
		if (cachedTransactionStatistics.getTimestamp() < timestamp) {
			synchronized (cachedTransactionStatisticsLock) {
				if (cachedTransactionStatistics.getTimestamp() < timestamp) {
					cachedTransactionStatistics.reset();
					for (int i = 0; i < transactionStatistics.length; i++) {
						cachedTransactionStatistics.mergeTransactionStatistics(transactionStatistics[i]);
					}
					cachedTransactionStatistics.setTimestamp(timestamp);
				}
			}
		}
		return cachedTransactionStatistics;
	}
}
