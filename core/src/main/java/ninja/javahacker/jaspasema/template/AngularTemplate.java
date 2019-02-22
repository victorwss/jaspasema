package ninja.javahacker.jaspasema.template;

import io.javalin.Handler;
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

    private static final String API_TEMPLATE = ""
            + "(function() {\n"
            + "\n"
            + "    'use strict';\n"
            + "    var targetUrl = \"#URL#\";\n"
            + "\n"
            + "    var jsonCall = function(type, method, urlContinuation, data, customHeaders, requestType, $http) {\n"
            + "        var headers = {};\n"
            + "        headers['ContentType'] = requestType;\n"
            + "        for (var ch in customHeaders) {\n"
            + "            headers[customHeaders[ch].name] = customHeaders[ch].value;\n"
            + "        }\n"
            + "        var config = {\n"
            + "            method: method,\n"
            + "            url: targetUrl + urlContinuation,\n"
            + "            data: data,\n"
            + "            headers: headers\n"
            + "        };\n"
            + "        return $http(config)\n"
            + "                .then(function(response) {\n"
            + "                    return Promise.resolve(response.data);\n"
            + "                }, function(response) {\n"
            + "                    return Promise.reject({ msg: \"API Error: \" + urlContinuation, status: response.status });\n"
            + "                });\n"
            + "    };\n"
            + "\n"
            + "#SERVICES#"
            + "})();";

    private static final String FACTORY_TEMPLATE = ""
            + "    angular\n"
            + "        .module('#MODULE#')\n"
            + "        .factory('#SERVICE_NAME#Service',#SERVICE_NAME#Service);\n"
            + "\n"
            + "    #SERVICE_NAME#Service.$inject = ['$http'];\n"
            + "\n"
            + "    function #SERVICE_NAME#Service($http){ \n"
            + "        var service = {\n"
            + "#METHODS_LIST#"
            + "        };\n"
            + "        return service;\n"
            + "\n"
            + "#IMPL_SERVICES#"
            + "    }\n"
            + "\n";

    private static final String METHOD_TEMPLATE = ""
            + "    function #METHOD#(#DEFINE_PARAMETERS#) {\n"
            + "        var data = {};\n"
            + "        var requestType = 'text/plain; charset=utf-8';\n"
            + "        var customHeaders = [];\n"
            + "        var targetUrl = \"#PATH#\";\n"
            + "#RECEIVE_PARAMETERS#"
            + "#PRE_SEND_INSTRUCTIONS#"
            + "        return jsonCall(\"#TYPE#\", \"#HTTP_METHOD#\", targetUrl, data, customHeaders, requestType, $http);\n"
            + "    }\n"
            + "\n";

    @NonNull
    private Supplier<String> url;

    @NonNull
    private Supplier<String> moduleName;

    @NonNull
    private Supplier<String> varName;

    @Override
    public Handler createStub(@NonNull ServiceConfigurer sc) {
        return ctx -> {
            ctx.contentType("text/javascript");
            createAngularStub(sc);
        };
    }

    private String createAngularStub(@NonNull ServiceConfigurer sc) {
        String api = sc.getServiceBuilders()
                .stream()
                .map(AngularTemplate::forService)
                .collect(Collectors.joining())
                .replace("#VAR_NAME#", varName.get())
                .replace("#MODULE#", moduleName.get())
                .replace("#URL#", url.get());

        return API_TEMPLATE.replace("#SERVICES#", api);
    }

    private static String forService(@NonNull ServiceBuilder sb) {
        String output = FACTORY_TEMPLATE.replace("#SERVICE_NAME#", sb.getServiceName());
        StringBuilder methodList = new StringBuilder(2048);
        String calls = sb.getMethods().stream().map(smb -> forMethod(methodList, smb)).collect(Collectors.joining());
        methodList.append('\n');
        return output.replace("#IMPL_SERVICES#", calls).replace("#METHODS_LIST#", methodList);
    }

    private static String forMethod(@NonNull StringBuilder methodList, @NonNull ServiceMethodBuilder<?> smb) {
        String methodName = smb.getCallName();
        if (methodList.length() != 0) methodList.append(",\n");
        methodList.append("     ").append(methodName).append(" : ").append(methodName);

        String output = METHOD_TEMPLATE
                //.replace("#SERVICE#", smb.getService().getServiceName())
                .replace("#METHOD#", smb.getCallName())
                .replace("#HTTP_METHOD#", smb.getHttpMethod())
                .replace("#TYPE#", smb.getReturnMapper().onReturn().getExpectedReturnType())
                .replace("#PATH#", smb.getPath());

        StringJoiner def = new StringJoiner(", ");
        StringJoiner rec = new StringJoiner("");
        StringJoiner extras = new StringJoiner("");
        smb.getParameterProcessors().forEach(pps -> {
            String pa = pps.getParameterAdded();
            if (!pa.isEmpty()) def.add(pa);
            String ia = pps.getInstructionAdded();
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
