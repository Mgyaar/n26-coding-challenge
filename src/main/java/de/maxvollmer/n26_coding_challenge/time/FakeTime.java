package de.maxvollmer.n26_coding_challenge.time;

import javax.inject.Singleton;

import de.maxvollmer.n26_coding_challenge.config.Config;

@Singleton
public class FakeTime extends RealTime
{
	private long timeOffset = 0;
	private long frozenTime = 0;
	private boolean isFrozenTime = false;

	@Override
	public long getCurrentTimestamp()
	{
		if (isFrozenTime) {
			return frozenTime + timeOffset;
		}
		else {
			return super.getCurrentTimestamp() + timeOffset;
		}
	}

	public long getRandomValidTimestamp() {
		return getEarliestValidTransactionTimestamp() + (long) (Math.random() * (Config.MILISECONDS_TO_KEEP_TRANSACTIONS - Config.MILISECONDS_TO_PACK_IN_ONE_LUMP));
	}

	public long getRandomInvalidTimestamp() {
		return getEarliestValidTransactionTimestamp() - 1 - (long) (Math.random() * 1000L * 60L * 60L * 24L * 30L);
	}

	public void jumpTwoMinutes() {
		timeOffset += 120000;
	}

	public void freezeTime() {
		frozenTime = super.getCurrentTimestamp();
		isFrozenTime = true;
	}

	public void unfreezeTime() {
		isFrozenTime = false;
	}
}
