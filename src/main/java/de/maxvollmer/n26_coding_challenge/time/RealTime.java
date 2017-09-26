package de.maxvollmer.n26_coding_challenge.time;

import javax.inject.Singleton;

import de.maxvollmer.n26_coding_challenge.config.Config;

@Singleton
public class RealTime implements Time
{
	@Override
	public long getCurrentTimestamp()
	{
		return System.currentTimeMillis();
	}

	@Override
	public long getEarliestValidTransactionTimestamp() {
		return getCurrentTimestamp() - Config.MILISECONDS_TO_KEEP_TRANSACTIONS + Config.MILISECONDS_TO_PACK_IN_ONE_LUMP;
	}

	@Override
	public boolean isValidTransactionTimestamp(final long timestamp)
	{
		return timestamp >= getEarliestValidTransactionTimestamp();
	}

	@Override
	public boolean areSameLumpTimestamps(final long timestamp1, final long timestamp2) {
		final long lump1 = timestamp1 / Config.MILISECONDS_TO_PACK_IN_ONE_LUMP;
		final long lump2 = timestamp2 / Config.MILISECONDS_TO_PACK_IN_ONE_LUMP;
		return lump1 == lump2;
	}
}
