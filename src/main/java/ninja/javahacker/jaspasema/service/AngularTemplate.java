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
public class AngularTemplate implements ApiTemplate {

    private static final String PREFIX = ""
            + "(function() {\n"
            + "  'use strict';\n\n";

    private static final String SUFFIX = ""
            + "    var jsonCall = function(type, method, urlContinuation, data, customHeaders, $http) {\n"
            + "        var headers = {};\n"
            + "        for (var ch in customHeaders) {\n"
            + "            headers[customHeaders[ch].name] = customHeaders[ch].value;\n"
            + "        }\n"
            + "        var config= {\n"
            + "            method: method,\n"
            + "            url: \"#URL#\" + urlContinuation,\n"
            + "            data: data,\n"
            + "            headers: headers\n"
            + "        };\n"
            + "        return $http(config)\n"
            + "                  .then(function(response) {\n"
            + "                      return Promise.resolve(response.data);\n"
            + "                  }, function(response) {\n"
            + "                      return Promise.reject({ msg: \"Erro api: \" + urlContinuation, status: response.status });\n"
            + "                  });\n"
            + "    };\n"
            + "\n"
            + "})();";

    private static final String FACTORY_TEMPLATE = ""
            + "  angular\n"
            + "    .module('#MODULE#')\n"
            + "    .factory('#SERVICE_NAME#Service',#SERVICE_NAME#Service);\n"
            + "\n"
            + "  #SERVICE_NAME#Service.$inject = ['$http'];\n"
            + "\n"
            + "  function #SERVICE_NAME#Service($http){ \n"
            + "    var service = {\n"
            + "#METHODS_LIST#"
            + "    };\n"
            + "    return service;\n"
            + "\n"
            + "#IMPL_SERVICES#"
            + "  }\n"
            + "\n";

    private static final String METHOD_TEMPLATE = ""
            + "    function #METHOD#(#DEFINE_PARAMETERS#) {\n"
            + "        var data = {};\n"
            + "        var customHeaders = [];\n"
            + "        var targetUrl = \"#PATH#\";\n"
            + "#RECEIVE_PARAMETERS#"
            + "        return jsonCall(\"#TYPE#\", \"#HTTP_METHOD#\", targetUrl, data, customHeaders, $http);\n"
            + "    }\n"
            + "\n";

    @NonNull
    private Supplier<String> url;

    @NonNull
    private Supplier<String> moduleName;

    @NonNull
    private Supplier<String> varName;

    private static String metodosList;

    @Override
    public Route createStub(@NonNull ServiceConfigurer sc) {
        return (rq, rp) -> {
            String v = varName.get();
            Supplier<String> pv = () -> v;
            rp.type("text/javascript");
            // String s = SUFFIX.replace("#VAR_NAME#", v).replace("#MODULE#", moduleName.get());
            // return new JQueryTemplate(url, pv).createJavascriptStub(sc) + s;
            return PREFIX + createAngularStub(sc) + SUFFIX.replace("#URL#", url.get());
        };
    }

    public String createAngularStub(@NonNull ServiceConfigurer sc) {
        String api = sc.getServiceBuilders()
                .stream()
                .map(AngularTemplate::forService)
                .collect(Collectors.joining());

        return api.replace("#VAR_NAME#", varName.get())
                  .replace("#MODULE#", moduleName.get());
    }

    private static String forService(@NonNull ServiceBuilder sb) {
        String output = FACTORY_TEMPLATE
                .replace("#SERVICE_NAME#", sb.getServiceName());
        metodosList = "";   // inicia a string com a lista de metodos
        String calls = sb.getMethods().stream().map(AngularTemplate::forMethod).collect(Collectors.joining());
        metodosList += "\n";    // completa a lista de metodos
        return output.replace("#IMPL_SERVICES#", calls)
                     .replace("#METHODS_LIST#", metodosList);
    }

    private static String forMethod(@NonNull ServiceMethodBuilder smb) {
        String metodoName = smb.getCallName();
        if (!metodosList.isEmpty()) metodosList += ",\n";
        metodosList += "     " + metodoName + " : " + metodoName;

        String output = METHOD_TEMPLATE
                //.replace("#SERVICE#", smb.getService().getServiceName())
                .replace("#METHOD#", smb.getCallName())
                .replace("#HTTP_METHOD#", smb.getHttpMethod())
                .replace("#TYPE#", smb.getReturnProcessor().getExpectedReturnType())
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
