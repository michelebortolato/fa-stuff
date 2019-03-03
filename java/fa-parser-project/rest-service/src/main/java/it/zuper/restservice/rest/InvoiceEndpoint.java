package it.zuper.restservice.rest;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.vavr.control.Either;
import it.zuper.restservice.beans.ItemWithQuantity;
import it.zuper.restservice.manager.ItemManager;

@Path("invoice")
public class InvoiceEndpoint {

	@Inject
	public ItemManager manager;
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("{id}")
	public Response createInvoice(@PathParam("id")String template, String date, Integer number, List<ItemWithQuantity> items) {
	
		Either<String, String> invoice = manager.createInvoice(template, date, number, items);
	
		return Utils.handleEither(invoice);
	}
	
}
