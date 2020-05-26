package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ApiModel("RestResponse")
@EqualsAndHashCode
@ToString
public class RestResponse<T> {
    private final RestResponse.ServiceResponse service;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final T data;

    public RestResponse(@JsonProperty("service") RestResponse.ServiceResponse service, @JsonProperty("data") T data) {
        this.service = service;
        this.data = data;
    }

    public static <T> RestResponse<T> ok(T data) {
        return new RestResponse(RestResponse.ServiceResponse.OK, data);
    }

    public static RestResponse<Void> ok() {
        return ok(null);
    }

    public static <T> RestResponse<T> fail(ErrorCode errorCode, String errorDescription, T data) {
        return fail(errorCode.numericCode(), errorCode.stringCode(), errorDescription, data);
    }

    public static <T> RestResponse<T> fail(long numericCode, String stringCode, String errorDescription, T data) {
        return new RestResponse(new RestResponse.ServiceResponse(numericCode, stringCode, errorDescription), data);
    }

    public static RestResponse<Void> fail(ErrorCode errorCode, String errorDescription) {
        return fail(errorCode, errorDescription, null);
    }

    public static RestResponse<Void> fail(long numericCode, String stringCode, String errorDescription) {
        return fail(numericCode, stringCode, errorDescription, null);
    }

    @ApiModelProperty(
            value = "ServiceResponse",
            dataType = "ServiceResponse"
    )
    public RestResponse.ServiceResponse getService() {
        return this.service;
    }

    @ApiModelProperty("Custom data that will be returned with RestResponse")
    public T getData() {
        return this.data;
    }

    @ApiModel(
            value = "ServiceResponse",
            description = "Wrapper for service response"
    )

    @EqualsAndHashCode
    @ToString
    public static class ServiceResponse {
        public static final RestResponse.ServiceResponse OK = new RestResponse.ServiceResponse(0L, (String)null, (String)null);
        private final long code;
        @JsonProperty("error_type")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private final String errorType;
        @JsonProperty("error_description")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private final String errorDescription;

        public ServiceResponse(@JsonProperty("code") long code, @JsonProperty("error_type") String errorType, @JsonProperty("error_description") String errorDescription) {
            this.code = code;
            this.errorType = errorType;
            this.errorDescription = errorDescription;
        }

        @ApiModelProperty(
                value = "Error code",
                dataType = "long",
                required = true
        )
        public long getCode() {
            return this.code;
        }

        @ApiModelProperty(
                value = "String representation of error type",
                dataType = "String"
        )
        public String getErrorType() {
            return this.errorType;
        }

        @ApiModelProperty(
                value = "Error description",
                dataType = "String"
        )
        public String getErrorDescription() {
            return this.errorDescription;
        }
    }
}