package it.zuper.restservice.rest;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;

import io.vavr.control.Either;
import it.zuper.restservice.beans.PricedItem;
import it.zuper.restservice.manager.ItemManager;

@Path("item")
public class ItemEndpoint {

	@Inject
	public ItemManager manager;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Path("{id}")
	public Response list(@PathParam("id") String template) {
		return Response.ok(new Gson().toJson(manager.list(template))).build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Path("{id}/{name}")
	public Response get(@PathParam("id") String template, @PathParam("name") String name) {
		return Utils.handleEither(manager.get(template, name));
	}


	@PUT
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Path("{id}/{name}")
	public Response add(@PathParam("id") String template, @PathParam("name") String name, @QueryParam("price")Double price) {
		return manager.add(template, name, price)
				.map(item -> new Gson().toJson(item))
				.map(Response::ok)
				.getOrElse(Response.serverError().entity("Insert Failure"))
				.build();
	}

	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Path("{id}/{name}")
	public Response update(@PathParam("id") String template, @PathParam("name") String name, @QueryParam("price")Double price) {
		PricedItem item = manager.update(template, name, price);
		return Response.ok(item).build();
	}

	@DELETE
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Path("{id}/{name}")
	public Response delete(@PathParam("id") String template, @PathParam("name") String name) {
		manager.deleteItem(template, name);
		return Response.ok().build();
	}

}
