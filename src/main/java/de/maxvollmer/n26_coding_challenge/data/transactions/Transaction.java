package de.maxvollmer.n26_coding_challenge.data.transactions;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Transaction
{
	private double amount;
	private long timestamp;

	public Transaction() {
		
	}

	public Transaction(final double amount, final long timestamp) {
		this.setAmount(amount);
		this.setTimestamp(timestamp);
	}

	public long getTimestamp()
	{
		return timestamp;
	}

	public void setTimestamp(final long timestamp)
	{
		this.timestamp = timestamp;
	}

	public double getAmount()
	{
		return amount;
	}

	public void setAmount(final double amount)
	{
		this.amount = amount;
	}
}
