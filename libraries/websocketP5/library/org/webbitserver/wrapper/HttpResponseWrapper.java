package org.webbitserver.wrapper;

import org.webbitserver.HttpResponse;

import java.net.HttpCookie;
import java.nio.charset.Charset;

public class HttpResponseWrapper implements HttpResponse {

    private HttpResponse response;

    public HttpResponseWrapper(HttpResponse response) {
        this.response = response;
    }

    public HttpResponse underlyingResponse() {
        return response;
    }

    public HttpResponseWrapper underlyingResponse(HttpResponse response) {
        this.response = response;
        return this;
    }

    public HttpResponse originalResponse() {
        if (response instanceof HttpResponseWrapper) {
            HttpResponseWrapper wrapper = (HttpResponseWrapper) response;
            return wrapper.originalResponse();
        } else {
            return response;
        }
    }

    @Override
    public HttpResponseWrapper charset(Charset charset) {
        response.charset(charset);
        return this;
    }

    @Override
    public Charset charset() {
        return response.charset();
    }

    @Override
    public HttpResponseWrapper status(int status) {
        response.status(status);
        return this;
    }

    @Override
    public int status() {
        return response.status();
    }

    @Override
    public HttpResponseWrapper header(String name, String value) {
        response.header(name, value);
        return this;
    }

    @Override
    public HttpResponseWrapper header(String name, long value) {
        response.header(name, value);
        return this;
    }

    @Override
    public HttpResponseWrapper cookie(HttpCookie httpCookie) {
        response.cookie(httpCookie);
        return this;
    }

    @Override
    public HttpResponseWrapper content(String content) {
        response.content(content);
        return this;
    }

    @Override
    public HttpResponseWrapper write(String content) {
        response.write(content);
        return this;
    }

    @Override
    public HttpResponseWrapper content(byte[] content) {
        response.content(content);
        return this;
    }

    @Override
    public HttpResponseWrapper error(Throwable error) {
        response.error(error);
        return this;
    }

    @Override
    public HttpResponseWrapper end() {
        response.end();
        return this;
    }
}
