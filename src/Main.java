import com.odoojava.api.*;

public class Main {
    public static void main(String[] args) {
        Session OdooSession = new Session("localhost", 8069, "mega", "admin", "test");

        try {

            OdooSession.startSession();
            FilterCollection filters = new FilterCollection();

            // Ejemplo para traer productos
            ObjectAdapter productsAd = OdooSession.getObjectAdapter("product.product");

            filters.add("sale_ok","=",true);
            RowCollection products = productsAd.searchAndReadObject(filters, new String[]{"name","default_code","barcode"});
            for (Row row : products){
                System.out.println("Row ID: " + row.getID());
                System.out.println("Name:" + row.get("name"));
                System.out.println("default_code:" + row.get("default_code"));
                System.out.println("barcode:" + row.get("barcode"));
            }
            filters.clear();

            // Ejemplo para traer clientes
            ObjectAdapter partnerAd = OdooSession.getObjectAdapter("res.partner");

            filters.add("customer_rank",">",0);
            RowCollection partners = partnerAd.searchAndReadObject(filters, new String[]{"name","email"});
            for (Row row : partners){
                System.out.println("Row ID: " + row.getID());
                System.out.println("Name:" + row.get("name"));
                System.out.println("Email:" + row.get("email"));
            }
            filters.clear();

            //Ejemplo para traer todos los pedidos de venta

            ObjectAdapter SaleOrderAd = OdooSession.getObjectAdapter("sale.order");
            filters.add("name","=ilike","%MEGA GO%");
            Integer limit = 100;
            RowCollection saleorders = SaleOrderAd.searchAndReadObject(filters,
                    new String[]{"name","invoice_status","partner_id","amount_untaxed","amount_tax","amount_total"},
                    limit,limit,"");
            for (Row row : saleorders){
                System.out.println("Row ID: " + row.getID());
                System.out.println("Name:" + row.get("name"));
                System.out.println("Name:" + row.get("invoice_status"));
                System.out.println("partner_id:" + row.get("partner_id"));
                System.out.println("amount_untaxed:" + row.get("amount_untaxed"));
                System.out.println("amount_tax:" + row.get("amount_tax"));
                System.out.println("amount_total:" + row.get("amount_total"));
            }
            filters.clear();


            //System.out.println(partnerAd);
        } catch (Exception e) {
            System.out.println("Error while reading data from server:\n\n" + e.getMessage());
        }


    }
}