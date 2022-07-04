package com.getindata.connectors.http.sink;

import com.getindata.connectors.http.SinkHttpClient;
import com.getindata.connectors.http.SinkHttpClientBuilder;
import org.apache.flink.connector.base.sink.AsyncSinkBaseBuilder;
import org.apache.flink.connector.base.sink.writer.ElementConverter;

import java.util.Optional;

/**
 * Builder to construct {@link HttpSink}.
 *
 * <p>The following example shows the minimum setup to create a {@link HttpSink} that writes String
 * values to an HTTP endpoint using POST method.
 *
 * <pre>{@code
 * HttpSink<String> httpSink =
 *     HttpSink.<String>builder()
 *             .setEndpointUrl("http://example.com/myendpoint")
 *             .setElementConverter(
 *                 (s, _context) -> new HttpSinkRequestEntry("POST", "text/plain", s.getBytes(StandardCharsets.UTF_8)))
 *             .build();
 * }</pre>
 *
 * <p>If the following parameters are not set in this builder, the following defaults will be used:
 * <ul>
 *   <li>{@code maxBatchSize} will be 500,</li>
 *   <li>{@code maxInFlightRequests} will be 50,</li>
 *   <li>{@code maxBufferedRequests} will be 10000,</li>
 *   <li>{@code maxBatchSizeInBytes} will be 5 MB i.e. {@code 5 * 1024 * 1024},</li>
 *   <li>{@code maxTimeInBufferMS} will be 5000ms,</li>
 *   <li>{@code maxRecordSizeInBytes} will be 1 MB i.e. {@code 1024 * 1024}.</li>
 * </ul>
 * {@code endpointUrl} and {@code elementConverter} must be set by the user.
 *
 * @param <InputT> type of the elements that should be sent through HTTP request.
 */
public class HttpSinkBuilder<InputT> extends
    AsyncSinkBaseBuilder<InputT, HttpSinkRequestEntry, HttpSinkBuilder<InputT>> {
  private static final int DEFAULT_MAX_BATCH_SIZE = 500;
  private static final int DEFAULT_MAX_IN_FLIGHT_REQUESTS = 50;
  private static final int DEFAULT_MAX_BUFFERED_REQUESTS = 10_000;
  private static final long DEFAULT_MAX_BATCH_SIZE_IN_B = 5 * 1024 * 1024;
  private static final long DEFAULT_MAX_TIME_IN_BUFFER_MS = 5000;
  private static final long DEFAULT_MAX_RECORD_SIZE_IN_B = 1024 * 1024;

  private String endpointUrl;
  private SinkHttpClientBuilder sinkHttpClientBuilder;
  private ElementConverter<InputT, HttpSinkRequestEntry> elementConverter;

  HttpSinkBuilder() {}

  /**
   * @param endpointUrl the URL of the endpoint
   * @return {@link HttpSinkBuilder} itself
   */
  public HttpSinkBuilder<InputT> setEndpointUrl(String endpointUrl) {
    this.endpointUrl = endpointUrl;
    return this;
  }

  /**
   * @param sinkHttpClientBuilder builder for an implementation of {@link SinkHttpClient} that will be used by {@link HttpSink}
   * @return {@link HttpSinkBuilder} itself
   */
  public HttpSinkBuilder<InputT> setSinkHttpClientBuilder(SinkHttpClientBuilder sinkHttpClientBuilder) {
    this.sinkHttpClientBuilder = sinkHttpClientBuilder;
    return this;
  }

  /**
   * @param elementConverter the {@link ElementConverter} to be used for the sink
   * @return {@link HttpSinkBuilder} itself
   */
  public HttpSinkBuilder<InputT> setElementConverter(ElementConverter<InputT, HttpSinkRequestEntry> elementConverter) {
    this.elementConverter = elementConverter;
    return this;
  }

  @Override
  public HttpSink<InputT> build() {
    return new HttpSink<>(
        elementConverter,
        Optional.ofNullable(getMaxBatchSize()).orElse(DEFAULT_MAX_BATCH_SIZE),
        Optional.ofNullable(getMaxInFlightRequests()).orElse(DEFAULT_MAX_IN_FLIGHT_REQUESTS),
        Optional.ofNullable(getMaxBufferedRequests()).orElse(DEFAULT_MAX_BUFFERED_REQUESTS),
        Optional.ofNullable(getMaxBatchSizeInBytes()).orElse(DEFAULT_MAX_BATCH_SIZE_IN_B),
        Optional.ofNullable(getMaxTimeInBufferMS()).orElse(DEFAULT_MAX_TIME_IN_BUFFER_MS),
        Optional.ofNullable(getMaxRecordSizeInBytes()).orElse(DEFAULT_MAX_RECORD_SIZE_IN_B),
        endpointUrl,
        sinkHttpClientBuilder
    );
  }
}