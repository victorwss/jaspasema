package ninja.javahacker.jaspasema.service;

import java.util.StringJoiner;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.Value;
import ninja.javahacker.jaspasema.processor.ParamProcessor;
import spark.Route;

/**
 * @author Victor Williams Stafusa da Silva
 */
@Value
public class JQueryTemplate implements ApiTemplate {

    private static final String METHOD_TEMPLATE = ""
            + "    services.#SERVICE#.#METHOD# = function(#DEFINE_PARAMETERS#) {\n"
            + "        var data = {};\n"
            + "        var customHeaders = [];\n"
            + "        var targetUrl = \"#PATH#\";\n"
            + "#RECEIVE_PARAMETERS#"
            + "        return jsonCall(\"#TYPE#\", \"#HTTP_METHOD#\", targetUrl, data, customHeaders);\n"
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
            + "    services.targetUrl = \"#URL#\";\n"
            + "\n"
            + "    var jsonCall = function(type, method, urlContinuation, data, customHeaders) {\n"
            + "        return $.ajax({\n"
            + "            dataType: type,\n"
            + "            url: services.targetUrl + urlContinuation,\n"
            + "            crossDomain: true,\n"
            + "            data: data,\n"
            + "            method: method,\n"
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

    @Override
    public Route createStub(@NonNull ServiceConfigurer sc) {
        return (rq, rp) -> {
            rp.type("text/javascript");
            return createJavascriptStub(sc);
        };
    }

    public String createJavascriptStub(@NonNull ServiceConfigurer sc) {
        String output = API_TEMPLATE
                .replace("#VAR_NAME#", varName.get())
                .replace("#URL#", url.get());

        String api = sc.getServiceBuilders()
                .stream()
                .map(JQueryTemplate::forService)
                .collect(Collectors.joining());

        return output.replace("#SERVICES#", api);
    }

    private static String forService(@NonNull ServiceBuilder sb) {
        String output = SERVICE_TEMPLATE.replace("#SERVICE#", sb.getServiceName());
        String calls = sb.getMethods().stream().map(JQueryTemplate::forMethod).collect(Collectors.joining());
        return output.replace("#METHODS#", calls);
    }

    private static String forMethod(@NonNull ServiceMethodBuilder<?> smb) {
        String output = METHOD_TEMPLATE
                .replace("#SERVICE#", smb.getServiceName())
                .replace("#METHOD#", smb.getCallName())
                .replace("#HTTP_METHOD#", smb.getHttpMethod())
                .replace("#TYPE#", smb.getReturnMapper().onReturn().getExpectedReturnType())
                .replace("#PATH#", smb.getPath());

        StringJoiner def = new StringJoiner(", ");
        StringJoiner rec = new StringJoiner("");
        for (ParamProcessor.Stub<?> pps : smb.getParameterProcessors()) {
            String pa = pps.getParameterAdded();
            if (!pa.isEmpty()) {
                def.add(pa);
            }
            String ia = pps.getInstructionAdded();
            if (!ia.isEmpty()) {
                rec.add("        " + ia + "\n");
            }
        }

        return output
                .replace("#DEFINE_PARAMETERS#", def.toString())
                .replace("#RECEIVE_PARAMETERS#", rec.toString());
    }
}
