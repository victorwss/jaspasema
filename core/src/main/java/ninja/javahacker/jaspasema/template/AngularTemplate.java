package ninja.javahacker.jaspasema.template;

import io.javalin.http.Handler;
import java.util.StringJoiner;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.Value;
import ninja.javahacker.jaspasema.service.ServiceBuilder;
import ninja.javahacker.jaspasema.service.ServiceConfigurer;
import ninja.javahacker.jaspasema.service.ServiceMethodBuilder;

/**
 * @author Victor Williams Stafusa da Silva
 */
@Value
public class AngularTemplate implements ApiTemplate {

    private static final String API_TEMPLATE =
            """
            (function() {
                "use strict";
                const targetUrl = "#URL#";

                const jsonCall = function(type, method, urlContinuation, data, customHeaders, requestType, $http) {
                    const headers = {};
                    headers["ContentType"] = requestType;
                    for (const ch in customHeaders) {
                        headers[customHeaders[ch].name] = customHeaders[ch].value;
                    }
                    const config = {
                        method: method,
                        url: targetUrl + urlContinuation,
                        data: data,
                        headers: headers
                    };
                    return $http(config)
                            .then(function(response) {
                                return Promise.resolve(response.data);
                            }, function(response) {
                                return Promise.reject({ msg: "API Error: " + urlContinuation, status: response.status });
                            });
                };

                #SERVICES#
            })();
            """;

    private static final String FACTORY_TEMPLATE =
            """
            angular
                .module("#MODULE#")
                .factory("#SERVICE_NAME#Service", #SERVICE_NAME#Service);

            #SERVICE_NAME#Service.$inject = ['$http'];

            function #SERVICE_NAME#Service($http) {
                const service = {
                    #METHODS_LIST#
                };
                return service;
            }

            #IMPL_SERVICES#
            """;

    private static final String METHOD_TEMPLATE =
            """
            function #METHOD#(#DEFINE_PARAMETERS#) {
                const __data = {};
                const __requestType = 'text/plain; charset=utf-8';
                const __customHeaders = [];
                const __targetUrl = "#PATH#";
                #RECEIVE_PARAMETERS##PRE_SEND_INSTRUCTIONS#
                return jsonCall("#TYPE#", "#HTTP_METHOD#", __targetUrl, __data, __customHeaders, __requestType, $http);
            }
            """;

    @NonNull
    private Supplier<String> url;

    @NonNull
    private Supplier<String> moduleName;

    @NonNull
    private Supplier<String> varName;

    @NonNull
    @Override
    public Handler createStub(@NonNull ServiceConfigurer sc) {
        return ctx -> {
            ctx.contentType("text/javascript");
            createAngularStub(sc);
        };
    }

    @NonNull
    private String createAngularStub(@NonNull ServiceConfigurer sc) {
        var api = sc.getServiceBuilders()
                .stream()
                .map(AngularTemplate::forService)
                .collect(Collectors.joining())
                .replace("#VAR_NAME#", varName.get())
                .replace("#MODULE#", moduleName.get())
                .replace("#URL#", url.get());

        return API_TEMPLATE.replace("#SERVICES#", api);
    }

    @NonNull
    private static String forService(@NonNull ServiceBuilder sb) {
        var output = FACTORY_TEMPLATE.replace("#SERVICE_NAME#", sb.getServiceName());
        var methodList = new StringBuilder(2048);
        var calls = sb.getMethods().stream().map(smb -> forMethod(methodList, smb)).collect(Collectors.joining());
        methodList.append('\n');
        return output.replace("#IMPL_SERVICES#", calls).replace("#METHODS_LIST#", methodList);
    }

    @NonNull
    private static String forMethod(@NonNull StringBuilder methodList, @NonNull ServiceMethodBuilder<?> smb) {
        var methodName = smb.getCallName();
        if (methodList.length() != 0) methodList.append(",\n");
        methodList.append("     ").append(methodName).append(" : ").append(methodName);

        var output = METHOD_TEMPLATE
                //.replace("#SERVICE#", smb.getService().getServiceName())
                .replace("#METHOD#", smb.getCallName())
                .replace("#HTTP_METHOD#", smb.getHttpMethod())
                .replace("#TYPE#", smb.getReturnMapper().onReturn().getExpectedReturnType())
                .replace("#PATH#", smb.getPath());

        var def = new StringJoiner(", ");
        var rec = new StringJoiner("");
        var extras = new StringJoiner("");
        smb.getParameterProcessors().forEach(pps -> {
            var pa = pps.getParameterAdded();
            if (!pa.isEmpty()) def.add(pa);
            var ia = pps.getInstructionAdded();
            if (!ia.isEmpty()) rec.add("        " + ia + "\n");
            pps.getPreSendInstructionAdded()
                    .stream()
                    .map(x -> "        " + x + "\n")
                    .forEach(extras::add);
        });

        return output
                .replace("#DEFINE_PARAMETERS#", def.toString())
                .replace("#RECEIVE_PARAMETERS#", rec.toString())
                .replace("#PRE_SEND_INSTRUCTIONS#", extras.toString());
    }

}
