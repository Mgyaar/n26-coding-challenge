package de.maxvollmer.n26_coding_challenge.time;

import javax.inject.Singleton;

@Singleton
public interface Time
{
	public long getCurrentTimestamp();
	public long getEarliestValidTransactionTimestamp();
	public boolean isValidTransactionTimestamp(final long timestamp);
	public boolean areSameLumpTimestamps(long timestamp1, long timestamp2);
}
