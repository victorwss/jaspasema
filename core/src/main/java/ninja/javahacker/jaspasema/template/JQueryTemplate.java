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
public class JQueryTemplate implements ApiTemplate {

    private static final String METHOD_TEMPLATE = ""
            + "    services.#SERVICE#.#METHOD# = function(#DEFINE_PARAMETERS#) {\n"
            + "        var __data = {};\n"
            + "        var __requestType = 'text/plain; charset=utf-8';\n"
            + "        var __customHeaders = [];\n"
            + "        var __targetUrl = \"#PATH#\";\n"
            + "#RECEIVE_PARAMETERS#"
            + "#PRE_SEND_INSTRUCTIONS#"
            + "        return jsonCall(\"#TYPE#\", \"#HTTP_METHOD#\", __targetUrl, __data, __customHeaders, __requestType);\n"
            + "    };\n"
            + "\n";

    private static final String SERVICE_TEMPLATE = ""
            + "    services.#SERVICE# = {};\n"
            + "\n"
            + "#METHODS#"
            + "    Object.freeze(services.#SERVICE#);\n"
            + "\n";

    private static final String API_TEMPLATE = ""
            + "var #VAR_NAME# = (function() {\n"
            + "\n"
            + "    'use strict';\n"
            + "\n"
            + "    var services = {};\n"
            + "    services.targetUrl = location.origin;\n"
            + "\n"
            + "    var jsonCall = function(type, method, urlContinuation, data, customHeaders, requestType) {\n"
            + "        return $.ajax({\n"
            + "            dataType: type,\n"
            + "            url: services.targetUrl + urlContinuation,\n"
            + "            crossDomain: true,\n"
            + "            data: data,\n"
            + "            method: method,\n"
            + "            contentType: requestType,\n"
            + "            xhrFields: {\n"
            + "                withCredentials: true\n"
            + "            },\n"
            + "            beforeSend: function(request) {\n"
            + "                for (var ch in customHeaders) {\n"
            + "                    request.setRequestHeader(customHeaders[ch].name, customHeaders[ch].value);\n"
            + "                }\n"
            + "            }\n"
            + "        });\n"
            + "    };\n"
            + "\n"
            + "#SERVICES#"
            + "    return Object.freeze(services);\n"
            + "})();";

    @NonNull
    private Supplier<String> url;

    @NonNull
    private Supplier<String> varName;

    @NonNull
    @Override
    public Handler createStub(@NonNull ServiceConfigurer sc) {
        return ctx -> {
            ctx.contentType("text/javascript");
            createJavascriptStub(sc);
        };
    }

    @NonNull
    private String createJavascriptStub(@NonNull ServiceConfigurer sc) {
        var output = API_TEMPLATE
                .replace("#VAR_NAME#", varName.get())
                .replace("#URL#", url.get());

        var api = sc.getServiceBuilders()
                .stream()
                .map(JQueryTemplate::forService)
                .collect(Collectors.joining());

        return output.replace("#SERVICES#", api);
    }

    @NonNull
    private static String forService(@NonNull ServiceBuilder sb) {
        var output = SERVICE_TEMPLATE.replace("#SERVICE#", sb.getServiceName());
        var calls = sb.getMethods().stream().map(JQueryTemplate::forMethod).collect(Collectors.joining());
        return output.replace("#METHODS#", calls);
    }

    @NonNull
    private static String forMethod(@NonNull ServiceMethodBuilder<?> smb) {
        var output = METHOD_TEMPLATE
                .replace("#SERVICE#", smb.getServiceName())
                .replace("#METHOD#", smb.getCallName())
                .replace("#HTTP_METHOD#", smb.getHttpMethod())
                .replace("#TYPE#", smb.getReturnMapper().onReturn().getExpectedReturnType())
                .replace("#PATH#", smb.getPath());

        var def = new StringJoiner(", ");
        var rec = new StringJoiner("");
        var extras = new StringJoiner("");
        smb.getParameterProcessors().forEach((pps) -> {
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
