import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;

public class Main2 {

    public static void main2(String[] args)throws XmlRpcException, MalformedURLException {


        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");// dd/MM/yyyy
        SimpleDateFormat sdfDateHMS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// dd/MM/yyyy
        Date now = new Date();
        String fechaActual = sdfDate.format(now);
        String fechaActualFull = sdfDateHMS.format(now);

        final XmlRpcClient client = new XmlRpcClient();

        /*final XmlRpcClientConfigImpl start_config = new XmlRpcClientConfigImpl();
        start_config.setServerURL(new URL("http://localhost:8069"));
        final Map<String, String> info = (Map<String, String>)client.execute(
                start_config, "start", emptyList());*/

        final String url = "http://localhost:8069";
        final String db = "mega";
        final String username = "admin";
        final String password = "test";

        final XmlRpcClient models = new XmlRpcClient() {{
            setConfig(new XmlRpcClientConfigImpl() {{
                setServerURL(new URL(String.format("%s/xmlrpc/2/object", url)));
            }});
        }};

        final XmlRpcClientConfigImpl common_config = new XmlRpcClientConfigImpl();
        common_config.setServerURL(new URL(String.format("%s/xmlrpc/2/common", url)));
        client.execute(common_config, "version", emptyList());

        int uid = (int)client.execute(common_config, "authenticate", asList(db, username, password, emptyMap()));

        models.execute("execute_kw", asList(
                db, uid, password,
                "res.partner", "check_access_rights",
                asList("read"),
                new HashMap() {{ put("raise_exception", false); }}
        ));

        asList((Object[])models.execute("execute_kw", asList(
                db, uid, password,
                "res.partner", "search",
                asList(asList(
                        asList("is_company", "=", true)))
        )));

        Integer count_company = (Integer) models.execute("execute_kw", asList(
                db, uid, password,
                "res.partner", "search_count",
                asList(asList(
                        asList("is_company", "=", true)))
        ));

        final List ids = asList((Object[])models.execute(
                "execute_kw", asList(
                        db, uid, password,
                        "res.partner", "search",
                        asList(asList(
                                asList("is_company", "=", true))),

                        new HashMap() {{ put("limit", 1); }})));
        final Map record = (Map)((Object[])models.execute(
                "execute_kw", asList(
                        db, uid, password,
                        "res.partner", "read",
                        asList(ids)
                )
        ))[0];
        // count the number of fields fetched by default
        record.size();

        asList((Object[])models.execute("execute_kw", asList(
                db, uid, password,
                "res.partner", "read",
                asList(ids),
                new HashMap() {{
                    put("fields", asList("name", "country_id", "comment"));
                }}
        )));


        Map<String, Map<String, Object>> Partnerfields = (Map<String, Map<String, Object>>) models.execute("execute_kw", asList(
                db, uid, password,
                "res.partner", "fields_get",
                emptyList(),
                new HashMap() {{
                    put("attributes", asList("string", "help", "type"));
                }}
        ));


        asList((Object[])models.execute("execute_kw", asList(
                db, uid, password,
                "res.partner", "search_read",
                asList(asList(
                        asList("is_company", "=", true))),
                new HashMap() {{
                    put("fields", asList("name", "country_id", "comment"));
                    put("limit", 5);
                }}
        )));

        final Integer Partnerid = (Integer)models.execute("execute_kw", asList(
                db, uid, password,
                "res.partner", "create",
                asList(new HashMap() {{ put("name", "New Partner"); }})
        ));

        models.execute("execute_kw", asList(
                db, uid, password,
                "res.partner", "write",
                asList(
                        asList(Partnerid),
                        new HashMap() {{ put("name", "Newer Partner"); }}
                )
        ));

        // get record name after having changed it
        List<Object> Idpartnernuevo = asList((Object[]) models.execute("execute_kw", asList(
                db, uid, password,
                "res.partner", "name_get",
                asList(asList(Partnerid))
        )));

        models.execute("execute_kw", asList(
                db, uid, password,
                "res.partner", "unlink",
                asList(asList(Partnerid))));
// check if the deleted record is still in the database
        asList((Object[])models.execute("execute_kw", asList(
                db, uid, password,
                "res.partner", "search",
                asList(asList(asList("id", "=", Partnerid)))
        )));

        /*

        juego con el modelo de objetos

        models.execute(
                "execute_kw", asList(
                        db, uid, password,
                        "ir.model", "create",
                        asList(new HashMap<String, Object>() {{
                            put("name", "Custom Model");
                            put("model", "x_custom_model2");
                            put("state", "manual");
                        }})
                ));
        final Object fields = models.execute(
                "execute_kw", asList(
                        db, uid, password,
                        "x_custom_model2", "fields_get",
                        emptyList(),
                        new HashMap<String, Object> () {{
                            put("attributes", asList(
                                    "string",
                                    "help",
                                    "type"));
                        }}
                ));

        final Integer Inti = (Integer)models.execute(
                "execute_kw", asList(
                        db, uid, password,
                        "ir.model", "create",
                        asList(new HashMap<String, Object>() {{
                            put("name", "Custom Model");
                            put("model", "x_custom4");
                            put("state", "manual");
                        }})
                ));
        models.execute(
                "execute_kw", asList(
                        db, uid, password,
                        "ir.model.fields", "create",
                        asList(new HashMap<String, Object>() {{
                            put("model_id", Inti);
                            put("name", "x_name");
                            put("ttype", "char");
                            put("state", "manual");
                            put("required", true);
                        }})
                ));
        final Integer record_id = (Integer)models.execute(
                "execute_kw", asList(
                        db, uid, password,
                        "x_custom3", "create",
                        asList(new HashMap<String, Object>() {{
                            put("x_name", "test record");
                        }})
                ));

        client.execute(
                "execute_kw", asList(
                        db, uid, password,
                        "x_custom", "read",
                        asList(asList(record_id))
                ));*/



    }
}
