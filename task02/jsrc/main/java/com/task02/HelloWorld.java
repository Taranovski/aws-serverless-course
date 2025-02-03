package com.task02;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.annotations.lambda.LambdaUrlConfig;
import com.syndicate.deployment.model.RetentionSetting;
import com.syndicate.deployment.model.lambda.url.AuthType;
import com.syndicate.deployment.model.lambda.url.InvokeMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@LambdaHandler(
        lambdaName = "hello_world",
        roleName = "hello_world-role",
        isPublishVersion = true,
        aliasName = "${lambdas_alias_name}",
        logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED
)
@LambdaUrlConfig(
        authType = AuthType.NONE,
        invokeMode = InvokeMode.BUFFERED
)
public class HelloWorld implements RequestHandler<APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse> {

    private static final int OK = 200;
    private static final int BAD_REQUEST = 400;
    private final Map<String, String> responseHeaders = Map.of("Content-Type", "application/json");

    @Override
    public APIGatewayV2HTTPResponse handleRequest(APIGatewayV2HTTPEvent requestEvent, Context context) {
        try {
            String method = getMethod(requestEvent);
            String path = getPath(requestEvent);

            ObjectMapper objectMapper = new ObjectMapper();

            if ("GET".equals(method) && "/hello".equals(path)) {
                Map<String, Object> resultMap = new HashMap<>();
                resultMap.put("statusCode", OK);
                resultMap.put("message", "Hello from Lambda");

                String body = objectMapper.writeValueAsString(resultMap);
                return new APIGatewayV2HTTPResponse(OK, responseHeaders, new HashMap<>(), new ArrayList<>(), body, false);
            } else {
                Map<String, Object> resultMap = new HashMap<>();
                resultMap.put("statusCode", BAD_REQUEST);
                resultMap.put("message", "Bad request syntax or unsupported method. Request path: " + path + ". HTTP method: " + method);

                String body = objectMapper.writeValueAsString(resultMap);
                return new APIGatewayV2HTTPResponse(BAD_REQUEST, responseHeaders, new HashMap<>(), new ArrayList<>(), body, false);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String getMethod(APIGatewayV2HTTPEvent requestEvent) {
        return requestEvent.getRequestContext().getHttp().getMethod();
    }

    private String getPath(APIGatewayV2HTTPEvent requestEvent) {
        return requestEvent.getRequestContext().getHttp().getPath();
    }

}
