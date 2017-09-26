package de.maxvollmer.n26_coding_challenge.endpoints;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.maxvollmer.n26_coding_challenge.data.transactions.Transaction;
import de.maxvollmer.n26_coding_challenge.data.transactions.TransactionManager;
import de.maxvollmer.n26_coding_challenge.data.transactions.exception.OutdatedTransactionException;

@Path("/transactions")
public class TransactionsEndPoint
{
	protected static final Response.Status RESPONSE_STATUS_SUCCESS = Response.Status.CREATED;
	protected static final Response.Status RESPONSE_STATUS_OUTDATED_TRANSACTION = Response.Status.NO_CONTENT;

	@Inject
	TransactionManager transactionManager;

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response postTransaction(final Transaction transaction) {
		try
		{
			transactionManager.addTransaction(transaction);
			return Response.status(RESPONSE_STATUS_SUCCESS).build();
		}
		catch (final OutdatedTransactionException e)
		{
			return Response.status(RESPONSE_STATUS_OUTDATED_TRANSACTION).build();
		}
	}
}
