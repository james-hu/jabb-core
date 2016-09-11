package net.sf.jabb.spring.rest;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.UnknownHttpStatusCodeException;

/**
 * Custom implementation of the {@link ResponseErrorHandler} interface.
 * The code was modified from {@link DefaultResponseErrorHandler}.
 *
 * <p>This error handler checks for the status code on the {@link ClientHttpResponse}: any
 * code with series {@link org.springframework.http.HttpStatus.Series#CLIENT_ERROR} or
 * {@link org.springframework.http.HttpStatus.Series#SERVER_ERROR} is considered to be an
 * error. This behavior can be changed by overriding the {@link #hasError(HttpStatus)}
 * method, {@link #handleClientError(HttpStatus, ClientHttpResponse)} method, 
 * and {@link #handleServerErro(HttpStatus, ClientHttpResponse)} method.
 *
 * @author Arjen Poutsma
 * @author Rossen Stoyanchev
 * @author James Hu (Zhengmao Hu)
 * @see RestTemplate#setErrorHandler
 */
public class CustomResponseErrorHandler implements ResponseErrorHandler {

	/**
	 * Delegates to {@link #hasError(HttpStatus)} with the response status code.
	 */
	@Override
	public boolean hasError(ClientHttpResponse response) throws IOException {
		return hasError(getHttpStatusCode(response));
	}

	protected HttpStatus getHttpStatusCode(ClientHttpResponse response) throws IOException {
		HttpStatus statusCode;
		try {
			statusCode = response.getStatusCode();
		}
		catch (IllegalArgumentException ex) {
			throw new UnknownHttpStatusCodeException(response.getRawStatusCode(),
					response.getStatusText(), response.getHeaders(), getResponseBody(response), getCharset(response));
		}
		return statusCode;
	}

	/**
	 * Template method called from {@link #hasError(ClientHttpResponse)}.
	 * <p>The default implementation checks if the given status code is
	 * {@link org.springframework.http.HttpStatus.Series#CLIENT_ERROR CLIENT_ERROR}
	 * or {@link org.springframework.http.HttpStatus.Series#SERVER_ERROR SERVER_ERROR}.
	 * Can be overridden in subclasses.
	 * @param statusCode the HTTP status code
	 * @return {@code true} if the response has an error; {@code false} otherwise
	 */
	protected boolean hasError(HttpStatus statusCode) {
		return (statusCode.series() == HttpStatus.Series.CLIENT_ERROR ||
				statusCode.series() == HttpStatus.Series.SERVER_ERROR);
	}

	/**
	 * This default implementation throws a {@link HttpClientErrorException} if the response status code
	 * is {@link org.springframework.http.HttpStatus.Series#CLIENT_ERROR}, a {@link HttpServerErrorException}
	 * if it is {@link org.springframework.http.HttpStatus.Series#SERVER_ERROR},
	 * and a {@link RestClientException} in other cases.
	 */
	@Override
	public void handleError(ClientHttpResponse response) throws IOException {
		HttpStatus statusCode = getHttpStatusCode(response);
		switch (statusCode.series()) {
			case CLIENT_ERROR:
				handleClientError(statusCode, response);
				break;
			case SERVER_ERROR:
				handleServerErro(statusCode, response);
				break;
			default:
				throw new RestClientException("Unknown status code [" + statusCode + "]");
		}
	}
	
	protected void handleClientError(HttpStatus statusCode, ClientHttpResponse response) throws IOException{
		throw new HttpClientErrorException(statusCode, response.getStatusText(),
				response.getHeaders(), getResponseBody(response), getCharset(response));
	}
	
	protected void handleServerErro(HttpStatus statusCode, ClientHttpResponse response) throws IOException{
			throw new HttpServerErrorException(statusCode, response.getStatusText(),
					response.getHeaders(), getResponseBody(response), getCharset(response));
	}

	protected byte[] getResponseBody(ClientHttpResponse response) {
		try {
			InputStream responseBody = response.getBody();
			if (responseBody != null) {
				return FileCopyUtils.copyToByteArray(responseBody);
			}
		}
		catch (IOException ex) {
			// ignore
		}
		return new byte[0];
	}

	protected Charset getCharset(ClientHttpResponse response) {
		HttpHeaders headers = response.getHeaders();
		MediaType contentType = headers.getContentType();
		return contentType != null ? contentType.getCharSet() : null;
	}

}
