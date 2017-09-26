package de.maxvollmer.n26_coding_challenge.endpoints;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import de.maxvollmer.n26_coding_challenge.data.transactions.TransactionManager;
import de.maxvollmer.n26_coding_challenge.data.transactions.TransactionStatistics;

@Path("/statistics")
public class StatisticsEndpoint
{
	@Inject
	TransactionManager transactionManager;

	@GET
	@Produces({MediaType.APPLICATION_JSON})
	public TransactionStatistics getTransactionStatistics() {
		return transactionManager.getTransactionStatistics();
	}
}
