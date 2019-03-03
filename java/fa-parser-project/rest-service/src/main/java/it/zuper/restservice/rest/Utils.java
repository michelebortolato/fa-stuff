package it.zuper.restservice.rest;

import javax.ws.rs.core.Response;

import io.vavr.control.Either;

public class Utils {

	public static <T> Response handleEither(Either<String, T> either) {
		return either.map(Response::ok)
				.getOrElseGet(Response.serverError()::entity)
				.build();
	}
	
}
