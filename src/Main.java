import com.odoojava.api.*;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;

public class Main {
    public static void main(String[] args) throws XmlRpcException, MalformedURLException {

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


        //TODO: obtener los clientes de ventas megago (los que creo el usuario odoo)
        List<Object> clientIds = asList((Object[]) models.execute("execute_kw", asList(
                db, uid, password,
                "res.partner", "search",
                asList(asList(
                        asList("origin_sync", "=", "megago"))),
                new HashMap() {{ put("limit", 100); }}
        )));

        System.out.println(clientIds);//terminate the current line


        //TODO: obtener los pedidos de ventas
        List<Object> SaleIds = asList((Object[]) models.execute("execute_kw", asList(
                db, uid, password,
                "sale.order", "search",
                asList(asList(
                        asList("warehouse_id", "=", 11))),
                new HashMap() {{ put("limit", 100); }}
        )));

        System.out.println(SaleIds);


        //TODO: obtener las lineas de los pedidos de ventas
        List<Object> saleLineIds = asList((Object[]) models.execute("execute_kw", asList(
                db, uid, password,
                "sale.order.line", "search",
                asList(asList(
                        asList("order_id", "in", SaleIds))),
                new HashMap() {{ put("limit", 100); }}
        )));

        System.out.println(saleLineIds);


        //TODO: obtener los productos de los pedidos de ventas
        List<Object> prodIds = asList((Object[]) models.execute("execute_kw", asList(
                db, uid, password,
                "sale.order.line", "read",
                asList(saleLineIds),
                new HashMap() {{
                    put("fields", asList("product_id"));
                }}
        )));

        System.out.println(prodIds);

        //TODO: actualizar campos en pedido de venta dado un id

        models.execute("execute_kw", asList(
                db, uid, password,
                "sale.order", "write",
                asList(
                        asList(SaleIds.get(0)),
                        new HashMap() {{
                            put("invoice_status_external", "FAILED");
                            put("invoice_external_id", "blah");
                            put("order_external_id", "otro blah");
                        }}
                )
        ));


        //TODO: actualizar informacion de cliente dado un id

        models.execute("execute_kw", asList(
                db, uid, password,
                "res.partner", "write",
                asList(
                        asList(32),
                        new HashMap() {{
                            put("vat", "66666666-6");
                            put("street", " ");
                            put("street2", " ");
                            put("country_id", 46);
                        }}
                )
        ));

        //TODO: crear clientes
        final Integer Partnerid = (Integer)models.execute("execute_kw", asList(
                db, uid, password,
                "res.partner", "create",
                asList(new HashMap() {{ put("name", "New Partner"); }})
        ));

        System.out.println(Partnerid);

        //TODO: crear pedidos de venta
        models.execute("execute_kw", asList(
                db, uid, password,
                "sale.order", "create",
                asList(
                        new HashMap() {{
                            put("name", "66666666-6");
                            put("partner_id", Partnerid);
                            put("order_line",
                                    asList(
                                        asList(0,0,new HashMap(){{put("product_id",22686);}})
                                    )
                            );
                        }}
                )
        ));

        //TODO: crear lineas de pedidos de ventas no es necesario





    }
}
